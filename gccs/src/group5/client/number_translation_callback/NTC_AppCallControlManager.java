//$Id: NTC_AppCallControlManager.java,v 1.1 2005/07/10 12:04:20 aachenner Exp $
package group5.client.number_translation_callback;

import group5.client.ApplicationFramework;

import org.apache.log4j.Logger;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.IpAppCallControlManagerPOA;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void callNotificationContinued() {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void callOverloadEncountered(int assignmentID) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void callOverloadCeased(int assignmentID) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void abortMultipleCalls(int[] callReferenceSet) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

}
