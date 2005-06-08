package impl;


import java.util.*;
import org.apache.log4j.Category;

//Referenced classes of package com.lucent.isg.simulator.callcontrol.core:
//         InitiatorLeg, TerminatorLeg, CCSimulatorCore, CCSubscriberGroup

public final class CallContext
{
 public static abstract class a
 {

     public abstract boolean a(CallEventObserver calleventobserver, CallContext callcontext);

     public boolean execute(CallContext callcontext)
     {
         boolean flag = false;
         synchronized(callcontext)
         {
             if(callcontext._fldnull != null)
             {
                 CallContext._fldchar.error("##ERROR## on.iter!=null on=" + callcontext);
                 throw new ConcurrentModificationException("Parallel iteration");
             }
             callcontext._fldnull = callcontext.b.iterator();
             while(callcontext._fldnull.hasNext()) 
             {
                 CallEventObserver calleventobserver = (CallEventObserver)callcontext._fldnull.next();
                 flag |= a(calleventobserver, callcontext);
             }
             callcontext._fldnull = null;
         }
         return flag;
     }

     public a()
     {
     }
 }


 public CallContext()
 {
     b = new ArrayList();
     _fldtry = 1;
     _fldvoid = _fldlong++;
     _fldnull = null;
 }

 public boolean route(String s, String s1)
     throws NumberFormatException
 {
     _fldchar.info("CallContext::route orig=" + s + " dest=" + s1);
     if(_fldcase != null)
     {
         TimerEvent timerevent = _fldcase;
         _fldcase = null;
         CCSimulatorCore.getInstance().cancelEvent(timerevent);
     }
     if(_fldtry == 12)
     {
         _mthdo();
         return false;
     }
     if(_flddo == null)
     {
         _fldelse = com.lucent.isg.simulator.callcontrol.a.a.a(s);
         _flddo = com.lucent.isg.simulator.callcontrol.a.a.a(s1);
         c = _flddo;
         CCSimulatorCore.getInstance()._mthif(this);
         CCSubscriberGroup ccsubscribergroup = CCSimulatorCore.getInstance().findSubscriber(_fldelse);
         if(ccsubscribergroup != null)
         {
             _fldchar.info("CallContext::route caller found");
             if(ccsubscribergroup.getCurrentCall() != null)
                 ccsubscribergroup.getCurrentCall().endCall(true);
             _fldnew = new InitiatorLeg(ccsubscribergroup, this);
             _fldnew.getInitiator().setUSStatus(2);
             _fldtry = 998;
             boolean flag = (new a() {

                 public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
                 {
                     return calleventobserver.onAddressCollected(callcontext);
                 }

             }).execute(this);
             if(_fldtry == 12)
                 return true;
             _fldtry = 2;
             if(!flag)
                 CCSimulatorCore.getInstance().addEvent(new TimerEvent() {

                     public void onTimer()
                     {
                         analyzeCall(true);
                     }

                 }, 2000L);
             return true;
         } else
         {
             invalidNumber();
             return false;
         }
     } else
     {
         c = com.lucent.isg.simulator.callcontrol.a.a.a(s1);
         (new a() {

             public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
             {
                 calleventobserver.onRedirected(callcontext);
                 return false;
             }

         }).execute(this);
         _fldtry = 2;
         return analyzeCall(false);
     }
 }

 public boolean analyzeCall(boolean flag)
 {
     _fldchar.info("CallContext::analyzeCall dest=" + c);
     CCSubscriberGroup ccsubscribergroup = CCSimulatorCore.getInstance().findSubscriber(c);
     if(ccsubscribergroup != null)
     {
         _fldchar.info("analyzeCall: Callee found starting routing");
         boolean flag1 = false;
         if(flag)
             flag1 = (new a() {

                 public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
                 {
                     return calleventobserver.onAnalyzed(callcontext);
                 }

             }).execute(this);
         if(_fldtry == 12)
             return true;
         _fldtry = 999;
         if(!flag1)
             a(ccsubscribergroup);
         return true;
     } else
     {
         _mthdo();
         return false;
     }
 }

 public int getState()
 {
     return _fldtry;
 }

 public void addObserver(CallEventObserver calleventobserver)
 {
     b.add(calleventobserver);
 }

 public void removeObserver(CallEventObserver calleventobserver)
 {
     synchronized(this)
     {
         if(_fldnull != null)
             _fldnull.remove();
         else
             b.remove(calleventobserver);
     }
 }

