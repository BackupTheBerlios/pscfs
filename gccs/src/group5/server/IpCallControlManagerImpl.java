//$Id: IpCallControlManagerImpl.java,v 1.34 2005/07/28 23:08:39 hoanghaiham Exp $
/**
 * 
 */
package group5.server;

import group5.server.framework.ServerFramework;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.csapi.IpInterface;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddressRange;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallLoadControlMechanism;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.IpAppCallControlManagerHelper;
import org.csapi.cc.gccs.IpCallControlManagerPOA;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallTreatment;
import org.csapi.fw.TpServiceProperty;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class IpCallControlManagerImpl extends IpCallControlManagerPOA {

	private IpAppCallControlManager ipACCM_delegate;

	private int CallSessionID;

	private synchronized int getCallSessionID() {
		CallSessionID++;
		return CallSessionID;
	}

	// array of call objects, accessed through call session id
	private HashMap m_CallList;

	private HashMap m_Observer;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger m_logger = Logger
			.getLogger(IpCallControlManagerImpl.class);

	/**
	 * 
	 */
	public IpCallControlManagerImpl() {
		super();
		m_logger.info("ctor()");
		Initialize();
	}

	private String applicationID;

	// private int nWatcherID;

	public IpCallControlManagerImpl(String appID, TpServiceProperty atProp[]) {
		applicationID = appID;
		m_logger.info("New IpCallControlManagerImpl created for application "
				+ applicationID);
		Initialize();
	}

	private void Initialize() {
		m_Observer = new HashMap();
		ipACCM_delegate = null;
		notificationObserverID = 0;
		CallSessionID = 0;
		m_CallList = new HashMap();
		registerEventWatcher();
	}

	private void registerEventWatcher() {
		// register with call simulator
		CallSimulator.getInstance().registerCallControlManager(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#createCall(org.csapi.cc.gccs.IpAppCall)
	 */
	public TpCallIdentifier createCall(IpAppCall appCall)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
		if (ipACCM_delegate == null) {
			m_logger.error("The callback interface should be set first."
					+ " Call SetCallback before calling this function");
			return null;
		}
		IpCallImpl aCallReference = new IpCallImpl(appCall);
		if (aCallReference == null) {
			m_logger.error("Cannot create IpCall");
			ipACCM_delegate.callAborted(0);
			return null;
		}
		CallInfo callInfo = new CallInfo(getCallSessionID());
		callInfo.CallObject = aCallReference;
		callInfo.CallRefence = aCallReference._this(ServerFramework.getORB());
		m_CallList.put(new Integer(callInfo.getSessionID()), callInfo);
		return callInfo.getCallIdentifier();
	}

	private void dumpCallInfoDB() {
		Iterator it = m_CallList.values().iterator();
		String strDumpInfomation="Call Session ID Database:\n";
		while (it.hasNext())
		{
			CallInfo ci = (CallInfo)it.next();
			strDumpInfomation += new Integer(ci.getSessionID()).toString() + "\n";
		}
		m_logger.info(strDumpInfomation);
	}
	public CallInfo getCallInfo(int callSessionID) throws P_INVALID_SESSION_ID {
		CallInfo ci = (CallInfo) m_CallList.get(new Integer(callSessionID));
		if (ci == null)
		{
			dumpCallInfoDB();
			throw new P_INVALID_SESSION_ID("Invalid session ID [" + callSessionID + "]");
		}
		return ci;
	}

	public void updateCallInfo(int callSessionID, CallInfo ci)
			throws P_INVALID_SESSION_ID {
		CallInfo ciOld = (CallInfo) m_CallList.get(new Integer(callSessionID));
		if (ciOld == null)
			throw new P_INVALID_SESSION_ID("Invalid session ID [" + callSessionID + "]");
		m_CallList.put(new Integer(callSessionID), ci);
	}

	/**
	 * 
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#enableCallNotification(org.csapi.cc.gccs.IpAppCallControlManager,
	 *      org.csapi.cc.gccs.TpCallEventCriteria)
	 */
	public int enableCallNotification(
			IpAppCallControlManager appCallControlManager,
			TpCallEventCriteria eventCriteria) throws P_INVALID_INTERFACE_TYPE,
			P_INVALID_EVENT_TYPE, TpCommonExceptions, P_INVALID_CRITERIA {
		m_logger.info("Application [" + applicationID + "] requires *enableCallNotification*");
		if (appCallControlManager == null) {
			m_logger.fatal("Parameter appCallControlManager is null");
			throw new P_INVALID_INTERFACE_TYPE("Invalid interface type");
		} else {
			return putNotificationObserver(appCallControlManager, eventCriteria);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#disableCallNotification(int)
	 */

	public void disableCallNotification(int assignmentID)
			throws P_INVALID_ASSIGNMENT_ID, TpCommonExceptions {
		m_logger.info("Disable Call Notification: m_Observer = " + m_Observer);
		if (m_Observer.containsKey(new Integer(assignmentID))==false)
		{
			throw new P_INVALID_ASSIGNMENT_ID("Invalid assignment id", new Integer(assignmentID).toString());
		}
		m_Observer.remove(new Integer(assignmentID));
	}

	public int setCallLoadControl(int duration,
			TpCallLoadControlMechanism mechanism, TpCallTreatment treatment,
			TpAddressRange addressRange) throws TpCommonExceptions,
			P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN {
		m_logger.error("Call to unimplemented function");
		return 0;
	}

	public void changeCallNotification(int assignmentID,
			TpCallEventCriteria eventCriteria) throws P_INVALID_ASSIGNMENT_ID,
			P_INVALID_EVENT_TYPE, TpCommonExceptions, P_INVALID_CRITERIA {
		m_logger.error("Call to unimplemented function");
	}

	public TpCallEventCriteriaResult[] getCriteria() throws TpCommonExceptions {
		return null;
	}

	public void setCallback(IpInterface appInterface)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
		ipACCM_delegate = IpAppCallControlManagerHelper.narrow(appInterface);
	}

	public void setCallbackWithSessionID(IpInterface appInterface, int sessionID)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		m_logger.error("Call to unimplemented function");
	}

	public boolean onRouteReq(int callSessionID) {
		m_logger.debug("callSessionID = " + callSessionID);
		CallInfo ci;
		try {
			ci = getCallInfo(callSessionID);
		} catch (P_INVALID_SESSION_ID ex) {
			m_logger.fatal("Invalid session id while trying to call routeReq");
			return false;
		}
		m_logger.debug("Target Address: "
				+ ci.getCallEventInfo().DestinationAddress.AddrString
				+ "\n\tOriginating Address: "
				+ ci.getCallEventInfo().OriginatingAddress.AddrString);
		Iterator iterator = m_Observer.values().iterator();
		while (iterator.hasNext()) {
			Observer observer = (Observer) iterator.next();
			// check event criteria
			m_logger.debug("ipAppManager of current observer: "
					+ observer.getIpAppCallControlManager());
			// dispatch event notification
			m_logger.debug("call info: " + ci + "\n\tcall session id: "
					+ ci.getSessionID());

			// if ((observer.getTpCallEventCriteria().CallNotificationType ==
			// eventData.callEventInfo.CallNotificationType) &
			// (observer.getTpCallEventCriteria().OriginatingAddress.AddrString
			// == eventData.callEventInfo.OriginatingAddress.AddrString) &
			// (observer.getTpCallEventCriteria().DestinationAddress.AddrString
			// ==eventData.callEventInfo.DestinationAddress.AddrString) &
			// (observer.getTpCallEventCriteria().MonitorMode
			// ==eventData.callEventInfo.MonitorMode)){
			IpAppCall ipAppCall = observer.getIpAppCallControlManager()
					.callEventNotify(ci.getCallIdentifier(),
							ci.getCallEventInfo(), observer.getAssignmentID());
			// set ipAppCall to IpCallImpl
			ci.CallObject.setIpAppCall(ipAppCall);
			// }
			// IpAppCall ipAppCall = observer.getIpAppCallControlManager()
			// .callEventNotify(ci.getCallIdentifier(), eventData.callEventInfo,
			// observer.getAssignmentID());

			// set ipAppCall to IpCallImpl

			// m_logger.debug("Hallo");
			// ci.CallObject.setIpAppCall(ipAppCall);
		}
		m_logger.debug("Getting out routeReq");
		return false;
	}

	public boolean onDeassignCall(int callSessionID) {
		m_CallList.remove(new Integer(callSessionID));
		return false;
	}

	public boolean onReleaseCall(int callSessionID) {
		m_CallList.remove(new Integer(callSessionID));
		return false;
	}

	private int notificationObserverID;

	private int getObserverID() {
		m_logger.debug("Entering getObserverID, notificationID = "
				+ notificationObserverID);
		notificationObserverID++;
		return notificationObserverID;
	}

	private class Observer {
		private IpAppCallControlManager ipAppCallControlManager;

		private TpCallEventCriteria tpCallEventCriteria;

		private int assignmentID;

		public Observer(IpAppCallControlManager ipAppCallControlManager,
				TpCallEventCriteria tpCallEventCriteria, int assignID) {
			m_logger.debug("Entering Observer with TpCallEventCriteria: "
					+ tpCallEventCriteria + ", assignID: " + assignID
					+ ", appCallControlManager " + ipAppCallControlManager);
			this.ipAppCallControlManager = ipAppCallControlManager;
			this.tpCallEventCriteria = tpCallEventCriteria;
			assignmentID = assignID;
		}

		public IpAppCallControlManager getIpAppCallControlManager() {
			return ipAppCallControlManager;
		}

		public TpCallEventCriteria getTpCallEventCriteria() {
			return tpCallEventCriteria;
		}

		public synchronized int getAssignmentID() {
			assignmentID++;
			return assignmentID;
		}
	}

	private int putNotificationObserver(
			IpAppCallControlManager ipAppCallControlManager,
			TpCallEventCriteria tpCallEventCriteria) {
		int notificationObserverId = getObserverID();
		Observer observer = new Observer(ipAppCallControlManager,
				tpCallEventCriteria, notificationObserverId);
		m_Observer.put(new Integer(notificationObserverId), observer);
		m_logger.debug("notificationID = " + notificationObserverId);
		return notificationObserverId;
	}
	public String toString() {
		return "CallControlManager for application *" + applicationID + "*";
	}
}
