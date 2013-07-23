package org.ow2.play.srbench;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

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
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.play_project.play_commons.constants.Namespace;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;

/**
 * An <i>iterable</i> simulator returning RDF models with simulted events in ascending
 * temporal order.
 * 
 * @author Roland Stühmer
 */
public class SrbenchSimulator implements Iterable<Model> {

	private static String sparqlPrefixes = "prefix om-owl:  <http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#> " +
			"prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> " +
			"prefix sens-obs:  <http://knoesis.wright.edu/ssw/> " +
			"prefix owl-time:  <http://www.w3.org/2006/time#> " +
			"prefix owl:     <http://www.w3.org/2002/07/owl#> " +
			"prefix xsd:     <http://www.w3.org/2001/XMLSchema#> " +
			"prefix weather:  <http://knoesis.wright.edu/ssw/ont/weather.owl#> " +
			"prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";

	private final String query;
	private final Model m;
	
	/**
	 * Return a simulator.
	 */
	public SrbenchSimulator() throws SrbenchSimulatorException {
		this("KLCK_2009_9_20.n3");
	}
	
	/**
	 * Return a simulator reading data from the specified file (using RDF
	 * {@linkplain Syntax.Turtle}).
	 */
	public SrbenchSimulator(String turtleFileName) throws SrbenchSimulatorException {

		try{
			m = RDF2Go.getModelFactory().createModel().open();
			m.readFrom(SrbenchSimulator.class.getClassLoader().getResourceAsStream(turtleFileName), Syntax.Turtle);
			
			this.query = sparqlPrefixes + "" +
					"SELECT ?observation ?data ?instant ?timestamp " +
					"WHERE { " +
					"?data rdf:type om-owl:MeasureData . " +
					"?observation om-owl:result ?data . " +
					"?observation om-owl:samplingTime ?instant . " +
					"?instant owl-time:inXSDDateTime ?timestamp . " +
					"} " +
					"ORDER BY ?timestamp ";
			
		} catch (ModelRuntimeException e) {
			throw new SrbenchSimulatorException("Error reading simulation data.", e);
		} catch (NullPointerException e) {
			throw new SrbenchSimulatorException("Error reading simulation data.", e);
		} catch (IOException e) {
			throw new SrbenchSimulatorException("Error reading simulation data.", e);
		}
	}

	@Override
	public Iterator<Model> iterator() {
		return new SrBenchIterator();
	}
	
	class SrBenchIterator implements Iterator<Model> {

		private final ClosableIterator<QueryRow> queryRows;
		
		public SrBenchIterator() {
			queryRows = m.sparqlSelect(query).iterator();
		}
		
		@Override
		public boolean hasNext() {
			return queryRows.hasNext();
		}

		@Override
		public Model next() {
			QueryRow result = queryRows.next();

			String eventId = EventHelpers.createRandomEventId("srbech");
			Event event = new Event(EventHelpers.createEmptyModel(eventId), eventId + EVENT_ID_SUFFIX, false);
			event.getModel().setNamespace("om-owl", "http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#");
			event.getModel().setNamespace("owl-time", "http://www.w3.org/2006/time#");
			event.getModel().setNamespace("weather", "http://knoesis.wright.edu/ssw/ont/weather.owl#");

			
			// Set the event type manually:
			ClosableIterator<Statement> types = m.findStatements(result.getValue("observation").asResource(), RDF.type, Variable.ANY);
			event.getModel().addStatement(new URIImpl(eventId + EVENT_ID_SUFFIX), RDF.type, types.next().getObject());
			types.close();

			// Get timestamp into the event payload:
			event.setEndTime(Calendar.getInstance());
			//event.setEndTime(result.getValue("timestamp"));
			event.setStream(new URIImpl(Namespace.STREAMS.getUri() + "Srbench" + Stream.STREAM_ID_SUFFIX));
			event.setSource(new URIImpl(Namespace.SOURCE.getUri() + this.getClass().getSimpleName()));

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

			return event.getModel();
		}

		@Override
		public void remove() {
			throw new IllegalArgumentException("Not implemented");
		}
	}
}
