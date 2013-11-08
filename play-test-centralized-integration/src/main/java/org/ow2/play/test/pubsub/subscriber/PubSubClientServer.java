/**
 * 
 */
package org.ow2.play.test.pubsub.subscriber;

import java.io.PrintStream;

import javax.xml.namespace.QName;

import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.notification.client.http.simple.HTTPProducerClient;
import org.petalslink.dsb.notification.client.http.simple.HTTPSubscriptionManagerClient;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.service.NotificationConsumerService;
import org.petalslink.dsb.soap.CXFExposer;

import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;

/**
 * @author chamerling
 * 
 */
public class PubSubClientServer {

    private String subscriptionID;

    private final QName[] topics;

    private final String producer;

    private final String me;

    Service server = null;

    PrintStream out;

	private CXFExposer exposer;

    public PubSubClientServer(PrintStream out, String producer, String me, QName... topics) {
        super();
        this.out = out;
        this.topics = topics;
        this.producer = producer;
        this.me = me;
    }

    
    public void start() {
        start(new T1P1S1Consumer(this));
    }
    
    public void start(INotificationConsumer consumer) {

        out.println("Creating service which will receive notification messages from the producer...");

        QName interfaceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationConsumer");
        QName serviceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationConsumerService");
        QName endpointName = new QName("http://docs.oasis-open.org/wsn/bw-2",
                "NotificationConsumerPort");
        // expose the service


        NotificationConsumerService service = new NotificationConsumerService(interfaceName,
                serviceName, endpointName, "NotificationConsumerService.wsdl", me, consumer);

        this.exposer = new CXFExposer();
        try {
            server = exposer.expose(service);
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.start();
        out.println("Created!");

        out.println("Subscribe to notification on topics " + topics);
        HTTPProducerClient pc = new HTTPProducerClient(producer);
        try {
        	for (QName topic : topics) {
	            subscriptionID = pc.subscribe(topic, me);
	            out.println("Subscribed, ID is " + subscriptionID + " for topic " + topic);
	        }
        } catch (NotificationException e1) {
            e1.printStackTrace();
        }

        Stats.get().startTime = System.currentTimeMillis();
    }
    
    public void simulate(Runnable notifier) {
    	notifier.run();
    	
    }

    public void stop() {

        // stop the local server
        out.println("Stopping the local server...");
        if (server != null) {
            server.stop();
        }
        out.println("Stopped!");

        if (subscriptionID != null) {
            // try to unsubscribe
            out.println("Unsubscribe...");
            HTTPSubscriptionManagerClient client = new HTTPSubscriptionManagerClient(producer);
            try {
                client.unsubscribe(subscriptionID);
            } catch (NotificationException e) {
                System.err.println(e.getMessage());
            }
            out.println("Done!");
        }
    }
    


}
