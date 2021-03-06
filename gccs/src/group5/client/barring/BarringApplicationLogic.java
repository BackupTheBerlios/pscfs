//$Id: BarringApplicationLogic.java,v 1.6 2005/07/27 08:47:09 huuhoa Exp $
/**
 * 
 */
package group5.client.barring;

import java.io.IOException;

import group5.client.ApplicationEvent;
import group5.client.ApplicationEventQueue;
import group5.client.ApplicationFramework;

import org.apache.log4j.Logger;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddress;
import org.csapi.TpAddressPlan;
import org.csapi.TpAddressPresentation;
import org.csapi.TpAddressRange;
import org.csapi.TpAddressScreening;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallMonitorMode;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.IpAppCallControlManagerHelper;
import org.csapi.cc.gccs.IpAppCallHelper;
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.P_EVENT_GCCS_ADDRESS_ANALYSED_EVENT;
import org.csapi.cc.gccs.TpCallAppInfo;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallNotificationType;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportRequest;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class BarringApplicationLogic {
	ApplicationEventQueue osaEventQueue;

	IpCallControlManager ipCCM;

	String number;

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(BarringApplicationLogic.class);
	}

	BarringApplicationLogic(IpCallControlManager ipCCM_param) {
		osaEventQueue = new ApplicationEventQueue();
		ipCCM = ipCCM_param;
		number = "1234567890??";
	}

	public synchronized void run() {
		m_logger.info("Start monitoring number: " + number);
		int assignmentID = monitorOrigNumbers(ipCCM,
				new BarringAppCallControlManager(this), number);
		m_logger.info("Entering loop with assignmentID: " + assignmentID);

		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					BarringAppCallControlManager appCCM = new BarringAppCallControlManager(
							BarringApplicationLogic.this);
					// now get the reference so that it is registered with the
					// ORB properly
					IpAppCallControlManager ipAppCCM = IpAppCallControlManagerHelper
							.narrow(ApplicationFramework.getPOA()
									.servant_to_reference(appCCM));

					ipCCM.setCallback(ipAppCCM);

					// createCall in call Berring
					BarringAppCall appCall = new BarringAppCall(
							BarringApplicationLogic.this);
					IpAppCall ipAppCall = IpAppCallHelper
							.narrow(ApplicationFramework.getPOA()
									.servant_to_reference(appCall));
					TpCallIdentifier callId = ipCCM.createCall(ipAppCall);
					if (callId == null) {
						m_logger.error("Cannot create call");
						return;
					}
					String origAddr = "1";
					String destAddr = "2";

					m_logger.debug("Got callSection: " + callId.CallSessionID
							+ ", CallIdentifier: "
							+ callId.CallReference.toString());
					m_logger.debug("About to call routeReq ...");
					doRouteReq(callId, origAddr, destAddr);
					// wait here until the result of routeReq come
					m_logger.debug("Waiting for routeRes ...");
					osaEventQueue.get(ApplicationEvent.evRouteRes);
					// then deassign the call
					m_logger.debug("Entering deassignCall");
					doDeassignCall(callId);
					m_logger.debug("The end of initCall");
				} catch (P_INVALID_INTERFACE_TYPE ex) {
					m_logger
							.fatal("Why invalid interface type??? Extra information: "
									+ ex.ExtraInformation);
				} catch (TpCommonExceptions ex) {
					m_logger.fatal("Some error occurs with information: "
							+ ex.ExtraInformation);
				} catch (ServantNotActive ex) {
					m_logger.fatal("Try to activate POA first");
				} catch (WrongPolicy ex) {
					m_logger.fatal("Wrong policy");
				}
			}

		});
		th.start();
		try {
			m_logger.debug("Entering dead");
			System.in.read();
			m_logger.debug("Disable Call Notification");
			ipCCM.disableCallNotification(assignmentID);
			m_logger.debug("Application exit");
		} catch (TpCommonExceptions ex) {
		} catch (P_INVALID_ASSIGNMENT_ID ex) {
		} catch (IOException ex) {

		}
	}

	private int monitorOrigNumbers(IpCallControlManager ipCCM2,
			BarringAppCallControlManager manager, String number2) {
		TpCallEventCriteria ec = createOrigEventCriteria(number2, new String(
				"*"), P_EVENT_GCCS_ADDRESS_ANALYSED_EVENT.value);
		int assignment = 0;
		m_logger.info("Calling enableCallNofitication()");
		try {
			assignment = ipCCM2
					.enableCallNotification(manager.getServant(), ec);
		} catch (P_INVALID_INTERFACE_TYPE ex) {
			m_logger.error("Invalid interface type: " + ex.getMessage());
		} catch (P_INVALID_EVENT_TYPE ex) {
			m_logger.error("Invalid event type: " + ex.getMessage());
		} catch (P_INVALID_CRITERIA ex) {
			m_logger.error("Invalid criteria: " + ex.getMessage());
		} catch (TpCommonExceptions ex) {
			m_logger.error("Error in calling enableCallNofitication: "
					+ ex.getMessage());
		}
		return assignment;
	}

	public void callEventNotify(TpCallIdentifier callReference,
			TpCallEventInfo eventInfo, int assignmentID) {
		m_logger.info("Call event notify");
		osaEventQueue.put(new ApplicationEvent(
				ApplicationEvent.evCallEventNotify, callReference, eventInfo,
				assignmentID));
	}

	public void routeRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		m_logger.debug("Result of routeReq has come");
		osaEventQueue.put(new ApplicationEvent(ApplicationEvent.evRouteRes,
				null, null, 0));
		m_logger.debug("exiting routeRes ...");
	}

	private void doRouteReq(TpCallIdentifier callId, String originatingAddr,
			String newDestination) {
		try {
			callId.CallReference.routeReq(callId.CallSessionID,
					new TpCallReportRequest[0],
					myAppCreateE164Address(newDestination),
					myAppCreateE164Address(originatingAddr),
					myAppCreateE164Address(newDestination),
					myAppCreateE164Address(newDestination),
					new TpCallAppInfo[0]);
		} catch (P_INVALID_SESSION_ID ex) {
			m_logger.error("Invalid session id, more " + ex.getMessage());
		} catch (P_UNSUPPORTED_ADDRESS_PLAN ex) {
			m_logger.error("Unsupported address plan, more " + ex.getMessage());
		} catch (P_INVALID_CRITERIA ex) {
			m_logger.error("Invalid criteria, more " + ex.getMessage());
		} catch (P_INVALID_ADDRESS ex) {
			m_logger.error("Invalid address, more " + ex.getMessage());
		} catch (P_INVALID_NETWORK_STATE ex) {
			m_logger.error("Invalid network state, more " + ex.getMessage());
		} catch (P_INVALID_EVENT_TYPE ex) {
			m_logger.error("Invalid event type, more " + ex.getMessage());
		} catch (TpCommonExceptions ex) {
			m_logger.error("Catch OSA exception, number: " + ex.ExceptionType);
		}
	}

	private void doDeassignCall(TpCallIdentifier callID) {
		try {
			callID.CallReference.deassignCall(callID.CallSessionID);
		} catch (P_INVALID_SESSION_ID ex) {
			m_logger.error("Invalid session ID exception, more: "
					+ ex.getMessage());
		} catch (TpCommonExceptions ex) {
			m_logger.error("Catch OSA exception, number: " + ex.ExceptionType);
		}
	}

	TpCallEventCriteria createOrigEventCriteria(String originating,
			String destination, int event_num) {
		TpCallEventCriteria evenCriteria = new TpCallEventCriteria();
		evenCriteria.DestinationAddress = myAppCreateE164AddressRange(destination);
		evenCriteria.OriginatingAddress = myAppCreateE164AddressRange(originating);
		evenCriteria.CallEventName = event_num;
		evenCriteria.CallNotificationType = TpCallNotificationType.P_ORIGINATING;
		evenCriteria.MonitorMode = TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT;
		return evenCriteria;
	}

	TpAddress myAppCreateE164Address(String address) {
		TpAddress addr = new TpAddress();
		addr.Plan = TpAddressPlan.P_ADDRESS_PLAN_E164;
		addr.Presentation = TpAddressPresentation.P_ADDRESS_PRESENTATION_ALLOWED;
		addr.Screening = TpAddressScreening.P_ADDRESS_SCREENING_USER_VERIFIED_PASSED;
		addr.AddrString = new String(address);
		addr.Name = new String("");
		addr.SubAddressString = new String("");
		return addr;
	}

	TpAddressRange myAppCreateE164AddressRange(String address) {
		TpAddressRange addr = new TpAddressRange();
		addr.Plan = TpAddressPlan.P_ADDRESS_PLAN_E164;
		addr.AddrString = new String(address);
		addr.Name = new String("");
		addr.SubAddressString = new String("");
		return addr;
	}
}