package org.unibl.etf.mdp.chat.service;

import java.io.IOException;

import org.unibl.etf.mdp.chat.view.Main;

public class LoginService
{

	public static boolean login(String username)
	{
		try
		{
			Main.service.out.println("LOGIN_CITIZEN#" + username);
			String response = Main.service.in.readLine();
			return "OK".equals(response);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
