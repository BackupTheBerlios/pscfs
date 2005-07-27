//$Id: CallInfo.java,v 1.2 2005/07/27 08:33:11 huuhoa Exp $
package group5.server;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.csapi.cc.gccs.IpCall;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;

/**
 * Store information about a call at server side: - Call partners - Call start
 * time - Call session id - Call originating, destination, divert
 * 
 * @author Nguyen Huu Hoa
 * 
 */
public class CallInfo {
	private TpCallEventInfo callEventInfo;

	private long callStartTime;

	private int callSessionID;

	public IpCall CallRefence;

	public IpCallImpl CallObject;

	public CallInfo(int callSessionID) {
		this.callSessionID = callSessionID;
		Calendar cal = new GregorianCalendar();
		callStartTime = cal.getTimeInMillis();
		callEventInfo = new TpCallEventInfo();
		CallRefence = null;
		CallObject = null;
	}

	public void setCallEventInfo(TpCallEventInfo evtInfo) {
		callEventInfo = evtInfo;
	}

	public TpCallEventInfo getCallEventInfo() {
		return callEventInfo;
	}

	public int getSessionID() {
		return callSessionID;
	}

	public long getStartTime() {
		return callStartTime;
	}

	public TpCallIdentifier getCallIdentifier() {
		TpCallIdentifier ci = new TpCallIdentifier();
		ci.CallReference = CallRefence;
		ci.CallSessionID = callSessionID;
		return ci;
	}
}
