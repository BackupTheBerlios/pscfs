//$Id: CallSimulator.java,v 1.18 2005/07/29 00:32:43 huuhoa Exp $
/**
 * 
 */
package group5.server;

import org.apache.log4j.Logger;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddress;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallMonitorMode;
import org.csapi.cc.gccs.P_EVENT_GCCS_ADDRESS_ANALYSED_EVENT;
import org.csapi.cc.gccs.TpCallAdditionalReportInfo;
import org.csapi.cc.gccs.TpCallAppInfo;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallNotificationType;
import org.csapi.cc.gccs.TpCallReleaseCause;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportRequest;
import org.csapi.cc.gccs.TpCallReportType;

/**
 * Simulation the call between 2 terminals.
 * <ol>
 * <li>Routing call between 2 parties
 * <li>Create/Release calls
 * <li>Report status of subscribers
 * </ol>
 * 
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class CallSimulator {
	private static Logger m_logger = Logger.getLogger(CallSimulator.class);

	private static CallSimulator m_instance = null;

	public static CallSimulator getInstance() {
		if (m_instance == null) {
			m_instance = new CallSimulator();
		}
		return m_instance;
	}

	private IpCallControlManagerImpl ipCCManager;

	public CallSimulator() {
		ipCCManager = null;
	}

	public void registerCallControlManager(IpCallControlManagerImpl ipManager) {
		ipCCManager = ipManager;
	}

	public boolean startSimulator() {
		m_logger.info("starting the call simulator");
		return true;
	}

	public boolean stopSimulator() {
		m_logger.info("stopping the call simulator");
		return true;
	}

	/**
	 * @see group5.server.EventHandlerImpl#onDeassignCall(int)
	 */
	public void deassignCall(int callSessionID) throws P_INVALID_SESSION_ID {
		if (ipCCManager == null) {
			m_logger.fatal("Register call control manager first");
			return;
		}
		CallInfo ci = ipCCManager.getCallInfo(callSessionID);
		// put deassign event to event pool
		CallEventQueue queue = CallEventQueue.getInstance();
		CallEvent evtCall = new CallEvent(ci.getSessionID(),
				CallEvent.eventDeassignCall);
		queue.put(evtCall);
		ipCCManager.onDeassignCall(callSessionID);
	}

	/**
	 * release a call, identified by callSessionID
	 * 
	 * @see group5.server.EventHandlerImpl#onReleaseCall(int)
	 */
	public void releaseCall(int callSessionID) throws P_INVALID_SESSION_ID {
		if (ipCCManager == null) {
			m_logger.fatal("Register call control manager first");
			return;
		}
		CallInfo ci = ipCCManager.getCallInfo(callSessionID);
		m_logger.info("Releasing call between ["
				+ ci.getCallEventInfo().OriginatingAddress.AddrString
				+ "] and ["
				+ ci.getCallEventInfo().DestinationAddress.AddrString + "]");
		Subscribers subDB = Subscribers.getInstance();
		Subscriber subOrig = subDB
				.getSubscriber(ci.getCallEventInfo().OriginatingAddress.AddrString);
		subOrig.endCall();
		Subscriber subTarg = subDB
				.getSubscriber(ci.getCallEventInfo().DestinationAddress.AddrString);
		subTarg.endCall();
		// put end call event to event pool
		CallEventQueue queue = CallEventQueue.getInstance();
		CallEvent evtCall = new CallEvent(callSessionID,
				CallEvent.eventReleaseCall);
		queue.put(evtCall);
		ipCCManager.onReleaseCall(callSessionID);
	}

	private boolean bCallRedirect = false;

	private synchronized boolean isCallRedirect() {
		return bCallRedirect;
	}

	private synchronized void setCallRedirect() {
		bCallRedirect = true;
		m_logger.debug("Notify all about the call redirection");
		notifyAll();

		// block until other thread finish routing old request
		try {
			wait();
		} catch (InterruptedException ex) {
			m_logger.error(ex);
		}
}

	private synchronized boolean waitForAnotherRouteReq() {
		boolean bResult = isCallRedirect();
		m_logger.debug("Enter wait with bResult=" + bResult);
		if (bResult)
			return true;
		try {
			wait(1000);
		} catch (InterruptedException ex) {
			m_logger.error(ex);
		}
		bResult = isCallRedirect();
		m_logger.debug("Finish wait with bResult=" + bResult);
		bCallRedirect = false;
		return bResult;
	}

	private synchronized void resumeThread() {
		notifyAll();
	}
	/**
	 * @see group5.server.EventHandlerImpl#onRouteReq(int, org.csapi.TpAddress,
	 *      org.csapi.TpAddress)
	 */
	public void routeReq(int callSessionID,
			TpCallReportRequest[] responseRequested, TpAddress targetAddress,
			TpAddress originatingAddress, TpAddress originalDestinationAddress,
			TpAddress redirectingAddress, TpCallAppInfo[] appInfo)
			throws P_INVALID_EVENT_TYPE, P_INVALID_NETWORK_STATE,
			TpCommonExceptions, P_INVALID_ADDRESS, P_INVALID_SESSION_ID,
			P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_CRITERIA {
		// TODO if targetAddress is different to originalDestinationAddress
		// then it is the result of another routeReq to indirect the call
		// must not inform CallControlManager anymore!
		m_logger.info("receive routeReq event with callSessionID: "
				+ callSessionID);
		m_logger.info("source: " + originatingAddress.AddrString + ", dest: "
				+ targetAddress.AddrString + ", origDest: "
				+ originalDestinationAddress.AddrString);
		// perform requesting for routing
		CallEventQueue queue = CallEventQueue.getInstance();
		CallEvent evtCall = new CallEvent(callSessionID, targetAddress,
				originatingAddress, CallEvent.eventRouteReq, 0, 0,
				originatingAddress, originalDestinationAddress,
				redirectingAddress, appInfo);
		queue.put(evtCall);
		if (ipCCManager == null)
			throw new P_INVALID_NETWORK_STATE("Invalid network state");
		CallInfo ci = ipCCManager.getCallInfo(callSessionID);
		TpCallEventInfo cei = ci.getCallEventInfo();
		cei.CallAppInfo = appInfo;
		cei.CallEventName = P_EVENT_GCCS_ADDRESS_ANALYSED_EVENT.value;
		cei.CallNotificationType = TpCallNotificationType.P_ORIGINATING;
		cei.DestinationAddress = targetAddress;
		cei.MonitorMode = TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT;
		cei.OriginalDestinationAddress = originalDestinationAddress;
		cei.OriginatingAddress = originatingAddress;
		cei.RedirectingAddress = redirectingAddress;
		ci.setCallEventInfo(cei);
		ipCCManager.updateCallInfo(callSessionID, ci);

		boolean bRedirect = false;
		if (targetAddress.AddrString
				.compareToIgnoreCase(originalDestinationAddress.AddrString) == 0) {
			ipCCManager.onRouteReq(callSessionID);
			m_logger.info("Wait for routeReq for a while");
			// redirect call
			bRedirect = waitForAnotherRouteReq();
			m_logger.debug("Finished waiting");
		}
		else
		{
			m_logger.debug("Received redirect call");
			setCallRedirect();
			m_logger.debug("Resume process");
		}
		if (bRedirect) {
			CallEvent evRouteErr = new CallEvent(callSessionID,
					CallEvent.eventRouteRes);
			evRouteErr.eventReport = new TpCallReport();
			evRouteErr.eventReport.AdditionalReportInfo = new TpCallAdditionalReportInfo();
			evRouteErr.eventReport.AdditionalReportInfo
					.Busy(new TpCallReleaseCause(0, 1));
			evRouteErr.eventReport.CallEventTime = "10";
			evRouteErr.eventReport.MonitorMode = TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT;
			// get an instance of subscribers

			// no subscriber
			m_logger.error("Cannot find any subscriber with address: "
					+ targetAddress.AddrString);
			evRouteErr.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_REDIRECTED;
			CallEventQueue.getInstance().put(evRouteErr);

			m_logger.debug("redirect call");
			resumeThread();
			return;
		}

		// returning the result

		CallEvent evRouteRes = new CallEvent(callSessionID,
				CallEvent.eventRouteRes);
		evRouteRes.eventReport = new TpCallReport();
		evRouteRes.eventReport.AdditionalReportInfo = new TpCallAdditionalReportInfo();
		evRouteRes.eventReport.AdditionalReportInfo
				.Busy(new TpCallReleaseCause(0, 1));
		evRouteRes.eventReport.CallEventTime = "10";
		evRouteRes.eventReport.MonitorMode = TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT;
		// get an instance of subscribers
		Subscribers subColl = Subscribers.getInstance();
		// m_logger.debug(subColl);
		// get subscriber pair
		Subscriber subTarg = subColl.getSubscriber(targetAddress.AddrString);
		if (subTarg == null) {
			// no subscriber
			m_logger.error("Cannot find any subscriber with address: "
					+ targetAddress.AddrString);
			evRouteRes.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_ROUTING_FAILURE;
			CallEventQueue.getInstance().put(evRouteRes);
			return;
		}

		m_logger.debug("Destination partner is: "
				+ subTarg.getSubscribeAddress());
		m_logger.debug("Status of subscriber: " + subTarg.getStatusDescription());

		if ((subTarg.getStatus() & Subscriber.Idle) == 0) {
			// subscriber is not idle, can not make call
			// evRouteRes.errorIndication.ErrorType = TpCallErrorType;
			m_logger.error("Destination partner is busying");
			evRouteRes.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_BUSY;
			if ((subTarg.getStatus() & Subscriber.Busy) != 0)
				evRouteRes.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_BUSY;
			if ((subTarg.getStatus() & Subscriber.Unreachable) != 0)
				evRouteRes.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_NOT_REACHABLE;
			CallEventQueue.getInstance().put(evRouteRes);
			return;
		}

		// making call
		subTarg.receiveCallFrom(originatingAddress.AddrString);
		// making call succeeded
		m_logger.info("Successfully routing call");
		evRouteRes.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_ANSWER;
		CallEventQueue.getInstance().put(evRouteRes);
		m_logger.debug("going out of onRouteReq of CallSimulator");
		m_logger.info("RouteReq sucessfully");

	}
}
