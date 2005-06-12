/**
 * 
 */
package impl;

import group5.CallControlException;
import group5.CallCriteria;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
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
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.IpAppCallControlManager;
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.IpCallControlManagerPOA;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallTreatment;

/**
 * @author Hoang Trung Hai
 *
 */
public class IpCallControlManagerImpl extends IpCallControlManagerPOA {

	private IpAppCallControlManager ipACCM_delegate;
	private IpCallControlManager ipCallControlManager;
	private HashMap administration;	
	private String C;
	private int CallSessionID;
	
	private synchronized int getCallSessionID()
	{
		CallSessionID++;
		return CallSessionID;
	}
	private HashMap mapIpCall;
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
		ci.CallSessionID = getCallSessionID();
		mapIpCall.put(new Integer(ci.CallSessionID), aCallReference);
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
		{
			String msgErr="Parameter appCallControlManager is null";
			if(m_logger.isInfoEnabled())
				m_logger.info(msgErr);	
			return 0;
		}
		else
		{
		try
			{
				 callEventCriteria(eventCriteria);
			     CallNotification callnotification = new CallNotification(this, appCallControlManager, eventCriteria);
			     return CallControlManager.getInstance().addCallNotification(callnotification);
			}
			catch (P_INVALID_CRITERIA e)
			{
				String msgErr="Invalid criteria:" + e.ExtraInformation;
				if(m_logger.isInfoEnabled())
					m_logger.info(msgErr);				
			
			}
			catch (P_INVALID_EVENT_TYPE e)
			{
				String msgErr="Error in enableCallNotification() call:" + e.ExtraInformation;
				if(m_logger.isInfoEnabled())
					m_logger.info(msgErr);						
			}
			catch (P_INVALID_INTERFACE_TYPE e)
			{
				String msgErr="Error in enableCallNotification() call: Invalid Interface Type." + e.ExtraInformation;
				if(m_logger.isInfoEnabled())
			        m_logger.info(msgErr);						
			}
			catch (TpCommonExceptions e)
			{
				String msgErr="Error in enableCallNotification() call" + e.ExtraInformation;
				if(m_logger.isInfoEnabled())
			        m_logger.info(msgErr);						
			}
			catch ( P_INVALID_ADDRESS e)
			{
				String msgErr="The address information in the event criteria is invalid" + e.ExtraInformation;
				if(m_logger.isInfoEnabled())
			        m_logger.info(msgErr);						
			}
			return 0;
		}		
	}

	/* (non-Javadoc)
	 * @see org.csapi.cc.gccs.IpCallControlManagerOperations#disableCallNotification(int)
	 */
	
	public void disableCallNotification(int assignmentID)
			throws P_INVALID_ASSIGNMENT_ID, TpCommonExceptions {
		// TODO Auto-generated method stub
		try
		{
			CallControlManager.getInstance().disableCallNotification(assignmentID);
		}		
		catch(P_INVALID_ASSIGNMENT_ID e)
		{
			String msgErr="Invalid assignmentID:" + e.ExtraInformation;
			if (m_logger.isInfoEnabled())
				m_logger.info(msgErr);
	
		}
		catch(TpCommonExceptions e)
		{
			String msgErr="Error in disableCallNotification:" + e.ExceptionType;
			if (m_logger.isInfoEnabled())
				m_logger.info(msgErr);			
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
	 private void callEventCriteria(TpCallEventCriteria tpcalleventcriteria)
	 	throws P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE
	 	{
		 	if(tpcalleventcriteria.MonitorMode == TpCallMonitorMode.P_CALL_MONITOR_MODE_DO_NOT_MONITOR)
		 		throw new P_INVALID_CRITERIA("DO_NOT_MONITOR is invalid here");
		 	byte byte0 = 6;
		 	if((tpcalleventcriteria.CallEventName | byte0) == byte0)
    	 return;
		 	if((tpcalleventcriteria.CallEventName & 1) != 0)
		 		throw new P_INVALID_EVENT_TYPE("P_EVENT_GCCS_OFFHOOK_EVENT not supported");
		 	else
		 		return;
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
