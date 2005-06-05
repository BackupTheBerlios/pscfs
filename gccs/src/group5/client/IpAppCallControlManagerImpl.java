/**
 * 
 */
package impl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
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
import group5.ApplicationLogic;
import group5.CallControlListener;
import group5.CallCriteria;
/**
 * @author Nguyen Huu Hoa
 *
 */
public class IpAppCallControlManagerImpl implements IpAppCallControlManager {

	ApplicationLogic appLogic;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * logger for the system
	 */
	static Logger m_logger;
	
	static {
		m_logger = Logger.getLogger(IpAppCallControlManagerImpl.class);
	}
    private IpCallControlManagerImpl ccAdapter;
    private IpAppCallControlManager ipAppCallControlManager;
    private IpAppCallImpl ipAppCallImpl;
    private CallControlListener callControlListener;
    //private Map callAdministration;
    private CallCriteria callCriteria;
    private int assignmentId;

	/**
	 * 
	 */
	public IpAppCallControlManagerImpl(ApplicationLogic logic) {
		super();
		BasicConfigurator.configure();
		if (m_logger.isEnabledFor(Level.INFO))
		{
			m_logger.info("ctor()");
		}
	
		// TODO Auto-generated constructor stub
		appLogic = logic;
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallControlManagerOperations#callAborted(int)
	 */
	public void callAborted(int callReference) {
		// TODO Auto-generated method stub
		if (m_logger.isEnabledFor(Level.INFO))
		{
			m_logger.info("callAborted() is called with callReference=" + callReference);
		}
	}

	/**
	 * This method notifies the application of the arrival of a call-related event.
	 * If this method is invoked with a monitor mode of P_CALL_MONITOR_MODE_INTERRUPT,
	 * then the APL has control of the call.
	 * If the APL does nothing with the call (including its associated legs)
	 * within a specified time period (the duration of which forms
	 * a part of the service level agreement), then the call in the network
	 * shall be released and callEnded() shall be invoked, giving a release
	 * cause of 102 (Recovery on timer expiry).
	 * 
	 * <p>
	 * <b>Setting the callback reference:</b>
	 * A reference to the application interface has to be passed back to
	 * the call interface to which the notification relates. However,
	 * the setting of a call back reference is only applicable if the notification 
	 * is in INTERRUPT mode. When the callEventNotify() method is invoked 
	 * with a monitor mode of P_CALL_MONITOR_MODE_INTERRUPT, the application writer 
	 * should ensure that no continue processing e.g. routeReq() is performed 
	 * until an IpAppCall has been passed to the gateway, either through 
	 * an explicit setCallbackWithSessionID() invocation on the supplied IpCall, 
	 * or via the return of the callEventNotify() method.
	 * The callback reference can be registered either in a) callEventNotify() 
	 * or b) explicitly with a setCallbackWithSessionID() method 
	 * e.g. depending on how the application provides its call reference.
	 * 
	 * Case a:
	 * 	From an efficiency point of view the callEventNotify() with explicit 
	 * 	pass of registration may be the preferred method.
	 * Case b:
	 * 	The callEventNotify() with no callback reference ("Null" value) 
	 * 	is used where (e.g. due to distributed application logic) the callback
	 * 	reference is provided subsequently in a setCallbackWithSessionID().
	 * In case the callEventNotify() contains no callback, at the moment
	 * the application needs to be informed the gateway will use as callback 
	 * the callback that has been registered by setCallbackWithSessionID().
	 * See example in 4.6
	 * Returns appCall: Specifies a reference to the application interface 
	 * which implements the callback interface for the new call. 
	 * If the application has previously explicitly passed a reference to the 
	 * IpAppCall interface using a setCallbackWithSessionID() invocation, 
	 * this parameter may be null, or if supplied must be the same as that provided
	 * during the setCallbackWithSessionID().
	 * This parameter will be null if the notification is in NOTIFY mode and in case b).
	 * 
	 * @see org.csapi.cc.gccs.IpAppCallControlManagerOperations#callEventNotify(org.csapi.cc.gccs.TpCallIdentifier, org.csapi.cc.gccs.TpCallEventInfo, int)
	 */
	public IpAppCall callEventNotify(TpCallIdentifier callReference,
			TpCallEventInfo eventInfo, int assignmentID) {
		// TODO clear out some confused about returning reference of appCall
		System.out.println("IpAppCallControlManager.callEventNotify() is called with callReference=" + callReference);
		IpAppCall ipAppCall = new IpAppCallImpl(appLogic);
		// further push event notification
		appLogic.callEventNotify(callReference, eventInfo, assignmentID);
		
		return ipAppCall;
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallControlManagerOperations#callNotificationInterrupted()
	 */
	public void callNotificationInterrupted() {
		// TODO Auto-generated method stub
		System.out.println("IpAppCallControlManager.callNotificationInterrupted() is called");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallControlManagerOperations#callNotificationContinued()
	 */
	public void callNotificationContinued() {
		// TODO Auto-generated method stub
		System.out.println("IpAppCallControlManager.callNotificationContinued() is called");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallControlManagerOperations#callOverloadEncountered(int)
	 */
	public void callOverloadEncountered(int assignmentID) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCallControlManager.callOverloadEncountered() is called with assignmentID=" + assignmentID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallControlManagerOperations#callOverloadCeased(int)
	 */
	public void callOverloadCeased(int assignmentID) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCallControlManager.callOverloadCeased() is called with assignmentID=" + assignmentID);
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpAppCallControlManagerOperations#abortMultipleCalls(int[])
	 */
	public void abortMultipleCalls(int[] callReferenceSet) {
		// TODO Auto-generated method stub
		System.out.println("IpAppCallControlManager.abortMultipleCalls() is called");
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
    public IpAppCallControlManagerImpl(CallControlAdapterImpl ccAdapter, CallCriteria criteria, CallControlListener listener, CallControlCallback callback)
    throws CallControlException
{
    ipAppCallControlManager = null;
    callControlListener = null;
    callControlCallback = null;
    callAdministration = null;
    assignmentId = -1;
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering IpAppCallControlManagerImpl Construction!");
    this.ccAdapter = ccAdapter;
    callCriteria = criteria;
    callControlListener = listener;
    callControlCallback = callback;
    callAdministration = Collections.synchronizedMap(new HashMap());
    ipAppCallImpl = new IpAppCallImpl(ccAdapter, this);
    try
    {
        ipAppCallControlManager = _this(ORBUtil.getOrb());
    }
    catch(SystemException ex)
    {
        if(m_log.isLoggable(Level.SEVERE))
            m_log.severe(ex.toString());
        throw new CallControlException(ex.toString(), 0, 3);
    }
    catch(Exception ex)
    {
        if(m_log.isLoggable(Level.SEVERE))
            m_log.severe(ex.toString());
        throw new CallControlException(ex.toString(), 0, 2);
    }
}

void destroy()
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering destroy!");
    ipAppCallImpl.destroy();
    callAdministration.clear();
    ipAppCallControlManager._release();
}

public IpAppCall callEventNotify(TpCallIdentifier callId, final TpCallEventInfo info, int assId)
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering callEventNotify!");
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("(" + Integer.toString(callId.CallSessionID) + "," + TypeConverter.eventName(info.CallEventName) + "," + Integer.toString(assId) + ")");
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Trying to create notifyApplication thread!");
    final CallImpl call = findCreateCall(callId, info);
    if((callCriteria.getCriteria() & info.CallEventName) != 0)
        (new Thread() {

            public void run()
            {
                notifyApplication(call, info.CallEventName, null);
            }

        }).start();
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("After create notifyApplication thread!");
    return callCriteria.isInterruptMode() ? ipAppCallImpl.getServant() : null;
    TpCommonExceptions e;
    e;
    String msg = "TpCommonExceptions: " + e.ExtraInformation;
    if(m_log.isLoggable(Level.SEVERE))
        m_log.severe(msg);
    throw new RuntimeException(msg);
    P_INVALID_SESSION_ID e;
    e;
    String msg = "P_INVALID_SESSION_ID: " + e.ExtraInformation;
    if(m_log.isLoggable(Level.SEVERE))
        m_log.severe(msg);
    throw new RuntimeException(msg);
    P_INVALID_INTERFACE_TYPE e;
    e;
    String msg = "P_INVALID_INTERFACE_TYPE: " + e.ExtraInformation;
    if(m_log.isLoggable(Level.SEVERE))
        m_log.severe(msg);
    throw new RuntimeException(msg);
}

public void callAborted(int callSessionID)
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering callAborted! " + "(" + Integer.toString(callSessionID) + ")");
    CallImpl call = (CallImpl)callAdministration.remove(new Integer(callSessionID));
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Trying to call notifyApplication!");
    if(call != null)
    {
        call.onAborted();
        notifyApplication(call, 2048, null);
    }
}

public void callNotificationContinued()
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering callNotificationContinued!");
}

public void callNotificationInterrupted()
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering callNotificationInterrupted!");
}

public void callOverloadCeased(int arg0)
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering callOverloadCeased! " + "(" + Integer.toString(arg0) + ")");
}

