@echo off
set PROJ_HOME=E:\RWTH Aachen\Lab\source\gccs
set MYCLASSPATH=%JACORB_HOME%\lib\avalon-framework-4.1.5.jar;%JACORB_HOME%\lib\jacorb.jar;%JACORB_HOME%\lib\logkit-1.2.jar;"%PROJ_HOME%\lib\parlay_interfaces-5.0.jar";"%PROJ_HOME%\lib\log4j-1.2.9.jar";"%PROJ_HOME%\bin"
set MYPROP=-Djacorb.home=C:/work/jacorb
cd "%PROJ_HOME%"
IF "%1"=="ns" (
REM ORBInitRef.NameService=corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root %*
	start ns -Djacorb.naming.ior_filename=corbaloc::localhost:2050/StandardNS/NameServer%2DPOA/_root %*
REM	start ns -Djacorb.naming.ior_filename=C:/Work/NS_Ref %*
)
IF "%1"=="server" (
	java -classpath %MYCLASSPATH% group5.server.framework.GCCSServer %*
	goto quit
)
IF "%1"=="client" (
	start java -classpath %MYCLASSPATH% group5.client.GCCSClient %MYPROP%" %*
	goto quit
)	
IF "%1"=="AIC" (
	java -classpath %MYCLASSPATH% group5.client.appinitcall.AICClient %MYPROP% %*
	goto quit
)	
IF "%1"=="NTC" (
	java -classpath %MYCLASSPATH% group5.client.number_translation_callback.NTC_client %MYPROP% %*
	goto quit
)	
IF "%1"=="NT" (
	java -classpath %MYCLASSPATH% group5.client.number_translation.NT_client %MYPROP% %*
	goto quit
)
IF "%1"=="test" (
  FOR /L %%I IN (1,1,10) DO CALL :startTest
	goto quit
)
goto usage
:startTest
start java -classpath %MYCLASSPATH% group5.client.appinitcall.AICClient %MYPROP% %*
goto :eof
:usage
@echo Usage: run ^<command^>
@echo Where ^<command^> is:
@echo 	server:	run the GCCS Server
@echo 	AIC:	run the Application Initiated Call client
@echo 	NT:	run the Number Translation client
@echo 	NTC:	run the Number Translation Callback client
	
:quit
set MYCLASSPATH=""
set MYPROP=""
cd "%PROJ_HOME%\etc"