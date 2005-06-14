//$Id: Subscribers.java,v 1.4 2005/06/14 08:15:31 huuhoa Exp $
/**
 * 
 */
package group5.server;

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
 * @author Nguyen Huu Hoa
 * 
 */
public class Subscribers {
	Map m_listSubscribers;

	private static Subscribers m_subscribers = new Subscribers();

	public static Subscribers getInstance() {
		return m_subscribers;
	}

	private Subscribers() {

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
	 * @param subscrID
	 *            ID of the subscriber
	 */
	public Subscriber getSubscriber(String subscriberAddr) {
		Subscriber sub = (Subscriber) m_listSubscribers.get(subscriberAddr);
		return sub;
	}
}
