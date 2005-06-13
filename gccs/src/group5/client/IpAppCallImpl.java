//$Id: IpAppCallImpl.java,v 1.8 2005/06/13 09:11:51 huuhoa Exp $
/**
 * 
 */
package group5.client;

import group5.ApplicationLogic;
import group5.server.IpCallControlManagerImpl;

import org.apache.log4j.Logger;
import org.csapi.cc.TpCallError;
import org.csapi.cc.gccs.IpAppCallPOA;
import org.csapi.cc.gccs.TpCallEndedReport;
import org.csapi.cc.gccs.TpCallFault;
import org.csapi.cc.gccs.TpCallInfoReport;
import org.csapi.cc.gccs.TpCallReport;

/**
 * @author Nguyen Huu Hoa Implementation of IpAppCall
 */
public class IpAppCallImpl extends IpAppCallPOA {
	ApplicationLogic appLogic;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger m_logger = Logger.getLogger(IpAppCallImpl.class);

	public IpAppCallImpl(IpCallControlManagerImpl ccManager,
			IpAppCallControlManagerImpl ccAppManager) {

	}

	/**
	 * TODO: Return the appropriated reference here
	 * 
	 * @return a reference to implementation of the interface IpAppCall
	 */
	public IpAppCallImpl getServant() {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering getServant");
		return this;
	}

	public void destroy() {
		if (m_logger.isInfoEnabled())
			m_logger.info("Entering destroy!");
	}

	/**
	 * 
	 */
	public IpAppCallImpl(ApplicationLogic logic) {
		super();
		m_logger.info("ctor()");
		m_logger.debug("ApplicationLogic=" + logic);
		// TODO Auto-generated constructor stub
		appLogic = logic;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#routeRes(int,
	 *      org.csapi.cc.gccs.TpCallReport, int)
	 */
	public void routeRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		// org.csapi.cc.gccs.IpCallControlManager obj = new
		// org.csapi.cc.gccs.IpCallControlManager();
		System.out.println("IpAppCall.routeRes() is called with callSessionID="
				+ callSessionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#routeErr(int,
	 *      org.csapi.cc.TpCallError, int)
	 */
	public void routeErr(int callSessionID, TpCallError errorIndication,
			int callLegSessionID) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCall.routeErr() is called with callSessionID="
				+ callSessionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#getCallInfoRes(int,
	 *      org.csapi.cc.gccs.TpCallInfoReport)
	 */
	public void getCallInfoRes(int callSessionID,
			TpCallInfoReport callInfoReport) {
		// TODO Auto-generated method stub
		System.out
				.println("IpAppCall.getCallInfoRes() is called with callSessionID="
						+ callSessionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#getCallInfoErr(int,
	 *      org.csapi.cc.TpCallError)
	 */
	public void getCallInfoErr(int callSessionID, TpCallError errorIndication) {
		// TODO Auto-generated method stub
		System.out
				.println("IpAppCall.getCallInfoErr() is called with callSessionID="
						+ callSessionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#superviseCallRes(int, int,
	 *      int)
	 */
	public void superviseCallRes(int callSessionID, int report, int usedTime) {
		// TODO Auto-generated method stub
		System.out
				.println("IpAppCall.superviseCallRes() is called with callSessionID="
						+ callSessionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#superviseCallErr(int,
	 *      org.csapi.cc.TpCallError)
	 */
	public void superviseCallErr(int callSessionID, TpCallError errorIndication) {
		// TODO Auto-generated method stub
		System.out
				.println("IpAppCall.superviseCallErr() is called with callSessionID="
						+ callSessionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#callFaultDetected(int,
	 *      org.csapi.cc.gccs.TpCallFault)
	 */
	public void callFaultDetected(int callSessionID, TpCallFault fault) {
		System.out
				.println("IpAppCall.callFaultDetected() is called with callSessionID="
						+ callSessionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#getMoreDialledDigitsRes(int,
	 *      java.lang.String)
	 */
	public void getMoreDialledDigitsRes(int callSessionID, String digits) {
		System.out
				.println("IpAppCall.getMoreDialledDigitsRes() is called with callSessionID="
						+ callSessionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#getMoreDialledDigitsErr(int,
	 *      org.csapi.cc.TpCallError)
	 */
	public void getMoreDialledDigitsErr(int callSessionID,
			TpCallError errorIndication) {
		System.out
				.println("IpAppCall.getMoreDialledDigitsErr() is called with callSessionID="
						+ callSessionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallOperations#callEnded(int,
	 *      org.csapi.cc.gccs.TpCallEndedReport)
	 */
	public void callEnded(int callSessionID, TpCallEndedReport report) {
		// TODO Auto-generated method stub
		System.out
				.println("IpAppCall.callEnded() is called with callSessionID="
						+ callSessionID);
	}
}
