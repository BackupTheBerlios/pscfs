//$Id: CallEvent.java,v 1.4 2005/06/12 22:24:03 huuhoa Exp $
package impl;

import org.csapi.TpAddress;
import org.csapi.cc.TpCallError;
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

	public TpAddress targetAddress;

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
	 * Specifies the result of the request to route the call to the destination 
	 * party. It also includes the network event, date and 
	 * time, monitoring mode and event specific information such as release cause
	 */
	public TpCallReport eventReport;
	
	/**
	 * Specifies the sessionID of the associated call leg. This corresponds 
	 * to the sessionID returned at the routeReq() and can 
	 * be used to correlate the response with the request.
	 */
	public int callLegSessionID;
	
	/**
	 * Specifies the error which led to the original request failing.
	 */
	public TpCallError errorIndication;
	
	/**
	 * 
	 * @throws org.omg.CORBA.BAD_PARAM
	 *             throw when eventType is not valid
	 */
	CallEvent ( int CallSession_ID, TpAddress targetAddr, TpAddress origAddr, 
			int event_Type, TpCallReport event_Report, TpCallError error ,int callLegsssion_ID)
			throws org.omg.CORBA.BAD_PARAM
			{
		
		CallSessionID = CallSession_ID;
		targetAddress = targetAddr;
		originatingAddress = origAddr;
		eventReport = event_Report;
		errorIndication = error;
		callLegSessionID = callLegsssion_ID;
		
		switch (event_Type)
		{
		case eventRouteReq:
		case eventDeassignCall:
		case eventReleaseCall:
			this.eventType = event_Type;
			break;
		default:
			throw new org.omg.CORBA.BAD_PARAM();
		}
	}
}
