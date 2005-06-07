package group5;

import org.omg.CORBA.UserException;

public final class P_INVALID_NAME_SERVICE extends UserException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 0x10000000;
	private static final String id = "P_INVALID_NAME_SERVICE"; 

	public P_INVALID_NAME_SERVICE()
	{
		super(id);
	}

	public java.lang.String ExtraInformation;
	public P_INVALID_NAME_SERVICE(java.lang.String _reason,java.lang.String ExtraInformation)
	{
		super(id + ": " + _reason);
		this.ExtraInformation = ExtraInformation;
	}
	public P_INVALID_NAME_SERVICE(java.lang.String ExtraInformation)
	{
		super(id);
		this.ExtraInformation = ExtraInformation;
	}
}
