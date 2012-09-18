/**
 * 
 */
package org.ow2.play.test.pubsub.subscriber;

import java.io.PrintWriter;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.notification.client.http.simple.HTTPProducerClient;
import org.petalslink.dsb.notification.client.http.simple.HTTPSubscriptionManagerClient;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.service.NotificationConsumerService;
import org.petalslink.dsb.soap.CXFExposer;
import org.petalslink.dsb.soap.api.Exposer;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class PubSubClientServer {

    private String subscriptionID;

    private QName topic;

    private String producer;

    private String me;

    Service server = null;

    PrintWriter writer;

    public PubSubClientServer(PrintWriter writer, QName topic, String producer, String me) {
        super();
        this.writer = writer;
        this.topic = topic;
        this.producer = producer;
        this.me = me;
    }

    public void start() {

        writer.println("Creating service which will receive notification messages from the producer...");

        QName interfaceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationConsumer");
        QName serviceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationConsumerService");
        QName endpointName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationConsumerPort");
        // expose the service
        INotificationConsumer consumer = new INotificationConsumer() {
            public void notify(Notify notify) throws WsnbException {
                Stats.get().request(notify);
                Document dom = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
                writer.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                writer.println("Got a notification #" + Stats.get().nb);
                try {
                    writer.println(XMLHelper.createStringFromDOMDocument(dom));
                } catch (TransformerException e) {
                }
                writer.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                writer.flush();
            }
        };

        NotificationConsumerService service = new NotificationConsumerService(interfaceName,
                serviceName, endpointName, "NotificationConsumerService.wsdl", me, consumer);

        Exposer exposer = new CXFExposer();
        try {
            server = exposer.expose(service);
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.start();
        writer.println("Created!");

        writer.println("Subscribe to notification on topic " + topic);
        HTTPProducerClient pc = new HTTPProducerClient(producer);
        try {
            subscriptionID = pc.subscribe(topic, me);
            writer.println("Subscribed, ID is " + subscriptionID);
        } catch (NotificationException e1) {
            e1.printStackTrace();
        }

        Stats.get().startTime = System.currentTimeMillis();
    }

    public void stop() {

        // stop the local server
        writer.println("Stopping the local server...");
        if (server != null) {
            server.stop();
        }

        writer.println("Stopped!");

        if (subscriptionID != null) {
            // try to unsubscribe
            writer.println("Unsubscribe...");
            HTTPSubscriptionManagerClient client = new HTTPSubscriptionManagerClient(producer);
            try {
                client.unsubscribe(subscriptionID);
            } catch (NotificationException e) {
                System.err.println(e.getMessage());
            }
            writer.println("Done!");
        }
    }

}
