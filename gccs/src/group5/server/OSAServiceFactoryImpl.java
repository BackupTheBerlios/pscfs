//$Id: OSAServiceFactoryImpl.java,v 1.4 2005/06/12 22:46:51 huuhoa Exp $
package group5.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.csapi.IpService;
import org.csapi.IpServiceHelper;
import org.csapi.TpCommonExceptions;
import org.csapi.fw.TpServiceProperty;
import org.csapi.fw.fw_service.service_lifecycle.IpServiceInstanceLifecycleManagerPOA;
import org.omg.PortableServer.Servant;

/**
 * Service Instance Lifecycle Manager This interface will create on demand
 * service (i.e. IpCallControlManager), when the OSA Framework requests that
 * interface.
 * 
 * @author Nguyen Huu Hoa
 * 
 */
public class OSAServiceFactoryImpl extends IpServiceInstanceLifecycleManagerPOA {
	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(ServerFramework.class);
	}

	private Constructor m_constructor;

	/**
	 * Service instance table
	 */
	private Hashtable siTable;

	public OSAServiceFactoryImpl(Class class1) {
		siTable = new Hashtable();
		try {
			m_constructor = class1.getConstructor(new Class[] {
					java.lang.String.class,
					org.csapi.fw.TpServiceProperty[].class });
		} catch (SecurityException securityexception) {
			m_logger.error("Error in getting constructor", securityexception);
		} catch (NoSuchMethodException nosuchmethodexception) {
			m_logger.error("Error in getting constructor",
					nosuchmethodexception);
			throw new RuntimeException("Give up! manager=" + class1);
		}
	}

	/**
	 * Create an reference to IpService with given applicationID, property list,
	 * serviceInstallID
	 */
	public IpService createServiceManager(String applicationID,
			TpServiceProperty srvProp[], String serviceInstanceID)
			throws TpCommonExceptions {
		m_logger.info("createServiceManager");
		try {
			org.omg.CORBA.Object obj;
			Servant servant = (Servant) m_constructor
					.newInstance(new java.lang.Object[] { applicationID,
							srvProp });
			Method method = servant.getClass().getMethod("_this",
					new Class[] { org.omg.CORBA.ORB.class });
			obj = (org.omg.CORBA.Object) method.invoke(servant,
					new java.lang.Object[] { ServerFramework.getORB() });
			siTable.put(serviceInstanceID, (ServiceInstance) servant);
			m_logger.info("Created new service manager instance");
			return IpServiceHelper.narrow(obj);
		} catch (IllegalArgumentException ex) {
			m_logger.error("Error in creating instance", ex);
		} catch (InstantiationException ex) {
			m_logger.error("Error in creating instance", ex);
		} catch (IllegalAccessException ex) {
			m_logger.error("Error in creating instance", ex);
		} catch (InvocationTargetException ex) {
			m_logger.error("Error in creating instance", ex);
		} catch (NoSuchMethodException ex) {
			m_logger.error("Could not find _this method", ex);
			throw new TpCommonExceptions(15, "");
		}
		return null;
	}

	/**
	 * Destroy a service given a serviceInstanceID
	 */
	public void destroyServiceManager(String serviceInstanceID) {
		ServiceInstance serviceinstance = (ServiceInstance) siTable
				.get(serviceInstanceID);
		if (serviceinstance != null) {
			serviceinstance.destroy();
			siTable.remove(serviceInstanceID);
		}
	}

}
