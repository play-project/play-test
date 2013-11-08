package org.ow2.play.test.pubsub.subscriber;

import javax.xml.namespace.QName;

import org.ontoware.rdf2go.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

import eu.play_project.dcep.distributedetalis.utils.EventCloudHelpers;
import eu.play_project.play_commons.constants.Namespace;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventTypeMetadata;
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
	public static QName[] topics = {
		Stream.ESRRecom.getTopicQName(),
		new QName(Namespace.STREAMS.getUri(), "OverallResults01", Namespace.STREAMS.getPrefix())
	};
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
	    
		try {
			Model rdf = rdfParser.parseRdf(dom);
			logger.info("RECEIVER Entry {} {}", rdf.getContextURI(), Main.getMembers(rdf));
			logger.info("TYPE '{}' TOPIC '{}'", EventTypeMetadata.getType(rdf), EventCloudHelpers.getCloudId(rdf));
		    stats.request();
			System.out.println("RECIEVER counted " + stats.nb + " events.");
		} catch (Exception e) {
			System.err.println("Received nonparsable event: " + e.getMessage());
		}
	}
}