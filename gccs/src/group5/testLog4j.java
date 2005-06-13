//$Id: testLog4j.java,v 1.3 2005/06/13 09:11:51 huuhoa Exp $
/**
 * 
 */
package group5;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

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
