@echo off
cd bin
set MYCLASSPATH=%JACORB_HOME%\lib\avalon-framework-4.1.5.jar;%JACORB_HOME%\lib\jacorb.jar;%JACORB_HOME%\lib\logkit-1.2.jar;..\lib\parlay_interfaces-5.0.jar;..\lib\log4j-1.2.9.jar;.
set MYPROP=-Djacorb.home=C:/work/jacorb
IF "%1"=="ns" (
REM ORBInitRef.NameService=corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root %*
	start ns -Djacorb.naming.ior_filename=corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root %*
REM	start ns -Djacorb.naming.ior_filename=C:/Work/NS_Ref %*
)
IF "%1"=="server" (
	java -classpath %MYCLASSPATH% group5.server.GCCSServer %*
)
IF "%1"=="client" (
	java -classpath %MYCLASSPATH% group5.client.GCCSClient -Djacorb.home=C:/work/jacorb/ %*
)	
IF "%1"=="nt1" (
	java -classpath %MYCLASSPATH% group5.client.number_translation.MyAppInit -Djacorb.home=C:/work/jacorb/ %*
)	
set MYCLASSPATH=""
set MYPROP=""
cd ..