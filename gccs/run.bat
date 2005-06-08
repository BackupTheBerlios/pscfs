@echo off
cd bin
set CLASSPATH=C:\Work\JacORB\lib\jacorb.jar;..\lib\parlay_interfaces-5.0.jar;..\lib\log4j-1.2.9.jar;.
IF "%1"=="ns" (
REM ORBInitRef.NameService=corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root %*
	start ns -Djacorb.naming.ior_filename=corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root %*
REM	start ns -Djacorb.naming.ior_filename=C:/Work/NS_Ref %*
)
IF "%1"=="server" (
	java -classpath %CLASSPATH% group5.server.GCCSServer %*
)
IF "%1"=="client" (
	java -classpath %CLASSPATH% group5.client.GCCSClient %*
)	
cd ..