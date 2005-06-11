/**
 * 
 */
package impl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.csapi.IpInterface;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddress;
import org.csapi.TpAoCInfo;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallChargePlan;
import org.csapi.cc.gccs.IpCallPOA;
import org.csapi.cc.gccs.TpCallAppInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallReleaseCause;
import org.csapi.cc.gccs.TpCallReportRequest;
import org.csapi.cc.gccs.IpCall;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import group5.*;


/**
 * @author Nguyen Duc Du Khuong
 * Represent each call session by IpCallImpl object
 */



public class IpCallImpl extends IpCallPOA {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(IpCallControlManagerImpl.class);
	/**
	 * Khuong added 9.28pm 07.06
	 * /
	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(IpCallImpl.class);
	}
	
    public IpCallImpl(TpCallIdentifier callid, String originatorAddress, String originalDestinationAddress, IpAppCallControlManagerImpl manager, IpAppCallImpl ipAppCallImpl)
    throws TpCommonExceptions, P_INVALID_SESSION_ID, P_INVALID_INTERFACE_TYPE
    {
    	
    }
	/**
	 * 
	 */
	public IpCallImpl() {
		super();
		BasicConfigurator.configure();
		logger.info("ctor()");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#routeReq(int, org.csapi.cc.gccs.TpCallReportRequest[], org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.cc.gccs.TpCallAppInfo[])
	 */
	public int routeReq(int callSessionID,
			TpCallReportRequest[] responseRequested, TpAddress targetAddress,
			TpAddress originatingAddress, TpAddress originalDestinationAddress,
			TpAddress redirectingAddress, TpCallAppInfo[] appInfo)
			throws P_INVALID_EVENT_TYPE, P_INVALID_NETWORK_STATE,
			TpCommonExceptions, P_INVALID_ADDRESS, P_INVALID_SESSION_ID,
			P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_CRITERIA {
		// TODO Auto-generated method stub
		
		// K added
		if(m_logger.isInfoEnabled())
			m_logger.info(("Route Request"));
		checkEnd();
		routeError = null;
		//TpCallReportRequest tpCallReportReq[] = 
		try {
			IpCallImpl.routReq(callSessionID, responseRequested, 
			targetAddress, originalDestinationAddress, originatingAddress,
			redirectingAddress, appInfo);
			if (m_logger.isInfoEnabled())
				m_logger.info("ipCall.routeReq successfully return!");
			//targetAddress =
		}
		catch (P_INVALID_EVENT_TYPE ex1)
		{
			m_logger.error("Catch exception of P_INVALID_EVENT_TYPE with more information: " + ex1.getMessage());
		}
		catch (P_INVALID_NETWORK_STATE ex2)
		{
			m_logger.error("Catch exception of P_INVALID_NETWORK_STATE with more information: " + ex2.getMessage());
		}
		catch (TpCommonExceptions ex3)
		{
			m_logger.error("Error occurs: "+ex3.getMessage());
		}
		catch (P_INVALID_ADDRESS ex4)
		{
			m_logger.error("Error occurs:" + ex4.getMessage());
		}
		catch (P_INVALID_SESSION_ID ex5)
		{
			m_logger.error("Error occurs:" + ex5.getMessage());
		}
		catch (P_UNSUPPORTED_ADDRESS_PLAN ex6)
		{
			m_logger.error("Error occurs:" + ex6.getMessage());
		}
		catch (P_INVALID_CRITERIA ex7)
		{
			m_logger.error("Error occurs:" + ex7.getMessage());
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#release(int, org.csapi.cc.gccs.TpCallReleaseCause)
	 */
	public void release(int callSessionID, TpCallReleaseCause cause)
			throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub
		//Khuong added
		if(m_logger.isInfoEnabled())
			m_logger.info("Entering release!");
		IpCall localCopy = cleanupCall();
		try
		{
			localCopy.release(callSessionID, new TpCallReleaseCause());
			cleanupCall();
		}
		catch (TpCommonExceptions ex1)
		{
			m_logger.error("Error occurs: "+ex1.getMessage());
		}
		catch (P_INVALID_NETWORK_STATE ex2)
		{
			m_logger.error("Catch exception of P_INVALID_NETWORK_STATE with more information: " + ex2.getMessage());
		}
		catch (P_INVALID_SESSION_ID ex3)
		{
			m_logger.error("Catch exception of P_INVALID_SESSION_ID with more information: " + ex3.getMessage());
		}
		return 0;
		
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#deassignCall(int)
	 */
	public void deassignCall(int callSessionID) throws TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub
		if(m_logger.isInfoEnabled())
			m_logger.info("Entering release!");
		IpCall localCopy = cleanupCall();
		try{
			localCopy.deassignCall(callSessionID);
            cleanup(localCopy);
		}
		catch(TpCommonExceptions ex1)
		{
			m_logger.error("Error occurs: "+ex1.getMessage());
		}
		catch(P_INVALID_SESSION_ID ex2)
		{
			m_logger.error("Catch exception of P_INVALID_SESSION_ID with more information: " + ex2.getMessage());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#getCallInfoReq(int, int)
	 */
	public void getCallInfoReq(int callSessionID, int callInfoRequested)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		logger.info("getCallInfoReq");
		logger.debug("getCallInfoReq - Unimplemented");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#setCallChargePlan(int, org.csapi.cc.TpCallChargePlan)
	 */
	public void setCallChargePlan(int callSessionID,
			TpCallChargePlan callChargePlan) throws TpCommonExceptions,
			P_INVALID_SESSION_ID {
		logger.info("setCallChargePlan");
		logger.debug("setCallChargePlan - Unimplemented");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#setAdviceOfCharge(int, org.csapi.TpAoCInfo, int)
	 */
	public void setAdviceOfCharge(int callSessionID, TpAoCInfo aOCInfo,
			int tariffSwitch) throws TpCommonExceptions, P_INVALID_SESSION_ID {
		logger.info("setAdviceOfCharge");
		logger.debug("setAdviceOfCharge - Unimplemented");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#getMoreDialledDigitsReq(int, int)
	 */
	public void getMoreDialledDigitsReq(int callSessionID, int length)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		logger.info("getMoreDialledDigietsReq");
		logger.debug("getMoreDialledDigitsReq - Unimplemented");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#superviseCallReq(int, int, int)
	 */
	public void superviseCallReq(int callSessionID, int time, int treatment)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		logger.info("superviseCallReq");
		logger.debug("superviseCallReq - Unimplemented");

	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#continueProcessing(int)
	 */
	public void continueProcessing(int callSessionID)
			throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		logger.info("continueProcessing");
		logger.debug("continueProcessing - Unimplemented");

	}

	/* (non-Javadoc)
	 * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
	 */
	public void setCallback(IpInterface appInterface)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
		logger.info("setCallback");
		logger.debug("setCallback - Unimplemented");

	}

	/* (non-Javadoc)
	 * @see org.csapi.IpServiceOperations#setCallbackWithSessionID(org.csapi.IpInterface, int)
	 */
	public void setCallbackWithSessionID(IpInterface appInterface, int sessionID)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub

	}
	
	// New
	
	
	//Khuong added
	
	private synchronized IpCall cleanupCall()
	throws CallControlException
    {
//		try{
			 checkEnded();
			 IpCall result = ipCall;
			 ipCall = null;
//		}
//		catch(CallControlException ex1)
//		{
//			m_logger.error("Error occurs: "+ex1.getMessage());
//		}
	    return result;
    }
	
	 private void cleanup(IpCall localCopy)
	    {
	        if(localCopy != null)
	        {
	            manager.callDone(callSessionID);
	            localCopy._release();
	        }
	    }
	private void checkEnded()
    throws CallControlException
    {
	    if(ipCall == null)
	        throw new CallControlException("Call has ended/already deassigned/already released", 0, 2);
	    else
	        return;
    }
	
	
	//Chua sua
	public static String _mthdo() {
		// TODO Auto-generated method stub
		return null;
	}
	//Chua sua
	public static CallContext getInstance() {
		// TODO Auto-generated method stub
		return null;
	}	
	
	private int callSessionID;
	private IpCall ipCall;
    private IpAppCallControlManagerImpl manager;
    
}
	