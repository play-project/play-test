package org.ow2.play.srbench.tests;

import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ow2.play.srbench.SrbenchSimulator;
import org.ow2.play.srbench.SrbenchSimulatorException;

public class SrbenchSimulatorTest {

	/**
	 * Test the iterator by writing all simulated events to stdout.
	 */
	@Test
	public void testSrbenchSimulator() throws SrbenchSimulatorException {
		for (Model m : new SrbenchSimulator()) {
			m.dump();
		}
	}
}
