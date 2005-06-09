//Source File Name:   ServiceRegistration.java
package group5.server;

/**
 * 
 * @author Nguyen Huu Hoa
 *
 */
import org.apache.log4j.Logger;
import org.csapi.P_INVALID_INTERFACE_NAME;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.TpCommonExceptions;
import org.csapi.fw.P_ACCESS_DENIED;
import org.csapi.fw.P_ILLEGAL_SERVICE_ID;
import org.csapi.fw.P_INVALID_ACCESS_TYPE;
import org.csapi.fw.P_INVALID_AUTH_TYPE;
import org.csapi.fw.P_INVALID_DOMAIN_ID;
import org.csapi.fw.P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY;
import org.csapi.fw.P_UNKNOWN_SERVICE_ID;
import org.csapi.fw.TpAuthDomain;
import org.csapi.fw.TpDomainID;
import org.csapi.fw.fw_access.trust_and_security.IpAPILevelAuthentication;
import org.csapi.fw.fw_access.trust_and_security.IpAPILevelAuthenticationHelper;
import org.csapi.fw.fw_access.trust_and_security.IpAccess;
import org.csapi.fw.fw_access.trust_and_security.IpAccessHelper;
import org.csapi.fw.fw_access.trust_and_security.IpClientAccessPOA;
import org.csapi.fw.fw_access.trust_and_security.IpInitial;
import org.csapi.fw.fw_service.service_lifecycle.IpServiceInstanceLifecycleManagerPOA;
import org.csapi.fw.fw_service.service_registration.IpFwServiceRegistration;
import org.csapi.fw.fw_service.service_registration.IpFwServiceRegistrationHelper;

// Referenced classes of package com.lucent.isg.simulator.framework:
// OSAServiceFactoryImpl, SCSProperties, IpAppAuthenticationImpl
public final class ServiceRegistration {
	private static final class IpClientAccessImpl extends IpClientAccessPOA {

		public void terminateAccess(String s, String s1, byte abyte0[]) {
		}

		private IpClientAccessImp() {
		}
	}

	private static Logger m_logger = Logger
			.getLogger(ServiceRegistration.class);

	private static ServiceRegistrationUtil m_serviceRegistration = new ServiceRegistrationUtil();

	private static final String _fldnew = "ServiceRegistration";

	private IpInitial m_ipInitial;

	private IpAccess _fldif;

	private IpFwServiceRegistration _fldfor;

	private IpAPILevelAuthentication a;

	private ServiceRegistration() {
		_fldfor = null;
		a = null;
	}

	static void _mthdo() {
		m_serviceRegistration._mthfor();
	}

	private void _mthfor() {
		if (_fldfor != null) {
			_fldfor._release();
			_fldfor = null;
		}
		if (_fldif != null) {
			_fldif._release();
			_fldif = null;
		}
		if (a != null) {
			try {
				if (!a._non_existent())
					a.abortAuthentication();
			} catch (Exception exception) {
			}
			a._release();
			a = null;
		}
	}

	public static String a(String s, SCSProperties scsproperties, Class class1)
			throws TpCommonExceptions {
		return m_serviceRegistration
				.a(
						s,
						scsproperties,
						((IpServiceInstanceLifecycleManagerPOA) (new OSAServiceFactoryImpl(
								class1))));
	}

	public static String registerService(String s, Class class1) throws TpCommonExceptions {
		return a(s, new SCSProperties(s, "1.0"), class1);
	}

	public static String registerService(
			String s,
			SCSProperties scsproperties,
			IpServiceInstanceLifecycleManagerPOA ipserviceinstancelifecyclemanagerpoa)
			throws TpCommonExceptions {
		return m_serviceRegistration.a(s, scsproperties,
				ipserviceinstancelifecyclemanagerpoa);
	}

	public static String registerService(
			String s,
			IpServiceInstanceLifecycleManagerPOA ipserviceinstancelifecyclemanagerpoa)
			throws TpCommonExceptions {
		return registerService(s, new SCSProperties(s, "1.0"),
				ipserviceinstancelifecyclemanagerpoa);
	}

