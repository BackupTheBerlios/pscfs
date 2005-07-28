//$Id: AIC_AppCall.java,v 1.5 2005/07/28 23:08:39 hoanghaiham Exp $
/**
 * 
 */
package group5.client.appinitcall;

import org.apache.log4j.Logger;
import org.csapi.cc.TpCallError;
import org.csapi.cc.gccs.IpAppCallPOA;
import org.csapi.cc.gccs.TpCallEndedReport;
import org.csapi.cc.gccs.TpCallFault;
import org.csapi.cc.gccs.TpCallInfoReport;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportType;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class AIC_AppCall extends IpAppCallPOA {
	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(AIC_AppCall.class);
	}

	MyApplicationLogic appLogic;

	public AIC_AppCall(MyApplicationLogic logic) {
		appLogic = logic;
	}

	public void routeRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		m_logger.debug("CallSessionID: " + callSessionID);
		String MsgErr;
		switch(eventReport.CallReportType.value()){
			case TpCallReportType._P_CALL_REPORT_UNDEFINED: 
				MsgErr= "Call report undefine"; break;
			case TpCallReportType._P_CALL_REPORT_PROGRESS: 
				MsgErr= "Call report progress"; break;
			case TpCallReportType._P_CALL_REPORT_ALERTING: 
				MsgErr= "Call report alerting"; break;
			case TpCallReportType._P_CALL_REPORT_ANSWER: 
				MsgErr= "Destination partner answer the call"; break;
			case TpCallReportType._P_CALL_REPORT_BUSY: 
				MsgErr= "Destination partner is busy"; break;
			case TpCallReportType._P_CALL_REPORT_NO_ANSWER: 
				MsgErr= "Destination partner no anwser the call"; break;
			case TpCallReportType._P_CALL_REPORT_DISCONNECT:
				MsgErr= "Call report disconnect"; break;
			case TpCallReportType._P_CALL_REPORT_REDIRECTED:
				MsgErr= "Call report redirected"; break;
			case TpCallReportType._P_CALL_REPORT_SERVICE_CODE:
				MsgErr= "Call report service code"; break;
			case TpCallReportType._P_CALL_REPORT_ROUTING_FAILURE: 
				MsgErr= "Call report no answer"; break;
			case TpCallReportType._P_CALL_REPORT_QUEUED: 
				MsgErr= "Call report queued"; break;
			case TpCallReportType._P_CALL_REPORT_NOT_REACHABLE: 
				MsgErr= "Destination partner is not reachable"; break;
			default: MsgErr= "Call report unknow";
		}
		m_logger.debug(MsgErr);
		appLogic.routeRes(callSessionID, eventReport, callLegSessionID);

	}

	public void routeErr(int callSessionID, TpCallError errorIndication,
			int callLegSessionID) {
		m_logger.info("Unimplemented function");
	}

	public void getCallInfoRes(int callSessionID,
			TpCallInfoReport callInfoReport) {
		m_logger.info("Unimplemented function");
	}

	public void getCallInfoErr(int callSessionID, TpCallError errorIndication) {
		m_logger.info("Unimplemented function");
	}

	public void superviseCallRes(int callSessionID, int report, int usedTime) {
		m_logger.info("Unimplemented function");
	}

	public void superviseCallErr(int callSessionID, TpCallError errorIndication) {
		m_logger.info("Unimplemented function");
	}

	public void callFaultDetected(int callSessionID, TpCallFault fault) {
		m_logger.info("Unimplemented function");
	}

	public void getMoreDialledDigitsRes(int callSessionID, String digits) {
		m_logger.info("Unimplemented function");
	}

	public void getMoreDialledDigitsErr(int callSessionID,
			TpCallError errorIndication) {
		m_logger.info("Unimplemented function");
	}

	public void callEnded(int callSessionID, TpCallEndedReport report) {
		m_logger.info("Unimplemented function");
	}
}
