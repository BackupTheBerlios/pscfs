/**
 * 
 */
package group5.server;

import org.apache.log4j.Logger;
import org.csapi.TpCommonExceptions;
import org.omg.CORBA.UserException;

/**
 * Call control service
 * @author Nguyen Huu Hoa
 *
 */
public final class CallControlSCS extends ServerFramework 
	implements IpSCS, ServiceInstance {

    private static Logger m_logger = Logger.getLogger(CallControlSCS.class);
    private static CallControlSCS m_this;
    static
    {
    	try
    	{
        	m_this = new CallControlSCS("huuhoa", "123456");
    	}
    	catch (UserException ex)
    	{
    		m_logger.fatal("Can not create server framework. " + ex.getMessage());
    		m_this = null;
    	}
    }
    private String m_serviceID;
//    private iSCSParserEventHandler m_parserEventHandler;

    public static CallControlSCS getInstance()
    {
        return m_this;
    }

    private CallControlSCS(String appId, String password) throws UserException
    {
    	super(appId, password);
    	m_serviceID = "";
//    	m_parserEventHandler = new CCSCSParserEventHandler();
    }

//    public iSCSParserEventHandler getParserEventHandler()
//    {
//        return m_parserEventHandler;
//    }

    public int startService()
    {
        SCSProperties scsproperties = new SCSProperties("P_GENERIC_CALL_CONTROL", "1.0");
        // SCSProperties scsproperties = new SCSProperties("CamelCallControl", "3.3");
        scsproperties.setProperty("P_SUPPORTED_INTERFACES", new String[] {
            "IpService", "IpCallControlManager", "IpCall"
        });
        scsproperties.setProperty("P_OPERATION_SET", new String[] {
            "IpCallControlManager.enableCallNotification", "IpCallControlManager.disableCallNotification", "IpCallControlManager.changeCallNotification", "IpCallControlManager.getCriteria", "IpCall.routeReq", "IpCall.release", "IpCall.deassignCall"
        });
        scsproperties.setProperty("P_TRIGGERING_EVENT_TYPES", new String[] {
            Integer.toString(2), Integer.toString(4), Integer.toString(8), Integer.toString(16), Integer.toString(32), Integer.toString(64)
        });
        scsproperties.setProperty("P_DYNAMIC_EVENT_TYPES", new String[] {
            Integer.toString(3), Integer.toString(4), Integer.toString(5), Integer.toString(6), Integer.toString(11), Integer.toString(9)
        });
        scsproperties.setProperty("P_ADDRESSPLAN", new String[] {
            Integer.toString(5)
        });
        scsproperties.setProperty("P_UI_CALL_BASED", new String[] {
            "TRUE"
        });
        scsproperties.setProperty("P_UI_AT_ALL_STAGES", new String[] {
            "FALSE"
        });
        scsproperties.setProperty("P_MEDIA_TYPE", new String[] {
            Integer.toString(1)
        });
        try
        {
        	m_serviceID = registerService("P_GENERIC_CALL_CONTROL", scsproperties, this.getClass());
        }
        catch(TpCommonExceptions ex)
        {
        	m_logger.fatal("Error starting service CallControlSCS" + ex.getMessage(), ex);
            return 1;
        }
        catch(Exception ex)
        {
        	m_logger.fatal("Error starting service CallControlSCS", ex);
            return 2;
        }
        m_logger.info("Started service CallControlSCS");
        return 0;
    }

    public int stopService()
    {
   		unannounceService(m_serviceID);
    	return 0;
    }

	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
