package org.unibl.etf.mdp.chat.service;

import java.util.ArrayList;

import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.view.Main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class UserFilesListThread extends Thread
{
	public ListView<String> listFiles;
	public ListView<User> listUsers;
	public UserFilesListThread(ListView<String> listFiles, ListView<User> listUsers)
	{
		this.listFiles = listFiles;
		this.listUsers = listUsers;
	}
	@Override
	public void run()
	{
		/*(while (Main.isRunning)
		{
			Platform.runLater(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						listFiles.setItems
						(
							FXCollections.observableArrayList
							(
								RMIService.view
								(
									listUsers.getSelectionModel().getSelectedItem().getUsername(),
									Main.username
								)
							)
						);
					}
					catch(Exception e)
					{
						//System.out.println("Ceka da se izabere korisnik!");
					}
				}
			});
			try
			{
				sleep(2000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}*/
	}
}
