package org.unibl.etf.mdp.chat.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.etfbl.api.Service;
import net.etfbl.api.ServiceServiceLocator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import org.unibl.etf.mdp.chat.service.BaseNetworkService;

public class RegisterViewController
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
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtSurname;
	
	@FXML
	private TextField txtUUID;

	@FXML
	private Button btnRegister;
	
	@FXML
	private Button btnExit;

	@FXML
	public void register(ActionEvent event)
	{
		
		Main.name = txtName.getText();
		Main.surname = txtSurname.getText();
		Main.uuid = txtUUID.getText();
		
		try
		{
			ServiceServiceLocator locator = new ServiceServiceLocator();
			Service service = locator.getService();
			long token = service.register(Main.name, Main.surname, "", Long.parseLong(Main.uuid), false);
			if(token != 0)
			{
				try
				{
					Files.createFile(Paths.get(RESOURCES + File.separator + TOKEN));
					Files.write(Paths.get(RESOURCES + File.separator + TOKEN), ("" + token).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				Main.username = "" + token;
				Main.token = token;
				
				Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
				
				Scene scene = new Scene(root);
				Main.myStage.setResizable(false);
				//stage.initStyle(StageStyle.UNDECORATED);
				Main.myStage.setTitle("Citizen ");
				Main.myStage.setScene(scene);
				Main.myStage.show();
				scene.getWindow().centerOnScreen();
	
				Main.service = new BaseNetworkService();
				Main.service.connect();
	
				Main.myStage.setOnCloseRequest(new EventHandler()
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
			else
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Prijava na sistem nije uspjesna");
				alert.setContentText("Unesite drugo korisnicko ime");
				alert.showAndWait();
			}
		}
		catch(Exception e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Prijava na sistem nije uspjesna");
			alert.setContentText("Unesite drugo korisnicko ime");
			alert.showAndWait();
		}
	}
	
	@FXML
	void onExit(ActionEvent event)
	{
		Platform.exit();
        System.exit(0);
	}
}
