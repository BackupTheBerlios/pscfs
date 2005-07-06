//$Id: MyAppEvent.java,v 1.1 2005/07/06 18:19:53 huuhoa Exp $
/**
 * 
 */
package group5.client.appinitcall;

import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;

/**
 * MyAppEvent is a class representing an event. It has 3 public attributes:
 * callID (call object reference + its descriptor), event details, and so?
 * called assignmentID (integer).
 * 
 * @author Nguyen Huu Hoa
 * 
 */
public class MyAppEvent {
	public TpCallIdentifier callId;

	public TpCallEventInfo eventInfo;

	public int assignmentID;

	MyAppEvent(TpCallIdentifier cid, TpCallEventInfo ei, int a) {
		callId = cid;
		eventInfo = ei;
		assignmentID = a;
	}
}
