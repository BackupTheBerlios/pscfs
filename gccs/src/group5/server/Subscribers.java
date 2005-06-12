//$Id: Subscribers.java,v 1.2 2005/06/12 22:24:04 huuhoa Exp $
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

	private static int subscriberID = 0;

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
	 * @return subscriberID associated with newly added subscriber
	 */
	public synchronized int addSubscriber(String subscriberAddress) {
		Subscriber subscr = new Subscriber(subscriberAddress);
		int subID = getSubscriberID();
		m_listSubscribers.put(new Integer(subID), subscr);
		notifyAll();
		return subID;
	}

	/**
	 * Return the subscriber associated with subscriber ID
	 * 
	 * @param subscrID
	 *            ID of the subscriber
	 */
	public synchronized Subscriber getSubscriber(int subscrID) {
		Subscriber sub = (Subscriber) m_listSubscribers.get(new Integer(
				subscrID));
		return sub;
	}

	/**
	 * Get new subscriber ID, unique in the whole application
	 */
	private synchronized int getSubscriberID() {
		subscriberID++;
		return subscriberID;
	}
}
