/**
 * 
 */
package impl;

import group5.ApplicationLogic;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csapi.cc.TpCallError;
import org.csapi.cc.gccs.IpAppCallPOA;
import org.csapi.cc.gccs.TpCallEndedReport;
import org.csapi.cc.gccs.TpCallFault;
import org.csapi.cc.gccs.TpCallInfoReport;
import org.csapi.cc.gccs.TpCallReport;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;
//import group5.testLog4j;
/**
 * @author Nguyen Huu Hoa
 * Implementation of IpAppCall
 */
public class IpAppCallImpl extends IpAppCallPOA {
	ApplicationLogic appLogic;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger m_logger = Logger.getLogger(IpAppCallImpl.class);

	public IpAppCallImpl(IpCallControlManagerImpl ccManager, IpAppCallControlManagerImpl ccAppManager)
	{
		
	}
	/**
	 * TODO: Return the appropriated reference here 
	 * @return a reference to implementation of the interface IpAppCall
	 */
	public IpAppCallImpl getServant()
	{
		if (m_logger.isEnabledFor(Level.INFO))
			m_logger.info("Entering getServant");
		return this;
	}
	public void destroy()
	{
		if (m_logger.isEnabledFor(Level.INFO))
			m_logger.info("Entering destroy!");
	}
	/**
	 * 
	 */
	public IpAppCallImpl(ApplicationLogic logic) {
		super();
		BasicConfigurator.configure();
		m_logger.info("ctor()");
		m_logger.debug("ApplicationLogic="+logic);
		// TODO Auto-generated constructor stub
		appLogic = logic;
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#routeRes(int, org.csapi.cc.gccs.TpCallReport, int)
	 */
	public void routeRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID) {
		//org.csapi.cc.gccs.IpCallControlManager obj = new org.csapi.cc.gccs.IpCallControlManager();
		System.out.println("IpAppCall.routeRes() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#routeErr(int, org.csapi.cc.TpCallError, int)
	 */
	public void routeErr(int callSessionID, TpCallError errorIndication,
			int callLegSessionID) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCall.routeErr() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#getCallInfoRes(int, org.csapi.cc.gccs.TpCallInfoReport)
	 */
	public void getCallInfoRes(int callSessionID,
			TpCallInfoReport callInfoReport) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCall.getCallInfoRes() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#getCallInfoErr(int, org.csapi.cc.TpCallError)
	 */
	public void getCallInfoErr(int callSessionID, TpCallError errorIndication) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCall.getCallInfoErr() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#superviseCallRes(int, int, int)
	 */
	public void superviseCallRes(int callSessionID, int report, int usedTime) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCall.superviseCallRes() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#superviseCallErr(int, org.csapi.cc.TpCallError)
	 */
	public void superviseCallErr(int callSessionID, TpCallError errorIndication) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCall.superviseCallErr() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#callFaultDetected(int, org.csapi.cc.gccs.TpCallFault)
	 */
	public void callFaultDetected(int callSessionID, TpCallFault fault) {
		System.out.println("IpAppCall.callFaultDetected() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#getMoreDialledDigitsRes(int, java.lang.String)
	 */
	public void getMoreDialledDigitsRes(int callSessionID, String digits) {
		System.out.println("IpAppCall.getMoreDialledDigitsRes() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#getMoreDialledDigitsErr(int, org.csapi.cc.TpCallError)
	 */
	public void getMoreDialledDigitsErr(int callSessionID,
			TpCallError errorIndication) {
		System.out.println("IpAppCall.getMoreDialledDigitsErr() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallOperations#callEnded(int, org.csapi.cc.gccs.TpCallEndedReport)
	 */
	public void callEnded(int callSessionID, TpCallEndedReport report) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCall.callEnded() is called with callSessionID=" + callSessionID);
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_release()
	 */
	public void _release() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_non_existent()
	 */
	public boolean _non_existent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_hash(int)
	 */
	public int _hash(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_a(java.lang.String)
	 */
	public boolean _is_a(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_domain_managers()
	 */
	public DomainManager[] _get_domain_managers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_duplicate()
	 */
	public Object _duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_interface_def()
	 */
	public Object _get_interface_def() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
	 */
	public boolean _is_equivalent(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_policy(int)
	 */
	public Policy _get_policy(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_request(java.lang.String)
	 */
	public Request _request(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[], org.omg.CORBA.SetOverrideType)
	 */
	public Object _set_policy_override(Policy[] arg0, SetOverrideType arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
	 */
	public Request _create_request(Context arg0, String arg1, NVList arg2,
			NamedValue arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
	 */
	public Request _create_request(Context arg0, String arg1, NVList arg2,
			NamedValue arg3, ExceptionList arg4, ContextList arg5) {
		// TODO Auto-generated method stub
		return null;
	}

}