	private synchronized String a(
			String s,
			SCSProperties scsproperties,
			IpServiceInstanceLifecycleManagerPOA ipserviceinstancelifecyclemanagerpoa)
			throws TpCommonExceptions {
		String s1 = a(s, scsproperties);
		String s2 = scsproperties.getServiceName();
		try {
			if (m_logger.isDebugEnabled())
				m_logger.debug("ServiceRegistrationUtil::scsId=" + s1);
			if (s1 != null) {
				IpFwServiceRegistration ipfwserviceregistration = obtainIpFwServiceRegistration();
				ipfwserviceregistration.announceServiceAvailability(s1,
						ipserviceinstancelifecyclemanagerpoa._this(ORBUtil
								.getInterceptorORB()));
			} else {
				throw new TpCommonExceptions("Error discovering service name="
						+ s2, 15, s2);
			}
		} catch (P_INVALID_INTERFACE_TYPE p_invalid_interface_type) {
			throw new TpCommonExceptions("Invalid interface type", 14, s1);
		} catch (P_ILLEGAL_SERVICE_ID p_illegal_service_id) {
			throw new TpCommonExceptions("Illegal service ID", 14, s1);
		} catch (P_UNKNOWN_SERVICE_ID p_unknown_service_id) {
			throw new TpCommonExceptions("Unknown service ID", 14, s1);
		}
		return s1;
	}



}

// import org.csapi.*;
// import org.csapi.fw.*;
// import org.csapi.fw.fw_access.trust_and_security.IpAccess;
// import
// org.csapi.fw.fw_service.service_lifecycle.IpServiceInstanceLifecycleManager;
// import org.csapi.fw.fw_service.service_registration.IpFwServiceRegistration;
// import
// org.csapi.fw.fw_service.service_registration.IpFwServiceRegistrationHelper;
//
// public class ServiceRegistration {
//
// private IpAccess ipAccess;
//
// private IpFwServiceRegistration registration;
//
// private SCSProperties serviceDescriptionHelper;
//
// private String serviceID;
//
// private IpServiceInstanceLifecycleManager manager;
//
// private boolean announced;
//
// private boolean registered;
//
// public ServiceRegistration(IpAccess theIPAccess, SCSProperties
// serviceProperties,
// IpServiceInstanceLifecycleManager manager)
// throws TpCommonExceptions, P_ACCESS_DENIED, P_INVALID_ACCESS_TYPE,
// P_INVALID_INTERFACE_TYPE, P_INVALID_AUTH_TYPE, P_INVALID_DOMAIN_ID,
// P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY {
// registration = null;
// announced = false;
// registered = false;
// this.manager = manager;
// ipAccess = theIPAccess;
// serviceDescriptionHelper = serviceProperties;
// }
//
// public String registerService() throws TpCommonExceptions, P_ACCESS_DENIED,
// P_INVALID_INTERFACE_NAME, P_ILLEGAL_SERVICE_ID,
// P_UNKNOWN_SERVICE_ID, P_PROPERTY_TYPE_MISMATCH,
// P_DUPLICATE_PROPERTY_NAME, P_ILLEGAL_SERVICE_TYPE,
// P_UNKNOWN_SERVICE_TYPE, P_MISSING_MANDATORY_PROPERTY,
// P_SERVICE_TYPE_UNAVAILABLE {
// if (!registered) {
// org.csapi.IpInterface theInterface = ipAccess
// .obtainInterface("P_REGISTRATION");
// registration = IpFwServiceRegistrationHelper.narrow(theInterface);
// serviceID = registration.registerService(
// serviceDescriptionHelper.getServiceName(),
// serviceDescriptionHelper.getServicePropertyList());
// registered = true;
// }
// return serviceID;
// }
//
// public void unregisterService() throws TpCommonExceptions,
// P_ILLEGAL_SERVICE_ID, P_UNKNOWN_SERVICE_ID {
// if (registered) {
// registration.unregisterService(serviceID);
// registered = false;
// }
// }
//
// public void announceServiceAvailability() throws TpCommonExceptions,
// P_ILLEGAL_SERVICE_ID, P_UNKNOWN_SERVICE_ID,
// P_INVALID_INTERFACE_TYPE {
// if (!announced) {
// registration.announceServiceAvailability(serviceID, manager);
// announced = true;
// }
// }
//
// public void unannounceService() throws TpCommonExceptions,
// P_ILLEGAL_SERVICE_ID, P_UNKNOWN_SERVICE_ID {
// if (announced) {
// registration.unannounceService(serviceID);
// announced = false;
// }
// }
//
// public TpServiceDescription describeService() throws TpCommonExceptions,
// P_ILLEGAL_SERVICE_ID, P_UNKNOWN_SERVICE_ID {
// return registration.describeService(serviceID);
// }
//
// public TpServiceDescription describeService(String aServiceID)
// throws TpCommonExceptions, P_ILLEGAL_SERVICE_ID,
// P_UNKNOWN_SERVICE_ID {
// return registration.describeService(aServiceID);
// }
//
// public boolean isRegistered() {
// return registered;
// }
//
// public boolean isAnnounced() {
// return announced;
// }
// }
