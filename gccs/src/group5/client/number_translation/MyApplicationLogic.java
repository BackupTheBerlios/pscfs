//$Id: MyApplicationLogic.java,v 1.20 2005/07/28 23:45:22 aachenner Exp $
/**
 * 
 */
package group5.client.number_translation;

import group5.client.ApplicationFramework;
import group5.client.ApplicationEvent;
import group5.client.ApplicationEventQueue;

import java.io.IOException;

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
import org.csapi.cc.gccs.TpCallReportType;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class MyApplicationLogic {
	ApplicationEventQueue osaEventQueue;

	IpCallControlManager ipCCM;

	String number;

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(MyApplicationLogic.class);
	}

	MyApplicationLogic(IpCallControlManager ipCCM_param) {
		osaEventQueue = new ApplicationEventQueue();
		ipCCM = ipCCM_param;
		number = "?";
	}

	public void run() {
		m_logger.info("Start monitoring number: " + number);
		int assignmentID = monitorOrigNumbers(ipCCM, new AppCallControlManager(
				this), number);
		m_logger.info("Entering loop with assignmentID: " + assignmentID);

		// Thread to to createCall
		Thread th1 = new Thread(new Runnable() {
			public void run() {
				try {
					// setCallback

					m_logger
							.debug("Starting Number Translation with createCall");

					AppCallControlManager appCCM = new AppCallControlManager(
							MyApplicationLogic.this);
					// now get the reference so that it is registered with the
					// ORB properly
					IpAppCallControlManager ipAppCCM = IpAppCallControlManagerHelper
							.narrow(ApplicationFramework.getPOA()
									.servant_to_reference(appCCM));

					ipCCM.setCallback(ipAppCCM);

					// createCall in number_translation
					AppCall appCall = new AppCall(MyApplicationLogic.this);
					IpAppCall ipAppCall = IpAppCallHelper
							.narrow(ApplicationFramework.getPOA()
									.servant_to_reference(appCall));
					TpCallIdentifier callId = ipCCM.createCall(ipAppCall);
					if (callId == null) {
						m_logger
								.error("Cannot create call because the CallID = Null");
						return;
					}
					String origAddr = "1";
					String destAddr = "2";
					m_logger.debug("Got callSection: " + callId.CallSessionID
							+ ", CallIdentifier: "
							+ callId.CallReference.toString());
					// m_logger.debug("About to call routeReq ...");
					m_logger.info("About to RouteReq the 1st time...");
					doRouteReq(callId, origAddr, destAddr);
					// wait here until the result of routeReq come
					m_logger
							.debug("Waiting for routeRes after finishing the 1st RouteReq");
					ApplicationEvent callEvent = osaEventQueue.get(ApplicationEvent.evRouteRes);
					switch (callEvent.eventReport.CallReportType.value())
					{
					case TpCallReportType._P_CALL_REPORT_BUSY:
					case TpCallReportType._P_CALL_REPORT_NO_ANSWER:
					case TpCallReportType._P_CALL_REPORT_NOT_REACHABLE:
					case TpCallReportType._P_CALL_REPORT_REDIRECTED:
					case TpCallReportType._P_CALL_REPORT_ROUTING_FAILURE:
						m_logger.info("Call is redirected or routing failure");
						return;
					}
					m_logger.debug("Got response for routeRes");
					// m_logger.debug("About to call routeReq ...");
					// then call routeReq again with swapping the position of
					// source and destination

					m_logger.info("About to RouteReq the 2rd time...");
					doRouteReq(callId, destAddr, origAddr);
					// then wait again for the result of routeReq
					m_logger
							.debug("Waiting for routeRes after finishing the 2rd RouteReq");
					osaEventQueue.get(ApplicationEvent.evRouteRes);
					m_logger
							.debug("Finished routeReq & routeRes twice. About to DeassignCall...");
					// then deassign the call
					doDeassignCall(callId);
					// m_logger.debug("Finished thread 1: Create Call");
				} catch (ServantNotActive ex) {
					m_logger
							.fatal("Servant not active. Try activate servant first");
				} catch (WrongPolicy ex) {
					m_logger.fatal("Wrong policy");
				} catch (TpCommonExceptions ex) {
					m_logger.fatal("Common exception with extra information: "
							+ ex.ExtraInformation);
				} catch (P_INVALID_INTERFACE_TYPE ex) {
					m_logger
							.fatal("Invalid interface type with extra information: "
									+ ex.ExtraInformation);
				}
			}
		});

		// Thread to wait for network events
		Thread th2 = new Thread(new Runnable() {
			public void run() {
				// wait for network events
				m_logger.debug("Starting to wait for network events");

				while (true) {
					ApplicationEvent event = osaEventQueue
							.get(ApplicationEvent.evCallEventNotify);
					// got event
					m_logger.debug("Got event with event name = "
							+ event.eventInfo.CallEventName
							+ ", from the Originating address = "
							+ event.eventInfo.OriginatingAddress.AddrString
							+ ", assignmentID = " + event.assignmentID);
					// check event
					if (event.eventInfo.CallEventName == P_EVENT_GCCS_ADDRESS_ANALYSED_EVENT.value) {
						// translate the address
						String addrString = translateModulo10(event.eventInfo.DestinationAddress.AddrString);
						// route to new address
						m_logger.debug("Routing request to new address");
						doRouteReq(event, addrString);
						// deassign from call
						doDeassignCall(event.callId);
						// m_logger.info("Finished doDeassignCall in Thread 2");
					} else {
						m_logger.info("Unknown event");
					}

				}
			}
		});
		th2.start();
		th1.start();

		try {
			m_logger.debug("Entering dead");
			System.in.read();
			m_logger.debug("Disabling CallNofitication");
			ipCCM.disableCallNotification(assignmentID);
			th1.stop();
			th2.stop();
			m_logger.debug("Application exit");
		} catch (IOException ex) {

		} catch (P_INVALID_ASSIGNMENT_ID ex) {

		} catch (TpCommonExceptions ex) {

		}
	}

	private void doRouteReq(TpCallIdentifier callId, String originatingAddr,
			String newDestination) {
		m_logger
				.info("Entering doRouteReq with callSessionID = "
						+ callId.CallSessionID + ", originating Address = "
						+ originatingAddr + ", destination address = "
						+ newDestination);
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

	public void callEventNotify(TpCallIdentifier callReference,
			TpCallEventInfo eventInfo, int assignmentID) {
		m_logger.debug("Entering callEventNotify");
		osaEventQueue.put(new ApplicationEvent(
				ApplicationEvent.evCallEventNotify, callReference, eventInfo,
				assignmentID));
		// m_logger.debug("Exiting callEventNotify");
	}

	public void routeRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		m_logger.debug("Entering routeRes with call sessionID = "
				+ callSessionID);
		ApplicationEvent ev = new ApplicationEvent(ApplicationEvent.evRouteRes,
				null, null, 0);
		ev.eventReport = eventReport;
		osaEventQueue.put(ev);
	}

	private void doRouteReq(ApplicationEvent event, String newDestination) {
		try {

			m_logger.info("Entering doRouteReq with Event's CallSessionID = "
					+ event.callId.CallSessionID);

			event.callId.CallReference.routeReq(event.callId.CallSessionID,
					new TpCallReportRequest[0],
					myAppCreateE164Address(newDestination),
					event.eventInfo.OriginatingAddress,
					event.eventInfo.OriginalDestinationAddress,
					event.eventInfo.DestinationAddress,
					event.eventInfo.CallAppInfo);
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
		m_logger.info("Entering doDessignCall with CallSessionID = "
				+ callID.CallSessionID);
		try {
			callID.CallReference.deassignCall(callID.CallSessionID);
		} catch (P_INVALID_SESSION_ID ex) {
			m_logger.error("Invalid session ID exception, more: "
					+ ex.getMessage());
		} catch (TpCommonExceptions ex) {
			m_logger.error("Catch OSA exception, number: " + ex.ExceptionType);
		}
	}

	private int monitorOrigNumbers(IpCallControlManager mgr,
			AppCallControlManager appMgr, String originating_address) {
		TpCallEventCriteria ec = createOrigEventCriteria(originating_address,
				new String("*"), P_EVENT_GCCS_ADDRESS_ANALYSED_EVENT.value);
		int assignment = 0;
		m_logger.info("Calling enableCallNofitication()");
		try {
			assignment = mgr.enableCallNotification(appMgr.getServant(), ec);
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

	TpCallEventCriteria createOrigEventCriteria(String originating,
			String destination, int event_num) {
		TpCallEventCriteria ec = new TpCallEventCriteria();
		ec.DestinationAddress = myAppCreateE164AddressRange(destination);
		ec.OriginatingAddress = myAppCreateE164AddressRange(originating);
		ec.CallEventName = event_num;
		ec.CallNotificationType = TpCallNotificationType.P_ORIGINATING;
		ec.MonitorMode = TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT;
		return ec;
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

	String translateModulo10(String addressToTranslate) {
		m_logger.info("Address to be translated: " + addressToTranslate);
		return "4";

	}

}