 public CCSubscriberGroup getPeer(CCSubscriberGroup ccsubscribergroup)
 {
     if(_fldif != null)
     {
         if(_fldnew.getInitiator() == ccsubscribergroup)
             return _fldif.getTerminator();
         else
             return _fldnew.getInitiator();
     } else
     {
         return null;
     }
 }

 public void invalidNumber()
 {
     _fldtry = 10;
     _mthdo();
 }

 public void endCall(boolean flag)
 {
     if(!flag && _fldtry == 5)
         (new a() {

             public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
             {
                 calleventobserver.onRefused(callcontext);
                 return false;
             }

         }).execute(this);
     clear(true);
 }

 private void a(CCSubscriberGroup ccsubscribergroup)
 {
     _fldchar.info("Checking callee status...");
     ccsubscribergroup.getUSStatus();
     JVM INSTR tableswitch 0 2: default 62
 //                   0 50
 //                   1 40
 //                   2 45;
        goto _L1 _L2 _L3 _L4
_L3:
     notReachable();
     return;
_L4:
     try
     {
         busy();
         return;
     }
     catch(Throwable throwable)
     {
         throwable.printStackTrace();
     }
       goto _L5
_L2:
     if(ccsubscribergroup.getCurrentCall() != null)
     {
         busy();
         return;
     }
_L1:
     _fldchar.info("Callee reachable");
     if(!ccsubscribergroup.beforeAlerting(this))
     {
         _fldchar.info("Behavior canceled alert()");
         return;
     }
     _fldif = new TerminatorLeg(ccsubscribergroup, this);
     a();
_L5:
 }

 private void a()
 {
     _fldchar.info("CallContext::alert() target=" + c);
     _fldtry = 5;
     boolean flag = (new a() {

         public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
         {
             return calleventobserver.onAlerting(callcontext);
         }

     }).execute(this);
     if(!flag)
         answer(false);
 }

 public void answer(boolean flag)
 {
     final int answerdelay = flag ? 1 : _fldif.getTerminator().beforeAnswering(this);
     if(answerdelay > 0)
         CCSimulatorCore.getInstance().addEvent(new TimerEvent() {

             public void onTimer()
             {
                 if(_fldtry != 5)
                     return;
                 if(answerdelay < CCSimulatorCore.getInstance().getNoanswerTime())
                 {
                     boolean flag1 = (new a() {

                         public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
                         {
                             return calleventobserver.onAnswered(callcontext);
                         }

                     }).execute(CallContext.this);
                     _fldtry = 1000;
                     if(!flag1)
                         _mthif();
                 } else
                 {
                     boolean flag2 = (new a() {

                         public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
                         {
                             return calleventobserver.onNotAnswered(callcontext);
                         }

                     }).execute(CallContext.this);
                     _fldtry = 14;
                     if(!flag2)
                         clear(true);
                 }
             }

         }, Math.min(answerdelay, CCSimulatorCore.getInstance().getNoanswerTime()));
 }

 public void refuse(int i)
 {
     CCSimulatorCore.getInstance().addEvent(new TimerEvent() {

         public void onTimer()
         {
             if(_fldtry != 5)
                 return;
             _fldtry = 9;
             boolean flag = (new a() {

                 public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
                 {
                     return calleventobserver.onRefused(callcontext);
                 }

             }).execute(CallContext.this);
             if(!flag)
                 clear(true);
         }

     }, i);
 }

 public void busy()
 {
     _fldtry = 8;
     boolean flag = (new a() {

         public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
         {
             return calleventobserver.onBusy(callcontext);
         }

     }).execute(this);
     if(!flag)
         clear(true);
 }

 private void _mthdo()
 {
     _fldtry = 13;
     boolean flag = (new a() {

         public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
         {
             return calleventobserver.onRoutingFailed(callcontext);
         }

     }).execute(this);
     if(!flag)
         clear(true);
 }

 public void notReachable()
 {
     _fldchar.info("Callee not reachable:" + c);
     _fldtry = 11;
     boolean flag = (new a() {

         public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
         {
             return calleventobserver.onNotReachable(callcontext);
         }

     }).execute(this);
     if(!flag)
         clear(true);
 }

 public CCSubscriberGroup getCaller()
 {
     return _fldnew == null ? null : _fldnew.getInitiator();
 }

 public CCSubscriberGroup getCallee()
 {
     return _fldif == null ? null : _fldif.getTerminator();
 }

