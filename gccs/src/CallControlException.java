
public final class CallControlException extends GeneralException
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1811429993110792340L;
	public CallControlException(String reason, int type)
    {
        super(reason, 4);
        setExceptionType(type);
    }

    public CallControlException(String reason, int type, int category)
    {
        super(reason, category);
        setExceptionType(type);
    }

    public String toString()
    {
        int type = getExceptionType();
        if(type < 0 || type >= exceptionTypes.length)
            type = 0;
        return "CallControlException: " + exceptionTypes[type] + " " + super.toString();
    }

    public static final int FAILURE = 0;
    public static final int UNKNOWN_EVENT = 1;
    public static final int INVALID_PARAMETER = 2;
    public static final int INVALID_ASSIGNMENT_ID = 3;
    public static final int TIMEOUT = 4;
    public static final int INVALID_ADDRESS = 5;
    public static final int INVALID_NETWORK_STATE = 6;
    public static final int INVALID_CRITERIA = 7;
    public static final int INVALID_INTERFACE_TYPE = 8;
    public static final int INVALID_EVENT_TYPE = 9;
    public static final int COMMON_EXCEPTION = 10;
    public static final int INVALID_SESSION_ID = 11;
    public static final int UNSUPPORTED_ADDRESS_PLAN = 12;
    private static final String exceptionTypes[] = {
        "Failure", "Unknown event", "Invalid parameter(s)", "Invalid ID", "Timeout", "Invalid Address", "Invalid Network state"
    };

}
