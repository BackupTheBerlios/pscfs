//$Id: MyAppEventQueue.java,v 1.2 2005/06/12 22:24:04 huuhoa Exp $
/**
 * 
 */
package group5.client.number_translation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Nguyen Huu Hoa
 * 
 */
public class MyAppEventQueue {
	List list;

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(MyAppEventQueue.class);
	}

	public MyAppEventQueue() {
		list = new ArrayList();
	}

	public synchronized void put(MyAppEvent event) {
		list.add(event);
		notifyAll();
	}

	public synchronized MyAppEvent get() {
		if (size() == 0)
			do {
				try {
					wait();
				} catch (Exception e) {
					m_logger.error(e);
				}
			} while (size() == 0);
		return (MyAppEvent) list.remove(0);
	}

	public synchronized int size() {
		return list.size();
	}
}
