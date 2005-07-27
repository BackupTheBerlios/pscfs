//$Id: AppCallControlManager.java,v 1.5 2005/07/27 08:47:09 huuhoa Exp $
package group5.client.number_translation;

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
		m_logger.debug("Receive callEventNotify from server");
		AppCall ipAppCall = new AppCall(appLogic);
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
