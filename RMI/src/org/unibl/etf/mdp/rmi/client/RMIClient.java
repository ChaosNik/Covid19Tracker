package org.unibl.etf.mdp.rmi.client;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.unibl.etf.mdp.rmi.image.ServerInterface;

public class RMIClient
{
	public static final String PATH = "resources";
	public static final int TCP_PORT = 9000;
	public static boolean working = true;

	public void main() //JEDNOSTAVNIJE POKRETANJE SERVERA
	//public static void main(String[] args) //KORISTIMO ZA TESTIRANJE
	{
		System.setProperty("java.security.policy", PATH + File.separator + "client_policyfile.txt");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try
		{
			Registry registry = LocateRegistry.getRegistry(1099);
			ServerInterface server = (ServerInterface) registry.lookup("Server");
			
			byte[] data = "cao".getBytes();
			System.out.println("Podaci poslani: ");
			for(byte b : data)System.out.print(b);
			System.out.println();
			server.save(1, "text.txt", org.unibl.etf.mdp.rmi.image.CompressionUtils.compress(data));
			server.save(1, "text1.txt", org.unibl.etf.mdp.rmi.image.CompressionUtils.compress(data));
			server.save(1, "text2.txt", org.unibl.etf.mdp.rmi.image.CompressionUtils.compress(data));
			System.out.println("Dokumenti: " + server.view(1));
			byte[] returnedData = org.unibl.etf.mdp.rmi.image.CompressionUtils.decompress(server.download(1, "text.txt"));
			server.save(1, "textVraceno.txt", returnedData);
			System.out.println("Podaci vraceni:");
			for(byte b : returnedData)System.out.print(b);
			System.out.println();
			System.out.println("Client process done!!!");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}