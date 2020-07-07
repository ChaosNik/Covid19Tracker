package org.unibl.etf.mdp.chat.view;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import org.unibl.etf.mdp.chat.service.BaseNetworkService;
import org.unibl.etf.mdp.chat.service.ChatThread;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application
{
	private static String RESOURCES = "resources";
	private static String TOKEN = "token.txt";
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
	    RESOURCES = p.getProperty("RESOURCES");
	    TOKEN = p.getProperty("TOKEN");
	}
	
	public static long token = 0;
	public static BaseNetworkService service;
	public static String username;
	public static String name;
	public static String surname;
	public static String password;
	public static String uuid;
	public static boolean isRunning = true;
	public static Stage myStage = null;
	public static ChatThread chatThread = null;
	
	public static MessageViewController messageWindow = null;
	
	@Override
	public void start(Stage stage)
	{
		System.out.println("Citizen...");
		try
		{
			Main.token = Long.parseLong(Files.readAllLines(Paths.get(RESOURCES + File.separator + TOKEN)).get(0));
		}
		catch(Exception e)
		{
			Main.token = 0;
		}
		if(Main.token == 0)
			try
			{
				myStage = stage;
				Parent root = FXMLLoader.load(getClass().getResource("RegisterView.fxml"));
	
				Scene scene = new Scene(root);
				stage.setResizable(false);
				//stage.initStyle(StageStyle.UNDECORATED);
				stage.setTitle("Citizen registering ");
				stage.setScene(scene);
				stage.show();
				scene.getWindow().centerOnScreen();
				
				service = new BaseNetworkService();
				service.connect();
				
				stage.setOnCloseRequest(new EventHandler()
				{
					@Override
					public void handle(Event arg0)
					{
						arg0.consume();
						/*isRunning = false;
						service.disconnect();*/
					}
				});
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		else
			try
			{
				myStage = stage;
				Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
	
				Scene scene = new Scene(root);
				stage.setResizable(false);
				//stage.initStyle(StageStyle.UNDECORATED);
				stage.setTitle("Citizen ");
				stage.setScene(scene);
				stage.show();
				scene.getWindow().centerOnScreen();
	
				service = new BaseNetworkService();
				service.connect();
	
				stage.setOnCloseRequest(new EventHandler()
				{
					@Override
					public void handle(Event arg0)
					{
						arg0.consume();
						/*isRunning = false;
						service.disconnect();*/
					}
				});
	
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
