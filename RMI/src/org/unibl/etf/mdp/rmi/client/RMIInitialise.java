package org.unibl.etf.mdp.rmi.client;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.unibl.etf.mdp.rmi.image.ServerInterface;

public class RMIInitialise
{
	public static final String PATH = "resources";
	public static final int TCP_PORT = 9000;
	public static boolean working = true;

	/*public static void main(String[] args)
	{
		System.setProperty("java.security.policy", PATH + File.separator + "client_policyfile.txt");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try
		{
			Registry registry = LocateRegistry.getRegistry(1099);

			ServerInterface server = (ServerInterface) registry.lookup("Server");
			byte[] data = "Test".getBytes();
			server.save("test.txt", data, "muki");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}*/
}