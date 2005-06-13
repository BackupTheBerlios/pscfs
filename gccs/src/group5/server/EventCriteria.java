//$Id: EventCriteria.java,v 1.1 2005/06/13 11:17:51 huuhoa Exp $
/**
 * 
 */
package group5.server;

import java.util.List;
import java.util.ListIterator;

/**
 * List of events to be watched by observer
 * @author Nguyen Huu Hoa
 *
 */
public class EventCriteria {
	private List m_eventList;
	public EventCriteria()
	{
		
	}
	/**
	 * Add new criteria for watching
	 */
	public void addCriteria(int eventID)
	{
		m_eventList.add(new Integer(eventID));
	}
	public void removeCriteria(int eventID)
	{
		ListIterator iterator = m_eventList.listIterator();
		while (iterator.hasNext())
		{
			Integer tempEventID = (Integer) iterator.next();
			if (tempEventID.intValue() == eventID)
				iterator.remove();
		}
	}
	/**
	 * This function is used to check whether the given eventID
	 * is contained in the list or not
	 */
	public boolean isWatched(int eventID)
	{
		ListIterator iterator = m_eventList.listIterator();
		while (iterator.hasNext())
		{
			Integer tempEventID = (Integer) iterator.next();
			if (tempEventID.intValue() == eventID)
				return true;
		}
		return false;
	}
}
