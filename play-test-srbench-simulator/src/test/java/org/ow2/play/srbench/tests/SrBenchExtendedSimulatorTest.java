package org.ow2.play.srbench.tests;

import junit.framework.Assert;

import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ow2.play.srbench.SrBenchExtendedSimulator;
import org.ow2.play.srbench.SrbenchSimulatorException;

public class SrBenchExtendedSimulatorTest {

	/**
	 * Test the iterator by writing simulated events to stdout.
	 * @throws SrbenchSimulatorException
	 */
	@Test
	public void testSrBenchExtendedSimulator() throws SrbenchSimulatorException {
		
		int i = 0;
		for (Model simulatedEvent : new SrBenchExtendedSimulator("SMALL_FILE_01.n3", "SMALL_FILE_02.n3")) {
			simulatedEvent.dump();
			i++;
		}
		Assert.assertEquals("The wrong number of events was produced from the testfiles.", 2, i);
	}
}
