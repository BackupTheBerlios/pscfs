//$Id: GCCSServer.java,v 1.7 2005/07/29 00:32:43 huuhoa Exp $
/**
 * Creation date: 21.06.2005 - 2005
 * Creator: Nguyen Huu Hoa
 */
package group5.server.framework;

import group5.server.CallSimulator;
import group5.server.Subscribers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class GCCSServer {
	private static Logger m_logger = Logger.getLogger(GCCSServer.class);

	private static String readIOR() {
		String aHostName = "localhost";
		int aPortNumber = 23104;
		String nsIOR = "";
		// open a socket to the Naming Responder
		Socket cnrs = null;
		while (cnrs == null) {
			try {
				cnrs = new Socket(aHostName, aPortNumber);
			} catch (IOException e) {
			}
			try {
				Thread.sleep(cnrs == null ? 500 : 5000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}

		try {
			// create a reader for the socket to properly convert
			// bytes to 16-bit UNICODE characters
			BufferedReader cnrr = new BufferedReader(new InputStreamReader(cnrs
					.getInputStream()));

			m_logger.debug("Fetching Naming Service IOR:");

			// read the IOR of the Naming Service
			nsIOR = cnrr.readLine();

			// dispose of network resources that are no longer needed
			cnrr.close();
			cnrs.close();
		} catch (IOException ex) {

		}
		// the retrieved IOR may contain a terminating, illegal
		// 0-character. If so, ignore it.
		if (nsIOR.charAt(nsIOR.length() - 1) == '\0') {
			nsIOR = nsIOR.substring(0, nsIOR.length() - 1);
		}
		return nsIOR;
	}

	public static void main(String[] args) {
		Properties appProps = new Properties();
		try {
			FileInputStream fis = new FileInputStream(
					"etc/GCCSServer.properties");
			appProps.load(fis);
			fis.close();
		} catch (IOException e) {
			System.out.println("Cannot find properties file");
		}
		Properties oldProp = System.getProperties();
		appProps.putAll(oldProp);
		System.setProperties(appProps);
		try {
			PropertyConfigurator.configure(System
					.getProperty("log4j.configuration"));
		} catch (ExceptionInInitializerError ex) {
			ex.printStackTrace();
			return;
		}
		try {

			int nORB = Integer.parseInt(System.getProperty("Framework.ORB"));
			String ORB_NS = "";
			switch (nORB) {
			case 0: // milife
				// ORB_NS =
				// "corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root";
				break;
			case 1: // erricson
				ORB_NS = GCCSServer.readIOR();
				m_logger.debug("ORB_NS - " + ORB_NS);
				System.setProperty("ORB.NameService", ORB_NS);
				break;
			}
			// initializing the call simulator
			CallSimulator simulator = new CallSimulator();
			// then adding some subscribers, should got from database
			// but from text file should be enough
			readSubscribersDatabase();
			simulator.startSimulator();

			Thread th = new Thread(new Runnable() {
				public void run() {
					CallControlSCS serviceInstance = CallControlSCS
							.getInstance();
					serviceInstance.startService();
				}
			});
			th.run();

			// adding some subscribers

			// just wait for enter key
			System.in.read();

			CallControlSCS serviceInstance = CallControlSCS.getInstance();
			serviceInstance.stopService();
			serviceInstance.destroy();
			simulator.stopSimulator();
			th.stop();
			m_logger.debug("Service exits");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void readSubscribersDatabase() {
		// get an instance of subscribers
		Subscribers subColl = Subscribers.getInstance();
		// adding four subscribers
		subColl.addSubscriber("1");
		subColl.addSubscriber("2");
		subColl.addSubscriber("3");
		subColl.addSubscriber("4");
	}
}
