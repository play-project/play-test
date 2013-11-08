package eu.play_project.dcep.distributedetalis.utils;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;

import org.event_processing.events.types.Event;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventTypeMetadata;

/**
 * 
 * @deprecated This class is just a copy of the same class in package
 *             {@code play-dcep-distributedetalis}. Please prefer that class.
 * @author Roland Stühmer
 * 
 */
@Deprecated
public class EventCloudHelpers {

	/**
	 * Print the member event IDs (if present in the complex event) as
	 * a space-separated string. This method will be replaced when the
	 * :members feature becomes a first-class feature of DCEP.
	 * 
	 * @deprecated This will be removed when :members feature becomes a built-in
	 * feature of DCEP.
	 */
	@Deprecated
	public static String getMembers(Model event) {
		String members = "";
		for (Statement statement : event) {
			if (statement.getPredicate().toString().equals(Event.MEMBERS.toString())) {
	    		String member = statement.getObject().toString();
	    		int endIndex = member.lastIndexOf(EVENT_ID_SUFFIX);
	    		if (endIndex > 0 ) {
	    			member = member.substring(0, endIndex);
	    		}
	    		members += member + " ";
			}
		}
		return members;
	}

	/**
	 * Returns the RDF event type as URI string. This method tries to find {@code rdf:type}
	 * statements with the proper event ID as subject. If this fails it falls back to arbitrary
	 * {@code rdf:type} statements in the event and finally defaults to the basic event type of
	 * {@linkplain Event.RDFS_CLASS}.
	 * 
	 * @see EventTypeMetadata#getType(Model)
	 */
	public static String getEventType(Model event) {
		Node primaryType = null;
		Node secondaryType = null;
		
		if (event.getContextURI() != null) {
			Node eventId = new URIImpl(event.getContextURI() + EVENT_ID_SUFFIX);
			for (Statement quad : event) {
				if (quad.getPredicate().equals(RDF.type)) {
					secondaryType = quad.getObject();
					if (quad.getSubject().equals(eventId)) {
						primaryType = quad.getObject();
						break;
					}
				}
			}
		}
		
		if (primaryType != null) {
			return primaryType.toString();
		}
		else if (secondaryType != null) {
			return secondaryType.toString();
		}
		else {
			return Event.RDFS_CLASS.toString();
		}
	}
	
	/**
	 * Returns the event cloud ID which is contained for a given event.
	 */
	public static String getCloudId(Model event) {
		Node primaryType = null;
		Node secondaryType = null;

		String streamId = "";
		String cloudId = "";

		if (event.getContextURI() != null) {
			Node eventId = new URIImpl(event.getContextURI() + EVENT_ID_SUFFIX);
			for (Statement quad : event) {
				if (quad.getPredicate().toString().equals(Event.STREAM.toString())) {
					secondaryType = quad.getObject();
					if (quad.getSubject().equals(eventId)) {
						primaryType = quad.getObject();
						break;
					}
				}
			}
		}

		if (primaryType != null) {
			streamId = primaryType.toString();
			cloudId = streamId.substring(0,
					streamId.lastIndexOf(Stream.STREAM_ID_SUFFIX));
			return cloudId;
		}
		else if (secondaryType != null) {
			streamId = secondaryType.toString();
			cloudId = streamId.substring(0,
					streamId.lastIndexOf(Stream.STREAM_ID_SUFFIX));
			return cloudId;
		}
		else {
			return cloudId;
		}
	}
}
