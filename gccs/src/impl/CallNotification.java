//$Id: CallNotification.java,v 1.3 2005/06/12 22:24:03 huuhoa Exp $
package impl;

import org.apache.log4j.Logger;
import org.csapi.IpInterface;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.TpAddressPlan;
import org.csapi.cc.TpCallMonitorMode;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;

final class CallNotification {

	public CallNotification(IpCallControlManagerImpl ipcallcontrolmanagerimpl,
			IpAppCallControlManager ipappcallcontrolmanager,
			TpCallEventCriteria tpcalleventcriteria) throws P_INVALID_ADDRESS,
			P_INVALID_EVENT_TYPE {
		m_secondCallbackInterface = null;
		m_logger.debug("New CallNotification being created");
		ipCallManager = ipcallcontrolmanagerimpl;
		m_firstCallbackInterface = ipappcallcontrolmanager;
		checkEventType();
		assignmentID = createAssignmentId();
		callEventCriteria(tpcalleventcriteria);
	}

	CallNotification(TpCallEventCriteria tpcalleventcriteria)
			throws P_INVALID_ADDRESS, P_INVALID_EVENT_TYPE {
		this(null, null, tpcalleventcriteria);
	}

	private boolean sameSourceAddr(CallNotification callnotification) {
		return sourceAddress.equals(callnotification.sourceAddress);
	}

	private boolean sameDestAddress(CallNotification callnotification) {
		return destAddress.equals(callnotification.destAddress);
	}

	private boolean sameAddrPlan(CallNotification callnotification) {
		TpAddressPlan tpaddressplan = callEventCriteria.OriginatingAddress.Plan;
		TpAddressPlan tpaddressplan1 = callEventCriteria.DestinationAddress.Plan;
		TpAddressPlan tpaddressplan2 = callnotification.callEventCriteria.OriginatingAddress.Plan;
		TpAddressPlan tpaddressplan3 = callnotification.callEventCriteria.DestinationAddress.Plan;
		return tpaddressplan.value() == tpaddressplan2.value()
				&& tpaddressplan1.value() == tpaddressplan3.value();
	}

	private boolean sameNotificationType(CallNotification callnotification) {
		return callEventCriteria.CallNotificationType.value() == callnotification.callEventCriteria.CallNotificationType
				.value();
	}

	private boolean sameMonitorMode(CallNotification callnotification) {
		return callnotification.callEventCriteria.MonitorMode == callEventCriteria.MonitorMode;
	}

	private boolean sameInterruptMode(CallNotification callnotification) {
		return callnotification.callEventCriteria.MonitorMode == TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT
				&& callEventCriteria.MonitorMode == TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT;
	}

	boolean sameCallNotification(CallNotification callnotification) {
		if (ipCallManager != callnotification.ipCallManager
				&& !sameInterruptMode(callnotification))
			return false;
		else
			return sameMonitorMode(callnotification)
					&& sameSourceAddr(callnotification)
					&& sameDestAddress(callnotification)
					&& sameAddrPlan(callnotification)
					&& sameNotificationType(callnotification);
	}

	private boolean matchCriteria(TpCallEventCriteria tpcalleventcriteria,
			TpCallEventCriteria tpcalleventcriteria1) {
		return tpcalleventcriteria.CallEventName == tpcalleventcriteria1.CallEventName
				&& tpcalleventcriteria.CallNotificationType == tpcalleventcriteria1.CallNotificationType
				&& tpcalleventcriteria.DestinationAddress.AddrString
						.equals(tpcalleventcriteria1.DestinationAddress.AddrString)
				&& tpcalleventcriteria.DestinationAddress.Plan == tpcalleventcriteria1.DestinationAddress.Plan
				&& tpcalleventcriteria.OriginatingAddress.AddrString
						.equals(tpcalleventcriteria1.OriginatingAddress.AddrString)
				&& tpcalleventcriteria.OriginatingAddress.Plan == tpcalleventcriteria1.OriginatingAddress.Plan
				&& tpcalleventcriteria.MonitorMode == tpcalleventcriteria1.MonitorMode;
	}

	public boolean exactlyMatchesCriteria(CallNotification callnotification) {
		return ipCallManager == callnotification.ipCallManager
				&& matchCriteria(callEventCriteria,
						callnotification.callEventCriteria);
	}

	public int getAssignmentId() {
		return assignmentID;
	}

	public void setFallback(CallNotification callnotification) {
		if (callnotification.m_firstCallbackInterface != m_firstCallbackInterface) {
			m_secondCallbackInterface = m_firstCallbackInterface;
			m_firstCallbackInterface = callnotification.m_firstCallbackInterface;
		}
	}

	public boolean hasManager(IpCallControlManagerImpl ipcallcontrolmanagerimpl) {
		return ipCallManager == ipcallcontrolmanagerimpl;
	}

	public IpInterface eventNotify(TpCallIdentifier tpcallidentifier,
			TpCallEventInfo tpcalleventinfo) {
		return m_firstCallbackInterface.callEventNotify(tpcallidentifier,
				tpcalleventinfo, assignmentID);
	}

	public IpInterface eventNotifyFallback(TpCallIdentifier tpcallidentifier,
			TpCallEventInfo tpcalleventinfo) {
		return m_secondCallbackInterface.callEventNotify(tpcallidentifier,
				tpcalleventinfo, assignmentID);
	}

	public boolean isActiveListener() {
		return callEventCriteria == null ? false
				: callEventCriteria.MonitorMode.value() == 0;
	}

	public static int createAssignmentId() {
		return staticAssignmentID != 0x7fffffff ? staticAssignmentID++ : 0;
	}

	void checkInterfaceType() throws P_INVALID_INTERFACE_TYPE {
	}

	void checkEventType() throws P_INVALID_EVENT_TYPE {
	}

	/**
	 * getCallEventCriteria
	 */
	TpCallEventCriteria _mthif() {
		return callEventCriteria;
	}

	void callEventCriteria(TpCallEventCriteria tpcalleventcriteria)
			throws P_INVALID_ADDRESS {
		callEventCriteria = tpcalleventcriteria;
		sourceAddress = new String(
				tpcalleventcriteria.OriginatingAddress.AddrString);
		destAddress = new String(
				tpcalleventcriteria.DestinationAddress.AddrString);
	}

	boolean callContext(CallContext callcontext) {
		return ((sourceAddress.compareTo(callcontext.getOriginatorNumber()) == 0) && (destAddress
				.compareTo(callcontext.getDestinationNumber()) == 0));
	}

	public void reset() {
		if (m_firstCallbackInterface != null) {
			m_firstCallbackInterface._release();
			m_firstCallbackInterface = null;
		}
		if (m_secondCallbackInterface != null) {
			m_secondCallbackInterface._release();
			m_secondCallbackInterface = null;
		}
		callEventCriteria = null;
		sourceAddress = null;
		destAddress = null;
		ipCallManager = null;
	}

	public String toString() {
		return "CallNotification: src=" + sourceAddress + " dest="
				+ destAddress + " hasFallback="
				+ (m_secondCallbackInterface != null);
	}

	private static Logger m_logger = Logger.getLogger(CallNotification.class);

	private static int staticAssignmentID = 0;

	private int assignmentID;

	private IpAppCallControlManager m_firstCallbackInterface;

	private IpAppCallControlManager m_secondCallbackInterface;

	private TpCallEventCriteria callEventCriteria;

	private String sourceAddress;

	private String destAddress;

	private IpCallControlManagerImpl ipCallManager;

}
