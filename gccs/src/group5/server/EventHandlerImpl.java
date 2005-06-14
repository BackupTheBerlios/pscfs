//$Id: EventHandlerImpl.java,v 1.2 2005/06/14 08:15:31 huuhoa Exp $
/**
 * 
 */
package group5.server;

import org.csapi.TpAddress;

/**
 * the base class for all event handlers. This class will handle event
 * notification from observer, then analyze it, finally call appropriated
 * handlers
 * 
 * @author Nguyen Huu Hoa
 * 
 */
public class EventHandlerImpl implements IpEventHandler {

	public void onEvent(int eventID, Object eventData) {
		// get data
		CallEvent ev = (CallEvent) eventData;
		switch (eventID) {
		case CallEvent.eventRouteReq:
			// Event route request
			onRouteReq(ev.CallSessionID, ev.targetAddress, ev.originatingAddress);
			break;
		case CallEvent.eventDeassignCall:
			onDeassignCall(ev.CallSessionID);
			break;
		case CallEvent.eventReleaseCall:
			onReleaseCall(ev.CallSessionID);
			break;
		default:
			break;
		}

	}

	protected void onRouteReq(int callSessionID, TpAddress targetAddr, TpAddress origAddr) {

	}

	protected void onDeassignCall(int callSessionID) {

	}

	protected void onReleaseCall(int callSessionID) {

	}
}
