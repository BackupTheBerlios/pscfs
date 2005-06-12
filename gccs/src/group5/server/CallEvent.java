package impl;




import org.csapi.TpAddress;


/**
 * @author Nguyen Duc Du Khuong
 *
 */
public class CallEvent {
	/**
	 * 
	 */
	//public TpCallIdentifier callId;
	//public TpCallEventInfo eventInfo;
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
	public int EventType;
	public static final int eventRouteReq = 1;
	public static final int eventDeassignCall = 2;
	public static final int eventReleaseCall = 3;
	public int EventID;

	CallEvent ( int CallSession_ID, TpAddress targetAddr, TpAddress origAddr, int eventType,
		int eventID ) {
		CallSessionID = CallSession_ID;
		targetAddress = targetAddr;
		originatingAddress = origAddr;
		EventType = eventType;
		EventID = eventID;
	}
}
