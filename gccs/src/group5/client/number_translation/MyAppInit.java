package group5.client.number_translation;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.csapi.IpService;
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.IpCallControlManagerHelper;
import org.omg.CORBA.UserException;

import group5.client.ApplicationFramework;

public class MyAppInit extends ApplicationFramework {
	MyAppInit() throws UserException {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(MyAppInit.class);
	}

	public void run()
	{
        try {
        	// Get reference to IpCallControlManager interface
        	IpService ipTemp = selectSCFs("P_GENERIC_CALL_CONTROL");
        	if (ipTemp==null)
        	{
        		m_logger.error("Can not obtain reference to IpCallControlManager");
        		return;
        	}
        	IpCallControlManager ipCCM = IpCallControlManagerHelper.narrow(ipTemp);
        	MyApplicationLogic appLogic = new MyApplicationLogic(ipCCM);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
	}
	public static void main(String args[])
	{
		// run the application
		BasicConfigurator.configure();
    	System.setProperty("ORB.NameService", "corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root");
		try
		{
			MyAppInit application = new MyAppInit();
			application.initApplication("huuhoa", "123456");
			application.run();
			application.endApplication();
		}
		catch (UserException ex)
		{
			System.out.println(ex.getMessage());
		}
	}
}
