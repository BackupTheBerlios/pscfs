//$Id: CallControlManager.java,v 1.9 2005/06/13 09:11:51 huuhoa Exp $
package group5.server;

import java.util.List;

import org.apache.log4j.Logger;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;

///         CallNotifications, CallNotification, OSACallObserver, IpCallImpl, 
//         IpCallControlManagerImpl
/**
 * 
 * @author Hoang Trung Hai
 * 
 */
public final class CallControlManager {
	private static Logger m_logger = Logger.getLogger(CallControlManager.class);

	private static CallControlManager collControlMager = new CallControlManager();

	private CallNotifications callNotification;

	private CallControlManager() {
		callNotification = new CallNotifications();
	}

	public static CallControlManager getInstance() {
		return collControlMager;
	}

	public TpCallEventCriteriaResult[] getCallEventCriteria(
			IpCallControlManagerImpl ipcallcontrolmanagerimpl) {
		return callNotification.getCallEventCriteria(ipcallcontrolmanagerimpl);
	}

	public int addCallNotification(CallNotification callnotification)
			throws P_INVALID_INTERFACE_TYPE, P_INVALID_EVENT_TYPE,
			TpCommonExceptions, P_INVALID_CRITERIA {
		List list = callNotification.addCallNotification(callnotification);
		if (list.size() > 0) {
			m_logger.debug("call notification "
					+ callnotification.getAssignmentId() + " overlaps");
			if (list.size() == 1) {
				CallNotification callnotification1 = (CallNotification) list
						.get(0);
				if (callnotification1.exactlyMatchesCriteria(callnotification)) {
					m_logger
							.debug("  Perfect match. Setting fallback interface...");
					callnotification1.setFallback(callnotification);
					return callnotification1.getAssignmentId();
				}
			}
			throw new P_INVALID_CRITERIA(
					"There is a call notification with overlapping criteria");
		} else {
			return callnotification.getAssignmentId();
		}
	}

	public void disableCallNotification(int assignmentID)
			throws P_INVALID_ASSIGNMENT_ID, TpCommonExceptions {
		CallNotification callnotification = callNotification
				.getCallNotification(assignmentID);
		if (callnotification != null)
			callnotification.reset();
		else
			throw new P_INVALID_ASSIGNMENT_ID(Integer.toString(assignmentID));
	}

	public void changeCallNotification(int assignmentID,
			TpCallEventCriteria tpcalleventcriteria)
			throws P_INVALID_ASSIGNMENT_ID, P_INVALID_EVENT_TYPE,
			TpCommonExceptions, P_INVALID_CRITERIA {
		try {
			CallNotification callnotification = callNotification
					.getCallNotification(assignmentID);
			if (callnotification != null) {
				List list;
				CallNotification callnotification1 = new CallNotification(
						tpcalleventcriteria);
				list = callNotification.findOverlapping(callnotification1);
				list.size();
				if (list.size() == 2) {

					throw new P_INVALID_CRITERIA(
							"Assignment ids match, setting event criteria...");
				} else {
					if (list.size() == 0) {
						m_logger.debug("call notification "
								+ callnotification.getAssignmentId()
								+ " has no overlap, setting event criteria...");
						callnotification.callEventCriteria(tpcalleventcriteria);

					} else {
						CallNotification callnotification2;
						m_logger.debug("call notification "
								+ callnotification.getAssignmentId()
								+ " has overlap.");
						callnotification2 = (CallNotification) list.get(0);
						if (callnotification2.getAssignmentId() != assignmentID) {
							String msgErr = "Assignment ids match, setting event criteria...";
							m_logger.debug(msgErr);
							throw new P_INVALID_CRITERIA(msgErr);
						} else {
							m_logger
									.debug("  Assignment ids match, setting event criteria...");
							callnotification
									.callEventCriteria(tpcalleventcriteria);
						}
					}
				}
			} else {
				throw new P_INVALID_ASSIGNMENT_ID(Integer
						.toString(assignmentID));
			}
		} catch (P_INVALID_ADDRESS e) {
			String msgErr = "Invalid valid address" + e.ExtraInformation;
			m_logger.debug(msgErr);
			throw new TpCommonExceptions(msgErr, 10, "changeCallNotification");
		}
	}

	/*
	 * public void onAddSubscriber(CCSubscriberGroup ccsubscribergroup) { }
	 * 
	 * public void onRemoveSubscriber(CCSubscriberGroup ccsubscribergroup) { }
	 * 
	 * public void onNewCall(CallContext callcontext) { CallNotification
	 * acallnotification[] = _fldgoto.a(callcontext); for(int i = 0; i <
	 * acallnotification.length; i++) { OSACallObserver osacallobserver = new
	 * OSACallObserver(callcontext, acallnotification[i]);
	 * IpCallImpl.getInstance().registerCallObserver(osacallobserver); }
	 *  }
	 * 
	 * public void onEndCall(CallContext callcontext) { }
	 */

	public void onStateCleared() {
		callNotification.clearState();
	}

	/*
	 * public void onPowerSwitch(CCSubscriberGroup ccsubscribergroup, boolean
	 * flag) { }
	 */

}
