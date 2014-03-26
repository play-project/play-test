package org.ow2.play.test.ffd;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.etsi.uri.gcm.util.GCM;
import org.junit.Test;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;

import eu.play_project.dcep.SubscriberPerformanceTest;
import eu.play_project.dcep.distributedetalis.api.DistributedEtalisTestApi;
import eu.play_project.dcep.distributedetalis.utils.ProActiveHelpers;
import eu.play_project.play_platformservices.api.QueryDispatchApi;
import eu.play_project.play_platformservices.api.QueryDispatchException;


public class Main {
	private static final long serialVersionUID = 100L;
	public static QueryDispatchApi queryDispatchApi;
	public static DistributedEtalisTestApi testApi;
	boolean start = false;
	static Component root;
	public static boolean test;


  public static void main(String[] args) throws InterruptedException, QueryDispatchException, IllegalLifeCycleException, NoSuchInterfaceException, ADLException {

			String gegionDetectionQuery;
			InstantiatePlayPlatform();

			// Get query.
			gegionDetectionQuery = getSparqlQueries("queries/1-BidPhase_gps-region-detection.eprq");
			System.out.println("SPARQL query:\n" + gegionDetectionQuery);

			// Compile query
			String patternId = queryDispatchApi.registerQuery("http://test.example.com", gegionDetectionQuery);
//
//			
//			//Subscribe to get complex events.
//			SubscriberPerformanceTest subscriber = null;
//			try {
//				subscriber = PAActiveObject.newActive(SubscriberPerformanceTest.class, new Object[] {});
//			} catch (ActiveObjectCreationException e) {
//				e.printStackTrace();
//			} catch (NodeException e) {
//				e.printStackTrace();
//			}
//
//			testApi.attach(subscriber);
//
//
//		
//			// Push events.
//				for (int i = 1; i < 19; i++) {
//					if (i % 100 == 0) {
//						System.out.println("Sent " + i + " Events");
//					}
//					// subscriber.setState(false);
//					//testApi.publish(createEvent("http://exmaple.com/eventId/" + i));
//					Thread.sleep(1);
//				}
//			
//				//
//				Thread.sleep(2000);
//
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
		testApi = ((eu.play_project.dcep.distributedetalis.api.DistributedEtalisTestApi) root
				.getFcInterface(DistributedEtalisTestApi.class.getSimpleName()));
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
	  
  }
