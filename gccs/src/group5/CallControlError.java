// $Id: CallControlError.java,v 1.5 2005/06/12 22:36:54 huuhoa Exp $
/**
 * 
 */
package group5;

import java.io.Serializable;
import org.csapi.cc.gccs.IpCallControlManager;

// Referenced classes of package com.lucent.isg.appsdk.callcontrol:
//            CallControlAdapter

public final class CallControlError extends GeneralEvent implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4513014954700266962L;

	public CallControlError(IpCallControlManager source, int assignmentId,
			int cause, int errorType) {
		super(source, assignmentId, cause);
		this.errorType = 0;
		this.errorType = errorType;
	}

	public int getErrorType() {
		return errorType;
	}

	/**
	 * was getAdapter
	 * 
	 * @return IpCallControlManager
	 */
	public IpCallControlManager getService() {
		return (IpCallControlManager) source;
	}

	public String toString() {
		return "Undefined errortype";
	}

	public static final int UNKNOWN = 0;

	private int errorType;
}
