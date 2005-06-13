//$Id: CallNotifications.java,v 1.6 2005/06/13 08:20:03 huuhoa Exp $
package impl;

//	 Source File Name:   CallNotifications.java

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;

final class CallNotifications {

	CallNotifications() {
		notificationTable = new Hashtable();
	}

	public TpCallEventCriteriaResult[] getCallEventCriteria(
			IpCallControlManagerImpl ipcallcontrolmanagerimpl) {
		Vector vector = new Vector();
		for (Iterator iterator = notificationTable.values().iterator(); iterator
				.hasNext();) {
			CallNotification callnotification = (CallNotification) iterator
					.next();
			if (callnotification.hasManager(ipcallcontrolmanagerimpl))
				vector.add(new TpCallEventCriteriaResult(callnotification
						._mthif(), callnotification.getAssignmentId()));
		}

		return (TpCallEventCriteriaResult[]) vector
				.toArray(new TpCallEventCriteriaResult[0]);
	}

	CallNotification getCallNotification(int i) {
		return (CallNotification) notificationTable.get(new Integer(i));
	}

	CallNotification removeCallNotification(int i) {
		return (CallNotification) notificationTable.remove(new Integer(i));
	}

	CallNotification[] callContext(CallContext callcontext) {
		LinkedList linkedlist = new LinkedList();
		for (Iterator iterator = notificationTable.values().iterator(); iterator
				.hasNext();) {
			CallNotification callnotification = (CallNotification) iterator
					.next();
			if (callnotification.callContext(callcontext))
				linkedlist.add(callnotification);
		}

		return (CallNotification[]) linkedlist
				.toArray(new CallNotification[linkedlist.size()]);
	}

	public List findOverlapping(CallNotification callnotification) {
		LinkedList linkedlist = new LinkedList();
		for (Iterator iterator = notificationTable.values().iterator(); iterator
				.hasNext();) {
			CallNotification callnotification1 = (CallNotification) iterator
					.next();
			if (callnotification1.sameCallNotification(callnotification))
				linkedlist.add(callnotification1);
		}

		return linkedlist;
	}

	public List addCallNotification(CallNotification callnotification) {
		List list = findOverlapping(callnotification);
		if (list.size() == 0)
			notificationTable.put(new Integer(callnotification
					.getAssignmentId()), callnotification);
		return list;
	}

	public void clearState() {
		m_logger.info("CallNotifications::clearState()");
		synchronized (notificationTable) {
			CallNotification callnotification;
			for (Iterator iterator = notificationTable.values().iterator(); iterator
					.hasNext(); callnotification.reset())
				callnotification = (CallNotification) iterator.next();

			notificationTable.clear();
		}
	}

	private static Logger m_logger = Logger.getLogger(CallNotifications.class);

	Map notificationTable;
}
