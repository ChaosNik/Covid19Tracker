package org.unibl.etf.mdp.chat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.view.Main;
import org.unibl.etf.mdp.chat.view.MessageViewController;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class RemindingThread extends Thread
{
	@Override
	public void run()
	{
		while (Main.isRunning)
		{
			String response;
			try
			{
				response = Main.reminder.in.readLine();
				if (response == null)
					response = "";
				if (response.startsWith("REFRESH"))
					Platform.runLater(new Runnable()
					{
						@Override
						public void run()
						{
							Main.overviewWindow.generatePotentialContacts();
						}
					});
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	}
}