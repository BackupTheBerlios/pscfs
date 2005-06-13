//$Id: IpAppCallControlManagerImpl.java,v 1.13 2005/06/13 09:11:51 huuhoa Exp $
/**
 * IpAppCallControlManagerImpl: Implementation of the interface IpAppCallControlManager
 * This class will manage the calls from the application's side 
 */

package group5.client;

import group5.CallControlEvent;
import group5.CallControlException;
import group5.CallControlListener;
import group5.CallCriteria;
import group5.TypeConverter;
import group5.client.number_translation.MyApplicationLogic;
import group5.server.IpCallControlManagerImpl;
import group5.server.IpCallImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallError;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.IpAppCallControlManagerPOA;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.omg.CORBA.SystemException;

/**
 * @author Nguyen Huu Hoa
 * 
 */
public class IpAppCallControlManagerImpl extends IpAppCallControlManagerPOA {

	MyApplicationLogic appLogic;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(IpAppCallControlManagerImpl.class);
	}

	private IpCallControlManagerImpl ccManager;

	private IpAppCallControlManager ipAppCallControlManager;

	private IpAppCallImpl ipAppCallImpl;

	private CallControlListener callControlListener;

	private Map callAdministration;

	private CallCriteria callCriteria;

	private int assignmentId;

	/**
	 * 
	 */
	public IpAppCallControlManagerImpl(MyApplicationLogic logic) {
		super();
		if (m_logger.isInfoEnabled()) {
			m_logger.info("ctor()");
		}

		// TODO Auto-generated constructor stub
		appLogic = logic;
	}

	/**
	 * This method notifies the application of the arrival of a call-related
	 * event. If this method is invoked with a monitor mode of
	 * P_CALL_MONITOR_MODE_INTERRUPT, then the APL has control of the call. If
	 * the APL does nothing with the call (including its associated legs) within
	 * a specified time period (the duration of which forms a part of the
	 * service level agreement), then the call in the network shall be released
	 * and callEnded() shall be invoked, giving a release cause of 102 (Recovery
	 * on timer expiry).
	 * 
	 * <p>
	 * <b>Setting the callback reference:</b> A reference to the application
	 * interface has to be passed back to the call interface to which the
	 * notification relates. However, the setting of a call back reference is
	 * only applicable if the notification is in INTERRUPT mode. When the
	 * callEventNotify() method is invoked with a monitor mode of
	 * P_CALL_MONITOR_MODE_INTERRUPT, the application writer should ensure that
	 * no continue processing e.g. routeReq() is performed until an IpAppCall
	 * has been passed to the gateway, either through an explicit
	 * setCallbackWithSessionID() invocation on the supplied IpCall, or via the
	 * return of the callEventNotify() method. The callback reference can be
	 * registered either in a) callEventNotify() or b) explicitly with a
	 * setCallbackWithSessionID() method e.g. depending on how the application
	 * provides its call reference.
	 * 
	 * Case a: From an efficiency point of view the callEventNotify() with
	 * explicit pass of registration may be the preferred method. Case b: The
	 * callEventNotify() with no callback reference ("Null" value) is used where
	 * (e.g. due to distributed application logic) the callback reference is
	 * provided subsequently in a setCallbackWithSessionID(). In case the
	 * callEventNotify() contains no callback, at the moment the application
	 * needs to be informed the gateway will use as callback the callback that
	 * has been registered by setCallbackWithSessionID(). See example in 4.6
	 * Returns appCall: Specifies a reference to the application interface which
	 * implements the callback interface for the new call. If the application
	 * has previously explicitly passed a reference to the IpAppCall interface
	 * using a setCallbackWithSessionID() invocation, this parameter may be
	 * null, or if supplied must be the same as that provided during the
	 * setCallbackWithSessionID(). This parameter will be null if the
	 * notification is in NOTIFY mode and in case b).
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallControlManagerOperations#callEventNotify(org.csapi.cc.gccs.TpCallIdentifier,
	 *      org.csapi.cc.gccs.TpCallEventInfo, int)
	 */
	public IpAppCall callEventNotify(TpCallIdentifier callReference,
			TpCallEventInfo eventInfo, int assignmentID) {
		// TODO clear out some confused about returning reference of appCall
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering callEventNotify!");
		if (m_logger.isInfoEnabled())
			m_logger.info("(" + Integer.toString(callReference.CallSessionID)
					+ "," + TypeConverter.eventName(eventInfo.CallEventName)
					+ "," + Integer.toString(assignmentID) + ")");
		if (m_logger.isInfoEnabled())
			m_logger.info("Trying to create notifyApplication thread!");
		try {
			final IpCallImpl call = findCreateCall(callReference, eventInfo);
			final int CallEventName = eventInfo.CallEventName;
			// further push event notification
			if ((callCriteria.getCriteria() & eventInfo.CallEventName) != 0)
				(new Thread() {
					public void run() {
						notifyApplication(call, CallEventName, null);
					}

				}).start();
		} catch (P_INVALID_INTERFACE_TYPE ex) {
			if (m_logger.isInfoEnabled())
				m_logger
						.info("Catch exception of P_INVALID_INTERFACE_TYPE with more information: "
								+ ex.getMessage());
		} catch (P_INVALID_SESSION_ID ex) {
			if (m_logger.isInfoEnabled())
				m_logger
						.info("Catch exception of P_INVALID_SESSION_ID with more information: "
								+ ex.getMessage());
		} catch (TpCommonExceptions ex) {
			if (m_logger.isInfoEnabled())
				m_logger
						.info("Catch exception of TpCommonExceptions with more information: "
								+ ex.getMessage());
		}
		if (m_logger.isInfoEnabled())
			m_logger.info("After create notifyApplication thread!");
		return callCriteria.isInterruptMode() ? ipAppCallImpl.getServant()
				._this() : null;
		//
		// System.out.println("IpAppCallControlManager.callEventNotify() is
		// called with callReference=" + callReference);
		// IpAppCall ipAppCall = new IpAppCallImpl(appLogic);
		// appLogic.callEventNotify(callReference, eventInfo, assignmentID);
		//		
		// return ipAppCall;
	}

	public void callAborted(int callReference) {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering callAborted! " + "("
					+ Integer.toString(callReference) + ")");
		IpCallImpl call = (IpCallImpl) callAdministration.remove(new Integer(
				callReference));
		if (m_logger.isInfoEnabled())
			m_logger.info("Trying to call notifyApplication!");
		if (call != null) {
			// call.onAborted();
			notifyApplication(call, 2048, null);
		}
	}

	public void callNotificationContinued() {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering callNotificationContinued!");
	}

	public void callNotificationInterrupted() {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering callNotificationInterrupted!");
	}

	public void callOverloadCeased(int assignmentID) {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering callOverloadCeased! " + "("
					+ Integer.toString(assignmentID) + ")");

	}

	public void callOverloadEncountered(int assignmentID) {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering callOverloadEncountered! " + "("
					+ Integer.toString(assignmentID) + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallControlManagerOperations#abortMultipleCalls(int[])
	 */
	public void abortMultipleCalls(int[] callReferenceSet) {
		// TODO Auto-generated method stub
		if (m_logger.isInfoEnabled())
			m_logger
					.info("IpAppCallControlManager.abortMultipleCalls() is called");
	}

	public IpAppCallControlManagerImpl(IpCallControlManagerImpl ccManager,
			TpCallEventCriteria criteria
	// , CallControlListener listener
	) throws CallControlException {
		ipAppCallControlManager = null;
		callControlListener = null;
		callAdministration = null;
		assignmentId = -1;
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering IpAppCallControlManagerImpl Construction!");
		this.ccManager = ccManager;
		// callCriteria = criteria;
		// callControlListener = listener;
		callAdministration = Collections.synchronizedMap(new HashMap());
		ipAppCallImpl = new IpAppCallImpl(ccManager, this);
		try {
			// TODO try to get ORB here
			// ipAppCallControlManager = _this(ORBUtil.getOrb());
		} catch (SystemException ex) {
			if (m_logger.isDebugEnabled())
				m_logger.debug(ex.toString());
			throw new CallControlException(ex.toString(), 0, 3);
		} catch (Exception ex) {
			if (m_logger.isDebugEnabled())
				m_logger.debug(ex.toString());
			throw new CallControlException(ex.toString(), 0, 2);
		}
	}

	void destroy() {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering destroy!");
		ipAppCallImpl.destroy();
		callAdministration.clear();
		ipAppCallControlManager._release();
	}

	public IpAppCallControlManager getServant() {
		return ipAppCallControlManager;
	}

	public int getAssignmentId() {
		return assignmentId;
	}

	void setAssignmentId(int id) {
		assignmentId = id;
	}

	public CallCriteria getCallCriteria() {
		return callCriteria;
	}

	public void setCallCriteria(CallCriteria callCriteria) {
		this.callCriteria = callCriteria;
	}

	public void notifyApplication(IpCallImpl call, int ofEvent,
			TpCallError error) {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering actually notifyApplication!");
		/**
		 * @TODO: call setError
		 */
		// call.setError(error);
		if (m_logger.isInfoEnabled())
			m_logger.info("Trying to deassign the call when Event.CALL_ENDED!");
		if (ofEvent == 1024) {
			try {
				/**
				 * @TODO: assign the call session ID
				 */
				call.deassignCall(0);
			} catch (org.csapi.P_INVALID_SESSION_ID ex) {
				if (m_logger.isInfoEnabled())
					m_logger.info("Common exception with information: "
							+ ex.getMessage());
			} catch (TpCommonExceptions ex) {
				if (m_logger.isInfoEnabled())
					m_logger.info("Common exception with information: "
							+ ex.getMessage());
			}
		}
		if (m_logger.isInfoEnabled())
			m_logger.info("Trying to call callControlListener.onEvent()!");
		callControlListener.onEvent(new CallControlEvent(ccManager._this(),
				assignmentId, 3, call._this(), ofEvent));
	}

	public void notifyApplication(int callSessionID, int ofEvent,
			TpCallError error) {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering notifyApplication!");
		IpCallImpl call = findCall(callSessionID);
		if (call != null)
			notifyApplication(call, ofEvent, error);
	}

	private IpCallImpl findCall(int callSessionID) {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering findCall!");
		return (IpCallImpl) callAdministration.get(new Integer(callSessionID));
	}

	private IpCallImpl findCreateCall(TpCallIdentifier callid,
			TpCallEventInfo info) throws TpCommonExceptions,
			P_INVALID_SESSION_ID, P_INVALID_INTERFACE_TYPE {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering findCreateCall!");
		Integer id = new Integer(callid.CallSessionID);
		IpCallImpl call = (IpCallImpl) callAdministration.get(id);
		if (call == null) {
			call = new IpCallImpl(callid, info.OriginatingAddress.AddrString,
					info.DestinationAddress.AddrString, this, ipAppCallImpl);
			callAdministration.put(id, call);
			if (m_logger.isInfoEnabled())
				m_logger.info("Created call object id = "
						+ callid.CallSessionID);
		}
		return call;
	}

	public void callDone(int callSessionId) {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering callDone!");
		java.lang.Object o = callAdministration.remove(new Integer(
				callSessionId));
		if (o == null && m_logger.isInfoEnabled())
			m_logger.info("callDone(): call does not exist");
	}
}
