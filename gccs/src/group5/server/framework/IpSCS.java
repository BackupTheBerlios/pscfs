//$Id: IpSCS.java,v 1.5 2005/06/13 09:11:51 huuhoa Exp $
/**
 * 
 */
package group5.server.framework;

/**
 * @author Nguyen Huu Hoa Interface to provide service management
 *         functionalities
 */
public interface IpSCS {
	/**
	 * Start the service
	 * 
	 * @return The result of action
	 * @value 0 means service was started successfully
	 * @value other: error code
	 */
	public abstract int startService();

	/**
	 * Stop the service
	 * 
	 * @return The result of action
	 * @value 0 means service was stopped successfully
	 * @value other: error code
	 */
	public abstract int stopService();
}
