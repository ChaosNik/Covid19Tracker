package org.unibl.etf.mdp.chat.service;

import java.io.IOException;

import org.unibl.etf.mdp.chat.view.Main;

public class LoginService
{

	public static boolean login()
	{
		try
		{
			Main.service.out.println("LOGIN_MEDIC#");
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
