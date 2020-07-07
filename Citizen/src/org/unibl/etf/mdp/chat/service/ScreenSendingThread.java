package org.unibl.etf.mdp.chat.service;

import org.unibl.etf.mdp.chat.view.Main;
import org.unibl.etf.mdp.chat.view.MessageViewController;

public class ScreenSendingThread extends Thread
{
	@Override
	public void run()
	{
		while (Main.isRunning)
		{
			/*if(ChatThread.sendScreen)
				MessageViewController.sendScreen();
			try
			{
				sleep(2000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}*/
		}
	}
}
