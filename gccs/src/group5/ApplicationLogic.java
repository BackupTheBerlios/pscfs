// $Id: ApplicationLogic.java,v 1.2 2005/06/12 22:24:04 huuhoa Exp $
/**
 * 
 */
package group5;

import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import impl.IpCallImpl;

/**
 * @author Nguyen Huu Hoa
 * 
 */
public class ApplicationLogic {

	/**
	 * 
	 */
	public ApplicationLogic() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void callEventNotify(TpCallIdentifier callReference,
			TpCallEventInfo eventInfo, int assignmentID) {
		IpCallImpl ipCall = (IpCallImpl) callReference.CallReference;
	}

}
