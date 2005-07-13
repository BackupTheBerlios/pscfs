//$Id: IpCallControlManagerImpl.java,v 1.28 2005/07/13 20:45:10 huuhoa Exp $
/**
 * 
 */
package group5.server;

import group5.CallControlException;
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
import org.csapi.cc.TpCallMonitorMode;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.IpAppCallControlManagerHelper;
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.IpCallControlManagerPOA;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallTreatment;
import org.csapi.fw.TpServiceProperty;

/**
 * @author Hoang Trung Hai
 * 
 */
public class IpCallControlManagerImpl extends IpCallControlManagerPOA {

	private IpAppCallControlManager ipACCM_delegate;

	private IpCallControlManager ipCallControlManager;

	private HashMap administration;

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
		ipCallControlManager = _this(ServerFramework.getORB());
		administration = new HashMap();
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
		// TODO Auto-generated method stub
		if (ipACCM_delegate == null) {
			m_logger
					.error("The callback interface should be set first. Call SetCallback before calling this function");
			return null;
		}
		IpCallImpl aCallReference = new IpCallImpl(appCall);
		if (aCallReference == null) {
			// TODO: hh
			m_logger.error("Cannot create IpCall");
			ipACCM_delegate.callAborted(0);
			return null;
		}

		CallInfo callInfo = new CallInfo(getCallSessionID());
		callInfo.CallObject = aCallReference;
		callInfo.CallRefence = aCallReference._this(ServerFramework.getORB());
		;
		m_CallList.put(new Integer(callInfo.getSessionID()), callInfo);
		return callInfo.getCallIdentifier();
	}

	public CallInfo getCallInfo(int callSessionID) throws P_INVALID_SESSION_ID {
		CallInfo ci = (CallInfo) m_CallList.get(new Integer(callSessionID));
		if (ci == null)
			throw new P_INVALID_SESSION_ID();
		return ci;
	}

	public void updateCallInfo(int callSessionID, CallInfo ci)
			throws P_INVALID_SESSION_ID {
		CallInfo ciOld = (CallInfo) m_CallList.get(new Integer(callSessionID));
		if (ciOld == null)
			throw new P_INVALID_SESSION_ID();
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
		m_logger.info("Entering enableCallNotification");
		if (appCallControlManager == null) {
			String msgErr = "Parameter appCallControlManager is null";
			m_logger.info(msgErr);
			return 0;
		} else {
			m_logger.debug("appCallControlManager is not null");
			try {
				callEventCriteria(eventCriteria);
				return putNotificationObserver(appCallControlManager,
						eventCriteria);
			} catch (P_INVALID_CRITERIA e) {
				String msgErr = "Invalid criteria:" + e.ExtraInformation;
				m_logger.info(msgErr);

			} catch (P_INVALID_EVENT_TYPE e) {
				String msgErr = "Error in enableCallNotification() call:"
						+ e.ExtraInformation;
				m_logger.info(msgErr);
			}
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#disableCallNotification(int)
	 */

	public void disableCallNotification(int assignmentID)
			throws P_INVALID_ASSIGNMENT_ID, TpCommonExceptions {
		m_logger.debug("Disable Call Notification: m_Observer = " + m_Observer);
		m_Observer.remove(new Integer(assignmentID));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#setCallLoadControl(int,
	 *      org.csapi.cc.TpCallLoadControlMechanism,
	 *      org.csapi.cc.gccs.TpCallTreatment, org.csapi.TpAddressRange)
	 */
	public int setCallLoadControl(int duration,
			TpCallLoadControlMechanism mechanism, TpCallTreatment treatment,
			TpAddressRange addressRange) throws TpCommonExceptions,
			P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN {
		m_logger.error("Call to unimplemented function");
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#changeCallNotification(int,
	 *      org.csapi.cc.gccs.TpCallEventCriteria)
	 */
	public void changeCallNotification(int assignmentID,
			TpCallEventCriteria eventCriteria) throws P_INVALID_ASSIGNMENT_ID,
			P_INVALID_EVENT_TYPE, TpCommonExceptions, P_INVALID_CRITERIA {
		m_logger.error("Call to unimplemented function");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#getCriteria()
	 */
	public TpCallEventCriteriaResult[] getCriteria() throws TpCommonExceptions {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
	 */
	public void setCallback(IpInterface appInterface)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
		// TODO Auto-generated method stub
		ipACCM_delegate = IpAppCallControlManagerHelper.narrow(appInterface);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.IpServiceOperations#setCallbackWithSessionID(org.csapi.IpInterface,
	 *      int)
	 */
	public void setCallbackWithSessionID(IpInterface appInterface, int sessionID)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 */

	/*
	 * private void checkCriteria(CallCriteria crit) throws CallControlException {
	 * if (crit == null || crit.getCriteria() == 0) { throw new
	 * CallControlException("No events specified", 2, 2); } if
	 * ((crit.getCriteria() & 0xffffe000) != 0) { throw new
	 * CallControlException("Unknown event", 1, 2); } if
	 * (analyseEventMask(crit.getCriteria()) == 0) { throw new
	 * CallControlException( "No callbacks will be received for given events",
	 * 2, 2); } else { return; } }
	 */
	private void callEventCriteria(TpCallEventCriteria tpcalleventcriteria)
			throws P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE {
		if (tpcalleventcriteria.MonitorMode == TpCallMonitorMode.P_CALL_MONITOR_MODE_DO_NOT_MONITOR)
			throw new P_INVALID_CRITERIA("DO_NOT_MONITOR is invalid here");
		byte byte0 = 6;
		if ((tpcalleventcriteria.CallEventName | byte0) == byte0) {
			return;
		}
		if ((tpcalleventcriteria.CallEventName & 1) != 0)
			throw new P_INVALID_EVENT_TYPE(
					"P_EVENT_GCCS_OFFHOOK_EVENT not supported");
		else
			return;
	}

	public void destroy() throws CallControlException {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering destroy");
		if (ipCallControlManager == null) {
			String msgErr = "Already destroyed";
			if (m_logger.isInfoEnabled())
				m_logger.info(msgErr);
			throw new CallControlException("Already destroyed", 0, 2);
		}
		int id;
		try {
			for (Iterator i = administration.keySet().iterator(); i.hasNext(); disableCallNotification(id))
				id = ((Integer) i.next()).intValue();
		} catch (P_INVALID_ASSIGNMENT_ID e) {
			String msgErr = "Invalid assignment ID" + e.getMessage();
			if (m_logger.isInfoEnabled())
				m_logger.info(msgErr);
		} catch (TpCommonExceptions e) {
			String msgErr = "Error destroy" + e.getMessage();
			if (m_logger.isInfoEnabled())
				m_logger.info(msgErr);
		}
		administration.clear();
		ipCallControlManager._release();
		ipCallControlManager = null;
	}

	public boolean onRouteReq(int callSessionID) {
		m_logger.debug("Got routeReq event with callSessionID: "
				+ callSessionID);
		CallInfo ci;
		try {
			ci = getCallInfo(callSessionID);
		} catch (P_INVALID_SESSION_ID ex) {
			m_logger
					.fatal("Invalid session id while trying to call routeReq");
			return false;
		}
		m_logger.debug("Target Address: " + ci.getCallEventInfo().DestinationAddress.AddrString);
		m_logger.debug("Originating Address: " + ci.getCallEventInfo().OriginatingAddress.AddrString);
		Iterator iterator = m_Observer.values().iterator();
		while (iterator.hasNext()) {
			Observer observer = (Observer) iterator.next();
			// check event criteria
			m_logger.debug("ipAppManager of current observer: "
					+ observer.getIpAppCallControlManager());
			// dispatch event notification
			m_logger.debug("call info: " + ci);
			m_logger.debug("call session id: " + ci.getSessionID());

			// if ((observer.getTpCallEventCriteria().CallNotificationType ==
			// eventData.callEventInfo.CallNotificationType) &
			// (observer.getTpCallEventCriteria().OriginatingAddress.AddrString
			// == eventData.callEventInfo.OriginatingAddress.AddrString) &
			// (observer.getTpCallEventCriteria().DestinationAddress.AddrString
			// ==eventData.callEventInfo.DestinationAddress.AddrString) &
			// (observer.getTpCallEventCriteria().MonitorMode
			// ==eventData.callEventInfo.MonitorMode)){
			m_logger.debug("Check eventCriteria");
			IpAppCall ipAppCall = observer
					.getIpAppCallControlManager()
					.callEventNotify(ci.getCallIdentifier(), ci.getCallEventInfo(),
							observer.getAssignmentID());
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
		// TODO Auto-generated method stub
		m_CallList.remove(new Integer(callSessionID));
		return false;
	}

	public boolean onReleaseCall(int callSessionID) {
		// TODO Auto-generated method stub
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
		m_logger.debug("Enterring putNotificationObserver");
		Observer observer = new Observer(ipAppCallControlManager,
				tpCallEventCriteria, notificationObserverId);
		m_logger.debug("Enterring put into Observer: " + m_Observer);
		m_Observer.put(new Integer(notificationObserverId), observer);
		m_logger.debug("Returning notification ID: " + notificationObserverId);
		return notificationObserverId;
	}

}
