cd bin
IF "%1"=="ns" (
REM ORBInitRef.NameService=corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root
	start ns -Djacorb.naming.ior_filename=corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root
REM	start ns -Djacorb.naming.ior_filename=C:/Work/NS_Ref
)
IF "%1"=="server" (
	start jaco group5.server.GCCSServer
)
IF "%1"=="client" (
	start jaco group5.client.GCCSClient
)	
cd ..