//$Id: IpEventHandler.java,v 1.1 2005/06/13 11:17:51 huuhoa Exp $
/**
 * 
 */
package group5.server;

/**
 * @author Nguyen Huu Hoa
 *
 */
public interface IpEventHandler {
	public abstract void onEvent(int eventID, Object eventData);
}
