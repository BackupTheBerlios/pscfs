//$Id: GCCSClient.java,v 1.6 2005/07/01 09:20:13 huuhoa Exp $
/**
 * 
 */
package group5.client;

import org.apache.log4j.PropertyConfigurator;
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
		PropertyConfigurator.configure(args[0]);
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
