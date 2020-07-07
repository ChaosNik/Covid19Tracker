package org.unibl.etf.mdp.rmi.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import net.etfbl.api.*;

public class Server implements ServerInterface
{

	private static String ROOT = "files";
	private static String RESOURCES = "resources";
	private static int RMI_PORT = 1099;
	private static String POLICY_SERVER = "server_policyfile.txt";
	
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
	static
	{
		FileReader reader;  
	    Properties p=new Properties();  
	    try
	    {
	    	reader=new FileReader("config.properties"); 
			p.load(reader);
		}
	    catch (IOException e)
	    {
			e.printStackTrace();
		}  
	    ROOT = p.getProperty("ROOT");
	    RESOURCES = p.getProperty("RESOURCES");
	    RMI_PORT = Integer.parseInt(p.getProperty("RMI_PORT"));
	    POLICY_SERVER = p.getProperty("POLICY_SERVER");
		    
		try
		{
			File logdir = new File("log");
			logdir.mkdir();
			logger.addHandler(new FileHandler("log/rmi.txt", true));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
	}
	public Server()
	{
		super();
	}
	@Override
	public boolean save(long token, String fileName, byte[] data) throws RemoteException
	{
		logger.log(Level.INFO, "save");
		ServiceServiceLocator locator = new ServiceServiceLocator();
		try
		{
			data = CompressionUtils.decompress(data);
			Service service = locator.getService();
			boolean isValid = service.isValid(token);
			if(!isValid) return false;
			
			File root = new File(ROOT);
			if(!root.exists()) root.mkdir();
			File folder = new File(ROOT + File.separator + token);
			if(!folder.exists()) folder.mkdir();
			File file = new File(ROOT + File.separator + token + File.separator + fileName);
			if(!file.exists()) file.createNewFile();
			Files.write(file.toPath(), data, StandardOpenOption.TRUNCATE_EXISTING);
			logger.log(Level.INFO, "File " + fileName + " sent from user " + token);
			return true;
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return false;
		}
	}

	@Override
	public List<String> view(long token) throws RemoteException
	{
		logger.log(Level.INFO, "view");
		ServiceServiceLocator locator = new ServiceServiceLocator();
		try
		{
			Service service = locator.getService();
			boolean isValid = service.isValid(token);
			if(!isValid) return null;
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return null;
		}
		File root = new File(ROOT);
		if(!root.exists()) root.mkdir();
		File folder = new File(ROOT + File.separator + token);
		if(!folder.exists()) folder.mkdir();
		ArrayList<String> result = new ArrayList<String>();
		for(File file : folder.listFiles())result.add(file.getName());
		logger.log(Level.INFO, "Files sent by user " + token + " were viewed");
		return result;
	}
	@Override
	public byte[] download(long token, String fileName) throws RemoteException
	{
		logger.log(Level.INFO, "download");
		ServiceServiceLocator locator = new ServiceServiceLocator();
		try
		{
			Service service = locator.getService();
			boolean isValid = service.isValid(token);
			if(!isValid) return null;
			
			File root = new File(ROOT);
			if(!root.exists()) root.mkdir();
			File folder = new File(ROOT + File.separator + token);
			if(!folder.exists()) folder.mkdir();
			File file = new File(ROOT + File.separator + token + File.separator + fileName);
			if(!file.exists()) file.createNewFile();
			logger.log(Level.INFO, "File " + fileName + " sent by user " + token + " was downloaded");
			return CompressionUtils.compress(Files.readAllBytes(file.toPath()));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return null;
		}
	}
	public static void main(String args[])
	{
		System.setProperty("java.security.policy", RESOURCES + File.separator + POLICY_SERVER);
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}
		try
		{
			Server server = new Server();
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
			Registry registry = LocateRegistry.createRegistry(RMI_PORT);
			registry.rebind("Server", stub);
			System.out.println("RMI server started...");
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
		
		//test();
	}
	
	private void test()
	{
		ServiceServiceLocator locator = new ServiceServiceLocator();
		try
		{
			Service service = locator.getService();
			System.out.println(service.register("aaaa", "aaaa", "aaaa", 12341234, false));
			boolean isValid = service.isValid(5);
			if(!isValid) System.out.println("NEEE");
			else System.out.println("DAAA");
			service.unregister(3);
			isValid = service.isValid(3);
			System.out.println(3);
			if(!isValid) System.out.println("NEEE");
			else System.out.println("DAAA");
			System.out.println(service.register("aaaa", "aaaa", "aaaa", 12341234, false));
			isValid = service.isValid(6);
			if(!isValid) System.out.println("NEEE");
			else System.out.println("DAAA");
			System.out.println("7");
			isValid = service.isValid(7);
			if(!isValid) System.out.println("NEEE");
			else System.out.println("DAAA");
		}
		catch(Exception e)
		{
			
		}
	}
}
