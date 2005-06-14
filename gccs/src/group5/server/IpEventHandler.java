//$Id: IpEventHandler.java,v 1.2 2005/06/14 19:30:33 aachenner Exp $
/**
 * 
 */
package group5.server;

import org.csapi.TpAddress;
import org.csapi.cc.gccs.TpCallReport;

/**
 * @author Nguyen Huu Hoa
 *
 */
public interface IpEventHandler {
	public void onEvent(int eventID, CallEvent eventData);
	public void onRouteReq(int callSessionID, TpAddress targetAddr, TpAddress origAddr);
	public void onDeassignCall(int callSessionID);
	public void onReleaseCall(int callSessionID);
	public void onRouteRes(int callSessionID, TpCallReport eventReport, int callLegSessionID );
}
