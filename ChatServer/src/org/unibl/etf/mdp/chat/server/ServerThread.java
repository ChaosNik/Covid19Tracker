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

public class ServerThread extends Thread
{

	private Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	private User citizen;
	private User medic;
	
	private static boolean shouldConnect = false;
	
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

	public ServerThread(Socket sock)
	{
		this.sock = sock;
		try
		{
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
		}
		catch (Exception e)
		{
			//logger.log(Level.INFO, e.toString(), e);
		}
	}

	public boolean loginCitizen(String username)
	{
		logger.log(Level.INFO, "loginCitizen");
		citizen = new User(username, out);
		if(!Server.citizens.contains(citizen))
			Server.citizens.add(citizen);
		else
			return false;
		if(Server.freeMedics.isEmpty())
			Server.freeCitizens.add(citizen);
		else
		{
			Server.citizen_medic.add(new Pair(citizen, Server.freeMedics.remove()));
			int index = Server.citizen_medic.indexOf(new Pair(citizen, null));
			if (index > -1)
			{
				User medic = Server.citizen_medic.get(index).getR();
				PrintWriter out = medic.getPrintWriter();
				out.println(Protocol.START_CHAT + username);
			}
		}
		return true;
	}
	public boolean loginMedic()
	{
		logger.log(Level.INFO, "loginMedic");
		String username = "";
		int max = 0;
		for(User medic : Server.medics)
		{
			int name = Integer.parseInt(medic.getUsername());
			if(name > max)
				max = name;
		}
		username = "" + (max + 1);
		medic = new User(username, out);
		if(!Server.medics.contains(medic))
			Server.medics.add(medic);
		else
			return false;
		if(Server.freeCitizens.isEmpty())
			Server.freeMedics.add(medic);
		else
		{
			Server.citizen_medic.add(new Pair(Server.freeCitizens.remove(), medic));
			shouldConnect = true;
		}
		return true;
	}

	public void sendMessageToMedic(String message)
	{
		logger.log(Level.INFO, "sendMessageToMedic");
		System.out.println(message);
		int index = Server.citizen_medic.indexOf(new Pair(citizen, null));
		if (index > -1)
		{
			User medic = Server.citizen_medic.get(index).getR();
			PrintWriter out = medic.getPrintWriter();
			out.println(Protocol.MESSAGE_TO_MEDIC + citizen.getUsername() + Protocol.SEPARATOR + message);
		}
	}
	public void sendMessageToCitizen(String message)
	{
		logger.log(Level.INFO, "sendMessageToCitizen");
		System.out.println(message);
		int index = Server.citizen_medic.indexOf(new Pair(null, medic));
		if (index > -1)
		{
			User citizen = Server.citizen_medic.get(index).getL();
			PrintWriter out = citizen.getPrintWriter();
			out.println(Protocol.MESSAGE_TO_CITIZEN + message);
		}
	}
	public void logoutCitizen()
	{
		logger.log(Level.INFO, "logoutCitizen");
		if(!Server.freeCitizens.remove(citizen))
		{
			int index = Server.citizen_medic.indexOf(new Pair(citizen, null));
			if (index > -1)
			{
				User medic = Server.citizen_medic.get(index).getR();
				Server.freeMedics.add(medic);
				Server.citizen_medic.remove(index);
				medic.getPrintWriter().println(Protocol.END_CHAT);
			}
		}
		Server.citizens.remove(citizen);
	}
	public void logoutMedic()
	{
		logger.log(Level.INFO, "logoutMedic");
		if(!Server.freeMedics.remove(medic))
		{
			int index = Server.citizen_medic.indexOf(new Pair(null, medic));
			if (index > -1)
			{
				User user = Server.citizen_medic.get(index).getL();
				Server.freeCitizens.add(user);
				Server.citizen_medic.remove(index);
			}
		}
		Server.medics.remove(medic);
	}
	public void endChat()
	{
		logger.log(Level.INFO, "endChat");
		int index = Server.citizen_medic.indexOf(new Pair(null, medic));
		if (index > -1)
		{
			User citizen = Server.citizen_medic.get(index).getL();
			PrintWriter out = citizen.getPrintWriter();
			out.println(Protocol.END_CHAT);
		}
	}
	@Override
	public void run()
	{
		logger.log(Level.INFO, "run");
		String request;
		try
		{
			while (!Protocol.END.equals(request = in.readLine()))
			{
				try
				{
					if (request == null)
					{
						request = "";
					}
					if (request.startsWith(Protocol.LOGIN_CITIZEN))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						boolean status = false;
						if (params.length == 2)
							status = loginCitizen(params[1]);
						if (!status)
							out.println(ErrorMessage.INVALID_LOGIN);
						else
							out.println(Protocol.OK);
					}
					if (request.startsWith(Protocol.LOGIN_MEDIC))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						boolean status = false;
						if (params.length == 1)
							status = loginMedic();
						if (!status)
							out.println(ErrorMessage.INVALID_LOGIN);
						else
							out.println(Protocol.OK);
						
						if(shouldConnect)
						{
							shouldConnect = false;
							int index = Server.citizen_medic.indexOf(new Pair(null, medic));
							if (index > -1)
							{
								User user = Server.citizen_medic.get(index).getL();
								out.println(Protocol.START_CHAT + user.getUsername());
							}
						}
					}
					if (request.startsWith(Protocol.MESSAGE_TO_MEDIC))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						if (params.length == 3)
							sendMessageToMedic(params[2]);
						else
							out.println(ErrorMessage.INVALID_REQUEST);
					}
					if (request.startsWith(Protocol.MESSAGE_TO_CITIZEN))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						if (params.length == 2)
							sendMessageToCitizen(params[1]);
						else
							out.println(ErrorMessage.INVALID_REQUEST);
					}
					if (request.startsWith(Protocol.SEND_N))
						Server.n = Integer.parseInt(request.split(Protocol.SEPARATOR)[1]);
					if (request.startsWith(Protocol.END_CHAT))
						endChat();
				}
				catch (Exception e)
				{
					logger.log(Level.INFO, e.toString(), e);
				}
			}
			
			if(in != null) in.close();
			if(out != null) out.close();
			if(sock != null) sock.close();
		}
		catch (IOException e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
		finally
		{
			if(medic != null) logoutMedic();
			if(citizen != null) logoutCitizen();
		}

	}

}
