/**
 * 
 */
package impl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.csapi.IpInterface;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddress;
import org.csapi.TpAoCInfo;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallChargePlan;
import org.csapi.cc.gccs.IpCallPOA;
import org.csapi.cc.gccs.TpCallAppInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallReleaseCause;
import org.csapi.cc.gccs.TpCallReportRequest;
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

/**
 * @author Nguyen Huu Hoa
 *
 */
public class IpCallImpl extends IpCallPOA {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(IpCallControlManagerImpl.class);

    public IpCallImpl(TpCallIdentifier callid, String originatorAddress, String originalDestinationAddress, IpAppCallControlManagerImpl manager, IpAppCallImpl ipAppCallImpl)
    throws TpCommonExceptions, P_INVALID_SESSION_ID, P_INVALID_INTERFACE_TYPE
    {
    	
    }
	/**
	 * 
	 */
	public IpCallImpl() {
		super();
		BasicConfigurator.configure();
		logger.info("ctor()");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#routeReq(int, org.csapi.cc.gccs.TpCallReportRequest[], org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.cc.gccs.TpCallAppInfo[])
	 */
	public int routeReq(int callSessionID,
			TpCallReportRequest[] responseRequested, TpAddress targetAddress,
			TpAddress originatingAddress, TpAddress originalDestinationAddress,
			TpAddress redirectingAddress, TpCallAppInfo[] appInfo)
			throws P_INVALID_EVENT_TYPE, P_INVALID_NETWORK_STATE,
			TpCommonExceptions, P_INVALID_ADDRESS, P_INVALID_SESSION_ID,
			P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_CRITERIA {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#release(int, org.csapi.cc.gccs.TpCallReleaseCause)
	 */
	public void release(int callSessionID, TpCallReleaseCause cause)
			throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#deassignCall(int)
	 */
	public void deassignCall(int callSessionID) throws TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#getCallInfoReq(int, int)
	 */
	public void getCallInfoReq(int callSessionID, int callInfoRequested)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		logger.info("getCallInfoReq");
		logger.debug("getCallInfoReq - Unimplemented");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#setCallChargePlan(int, org.csapi.cc.TpCallChargePlan)
	 */
	public void setCallChargePlan(int callSessionID,
			TpCallChargePlan callChargePlan) throws TpCommonExceptions,
			P_INVALID_SESSION_ID {
		logger.info("setCallChargePlan");
		logger.debug("setCallChargePlan - Unimplemented");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#setAdviceOfCharge(int, org.csapi.TpAoCInfo, int)
	 */
	public void setAdviceOfCharge(int callSessionID, TpAoCInfo aOCInfo,
			int tariffSwitch) throws TpCommonExceptions, P_INVALID_SESSION_ID {
		logger.info("setAdviceOfCharge");
		logger.debug("setAdviceOfCharge - Unimplemented");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#getMoreDialledDigitsReq(int, int)
	 */
	public void getMoreDialledDigitsReq(int callSessionID, int length)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		logger.info("getMoreDialledDigietsReq");
		logger.debug("getMoreDialledDigitsReq - Unimplemented");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#superviseCallReq(int, int, int)
	 */
	public void superviseCallReq(int callSessionID, int time, int treatment)
			throws TpCommonExceptions, P_INVALID_SESSION_ID {
		logger.info("superviseCallReq");
		logger.debug("superviseCallReq - Unimplemented");

	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallOperations#continueProcessing(int)
	 */
	public void continueProcessing(int callSessionID)
			throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		logger.info("continueProcessing");
		logger.debug("continueProcessing - Unimplemented");

	}

	/* (non-Javadoc)
	 * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
	 */
	public void setCallback(IpInterface appInterface)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
		logger.info("setCallback");
		logger.debug("setCallback - Unimplemented");

	}

	/* (non-Javadoc)
	 * @see org.csapi.IpServiceOperations#setCallbackWithSessionID(org.csapi.IpInterface, int)
	 */
	public void setCallbackWithSessionID(IpInterface appInterface, int sessionID)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions,
			P_INVALID_SESSION_ID {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_release()
	 */
	public void _release() {
		logger.info("_release");
		logger.debug("_release - Unimplemented");

	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_non_existent()
	 */
	public boolean _non_existent() {
		logger.info("_non_existent");
		logger.debug("_non_existent - Unimplemented");
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_hash(int)
	 */
	public int _hash(int arg0) {
		logger.info("_hash");
		logger.debug("_hash - Unimplemented");
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_a(java.lang.String)
	 */
	public boolean _is_a(String arg0) {
		logger.info("_is_a");
		logger.debug("_is_a - Unimplemented");
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_domain_managers()
	 */
	public DomainManager[] _get_domain_managers() {
		logger.info("_get_domain_managers");
		logger.debug("_get_domain_managers - Unimplemented");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_duplicate()
	 */
	public Object _duplicate() {
		logger.info("_duplicate");
		logger.debug("_duplicate - Unimplemented");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_interface_def()
	 */
	public Object _get_interface_def() {
		logger.info("_get_interface_def");
		logger.debug("_get_interface_def - Unimplemented");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
	 */
	public boolean _is_equivalent(Object arg0) {
		logger.info("_is_equivalent");
		logger.debug("_is_equivalent - Unimplemented");
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_policy(int)
	 */
	public Policy _get_policy(int arg0) {
		logger.info("_get_policy");
		logger.debug("_get_policy - Unimplemented");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_request(java.lang.String)
	 */
	public Request _request(String arg0) {
		logger.info("_request");
		logger.debug("_request - Unimplemented");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[], org.omg.CORBA.SetOverrideType)
	 */
	public Object _set_policy_override(Policy[] arg0, SetOverrideType arg1) {
		logger.info("_set_policy_override");
		logger.debug("_set_policy_override - Unimplemented");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
	 */
	public Request _create_request(Context arg0, String arg1, NVList arg2,
			NamedValue arg3) {
		logger.info("_create_request");
		logger.debug("_create_request - Unimplemented");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
	 */
	public Request _create_request(Context arg0, String arg1, NVList arg2,
			NamedValue arg3, ExceptionList arg4, ContextList arg5) {
		logger.info("_create_request");
		logger.debug("_create_request - Unimplemented");
		return null;
	}

}