 public Group getCallerInfo(int i)
 {
     if(_fldnew == null)
         return new Group("?", "?", 0, 1, "?", "?");
     Group group = _fldnew.getInitiator().getGroupInfo();
     if(group.callstatus != 15)
         group.callstatus = i;
     group.peermsisdn = c;
     if(_fldif != null)
         group.peername = _fldif.getTerminator().getName();
     return group;
 }

 public Group getCalleeInfo(int i)
 {
     if(_fldif != null)
     {
         Group group = _fldif.getTerminator().getGroupInfo();
         if(group.callstatus != 15)
         {
             group.callstatus = i;
             group.peermsisdn = _fldnew.getInitiator().getMsisdn();
             group.peername = _fldnew.getInitiator().getName();
         }
         return group;
     } else
     {
         return null;
     }
 }

 public void clear(boolean flag)
 {
     if(_fldtry != 12)
     {
         _fldtry = 12;
         CCSimulatorCore.getInstance().addEvent(new TimerEvent() {

             public void onTimer()
             {
                 (new a() {

                     public boolean a(CallEventObserver calleventobserver, CallContext callcontext)
                     {
                         calleventobserver.onCallEnded(callcontext);
                         return false;
                     }

                 }).execute(CallContext.this);
                 CCSimulatorCore.getInstance().a(CallContext.this);
                 _fldnew.clearCall();
                 if(_fldif != null)
                 {
                     _fldif.clearCall();
                     _fldif = null;
                 }
                 b.clear();
             }

         }, flag ? 1000L : 0L);
     }
 }

 public String toString()
 {
     String s = "\nCall status: " + _fldtry + " originatorNumber:" + _fldelse + " originalDestination:" + _flddo + " destination:" + c;
     return s;
 }

 public void continueProcessing()
 {
     _fldchar.info("continueProcessing state=" + _fldtry);
     switch(_fldtry)
     {
     case 2: // '\002'
     case 998: 
         analyzeCall(true);
         break;

     case 5: // '\005'
         answer(false);
         break;

     case 1000: 
         _mthif();
         break;

     case 999: 
         CCSubscriberGroup ccsubscribergroup = CCSimulatorCore.getInstance().findSubscriber(c);
         a(ccsubscribergroup);
         break;

     case 8: // '\b'
     case 11: // '\013'
     case 13: // '\r'
     case 14: // '\016'
         clear(true);
         break;

     default:
         _fldchar.warn("Unexpected state in continueProcessing: " + _fldtry);
         break;

     case 12: // '\f'
         break;
     }
 }

 private void _mthif()
 {
     if(_fldif != null)
     {
         _fldif.getTerminator().setUSStatus(2);
         _fldtry = 6;
     }
 }

 public int getSessionId()
 {
     return _fldvoid;
 }

 public void startIdleTimer()
 {
     CCSimulatorCore.getInstance().addEvent(_fldcase = new TimerEvent() {

         public void onTimer()
         {
             CallContext._fldchar.info("IdleTimer for call " + getSessionId());
             CCSimulatorCore.getInstance().a(CallContext.this);
         }

     }, 10000L);
 }

 public String getDestinationNumber()
 {
     return c;
 }

 public String getInitialDestinationNumber()
 {
     return _flddo;
 }

 public String getOriginatorNumber()
 {
     return _fldelse;
 }

 public boolean allowsUI()
 {
     switch(_fldtry)
     {
     case 2: // '\002'
     case 5: // '\005'
     case 11: // '\013'
     case 14: // '\016'
     case 998: 
     case 999: 
         return true;
     }
     return false;
 }

 static Class _mthclass$(String s)
 {
     return Class.forName(s);
     ClassNotFoundException classnotfoundexception;
   //  classnotfoundexception;
     throw new NoClassDefFoundError(classnotfoundexception.getMessage());
 }

 private static final int _fldfor = 2000;
 public static final int _fldint = 1000;
 private static final int _fldbyte = 998;
 private static final int _fldgoto = 999;
 private static final int a = 1000;
 private static Category _fldchar;
 private List b;
 private String _fldelse;
 private String _flddo;
 private String c;
 private volatile int _fldtry;
 private InitiatorLeg _fldnew;
 private TerminatorLeg _fldif;
 private static int _fldlong = 0;
 private int _fldvoid;
 private TimerEvent _fldcase;
 private Iterator _fldnull;

}
