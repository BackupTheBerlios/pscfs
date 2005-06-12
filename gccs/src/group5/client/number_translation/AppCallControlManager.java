//$Id: AppCallControlManager.java,v 1.2 2005/06/12 22:24:04 huuhoa Exp $
package group5.client.number_translation;

import group5.client.ApplicationFramework;

import org.apache.log4j.Logger;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.IpAppCallControlManagerPOA;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;

public class AppCallControlManager extends IpAppCallControlManagerPOA {
	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(AppCall.class);
	}

	private IpAppCallControlManager ipAppCallControlManager;

	MyApplicationLogic appLogic;

	public AppCallControlManager(MyApplicationLogic logic) {
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
		AppCall ipAppCall = new AppCall(appLogic);
		appLogic.callEventNotify(callReference, eventInfo, assignmentID);
		return ipAppCall._this();
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
