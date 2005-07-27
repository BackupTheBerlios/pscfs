//$Id: ApplicationEvent.java,v 1.4 2005/07/27 08:47:09 huuhoa Exp $
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
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class ApplicationEvent {
	public TpCallIdentifier callId;

	public TpCallEventInfo eventInfo;

	public int assignmentID;

	public int eventType;

	public static int evCallEventNotify = 1;

	public static int evRouteRes = 2;

	public ApplicationEvent(int evType, TpCallIdentifier cid,
			TpCallEventInfo ei, int a) {
		callId = cid;
		eventInfo = ei;
		assignmentID = a;
		eventType = evType;
	}
}
