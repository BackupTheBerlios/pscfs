//$Id: EventCriteria.java,v 1.4 2005/07/27 08:47:08 huuhoa Exp $
/**
 * 
 */
package group5.server;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * List of events to be watched by observer
 * 
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class EventCriteria {
	private List m_eventList;

	public EventCriteria() {
		m_eventList = new ArrayList();
	}

	/**
	 * Add new criteria for watching
	 */
	public void addCriteria(int eventID) {
		m_eventList.add(new Integer(eventID));
	}

	public void removeCriteria(int eventID) {
		ListIterator iterator = m_eventList.listIterator();
		while (iterator.hasNext()) {
			Integer tempEventID = (Integer) iterator.next();
			if (tempEventID.intValue() == eventID)
				iterator.remove();
		}
	}

	/**
	 * This function is used to check whether the given eventID is contained in
	 * the list or not
	 */
	public boolean isWatched(int eventID) {
		ListIterator iterator = m_eventList.listIterator();
		while (iterator.hasNext()) {
			Integer tempEventID = (Integer) iterator.next();
			if (tempEventID.intValue() == eventID)
				return true;
		}
		return false;
	}

	public String toString() {
		String strObject = "Event Criteria: ";
		ListIterator iterator = m_eventList.listIterator();
		while (iterator.hasNext()) {
			Integer tempEventID = (Integer) iterator.next();
			strObject += tempEventID.intValue();
			strObject += ", ";
		}
		return strObject;
	}
}
