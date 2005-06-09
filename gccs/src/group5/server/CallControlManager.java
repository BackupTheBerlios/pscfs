package impl;

import java.util.List;
import org.apache.log4j.Category;
import org.csapi.*;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;

///         CallNotifications, CallNotification, OSACallObserver, IpCallImpl, 
//         IpCallControlManagerImpl
/**
 * 
 * @author Hoang Trung Hai
 *
 */
public final class CallControlManager
{

 private CallControlManager()
 {
     _fldgoto = new CallNotifications();
 }

 public static CallControlManager getInstance()
 {
     return _fldelse;
 }

 public TpCallEventCriteriaResult[] getCallEventCriteria(IpCallControlManagerImpl ipcallcontrolmanagerimpl)
 {
     return _fldgoto.getCallEventCriteria(ipcallcontrolmanagerimpl);
 }

 public int addCallNotification(CallNotification callnotification)
     throws P_INVALID_INTERFACE_TYPE, P_INVALID_EVENT_TYPE, TpCommonExceptions, P_INVALID_CRITERIA
 {
     List list = _fldgoto.addCallNotification(callnotification);
     if(list.size() > 0)
     {
         _fldlong.debug("call notification " + callnotification.getAssignmentId() + " overlaps");
         if(list.size() == 1)
         {
             CallNotification callnotification1 = (CallNotification)list.get(0);
             if(callnotification1.exactlyMatchesCriteria(callnotification))
             {
                 _fldlong.debug("  Perfect match. Setting fallback interface...");
                 callnotification1.setFallback(callnotification);
                 return callnotification1.getAssignmentId();
             }
         }
         throw new P_INVALID_CRITERIA("There is a call notification with overlapping criteria");
     } else
     {
         return callnotification.getAssignmentId();
     }
 }

 public void disableCallNotification(int i)
     throws P_INVALID_ASSIGNMENT_ID, TpCommonExceptions
 {
     CallNotification callnotification = _fldgoto._mthif(i);
     if(callnotification != null)
         callnotification.reset();
     else
         throw new P_INVALID_ASSIGNMENT_ID(Integer.toString(i));
 }

 /*public void changeCallNotification(int i, TpCallEventCriteria tpcalleventcriteria)
     throws P_INVALID_ASSIGNMENT_ID, P_INVALID_EVENT_TYPE, TpCommonExceptions, P_INVALID_CRITERIA
 {
     CallNotification callnotification = _fldgoto.a(i);
     if(callnotification == null) goto _L2; else goto _L1
_L1:
     List list;
     CallNotification callnotification1 = new CallNotification(tpcalleventcriteria);
     list = _fldgoto.findOverlapping(callnotification1);
     list.size();
     JVM INSTR lookupswitch 2: default 180
 //                   0: 68
 //                   1: 109;
        goto _L3 _L4 _L5
_L4:
     _fldlong.debug("call notification " + callnotification.getAssignmentId() + " has no overlap, setting event criteria...");
     callnotification.a(tpcalleventcriteria);
       goto _L6
_L5:
     CallNotification callnotification2;
     _fldlong.debug("call notification " + callnotification.getAssignmentId() + " has overlap.");
     callnotification2 = (CallNotification)list.get(0);
     if(callnotification2.getAssignmentId() != i) goto _L3; else goto _L7
_L7:
     _fldlong.debug("  Assignment ids match, setting event criteria...");
     callnotification.a(tpcalleventcriteria);
       goto _L6
_L3:
     throw new P_INVALID_CRITERIA("Overlapping registration");
     P_INVALID_ADDRESS p_invalid_address;
     p_invalid_address;
     throw new P_INVALID_CRITERIA("Invalid address encountered in event criteria");
_L2:
     throw new P_INVALID_ASSIGNMENT_ID(Integer.toString(i));
_L6:
 }

 public void onAddSubscriber(CCSubscriberGroup ccsubscribergroup)
 {
 }

 public void onRemoveSubscriber(CCSubscriberGroup ccsubscribergroup)
 {
 }

 public void onNewCall(CallContext callcontext)
 {
     CallNotification acallnotification[] = _fldgoto.a(callcontext);
     for(int i = 0; i < acallnotification.length; i++)
     {
         OSACallObserver osacallobserver = new OSACallObserver(callcontext, acallnotification[i]);
         IpCallImpl.getInstance().registerCallObserver(osacallobserver);
     }

 }

 public void onEndCall(CallContext callcontext)
 {
 }*/

 public void onStateCleared()
 {
     _fldgoto.clearState();
 }

 /*public void onPowerSwitch(CCSubscriberGroup ccsubscribergroup, boolean flag)
 {
 }*/

 
 private static Category _fldlong;
 private static CallControlManager _fldelse = new CallControlManager();
 private CallNotifications _fldgoto;

}
