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

public class RemindingServer extends Thread
{

	public static int TCP_PORT = 9000;

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
	    TCP_PORT = Integer.parseInt(p.getProperty("TCP_PORT"));
	}
	public static int n = 10;
	@Override
	public void run()
	{
		try
		{
			ServerSocket ss = new ServerSocket(TCP_PORT);
			System.out.println("Remainding server running...");
			while (true)
			{
				Socket sock = ss.accept();
				new RemindingThread(sock).start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
