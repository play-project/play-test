package org.ow2.play.test.pubsub.subscriber;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;
import static eu.play_project.play_commons.constants.Namespace.EVENTS;

import java.util.Calendar;
import java.util.Random;

import javax.xml.namespace.QName;

import org.event_processing.events.types.Event;
import org.event_processing.events.types.ProximityInfoEvent;
import org.event_processing.events.types.VesselEvent;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractSender;

public class T1P2S1Notifier extends AbstractSender implements Runnable {

	public static QName topic = Stream.TaxiUCCall.getTopicQName();
	private static long delay = 200;
	
	private static Random random = new Random();
	private Logger logger;
	
	public T1P2S1Notifier() {
		super(topic);
		this.logger = LoggerFactory.getLogger(T1P2S1Notifier.class);
	}


	@Override
	public void run() {
		int numberOfEvents = 100;
		int speedingEvents = 0;
		
		for (int i = 1; i <= numberOfEvents; i++) {
			Event vesselEvent, proximityEvent;

			if(i % 3 == 0 && speedingEvents < 30) {
				// high speed over 10 kts
				vesselEvent = getNewVesselEvent("1"+i, 12.5);
				// close proximity less than 5 mi
				proximityEvent = getNewProximityEvent("1"+i, 4.1);
				speedingEvents++;
				System.out.printf("NOTIFIER sending event %s SPEEDING\n", i);				
			}
			else {
				// speed less than 10 kts
				vesselEvent = getNewVesselEvent("1"+i, 9);
				// proximity more than 5 mi
				proximityEvent = getNewProximityEvent("1"+i, 7);
				System.out.printf("NOTIFIER sending event %s normal\n", i);				
			}
			logger.info("NOTIFIER Exit " + vesselEvent.getModel().getContextURI());
			this.notify(vesselEvent, Stream.VesselStream.getTopicQName());
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e1) {
            }
			logger.info("NOTIFIER Exit " + proximityEvent.getModel().getContextURI());
			this.notify(proximityEvent, Stream.ProximityInfoStream.getTopicQName());
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e1) {
            	
            }

		}
	}
	
	
	private static VesselEvent getNewVesselEvent(String mmsi, double speed) {
		String eventId = EventHelpers.createRandomEventId("testing");
		
		VesselEvent event = new VesselEvent(EventHelpers.createEmptyModel(eventId),
				eventId + EVENT_ID_SUFFIX, true);
		// Run some setters of the event
		event.setAisMMSI(mmsi);
		event.setAisCallSign("SY6188");
		event.setAisCourse(155.5);
		event.setAisName("AQUARIUS ALFA");
		event.setAisNavStatus("Under way using engine");
		event.setAisShipCargo("Undefined");
		event.setAisShipType("Sailing");
		event.setAisSpeed(speed);
		event.setAisTrueHeading(29.7);
		event.setAisWindDirection(356.0);
		event.setAisWindSpeed(15.0);
		// position is missing
		
		// Create a Calendar for the current date and time
		event.setEndTime(Calendar.getInstance());
		event.setStream(new URIImpl(Stream.VesselStream.getUri()));

		return event;
	}


	private static ProximityInfoEvent getNewProximityEvent(String mmsi, double distance) {
		String eventId = EventHelpers.createRandomEventId("testing");
		
		ProximityInfoEvent event = new ProximityInfoEvent(EventHelpers.createEmptyModel(eventId),
				eventId + EVENT_ID_SUFFIX, true);
		// Run some setters of the event
		event.setAisMMSI(mmsi);
		event.setAisDistance(distance);
		event.setAisNearbyCallSign("");
		event.setAisNearbyCourse(347.0);
		event.setAisNearbyMMSI("253504000");
		event.setAisNearbyName("BARONG C" );
		event.setAisNearbyShipCargo("Undefined");
		event.setAisNearbyShipType("Undefined");
		event.setAisNearbySpeed(9.0);
		event.setAisNearbyTrueHeading(51.0);
		// position is missing
		
		// Create a Calendar for the current date and time
		event.setEndTime(Calendar.getInstance());
		event.setStream(new URIImpl(Stream.ProximityInfoStream.getUri()));

		return event;
	}

}
