package impl;


import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Category;
import org.csapi.*;
import org.csapi.cc.*;
import org.csapi.cc.gccs.*;
import org.csapi.cc.gccs.TpCallAppInfo;
import org.csapi.cc.gccs.TpCallEndedReport;
import org.csapi.cc.gccs.TpCallEventInfo;


//Referenced classes of 
//         IpCallImpl, CallNotification, ResponseHandler

public final class OSACallObserver
 implements CallEventObserver, IpCallOperations
{

 private static Category i;
 private int w;
 private int n;
 private static SimpleDateFormat r = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
 private static int s;
 private int e;
 private CallContext t;
 private static final int p = 3;
 private static final int m = 16;
 private static final int u = 17;
 private static final int l = 19;
 private static final int x = 20;
 private static final int j = 23;
 private static final int v = 102;
 private int o;
 private CallNotification callNotification;
 private IpAppCall g;
 private TpCallIdentifier q;
 private boolean f;
 private boolean k;
 private UICall h;

 public OSACallObserver(CallContext callcontext, CallNotification callnotification)
 {
     w = 0;
     n = 0;
     e = s++;
     o = 16;
     g = null;
     q = null;
     f = false;
     k = false;
     h = null;
     t = callcontext;
     y = callnotification;
     callcontext.addObserver(this);
 }

 OSACallObserver()
 {
     w = 0;
     n = 0;
     e = s++;
     o = 16;
     g = null;
     q = null;
     f = false;
     k = false;
     h = null;
 }

 private void b()
 {
     f = false;
     i.debug("adding event to check application after " + IpCallImpl._mthdo() + " ms.");
     IpCallImpl.getInstance()._mthif().triggerEvent(new TimerEvent() {

         public void onTimer()
         {
             OSACallObserver.i.debug("checking application for response...");
             if(!_mthchar())
             {
                 OSACallObserver.i.info("application response timeout");
                 k = true;
                 o = 102;
                 getContext().endCall(false);
             } else
             {
                 OSACallObserver.i.info("application has responded within timeout period");
             }
         }
     }, IpCallImpl._mthdo());
 }

 private boolean _mthchar()
 {
     return f;
 }

 private void c()
 {
     i.debug("kicking the simulator...");
     getContext().continueProcessing();
 }

 public int getCallSessionId()
 {
     return e;
 }

 public IpAppCall getCallback()
 {
     return g;
 }

 public void clearCallback()
 {
     if(g != null)
     {
         g._release();
         g = null;
     }
 }

 public CallContext getContext()
 {
     return t;
 }

 public boolean allowsUI()
 {
     return h == null && t.allowsUI();
 }

 CallNotification _mthvoid()
 {
     return y;
 }

 public void setCallbackWithSessionID(IpInterface ipinterface, int i1)
     throws TpCommonExceptions
 {
     i.debug("A new call back interface is set for call " + i1);
     try
     {
         g = IpAppCallHelper.narrow(ipinterface);
         i.debug("  Succesfully changed call back interface");
     }
     catch(Exception exception)
     {
         i.debug("  Failed to change call back interface");
         throw new TpCommonExceptions(14, "Invalid Interface Type");
     }
 }

 public void deassignCall(int i1)
 {
     deassignCall(i1, true);
 }

 public void deassignCall(int i1, boolean flag)
 {
     i.debug("Call " + i1 + " is deassigned");
     f = true;
     if(t != null)
     {
         t.removeObserver(this);
         if(flag)
             c();
     }
     if(h != null)
         h.callDeassigned();
     t = null;
     y = null;
     clearCallback();
     q = null;
     w = 0;
     n = 0;
 }

 private String a(TpCallReleaseCause tpcallreleasecause)
 {
     switch(tpcallreleasecause.Value)
     {
     case 4: // '\004'
         return "BUSY";

     case 5: // '\005'
         return "NO ANSWER";

     case 6: // '\006'
         return "DISCONNECT";

     case 7: // '\007'
         return "REDIRECTED";

     case 8: // '\b'
         return "SERVICE CODE";

     case 11: // '\013'
         return "NOT REACHABLE";

     case 9: // '\t'
         return "ROUTING FAILURE";

     case 10: // '\n'
     default:
         return "undefined";
     }
 }

 public void release(int i1, TpCallReleaseCause tpcallreleasecause)
 {
     if(i.isDebugEnabled())
         i.debug("Call " + i1 + " is release with cause " + a(tpcallreleasecause));
     f = true;
     CallContext callcontext = t;
     deassignCall(i1, false);
     if(callcontext != null)
         callcontext.endCall(false);
     else
         i.error("release() context was null!");
 }

 private void a(TpCallReportRequest atpcallreportrequest[])
     throws P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE, TpCommonExceptions
 {
     if(i.isDebugEnabled())
         i.debug("setCallReportRequests for call " + e + " count=" + atpcallreportrequest.length);
     if(getCallback() == null && atpcallreportrequest.length > 0)
         throw new TpCommonExceptions(17, "No callback address set");
     for(int i1 = 0; i1 < atpcallreportrequest.length; i1++)
     {
         TpCallReportRequest tpcallreportrequest = atpcallreportrequest[i1];
         _mthif(tpcallreportrequest.CallReportType);
         int j1 = 1 << tpcallreportrequest.CallReportType.value();
         switch(tpcallreportrequest.MonitorMode.value())
         {
         case 0: // '\0'
             i.debug("bit=" + Integer.toHexString(j1) + " monitor mode = INTERRUPT");
             n |= j1;
             break;

         case 1: // '\001'
             i.debug("bit=" + Integer.toHexString(j1) + " monitor mode = NOTIFY");
             w |= j1;
             break;

         case 2: // '\002'
             i.debug("bit=" + Integer.toHexString(j1) + " monitor mode = DO_NOT_MONITOR");
             n &= ~j1;
             w &= ~j1;
             break;
         }
     }

 }

 protected void a(TpAddressPlan tpaddressplan)
     throws P_UNSUPPORTED_ADDRESS_PLAN
 {
     if(tpaddressplan.value() != 5)
         throw new P_UNSUPPORTED_ADDRESS_PLAN("Only address plan E164 is supported");
     else
         return;
 }

 protected void a(TpAddress tpaddress)
     throws P_INVALID_ADDRESS
 {
     try
     {
         if(Long.parseLong(tpaddress.AddrString) < 0L)
             throw new P_INVALID_ADDRESS("Address may not be negative");
     }
     catch(NumberFormatException numberformatexception)
     {
         throw new P_INVALID_ADDRESS("Address must be a number");
     }
 }

 protected void _mthif(TpCallReportType tpcallreporttype)
     throws P_INVALID_EVENT_TYPE
 {
     switch(tpcallreporttype.value())
     {
     case 2: // '\002'
     case 3: // '\003'
     case 4: // '\004'
     case 5: // '\005'
     case 6: // '\006'
     case 7: // '\007'
     case 9: // '\t'
     case 11: // '\013'
         return;

     case 8: // '\b'
     case 10: // '\n'
     default:
         i.info("Application requested invalid TpCallReportType:" + tpcallreporttype.value());
         break;
     }
     throw new P_INVALID_EVENT_TYPE("Invalid TpCallReportType:" + tpcallreporttype.value());
 }

 public int routeReq(int i1, TpCallReportRequest atpcallreportrequest[], TpAddress tpaddress, TpAddress tpaddress1, TpAddress tpaddress2, TpAddress tpaddress3, TpCallAppInfo atpcallappinfo[])
     throws P_INVALID_EVENT_TYPE, P_INVALID_NETWORK_STATE, TpCommonExceptions, P_INVALID_ADDRESS, P_INVALID_SESSION_ID, P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_CRITERIA
 {
     i.debug("call " + i1 + " is being routed to " + tpaddress.AddrString);
     f = true;
     a(tpaddress.Plan);
     a(tpaddress);
     a(atpcallreportrequest);
     t.route(tpaddress1.AddrString, tpaddress.AddrString);
     return 0;
 }

 TpCallIdentifier d()
 {
     if(q == null)
         q = new TpCallIdentifier(IpCallImpl.getInstance().getIpCall(), getCallSessionId());
     return q;
 }

 TpCallIdentifier _mthtry()
 {
     if(q == null)
         q = new TpCallIdentifier(null, getCallSessionId());
     return q;
 }

 private TpCallNotificationType _mthfor(int i1)
 {
     switch(i1)
     {
     case 1: // '\001'
     case 2: // '\002'
     case 4: // '\004'
         return TpCallNotificationType.P_ORIGINATING;

     case 3: // '\003'
     default:
         return TpCallNotificationType.P_TERMINATING;
     }
 }

 protected TpAddress _mthbyte()
 {
     return new TpAddress(TpAddressPlan.P_ADDRESS_PLAN_E164, "", "", TpAddressPresentation.P_ADDRESS_PRESENTATION_UNDEFINED, TpAddressScreening.P_ADDRESS_SCREENING_UNDEFINED, "");
 }

 protected TpAddress _mthif(String s1)
 {
     return new TpAddress(TpAddressPlan.P_ADDRESS_PLAN_E164, s1, "name", TpAddressPresentation.P_ADDRESS_PRESENTATION_UNDEFINED, TpAddressScreening.P_ADDRESS_SCREENING_UNDEFINED, "");
 }

 public TpCallEventInfo createCallEventInfo(String s1, String s2, int i1)
 {
     return new TpCallEventInfo(_mthif(s2), _mthif(s1), _mthif(t.getOriginatorNumber()), _mthbyte(), new TpCallAppInfo[0], i1, _mthfor(i1), y._mthif().MonitorMode);
 }

 public TpCallReport createCallReport(TpCallReportType tpcallreporttype)
 {
     TpCallAdditionalReportInfo tpcalladditionalreportinfo = new TpCallAdditionalReportInfo();
     switch(tpcallreporttype.value())
     {
     case 4: // '\004'
         tpcalladditionalreportinfo.Busy(new TpCallReleaseCause(o, 0));
         break;

     case 7: // '\007'
         tpcalladditionalreportinfo.ForwardAddress(_mthif(t.getDestinationNumber()));
         break;

     case 9: // '\t'
         tpcalladditionalreportinfo.RoutingFailure(new TpCallReleaseCause(o, 0));
         break;

     case 11: // '\013'
         tpcalladditionalreportinfo.NotReachable(new TpCallReleaseCause(o, 0));
         break;

     case 6: // '\006'
         tpcalladditionalreportinfo.CallDisconnect(new TpCallReleaseCause(o, 0));
         break;

     case 5: // '\005'
     case 8: // '\b'
     case 10: // '\n'
     default:
         tpcalladditionalreportinfo.Dummy(TpCallReportType.P_CALL_REPORT_UNDEFINED, (short)0);
         break;
     }
     return new TpCallReport(a(tpcallreporttype), r.format(new Date()), tpcallreporttype, tpcalladditionalreportinfo);
 }

 private TpCallFault _mthcase()
 {
     return TpCallFault.P_CALL_FAULT_UNDEFINED;
 }

 public TpCallError createCallError(TpCallErrorType tpcallerrortype)
 {
     TpCallAdditionalErrorInfo tpcalladditionalerrorinfo = new TpCallAdditionalErrorInfo();
     tpcalladditionalerrorinfo.Dummy(TpCallErrorType.P_CALL_ERROR_UNDEFINED, (short)0);
     return new TpCallError(r.format(new Date()), tpcallerrortype, tpcalladditionalerrorinfo);
 }

 private TpCallEndedReport _mthnull()
 {
     return new TpCallEndedReport(0, new TpCallReleaseCause(o, 0));
 }

 private boolean _mthdo(int i1)
 {
     return y != null && (y._mthif().CallEventName & i1) != 0;
 }

 private TpCallMonitorMode a(TpCallReportType tpcallreporttype)
 {
     int i1 = 1 << tpcallreporttype.value();
     if((w & i1) != 0)
         return TpCallMonitorMode.P_CALL_MONITOR_MODE_NOTIFY;
     if((n & i1) != 0)
         return TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT;
     else
         return TpCallMonitorMode.P_CALL_MONITOR_MODE_DO_NOT_MONITOR;
 }

 private boolean a(CallContext callcontext, int i1)
 {
     if(_mthdo(i1))
     {
         i.debug("callEventNotify sent for call " + getCallSessionId());
         ResponseHandler.getInstance().doCallEventNotify(this, callcontext.getDestinationNumber(), i1);
         if(y.isActiveListener())
         {
             b();
             return true;
         }
     }
     return false;
 }

 private boolean _mthdo(TpCallReportType tpcallreporttype)
 {
     if(i.isDebugEnabled())
         i.debug("sendRouteRes for call " + getCallSessionId());
     TpCallMonitorMode tpcallmonitormode = a(tpcallreporttype);
     if(i.isDebugEnabled())
         i.debug("sendRouteRes:eventtype=" + tpcallreporttype.value() + "monitor mode=" + tpcallmonitormode.value());
     if(tpcallmonitormode != TpCallMonitorMode.P_CALL_MONITOR_MODE_DO_NOT_MONITOR)
     {
         ResponseHandler.getInstance().doRouteRes(this, createCallReport(tpcallreporttype), 0);
         if(tpcallmonitormode == TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT)
         {
             b();
             return true;
         } else
         {
             return false;
         }
     } else
     {
         return false;
     }
 }

 private void _mthgoto()
 {
     if(i.isDebugEnabled())
         i.debug("sendRouteErr for call " + getCallSessionId());
     ResponseHandler.getInstance().doRouteErr(this, createCallError(TpCallErrorType.P_CALL_ERROR_UNDEFINED), getCallSessionId());
 }

 private void _mthelse()
 {
     i.debug("callEnded sent for call " + getCallSessionId());
     ResponseHandler.getInstance().doCallEnded(this, _mthnull());
 }

 private void _mthlong()
 {
     i.debug("callFaultDetected sent for call " + getCallSessionId());
     ResponseHandler.getInstance().doCallFaultDetected(this, _mthcase());
 }

 public boolean onAddressCollected(CallContext callcontext)
 {
     return a(callcontext, 2);
 }

 public boolean onAnalyzed(CallContext callcontext)
 {
     return a(callcontext, 4);
 }

 public boolean onAlerting(CallContext callcontext)
 {
     return _mthdo(TpCallReportType.P_CALL_REPORT_ALERTING);
 }

 public boolean onAnswered(CallContext callcontext)
 {
     a(callcontext, 128);
     return _mthdo(TpCallReportType.P_CALL_REPORT_ANSWER);
 }

 public boolean onRefused(CallContext callcontext)
 {
     o = 16;
     return false;
 }

 public boolean onBusy(CallContext callcontext)
 {
     o = 17;
     boolean flag = a(callcontext, 8);
     flag |= _mthdo(TpCallReportType.P_CALL_REPORT_BUSY);
     return flag;
 }

 public boolean onNotReachable(CallContext callcontext)
 {
     o = 20;
     return _mthdo(TpCallReportType.P_CALL_REPORT_NOT_REACHABLE);
 }

 public boolean onRoutingFailed(CallContext callcontext)
 {
     o = 3;
     _mthgoto();
     _mthlong();
     return false;
 }

 public void onCallEnded(CallContext callcontext)
 {
     _mthelse();
     if(h != null)
         h.callEnded();
 }

 public void onDisconnected(CallContext callcontext)
 {
     o = 16;
     _mthdo(TpCallReportType.P_CALL_REPORT_DISCONNECT);
 }

 public boolean onNotAnswered(CallContext callcontext)
 {
     o = 19;
     a(callcontext, 32);
     return _mthdo(TpCallReportType.P_CALL_REPORT_NO_ANSWER);
 }

 public void onRedirected(CallContext callcontext)
 {
     o = 23;
     _mthdo(TpCallReportType.P_CALL_REPORT_REDIRECTED);
 }

 public void getCallInfoReq(int i1, int j1)
     throws TpCommonExceptions, P_INVALID_SESSION_ID
 {
 }

 public void getMoreDialledDigitsReq(int i1, int j1)
     throws TpCommonExceptions, P_INVALID_SESSION_ID
 {
 }

 public void setAdviceOfCharge(int i1, TpAoCInfo tpaocinfo, int j1)
     throws TpCommonExceptions, P_INVALID_SESSION_ID
 {
 }

 public void setCallChargePlan(int i1, TpCallChargePlan tpcallchargeplan)
     throws TpCommonExceptions, P_INVALID_SESSION_ID
 {
 }

 public void superviseCallReq(int i1, int j1, int k1)
     throws TpCommonExceptions, P_INVALID_SESSION_ID
 {
 }

 public void setCallback(IpInterface ipinterface)
     throws TpCommonExceptions
 {
 }

 public void setUICall(UICall uicall)
 {
     h = uicall;
     f = true;
     if(!isMonitoredInInteruptedMode());
 }

 public void clearUICall()
 {
     h = null;
 }

 public boolean isMonitoredInInteruptedMode()
 {
     return y == null ? false : y.isActiveListener();
 }


public void continueProcessing(int callSessionID) throws P_INVALID_NETWORK_STATE, TpCommonExceptions, P_INVALID_SESSION_ID {
	// TODO Auto-generated method stub
	
}

}

