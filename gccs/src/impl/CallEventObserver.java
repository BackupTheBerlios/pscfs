package impl;
//Khuong
public interface CallEventObserver {
		
		public abstract boolean onAddressCollected(CallContext callcontext);

	    public abstract boolean onAnalyzed(CallContext callcontext);

	    public abstract boolean onAlerting(CallContext callcontext);

	    public abstract boolean onAnswered(CallContext callcontext);

	    public abstract boolean onRefused(CallContext callcontext);

	    public abstract boolean onRoutingFailed(CallContext callcontext);

	    public abstract boolean onBusy(CallContext callcontext);

	    public abstract boolean onNotReachable(CallContext callcontext);

	    public abstract void onCallEnded(CallContext callcontext);

	    public abstract boolean onNotAnswered(CallContext callcontext);

	    public abstract void onRedirected(CallContext callcontext);

	
}
