package org.unibl.etf.mdp.chat.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.unibl.etf.mdp.chat.view.Main;

public class ReminderNetworkService
{

	private static int TCP_PORT = 9000;
	private static String NETWORK_ADDRESS = "127.0.0.1";
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
	    NETWORK_ADDRESS = p.getProperty("NETWORK_ADDRESS");
	    TCP_PORT = Integer.parseInt(p.getProperty("TCP_PORT"));
	}
	BufferedReader in;
	public Socket sock;

	public boolean connect()
	{
		try
		{
			InetAddress addr = InetAddress.getByName(NETWORK_ADDRESS);
			sock = new Socket(addr, TCP_PORT);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public void disconnect()
	{
		try
		{
			in.close();
			sock.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
