//$Id: ApplicationEventQueue.java,v 1.2 2005/07/09 13:20:33 aachenner Exp $
/**
 * 
 */
package group5.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Nguyen Huu Hoa
 * 
 */
public class ApplicationEventQueue {
	List list;

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(ApplicationEventQueue.class);
	}

	public ApplicationEventQueue() {
		list = new ArrayList();
	}

	public synchronized void put(ApplicationEvent event) {
		list.add(event);
		notifyAll();
	}

	public synchronized ApplicationEvent[] get(int[] eventType) {
		if (size() == 0) {
			do {
				try {
					wait();
				} catch (Exception e) {
					m_logger.error(e);
				}
			} while (size() == 0);
		}
		// check for event criteria here
		return new ApplicationEvent[] {(ApplicationEvent)list.remove(0)};
	}

	public synchronized int size() {
		return list.size();
	}
}
