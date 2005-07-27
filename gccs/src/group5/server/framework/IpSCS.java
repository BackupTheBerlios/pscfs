//$Id: IpSCS.java,v 1.6 2005/07/27 08:47:09 huuhoa Exp $
/**
 * 
 */
package group5.server.framework;

/**
 * Interface to provide service management functionalities
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
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
