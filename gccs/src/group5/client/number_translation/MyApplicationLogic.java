//$Id: MyApplicationLogic.java,v 1.13 2005/07/09 10:03:27 aachenner Exp $
/**
 * 
 */
package group5.client.number_translation;

import group5.client.ApplicationFramework;

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
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * 
 * @author Nguyen Huu Hoa
 * 
 */
public class MyApplicationLogic {
	MyAppEventQueue osaEventQueue;

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
		osaEventQueue = new MyAppEventQueue();
		ipCCM = ipCCM_param;
		number = "?";
	}

	public void run() {
		m_logger.info("Start monitoring number: " + number);
		int assignmentID = monitorOrigNumbers(ipCCM, new AppCallControlManager(
				this), number);
		m_logger.info("Entering loop with assignmentID: " + assignmentID);
		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					// setCallback
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
					m_logger.debug("Waiting for routeRes ...");
					osaEventQueue.get();
					m_logger.debug("Got response for routeRes ...");
					//then deassign the call
					doDeassignCall(callId);			
					
					
					// wait for network events
					m_logger.debug("Inside run method");
					while (true) {
						MyAppEvent event = osaEventQueue.get();
						// got event
						m_logger
								.debug("Got event with eventID = "
										+ event.eventInfo.CallEventName
										+ ", from address "
										+ event.eventInfo.OriginatingAddress.AddrString);
						// check event
						if (event.eventInfo.CallEventName == P_EVENT_GCCS_ADDRESS_ANALYSED_EVENT.value) {
							// translate the address
							String addrString = translateModulo10(event.eventInfo.DestinationAddress.AddrString);
							// route to new address
							doRouteReq(event, addrString);
							// deassign from call
							doDeassignCall(event.callId);
						} else {
							m_logger.info("Unknown event");
						}
					}
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
		th.start();
		try {
			m_logger.debug("Entering dead");
			System.in.read();
			m_logger.debug("disabling CallNofitication");
			ipCCM.disableCallNotification(assignmentID);
			th.stop();
			m_logger.debug("Application exit");
		} catch (IOException ex) {

		} catch (P_INVALID_ASSIGNMENT_ID ex) {

		} catch (TpCommonExceptions ex) {

		}
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
	
	public void callEventNotify(TpCallIdentifier callReference,
			TpCallEventInfo eventInfo, int assignmentID) {
		m_logger.debug("Entering callEventNotify");
		osaEventQueue
				.put(new MyAppEvent(callReference, eventInfo, assignmentID));
		m_logger.debug("Exiting callEventNotify");
	}

	public void routeRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		m_logger.debug("Result of routeReq has come");
		osaEventQueue.put(new MyAppEvent(null, null, 0));
		m_logger.debug("exiting routeRes ...");
		//m_logger.error("Not implemented");
	}

	private void doRouteReq(MyAppEvent event, String newDestination) {
		try {
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
		// if (addressToTranslate.compareTo("123456789021") == 0) {
		// m_logger.info("Route to 000180074992");
		return "4";
		// }
		// try {
		// String lastAddress = addressToTranslate.substring(addressToTranslate
		// .length() - 2, addressToTranslate.length());
		//
		// m_logger.info("lastAddress = " + lastAddress);
		// int addrInt = Integer.parseInt(lastAddress);
		// if (addrInt < 20)
		// return "000180074992";
		// else
		// return addressToTranslate;
		// } catch (NumberFormatException ex) {
		// m_logger.error("Invalid number format " + ex.getMessage());
		// } catch (StringIndexOutOfBoundsException ex) {
		// m_logger.fatal("String index out of bounds. " + ex.getMessage());
		// }
		// return addressToTranslate;
	}

}
