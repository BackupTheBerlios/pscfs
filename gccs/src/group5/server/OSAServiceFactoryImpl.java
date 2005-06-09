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

	public IpService createServiceManager(String s,
			TpServiceProperty atpserviceproperty[], String s1)
			throws TpCommonExceptions {
		m_logger.info("createServiceManager");
		try {
			org.omg.CORBA.Object obj;
			Servant servant = (Servant) m_constructor
					.newInstance(new java.lang.Object[] { s, atpserviceproperty });
			Method method = servant.getClass().getMethod("_this",
					new Class[] { org.omg.CORBA.ORB.class });
			obj = (org.omg.CORBA.Object) method.invoke(servant,
					new java.lang.Object[] { ServerFramework.getORB() });
			siTable.put(s1, (ServiceInstance) servant);
			m_logger.info("Created new service manager instance");
			return IpServiceHelper.narrow(obj);
		} catch (IllegalArgumentException illegalargumentexception) {
			m_logger.error("Error in creating instance",
					illegalargumentexception);
		} catch (InstantiationException instantiationexception) {
			m_logger
					.error("Error in creating instance", instantiationexception);
		} catch (IllegalAccessException illegalaccessexception) {
			m_logger
					.error("Error in creating instance", illegalaccessexception);
		} catch (InvocationTargetException invocationtargetexception) {
			m_logger.error("Error in creating instance",
					invocationtargetexception);
		} catch (NoSuchMethodException nosuchmethodexception) {
			m_logger
					.error("Could not find _this method", nosuchmethodexception);
			throw new TpCommonExceptions(15, "");
		}
		return null;
	}

	public void destroyServiceManager(String s) {
		ServiceInstance serviceinstance = (ServiceInstance) siTable.get(s);
		if (serviceinstance != null) {
			serviceinstance.destroy();
			siTable.remove(s);
		}
	}

}