public void callOverloadEncountered(int arg0)
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering callOverloadEncountered! " + "(" + Integer.toString(arg0) + ")");
}

public IpAppCallControlManager getServant()
{
    return ipAppCallControlManager;
}

public int getAssignmentId()
{
    return assignmentId;
}

void setAssignmentId(int id)
{
    assignmentId = id;
}

public CallCriteria getCallCriteria()
{
    return callCriteria;
}

public void setCallCriteria(CallCriteria callCriteria)
{
    this.callCriteria = callCriteria;
}

public void notifyApplication(CallImpl call, int ofEvent, TpCallError error)
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering actually notifyApplication!");
    call.setError(error);
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Trying to deassign the call when Event.CALL_ENDED!");
    if(ofEvent == 1024)
        try
        {
            call.deassign();
        }
        catch(CallControlException e) { }
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Trying to call callControlListener.onEvent()!");
    if(callControlListener != null)
        callControlListener.onEvent(new CallControlEvent(ccAdapter, assignmentId, 3, call, ofEvent));
    else
        callControlCallback.event(assignmentId, call, ofEvent);
}

public void notifyApplication(int callSessionID, int ofEvent, TpCallError error)
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering notifyApplication!");
    CallImpl call = findCall(callSessionID);
    if(call != null)
        notifyApplication(call, ofEvent, error);
}

private CallImpl findCall(int callSessionID)
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering findCall!");
    return (CallImpl)callAdministration.get(new Integer(callSessionID));
}

private CallImpl findCreateCall(TpCallIdentifier callid, TpCallEventInfo info)
    throws TpCommonExceptions, P_INVALID_SESSION_ID, P_INVALID_INTERFACE_TYPE
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering findCreateCall!");
    Integer id = new Integer(callid.CallSessionID);
    CallImpl call = (CallImpl)callAdministration.get(id);
    if(call == null)
    {
        call = new CallImpl(callid, info.OriginatingAddress.AddrString, info.DestinationAddress.AddrString, this, ipAppCallImpl);
        callAdministration.put(id, call);
        if(m_log.isLoggable(Level.FINEST))
            m_log.finest("Created call object id = " + callid.CallSessionID);
    }
    return call;
}

public void callDone(int callSessionId)
{
    if(m_log.isLoggable(Level.FINEST))
        m_log.finest("Entering callDone!");
    java.lang.Object o = callAdministration.remove(new Integer(callSessionId));
    if(o == null && m_log.isLoggable(Level.FINEST))
        m_log.finest("callDone(): call does not exist");
}

}
