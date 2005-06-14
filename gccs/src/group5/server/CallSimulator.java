//$Id: CallSimulator.java,v 1.2 2005/06/14 08:15:31 huuhoa Exp $
/**
 * 
 */
package group5.server;

import org.csapi.TpAddress;
import org.csapi.cc.TpCallError;

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
		// TODO Auto-generated method stub
		super.onDeassignCall(callSessionID);
	}

	/**
	 * @see group5.server.EventHandlerImpl#onReleaseCall(int)
	 */
	protected void onReleaseCall(int callSessionID) {
		// TODO Auto-generated method stub
		super.onReleaseCall(callSessionID);
	}

	/**
	 * @see group5.server.EventHandlerImpl#onRouteReq(int, org.csapi.TpAddress, org.csapi.TpAddress)
	 */
	protected void onRouteReq(int callSessionID, TpAddress targetAddr, TpAddress origAddr) {
		// get an instance of subscribers
		Subscribers subColl = Subscribers.getInstance();
		// get subscriber pair
		Subscriber subOrig = subColl.getSubscriber(origAddr.AddrString);
		Subscriber subTarg = subColl.getSubscriber(targetAddr.AddrString);
		
		CallEvent evRouteRes = new CallEvent(callSessionID, CallEvent.eventRouteRes);
		if ((subOrig.getStatus() & Subscriber.Idle)==0)
		{
			// subscriber is not idle, can not make call
			evRouteRes.errorIndication = new TpCallError();
			//evRouteRes.errorIndication.ErrorType = TpCallErrorType;
			CallEventQueue.getInstance().put(evRouteRes);
			return;
		}
		if ((subTarg.getStatus() & Subscriber.Idle)==0)
		{
			// subscriber is not idle, can not make call
			evRouteRes.errorIndication = new TpCallError();
			//evRouteRes.errorIndication.ErrorType = TpCallErrorType;
			CallEventQueue.getInstance().put(evRouteRes);
			return;
		}
		// making call
		subOrig.makeCallTo(targetAddr.AddrString);
		subTarg.makeCallTo(origAddr.AddrString);
		// making call succeeded
		// TODO Add appropriated indicator here
		CallEventQueue.getInstance().put(evRouteRes);
	}
	
}
