//$Id: IpCallControlManagerImpl.java,v 1.20 2005/07/09 09:17:14 hoanghaiham Exp $
/**
 * 
 */
package group5.server;

import group5.CallControlException;
//import group5.client.IpAppCallControlManagerImpl;
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
import org.csapi.TpAddress;
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
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallTreatment;
import org.csapi.fw.TpServiceProperty;

/**
 * @author Hoang Trung Hai
 * 
 */
public class IpCallControlManagerImpl extends IpCallControlManagerPOA implements
		CallControlAdapter {

	private IpAppCallControlManager ipACCM_delegate;

	private IpCallControlManager ipCallControlManager;

	private HashMap administration;

	private int CallSessionID;

	private synchronized int getCallSessionID() {
		CallSessionID++;
		return CallSessionID;
	}

	private HashMap mapIpCallIdentify;

	private HashMap mapIpCall;

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
		ipACCM_delegate = null;
		m_Observer = new HashMap();
		notificationObserverID = 0;
		ipCallControlManager = _this(ServerFramework.getORB());
		administration = new HashMap();
		CallSessionID = 0;
		mapIpCallIdentify = new HashMap();
		mapIpCall = new HashMap();
	}

	private String applicationID;

	public IpCallControlManagerImpl(String appID, TpServiceProperty atProp[]) {
		applicationID = appID;
		m_logger.info("New IpCallControlManagerImpl created for application "
				+ applicationID);
		m_Observer = new HashMap();
		ipACCM_delegate = null;
		notificationObserverID = 0;
		CallSessionID = 0;
		ipCallControlManager = _this(ServerFramework.getORB());
		administration = new HashMap();
		mapIpCallIdentify = new HashMap();
		mapIpCall = new HashMap();
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
			m_logger.error("The callback interface should be set first. Call SetCallback before calling this function");
			return null;
		}
		IpCallImpl aCallReference = new IpCallImpl(appCall);
		if (aCallReference == null) {
			// TODO: hh
			m_logger.error("Cannot create IpCall");
			ipACCM_delegate.callAborted(0);
			return null;
		}

		org.csapi.cc.gccs.TpCallIdentifier ci = new TpCallIdentifier();
		ci.CallReference = aCallReference._this(ServerFramework.getORB());
		ci.CallSessionID = getCallSessionID();
		mapIpCallIdentify.put(new Integer(ci.CallSessionID), ci);
		mapIpCall.put(new Integer(ci.CallSessionID), aCallReference);
		return ci;
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
	static int analyseEventMask(int eventMask) {
		return eventMask & 0xfe;
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

	/*
	 * (non-Javadoc)
	 */
	/*	private IpAppCallControlManagerImpl findRegistration(int id)
			throws CallControlException {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering findRegistration!");
		Object ccm = administration.get(new Integer(id));
		if (ccm != null)
			return (IpAppCallControlManagerImpl) ccm;
		String msgErr = "Invalid assignment ID " + id;
		if (m_logger.isInfoEnabled())
			m_logger.info(msgErr);
		throw new CallControlException(msgErr, 3, 2);
	}
	*/
	/*
	 * (non-Javadoc)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see group5.server.CallControlAdapter#onEvent(int,
	 *      group5.server.CallEvent)
	 */
	public boolean onEvent(int eventID, CallEvent eventData) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see group5.server.CallControlAdapter#onRouteReq(int,
	 *      org.csapi.TpAddress, org.csapi.TpAddress)
	 */
	public boolean onRouteReq(int callSessionID, TpAddress targetAddr,
			TpAddress origAddr) {
		TpCallEventInfo callEventInfo = new TpCallEventInfo();
		callEventInfo.DestinationAddress = targetAddr;
		callEventInfo.OriginatingAddress = origAddr;

		Iterator iterator = m_Observer.values().iterator();
		while (iterator.hasNext()) {
			Observer observer = (Observer) iterator.next();
			// check event criteria

			// dispatch event notification
			TpCallIdentifier ci = (TpCallIdentifier) mapIpCallIdentify
					.get(new Integer(callSessionID));
			IpAppCall ipAppCall = observer.getIpAppCallControlManager()
					.callEventNotify(ci, callEventInfo,
							observer.getAssignmentID());
			// set ipAppCall to IpCallImpl
			IpCallImpl ipCallImpl = (IpCallImpl) mapIpCall.get(new Integer(
					callSessionID));
			ipCallImpl.setIpAppCall(ipAppCall);
		}
		return false;
	}

	/**
	 * @see group5.server.CallControlAdapter#onDeassignCall(int)
	 */
	public boolean onDeassignCall(int callSessionID) {
		// TODO Auto-generated method stub
		mapIpCall.remove(new Integer(callSessionID));
		return false;
	}

	/**
	 * @see group5.server.CallControlAdapter#onReleaseCall(int)
	 */
	public boolean onReleaseCall(int callSessionID) {
		// TODO Auto-generated method stub
		mapIpCall.remove(new Integer(callSessionID));
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
			m_logger.debug("Enterring Observer" + ipAppCallControlManager
					+ " TpCallEventCriteria" + tpCallEventCriteria
					+ " AssignId" + assignID);
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
		m_logger.info(" Enterring putNotificationObserver ");
		Observer observer = new Observer(ipAppCallControlManager,
				tpCallEventCriteria, notificationObserverId);
		m_logger.info(" Enterring put into Observer " + m_Observer);
		m_Observer.put(new Integer(notificationObserverId), observer);
		m_logger.debug("Returning notification ID" + notificationObserverId);
		return notificationObserverId;
	}

}
