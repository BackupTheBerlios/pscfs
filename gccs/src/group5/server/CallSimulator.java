//$Id: CallSimulator.java,v 1.1 2005/06/13 12:31:26 huuhoa Exp $
/**
 * 
 */
package group5.server;

import org.csapi.TpAddress;

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
	 * @see group5.server.EventHandlerImpl#onDeassignCall()
	 */
	protected void onDeassignCall() {
		// TODO Auto-generated method stub
		super.onDeassignCall();
	}

	/**
	 * @see group5.server.EventHandlerImpl#onReleaseCall()
	 */
	protected void onReleaseCall() {
		// TODO Auto-generated method stub
		super.onReleaseCall();
	}

	/**
	 * @see group5.server.EventHandlerImpl#onRouteReq(org.csapi.TpAddress, org.csapi.TpAddress)
	 */
	protected void onRouteReq(TpAddress targetAddr, TpAddress origAddr) {
		// TODO Auto-generated method stub
		super.onRouteReq(targetAddr, origAddr);
	}
	
}
