//$Id: MyApplicationLogic.java,v 1.7 2005/06/23 22:53:41 huuhoa Exp $
/**
 * 
 */
package group5.client.number_translation;

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
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.P_EVENT_GCCS_ADDRESS_ANALYSED_EVENT;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallNotificationType;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportRequest;

/**
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
		number = "1234567890??";
	}

	public void run() {
		m_logger.info("Start monitoring number: " + number);
		int assignmentID = monitorOrigNumbers(ipCCM, new AppCallControlManager(
				this), number);
		m_logger.info("Entering loop");
		Thread th = new Thread(new Runnable() {
			public void run() {
				// wait for network events
				MyAppEvent event = osaEventQueue.get();
				// got event
				m_logger.debug("Got event with eventID = "
						+ event.eventInfo.CallEventName + ", from address "
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
		});
		th.run();
		try {
			System.in.read();
			ipCCM.disableCallNotification(assignmentID);
			th.stop();
		} catch (IOException ex) {

		} catch (P_INVALID_ASSIGNMENT_ID ex) {

		} catch (TpCommonExceptions ex) {

		}
	}

	public void callEventNotify(TpCallIdentifier callReference,
			TpCallEventInfo eventInfo, int assignmentID) {
		m_logger.info("Call event notify");
		osaEventQueue
				.put(new MyAppEvent(callReference, eventInfo, assignmentID));
	}

	public void routeRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		m_logger.error("Not implemented");
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
		return "000180074992";
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
