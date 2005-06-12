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
	public int eventType;
	public static final int eventRouteReq = 1;
	public static final int eventDeassignCall = 2;
	public static final int eventReleaseCall = 3;
	public int eventID;

	/**
	 * 
	 * @throws org.omg.CORBA.BAD_PARAM throw when eventType is not valid
	 */
	CallEvent ( int CallSession_ID, TpAddress targetAddr, TpAddress origAddr, int eventType,
		int eventID ) throws org.omg.CORBA.BAD_PARAM {
		CallSessionID = CallSession_ID;
		targetAddress = targetAddr;
		originatingAddress = origAddr;
		switch (eventType)
		{
		case eventRouteReq:
		case eventDeassignCall:
		case eventReleaseCall:
			this.eventType = eventType;
			break;
		default:
			throw new org.omg.CORBA.BAD_PARAM();
		}
		this.eventID = eventID;
	}
}
