/**
 * 
 */
package impl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.csapi.IpInterface;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddressRange;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallLoadControlMechanism;
import org.csapi.cc.TpCallMonitorMode;
import org.csapi.cc.gccs.TpCallNotificationType;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.IpCallControlManagerPOA;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallTreatment;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;
import java.util.HashMap;
import java.util.Iterator;
import group5.*;



/**
 * @author Nguyen Huu Hoa
 *
 */
public class IpCallControlManagerImpl extends IpCallControlManagerPOA {

	private IpAppCallControlManager ipACCM_delegate;
	private IpCallControlManager ipCallControlManager;
	private HashMap administration;	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger m_logger = Logger.getLogger(IpCallControlManagerImpl.class);
	
	/**
	 * 
	 */
	public IpCallControlManagerImpl() {
		super();
		BasicConfigurator.configure();
		m_logger.info("ctor()");
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#createCall(org.csapi.cc.gccs.IpAppCall)
	 */
	public TpCallIdentifier createCall(IpAppCall appCall)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
		// TODO Auto-generated method stub
		if (ipACCM_delegate==null)
			return null;
		impl.IpCallImpl aCallReference = new impl.IpCallImpl();
		if (aCallReference==null)
		{
			// TODO: hh
			ipACCM_delegate.callAborted(0);
		}
		
		org.csapi.cc.gccs.TpCallIdentifier ci = new TpCallIdentifier();
		ci.CallReference = aCallReference._this();
		ci.CallSessionID = 0; //TODO: set appropriated value here
		return ci;
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#enableCallNotification(org.csapi.cc.gccs.IpAppCallControlManager, org.csapi.cc.gccs.TpCallEventCriteria)
	 */
	public int enableCallNotification(
			IpAppCallControlManager appCallControlManager,
			TpCallEventCriteria eventCriteria) throws P_INVALID_INTERFACE_TYPE,
			P_INVALID_EVENT_TYPE, TpCommonExceptions, P_INVALID_CRITERIA {
		// TODO Auto-generated method stub
	   	if(m_logger.isInfoEnabled())
            m_logger.info("Entering enableCallNotification");    		
		if (appCallControlManager == null)
			throw new CallControlException("Parameter 'appCallControlManager' is null", 3);
		else
		{
//			checkCriteria(eventCriteria)
			IpAppCallControlManagerImpl ccm = new IpAppCallControlManagerImpl(this,eventCriteria);
			int assignmentID=0;
			try
			{
				assignmentID=ipCallControlManager.enableCallNotification(ccm.getServant(),createCallCriteria(eventCriteria));
				ccm.setAssignmentId(assignmentID);
				administration.put(new Integer(assignmentID),ccm);
			}
			catch (P_INVALID_CRITERIA e)
			{
				String msgErr="Invalid criteria:" + e.ExtraInformation;
				if(m_logger.isInfoEnabled())
					m_logger.info(msgErr);				
				ccm.destroy();
				throw new CallControlException(msgErr,4,3);
				
			}
			catch (P_INVALID_EVENT_TYPE e)
			{
				String msgErr="Error in enableCallNotification() call:" + e.ExtraInformation;
				if(m_logger.isInfoEnabled())
					m_logger.info(msgErr);						
				ccm.destroy();
				throw new CallControlException(msgErr,4,3);
				
			}
			catch (P_INVALID_INTERFACE_TYPE e)
			{
				String msgErr="Error in enableCallNotification() call: Invalid Interface Type." + e.ExtraInformation;
				ccm.destroy();
				if(m_logger.isInfoEnabled())
			        m_logger.info(msgErr);						
				throw new CallControlException(msgErr,4,3);
				
			}
			catch (TpCommonExceptions e)
			{
				String msgErr="Error in enableCallNotification() call" + e.ExtraInformation;
				if(m_logger.isInfoEnabled())
			        m_logger.info(msgErr);						
				ccm.destroy();
				throw new CallControlException(msgErr,4,3);
				
			}
		}		
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#disableCallNotification(int)
	 */
	public void disableCallNotification(int assignmentID)
			throws P_INVALID_ASSIGNMENT_ID, TpCommonExceptions {
		// TODO Auto-generated method stub
		IpAppCallControlManagerImpl ccm;
		try
		{
			ccm=findRegistration(assignmentID);
			administration.remove(new Integer(assignmentID));
			ipCallControlManager.disableCallNotification(assignmentID);
			ccm.destroy();
		}		
		catch(P_INVALID_ASSIGNMENT_ID e)
		{
			String msgErr="Invalid assignmentID:" + e.ExtraInformation;
//			ccm.destroy();
		
		}
		catch(TpCommonExceptions e)
		{
			String msgErr="Error in disableCallNotification:" + e.ExceptionType;
//			ccm.destroy();
			
		}
		catch (CallControlException e)
		{
			String msgErr="Error in disableCallNotification" + e.getMessage();
//			ccm.destroy();
		}
			
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#setCallLoadControl(int, org.csapi.cc.TpCallLoadControlMechanism, org.csapi.cc.gccs.TpCallTreatment, org.csapi.TpAddressRange)
	 */
	public int setCallLoadControl(int duration,
			TpCallLoadControlMechanism mechanism, TpCallTreatment treatment,
			TpAddressRange addressRange) throws TpCommonExceptions,
			P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#changeCallNotification(int, org.csapi.cc.gccs.TpCallEventCriteria)
	 */
	public void changeCallNotification(int assignmentID,
			TpCallEventCriteria eventCriteria) throws P_INVALID_ASSIGNMENT_ID,
			P_INVALID_EVENT_TYPE, TpCommonExceptions, P_INVALID_CRITERIA {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#getCriteria()
	 */
	public TpCallEventCriteriaResult[] getCriteria() throws TpCommonExceptions {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
	 */
	public void setCallback(IpInterface appInterface)
			throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
		// TODO Auto-generated method stub
		//ipACCM_delegate = appInterface;
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
	public org.omg.CORBA.Object _get_interface_def() {
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
	/* (non-Javadoc)
	 */	
	 static int analyseEventMask(int eventMask)
	    {
	        return eventMask & 0xfe;
	    }
	 /* (non-Javadoc)
	  */
	 
	 private void checkCriteria(CallCriteria crit)
	    throws CallControlException
	    {
	        if(crit == null || crit.getCriteria() == 0)
	        {
	            throw new CallControlException("No events specified", 2, 2);
	        }
	        if((crit.getCriteria() & 0xffffe000) != 0)
	        {
	            throw new CallControlException("Un known event", 1, 2);
	        }
	        if(analyseEventMask(crit.getCriteria()) == 0)
	        {
	            throw new CallControlException("No callbacks will be received for given events", 2, 2);
	        } else
	        {
	            return;
	        }
	    }
	 private CallCriteria createCallCriteria(TpCallEventCriteria input)
	    {
	        if(m_logger.isInfoEnabled())
	            m_logger.info("Entering createCallCriteria!");
	        TpCallNotificationType _tmp = input.CallNotificationType;
	        TpCallMonitorMode _tmp1 = input.MonitorMode;
	        return new CallCriteria(input.OriginatingAddress.AddrString, input.DestinationAddress.AddrString, input.CallEventName, input.CallNotificationType.value() == 0, input.MonitorMode.value() == 0);
	    }	 
	/* (non-Javadoc)
	 */	
	 private IpAppCallControlManagerImpl findRegistration(int id)
        throws CallControlException
    {
		if(m_logger.isInfoEnabled())
	            m_logger.info("Entering findRegistration!");
        Object ccm = administration.get(new Integer(id));
        if(ccm != null)
        return (IpAppCallControlManagerImpl)ccm;
        String msgErr="Invalid assignment ID " + id;
        if(m_logger.isInfoEnabled())
            m_logger.info(msgErr);
        throw new CallControlException(msgErr, 3, 2);
    }
	 /* (non-Javadoc)
	  */	
    public void destroy()
    	throws CallControlException
    {
    	if(m_logger.isInfoEnabled())
              m_logger.info("Entering destroy");    	
    	if(ipCallControlManager == null)
    	{
    		String msgErr ="Already destroyed";
    	 	if(m_logger.isInfoEnabled())
                m_logger.info(msgErr); 
    		throw new CallControlException("Already destroyed", 0, 2);
    	}
	    int id;
	    try
	    {
	    	for(Iterator i = administration.keySet().iterator(); i.hasNext(); disableCallNotification(id))
		        id = ((Integer)i.next()).intValue();
	    }
	    catch (P_INVALID_ASSIGNMENT_ID e)
	    {
	    	String msgErr="Invalid assignment ID" + e.getMessage();
	    	if(m_logger.isInfoEnabled())
	              m_logger.info(msgErr);	    	
	    }
	    catch (TpCommonExceptions e)
	    {
	    	String msgErr="Error destroy" + e.getMessage();
	    	if(m_logger.isInfoEnabled())
	              m_logger.info(msgErr);	    	
	    }
	    administration.clear();
	    ipCallControlManager._release();
	    ipCallControlManager = null;
    }
}
