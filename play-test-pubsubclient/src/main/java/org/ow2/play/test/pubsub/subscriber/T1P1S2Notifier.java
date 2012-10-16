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

public class T1P1S2Notifier extends AbstractSender implements Runnable {

	public static QName topic = Stream.TaxiUCCall.getTopicQName();
	private static long delay = 200;
	
	private static Random random = new Random();
	private PubSubClientServer pubSubClientServer;
	private Logger logger;
	
	public T1P1S2Notifier() {
		super(topic);
		this.logger = LoggerFactory.getLogger(T1P1S2Notifier.class);
	}


	@Override
	public void run() {
		int numberOfEvents = 200;
		int myMissedCalls = 0;
		
		for (int i = 0; i < numberOfEvents; i++) {
			String calleeNumber;
			
			if(i % 3 == 0 && myMissedCalls < 30) {
				calleeNumber = "491799041747";
				myMissedCalls++;
				System.out.printf("NOTIFIER sending event %s ROLAND: %s\n", i+1, calleeNumber);
			}
			else {
				calleeNumber = Long.toString(Math.abs(random.nextLong()));
				System.out.printf("NOTIFIER sending event %s normal: %s\n", i+1, calleeNumber);				
			}
			UcTelcoCall event = getNewCallEvent(calleeNumber);
			logger.info("NOTIFIER Exit " + event.getModel().getContextURI());
			this.notify(event);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e1) {
            }

		}
	}
	
	
	private static UcTelcoCall getNewCallEvent(String calleeNumber) {
		String eventId = EVENTS.getUri() + "testing" + Math.abs(random.nextLong());
		
		UcTelcoCall event = new UcTelcoCall(EventHelpers.createEmptyModel(eventId),
				eventId + EVENT_ID_SUFFIX, true);
		// Run some setters of the event
		event.setUcTelcoCalleePhoneNumber(calleeNumber);
		event.setUcTelcoCallerPhoneNumber(Long.toString(Math.abs(random.nextLong())));
		event.setUcTelcoDirection("incoming");
		// Create a Calendar for the current date and time
		event.setEndTime(Calendar.getInstance());
		event.setStream(new URIImpl(Stream.TaxiUCCall.getUri()));

		return event;
	}


}
