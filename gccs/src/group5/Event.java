//$Id: Event.java,v 1.3 2005/06/12 22:46:51 huuhoa Exp $
package group5;

public interface Event {
	public static final int NONE = 0;

	public static final int ADDRESS_COLLECTED = 2;

	public static final int ADDRESS_ANALYSED = 4;

	public static final int CALLED_PARTY_BUSY = 8;

	public static final int CALLED_PARTY_UNREACHABLE = 16;

	public static final int NO_ANSWER = 32;

	public static final int ROUTE_FAILURE = 64;

	public static final int ANSWER_FROM_CALL_PARTY = 128;

	public static final int ALERTING = 256;

	public static final int CALL_REDIRECTED = 512;

	public static final int CALL_ENDED = 1024;

	public static final int CALL_ABORTED = 2048;

	public static final int CALL_FAULT = 4096;

	public static final int ALL = 8190;
}
