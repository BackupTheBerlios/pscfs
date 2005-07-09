//$Id: CallSimulator.java,v 1.8 2005/07/09 09:23:22 huuhoa Exp $
/**
 * 
 */
package group5.server;

import org.apache.log4j.Logger;
import org.csapi.TpAddress;
import org.csapi.cc.TpCallMonitorMode;
import org.csapi.cc.gccs.TpCallAdditionalReportInfo;
import org.csapi.cc.gccs.TpCallReleaseCause;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportType;

/**
 * Simulation the call between 2 terminals.
 * <ol>
 * <li>Routing call between 2 parties
 * <li>Create/Release calls
 * <li>Report status of subscribers
 * </ol>
 * 
 * @author Nguyen Huu Hoa
 * 
 */
public class CallSimulator implements IpEventHandler {
	private static Logger m_logger = Logger.getLogger(CallSimulator.class);

	private int nWatcherID;
	public CallSimulator()
	{
		nWatcherID = 0;
	}
	public boolean startSimulator()
	{
		m_logger.info("starting the call simulator");
		// register for event notifications
		m_logger.debug("About to register watcher for call simulator");
		EventCriteria evCriteria = new EventCriteria();
		m_logger.debug("Event criteria is created");
		evCriteria.addCriteria(CallEvent.eventRouteReq);
		evCriteria.addCriteria(CallEvent.eventDeassignCall);
		evCriteria.addCriteria(CallEvent.eventReleaseCall);
		m_logger.debug("Finished adding criteria");
		nWatcherID = EventObserver.getInstance().addWatcher(this, evCriteria);
		m_logger.debug("Finished registering watcher for call simulator");
		
		return true;
	}
	public boolean stopSimulator()
	{
		m_logger.info("stopping the call simulator");
		EventObserver.getInstance().removeWatcher(nWatcherID);
		return true;
	}
	/**
	 * @see group5.server.EventHandlerImpl#onDeassignCall(int)
	 */
	public void onDeassignCall(int callSessionID) {

	}

	/**
	 * release a call, identified by callSessionID
	 * 
	 * @see group5.server.EventHandlerImpl#onReleaseCall(int)
	 */
	public void onReleaseCall(int callSessionID) {

	}

	/**
	 * @see group5.server.EventHandlerImpl#onRouteReq(int, org.csapi.TpAddress,
	 *      org.csapi.TpAddress)
	 */
	public void onRouteReq(int callSessionID, TpAddress targetAddr,
			TpAddress origAddr) {
		m_logger.info("receive routeReq event with callSessionID: " + callSessionID);
		CallEvent evRouteRes = new CallEvent(callSessionID,
				CallEvent.eventRouteRes);
		evRouteRes.eventReport = new TpCallReport();
		evRouteRes.eventReport.AdditionalReportInfo = new TpCallAdditionalReportInfo();
		evRouteRes.eventReport.AdditionalReportInfo.Busy(new TpCallReleaseCause(0, 1));
		evRouteRes.eventReport.CallEventTime = "10";
		evRouteRes.eventReport.MonitorMode = TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT;
		// get an instance of subscribers
		Subscribers subColl = Subscribers.getInstance();
		m_logger.debug(subColl);
		// get subscriber pair
		Subscriber subTarg = subColl.getSubscriber(targetAddr.AddrString);
		if (subTarg == null) {
			// no subscriber
			m_logger.error("Cannot find any subscriber with address: " + targetAddr.AddrString);
			evRouteRes.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_NOT_REACHABLE;
			CallEventQueue.getInstance().put(evRouteRes);
			return;
		}

		m_logger.debug("destination partner is: " + subTarg.getSubscribeAddress());
		m_logger.debug("Status of subscriber: " + subTarg.getStatus());
		
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
		subTarg.receiveCallFrom(origAddr.AddrString);
		// making call succeeded
		m_logger.info("successfully routing call");
		evRouteRes.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_ANSWER;
		CallEventQueue.getInstance().put(evRouteRes);
		m_logger.debug("going out of onRouteReq of CallSimulator");
	}

	public void onEvent(int eventID, CallEvent eventData) {
		// TODO Auto-generated method stub
		
	}

	public void onRouteRes(int callSessionID, TpCallReport eventReport, int callLegSessionID) {
		// TODO Auto-generated method stub
		
	}

}
