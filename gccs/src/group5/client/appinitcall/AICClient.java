//$Id: AICClient.java,v 1.1 2005/07/09 13:20:33 aachenner Exp $
package group5.client.appinitcall;

import group5.client.ApplicationFramework;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.csapi.IpService;
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.IpCallControlManagerHelper;
import org.omg.CORBA.UserException;

public class AICClient extends ApplicationFramework {
	AICClient() throws UserException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(AICClient.class);
	}

	public void run() {
		try {
			// Get reference to IpCallControlManager interface
			IpService ipTemp = selectSCFs("P_GENERIC_CALL_CONTROL");
			if (ipTemp == null) {
				m_logger
						.error("Can not obtain reference to IpCallControlManager");
				return;
			}
			IpCallControlManager ipCCM = IpCallControlManagerHelper
					.narrow(ipTemp);
			MyApplicationLogic appLogic = new MyApplicationLogic(ipCCM);
			appLogic.run();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String args[]) {
		// run the application
		Properties appProps = new Properties();
		try {
			FileInputStream fis = new FileInputStream("MyAppInitiatedCall.properties");
			appProps.load(fis);
			fis.close();
		} catch (IOException e) {
			m_logger.fatal("Cannot find properties file");
		}
		System.setProperties(appProps);
		BasicConfigurator.configure();
		try {
			AICClient application = new AICClient();
			application.initApplication(System.getProperty("ApplicationID"), System.getProperty("ApplicationPassword"));
			application.run();
			application.endApplication();
		} catch (UserException ex) {
			m_logger.fatal(ex.getMessage());
		}
	}
}