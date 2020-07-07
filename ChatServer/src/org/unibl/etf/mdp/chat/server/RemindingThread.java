package org.unibl.etf.mdp.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class RemindingThread extends Thread
{

	private Socket sock;
	private PrintWriter out;
	
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
	static
	{
		try
		{
			File logdir = new File("log");
			logdir.mkdir();
			logger.addHandler(new FileHandler("log/chat.txt", true));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
	}

	public RemindingThread(Socket sock)
	{
		this.sock = sock;
		try
		{
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
		}
		catch (Exception e)
		{
			//logger.log(Level.INFO, e.toString(), e);
		}
	}

	@Override
	public void run()
	{
		logger.log(Level.INFO, "run");
		while(!sock.isClosed())
		{
			try
			{
				out.println("REFRESH");
				Thread.sleep(Server.n * 1000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
