package group5;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CallCriteria
{

    public CallCriteria(String originatorAddress, String terminatorAddress, int criteria, boolean originatorEvents, boolean interruptMode)
    {
    	if (m_logger.isEnabledFor(Level.INFO))
            m_logger.info("Constructing CallCriteria:" + originatorAddress + ":" + terminatorAddress + ":" + criteria + ":" + originatorEvents + ":" + interruptMode);
        setOriginatorAddress(originatorAddress);
        this.terminatorAddress = terminatorAddress;
        this.criteria = criteria;
        registerForOrginatorEvents = originatorEvents;
        this.interruptMode = interruptMode;
    }

    public String getOriginatorAddress()
    {
        return originatorAddress;
    }

    public void setOriginatorAddress(String originatorAddress)
    {
        if(originatorAddress == null || originatorAddress.length() == 0)
            this.originatorAddress = "*";
        else
            this.originatorAddress = originatorAddress;
    }

    public String getTerminatorAddress()
    {
        return terminatorAddress;
    }

    public void setTerminatorAddress(String terminatorAddress)
    {
        this.terminatorAddress = terminatorAddress;
    }

    public int getCriteria()
    {
        return criteria;
    }

    public void setCriteria(int criteria)
    {
        this.criteria = criteria;
    }

    public boolean isRegisterForOrginatorEvents()
    {
        return registerForOrginatorEvents;
    }

    public void setRegisterForOrginatorEvents(boolean registerForOrginatorEvents)
    {
        this.registerForOrginatorEvents = registerForOrginatorEvents;
    }

    public boolean isInterruptMode()
    {
        return interruptMode;
    }

    public void setInterruptMode(boolean interruptMode)
    {
        this.interruptMode = interruptMode;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(originatorAddress).append(",");
        sb.append(terminatorAddress).append(",");
        sb.append(Integer.toString(criteria)).append(",");
        sb.append(Boolean.toString(registerForOrginatorEvents)).append(",");
        sb.append(Boolean.toString(interruptMode));
        sb.append(")");
        return sb.toString();
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof CallCriteria)
        {
            CallCriteria c = (CallCriteria)obj;
            return criteria == c.criteria && originatorAddress.equals(c.originatorAddress) && terminatorAddress.equals(c.terminatorAddress) && interruptMode == c.interruptMode && registerForOrginatorEvents == c.registerForOrginatorEvents;
        } else
        {
            return false;
        }
    }

    private static Logger m_logger;
    private String originatorAddress;
    private String terminatorAddress;
    private int criteria;
    private boolean registerForOrginatorEvents;
    private boolean interruptMode;

    static 
    {
        m_logger = Logger.getLogger(CallCriteria.class);
    }
}
