//$Id: Subscribers.java,v 1.8 2005/07/27 08:47:09 huuhoa Exp $
/**
 * 
 */
package group5.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Collection of subscribers. This collection works as a database for collecting
 * all subscribers in the system. Service can access this databse through
 * functions:
 * <ol>
 * <li>Subscribers.getInstance() to get access to instance of the collection
 * <li>addSubscriber() to add new subscriber to collection
 * <li>getSubscriber() to get subscriber in collection associated given a
 * subscriber ID
 * </ol>
 * 
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class Subscribers {
	Map m_listSubscribers;

	private static Subscribers m_subscribers = new Subscribers();

	public static Subscribers getInstance() {
		if (m_subscribers == null) {
			m_subscribers = new Subscribers();
		}
		return m_subscribers;
	}

	private Subscribers() {
		m_listSubscribers = new HashMap();
	}

	/**
	 * Add new subscriber, given an address of that subscriber
	 * 
	 * @param subscriberAddress
	 *            Address of the new subscriber
	 */
	public void addSubscriber(String subscriberAddress) {
		Subscriber subscr = new Subscriber(subscriberAddress);
		m_listSubscribers.put(subscriberAddress, subscr);
	}

	/**
	 * Return the subscriber associated with subscriber ID
	 * 
	 * @param subscriberAddr
	 *            ID of the subscriber
	 */
	public Subscriber getSubscriber(String subscriberAddr) {
		Subscriber sub = (Subscriber) m_listSubscribers.get(subscriberAddr);
		return sub;
	}

	/**
	 * Dumping the subscribers database
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String strDumping = "Subscribers Database\n";
		Iterator iterator = m_listSubscribers.values().iterator();
		while (iterator.hasNext()) {
			Subscriber sub = (Subscriber) iterator.next();
			strDumping = strDumping + "subscriber address: "
					+ sub.getSubscribeAddress() + "\n";
		}
		return strDumping;
	}
}
