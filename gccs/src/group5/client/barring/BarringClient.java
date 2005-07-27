//$Id: BarringClient.java,v 1.5 2005/07/27 08:47:09 huuhoa Exp $
package group5.client.barring;

import group5.client.ApplicationFramework;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.csapi.IpService;
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.IpCallControlManagerHelper;
import org.omg.CORBA.UserException;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class BarringClient extends ApplicationFramework {
	BarringClient() throws UserException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(BarringClient.class);
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
			BarringApplicationLogic appLogic = new BarringApplicationLogic(
					ipCCM);
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
			FileInputStream fis = new FileInputStream("etc/Barring.properties");
			appProps.load(fis);
			fis.close();
		} catch (IOException e) {
			m_logger.fatal("Cannot find properties file");
		}
		System.setProperties(appProps);
		PropertyConfigurator.configure(System
				.getProperty("log4j.configuration"));
		try {
			BarringClient application = new BarringClient();
			application.initApplication(System.getProperty("ApplicationID"),
					System.getProperty("ApplicationPassword"));
			application.run();
			application.endApplication();
		} catch (UserException ex) {
			m_logger.fatal(ex.getMessage());
		}
	}
}
