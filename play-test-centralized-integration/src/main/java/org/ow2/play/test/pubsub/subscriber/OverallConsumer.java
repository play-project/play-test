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

/**
 * Mainly a receiever for events to stress-test many subscriptions. For M36
 * overall-scenario tests at month M36.
 * 
 * @author Roland Stühmer
 * 
 */
final class OverallConsumer implements INotificationConsumer {

	private final PubSubClientServer pubSubClientServer;
	private final Logger logger;
	private final AbstractReceiver rdfParser;
	public static QName topic = Stream.TaxiUCESRRecomDcep.getTopicQName(); // bogus topic because we will to manual subscriptions
	private final Stats stats = Stats.get();

	/**
	 * @param pubSubClientServer
	 */
	OverallConsumer(PubSubClientServer pubSubClientServer) {
		this.pubSubClientServer = pubSubClientServer;
		this.logger = LoggerFactory.getLogger(OverallConsumer.class);
		this.rdfParser = new AbstractReceiver() {
		};
	}

	@Override
	public void notify(Notify notify) throws WsnbException {
	    Document dom = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
	    
	    UcTelcoClic2Call event;
		try {
			event = rdfParser.getEvent(dom, UcTelcoClic2Call.class);
			logger.info("RECEIVER Entry " + event.getModel().getContextURI() + " " + Main.getMembers(event.getModel()));
		    stats.request();
			System.out.println("RECIEVER counted " + stats.nb + " events.");
		} catch (Exception e) {
			System.err.println("Received nonparsable event: " + e.getMessage());
		}
	}
}