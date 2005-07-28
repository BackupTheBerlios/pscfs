//$Id: ApplicationEvent.java,v 1.5 2005/07/28 23:45:22 aachenner Exp $
/**
 * 
 */
package group5.client;

import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallReport;

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
	public TpCallReport eventReport;

	public ApplicationEvent(int evType, TpCallIdentifier cid,
			TpCallEventInfo ei, int a) {
		callId = cid;
		eventInfo = ei;
		assignmentID = a;
		eventType = evType;
	}
}
