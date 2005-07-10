//$Id: IpCallImpl.java,v 1.23 2005/07/10 16:31:45 hoanghaiham Exp $
/**
 * 
 */
package group5.server;

import group5.client.IpAppCallControlManagerImpl;
import group5.client.IpAppCallImpl;

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
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManagerHelper;
import org.csapi.cc.gccs.IpAppCallHelper;
import org.csapi.cc.gccs.IpCallPOA;
import org.csapi.cc.gccs.TpCallAppInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallReleaseCause;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportRequest;

/**
 * Represent each call session by IpCallImpl object
 * 
 * @author Nguyen Duc Du Khuong
 */

public class IpCallImpl extends IpCallPOA implements IpEventHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int nWatcherID;

	private IpAppCall appCall;

	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(IpCallImpl.class);
	}

	public void setIpAppCall(IpAppCall appCall) {
		this.appCall = appCall;
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
		nWatcherID = 0;
		appCall = null;
	}

	public IpCallImpl(IpAppCall appCall) {
		super();
		m_logger.info("ctor(appCall)");
		nWatcherID = 0;
		this.appCall = appCall;
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
		m_logger.info("Route Request");
		if (targetAddress == null)
			throw new P_INVALID_ADDRESS("Error in the target address");
		if (originalDestinationAddress == null)
			throw new P_INVALID_ADDRESS("Error in orginal destination Address");
		if (originatingAddress == null)
			throw new P_INVALID_ADDRESS("Error in orginanating Address");
		if (redirectingAddress == null)
			throw new P_INVALID_ADDRESS("Error in redirecting Address");

		if (nWatcherID == 0) {
			// register for event notifications
			m_logger.debug("About to register watcher");
			EventCriteria evCriteria = new EventCriteria();
			m_logger.debug("Event criteria is created");
			evCriteria.addCriteria(CallEvent.eventRouteRes);
			evCriteria.addCriteria(CallEvent.eventRouteErr);
			m_logger.debug("Finished adding criteria");
			nWatcherID = EventObserver.getInstance().addWatcher(this,
					evCriteria);
			m_logger.debug("Finished registering watcher");
		}
		CallEventQueue queue = CallEventQueue.getInstance();
		CallEvent evtCall = new CallEvent(callSessionID, targetAddress,
				originatingAddress, CallEvent.eventRouteReq, 0, 0,originatingAddress,
			 originalDestinationAddress, redirectingAddress ,appInfo);
		queue.put(evtCall);

		// event_Observer.listen();

		m_logger.debug("Route request successful");
		/**
		 * Returns callLegSessionID: Specifies the sessionID assigned by the
		 * gateway. This is the sessionID of the implicitly created call leg.
		 * The same ID will be returned in the routeRes or Err. This allows the
		 * application to correlate the request and the result.
		 */
		return 0;

		// 
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
		// m_m_logger.error("Catch exception of P_INVALID_NETWORK_STATE with
		// more
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

		if (m_logger.isInfoEnabled())
			m_logger.info("release Call");

		CallEventQueue queue = CallEventQueue.getInstance();
		CallEvent evtCall = new CallEvent(callSessionID, null, null,
				CallEvent.eventReleaseCall, 0, 0,null,null,null,null);
		queue.put(evtCall);
		EventObserver.getInstance().removeWatcher(nWatcherID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#deassignCall(int)
	 */
	public void deassignCall(int callSessionID) throws TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub

		if (m_logger.isInfoEnabled())
			m_logger.info("deassign Call");

		CallEventQueue queue = CallEventQueue.getInstance();
		CallEvent evtCall = new CallEvent(callSessionID, null, null,
				CallEvent.eventDeassignCall, 0, 0,null,null,null,null);
		queue.put(evtCall);
		EventObserver.getInstance().removeWatcher(nWatcherID);
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
		m_logger.debug("A new call back interface is set for call " + sessionID);
		appCall = IpAppCallHelper.narrow(appInterface);
		m_logger.info("  Succesfully changed call back interface"); 
	}
	
	public void onEvent(int eventID, CallEvent eventData) {
		// TODO Auto-generated method stub

	}

	public void onRouteReq(int callSessionID, TpAddress targetAddr,
			TpAddress origAddr) {
		// TODO Auto-generated method stub

	}

	public void onDeassignCall(int callSessionID) {
		// TODO Auto-generated method stub

	}

	public void onReleaseCall(int callSessionID) {
		// TODO Auto-generated method stub

	}

	public void onRouteRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		m_logger.info("Get result of previous request routeReq");
		m_logger.debug("IpAppCall: " + appCall);
		m_logger.debug("callsessionID: " + callSessionID);
		m_logger.debug("eventReport: " + eventReport);
		appCall.routeRes(callSessionID, eventReport, callLegSessionID);
		m_logger.info("Finish forwarding that event to client");
	}
}
