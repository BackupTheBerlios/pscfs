//$Id: IpCallImpl.java,v 1.31 2005/07/28 23:45:22 aachenner Exp $
/**
 * 
 */
package group5.server;

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
import org.csapi.cc.gccs.IpAppCallHelper;
import org.csapi.cc.gccs.IpCallPOA;
import org.csapi.cc.gccs.TpCallAppInfo;
import org.csapi.cc.gccs.TpCallReleaseCause;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportRequest;
import org.csapi.cc.gccs.TpCallReportType;

/**
 * Represent each call session by IpCallImpl object
 * 
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
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

	/**
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
		m_logger.info("Got route request");
		if (targetAddress == null)
			throw new P_INVALID_ADDRESS("Error in the target address");
		if (originalDestinationAddress == null)
			throw new P_INVALID_ADDRESS("Error in orginal destination Address");
		if (originatingAddress == null)
			throw new P_INVALID_ADDRESS("Error in orginanating Address");
		if (redirectingAddress == null)
			throw new P_INVALID_ADDRESS("Error in redirecting Address");

		if (appCall == null)
			throw new TpCommonExceptions("P_NO_CALLBACK_ADDRESS_SET", 1,
					"Should set callback first");
		if (nWatcherID == 0) {
			// register for event notifications
			EventCriteria evCriteria = new EventCriteria();
			evCriteria.addCriteria(CallEvent.eventRouteRes);
			evCriteria.addCriteria(CallEvent.eventRouteErr);
			nWatcherID = EventObserver.getInstance().addWatcher(this,
					evCriteria);
		}
		CallSimulator.getInstance().routeReq(callSessionID, responseRequested,
				targetAddress, originatingAddress, originalDestinationAddress,
				redirectingAddress, appInfo);
		// event_Observer.listen();

		m_logger.debug("Route request successful");
		return 0;

	}

	/**
	 * 
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#release(int,
	 *      org.csapi.cc.gccs.TpCallReleaseCause)
	 */
	public void release(int callSessionID, TpCallReleaseCause cause)
			throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		m_logger.info("receive request to releaseCall");
		CallSimulator.getInstance().releaseCall(callSessionID);
		EventObserver.getInstance().removeWatcher(nWatcherID);
	}

	/**
	 * @see org.csapi.cc.gccs.IpCallOperations#deassignCall(int)
	 */
	public void deassignCall(int callSessionID) throws TpCommonExceptions,
			P_INVALID_SESSION_ID {
		m_logger.info("receive request to deassignCall");
		CallSimulator.getInstance().deassignCall(callSessionID);
		EventObserver.getInstance().removeWatcher(nWatcherID);
	}

	/**
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#getCallInfoReq(int, int)
	 */
	public void getCallInfoReq(int callSessionID, int callInfoRequested)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		m_logger.debug("getCallInfoReq - Unimplemented");
	}

	/**
	 * 
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#setCallChargePlan(int,
	 *      org.csapi.cc.TpCallChargePlan)
	 */
	public void setCallChargePlan(int callSessionID,
			TpCallChargePlan callChargePlan) throws TpCommonExceptions,
			P_INVALID_SESSION_ID {
		m_logger.debug("setCallChargePlan - Unimplemented");
	}

	/**
	 * 
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#setAdviceOfCharge(int,
	 *      org.csapi.TpAoCInfo, int)
	 */
	public void setAdviceOfCharge(int callSessionID, TpAoCInfo aOCInfo,
			int tariffSwitch) throws TpCommonExceptions, P_INVALID_SESSION_ID {
		m_logger.debug("setAdviceOfCharge - Unimplemented");
	}

	/**
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#getMoreDialledDigitsReq(int, int)
	 */
	public void getMoreDialledDigitsReq(int callSessionID, int length)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		m_logger.debug("getMoreDialledDigitsReq - Unimplemented");
	}

	/**
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#superviseCallReq(int, int, int)
	 */
	public void superviseCallReq(int callSessionID, int time, int treatment)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		m_logger.debug("superviseCallReq - Unimplemented");

	}

	/**
	 * 
	 * @see org.csapi.cc.gccs.IpCallOperations#continueProcessing(int)
	 */
	public void continueProcessing(int callSessionID)
			throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		m_logger.debug("continueProcessing - Unimplemented");

	}

	/***************************************************************************
	 * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
	 */
	public void setCallback(IpInterface appInterface)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
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
		m_logger
				.debug("A new call back interface is set for call " + sessionID);
		appCall = IpAppCallHelper.narrow(appInterface);
		m_logger.info("  Succesfully changed call back interface");
	}

	public void onEvent(int eventID, CallEvent eventData) {

	}

	public void onRouteReq(int callSessionID, TpAddress targetAddr,
			TpAddress origAddr) {

	}

	public void onDeassignCall(int callSessionID) {

	}

	public void onReleaseCall(int callSessionID) {

	}

	public void onRouteRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		m_logger.info("Get result of previous request routeReq");
		m_logger.debug("callsessionID: " + callSessionID);
		String msgError;
		switch (eventReport.CallReportType.value()){
			case TpCallReportType._P_CALL_REPORT_UNDEFINED: msgError="Call report undefine"; break;
			case TpCallReportType._P_CALL_REPORT_PROGRESS: msgError="Call report progress"; break;
			case TpCallReportType._P_CALL_REPORT_ALERTING: msgError="Call report alerting"; break;
			case TpCallReportType._P_CALL_REPORT_ANSWER: msgError="Destination partner answer the call"; break;
			case TpCallReportType._P_CALL_REPORT_BUSY: msgError="Destination partner is busy"; break;
			case TpCallReportType._P_CALL_REPORT_NO_ANSWER: msgError="Destination partner not anwser the call"; break;
			case TpCallReportType._P_CALL_REPORT_DISCONNECT: msgError="Call report disconnect"; break;
			case TpCallReportType._P_CALL_REPORT_REDIRECTED: msgError="Call report redirected"; break;
			case TpCallReportType._P_CALL_REPORT_SERVICE_CODE: msgError="Call report service code"; break;
			case TpCallReportType._P_CALL_REPORT_ROUTING_FAILURE: msgError="Call report undefine"; break;
			case TpCallReportType._P_CALL_REPORT_QUEUED: msgError="Call report queue"; break;
			case TpCallReportType._P_CALL_REPORT_NOT_REACHABLE: msgError="Destination partner is not reachable"; break;
			default: msgError="Call report undefine"; break;
		
		}
		m_logger.debug(msgError);
		if (appCall != null)
			appCall.routeRes(callSessionID, eventReport, callLegSessionID);
		m_logger.debug("Finish forwarding that event to client");
	}
}
