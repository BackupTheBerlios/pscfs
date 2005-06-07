//Source File Name:   ServiceRegistration.java
package group5.server;

/**
 * 
 * @author Nguyen Huu Hoa
 *
 */

import org.csapi.*;
import org.csapi.fw.*;
import org.csapi.fw.fw_access.trust_and_security.IpAccess;
import org.csapi.fw.fw_service.service_lifecycle.IpServiceInstanceLifecycleManager;
import org.csapi.fw.fw_service.service_registration.IpFwServiceRegistration;
import org.csapi.fw.fw_service.service_registration.IpFwServiceRegistrationHelper;

public class ServiceRegistration {

	private IpAccess ipAccess;

	private IpFwServiceRegistration registration;

	private ServiceDescriptionHelper serviceDescriptionHelper;

	private String serviceID;

	private IpServiceInstanceLifecycleManager manager;

	private boolean announced;

	private boolean registered;

	public ServiceRegistration(FwSession theSession, Configuration config,
			IpServiceInstanceLifecycleManager manager)
			throws TpCommonExceptions, P_ACCESS_DENIED, P_INVALID_ACCESS_TYPE,
			P_INVALID_INTERFACE_TYPE, P_INVALID_AUTH_TYPE, P_INVALID_DOMAIN_ID,
			P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY, FwSessionException {
		registration = null;
		announced = false;
		registered = false;
		this.manager = manager;
		ipAccess = theSession.getAccess().getIpAccess();
		serviceDescriptionHelper = new ServiceDescriptionHelper(config);
	}

	public String registerService() throws TpCommonExceptions, P_ACCESS_DENIED,
			P_INVALID_INTERFACE_NAME, P_ILLEGAL_SERVICE_ID,
			P_UNKNOWN_SERVICE_ID, P_PROPERTY_TYPE_MISMATCH,
			P_DUPLICATE_PROPERTY_NAME, P_ILLEGAL_SERVICE_TYPE,
			P_UNKNOWN_SERVICE_TYPE, P_MISSING_MANDATORY_PROPERTY,
			P_SERVICE_TYPE_UNAVAILABLE, ConfigurationException {
		if (!registered) {
			org.csapi.IpInterface theInterface = ipAccess
					.obtainInterface("P_REGISTRATION");
			registration = IpFwServiceRegistrationHelper.narrow(theInterface);
			serviceID = registration.registerService(serviceDescriptionHelper
					.getServiceTypeName(), serviceDescriptionHelper
					.getServicePropertyList());
			registered = true;
		}
		return serviceID;
	}

	public void unregisterService() throws TpCommonExceptions,
			P_ILLEGAL_SERVICE_ID, P_UNKNOWN_SERVICE_ID {
		if (registered) {
			registration.unregisterService(serviceID);
			registered = false;
		}
	}

	public void announceServiceAvailability() throws TpCommonExceptions,
			P_ILLEGAL_SERVICE_ID, P_UNKNOWN_SERVICE_ID,
			P_INVALID_INTERFACE_TYPE {
		if (!announced) {
			registration.announceServiceAvailability(serviceID, manager);
			announced = true;
		}
	}

	public void unannounceService() throws TpCommonExceptions,
			P_ILLEGAL_SERVICE_ID, P_UNKNOWN_SERVICE_ID {
		if (announced) {
			registration.unannounceService(serviceID);
			announced = false;
		}
	}

	public TpServiceDescription describeService() throws TpCommonExceptions,
			P_ILLEGAL_SERVICE_ID, P_UNKNOWN_SERVICE_ID {
		return registration.describeService(serviceID);
	}

	public TpServiceDescription describeService(String aServiceID)
			throws TpCommonExceptions, P_ILLEGAL_SERVICE_ID,
			P_UNKNOWN_SERVICE_ID {
		return registration.describeService(aServiceID);
	}

	public boolean isRegistered() {
		return registered;
	}

	public boolean isAnnounced() {
		return announced;
	}
}