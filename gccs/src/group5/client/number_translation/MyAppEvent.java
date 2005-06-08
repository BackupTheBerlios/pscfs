/**
 * 
 */
package group5.client.number_translation;

import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;

/**
 * MyAppEvent is a class representing an event. It has 3 public
 * attributes: callID (call object reference + its descriptor),
 * event details, and so? called assignmentID (integer).
 * @author Nguyen Huu Hoa
 *
 */
public class MyAppEvent {
	public TpCallIdentifier callId;
	public TpCallEventInfo eventInfo;
	public int assignmentID;

	MyAppEvent ( TpCallIdentifier cid,
		TpCallEventInfo ei, int a ) {
		callId = cid;
		eventInfo = ei;
		assignmentID = a;
	}
}
