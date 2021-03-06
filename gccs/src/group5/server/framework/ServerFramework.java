//$Id: ServerFramework.java,v 1.18 2005/07/29 00:32:43 huuhoa Exp $
/**
 * 
 */
package group5.server.framework;

import group5.P_INVALID_NAME_SERVICE;
import group5.utils.CommonFuntions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
 * @author Nguyen Duc Du Khuong
 * @author Nguyen Huu Hoa
 * @author Hoang Trung Hai
 */
public class ServerFramework {

	/**
	 * m_logger for the system
	 */
	private static Logger m_logger;

	static {
		m_logger = Logger
				.getLogger(group5.server.framework.ServerFramework.class);
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
		return m_ipInitial;
	}

	/**
	 * @return reference to IpAPILevelAuthentication interface
	 * @throws TpCommonExceptions
	 */
	protected IpAPILevelAuthentication getIpAuthentication()
			throws TpCommonExceptions {
		if (m_ipAPILevelAuthentication == null) {
			try {
				// obtain the authentication interface
				m_ipAPILevelAuthentication = initializeAuthentication(
						getIpInitial(), m_applicationID, m_password);
				// perform authentication
				m_logger.debug("perform authentication");
				authenticate(m_ipAPILevelAuthentication);
			} catch (P_INVALID_INTERFACE_TYPE ex) {
				m_logger.fatal("Invalid interface type: " + ex.getMessage());
				throw new TpCommonExceptions(4, "Invalid interface type: "
						+ ex.getMessage());
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
		IpAppAuthenticationImpl cb = new IpAppAuthenticationImpl(this, password);

		// Register the callback object with the ORB
		IpClientAPILevelAuthentication callback = cb._this(orb);

		// Create an OSA domain ID containing the application ID and a reference
		// to the callback object
		TpDomainID domainID = new TpDomainID();
		domainID.ServiceSupplierID("ServiceRegistration");
		// domainID.ClientAppID(appId);
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
				IpAccess ipAccess = requestAccess(getIpAuthentication());
				m_logger.debug("Got the reference to IpAccess");
				org.csapi.IpInterface ipIF = ipAccess
						.obtainInterface(regService);
				m_logger.debug("Got the reference to IpFwServiceRegistration");
				m_ipFwSrvReg = IpFwServiceRegistrationHelper.narrow(ipIF);
			} catch (P_INVALID_INTERFACE_NAME ex) {
				m_logger.fatal("Invalid interface name: " + regService);
				throw new TpCommonExceptions(14, ex.ExtraInformation);
			} catch (P_INVALID_INTERFACE_TYPE ex) {
				m_logger.fatal("Invalid interface type: " + regService);
				throw new TpCommonExceptions(14, ex.ExtraInformation);
			} catch (P_ACCESS_DENIED ex) {
				m_logger.fatal("access denied obtaining interface "
						+ regService);
				throw new TpCommonExceptions(14, ex.ExtraInformation);
			} catch (P_INVALID_ACCESS_TYPE ex) {
				m_logger.fatal("invalid access type while obtaining interface "
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
	 * 
	 * @param serviceTypeName
	 *            "P_GENERIC_CALL_CONTROL" in case of generic call control
	 * @param scsproperties
	 *            List of service capability properties
	 * @param class1
	 *            Class of the interface life cycle manager
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

	private synchronized String registerService(String serviceTypeName,
			SCSProperties scProp, IpServiceInstanceLifecycleManagerPOA ipSILM)
			throws TpCommonExceptions {
		String serviceID = registerService(serviceTypeName, scProp);
		String s2 = scProp.getServiceName();
		try {
			m_logger.info("serviceID=" + serviceID);
			if (serviceID != null) {
				IpFwServiceRegistration ipFSR = obtainIpFwServiceRegistration();
				ipFSR.announceServiceAvailability(serviceID, ipSILM._this(orb));
			} else {
				throw new TpCommonExceptions("Error discovering service name="
						+ s2, 15, s2);
			}
		} catch (P_INVALID_INTERFACE_TYPE ex) {
			throw new TpCommonExceptions("Invalid interface type", 14,
					serviceID);
		} catch (P_ILLEGAL_SERVICE_ID ex) {
			throw new TpCommonExceptions("Illegal service ID", 14, serviceID);
		} catch (P_UNKNOWN_SERVICE_ID ex) {
			throw new TpCommonExceptions("Unknown service ID", 14, serviceID);
		}
		return serviceID;
	}

	protected String registerService(String serviceTypeName,
			SCSProperties scProp) throws TpCommonExceptions {
		m_logger.info("Registering service type=" + serviceTypeName
				+ " version=" + scProp.getServiceVersion());
		try {
			return obtainIpFwServiceRegistration().registerService(
					serviceTypeName, scProp.getServicePropertyList());
		} catch (P_MISSING_MANDATORY_PROPERTY ex) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ ex.ExtraInformation);
			throw new TpCommonExceptions("Missing mandatory property for "
					+ serviceTypeName, 14, ex.ExtraInformation);
		} catch (P_ILLEGAL_SERVICE_TYPE ex) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ ex.ExtraInformation);
			throw new TpCommonExceptions("Illegal service type for "
					+ serviceTypeName, 14, ex.ExtraInformation);
		} catch (P_UNKNOWN_SERVICE_TYPE ex) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ ex.ExtraInformation);
			throw new TpCommonExceptions("Unknown service type for "
					+ serviceTypeName, 14, ex.ExtraInformation);
		} catch (P_PROPERTY_TYPE_MISMATCH ex) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ ex.ExtraInformation);
			throw new TpCommonExceptions("Property type mismatch for "
					+ serviceTypeName, 14, ex.ExtraInformation);
		} catch (P_DUPLICATE_PROPERTY_NAME ex) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ ex.ExtraInformation);
			throw new TpCommonExceptions("Duplicate property name for "
					+ serviceTypeName, 14, ex.ExtraInformation);
		} catch (P_SERVICE_TYPE_UNAVAILABLE ex) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ ex.ExtraInformation);
			throw new TpCommonExceptions("Service type unavailable for "
					+ serviceTypeName, 14, ex.ExtraInformation);
		} catch (TpCommonExceptions ex) {
			m_logger.error("Error registering " + serviceTypeName + ": "
					+ ex.ExtraInformation);
			throw new TpCommonExceptions("Service registration failed", 14,
					ex.ExtraInformation);
		} catch (Throwable ex) {
			m_logger.error("Unknown error in registerService of "
					+ serviceTypeName, ex);
			throw new TpCommonExceptions("Error in registerService:"
					+ ex.getClass(), 14, ex.getMessage());
		}
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
			P_INVALID_SIGNATURE {
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
	final class IpAppAuthenticationImpl extends
			IpClientAPILevelAuthenticationPOA {
		private Object obj;

		private byte password[];

		public IpAppAuthenticationImpl(Object obj, String s) {
			this.obj = obj;
			password = s.getBytes();
		}

		public byte[] authenticate(byte abyte0[]) {
			m_logger.debug("IpAppAuthentication::authenticate");
			m_logger.debug("Challenge = " + abyte0);
			int i = abyte0[2] * 256 + abyte0[3];
			byte byte0 = abyte0[4];
			int j = i - byte0 - 5;
			byte abyte1[] = new byte[1 + password.length + byte0];
			abyte1[0] = abyte0[1];
			System.arraycopy(password, 0, abyte1, 1, password.length);
			System.arraycopy(abyte0, 5, abyte1, 1 + password.length, byte0);
			byte abyte2[] = null;
			try {
				MessageDigest messagedigest = MessageDigest.getInstance("MD5");
				messagedigest.update(abyte1);
				abyte2 = messagedigest.digest();
			} catch (NoSuchAlgorithmException ex) {
				m_logger.fatal("IpAppAuthentication::authenticate: " + ex);
				return new byte[0];
			}
			int k = 5 + abyte2.length + j;
			byte abyte3[] = new byte[k];
			abyte3[0] = 2;
			abyte3[1] = abyte0[1];
			abyte3[2] = (new Integer(k / 256)).byteValue();
			abyte3[3] = (new Integer(k % 256)).byteValue();
			abyte3[4] = (new Integer(abyte2.length)).byteValue();
			System.arraycopy(abyte2, 0, abyte3, 5, abyte2.length);
			System.arraycopy(abyte0, i - j, abyte3, 5 + abyte2.length, j);
			return abyte3;
		}

		public void authenticationSucceeded() {
			m_logger.debug("IpAppAuthentication::authenticationSucceeded");
			synchronized (ServerFramework.this) {
				ServerFramework.this.authenticated = true;
				ServerFramework.this.notify();
			}
		}

		public void abortAuthentication() {
			m_logger.debug("IpAppAuthentication::abortAuthentication");
			synchronized (obj) {
				obj.notify();
			}
		}

		public byte[] challenge(byte[] challenge) {
			m_logger
					.info("Framework challenges service in authentication phase");
			return new byte[] {1, 2, 3};
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
				m_logger.info("Signing service agreement ");
				m_logger.info("\tservicetoken     = " + serviceToken);
				m_logger.info("\tagreementText    = " + agreementText);
				m_logger.info("\tsigningAlgorithm = " + signingAlgorithm);
			}

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
