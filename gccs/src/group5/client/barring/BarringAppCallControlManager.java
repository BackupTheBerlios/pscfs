//$Id: BarringAppCallControlManager.java,v 1.3 2005/07/27 08:59:42 huuhoa Exp $
package group5.client.barring;

import group5.client.ApplicationFramework;

import org.apache.log4j.Logger;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.IpAppCallControlManagerPOA;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class BarringAppCallControlManager extends IpAppCallControlManagerPOA {
	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(BarringAppCall.class);
	}

	private IpAppCallControlManager ipAppCallControlManager;

	BarringApplicationLogic appLogic;

	public BarringAppCallControlManager(BarringApplicationLogic logic) {
		appLogic = logic;
		ipAppCallControlManager = _this(ApplicationFramework.getORB());
	}

	public void callAborted(int callReference) {
		m_logger.error("Call Aborted");
	}

	IpAppCallControlManager getServant() {
		return ipAppCallControlManager;
	}

	public IpAppCall callEventNotify(TpCallIdentifier callReference,
			TpCallEventInfo eventInfo, int assignmentID) {
		BarringAppCall ipAppCall = new BarringAppCall(appLogic);
		appLogic.callEventNotify(callReference, eventInfo, assignmentID);
		return ipAppCall._this();
	}

	public void callNotificationInterrupted() {
		m_logger.info("Unimplemented function");
	}

	public void callNotificationContinued() {
		m_logger.info("Unimplemented function");
	}

	public void callOverloadEncountered(int assignmentID) {
		m_logger.info("Unimplemented function");
	}

	public void callOverloadCeased(int assignmentID) {
		m_logger.info("Unimplemented function");
	}

	public void abortMultipleCalls(int[] callReferenceSet) {
		m_logger.info("Unimplemented function");
	}

}
