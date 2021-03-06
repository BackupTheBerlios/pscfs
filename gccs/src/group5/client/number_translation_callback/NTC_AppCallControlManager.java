//$Id: NTC_AppCallControlManager.java,v 1.3 2005/07/27 08:59:41 huuhoa Exp $
package group5.client.number_translation_callback;

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
public class NTC_AppCallControlManager extends IpAppCallControlManagerPOA {
	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(NTC_AppCall.class);
	}

	private IpAppCallControlManager ipAppCallControlManager;

	NTC_ApplicationLogic appLogic;

	public NTC_AppCallControlManager(NTC_ApplicationLogic logic) {
		appLogic = logic;
		ipAppCallControlManager = _this(ApplicationFramework.getORB());
	}

	public void callAborted(int callReference) {

	}

	IpAppCallControlManager getServant() {
		return ipAppCallControlManager;
	}

	public IpAppCall callEventNotify(TpCallIdentifier callReference,
			TpCallEventInfo eventInfo, int assignmentID) {
		m_logger.debug("Receive callEventNotify from server");
		NTC_AppCall ipAppCall = new NTC_AppCall(appLogic);
		appLogic.callEventNotify(callReference, eventInfo, assignmentID);
		m_logger.debug("Returning a refence to IpAppCall");
		return ipAppCall._this(ApplicationFramework.getORB());
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
