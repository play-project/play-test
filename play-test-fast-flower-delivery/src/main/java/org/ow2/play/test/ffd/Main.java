package org.ow2.play.test.ffd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.play_project.dcep.api.DcepTestApi;
import eu.play_project.dcep.distributedetalis.utils.EventCloudHelpers;
import eu.play_project.dcep.distributedetalis.utils.ProActiveHelpers;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_platformservices.api.QueryDispatchApi;
import eu.play_project.play_platformservices.api.QueryDispatchException;


public class Main {
	private final static Logger logger = LoggerFactory.getLogger(org.ow2.play.test.ffd.Main.class);
	public static QueryDispatchApi queryDispatchApi;
	public static DcepTestApi testApi;
	boolean start = false;
	static Component root;
	public static boolean test;


  public static void main(String[] args) throws InterruptedException, QueryDispatchException, IllegalLifeCycleException, NoSuchInterfaceException, ADLException, ModelRuntimeException, IOException {

			String regionDetectionQuery;
			InstantiatePlayPlatform();

			// Get query.
			regionDetectionQuery = getSparqlQueries("queries/1-BidPhase_gps-region-detection.eprq");
			System.out.println("SPARQL query:\n" + regionDetectionQuery);

			// Compile query
			String patternId = queryDispatchApi.registerQuery("http://test.example.com", regionDetectionQuery);

			
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
			testApi.publish(EventCloudHelpers.toCompoundEvent(loadEvent("events/FDS_gps-location.trig", Syntax.forFileName("FDS_gps-location.trig"))));

			// Wait
			delay();
			System.out.println(subscriber.getComplexEvents());
			//assertEquals("We expect exactly one complex event as a result.", 1, subscriber.getComplexEvents().size());


//
//			// Test if result is OK
//			assertTrue("Number of complex events wrong "
//					+ subscriber.getComplexEvents().size(), subscriber
//					.getComplexEvents().size() == 16);
//
//
//			// Stop and terminate GCM Components
//			try {
//				GCM.getGCMLifeCycleController(root).stopFc();
//				// Terminate all subcomponents.
//				 for(Component subcomponent : GCM.getContentController(root).getFcSubComponents()){
//					GCM.getGCMLifeCycleController(subcomponent).terminateGCMComponent();
//				 }
//
//
//			} catch (IllegalLifeCycleException e) {
//				e.printStackTrace();
//			} catch (NoSuchInterfaceException e) {
//				e.printStackTrace();
//			}
	}
  
  public static void InstantiatePlayPlatform()
			throws IllegalLifeCycleException, NoSuchInterfaceException,
			ADLException {

		root = ProActiveHelpers.newComponent("PsDcepComponent");
		GCM.getGCMLifeCycleController(root).startFc();

		queryDispatchApi = ((eu.play_project.play_platformservices.api.QueryDispatchApi) root
				.getFcInterface(QueryDispatchApi.class.getSimpleName()));
		testApi = ((DcepTestApi) root .getFcInterface(DcepTestApi.class.getSimpleName()));
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
			Thread.sleep(500);
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
