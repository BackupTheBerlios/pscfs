//$Id: ApplicationFramework.java,v 1.6 2005/06/12 22:24:04 huuhoa Exp $
/**
 * 
 */
package group5.client;

import group5.P_INVALID_NAME_SERVICE;
import group5.utils.CommonFuntions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.csapi.IpInterface;
import org.csapi.IpService;
import org.csapi.P_APPLICATION_NOT_ACTIVATED;
import org.csapi.P_INFORMATION_NOT_AVAILABLE;
import org.csapi.P_INVALID_INTERFACE_NAME;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_UNKNOWN_SUBSCRIBER;
import org.csapi.TpAddress;
import org.csapi.TpAddressPlan;
import org.csapi.TpAddressPresentation;
import org.csapi.TpAddressScreening;
import org.csapi.TpCommonExceptions;
import org.csapi.fw.P_ACCESS_DENIED;
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
import org.csapi.fw.P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY;
import org.csapi.fw.P_SERVICE_ACCESS_DENIED;
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
import org.csapi.fw.fw_application.discovery.IpServiceDiscoveryHelper;
import org.csapi.fw.fw_application.service_agreement.IpAppServiceAgreementManagement;
import org.csapi.fw.fw_application.service_agreement.IpAppServiceAgreementManagementPOA;
import org.csapi.fw.fw_application.service_agreement.IpServiceAgreementManagement;
import org.csapi.fw.fw_application.service_agreement.IpServiceAgreementManagementHelper;
import org.csapi.mm.TpMobilityDiagnostic;
import org.csapi.mm.TpMobilityError;
import org.csapi.mm.TpUserStatus;
import org.csapi.mm.TpUserStatusExtended;
import org.csapi.mm.TpUserStatusIndicator;
import org.csapi.mm.us.IpAppUserStatus;
import org.csapi.mm.us.IpAppUserStatusPOA;
import org.csapi.mm.us.IpUserStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

/**
 * The application framework
 * 
 * @author Nguyen Huu Hoa
 */
public class ApplicationFramework {

	/**
	 * m_logger for the system
	 */
	static Logger m_logger;

