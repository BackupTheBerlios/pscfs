//$Id: CallControlAdapter.java,v 1.3 2005/07/10 16:31:44 hoanghaiham Exp $
package group5.server;

import org.csapi.TpAddress;

public interface CallControlAdapter {

	/**
	 * This function will be called automatically whenever an event with bProvision=true.
	 * Event will be further processed and this function return a boolean value,
	 * indicating whether there are changes in the event content or not.
	 * @return <b>true</b> means the content of event has been changed, and the changes
	 * have been put back to the event queue, the observer should 
	 * discard the event and wait for new event to process.<br>
	 * <b>false</b> means there's no change at all. Observer can further process this event.
	 */
	public abstract boolean onEvent(int eventID, CallEvent eventData);

	public abstract boolean onRouteReq(int callSessionID, TpAddress targetAddr,
			TpAddress origAddr,CallEvent eventData);

	public abstract boolean onDeassignCall(int callSessionID);

	public abstract boolean onReleaseCall(int callSessionID);

}