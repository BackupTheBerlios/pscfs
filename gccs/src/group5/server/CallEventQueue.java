//$Id: CallEventQueue.java,v 1.11 2005/07/27 08:47:08 huuhoa Exp $
package group5.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
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
		list = new ArrayList();
	}

	public synchronized void put(CallEvent event) {
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
