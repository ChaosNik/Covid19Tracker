package org.unibl.etf.mdp.chat.view;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.mdp.chat.service.BaseNetworkService;
import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.ChatThread;
import org.unibl.etf.mdp.chat.service.LoginService;
import org.unibl.etf.mdp.chat.service.ReminderNetworkService;
import org.unibl.etf.mdp.chat.service.RemindingThread;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Main extends Application
{

	public static BaseNetworkService service;
	public static ReminderNetworkService reminder;
	public static long token = 1;
	public static boolean isRunning = true;
	public static Stage myStage = null;
	public static ChatThread chatThread;
	public static RemindingThread remindingThread;
	
	public static CitizensViewController overviewWindow = null;
	public static MessageViewController messageWindow = null;
	
	public static Stage citizensStage = null;
	
	private static int N = 10;
	private static int P = 60;
	private static int K = 5;
	
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
	    N = Integer.parseInt(p.getProperty("N"));
	    P = Integer.parseInt(p.getProperty("P"));
	    K = Integer.parseInt(p.getProperty("K"));
	}
	
	@Override
	public void start(Stage stage)
	{
		System.out.println("Admin...");
		service = new BaseNetworkService();
		service.connect();
		reminder = new ReminderNetworkService();
		reminder.connect();
		if(LoginService.login())
		{
			try
			{
				ChatService.sendN(N);
				
				myStage = stage;
				Parent root = FXMLLoader.load(getClass().getResource("CitizensView.fxml"));
				
				Scene scene = new Scene(root);
				stage.setResizable(false);
				//stage.initStyle(StageStyle.UNDECORATED);
				stage.setTitle("Medic");
				stage.setScene(scene);
				stage.show();
				scene.getWindow().centerOnScreen();
				
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
		else
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Prijava na sistem nije uspjesna");
			alert.setContentText("Unesite drugo korisnicko ime");
			alert.showAndWait();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
