//$Id: GeneralEvent.java,v 1.3 2005/06/12 22:46:51 huuhoa Exp $
package group5;

import java.io.Serializable;
import java.util.EventObject;

public abstract class GeneralEvent extends EventObject implements Serializable {

	public static final int CAUSE_UNKNOWN = 0;

	public static final int CAUSE_REQUESTED = 1;

	public static final int CAUSE_UNSOLLICITED = 2;

	public static final int CAUSE_TRIGGERED = 3;

	public static final int CAUSE_PERIODIC = 4;

	private static final String causeText[] = { "CAUSE_UNKNOWN",
			"CAUSE_REQUESTED", "CAUSE_UNSOLLICITED", "CAUSE_TRIGGERED",
			"CAUSE_PERIODIC" };

	protected int assignmentId;

	protected int cause;

	protected long when;

	protected GeneralEvent(Object source, int assignmentId, int cause) {
		super(source);
		this.assignmentId = assignmentId;
		this.cause = cause;
		when = System.currentTimeMillis();
	}

	public int getAssignmentId() {
		return assignmentId;
	}

	public int getCause() {
		return cause;
	}

	public long getWhen() {
		return when;
	}

	public static String getCauseAsString(int cause) {
		if (cause >= 0 && cause < causeText.length)
			return causeText[cause];
		else
			return null;
	}

}