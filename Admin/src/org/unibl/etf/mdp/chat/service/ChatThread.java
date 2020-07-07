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

public class ChatThread extends Thread
{

	//public ListView<User> listUsers;
	//public HashMap<User, ArrayList<String>> messages;
	//public static Boolean sendScreen = false;
	public ArrayList<String> database = new ArrayList<>();
	private ListView<String> messages = new ListView<>();

	//public ChatThread(ListView<User> listUsers, HashMap<User, ArrayList<String>> messages) {
	public ChatThread()
	{
	}
	public void setListView(ListView<String> messages)
	{
		this.messages = messages;
	}

	/*public void updateUserList(String response)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				ArrayList<User> users = ChatService.getUsers(response);
				listUsers.setItems(FXCollections.observableArrayList(users));
			}
		});
	}*/

	public void updateMessageList(String response)
	{
		String[] params = response.split("#");
		String message = "CITIZEN#" + params[1] + params[2];

		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				database.add(params[1] + ": " + params[2]);
				messages.setItems(FXCollections.observableArrayList(database));
			}
		});
	}
	public void addMessage(String message)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				database.add(message);
				messages.setItems(FXCollections.observableArrayList(database));
			}
		});
	}
	//upitno
	public void startChat(String response)
	{
		String[] params = response.split("#");
		Main.token = Long.parseLong(params[1]);
		
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Main.overviewWindow.openMessagingWindow();
			}
		}).start();
	}
	public void endChat()
	{
		Main.messageWindow.endChat();
	}
	public void generatePotentialContacts()
	{
		Main.overviewWindow.generatePotentialContacts();
	}
	
	/*public void upadateSendScreen(boolean startstop)
	{
		sendScreen = startstop;
		//MessageViewController.sendScreen();
	}*/

	@Override
	public void run()
	{
		while (Main.isRunning)
		{
			String response;
			try
			{
				response = Main.service.in.readLine();
				if (response == null)
				{
					response = "";
				}
				/*if (response.startsWith("LIST#"))
				{
					String[] params = response.split("#");
					if (params.length == 2)
					{
						updateUserList(params[1]);
					}
				}*/
				if (response.startsWith("MESSAGE_TO_MEDIC#"))
				{
					updateMessageList(response);
				}
				if (response.startsWith("START_CHAT#"))
				{
					startChat(response);
				}
				if(response.startsWith("END_CHAT"))
				{
					endChat();
				}
				if(response.startsWith("GENERATE_POTENTIAL_CONTACTS"))
				{
					generatePotentialContacts();
				}
				
				/*if(response.startsWith("START_SCREEN"))
				{
					upadateSendScreen(true);
				}
				
				if(response.startsWith("STOP_SCREEN"))
				{
					upadateSendScreen(false);
				}*/
			}
			catch (IOException e)
			{
				//e.printStackTrace();
			}

		}
	}
}