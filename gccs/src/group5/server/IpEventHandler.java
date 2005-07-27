//$Id: IpEventHandler.java,v 1.4 2005/07/27 08:47:08 huuhoa Exp $
/**
 * 
 */
package group5.server;

import org.csapi.TpAddress;
import org.csapi.cc.gccs.TpCallReport;

/**
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public interface IpEventHandler {
	public void onEvent(int eventID, CallEvent eventData);

	public void onRouteReq(int callSessionID, TpAddress targetAddr,
			TpAddress origAddr);

	public void onDeassignCall(int callSessionID);

	public void onReleaseCall(int callSessionID);

	public void onRouteRes(int callSessionID, TpCallReport eventReport,
			int callLegSessionID);
}
