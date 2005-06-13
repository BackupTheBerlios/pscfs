//$Id: EventObserver.java,v 1.2 2005/06/13 12:34:25 huuhoa Exp $
/**
 * 
 */
package group5.server;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class is used to observe the event pool (CallEventQueue). Everytime an
 * event arrived, it will get the event and dispatch to appropriated event
 * handler.<br>
 * This observer can be used by IpCall to watch for result of routeReq, or by
 * call simulator to watch for request on making calls, or by
 * IpCallControlManager to get notification about the calls.<br>
 * <b>Usage:</b><br>
 * <ol>
 * <li>Get existing instance of EventObserver
 * <pre>evObserver = EventObserver.getInstance();</pre>
 * <li>Set appropriated watcher by calling setWatcher()
 * <li>Start the listener
 * <pre>evObserver.listen();</pre>
 * </ol>
 * @author Nguyen Huu Hoa
 * 
 */
public class EventObserver {

	private static Logger m_logger = Logger.getLogger(EventObserver.class);

	private static EventObserver m_eventObserver = null;

	public static EventObserver getInstance() {
		if (m_eventObserver == null)
		{
			m_eventObserver = new EventObserver();
		}
		return m_eventObserver;
	}
	private EventListener evListener;

	private EventObserver() {
		m_logger.debug("ctor()");
		evListener = new EventListener();
	}

	// begin actual observer implementation
	Map m_mapObservers;

	private class Observer {
		private IpEventHandler handler;

		private EventCriteria criteria;

		public Observer(IpEventHandler handler, EventCriteria criteria) {
			this.handler = handler;
			this.criteria = criteria;
		}

		public IpEventHandler getHandler() {
			return handler;
		}

		public EventCriteria getCriteria() {
			return criteria;
		}
	}

	private static int watcherID = 0;

	public synchronized int getWatcherID() {
		watcherID++;
		return watcherID;
	}

	public synchronized void setWatcher(IpEventHandler eventHandler,
			EventCriteria eventCriteria) {
		Observer newObserver = new Observer(eventHandler, eventCriteria);
		int nWatcherID = getWatcherID();
		m_mapObservers.put(new Integer(nWatcherID), newObserver);
	}

	public synchronized void removeWatcher(int nWatcherID) {
		m_mapObservers.remove(new Integer(nWatcherID));
	}

	/**
	 * This function will watch for events and dispatch them to appropriated
	 * listeners
	 */
	public void listen() {
		// start the listener
		evListener.start();
	}

	public void stop()
	{
		evListener.stop();
	}
	private class EventListener extends Thread {
		public void run() {
			CallEventQueue evQueue = CallEventQueue.getInstance();
			while (true) {
				CallEvent ev = evQueue.get();
				// dispatch events
				m_logger.debug("Got event with eventType: " + ev.eventType);
				Iterator iterator = m_mapObservers.values().iterator();
				while (iterator.hasNext()) {
					Observer ob = (Observer) iterator.next();
					if (ob.getCriteria().isWatched(ev.eventType)) {
						ob.getHandler().onEvent(ev.eventType, ev);
					}
				}
			}
		}
	}
}
