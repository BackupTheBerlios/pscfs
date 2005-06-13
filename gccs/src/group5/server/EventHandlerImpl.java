//$Id: EventHandlerImpl.java,v 1.1 2005/06/13 12:31:26 huuhoa Exp $
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
			onRouteReq(ev.targetAddress, ev.originatingAddress);
			break;
		case CallEvent.eventDeassignCall:
			onDeassignCall();
			break;
		case CallEvent.eventReleaseCall:
			onReleaseCall();
			break;
		default:
			break;
		}

	}

	protected void onRouteReq(TpAddress targetAddr, TpAddress origAddr) {

	}

	protected void onDeassignCall() {

	}

	protected void onReleaseCall() {

	}
}
