package org.ow2.play.test;

import java.util.Iterator;
import java.util.regex.Pattern;

import org.event_processing.events.types.TwitterEvent;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.openrdf.rdf2go.RepositoryModelSet;

import eu.play_project.play_commons.constants.Event;
import eu.play_project.play_commons.constants.Namespace;

public class TweetSimulator implements Iterator<Model> {

	private final RepositoryModelSet dataSet;
	private final ClosableIterator<QueryRow> eventIds;
	private final URI eventType;
	private URI currentGraphUri;

	public TweetSimulator(RepositoryModelSet dataSet, String eventType) {
		this.dataSet = dataSet;
		this.eventType = new URIImpl(Namespace.TYPES.getUri() + eventType);

		/*
		 * Count all event IDs in storage
		 */
		QueryResultTable count = dataSet.sparqlSelect(
				"prefix : <http://events.event-processing.org/types/> " +
				"SELECT (COUNT(?g) AS ?count) " +
				"WHERE { GRAPH ?g { " +
				"?s :endTime ?time " +
				"} " +
				"} ");
		
		System.out.format("For keyword '%s' there were %s recorded tweets.\n", eventType, count.iterator().next().getLiteralValue("count"));

		
		/*
		 * Get all event IDs from storage, sorted by time for later replay
		 */
		QueryResultTable result = dataSet.sparqlSelect(
				"prefix : <http://events.event-processing.org/types/> " +
				"SELECT ?g " +
				"WHERE { GRAPH ?g { " +
				"?s :endTime ?time " +
				"} " +
				"} ORDER BY ASC (?time) ");
		
		this.eventIds = result.iterator();

	}

	@Override
	public boolean hasNext() {
		return eventIds.hasNext();
	}

	@Override
	public Model next() {
		/*
		 * Get the complete event (identified by ID) from storage
		 */
		this.currentGraphUri = eventIds.next().getValue("g").asURI();
		Model model = dataSet.getModel(this.currentGraphUri);
		
		/*
		 * Replace the generic TwitterEvent type a specific one per company keyword
		 */
		URI eventSubject = new URIImpl(this.currentGraphUri.toString() + Event.EVENT_ID_SUFFIX);
		if (model.contains(eventSubject, RDF.type, TwitterEvent.RDFS_CLASS)) {
			model.removeStatements(eventSubject, RDF.type, Variable.ANY);
			model.addStatement(eventSubject, RDF.type, this.eventType);
		}
		
		/*
		 * Find some special characters in tweet messages
		 */
		URIImpl siocContent = new URIImpl(Namespace.SIOC.getUri() + "content");
		ClosableIterator<Statement> tweetMessages = model.findStatements(eventSubject, siocContent, Variable.ANY);
		while (tweetMessages.hasNext()) {
			Statement tweetMessage = tweetMessages.next();
			String oldMessage = tweetMessage.getObject().toString();
			System.out.println(oldMessage);

			// check on unicode level:
			if (Pattern.compile("\\p{C}").matcher(oldMessage).find()) {
				System.out.println("Found char 2[] in string " + oldMessage);
			}
			
			// check on ascii level:
			//oldMessage.getBytes(StandardCharsets.US_ASCII);

			
			//String newMessage =
			
			//model.removeStatements(tweetMessage); // TODO enable for permanent change on disk
			//model.addStatement(eventSubject, siocContent, newMessage);
		}
		
		return model;
	}

	@Override
	public void remove() {
		if (this.currentGraphUri == null) {
			throw new IllegalStateException("next() was not invoked at least once");
		}
		else {
			dataSet.removeModel(this.currentGraphUri);
		}
	}
}
