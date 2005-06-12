//$Id: Subscriber.java,v 1.3 2005/06/12 22:24:04 huuhoa Exp $
/**
 * 
 */
package group5.server;

/**
 * Represent for a subscriber
 * 
 * @author Nguyen Huu Hoa
 * 
 */
public class Subscriber {
	/**
	 * the status of the subscriber - reachable - unreachable - busy - ...
	 */
	private int status;

	public static final int Reachable = 0;

	public static final int Unreachable = 1;

	public static final int Busy = 2;

	/**
	 * Address of the subscriber in the network
	 */
	private String subscribeAddress;

	/**
	 * Address of the partner if the subscriber is on the call with other
	 */
	private String partnerAddress;

	public Subscriber() {
		subscribeAddress = "";
		partnerAddress = "";
		status = Reachable;
	}

	public Subscriber(String subscrAddr) {
		subscribeAddress = subscrAddr;
		partnerAddress = "";
		status = Reachable;
	}

	/**
	 * @param status
	 *            The status to set.
	 * @exception org.omg.CORBA.BAD_PARAM
	 *                This exception is thrown when status is not valid. The
	 *                valid statuses are:
	 *                <ul>
	 *                <li>Subscriber.Reachable
	 *                <li>Subscriber.Unreachable
	 *                <li>Subscriber.Busy
	 *                </ul>
	 */
	public void setStatus(int status) throws org.omg.CORBA.BAD_PARAM {
		switch (status) {
		case Reachable:
		case Unreachable:
		case Busy:
			this.status = status;
			break;
		default:
			throw new org.omg.CORBA.BAD_PARAM();
		}
	}

	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param subscribeAddress
	 *            The subscribeAddress to set.
	 */
	public void setSubscribeAddress(String subscribeAddress) {
		this.subscribeAddress = subscribeAddress;
	}

	/**
	 * @return Returns the subscribeAddress.
	 */
	public String getSubscribeAddress() {
		return subscribeAddress;
	}

	/**
	 * @param partnerAddress
	 *            The partnerAddress to set.
	 */
	public void setPartnerAddress(String partnerAddress) {
		this.partnerAddress = partnerAddress;
	}

	/**
	 * @return Returns the partnerAddress.
	 */
	public String getPartnerAddress() {
		return partnerAddress;
	}
}
