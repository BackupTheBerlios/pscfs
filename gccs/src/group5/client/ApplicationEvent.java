//$Id: ApplicationEvent.java,v 1.1 2005/07/09 10:28:46 hoanghaiham Exp $
/**
 * 
 */
package group5.client;

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
public class ApplicationEvent {
	public TpCallIdentifier callId;

	public TpCallEventInfo eventInfo;

	public int assignmentID;

	public ApplicationEvent(TpCallIdentifier cid, TpCallEventInfo ei, int a) {
		callId = cid;
		eventInfo = ei;
		assignmentID = a;
	}
}
