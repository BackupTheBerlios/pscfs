//$Id: OSAServiceFactoryImpl.java,v 1.9 2005/07/01 09:20:13 huuhoa Exp $
package group5.server.framework;

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
	private static Logger m_logger;

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
		} catch (SecurityException ex) {
			m_logger.error("Error in getting constructor", ex);
		} catch (NoSuchMethodException ex) {
			m_logger.error("Error in getting constructor",
					ex);
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
			m_logger.debug("create new instance for " + applicationID);
			Method method = servant.getClass().getMethod("_this",
					new Class[] { org.omg.CORBA.ORB.class });
			m_logger.debug("got _this method for " + servant.getClass());
			obj = (org.omg.CORBA.Object) method.invoke(servant,
					new java.lang.Object[] { ServerFramework.getORB() });
			m_logger.debug("got reference to CORBA interface: " + obj.toString());
			m_logger.debug("serviceInstanceID: " + serviceInstanceID);
			//siTable.put(serviceInstanceID, (ServiceInstance) servant);
			m_logger.debug("Created new service manager instance");
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
		m_logger.debug("return null object when creating service instance");
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
