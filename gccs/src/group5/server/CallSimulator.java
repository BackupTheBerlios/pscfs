//$Id: CallSimulator.java,v 1.3 2005/06/14 18:24:55 huuhoa Exp $
/**
 * 
 */
package group5.server;

import org.csapi.TpAddress;
import org.csapi.cc.TpCallError;
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
public class CallSimulator extends EventHandlerImpl {

	/**
	 * @see group5.server.EventHandlerImpl#onDeassignCall(int)
	 */
	protected void onDeassignCall(int callSessionID) {

	}

	/**
	 * release a call, identified by callSessionID
	 * 
	 * @see group5.server.EventHandlerImpl#onReleaseCall(int)
	 */
	protected void onReleaseCall(int callSessionID) {

	}

	/**
	 * @see group5.server.EventHandlerImpl#onRouteReq(int, org.csapi.TpAddress,
	 *      org.csapi.TpAddress)
	 */
	protected void onRouteReq(int callSessionID, TpAddress targetAddr,
			TpAddress origAddr) {
		CallEvent evRouteRes = new CallEvent(callSessionID,
				CallEvent.eventRouteRes);
		// get an instance of subscribers
		Subscribers subColl = Subscribers.getInstance();
		// get subscriber pair
		Subscriber subTarg = subColl.getSubscriber(targetAddr.AddrString);
		if (subTarg == null) {
			// no subscriber
			evRouteRes.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_NOT_REACHABLE;
			CallEventQueue.getInstance().put(evRouteRes);
			return;
		}

		if ((subTarg.getStatus() & Subscriber.Idle) == 0) {
			// subscriber is not idle, can not make call
			evRouteRes.errorIndication = new TpCallError();
			// evRouteRes.errorIndication.ErrorType = TpCallErrorType;
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
		// TODO Add appropriated indicator here
		evRouteRes.eventReport.CallReportType = TpCallReportType.P_CALL_REPORT_ANSWER;
		CallEventQueue.getInstance().put(evRouteRes);
	}

}
