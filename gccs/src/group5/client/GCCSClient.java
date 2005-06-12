//$Id: GCCSClient.java,v 1.4 2005/06/12 22:24:04 huuhoa Exp $
/**
 * 
 */
package group5.client;

import org.apache.log4j.BasicConfigurator;
import org.csapi.mm.us.IpUserStatus;
import org.csapi.mm.us.IpUserStatusHelper;

/**
 * @author Nguyen Huu Hoa Sample client that use ApplicationFramework
 */
public class GCCSClient extends ApplicationFramework {
	GCCSClient() throws org.omg.CORBA.UserException {
		super();
	}

	/**
	 * The main method, runs the sample.
	 */
	public static void main(String args[]) {
		BasicConfigurator.configure();
		if (args.length < 1) {
			System.out.println("Error: user's MSISDN not specified");
			System.exit(1);
		}
		String user = args[0];
		try {
			System
					.setProperty("ORB.NameService",
							"corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root");
			GCCSClient sample = new GCCSClient();
			sample.initApplication(ApplicationFramework.clientAppID,
					ApplicationFramework.clientAppSharedSecret);
			IpUserStatus ipUS = IpUserStatusHelper.narrow(sample
					.selectSCFs("P_USER_STATUS"));
			sample.requestStatus(ipUS, user);
			sample.endApplication();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
