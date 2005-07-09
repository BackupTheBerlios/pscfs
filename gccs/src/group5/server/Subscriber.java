//$Id: Subscriber.java,v 1.8 2005/07/09 09:23:22 huuhoa Exp $
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

	public static final int Reachable = 0x0001;

	public static final int Unreachable = 0x0002;

	/**
	 * subscriber does not take part in any call.
	 * Feel free to call it :)
	 */
	public static final int Idle = 0x0004;

	/**
	 * it is busying calling other
	 */
	public static final int Busy = 0x0008;
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
		status = Reachable | Idle;
	}

	public Subscriber(String subscrAddr) {
		subscribeAddress = subscrAddr;
		partnerAddress = "";
		status = Reachable | Idle;
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
	 * @param subscribeAddr
	 *            The subscribeAddress to set.
	 */
	public void setSubscribeAddress(String subscribeAddr) {
		this.subscribeAddress = subscribeAddr;
	}

	/**
	 * @return Returns the subscribeAddress.
	 */
	public String getSubscribeAddress() {
		return subscribeAddress;
	}

	/**
	 * @param partnerAddr
	 *            The partnerAddress to set.
	 */
	public void receiveCallFrom(String partnerAddr) {
		this.partnerAddress = partnerAddr;
		status |= Busy;
	}

	public void endCall()
	{
		status &= ~Busy;
	}
	/**
	 * @return Returns the partnerAddress.
	 */
	public String getPartnerAddress() {
		return partnerAddress;
	}
}
