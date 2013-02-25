package org.ow2.play.test.pubsub.subscriber;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;
import static eu.play_project.play_commons.constants.Namespace.EVENTS;

import java.util.Calendar;
import java.util.Random;

import javax.xml.namespace.QName;

import org.event_processing.events.types.UcTelcoCall;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractSender;

public class T1P1S1Notifier extends AbstractSender implements Runnable {

	public static QName topic = Stream.TaxiUCCall.getTopicQName();
	private static long delay = 200;
	
	private static Random random = new Random();
	private PubSubClientServer pubSubClientServer;
	private Logger logger;
	
	public T1P1S1Notifier() {
		super(topic);
		this.logger = LoggerFactory.getLogger(T1P1S1Notifier.class);
	}


	@Override
	public void run() {
		
		for (int i = 0; i < 200; i++) {
			UcTelcoCall event = getNewCallEvent();
			System.out.printf("NOTIFIER sending event %s normal\n", i+1);				
			logger.info("NOTIFIER Exit " + event.getModel().getContextURI());
			this.notify(event);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e1) {
            	
            }

		}
	}
	
	
	private static UcTelcoCall getNewCallEvent() {
		String eventId = EventHelpers.createRandomEventId("testing");
		
		UcTelcoCall event = new UcTelcoCall(EventHelpers.createEmptyModel(eventId),
				eventId + EVENT_ID_SUFFIX, true);
		// Run some setters of the event
		event.setUcTelcoCalleePhoneNumber("49123456789");
		event.setUcTelcoCallerPhoneNumber(Long.toString(Math.abs(random.nextLong())));
		event.setUcTelcoDirection("incoming");
		// Create a Calendar for the current date and time
		event.setEndTime(Calendar.getInstance());
		event.setStream(new URIImpl(Stream.TaxiUCCall.getUri()));

		return event;
	}


}
