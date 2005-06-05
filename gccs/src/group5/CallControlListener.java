package group5;

import java.util.EventListener;

// Referenced classes:
//            CallControlEvent, CallControlError

public interface CallControlListener
    extends EventListener
{

    public abstract void onEvent(CallControlEvent callcontrolevent);

    public abstract void onError(CallControlError callcontrolerror);
}
