package group5;
import java.io.Serializable;
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.IpCall;

// Referenced classes:
//            IpCallControlManager, Event, IpCall

public class CallControlEvent extends GeneralEvent
    implements Event, Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 5558427124773383154L;
	public CallControlEvent(IpCallControlManager source, int assignmentId, int cause, IpCall call, int callEvent)
    {
        super(source, assignmentId, cause);
        this.call = call;
        this.callEvent = callEvent;
    }

    public IpCall getCall()
    {
        return call;
    }

    public int getCallEvent()
    {
        return callEvent;
    }

    /**
     * was getAdapter
     * @return IpCallControlManager
     */
    public IpCallControlManager getService()
    {
        return (IpCallControlManager)source;
    }

    public String toString()
    {
        return call.toString() + ",CallEvent=" + callEvent;
    }

    private IpCall call;
    private int callEvent;
}
