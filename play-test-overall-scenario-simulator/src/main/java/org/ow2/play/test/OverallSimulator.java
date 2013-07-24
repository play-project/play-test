package org.ow2.play.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.openrdf.rdf2go.RepositoryModelSet;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class OverallSimulator {

	private final Map<String, SailRepository> sesameRepository = new HashMap<String, SailRepository>();
	public Map<String, RepositoryModelSet> sesame = new HashMap<String, RepositoryModelSet>();
	private final String[] companies = new String[] { "apple", "microsoft", "google", "yahoo" };
	public final Map<String, TweetSimulator> simulator = new HashMap<String, TweetSimulator>();
	
	public static void main(String[] args) {
		OverallSimulator sim = new OverallSimulator();
		sim.init();
		
		boolean proceed = true;
		
		while (proceed) {
			for (TweetSimulator t : sim.simulator.values()) {
				if (t.hasNext()) {
					Model m = t.next();
					System.out.println("==============================================================================================");
					System.out.println(m.getContextURI());
					m.dump();
				}
				else {
					proceed = false;
				}
			}
		}

		sim.destroy();
	}

	public void init() {

		for (String company : companies) {
			
			try {
				File dataDir = new File(String.format("./src/main/resources/%s/", company));

				sesameRepository.put(company, new SailRepository(new MemoryStore(dataDir)));
				sesameRepository.get(company).initialize();

				sesame.put(company, new RepositoryModelSet(sesameRepository.get(company)));
				sesame.get(company).open();
				
				simulator.put(company, new TweetSimulator(sesame.get(company), company));

			} catch (RepositoryException e) {
				Logger.getAnonymousLogger().log(Level.WARNING,
						"Problem while initializing Sesame storage.", e);
			}

		}

	}

	public void destroy() {

		for (RepositoryModelSet modelSet : sesame.values()) {
			modelSet.close();
		}
		for (SailRepository repo : sesameRepository.values()) {
			try {
				repo.shutDown();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}
}
