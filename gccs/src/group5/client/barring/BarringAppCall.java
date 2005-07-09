//$Id: BarringAppCall.java,v 1.1 2005/07/09 10:28:46 hoanghaiham Exp $
/**
 * 
 */
package group5.client.barring;

import org.apache.log4j.Logger;
import org.csapi.cc.TpCallError;
import org.csapi.cc.gccs.IpAppCallPOA;
import org.csapi.cc.gccs.TpCallEndedReport;
import org.csapi.cc.gccs.TpCallFault;
import org.csapi.cc.gccs.TpCallInfoReport;
import org.csapi.cc.gccs.TpCallReport;

/**
 * @author Nguyen Huu Hoa
 * 
 */
public class BarringAppCall extends IpAppCallPOA {
	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(BarringAppCall.class);
	}

	BarringApplicationLogic appLogic;

	public BarringAppCall(BarringApplicationLogic logic) {
		appLogic = logic;
	}

	public void routeRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		m_logger.info("Receive result from server about routeReq");
		m_logger.debug("Current application logic: " + appLogic);
		m_logger.debug("CallSessionID: " + callSessionID);
		m_logger.debug("CallReport: " + eventReport);
		appLogic.routeRes(callSessionID, eventReport, callLegSessionID);
		
	}

	public void routeErr(int callSessionID, TpCallError errorIndication,
			int callLegSessionID) {
		m_logger.info("Unimplemented function");
	}

	public void getCallInfoRes(int callSessionID,
			TpCallInfoReport callInfoReport) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void getCallInfoErr(int callSessionID, TpCallError errorIndication) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void superviseCallRes(int callSessionID, int report, int usedTime) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void superviseCallErr(int callSessionID, TpCallError errorIndication) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void callFaultDetected(int callSessionID, TpCallFault fault) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void getMoreDialledDigitsRes(int callSessionID, String digits) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void getMoreDialledDigitsErr(int callSessionID,
			TpCallError errorIndication) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}

	public void callEnded(int callSessionID, TpCallEndedReport report) {
		// TODO Auto-generated method stub
		m_logger.info("Unimplemented function");
	}
}
