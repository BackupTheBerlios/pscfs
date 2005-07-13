//$Id: EventObserver.java,v 1.11 2005/07/13 20:45:10 huuhoa Exp $
/**
 * 
 */
package group5.server;

import java.util.HashMap;
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
 * 
 * <pre>
 * evObserver = EventObserver.getInstance();
 * </pre>
 * 
 * <li>Set appropriated watcher by calling addWatcher()
 * <li>Start the listener
 * 
 * <pre>
 * evObserver.listen();
 * </pre>
 * 
 * </ol>
 * 
 * @author Nguyen Huu Hoa
 * 
 */
public final class EventObserver {

	private static Logger m_logger = Logger.getLogger(EventObserver.class);

	private static EventObserver m_eventObserver = null;

	public static EventObserver getInstance() {
		if (m_eventObserver == null) {
			m_eventObserver = new EventObserver();
		}
		return m_eventObserver;
	}

	private EventListener evListener;

	private EventObserver() {
		m_logger.debug("ctor()");
//		m_ipCallManager = null;
		m_mapObservers = new HashMap();
		evListener = new EventListener();
	}

//	// begin actual observer implementation
//	private CallControlAdapter m_ipCallManager;
//
//	public void SetIpCallControlManager(CallControlAdapter ipCallManager) {
//		m_ipCallManager = ipCallManager;
//	}

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

	private boolean bWatcherSafe = true;

	private synchronized boolean isWatcherSafe() {
		return this.bWatcherSafe;
	}

	private synchronized void setWatcherFlag(boolean bFlag) {
		bWatcherSafe = bFlag;
		notifyAll();
	}

	public synchronized int getWatcherID() {
		watcherID++;
		return watcherID;
	}

	public synchronized int addWatcher(IpEventHandler eventHandler,
			EventCriteria eventCriteria) {
		Observer newObserver = new Observer(eventHandler, eventCriteria);
		int nWatcherID = getWatcherID();
		while (isWatcherSafe() == false) {
			try {
				wait();
			} catch (Exception ex) {
				m_logger.error(ex);
			}
		}
		m_mapObservers.put(new Integer(nWatcherID), newObserver);
		return nWatcherID;
	}

	public synchronized void removeWatcher(int nWatcherID) {
		while (isWatcherSafe() == false) {
			try {
				wait();
			} catch (Exception ex) {
				m_logger.error(ex);
			}
		}
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

	public void stop() {
		evListener.stop();
	}

	private class EventListener extends Thread {
		public void run() {
			CallEventQueue evQueue = CallEventQueue.getInstance();
			while (true) {
				CallEvent ev = evQueue.get();
				// first examine the bProvision field of CallEvent
				boolean bStop = false;
//				if (ev.isProvision()) {
//					if (m_ipCallManager != null) {
//						m_logger.debug("dispatching event to ip call manager");
//						bStop = m_ipCallManager.onEvent(ev.eventType, ev);
//					}
//				}
				if (bStop == false) {
					ev.setProvision(false);
					setWatcherFlag(false);
					// dispatch events
					m_logger.debug("Got event with eventType: " + ev.eventType);
					m_logger.debug("Number of Observers: "
							+ m_mapObservers.size());
					Iterator iterator = m_mapObservers.values().iterator();
					while (iterator.hasNext()) {
						Observer ob = (Observer) iterator.next();
						m_logger.debug("The watched criteria: "
								+ ob.getCriteria().toString());
						if (ob.getCriteria().isWatched(ev.eventType)) {
							dispatchEvent(ob.getHandler(), ev.eventType, ev);
						}
					}
					setWatcherFlag(true);
				}
			}
		}

		private void dispatchEvent(IpEventHandler handler, int eventType,
				CallEvent eventData) {
			m_logger.debug("Dispatching event to hander: " + handler
					+ ", eventType: " + eventType);
			// get data
			switch (eventType) {
			case CallEvent.eventRouteReq:
				// Event route request
				handler.onRouteReq(eventData.CallSessionID, eventData
						.getTargetAddress(), eventData.originatingAddress);
				break;
			case CallEvent.eventDeassignCall:
				handler.onDeassignCall(eventData.CallSessionID);
				break;
			case CallEvent.eventReleaseCall:
				handler.onReleaseCall(eventData.CallSessionID);
				break;
			case CallEvent.eventRouteRes:
				handler.onRouteRes(eventData.CallSessionID,
						eventData.eventReport, eventData.callLegSessionID);
			default:
				handler.onEvent(eventType, eventData);
				break;
			}

		}
	}
}
