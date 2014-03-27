package org.ow2.play.test.ffd.tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

import eu.play_project.play_commons.eventtypes.EventHelpers;
import fr.inria.eventcloud.api.responses.SparqlQueryStatistics;
import fr.inria.eventcloud.api.responses.SparqlSelectResponse;
import fr.inria.eventcloud.api.wrappers.ResultSetWrapper;

public class HistoricDataTest {
	
	/**
	 * Read Model from file and test basic graph query.
	 */
	@Test
	public void readBerlinQuarterModelFromFile() throws SyntaxNotSupportedException, ModelRuntimeException, IOException {
		// Create an empty model.
		ModelSet rdf = EventHelpers.createEmptyModelSet();

		InputStream in = this.getClass().getClassLoader().getResourceAsStream("static-data/berlin-quarters.trig");
		if (in == null) {
			throw new IllegalArgumentException("File: static-data/berlin-quarters.trig not found");
		}

		// Read data from file.
		rdf.readFrom(in, Syntax.Trig);

		// Query data from model
		//Query query = QueryFactory.create("SELECT * WHERE {?S ?P ?O}");
		Query query = QueryFactory.create(""
				+ "SELECT *"
				+ " WHERE { "
					+ "GRAPH ?id{?S ?P ?O} "
					+ "FILTER (?id = ?id2) "
					+ "VALUES (?id2){(<http://dbspedia.org/>)} "
				+ "}");

		Dataset jena = (Dataset) rdf.getUnderlyingModelSetImplementation();
		
		QueryExecution qexec = QueryExecutionFactory.create(query, jena);

		SparqlSelectResponse result;
		try {
			ResultSet results = qexec.execSelect();

			// Put result in PLAY result wrapper.
			ResultSetWrapper dataIn = new ResultSetWrapper(results);
			result = new SparqlSelectResponse(new SparqlQueryStatistics(1, 1, 1, 1, 1, 1, 1), dataIn);
		} finally {
			qexec.close();
		}
		
		System.out.println(result.getResult().next());
//		assertTrue(result.getResult().next().get("O").toString()
//				.equals("Roland St\u00FChmer"));
	}

}
