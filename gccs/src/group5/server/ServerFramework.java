/**
 * 
 */
package group5.server;

import group5.P_INVALID_NAME_SERVICE;
import group5.utils.CommonFuntions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.csapi.IpInterface;
import org.csapi.IpService;
import org.csapi.P_INVALID_INTERFACE_NAME;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.TpCommonExceptions;
import org.csapi.fw.P_ACCESS_DENIED;
import org.csapi.fw.P_DUPLICATE_PROPERTY_NAME;
import org.csapi.fw.P_ILLEGAL_SERVICE_ID;
import org.csapi.fw.P_ILLEGAL_SERVICE_TYPE;
import org.csapi.fw.P_INVALID_ACCESS_TYPE;
import org.csapi.fw.P_INVALID_AGREEMENT_TEXT;
import org.csapi.fw.P_INVALID_AUTH_TYPE;
import org.csapi.fw.P_INVALID_DOMAIN_ID;
import org.csapi.fw.P_INVALID_PROPERTY;
import org.csapi.fw.P_INVALID_SERVICE_ID;
import org.csapi.fw.P_INVALID_SERVICE_TOKEN;
import org.csapi.fw.P_INVALID_SIGNATURE;
import org.csapi.fw.P_INVALID_SIGNING_ALGORITHM;
import org.csapi.fw.P_MISSING_MANDATORY_PROPERTY;
import org.csapi.fw.P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY;
import org.csapi.fw.P_PROPERTY_TYPE_MISMATCH;
import org.csapi.fw.P_SERVICE_ACCESS_DENIED;
import org.csapi.fw.P_SERVICE_TYPE_UNAVAILABLE;
import org.csapi.fw.P_UNKNOWN_SERVICE_ID;
import org.csapi.fw.P_UNKNOWN_SERVICE_TYPE;
import org.csapi.fw.TpAuthDomain;
import org.csapi.fw.TpDomainID;
import org.csapi.fw.TpProperty;
import org.csapi.fw.TpService;
import org.csapi.fw.TpServiceProperty;
import org.csapi.fw.TpSignatureAndServiceMgr;
import org.csapi.fw.fw_access.trust_and_security.IpAPILevelAuthentication;
import org.csapi.fw.fw_access.trust_and_security.IpAPILevelAuthenticationHelper;
import org.csapi.fw.fw_access.trust_and_security.IpAccess;
import org.csapi.fw.fw_access.trust_and_security.IpAccessHelper;
import org.csapi.fw.fw_access.trust_and_security.IpClientAPILevelAuthentication;
import org.csapi.fw.fw_access.trust_and_security.IpClientAPILevelAuthenticationPOA;
import org.csapi.fw.fw_access.trust_and_security.IpClientAccess;
import org.csapi.fw.fw_access.trust_and_security.IpClientAccessPOA;
import org.csapi.fw.fw_access.trust_and_security.IpInitial;
import org.csapi.fw.fw_access.trust_and_security.IpInitialHelper;
import org.csapi.fw.fw_application.discovery.IpServiceDiscovery;
import org.csapi.fw.fw_application.service_agreement.IpAppServiceAgreementManagement;
import org.csapi.fw.fw_application.service_agreement.IpAppServiceAgreementManagementPOA;
import org.csapi.fw.fw_application.service_agreement.IpServiceAgreementManagement;
import org.csapi.fw.fw_application.service_agreement.IpServiceAgreementManagementHelper;
import org.csapi.fw.fw_service.service_lifecycle.IpServiceInstanceLifecycleManagerPOA;
import org.csapi.fw.fw_service.service_registration.IpFwServiceRegistration;
import org.csapi.fw.fw_service.service_registration.IpFwServiceRegistrationHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

/**
 * The framework for server service
 * 
 * @author Nguyen Huu Hoa
 */
