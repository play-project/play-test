package org.ow2.play.test.ffd.tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.etsi.uri.gcm.util.GCM;
import org.junit.Test;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ow2.play.test.ffd.Main;
import org.ow2.play.test.ffd.SimplePublishApiSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.play_project.dcep.api.DcepTestApi;
import eu.play_project.dcep.distributedetalis.utils.EventCloudHelpers;
import eu.play_project.dcep.distributedetalis.utils.ProActiveHelpers;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_platformservices.api.QueryDispatchApi;
import eu.play_project.play_platformservices.api.QueryDispatchException;

public class GpsLocationUpdateAndRegionDetectionTest extends ScenarioAbstractTest {
	
private final Logger logger = LoggerFactory.getLogger(GpsLocationUpdateAndRegionDetectionTest.class);
	
	@Test
	public void testGps() throws IOException, QueryDispatchException, IllegalLifeCycleException, NoSuchInterfaceException, ADLException {

		String gegionDetectionQuery;
		InstantiatePlayPlatform();

		// Get query.
		gegionDetectionQuery = getSparqlQueries("queries/1-BidPhase_gps-region-detection.eprq");
		System.out.println("SPARQL query:\n" + gegionDetectionQuery);

		// Compile query
		queryDispatchApi.registerQuery("http://test.example.com", gegionDetectionQuery);

		//Subscribe to get complex events.
		SimplePublishApiSubscriber subscriber = null;
		try {
			subscriber = PAActiveObject.newActive(SimplePublishApiSubscriber.class, new Object[] {});
		} catch (ActiveObjectCreationException e) {
			e.printStackTrace();
		} catch (NodeException e) {
			e.printStackTrace();
		}
		testApi.attach(subscriber);

		logger.info("Publish events");
		testApi.publish(EventCloudHelpers.toCompoundEvent(loadEvent("events/FDS_gps-location.trig", Syntax.Trig)));

		// Wait
		delay();
		
		System.out.println(subscriber.getComplexEvents());
		assertEquals("We expect exactly one complex event as a result.", 1, subscriber.getComplexEvents().size());
		assertEquals("We expect exactly 12 triples in complex event.", 12, subscriber.getComplexEvents().get(0).getTriples().size());
	}

	public static void InstantiatePlayPlatform()
			throws IllegalLifeCycleException, NoSuchInterfaceException,
			ADLException {
	
		root = ProActiveHelpers.newComponent("PsDcepComponent");
		GCM.getGCMLifeCycleController(root).startFc();
	
		queryDispatchApi = ((eu.play_project.play_platformservices.api.QueryDispatchApi) root
				.getFcInterface(QueryDispatchApi.class.getSimpleName()));
		testApi = ((DcepTestApi) root.getFcInterface(DcepTestApi.class.getSimpleName()));
	}

	private static String getSparqlQueries(String queryFile){
		try {
			InputStream is = Main.class.getClassLoader().getResourceAsStream(queryFile);
			BufferedReader br =new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String line;
			
			while (null != (line = br.readLine())) {
					sb.append(line);
					sb.append("\n");
			}
			//System.out.println(sb.toString());
			br.close();
			is.close();
	
			return sb.toString();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}
	
	private static void delay(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static Model loadEvent(String rdfFile, Syntax rdfSyntax) throws ModelRuntimeException, IOException{
		ModelSet event = EventHelpers.createEmptyModelSet();
		event.readFrom(org.ow2.play.test.ffd.Main.class.getClassLoader().getResourceAsStream(rdfFile), rdfSyntax);
		return event.getModels().next();
	}
  
}