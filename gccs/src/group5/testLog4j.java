//$Id: testLog4j.java,v 1.2 2005/06/12 22:24:04 huuhoa Exp $
/**
 * 
 */
package group5;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * @author Nguyen Huu Hoa
 * 
 */
public class testLog4j {

	/**
	 * 
	 */
	public testLog4j() {
		super();
		// TODO Auto-generated constructor stub
	}

	static Logger logger = Logger.getLogger(testLog4j.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BasicConfigurator.configure();
		logger.debug("Hello world.");
		logger.info("What a beatiful day.");
	}
}