public class ServerFramework {

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(ServerFramework.class);
	}

	/** The instance of the CORBA Object Request Broker */
	private static ORB orb;

	public static ORB getORB() {
		return orb;
	}

	/**
	 * for synchronization
	 */
	boolean authenticated = false;

	boolean agreementSigned = false;

	boolean statusReported = false;

	private IpInitial m_ipInitial;

	/**
	 * reference to IpAccess, obtained from OSA framework
	 */
	private IpAccess m_ipAccess;

	/**
	 * reference to IpServiceDiscovery, obtained from OSA framework
	 */
	private IpServiceDiscovery m_ipSvcDisc;

	/**
	 * reference to service agreement interface
	 */
	private IpServiceAgreementManagement m_ipSvcAgmt;

	IpFwServiceRegistration m_ipFwSrvReg;

	IpAPILevelAuthentication m_ipAPILevelAuthentication;

	// for application authentication
	String m_applicationID;

	String m_password;

	/**
	 * Default constructor, creates and starts the CORBA ORB and POA.
	 */
	protected ServerFramework(String appId, String password)
			throws org.omg.CORBA.UserException {
		m_applicationID = appId;
		m_password = password;
		// Create an instance of the ORB.
		// Make sure the correct ORB is specified on the commandline, i.e.
		// -Dorg.omg.CORBA.ORBClass=<your ORB class>
		// -Dorg.omg.CORBA.ORBSingletonClass=<your ORB singleton class>
		System.setProperty("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
		System.setProperty("org.omg.CORBA.ORBSingletonClass",
				"org.jacorb.orb.ORBSingleton");
		java.util.Properties props = System.getProperties();
		orb = ORB.init(new String[0], props);

		// Start the ORB, so that it can handle the callback CORBA requests
		// invoked by the OSA API. The ORB is started in a separate thread
		// because orb.run() blocks until the ORB is shut down.
		Thread tCallback = new Thread() {
			public void run() {
				ServerFramework.orb.run();
			}
		};
		tCallback.setDaemon(true); // make sure the ORB quits when we're ready
		tCallback.start();

		// Get a reference to the ORB's Portable Object Adapter.
		org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
		POA poa = POAHelper.narrow(obj);

		// Activate the Root POA Manager.
		poa.the_POAManager().activate();
	}

	/**
	 * Initializes the CORBA Object Request Broker and Naming Service, and
	 * resolves the initial reference to the OSA API. The initial reference is
	 * resolved using the Naming Service, with the indicated name.
	 * 
	 * @param namingSvcIor
	 *            IOR of the naming service
	 * @param initial
	 *            name of the OSA initial reference.
	 * @return reference to IpInitial
	 * @exception org.omg.CORBA.UserException
	 *                Thrown if CORBA initialization fails, or the name cannot
	 *                be found.
	 */
	IpInitial getIpInitial() throws org.omg.CORBA.UserException,
			P_INVALID_NAME_SERVICE {
		m_logger.debug("Enterring getIpInital");
		String CORBA_NameService = System.getProperty("ORB.NameService");
		if (CORBA_NameService == null)
			throw new P_INVALID_NAME_SERVICE(
					"can not get value of property: ORB.NameService",
					"set value for that property before calling this function");

		// Find the naming service
		org.omg.CORBA.Object obj = orb.string_to_object(CORBA_NameService);
		NamingContextExt namingContext = NamingContextExtHelper.narrow(obj);

		// Lookup the initial OSA reference using the naming service
		obj = namingContext.resolve_str("IpInitial");

		m_ipInitial = IpInitialHelper.narrow(obj);
		m_logger.debug("<----getting out of getIpInitial()");
		return m_ipInitial;
	}

	protected IpAPILevelAuthentication getIpAuthentication()
			throws TpCommonExceptions {
		m_logger.debug("ServiceRegistration::getIpAuthentication");
		if (m_ipAPILevelAuthentication == null) {
			try {
				m_ipAPILevelAuthentication = initializeAuthentication(
						getIpInitial(), m_applicationID, m_password);
			} catch (P_INVALID_INTERFACE_TYPE ex) {
				m_logger.fatal("Invalid interface type: " + ex.getMessage());
			} catch (P_INVALID_DOMAIN_ID ex) {
				m_logger.fatal("Invalid domain id: " + ex.getMessage());
			} catch (P_INVALID_AUTH_TYPE ex) {
				m_logger.fatal("Invalid authentication type: "
						+ ex.getMessage());
			} catch (UserException ex) {
				m_logger.fatal("User exception with information: "
						+ ex.getMessage());
			}
		}
		return m_ipAPILevelAuthentication;
	}

	/**
	 * Prepares the authentication phase.
	 * 
	 * @param initialIf
	 *            reference to the OSA IpInitial interface
	 * @param appId
	 *            the application ID
	 * @param password
	 *            the password for this application
	 * @return reference to the OSA IpAuthentication interface
	 */
	IpAPILevelAuthentication initializeAuthentication(IpInitial initialIf,
			String appId, String password) throws TpCommonExceptions,
			P_INVALID_INTERFACE_TYPE, P_INVALID_DOMAIN_ID, P_INVALID_AUTH_TYPE {

		// Create the authentication callback object
		AuthenticationCallback cb = new AuthenticationCallback(appId, password);

		// Register the callback object with the ORB
		IpClientAPILevelAuthentication callback = cb._this(orb);

		// Create an OSA domain ID containing the application ID and a reference
		// to the callback object
		TpDomainID domainID = new TpDomainID();
		domainID.ClientAppID(appId);
		TpAuthDomain authDomain = new TpAuthDomain(domainID, callback);

		/***********************************************************************
		 * Do the OSA invocation: initiateAuthentication()
		 **********************************************************************/
		TpAuthDomain fwDomain = initialIf.initiateAuthentication(authDomain,
				"P_OSA_AUTHENTICATION");

		// Get the IpAPILevelAuthentication reference from the returned result.
		return IpAPILevelAuthenticationHelper.narrow(fwDomain.AuthInterface);
	}

	/**
	 * Starts the OSA authentication procedure.
	 * 
	 * @param authIf
	 *            reference to the OSA authentication interface
	 */
	void authenticate(IpAPILevelAuthentication authIf)
			throws TpCommonExceptions, P_ACCESS_DENIED,
			P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY {

		/***********************************************************************
		 * Do the OSA invocation: selectEncryptionMethod();
		 * 
		 * The value of the argument can be "" (no encryption), "P_DES_56", and
		 * "P_DES_128".
		 **********************************************************************/
		authIf.selectEncryptionMethod("");
		// authIf.selectEncryptionMethod(prescribedMethod);

		// Now we have to wait until the OSA API has called our callback
		// object to authenticate us.
		synchronized (this) {
			while (!authenticated) {
				try {
					wait();
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}

	/**
	 * Requests an access session.
	 * 
	 * @param authIf
	 *            reference to the OSA authentication interface
	 * @return reference to the OSA IpAccess interface
	 */
	IpAccess requestAccess(IpAPILevelAuthentication authIf)
			throws TpCommonExceptions, P_INVALID_ACCESS_TYPE,
			P_INVALID_INTERFACE_TYPE, P_ACCESS_DENIED {

		// Create the access callback object
		AccessCallback cb = new AccessCallback();

		// Register the callback object with the ORB
		IpClientAccess callback = cb._this(orb);

		/***********************************************************************
		 * Do the OSA invocation: requestAccess();
		 **********************************************************************/
		IpInterface itf = authIf.requestAccess("P_OSA_ACCESS", callback);

		m_ipAccess = IpAccessHelper.narrow(itf);
		return m_ipAccess;
	}

	/**
	 * Terminates an access session.
	 * 
	 * @param accessIf
	 *            reference to the OSA IpAccess interface
	 */
	void endAccessSession(IpAccess accessIf) throws TpCommonExceptions,
			P_ACCESS_DENIED, P_INVALID_PROPERTY {

		/***********************************************************************
		 * Do the OSA invocation: requestAccess();
		 **********************************************************************/
		accessIf.endAccess(new TpProperty[0]);
	}

	protected IpFwServiceRegistration obtainIpFwServiceRegistration()
			throws TpCommonExceptions {
		if (m_ipFwSrvReg == null) {
			String regService = "P_REGISTRATION";
			try {
				org.csapi.IpInterface ipinterface = requestAccess(
						getIpAuthentication()).obtainInterface(regService);
				m_ipFwSrvReg = IpFwServiceRegistrationHelper
						.narrow(ipinterface);
			} catch (P_INVALID_INTERFACE_NAME p_invalid_interface_name) {
				m_logger.fatal("Invalid interface name: " + regService);
				throw new TpCommonExceptions(14,
						p_invalid_interface_name.ExtraInformation);
			} catch (P_INVALID_INTERFACE_TYPE p_invalid_interface_name) {
				m_logger.fatal("Invalid interface type: " + regService);
				throw new TpCommonExceptions(14,
						p_invalid_interface_name.ExtraInformation);
			} catch (P_ACCESS_DENIED p_access_denied) {
				m_logger.fatal("access denied obtaining interface "
						+ regService);
				throw new TpCommonExceptions(14,
						p_access_denied.ExtraInformation);
			} catch (P_INVALID_ACCESS_TYPE ex) {
				m_logger.fatal("access denied obtaining interface "
						+ regService);
				throw new TpCommonExceptions(14, ex.ExtraInformation);
			}
		}
		return m_ipFwSrvReg;
	}

	public synchronized void unannounceService(String scsId) {
		if (scsId == null) {
			m_logger.error("scsId=null");
			return;
		}
		try {
			IpFwServiceRegistration ipFSR = obtainIpFwServiceRegistration();
			if (ipFSR != null && !ipFSR._non_existent()) {
				ipFSR.unannounceService(scsId);
				ipFSR.unregisterService(scsId);
			}
		} catch (TpCommonExceptions tpcommonexceptions) {
			m_logger.error("Invalid service ID:" + scsId);
		} catch (P_UNKNOWN_SERVICE_ID p_unknown_service_id) {
			m_logger.error("Unknown service ID:" + scsId);
		} catch (P_ILLEGAL_SERVICE_ID p_illegal_service_id) {
			m_logger.error("Illegal service ID:" + scsId);
		} catch (Throwable throwable) {
			m_logger.error("Unknown exception in unannounceService: "
					+ throwable.getClass());
		}
	}

	/**
	 * Register new service with OSA framework
	 * @param serviceTypeName "P_GENERIC_CALL_CONTROL" in case of generic call control
	 * @param scsproperties List of service capability properties
	 * @param class1 Class of the interface life cycle manager
	 * @return ServiceID
	 * @throws TpCommonExceptions
	 */
	public String registerService(String serviceTypeName,
			SCSProperties scsproperties, Class class1)
			throws TpCommonExceptions {
		return registerService(
				serviceTypeName,
				scsproperties,
				((IpServiceInstanceLifecycleManagerPOA) (new OSAServiceFactoryImpl(
						class1))));
	}

	private synchronized String registerService(
			String serviceTypeName,
			SCSProperties scsproperties,
			IpServiceInstanceLifecycleManagerPOA ipserviceinstancelifecyclemanagerpoa)
			throws TpCommonExceptions {
		String serviceID = registerService(serviceTypeName, scsproperties);
		String s2 = scsproperties.getServiceName();
		try {
			if (m_logger.isDebugEnabled())
				m_logger.debug("ServiceRegistrationUtil::scsId=" + serviceID);
			if (serviceID != null) {
				IpFwServiceRegistration ipfwserviceregistration = obtainIpFwServiceRegistration();
				ipfwserviceregistration.announceServiceAvailability(serviceID,
						ipserviceinstancelifecyclemanagerpoa._this(orb));
			} else {
				throw new TpCommonExceptions("Error discovering service name="
						+ s2, 15, s2);
			}
		} catch (P_INVALID_INTERFACE_TYPE p_invalid_interface_type) {
			throw new TpCommonExceptions("Invalid interface type", 14,
					serviceID);
		} catch (P_ILLEGAL_SERVICE_ID p_illegal_service_id) {
			throw new TpCommonExceptions("Illegal service ID", 14, serviceID);
		} catch (P_UNKNOWN_SERVICE_ID p_unknown_service_id) {
			throw new TpCommonExceptions("Unknown service ID", 14, serviceID);
		}
		return serviceID;
	}

	protected String registerService(String serviceTypeName,
			SCSProperties scsproperties) throws TpCommonExceptions {
		m_logger.info("Registering service type=" + serviceTypeName
				+ " version=" + scsproperties.getServiceVersion());
		try {
			return obtainIpFwServiceRegistration().registerService(
					serviceTypeName, scsproperties.getServicePropertyList());
		} catch (P_MISSING_MANDATORY_PROPERTY p_missing_mandatory_property) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ p_missing_mandatory_property.ExtraInformation);
			throw new TpCommonExceptions("Missing mandatory property for "
					+ serviceTypeName, 14,
					p_missing_mandatory_property.ExtraInformation);
		} catch (P_ILLEGAL_SERVICE_TYPE p_illegal_service_type) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ p_illegal_service_type.ExtraInformation);
			throw new TpCommonExceptions("Illegal service type for "
					+ serviceTypeName, 14,
					p_illegal_service_type.ExtraInformation);
		} catch (P_UNKNOWN_SERVICE_TYPE p_unknown_service_type) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ p_unknown_service_type.ExtraInformation);
			throw new TpCommonExceptions("Unknown service type for "
					+ serviceTypeName, 14,
					p_unknown_service_type.ExtraInformation);
		} catch (P_PROPERTY_TYPE_MISMATCH p_property_type_mismatch) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ p_property_type_mismatch.ExtraInformation);
			throw new TpCommonExceptions("Property type mismatch for "
					+ serviceTypeName, 14,
					p_property_type_mismatch.ExtraInformation);
		} catch (P_DUPLICATE_PROPERTY_NAME p_duplicate_property_name) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ p_duplicate_property_name.ExtraInformation);
			throw new TpCommonExceptions("Duplicate property name for "
					+ serviceTypeName, 14,
					p_duplicate_property_name.ExtraInformation);
		} catch (P_SERVICE_TYPE_UNAVAILABLE p_service_type_unavailable) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ p_service_type_unavailable.ExtraInformation);
			throw new TpCommonExceptions("Service type unavailable for "
					+ serviceTypeName, 14,
					p_service_type_unavailable.ExtraInformation);
		} catch (TpCommonExceptions tpcommonexceptions) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ tpcommonexceptions.ExtraInformation);
			throw new TpCommonExceptions("Service registration failed", 14,
					tpcommonexceptions.ExtraInformation);
		} catch (Throwable throwable) {
			m_logger.error("Unknown error in registerService of "
					+ serviceTypeName, throwable);
			throw new TpCommonExceptions("Error in registerService:"
					+ throwable.getClass(), 14, throwable.getMessage());
		}
	}

	/**
	 * Discovers a service using the OSA IpServiceDiscovery interface.
	 * 
	 * @param discoveryIf
	 *            reference to the OSA IpServiceDisovery interface
	 * @param serviceName
	 *            the name of the service
	 * @return service ID for the service
	 */
	String discoverService(IpServiceDiscovery discoveryIf, String serviceName)
			throws TpCommonExceptions, P_UNKNOWN_SERVICE_TYPE,
			P_ILLEGAL_SERVICE_TYPE, P_INVALID_PROPERTY, P_ACCESS_DENIED {

		// Create a list of service properties that indicates what kind of
		// service we are looking for. In our case the service is identified
		// only by its name.
		TpServiceProperty prop = new TpServiceProperty("Service Name",
				new String[] { serviceName });
		TpServiceProperty[] props = new TpServiceProperty[0];

		// Obtain a service ID of the service

		/***********************************************************************
		 * Do the OSA invocation: discoverService()
		 **********************************************************************/
		TpService[] svcList = discoveryIf
				.discoverService(serviceName, props, 1);

		// Return the service ID of the service
		return svcList[0].ServiceID;
	}

	/**
	 * Selects the indicated service.
	 * 
	 * @param agmtIf
	 *            reference to the OSA service agreement management interface
	 * @param serviceId
	 *            ID for the service
	 * @return service token for the selected service
	 */
	String selectService(IpServiceAgreementManagement agmtIf, String serviceId)
			throws TpCommonExceptions, P_INVALID_SERVICE_ID,
			P_INVALID_SERVICE_TOKEN, P_SERVICE_ACCESS_DENIED, P_ACCESS_DENIED,
			P_INVALID_SIGNATURE, IOException {

		String serviceToken = null;
		try {
			/*******************************************************************
			 * Do the OSA invocation: selectService()
			 ******************************************************************/
			serviceToken = agmtIf.selectService(serviceId);

		} catch (P_SERVICE_ACCESS_DENIED e) {
			// Exception is thrown if a signed service agreement already exists
			// for this service.
			m_logger.error("Service access denied for " + serviceId + ": "
					+ e.ExtraInformation);

			try {
				// Retrieve the service token for the service from a file and
				// terminate the agreement
				serviceToken = fetchToken("servicetoken");
				if (m_logger.isInfoEnabled())
					m_logger.info("Fetched stored token: " + serviceToken
							+ ", terminating agreement");
				terminateServiceAgreement(agmtIf, serviceToken);
			} catch (IOException ioe) {
				// File does not seem to exist; throw original exception
				throw e;
			}

			serviceToken = agmtIf.selectService(serviceId); // try again
		}

		// Store the token in a file
		storeToken("servicetoken", serviceToken);

		return serviceToken;
	}

	/**
	 * Performs mutually signing of a service agreement for the service.
	 * 
	 * @param agmtIf
	 *            reference to the OSA service agreement management interface
	 * @param svcToken
	 *            token identifying service instance for this application
	 * @return reference to the service manager interface of the service
	 */
	IpService signServiceAgreement(IpServiceAgreementManagement agmtIf,
			String svcToken) throws TpCommonExceptions,
			P_INVALID_SERVICE_TOKEN, P_INVALID_SIGNING_ALGORITHM,
			P_INVALID_AGREEMENT_TEXT, P_SERVICE_ACCESS_DENIED, P_ACCESS_DENIED,
			P_INVALID_SIGNATURE, IOException {
		/***********************************************************************
		 * Do the OSA invocation: initiateSignServiceAgreement()
		 **********************************************************************/
		agmtIf.initiateSignServiceAgreement(svcToken);

		// Now we have to wait until the OSA API has called our AccessCallback
		// object to sign a service agreement.
		synchronized (this) {
			while (!agreementSigned) {
				try {
					wait();
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}

		TpCommonExceptions ex = null;

		// Signature verifier=null;
		String agreementText = "This is a dummy text";

		for (int retries = 0; retries < 5; retries++) {
			// To obtain a reference to the service manager, we need to let
			// the OSA framework sign our service agreement
			try {
				/***************************************************************
				 * Do the OSA invocation: signServiceAgreement()
				 **************************************************************/
				if (m_logger.isInfoEnabled())
					m_logger
							.info("The client invokes signServiceAgreement() of framework with singing method of P_MD5_RSA_512 ...");
				TpSignatureAndServiceMgr signatureAndServiceMgr = agmtIf
						.signServiceAgreement(svcToken, agreementText,
								"P_MD5_RSA_512");

				byte[] fwSignature = signatureAndServiceMgr.DigitalSignature;
				if (m_logger.isInfoEnabled())
					m_logger.info("Digital Signature of the framework is: "
							+ CommonFuntions.hexBytesToString(fwSignature));

				if (m_logger.isInfoEnabled())
					m_logger.info("Verifying the framework signature...");

				// verify the signature
				// boolean v=verifier.verify(fwSignature);

				// System.out.println("Framework's signature verified?: " + v);

				// succeeded, exit retry loop and return the reference
				return signatureAndServiceMgr.ServiceMgrInterface;

			} catch (TpCommonExceptions e) {
				// Framework not ready yet; sleep for a second
				ex = e;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
				}
			}
			// catch(SignatureException se){
			// se.printStackTrace();
			// }
		}

		// Retries exceeded; throw original exception
		throw ex;
	}

	/**
	 * Stores the service token in a file.
	 * 
	 * @param filename
	 *            the file where the token needs to be stored
	 * @param token
	 *            the service token
	 */
	private void storeToken(String filename, String serviceToken)
			throws IOException {

		Properties props = new Properties();
		props.setProperty("token", serviceToken);
		FileOutputStream file = new FileOutputStream(filename);
		props.store(file, null);
		file.close();
	}

	/**
	 * Retrieved the service token from a file.
	 * 
	 * @param filename
	 *            the file in which the token is stored
	 * @return the stored service token
	 */
	private String fetchToken(String filename) throws IOException {

		Properties props = new Properties();
		FileInputStream file = new FileInputStream(filename);
		props.load(file);
		file.close();
		return props.getProperty("token");
	}

	/**
	 * Terminates the service agreement.
	 * 
	 * @param svcAgmtIf
	 *            reference to the service agreement management interface
	 * @param svcToken
	 *            the service token for which to terminate the agreement
	 */
	void terminateServiceAgreement(IpServiceAgreementManagement svcAgmtIf,
			String svcToken) throws TpCommonExceptions,
			P_INVALID_SERVICE_TOKEN, P_INVALID_SIGNATURE, P_ACCESS_DENIED {

		if (m_logger.isInfoEnabled())
			m_logger.info("Terminating service agreement by the client....");

		// svcAgmtIf.terminateServiceAgreement(svcToken, agreementText,
		// fwSignature);
	}

	/**
	 * Obtains the OSA service agreement management interface.
	 */
	IpServiceAgreementManagement obtainServiceAgreementInterface(
			IpAccess accessIf) throws TpCommonExceptions,
			P_INVALID_INTERFACE_NAME, P_INVALID_INTERFACE_TYPE, P_ACCESS_DENIED {

		// Create service agreement management callback object
		ServiceAgreementCallback cb = new ServiceAgreementCallback();

		// Register the callback object with the ORB
		IpAppServiceAgreementManagement callback = cb._this(orb);

		/***********************************************************************
		 * Do the OSA invocation: obtainInterfaceWithCallback();
		 **********************************************************************/
		IpInterface itf = accessIf.obtainInterfaceWithCallback(
				"P_SERVICE_AGREEMENT_MANAGEMENT", callback);

		return IpServiceAgreementManagementHelper.narrow(itf);
	}

	/**
	 * Initialize the application, connect to framework and obtain IpAccess
	 * service. Application can use the framework as follows: <code>
	 * // Be sure to set ORB.NameService to Naming Service of actual ORB
	 * e.g	System.setProperty("ORB.NameService", "corbaloc::localhost:2050/StandardNS/NameServer-POA/_root");
	 * ApplicationFramework appFramework = new ApplicationFramework();
	 * // Init application 
	 * appFramework.initApplication("<ApplicationID>", "<ApplicationKey>");
	 * // Get appropriated service
	 * IpService tempService = selectSCFs("P_USER_STATUS");
	 * IpUserStatus ipUS = IpUserStatusHelper.narrow(tempService);
	 * // consume service
	 * call to service interface API
	 * // End application
	 * appFramework.EndApplication();
	 * </code>
	 * TODO: Add more exception specification here
	 */
	// public void initApplication(String clAppID, String clAppSharedSecretKey)
	// throws P_INVALID_NAME_SERVICE, UserException {
	// // Step 1: get a reference to the OSA IpInitial interface
	// String CORBA_NameService = System.getProperty("ORB.NameService");
	// if (CORBA_NameService == null)
	// throw new P_INVALID_NAME_SERVICE(
	// "can not get value of property: ORB.NameService",
	// "set value for that property before calling this function");
	// try {
	// IpInitial ipInitial = initializeOSA(CORBA_NameService, "IpInitial");
	//
	// if (m_logger.isInfoEnabled()) {
	// m_logger.info("Obtained IpInitial, starting authentication");
	// }
	//
	// // Step 2: get a reference to the OSA IpAuthentication interface
	// //
	// // You could fill in your own application ID and secret to see if
	// // authentication and service selection succeeds for your app.
	// IpAPILevelAuthentication ipAuthentication = initializeAuthentication(
	// ipInitial, clientAppID, clientAppSharedSecret);
	//
	// if (m_logger.isInfoEnabled()) {
	// m_logger.info("Obtained IpAPILevelAuthentication");
	// }
	//
	// // Step 3: perform authentication
	// authenticate(ipAuthentication);
	//
	// if (m_logger.isInfoEnabled()) {
	// m_logger.info("Authenticated, requesting access");
	// }
	//
	// // Step 4: get a reference to the OSA IpAccess interface
	// m_ipAccess = requestAccess(ipAuthentication);
	//
	// if (m_logger.isInfoEnabled()) {
	// m_logger.info("Obtained IpAccess");
	// }
	//
	// // Step 5: get a refence to the IpServiceDiscovery interface
	// m_ipSvcDisc = obtainDiscoveryInterface(m_ipAccess);
	//
	// if (m_logger.isInfoEnabled()) {
	// m_logger.info("Obtained IpServiceDiscovery");
	// }
	// // step 6: in selectSCFs
	//
	// // Step 7: get a refence to the IpServiceAgreementManagement
	// // interface
	// m_ipSvcAgmt = obtainServiceAgreementInterface(m_ipAccess);
	//
	// if (m_logger.isInfoEnabled()) {
	// m_logger.info("Obtained IpServiceAgreementManagement");
	// }
	// } catch (P_ACCESS_DENIED ex) {
	// m_logger
	// .error("Access denied while requesting access to interface IpAccess. More
	// information: "
	// + ex.getMessage());
	// } catch (P_INVALID_INTERFACE_TYPE ex) {
	// m_logger.error("Invalid interface type. More information: "
	// + ex.getMessage());
	// } catch (P_INVALID_INTERFACE_NAME ex) {
	// m_logger.error("Invalid interface name. More information: "
	// + ex.getMessage());
	// } catch (P_INVALID_ACCESS_TYPE ex) {
	// m_logger.error("Invalid access type. More information: "
	// + ex.getMessage());
	// } catch (P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY ex) {
	// m_logger
	// .error("No acceptable encryption capability. More information: "
	// + ex.getMessage());
	// } catch (P_INVALID_AUTH_TYPE ex) {
	// m_logger.error("Invalid authentication type. More information: "
	// + ex.getMessage());
	// } catch (P_INVALID_DOMAIN_ID ex) {
	// m_logger.error("Invalid domain id. More information: "
	// + ex.getMessage());
	// }
	// }
	/**
	 * Use this function to get the reference to service interface.
	 * 
	 * @param serviceName
	 *            Name of the service, according to Parlay spec. (e.g if
	 *            serviceName = P_USER_STATUS, the return interface will be
	 *            IpUserStatus
	 * @return reference to service, with type IpService. In order to get the
	 *         appropriate service type, one should call:
	 * 
	 * <pre>
	 * IpService tempService = selectSCFs(&quot;P_USER_STATUS&quot;);IpUserStatus ipUS = IpUserStatusHelper.narrow(tempService);
	 *     
	 *    
	 *   
	 *  
	 * </pre>
	 * 
	 * Here is the list of services that can be query from OSA Framework
	 * <ul>
	 * <li><b>NULL</b> An empty (NULL) string indicates no SCF name.</li>
	 * <li><b>P_GENERIC_CALL_CONTROL</b> The name of the Generic Call Control
	 * SCF.</li>
	 * <li><b>P_MULTI_PARTY_CALL_CONTROL</b> The name of the MultiParty Call
	 * Control SCF.</li>
	 * <li><b>P_MULTI_MEDIA_CALL_CONTROL</b> The name of the MultiMedia Call
	 * Control SCF.</li>
	 * <li><b>P_CONFERENCE_CALL_CONTROL</b> The name of the Conference Call
	 * Control SCF.</li>
	 * <li><b>P_USER_INTERACTION</b> The name of the User Interaction SCFs.</li>
	 * <li><b>P_USER_INTERACTION_ADMIN</b> The name of the User Interaction
	 * Administration SCF.</li>
	 * <li><b>P_TERMINAL_CAPABILITIES</b> The name of the Terminal
	 * Capabilities SCF.</li>
	 * <li><b>P_USER_BINDING</b> The name of the User Binding SCF.</li>
	 * <li><b>P_USER_LOCATION</b> The name of the User Location SCF.</li>
	 * <li><b>P_USER_LOCATION_CAMEL</b> The name of the Network User Location
	 * SCF.</li>
	 * <li><b>P_USER_LOCATION_EMERGENCY</b> The name of the User Location
	 * Emergency SCF.</li>
	 * <li><b>P_USER_STATUS</b> The name of the User Status SCF.</li>
	 * <li><b>P_EXTENDED_USER_STATUS</b> The name of Extended User Status SCF.</li>
	 * <li><b>P_DATA_SESSION_CONTROL</b> The name of the Data Session Control
	 * SCF.</li>
	 * <li><b>P_GENERIC_MESSAGING</b> The name of the Generic Messaging SCF.</li>
	 * <li><b>P_CONNECTIVITY_MANAGER</b> The name of the Connectivity Manager
	 * SCF.</li>
	 * <li><b>P_CHARGING</b> The name of the Charging SCF.</li>
	 * <li><b>P_ACCOUNT_MANAGEMENT</b> The name of the Account Management SCF.</li>
	 * <li><b>P_POLICY_PROVISIONING</b> The name of the Policy Management
	 * provisioning SCF.</li>
	 * <li><b>P_POLICY_EVALUATION</b> The name of the Policy Management policy
	 * evaluation SCF.</li>
	 * <li><b>P_PAM_ACCESS</b> The name of PAM presentity SCF.</li>
	 * <li><b>P_PAM_EVENT_MANAGEMENT</b> The name of PAM watcher SCF.</li>
	 * <li><b>P_PAM_PROVISIONING</b> The name of PAM provisioning SCF.</li>
	 * </ul>
	 */
	// public IpService selectSCFs(String serviceName) {
	// try {
	// // Step 6: obtain a service ID for the desired service
	// // String svcId = discoverService(m_ipSvcDisc, "P_USER_STATUS");
	// String svcId = discoverService(m_ipSvcDisc, serviceName);
	//
	// if (m_logger.isInfoEnabled()) {
	// m_logger.info("Obtained service ID: " + svcId);
	// }
	//
	// // Step 8: obtain a service token for the session
	// String svcToken = selectService(m_ipSvcAgmt, svcId);
	//
	// if (m_logger.isInfoEnabled()) {
	// m_logger.info("Obtained service token: " + svcToken);
	// }
	//
	// // Step 9: sign the service agreement
	// IpService svcMgr = signServiceAgreement(m_ipSvcAgmt, svcToken);
	// // IpUserStatus ipUS = IpUserStatusHelper.narrow(svcMgr);
	//
	// if (m_logger.isInfoEnabled()) {
	// m_logger.info("Signed service agreement");
	// }
	// return svcMgr;
	// }
	// catch (P_ILLEGAL_SERVICE_TYPE ex) {
	// m_logger.error("Illegal service type: " + ex.getMessage());
	// }
	// catch (P_UNKNOWN_SERVICE_TYPE ex) {
	// m_logger.error("Unknown service type: " + ex.getMessage());
	// }
	// catch (P_ACCESS_DENIED ex) {
	// m_logger.error("Access to service was denied. More information: " +
	// ex.getMessage());
	// }
	// catch(P_INVALID_PROPERTY ex) {
	// m_logger.error("Invalid property. More information: " + ex.getMessage());
	// }
	// // catch exceptions from selectService
	// catch(IOException ex) {
	// m_logger.error("File IO exception. More information: " +
	// ex.getMessage());
	// }
	// catch(P_INVALID_SERVICE_ID ex) {
	// m_logger.error("Invalid service ID. More information: " +
	// ex.getMessage());
	// }
	// // catch exceptions from signServiceAgreement
	// catch (P_SERVICE_ACCESS_DENIED ex) {
	// m_logger.error("Access to service was denied. More information: " +
	// ex.getMessage());
	// }
	// catch (P_INVALID_SIGNATURE ex) {
	// m_logger.error("The signature was invalid. More information: " +
	// ex.getMessage());
	// }
	// catch (P_INVALID_SERVICE_TOKEN ex) {
	// m_logger.error("Invalid service token. More information: " +
	// ex.getMessage());
	// }
	// catch (P_INVALID_SIGNING_ALGORITHM ex) {
	// m_logger.error("Invalid signing algorithm. More information: " +
	// ex.getMessage());
	// }
	// catch (P_INVALID_AGREEMENT_TEXT ex) {
	// m_logger.error("Invalid agreement text. More information: " +
	// ex.getMessage());
	// }
	// // common exceptions
	// catch (TpCommonExceptions ex) {
	// m_logger.error("Some errors occur: " + ex.getMessage());
	// ex.printStackTrace();
	// }
	// // m_logger.error("Some errors occur: " + e.getMessage());
	// // e.printStackTrace();
	// // }
	// return null;
	// }
	public void endApplication() {
		try {
			// Step 10: use the service
			// requestStatus(ipUS, user);

			// Step 11: clean up
			if (m_logger.isInfoEnabled()) {
				m_logger.info("Cleaning up");
			}

			// sample.terminateServiceAgreement(ipSvcAgmt, svcToken);
			endAccessSession(m_ipAccess);

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Done");
			}

		} catch (Exception e) {
			m_logger.error("Some errors occur: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * This inner class represents the callback object which is used during OSA
	 * authentication. The OSA API calls the authenticate() method of this
	 * class.
	 */
	class AuthenticationCallback extends IpClientAPILevelAuthenticationPOA {

		/** The ID of the application */
		String applicationID;

		/** The password of the application */
		byte[] password;

		/**
		 * Constructs the callback object for OSA authentication.
		 * 
		 * @param appId
		 *            The application ID
		 * @param password
		 *            The password used to perform authentication
		 */
		AuthenticationCallback(String appId, String password) {
			this.applicationID = appId;
			this.password = password.getBytes();
		}

		/**
		 * Performs authentication of the application on request of the OSA
		 * framework.
		 * 
		 * @param challenge
		 *            challenge string that is input for the CHAP authentication
		 *            protocol
		 * @return CHAP response
		 */
		public byte[] authenticate(byte[] challengeByte) {

			/*
			 * CHAP Challenge and Response packet format:
			 * =========================================
			 * 
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
			 * Code | Identifier | Length |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
			 * Value-Size | Value ...
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
			 * Name ... +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * 
			 */

			if (m_logger.isInfoEnabled())
				m_logger.info("Callback to authenticate");

			byte[] cipher = new byte[10];
			return cipher;
		}

		/**
		 * This method is invoked if the framework has validated and approved
		 * the client's authentication credentials.
		 */
		public void authenticationSucceeded() {
			// Wake up our main object that is waiting for the authentication.
			synchronized (ServerFramework.this) {
				ServerFramework.this.authenticated = true;
				ServerFramework.this.notify();
				return;
			}
		}

		/**
		 * This method is invoked if the framework wishes to abort the
		 * authentication process (e.g. if the client application responds
		 * incorrectly to a challenge).
		 */
		public void abortAuthentication() {
			if (m_logger.isInfoEnabled()) {
				m_logger.info("AuthenticationCallback:");
				m_logger.info("\tAuthentication aborted by the framework!");
			}
		}

		public byte[] challenge(byte[] arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * This inner class represents the callback object that is needed when
	 * requesting access to a service. It implements the IpAppAccess interface,
	 * i.e. the client side OSA access interface. This interface is used by the
	 * Framework as a callback reference.
	 */
	class AccessCallback extends IpClientAccessPOA {

		/**
		 * Called by OSA to terminate an access session.
		 */
		public void terminateAccess(String terminationText,
				String signingAlgorithm, byte[] digitalSignature) {
			if (m_logger.isInfoEnabled()) {
				m_logger.info("AccessCallback:");
				m_logger.info("\tAccess session terminated by the framework!");
			}
		}
	}

	/**
	 * This inner class represents the callback object that is needed when
	 * signing a service agreement. It implements the
	 * IpAppServiceAgreementManagement interface, i.e. the client side OSA
	 * interface for service agreement management. This interface is used by the
	 * Framework as a callback reference.
	 */
	class ServiceAgreementCallback extends IpAppServiceAgreementManagementPOA {

		/**
		 * Called by OSA during service selection. The agreement text should be
		 * digitally signed with the specified signing algorithm. We only
		 * support dummy behaviour, i.e. no signing.
		 */
		public byte[] signServiceAgreement(String serviceToken,
				String agreementText, String signingAlgorithm)
				throws P_INVALID_SIGNING_ALGORITHM,
				org.csapi.fw.P_INVALID_SERVICE_TOKEN,
				org.csapi.fw.P_INVALID_AGREEMENT_TEXT,
				org.csapi.TpCommonExceptions {

			if (m_logger.isInfoEnabled()) {
				m_logger
						.info("ServiceAgreementCallback: Signing service agreement ");
				m_logger.info("\tservicetoken     = " + serviceToken);
				m_logger.info("\tagreementText    = " + agreementText);
				m_logger.info("\tsigningAlgorithm = " + signingAlgorithm);
			}
			byte[] response;

			// Validate Input Parameters
			if (serviceToken == null) {
				// Missing aservice Token, cannot continue
				throw new org.csapi.fw.P_INVALID_SERVICE_TOKEN("Null");
			} else if (agreementText == null) {
				// Missing agreement text, cannot continue
				throw new org.csapi.fw.P_INVALID_AGREEMENT_TEXT("Null");
			} else if (signingAlgorithm == null) {
				// Missing signingAlgorithm, cannot continue
				throw new org.csapi.fw.P_INVALID_SIGNING_ALGORITHM("Null");
			}

			// Wake up our main object that is waiting for the agreement.
			// Make sure the response is returned before the main object
			// resumes execution.
			synchronized (ServerFramework.this) {
				ServerFramework.this.agreementSigned = true;
				ServerFramework.this.notify();

				// use no signing algorithm, just return the text
				return new byte[10];
			}
		}

		/**
		 * Called by OSA to terminate a service agreement, e.g. because the
		 * agreement expired.
		 */
		public void terminateServiceAgreement(String serviceToken,
				String terminationText, byte[] digitalSignature) {
			if (m_logger.isInfoEnabled()) {
				m_logger.info("Callback object:");
				m_logger
						.info("\tService agreement terminated by the framework!");
			}
		}
	}
}
