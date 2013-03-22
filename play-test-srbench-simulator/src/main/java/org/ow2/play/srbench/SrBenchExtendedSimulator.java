package org.ow2.play.srbench;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.ontoware.rdf2go.model.Model;

/**
 * An extended simulator iterating over many input files. The order of the files
 * provided to the constructor must be in ascending order of event occurrence so
 * that a total order of simulated events is guaranteed.
 * 
 * @author Roland Stühmer
 */
public class SrBenchExtendedSimulator implements Iterable<Model> {

	private final Iterator<String> fileIterator;
	private Iterator<Model> currentSimulation;

	/**
	 * Return an extended simulator.
	 */
	public SrBenchExtendedSimulator() throws SrbenchSimulatorException {
		this("REDO3_2009_9_19.n3", "KLCK_2009_9_20.n3", "DYNT2_2009_9_21.n3");
	}

	/**
	 * Return an extended simulator reading data from specified files. The order
	 * of the files provided to the constructor must be in ascending order of
	 * event occurrence so that a total order of simulated events is guaranteed.
	 */
	public SrBenchExtendedSimulator(String... turtleFileName) throws SrbenchSimulatorException {
		if (turtleFileName == null || turtleFileName.length == 0) {
			throw new SrbenchSimulatorException("You must specify at least one filename in turtleFileName");
		}

		this.fileIterator = Arrays.asList(turtleFileName).iterator();
		this.currentSimulation = new SrbenchSimulator(this.fileIterator.next()).iterator();
	}
	
	@Override
	public Iterator<Model> iterator() {
		return new SrBenchExtendedIterator();
	}
	
	/**
	 * An iterator {@linkplain SrBenchExtendedIterator} with nested iterators
	 * {@linkplain SrbenchSimulator}. Whenever a nested iterator has no more
	 * elements the outer iterator moves on.
	 * 
	 * @author Roland Stühmer
	 */
	class SrBenchExtendedIterator implements Iterator<Model> {

		@Override
		public boolean hasNext() {
			if (currentSimulation.hasNext()) {
				return true;
			}
			else if (fileIterator.hasNext()) {
				String fileName = fileIterator.next();
				try {
					currentSimulation = new SrbenchSimulator(fileName).iterator();
				} catch (SrbenchSimulatorException e) {
					// Do nothing
				}
				// Do not simply return true here, the nested itearator might be empty:
				return this.hasNext();
			}
			else {
				return false;
			}
		}

		@Override
		public Model next() {
			if (this.hasNext()) {
				return currentSimulation.next();
			}
			else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			throw new IllegalArgumentException("Not implemented");
		}
	}
}
