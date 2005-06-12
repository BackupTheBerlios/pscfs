//$Id: GeneralException.java,v 1.2 2005/06/12 22:24:04 huuhoa Exp $
package group5;

public abstract class GeneralException extends Exception {

	public static final int UNKNOWN = 0;

	public static final int JAVA_EXCEPTION = 1;

	public static final int SDK_EXCEPTION = 2;

	public static final int CORBA_EXCEPTION = 3;

	public static final int OSA_EXCEPTION = 4;

	private int category;

	private int exceptionType;

	private String categoryStrings[] = { "?", "java", "SDK", "CORBA", "OSA" };

	public GeneralException() {
		category = 0;
		exceptionType = 0;
	}

	public GeneralException(String reason) {
		super(reason);
		category = 0;
		exceptionType = 0;
	}

	public GeneralException(int category) {
		this.category = 0;
		exceptionType = 0;
		this.category = category;
	}

	public GeneralException(String reason, int category) {
		super(reason);
		this.category = 0;
		exceptionType = 0;
		this.category = category;
	}

	public int getCategory() {
		return category;
	}

	public int getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(int type) {
		exceptionType = type;
	}

	public String toString() {
		int i = category;
		if (i < 0 || i >= categoryStrings.length)
			i = 0;
		String result = "[category = " + categoryStrings[i];
		String msg = getMessage();
		if (msg != null)
			result = result + ", message = " + msg;
		result = result + "]";
		return result;
	}
}