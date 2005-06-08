package impl;

import org.apache.log4j.Category;
import org.csapi.*;
import org.csapi.cc.TpCallMonitorMode;
import org.csapi.cc.gccs.*;


final class CallNotification
{

 public CallNotification(IpCallControlManagerImpl ipcallcontrolmanagerimpl, IpAppCallControlManager ipappcallcontrolmanager, TpCallEventCriteria tpcalleventcriteria)
     throws P_INVALID_ADDRESS, P_INVALID_EVENT_TYPE
 {
     _flddo = null;
     _fldfor.debug("New CallNotification being created");
     _fldcase = ipcallcontrolmanagerimpl;
     _fldint = ipappcallcontrolmanager;
     _mthdo();
     assignmentID = createAssignmentId();
     callEventCriteria(tpcalleventcriteria);
 }

 CallNotification(TpCallEventCriteria tpcalleventcriteria)
     throws P_INVALID_ADDRESS, P_INVALID_EVENT_TYPE
 {
     this(null, null, tpcalleventcriteria);
 }

 private boolean _mthdo(CallNotification callnotification)
 {
     return _fldif.equals(callnotification._fldif);
 }

 private boolean a(CallNotification callnotification)
 {
     return _fldbyte.equals(callnotification._fldbyte);
 }

 private boolean _mthint(CallNotification callnotification)
 {
     TpAddressPlan tpaddressplan = _fldtry.OriginatingAddress.Plan;
     TpAddressPlan tpaddressplan1 = _fldtry.DestinationAddress.Plan;
     TpAddressPlan tpaddressplan2 = callnotification._fldtry.OriginatingAddress.Plan;
     TpAddressPlan tpaddressplan3 = callnotification._fldtry.DestinationAddress.Plan;
     return tpaddressplan.value() == tpaddressplan2.value() && tpaddressplan1.value() == tpaddressplan3.value();
 }

 private boolean _mthif(CallNotification callnotification)
 {
     return _fldtry.CallNotificationType.value() == callnotification._fldtry.CallNotificationType.value();
 }

 private boolean _mthfor(CallNotification callnotification)
 {
     return callnotification._fldtry.MonitorMode == _fldtry.MonitorMode;
 }

 private boolean _mthtry(CallNotification callnotification)
 {
     return callnotification._fldtry.MonitorMode == TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT && _fldtry.MonitorMode == TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT;
 }

 boolean _mthnew(CallNotification callnotification)
 {
     if(_fldcase != callnotification._fldcase && !_mthtry(callnotification))
         return false;
     else
         return _mthfor(callnotification) && _mthdo(callnotification) && a(callnotification) && _mthint(callnotification) && _mthif(callnotification);
 }

 private boolean a(TpCallEventCriteria tpcalleventcriteria, TpCallEventCriteria tpcalleventcriteria1)
 {
     return tpcalleventcriteria.CallEventName == tpcalleventcriteria1.CallEventName && tpcalleventcriteria.CallNotificationType == tpcalleventcriteria1.CallNotificationType && tpcalleventcriteria.DestinationAddress.AddrString.equals(tpcalleventcriteria1.DestinationAddress.AddrString) && tpcalleventcriteria.DestinationAddress.Plan == tpcalleventcriteria1.DestinationAddress.Plan && tpcalleventcriteria.OriginatingAddress.AddrString.equals(tpcalleventcriteria1.OriginatingAddress.AddrString) && tpcalleventcriteria.OriginatingAddress.Plan == tpcalleventcriteria1.OriginatingAddress.Plan && tpcalleventcriteria.MonitorMode == tpcalleventcriteria1.MonitorMode;
 }

 public boolean exactlyMatchesCriteria(CallNotification callnotification)
 {
     return _fldcase == callnotification._fldcase && a(_fldtry, callnotification._fldtry);
 }

 public int getAssignmentId()
 {
     return assignmentID;
 }

 public void setFallback(CallNotification callnotification)
 {
     if(callnotification._fldint != _fldint)
     {
         _flddo = _fldint;
         _fldint = callnotification._fldint;
     }
 }

 public boolean hasManager(IpCallControlManagerImpl ipcallcontrolmanagerimpl)
 {
     return _fldcase == ipcallcontrolmanagerimpl;
 }

 public IpInterface eventNotify(TpCallIdentifier tpcallidentifier, TpCallEventInfo tpcalleventinfo)
 {
     return _fldint.callEventNotify(tpcallidentifier, tpcalleventinfo, assignmentID);
 }

 public IpInterface eventNotifyFallback(TpCallIdentifier tpcallidentifier, TpCallEventInfo tpcalleventinfo)
 {
     return _flddo.callEventNotify(tpcallidentifier, tpcalleventinfo, assignmentID);
 }

 public boolean isActiveListener()
 {
     return _fldtry == null ? false : _fldtry.MonitorMode.value() == 0;
 }

 public static int createAssignmentId()
 {
     return _fldnew != 0x7fffffff ? _fldnew++ : 0;
 }

 void a()
     throws P_INVALID_INTERFACE_TYPE
 {
 }

 void _mthdo()
     throws P_INVALID_EVENT_TYPE
 {
 }

 TpCallEventCriteria _mthif()
 {
     return _fldtry;
 }

 void callEventCriteria(TpCallEventCriteria tpcalleventcriteria)
     throws P_INVALID_ADDRESS
 {
     _fldtry = tpcalleventcriteria;
     _fldif = new String(tpcalleventcriteria.OriginatingAddress.AddrString);
     _fldbyte = new String(tpcalleventcriteria.DestinationAddress.AddrString);
 }

 boolean callContext(CallContext callcontext)
 {
     return _fldif.callContext(callcontext.getOriginatorNumber()) && _fldbyte.callContext(callcontext.getDestinationNumber());
     P_INVALID_ADDRESS p_invalid_address;
     //p_invalid_address;
     _fldfor.error("No match for " + callcontext, p_invalid_address);
     return false;
 }

 public void reset()
 {
     if(_fldint != null)
     {
         _fldint._release();
         _fldint = null;
     }
     if(_flddo != null)
     {
         _flddo._release();
         _flddo = null;
     }
     _fldtry = null;
	  _fldif = null;
     _fldbyte = null;
     _fldcase = null;
 }

 public String toString()
 {
     return "CallNotification: src=" + _fldif + " dest=" + _fldbyte + " hasFallback=" + (_flddo != null);
 }

  private static Category _fldfor;
 private static int _fldnew = 0;
 private int assignmentID;
 private IpAppCallControlManager _fldint;
 private IpAppCallControlManager _flddo;
 private TpCallEventCriteria _fldtry;
 private String _fldif;
 private String _fldbyte;
 private IpCallControlManagerImpl _fldcase;

 
}