	static {
		m_logger = Logger.getLogger(ApplicationFramework.class);
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

	/**
	 * ApplicationID for authentication with Milife SDK
	 */
	protected static String clientAppID = "TestEncryption";

	/**
	 * secret key for authentication with Milife SDK
	 */
	protected static String clientAppSharedSecret = "ABCDEF0011";

	private byte[] fwSignature = null;

	private String agreementText = "Dummy agreement text";

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

	/**
	 * Default constructor, creates and starts the CORBA ORB and POA.
	 */
	protected ApplicationFramework() throws org.omg.CORBA.UserException {
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
				ApplicationFramework.orb.run();
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
	IpInitial initializeOSA(String namingSvcIor, String initial)
			throws org.omg.CORBA.UserException {

		// Find the naming service
		org.omg.CORBA.Object obj = orb.string_to_object(namingSvcIor);
		NamingContextExt namingContext = NamingContextExtHelper.narrow(obj);

		// Lookup the initial OSA reference using the naming service
		obj = namingContext.resolve_str(initial);

		return IpInitialHelper.narrow(obj);
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

		return IpAccessHelper.narrow(itf);
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

				fwSignature = signatureAndServiceMgr.DigitalSignature;
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

		svcAgmtIf.terminateServiceAgreement(svcToken, agreementText,
				fwSignature);
	}

	/**
	 * Obtains the OSA service discovery interface.
	 */
	IpServiceDiscovery obtainDiscoveryInterface(IpAccess accessIf)
			throws TpCommonExceptions, P_INVALID_INTERFACE_NAME,
			P_ACCESS_DENIED {

		/***********************************************************************
		 * Do the OSA invocation: obtainInterface();
		 **********************************************************************/
		IpInterface itf = accessIf.obtainInterface("P_DISCOVERY");

		return IpServiceDiscoveryHelper.narrow(itf);
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
	 * Issues a status-report request for a user. The result is returned
	 * asynchronously by OSA to a callback object.
	 * 
	 * @param usIf
	 *            reference to the OSA IpUserStatus interface
	 * @param user
	 *            the MSISDN of the user
	 */
	protected void requestStatus(IpUserStatus usIf, String user)
			throws TpCommonExceptions, P_INVALID_INTERFACE_TYPE,
			P_INFORMATION_NOT_AVAILABLE, P_APPLICATION_NOT_ACTIVATED,
			P_UNKNOWN_SUBSCRIBER {
		// convert user string into an address that OSA understands
		TpAddress address = new TpAddress();
		address.AddrString = user;
		address.Name = "";
		address.Plan = TpAddressPlan.P_ADDRESS_PLAN_E164;
		address.Presentation = TpAddressPresentation.P_ADDRESS_PRESENTATION_UNDEFINED;
		address.Screening = TpAddressScreening.P_ADDRESS_SCREENING_UNDEFINED;
		address.SubAddressString = "";

		TpAddress[] addressList = { address };

		// create a callback reference for the service invocation
		UserStatusCallback cb = new UserStatusCallback();
		IpAppUserStatus callback = cb._this(orb); // register with the ORB

		// invoke the service
		int assignmentId = usIf.statusReportReq(callback, addressList);
		if (m_logger.isInfoEnabled())
			m_logger.info("statusReportRequest id=" + assignmentId);

		// Now we have to wait until the OSA API has called our
		// callback object with a result.
		synchronized (this) {
			while (!statusReported) {
				try {
					wait();
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
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
	public void initApplication(String clAppID, String clAppSharedSecretKey)
			throws P_INVALID_NAME_SERVICE, UserException {
		clientAppID = clAppID;
		clientAppSharedSecret = clAppSharedSecretKey;
		// Step 1: get a reference to the OSA IpInitial interface
		String CORBA_NameService = System.getProperty("ORB.NameService");
		if (CORBA_NameService == null)
			throw new P_INVALID_NAME_SERVICE(
					"can not get value of property: ORB.NameService",
					"set value for that property before calling this function");
		try {
			IpInitial ipInitial = initializeOSA(CORBA_NameService, "IpInitial");

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Obtained IpInitial, starting authentication");
			}

			// Step 2: get a reference to the OSA IpAuthentication interface
			//
			// You could fill in your own application ID and secret to see if
			// authentication and service selection succeeds for your app.
			IpAPILevelAuthentication ipAuthentication = initializeAuthentication(
					ipInitial, clientAppID, clientAppSharedSecret);

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Obtained IpAPILevelAuthentication");
			}

			// Step 3: perform authentication
			authenticate(ipAuthentication);

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Authenticated, requesting access");
			}

			// Step 4: get a reference to the OSA IpAccess interface
			m_ipAccess = requestAccess(ipAuthentication);

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Obtained IpAccess");
			}

			// Step 5: get a refence to the IpServiceDiscovery interface
			m_ipSvcDisc = obtainDiscoveryInterface(m_ipAccess);

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Obtained IpServiceDiscovery");
			}
			// step 6: in selectSCFs

			// Step 7: get a refence to the IpServiceAgreementManagement
			// interface
			m_ipSvcAgmt = obtainServiceAgreementInterface(m_ipAccess);

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Obtained IpServiceAgreementManagement");
			}
		} catch (P_ACCESS_DENIED ex) {
			m_logger
					.error("Access denied while requesting access to interface IpAccess. More information: "
							+ ex.getMessage());
		} catch (P_INVALID_INTERFACE_TYPE ex) {
			m_logger.error("Invalid interface type. More information: "
					+ ex.getMessage());
		} catch (P_INVALID_INTERFACE_NAME ex) {
			m_logger.error("Invalid interface name. More information: "
					+ ex.getMessage());
		} catch (P_INVALID_ACCESS_TYPE ex) {
			m_logger.error("Invalid access type. More information: "
					+ ex.getMessage());
		} catch (P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY ex) {
			m_logger
					.error("No acceptable encryption capability. More information: "
							+ ex.getMessage());
		} catch (P_INVALID_AUTH_TYPE ex) {
			m_logger.error("Invalid authentication type. More information: "
					+ ex.getMessage());
		} catch (P_INVALID_DOMAIN_ID ex) {
			m_logger.error("Invalid domain id. More information: "
					+ ex.getMessage());
		}
	}

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
	 * </pre>
	 * 
	 * Here is the list of services that can be query from OSA Framework
	 *         <ul>
	 *         <li><b>NULL</b> An empty (NULL) string indicates no SCF name.</li>
	 *         <li><b>P_GENERIC_CALL_CONTROL</b> The name of the Generic Call
	 *         Control SCF.</li>
	 *         <li><b>P_MULTI_PARTY_CALL_CONTROL</b> The name of the
	 *         MultiParty Call Control SCF.</li>
	 *         <li><b>P_MULTI_MEDIA_CALL_CONTROL</b> The name of the
	 *         MultiMedia Call Control SCF.</li>
	 *         <li><b>P_CONFERENCE_CALL_CONTROL</b> The name of the Conference
	 *         Call Control SCF.</li>
	 *         <li><b>P_USER_INTERACTION</b> The name of the User Interaction
	 *         SCFs.</li>
	 *         <li><b>P_USER_INTERACTION_ADMIN</b> The name of the User
	 *         Interaction Administration SCF.</li>
	 *         <li><b>P_TERMINAL_CAPABILITIES</b> The name of the Terminal
	 *         Capabilities SCF.</li>
	 *         <li><b>P_USER_BINDING</b> The name of the User Binding SCF.</li>
	 *         <li><b>P_USER_LOCATION</b> The name of the User Location SCF.</li>
	 *         <li><b>P_USER_LOCATION_CAMEL</b> The name of the Network User
	 *         Location SCF.</li>
	 *         <li><b>P_USER_LOCATION_EMERGENCY</b> The name of the User
	 *         Location Emergency SCF.</li>
	 *         <li><b>P_USER_STATUS</b> The name of the User Status SCF.</li>
	 *         <li><b>P_EXTENDED_USER_STATUS</b> The name of Extended User
	 *         Status SCF.</li>
	 *         <li><b>P_DATA_SESSION_CONTROL</b> The name of the Data Session
	 *         Control SCF.</li>
	 *         <li><b>P_GENERIC_MESSAGING</b> The name of the Generic
	 *         Messaging SCF.</li>
	 *         <li><b>P_CONNECTIVITY_MANAGER</b> The name of the Connectivity
	 *         Manager SCF.</li>
	 *         <li><b>P_CHARGING</b> The name of the Charging SCF.</li>
	 *         <li><b>P_ACCOUNT_MANAGEMENT</b> The name of the Account
	 *         Management SCF.</li>
	 *         <li><b>P_POLICY_PROVISIONING</b> The name of the Policy
	 *         Management provisioning SCF.</li>
	 *         <li><b>P_POLICY_EVALUATION</b> The name of the Policy
	 *         Management policy evaluation SCF.</li>
	 *         <li><b>P_PAM_ACCESS</b> The name of PAM presentity SCF.</li>
	 *         <li><b>P_PAM_EVENT_MANAGEMENT</b> The name of PAM watcher SCF.</li>
	 *         <li><b>P_PAM_PROVISIONING</b> The name of PAM provisioning SCF.</li>
	 *         </ul>
	 */
	public IpService selectSCFs(String serviceName) {
		try {
			// Step 6: obtain a service ID for the desired service
			// String svcId = discoverService(m_ipSvcDisc, "P_USER_STATUS");
			String svcId = discoverService(m_ipSvcDisc, serviceName);

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Obtained service ID: " + svcId);
			}

			// Step 8: obtain a service token for the session
			String svcToken = selectService(m_ipSvcAgmt, svcId);

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Obtained service token: " + svcToken);
			}

			// Step 9: sign the service agreement
			IpService svcMgr = signServiceAgreement(m_ipSvcAgmt, svcToken);
			// IpUserStatus ipUS = IpUserStatusHelper.narrow(svcMgr);

			if (m_logger.isInfoEnabled()) {
				m_logger.info("Signed service agreement");
			}
			return svcMgr;
		} catch (P_ILLEGAL_SERVICE_TYPE ex) {
			m_logger.error("Illegal service type: " + ex.getMessage());
		} catch (P_UNKNOWN_SERVICE_TYPE ex) {
			m_logger.error("Unknown service type: " + ex.getMessage());
		} catch (P_ACCESS_DENIED ex) {
			m_logger.error("Access to service was denied. More information: "
					+ ex.getMessage());
		} catch (P_INVALID_PROPERTY ex) {
			m_logger.error("Invalid property. More information: "
					+ ex.getMessage());
		}
		// catch exceptions from selectService
		catch (IOException ex) {
			m_logger.error("File IO exception. More information: "
					+ ex.getMessage());
		} catch (P_INVALID_SERVICE_ID ex) {
			m_logger.error("Invalid service ID. More information: "
					+ ex.getMessage());
		}
		// catch exceptions from signServiceAgreement
		catch (P_SERVICE_ACCESS_DENIED ex) {
			m_logger.error("Access to service was denied. More information: "
					+ ex.getMessage());
		} catch (P_INVALID_SIGNATURE ex) {
			m_logger.error("The signature was invalid. More information: "
					+ ex.getMessage());
		} catch (P_INVALID_SERVICE_TOKEN ex) {
			m_logger.error("Invalid service token. More information: "
					+ ex.getMessage());
		} catch (P_INVALID_SIGNING_ALGORITHM ex) {
			m_logger.error("Invalid signing algorithm. More information: "
					+ ex.getMessage());
		} catch (P_INVALID_AGREEMENT_TEXT ex) {
			m_logger.error("Invalid agreement text. More information: "
					+ ex.getMessage());
		}
		// common exceptions
		catch (TpCommonExceptions ex) {
			m_logger.error("Some errors occur: " + ex.getMessage());
			ex.printStackTrace();
		}
		// m_logger.error("Some errors occur: " + e.getMessage());
		// e.printStackTrace();
		// }
		return null;
	}

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
			synchronized (ApplicationFramework.this) {
				ApplicationFramework.this.authenticated = true;
				ApplicationFramework.this.notify();
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
			synchronized (ApplicationFramework.this) {
				ApplicationFramework.this.agreementSigned = true;
				ApplicationFramework.this.notify();

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

	/**
	 * This inner class represents the callback object that is needed when
	 * requesting the status of a user. It implements the IpAppUserStatus
	 * interface, which is used by OSA's User Status SCS to report results and
	 * errors to.
	 */
	class UserStatusCallback extends IpAppUserStatusPOA {

		/**
		 * Called by OSA to deliver the result of a status-report request.
		 * 
		 * @param assignmentID
		 *            assignment ID of the status-report request.
		 * @param status
		 *            status of one or more users
		 */
		public void statusReportRes(int assignmentId, TpUserStatus[] status) {

			if (m_logger.isInfoEnabled()) {
				m_logger
						.info("UserStatusCallback: Received user status report:");
			}

			// For each user in the array, print the user ID and the status
			for (int i = 0; i < status.length; i++) {
				TpUserStatus s = status[i];
				if (m_logger.isInfoEnabled())
					m_logger.info("\t" + s.UserID.AddrString + ": ");

				// Check if no error occurred
				if (s.StatusCode.value() == TpMobilityError._P_M_OK) {
					switch (s.Status.value()) {
					case TpUserStatusIndicator._P_US_BUSY:
						m_logger.info("BUSY");
						break;
					case TpUserStatusIndicator._P_US_NOT_REACHABLE:
						m_logger.info("NOT REACHABLE");
						break;
					case TpUserStatusIndicator._P_US_REACHABLE:
						m_logger.info("REACHABLE");
						break;
					default:
						m_logger.info("Undefined status (" + s.Status.value()
								+ ")");
						break;
					}
				} else {
					m_logger.error("error occured (code = "
							+ s.StatusCode.value() + ")");
				}
			}

			// Wake up our main object that is waiting for the report.
			// Make sure the response is returned before the main object
			// resumes execution.
			synchronized (ApplicationFramework.this) {
				ApplicationFramework.this.statusReported = true;
				ApplicationFramework.this.notify();
				return;
			}
		}

		/**
		 * Called by OSA to deliver an error result of a status-report request.
		 * 
		 * @param assignmentID
		 *            assignment ID of the failed status-report request
		 * @param cause
		 *            the error that led to the failure
		 * @param diagnostic
		 *            additional information about the error that led to the
		 *            failure
		 */
		public void statusReportErr(int assignmentId, TpMobilityError cause,
				TpMobilityDiagnostic diagnostic) {
			if (m_logger.isInfoEnabled()) {
				m_logger.info("User Status error:");
				m_logger.info("Assignment ID = " + assignmentId);
				String causeString = "Cause = ";
				switch (cause.value()) {
				case TpMobilityError._P_M_OK:
					causeString = "OK";
					break;
				case TpMobilityError._P_M_ABSENT_SUBSCRIBER:
					causeString = "ABSENT SUBSCRIBER";
					break;
				case TpMobilityError._P_M_SYSTEM_FAILURE:
					causeString = "SYSTEM FAILURE";
					break;
				case TpMobilityError._P_M_UNAUTHORIZED_APPLICATION:
					causeString = "UNAUTHORIZED APPLICATION";
					break;
				case TpMobilityError._P_M_UNAUTHORIZED_NETWORK:
					causeString = "UNAUTHORIZED NETWORK";
					break;
				case TpMobilityError._P_M_UNKNOWN_SUBSCRIBER:
					causeString = "UNKNOWN SUBSCRIBER";
					break;
				default:
					causeString = "UNKNOWN";
				}
				m_logger.info(causeString);
				// Diagnostic could be analyzed the same way as cause, not done
				// here
				m_logger.info("Diagnostic = " + diagnostic.value());
			}

			// Wake up our main object that is waiting for the report.
			// Make sure the response is returned before the main object
			// resumes execution.
			synchronized (ApplicationFramework.this) {
				ApplicationFramework.this.statusReported = true;
				ApplicationFramework.this.notify();
				return;
			}
		}

		/** Not implemented in this sample. Only defined for completeness */
		public void triggeredStatusReport(int assignmentId, TpUserStatus status) {
			if (m_logger.isInfoEnabled())
				m_logger.info("Triggered status report not implemented");
		}

		/** Not implemented in this sample. Only defined for completeness */
		public void triggeredStatusReportErr(int assignmentId,
				TpMobilityError cause, TpMobilityDiagnostic diagnostic) {
			if (m_logger.isInfoEnabled())
				m_logger.info("Triggered status report error not implemented");
		}

		public void extendedStatusReportRes(int arg0,
				TpUserStatusExtended[] arg1) {
			// TODO Auto-generated method stub

		}

		public void extendedStatusReportErr(int arg0, TpMobilityError arg1,
				TpMobilityDiagnostic arg2) {
			// TODO Auto-generated method stub

		}

		public void extTriggeredStatusReport(int arg0, TpUserStatusExtended arg1) {
			// TODO Auto-generated method stub

		}

		public void extTriggeredStatusReportErr(int arg0, TpMobilityError arg1,
				TpMobilityDiagnostic arg2) {
			// TODO Auto-generated method stub

		}
	}

}
