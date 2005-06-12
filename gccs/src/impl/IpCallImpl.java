/**
 * 
 */
package impl;

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

/**
 * @author Nguyen Duc Du Khuong Represent each call session by IpCallImpl object
 */

public class IpCallImpl extends IpCallPOA {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Khuong added 9.28pm 07.06 / /** m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(IpCallImpl.class);
	}

	public IpCallImpl(TpCallIdentifier callid, String originatorAddress,
			String originalDestinationAddress,
			IpAppCallControlManagerImpl manager, IpAppCallImpl ipAppCallImpl)
			throws TpCommonExceptions, P_INVALID_SESSION_ID,
			P_INVALID_INTERFACE_TYPE {

	}

	/**
	 * 
	 */
	public IpCallImpl() {
		super();
		m_logger.info("ctor()");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#routeReq(int,
	 *      org.csapi.cc.gccs.TpCallReportRequest[], org.csapi.TpAddress,
	 *      org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.TpAddress,
	 *      org.csapi.cc.gccs.TpCallAppInfo[])
	 */
	public int routeReq(int callSessionID,
			TpCallReportRequest[] responseRequested, TpAddress targetAddress,
			TpAddress originatingAddress, TpAddress originalDestinationAddress,
			TpAddress redirectingAddress, TpCallAppInfo[] appInfo)
			throws P_INVALID_EVENT_TYPE, P_INVALID_NETWORK_STATE,
			TpCommonExceptions, P_INVALID_ADDRESS, P_INVALID_SESSION_ID,
			P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_CRITERIA {
		// TODO Auto-generated method stub

		EventOfCallQueue queue = EventOfCallQueue.getInstance();
		CallEvent evtCall = new CallEvent(callSessionID, targetAddress,
				originatingAddress, CallEvent.eventRouteReq, 0);
		queue.put(evtCall);
		return callSessionID;
		// // K added
		// if(m_m_logger.isInfoEnabled())
		// m_m_logger.info(("Route Request"));
		// checkEnd();
		// routeError = null;
		// //TpCallReportRequest tpCallReportReq[] =
		// try {
		// IpCallImpl.routReq(callSessionID, responseRequested,
		// targetAddress, originalDestinationAddress, originatingAddress,
		// redirectingAddress, appInfo);
		// if (m_m_logger.isInfoEnabled())
		// m_m_logger.info("ipCall.routeReq successfully return!");
		// //targetAddress =
		// }
		// catch (P_INVALID_EVENT_TYPE ex1)
		// {
		// m_m_logger.error("Catch exception of P_INVALID_EVENT_TYPE with more
		// information: " + ex1.getMessage());
		// }
		// catch (P_INVALID_NETWORK_STATE ex2)
		// {
		// m_m_logger.error("Catch exception of P_INVALID_NETWORK_STATE with more
		// information: " + ex2.getMessage());
		// }
		// catch (TpCommonExceptions ex3)
		// {
		// m_m_logger.error("Error occurs: "+ex3.getMessage());
		// }
		// catch (P_INVALID_ADDRESS ex4)
		// {
		// m_m_logger.error("Error occurs:" + ex4.getMessage());
		// }
		// catch (P_INVALID_SESSION_ID ex5)
		// {
		// m_m_logger.error("Error occurs:" + ex5.getMessage());
		// }
		// catch (P_UNSUPPORTED_ADDRESS_PLAN ex6)
		// {
		// m_m_logger.error("Error occurs:" + ex6.getMessage());
		// }
		// catch (P_INVALID_CRITERIA ex7)
		// {
		// m_m_logger.error("Error occurs:" + ex7.getMessage());
		// }
		// return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#release(int,
	 *      org.csapi.cc.gccs.TpCallReleaseCause)
	 */
	public void release(int callSessionID, TpCallReleaseCause cause)
			throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub
		// Khuong added
		EventOfCallQueue queue = EventOfCallQueue.getInstance();
		CallEvent evtCall = new CallEvent(callSessionID, null,
				null, CallEvent.eventReleaseCall, 0);
		queue.put(evtCall);

//		if (m_logger.isInfoEnabled())
//			m_logger.info("Entering release!");
//		IpCall localCopy = cleanupCall();
//		try {
//			localCopy.release(callSessionID, new TpCallReleaseCause());
//			cleanupCall();
//		} catch (TpCommonExceptions ex1) {
//			m_logger.error("Error occurs: " + ex1.getMessage());
//		} catch (P_INVALID_NETWORK_STATE ex2) {
//			m_logger
//					.error("Catch exception of P_INVALID_NETWORK_STATE with more information: "
//							+ ex2.getMessage());
//		} catch (P_INVALID_SESSION_ID ex3) {
//			m_logger
//					.error("Catch exception of P_INVALID_SESSION_ID with more information: "
//							+ ex3.getMessage());
//		}
//		return 0;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#deassignCall(int)
	 */
	public void deassignCall(int callSessionID) throws TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub
		EventOfCallQueue queue = EventOfCallQueue.getInstance();
		CallEvent evtCall = new CallEvent(callSessionID, null,
				null, CallEvent.eventDeassignCall, 0);
		queue.put(evtCall);
		
//		if (m_logger.isInfoEnabled())
//			m_logger.info("Entering release!");
//		IpCall localCopy = cleanupCall();
//		try {
//			localCopy.deassignCall(callSessionID);
//			cleanup(localCopy);
//		} catch (TpCommonExceptions ex1) {
//			m_logger.error("Error occurs: " + ex1.getMessage());
//		} catch (P_INVALID_SESSION_ID ex2) {
//			m_logger
//					.error("Catch exception of P_INVALID_SESSION_ID with more information: "
//							+ ex2.getMessage());
//		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#getCallInfoReq(int, int)
	 */
	public void getCallInfoReq(int callSessionID, int callInfoRequested)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		m_logger.info("getCallInfoReq");
		m_logger.debug("getCallInfoReq - Unimplemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#setCallChargePlan(int,
	 *      org.csapi.cc.TpCallChargePlan)
	 */
	public void setCallChargePlan(int callSessionID,
			TpCallChargePlan callChargePlan) throws TpCommonExceptions,
			P_INVALID_SESSION_ID {
		m_logger.info("setCallChargePlan");
		m_logger.debug("setCallChargePlan - Unimplemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#setAdviceOfCharge(int,
	 *      org.csapi.TpAoCInfo, int)
	 */
	public void setAdviceOfCharge(int callSessionID, TpAoCInfo aOCInfo,
			int tariffSwitch) throws TpCommonExceptions, P_INVALID_SESSION_ID {
		m_logger.info("setAdviceOfCharge");
		m_logger.debug("setAdviceOfCharge - Unimplemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#getMoreDialledDigitsReq(int, int)
	 */
	public void getMoreDialledDigitsReq(int callSessionID, int length)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		m_logger.info("getMoreDialledDigietsReq");
		m_logger.debug("getMoreDialledDigitsReq - Unimplemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#superviseCallReq(int, int, int)
	 */
	public void superviseCallReq(int callSessionID, int time, int treatment)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		m_logger.info("superviseCallReq");
		m_logger.debug("superviseCallReq - Unimplemented");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#continueProcessing(int)
	 */
	public void continueProcessing(int callSessionID)
			throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		m_logger.info("continueProcessing");
		m_logger.debug("continueProcessing - Unimplemented");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
	 */
	public void setCallback(IpInterface appInterface)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
		m_logger.info("setCallback");
		m_logger.debug("setCallback - Unimplemented");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.IpServiceOperations#setCallbackWithSessionID(org.csapi.IpInterface,
	 *      int)
	 */
	public void setCallbackWithSessionID(IpInterface appInterface, int sessionID)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub

	}

	private int callSessionID;

	private IpCall ipCall;

	private IpAppCallControlManagerImpl manager;

}
