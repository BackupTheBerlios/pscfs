cd bin
IF "%1"=="ns" (
	start ns -Djacorb.naming.ior_filename=C:/Work/NS_Ref
)
IF "%1"=="server" (
	start jaco group5.server.GCCSServer
)
IF "%1"=="client" (
	start jaco group5.client.GCCSClient
)	
cd ..