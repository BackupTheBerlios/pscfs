//$Id: CallEventQueue.java,v 1.9 2005/07/13 19:55:33 hoanghaiham Exp $
package group5.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Nguyen Duc Du Khuong
 * 
 */

public final class CallEventQueue {

	List list;

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;
	static {
		m_logger = Logger.getLogger(CallEventQueue.class);
	}

	static CallEventQueue m_evtQueue = new CallEventQueue();

	public static CallEventQueue getInstance() {
		return m_evtQueue;
	}

	public CallEventQueue() {
		m_logger.debug("Creating new Event Queue");
		list = new ArrayList();
	}

	public synchronized void put(CallEvent event) {
		m_logger.debug("Entering callEvent");
		list.add(event);
		notifyAll();
	}

	public synchronized CallEvent get() {
		if (size() == 0)
			do {
				try {
					wait();
				} catch (Exception e) {
					m_logger.error(e);
				}
			} while (size() == 0);
		return (CallEvent) list.remove(0);
	}

	public synchronized int size() {
		return list.size();
	}
}
