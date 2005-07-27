//$Id: ApplicationEventQueue.java,v 1.4 2005/07/27 08:47:09 huuhoa Exp $
/**
 * 
 */
package group5.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
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

	public synchronized ApplicationEvent get(int eventType) {
		while (true) {
			// check for event criteria here
			for (Iterator it = list.listIterator(); it.hasNext();) {
				ApplicationEvent ev = (ApplicationEvent) it.next();
				if (ev.eventType == eventType) {
					it.remove();
					return ev;
				}
			}
			try {
				wait();
			} catch (Exception e) {
				m_logger.error(e);
			}
		}
	}

	public synchronized int size() {
		return list.size();
	}
}
