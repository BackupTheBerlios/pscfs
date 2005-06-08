package impl;

//	 Source File Name:   CallNotifications.java

	import java.util.*;
	import org.apache.log4j.Category;
	import org.csapi.cc.gccs.TpCallEventCriteriaResult;

//	 Referenced classes of package com.lucent.isg.simulator.callcontrol.osa:
//	            CallNotification, IpCallControlManagerImpl

	final class CallNotifications
	{

	    CallNotifications()
	    {
	    	amap = new Hashtable();
	    }

	    public TpCallEventCriteriaResult[] getCallEventCriteria(IpCallControlManagerImpl ipcallcontrolmanagerimpl)
	    {
	        Vector vector = new Vector();
	        for(Iterator iterator = amap.values().iterator(); iterator.hasNext();)
	        {
	            CallNotification callnotification = (CallNotification)iterator.next();
	            if(callnotification.hasManager(ipcallcontrolmanagerimpl))
	                vector.add(new TpCallEventCriteriaResult(callnotification._mthif(), callnotification.getAssignmentId()));
	        }

	        return (TpCallEventCriteriaResult[])vector.toArray(new TpCallEventCriteriaResult[0]);
	    }

	    CallNotification a(int i)
	    {
	        return (CallNotification)amap.get(new Integer(i));
	    }

	    CallNotification _mthif(int i)
	    {
	        return (CallNotification)amap.remove(new Integer(i));
	    }

	    CallNotification[] a(CallContext callcontext)
	    {
	        LinkedList linkedlist = new LinkedList();
	        for(Iterator iterator = amap.values().iterator(); iterator.hasNext();)
	        {
	            CallNotification callnotification = (CallNotification)iterator.next();
	            if(callnotification.a(callcontext))
	                linkedlist.add(callnotification);
	        }

	        return (CallNotification[])linkedlist.toArray(new CallNotification[linkedlist.size()]);
	    }

	    public List findOverlapping(CallNotification callnotification)
	    {
	        LinkedList linkedlist = new LinkedList();
	        for(Iterator iterator = amap.values().iterator(); iterator.hasNext();)
	        {
	            CallNotification callnotification1 = (CallNotification)iterator.next();
	            if(callnotification1._mthnew(callnotification))
	                linkedlist.add(callnotification1);
	        }

	        return linkedlist;
	    }

	    public List addCallNotification(CallNotification callnotification)
	    {
	        List list = findOverlapping(callnotification);
	        if(list.size() == 0)
	        	amap.put(new Integer(callnotification.getAssignmentId()), callnotification);
	        return list;
	    }

	    public void clearState()
	    {
	        _fldif.info("CallNotifications::clearState()");
	        synchronized(amap)
	        {
	            CallNotification callnotification;
	            for(Iterator iterator = amap.values().iterator(); iterator.hasNext(); callnotification.reset())
	                callnotification = (CallNotification)iterator.next();

	            amap.clear();
	        }
	    }

	    private static Category _fldif;
	    Map amap;

	   
	}
