package org.ow2.play.srbench;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.event_processing.events.types.Event;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.play_project.play_commons.constants.Namespace;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;

public class SrbenchSimulator {

private static String sparqlPrefixes = "prefix om-owl:  <http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#> " +
		"prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> " +
		"prefix sens-obs:  <http://knoesis.wright.edu/ssw/> " +
		"prefix owl-time:  <http://www.w3.org/2006/time#> " +
		"prefix owl:     <http://www.w3.org/2002/07/owl#> " +
		"prefix xsd:     <http://www.w3.org/2001/XMLSchema#> " +
		"prefix weather:  <http://knoesis.wright.edu/ssw/ont/weather.owl#> " +
		"prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";

	public static void main(String[] args) {
		Logger logger = Logger.getLogger(SrbenchSimulator.class);
		try {
			Model m = RDF2Go.getModelFactory().createModel().open();
			m.readFrom(SrbenchSimulator.class.getClassLoader().getResourceAsStream("KLCK_2009_9_20.n3"), Syntax.Turtle);
			
			String query = sparqlPrefixes + "" +
					"SELECT ?observation ?data ?instant ?timestamp " +
					"WHERE { " +
					"?data rdf:type om-owl:MeasureData . " +
					"?observation om-owl:result ?data . " +
					"?observation om-owl:samplingTime ?instant . " +
					"?instant owl-time:inXSDDateTime ?timestamp . " +
					"} " +
					"ORDER BY ?timestamp ";
			
			for (QueryRow result : m.sparqlSelect(query)) {
				System.out.format("obs: %s, data: %s time: %s\n", result.getValue("observation"), result.getValue("data"), result.getValue("timestamp"));

				String eventId = EventHelpers.createRandomEventId("srbech");
				Event event = new Event(EventHelpers.createEmptyModel(eventId), eventId + EVENT_ID_SUFFIX, true);

				// Get timestamp into the event payload:
				event.setEndTime(result.getValue("timestamp"));
				event.setStream(new URIImpl(Namespace.STREAMS.getUri() + "Srbench" + Stream.STREAM_ID_SUFFIX));

				// Link the event to the Observation
				event.getModel().addStatement(new URIImpl(eventId + EVENT_ID_SUFFIX), new URIImpl(Namespace.TYPES.getUri() + "observation"), result.getValue("observation"));
				
				// Get all statements of the om-owl:Observation into the event payload:
				ClosableIterator<Statement> observations = m.findStatements(result.getValue("observation").asResource(), Variable.ANY, Variable.ANY);
				event.getModel().addAll(observations);
				observations.close();
				
				// Get all statements of the om-owl:MeasureData into the event payload:
				ClosableIterator<Statement> measureData = m.findStatements(result.getValue("data").asResource(), Variable.ANY, Variable.ANY);
				event.getModel().addAll(measureData);
				measureData.close();

				// Get all statements of the owl-time:Instant into the event payload:
				ClosableIterator<Statement> instant = m.findStatements(result.getValue("instant").asResource(), Variable.ANY, Variable.ANY);
				event.getModel().addAll(instant);
				instant.close();

				event.getModel().dump();
			};
			
		} catch (ModelRuntimeException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

}
