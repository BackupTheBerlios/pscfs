//$Id: CallEvent.java,v 1.12 2005/07/06 18:19:53 huuhoa Exp $
package group5.server;

import org.csapi.TpAddress;
import org.csapi.cc.gccs.TpCallReport;

/**
 * @author Nguyen Duc Du Khuong
 * 
 */
public class CallEvent {
	/**
	 * 
	 */
	public int CallSessionID;

	private TpAddress targetAddress;

	public TpAddress originatingAddress;

	/**
	 * There are 3 types of events:
	 * <ol>
	 * <li> <b>eventRouteReq</b>: Route request
	 * <li> <b>eventDeassignCall</b>: Deassign call
	 * <li> <b>eventReleaseCall</b>: Release call
	 * </ol>
	 */
	public int eventType;

	public static final int eventRouteReq = 1;

	public static final int eventDeassignCall = 2;

	public static final int eventReleaseCall = 3;

	/**
	 * There are 2 types of events of Routerequest's results: routeRes and
	 * <ol>
	 * <li><b>eventRouteRes</b>: The request to route the call to the
	 * destination was successful,
	 * <li> <b>eventRouteErr</b>: The request to route the call to the
	 * destination party was unsuccessful
	 * </ol>
	 */

	public int eventType_Result;

	public static final int eventRouteRes = 4;

	public static final int eventRouteErr = 5;

	/**
	 * Set if the event need to be parsed by CallControlManager. bProvision is
	 * set by default. CallControlManager will be the only one who examines the
	 * content of events, then forwards events to AppCallControlManager to have
	 * it make changes if needed. If there is any changes, CallControlManager
	 * will put the event back to queue with bProvision unset. Event with
	 * bProvision unset will be dispatched to other observers not the
	 * CallControlManager.
	 */
	private boolean bProvision;

	/**
	 * Specifies the result of the request to route the call to the destination
	 * party. It also includes the network event, date and time, monitoring mode
	 * and event specific information such as release cause
	 */
	public TpCallReport eventReport;

	/**
	 * Specifies the sessionID of the associated call leg. This corresponds to
	 * the sessionID returned at the routeReq() and can be used to correlate the
	 * response with the request.
	 */
	public int callLegSessionID;

	/**
	 * Specifies the error which led to the original request failing.
	 */
	// public TpCallError errorIndication;
	public CallEvent(int CallSession_ID, int eventType) 
	throws org.omg.CORBA.BAD_PARAM {
		System.out.println("Entering constructor of CallEvent");
		bProvision = true;
		CallSessionID = CallSession_ID;
		setTargetAddress(null);
		originatingAddress = null;
		eventType_Result = 0;
		callLegSessionID = 0;
		switch (eventType) {
		case eventRouteReq:
		case eventDeassignCall:
		case eventReleaseCall:
		case eventRouteRes:
		case eventRouteErr:
			this.eventType = eventType;
			break;
		default:
			throw new org.omg.CORBA.BAD_PARAM();
		}
		System.out.println("Getting out of constructor");
	}

	/**
	 * 
	 * @throws org.omg.CORBA.BAD_PARAM
	 *             throw when eventType is not valid
	 */
	CallEvent(int CallSession_ID, TpAddress targetAddr, TpAddress origAddr,
			int event_Type, int event_Type_Result, int callLegsssion_ID)
			throws org.omg.CORBA.BAD_PARAM {
		bProvision = true;
		CallSessionID = CallSession_ID;
		setTargetAddress(targetAddr);
		originatingAddress = origAddr;
		eventType_Result = event_Type_Result;
		callLegSessionID = callLegsssion_ID;

		switch (event_Type) {
		case eventRouteReq:
		case eventDeassignCall:
		case eventReleaseCall:
		case eventRouteRes:
		case eventRouteErr:
			this.eventType = event_Type;
			break;
		default:
			throw new org.omg.CORBA.BAD_PARAM();
		}
	}

	/**
	 * @param bProvision
	 *            The bProvision to set.
	 */
	public void setProvision(boolean bProvision) {
		this.bProvision = bProvision;
	}

	/**
	 * @return Returns the bProvision.
	 */
	public boolean isProvision() {
		return bProvision;
	}

	/**
	 * @param targetAddress The targetAddress to set.
	 */
	public void setTargetAddress(TpAddress targetAddress) {
		this.targetAddress = targetAddress;
	}

	/**
	 * @return Returns the targetAddress.
	 */
	public TpAddress getTargetAddress() {
		return targetAddress;
	}
}
