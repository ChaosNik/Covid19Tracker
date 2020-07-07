package org.unibl.etf.mdp.chat.server;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Properties;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server
{

	//public static int TCP_PORT = 9000;
	private static int SSL_PORT = 8443;
	private static String KEY_STORE_PATH = "./keystore.jks";
	private static String KEY_STORE_PASSWORD = "securemdp";

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
	    SSL_PORT = Integer.parseInt(p.getProperty("SSL_PORT"));
	    KEY_STORE_PATH = p.getProperty("KEY_STORE_PATH");
	    KEY_STORE_PASSWORD = p.getProperty("KEY_STORE_PASSWORD");
	}
	public static int n = 10;
	public static ArrayList<User> medics = new ArrayList<User>();
	public static ArrayList<User> citizens = new ArrayList<User>();
	public static ArrayDeque<User> freeCitizens = new ArrayDeque<User>();
	public static ArrayDeque<User> freeMedics = new ArrayDeque<User>();
	public static ArrayList<Pair<User, User>> citizen_medic = new ArrayList<Pair<User, User>>();
	public static void main(String[] args)
	{

		try
		{
			/*ServerSocket ss = new ServerSocket(TCP_PORT);
			System.out.println("Chat server running...");
			while (true)
			{
				Socket sock = ss.accept();
				new ServerThread(sock).start();
			}*/
			new RemindingServer().start();
			System.setProperty("javax.net.ssl.keyStore", KEY_STORE_PATH);
			System.setProperty("javax.net.ssl.keyStorePassword", KEY_STORE_PASSWORD);

			SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			ServerSocket ss = ssf.createServerSocket(SSL_PORT);
			System.out.println("Chat server running...");
			while (true)
			{
				SSLSocket s = (SSLSocket) ss.accept();
				new ServerThread(s).start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
