//$Id: GCCSServer.java,v 1.1 2005/06/22 08:23:18 huuhoa Exp $
/**
 * Creation date: 21.06.2005 - 2005
 * Creator: Nguyen Huu Hoa
 */
package group5.server.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * @author Nguyen Huu Hoa
 * 
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
		BasicConfigurator.configure();
		try {
			int nORB = 0;
			if (args.length>1)
			{
				if (args[1].compareToIgnoreCase("-milife")==0)
				{
					nORB = 0; // milife
				}
				if (args[1].compareToIgnoreCase("-erricson")==0)
				{
					nORB = 1; // erricson
				}
			}
			String ORB_NS = "";
			switch(nORB)
			{
			case 0:	// milife
				ORB_NS = "corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root";
				break;
			case 1: // erricson
				ORB_NS = GCCSServer.readIOR();
				break;
			}
			m_logger.debug("ORB_NS - " + ORB_NS);
			System.setProperty("ORB.NameService", ORB_NS);
			CallControlSCS serviceInstance = CallControlSCS.getInstance();
			serviceInstance.startService();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
