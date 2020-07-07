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

public class BaseNetworkService
{

	private static int TCP_PORT = 9000;
	private static String NETWORK_ADDRESS = "127.0.0.1";
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
	    NETWORK_ADDRESS = p.getProperty("NETWORK_ADDRESS");
	    TCP_PORT = Integer.parseInt(p.getProperty("SSL_PORT"));
	    SSL_PORT = Integer.parseInt(p.getProperty("SSL_PORT"));
	    KEY_STORE_PATH = p.getProperty("KEY_STORE_PATH");
	    KEY_STORE_PASSWORD = p.getProperty("KEY_STORE_PASSWORD");
	}
	BufferedReader in;
	PrintWriter out;
	//Socket sock;
	SSLSocket sock;

	public boolean connect()
	{
		try
		{
			InetAddress addr = InetAddress.getByName(NETWORK_ADDRESS);
			//sock = new Socket(addr, TCP_PORT);
			System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
			System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);
			SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
			sock = (SSLSocket) sf.createSocket(NETWORK_ADDRESS, SSL_PORT);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
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
			out.println("END");
			in.close();
			out.close();
			sock.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
