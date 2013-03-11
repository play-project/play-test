package org.ow2.play.test.pubsub.subscriber;

import javax.xml.namespace.QName;

import org.event_processing.events.types.UcTelcoClic2Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_eventadapter.AbstractReceiver;
import eu.play_project.play_eventadapter.NoRdfEventException;

final class T1P1S2Consumer implements INotificationConsumer {
	/**
	 * 
	 */
	private final PubSubClientServer pubSubClientServer;
	private Logger logger;
	private AbstractReceiver rdfParser;
	public static QName topic = Stream.TaxiUCClic2Call.getTopicQName();
	private Stats stats = Stats.get();

	/**
	 * @param pubSubClientServer
	 */
	T1P1S2Consumer(PubSubClientServer pubSubClientServer) {
		this.pubSubClientServer = pubSubClientServer;
		this.logger = LoggerFactory.getLogger(T1P1S2Consumer.class);
		this.rdfParser = new AbstractReceiver() {
		};
	}

	public void notify(Notify notify) throws WsnbException {
	    Document dom = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
	    
	    UcTelcoClic2Call event;
		try {
			event = rdfParser.getEvent(dom, UcTelcoClic2Call.class);
			logger.info("RECEIVER Entry " + event.getModel().getContextURI() + " " + Main.getMembers(event.getModel()));
		    stats.request();
			System.out.println("RECIEVER counted " + stats.nb + " events.");
		} catch (NoRdfEventException e) {
			System.err.println("Received nonparsable event: " + e.getMessage());
		}
	}
}