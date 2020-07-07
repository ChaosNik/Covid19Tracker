package org.unibl.etf.mdp.chat.view;

import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.LoginService;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.etfbl.api.Service;
import net.etfbl.api.ServiceServiceLocator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class LoginViewController
{
	private static String REST_PASSWORD = "http://localhost:8080/REST/api/rest/password/";
	private static String RESOURCES = "resources";
	private static String PASSWORD = "password.txt";
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
	    REST_PASSWORD = p.getProperty("REST_PASSWORD");
	    RESOURCES = p.getProperty("RESOURCES");
	    PASSWORD = p.getProperty("PASSWORD");
	}
	
	@FXML
	private PasswordField txtPassword;

	@FXML
	private Button btnLogin;
	
	@FXML
	private Button btnExit;

	@FXML
	public void login(ActionEvent event)
	{
		String enteredPassword = txtPassword.getText();
		
		boolean correctPassword = false;
		boolean passwordExists = true;
		
		try
		{
			Main.password = Files.readAllLines(Paths.get(RESOURCES + File.separator + PASSWORD)).get(0);
			if(Main.password.equals(enteredPassword))
				correctPassword = true;
		}
		catch(Exception e)
		{
			passwordExists = false;
		}
		if(!passwordExists)
			try
			{
				Files.createFile(Paths.get(RESOURCES + File.separator + PASSWORD));
				Files.write(Paths.get(RESOURCES + File.separator + PASSWORD), enteredPassword.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
				Main.password = enteredPassword;
				
				/*Client client = ClientBuilder.newClient();
				WebTarget resource = client.target(REST_PASSWORD + Main.username + "/" + Main.password);
				Builder request = resource.request();
				request.accept(MediaType.APPLICATION_JSON);
				Response response = request.post(Entity.json(""));
				
				if(("SUCCESS").equals(response.readEntity(String.class)))*/
					correctPassword = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		boolean canLogin = false;
		try
		{
			ServiceServiceLocator locator = new ServiceServiceLocator();
			Service service = locator.getService();
			canLogin = service.login(Main.token);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(correctPassword && canLogin)
		{
			LoginService.login("" + Main.token);
			
			final Node source = (Node) event.getSource();
			final Stage stage = (Stage) source.getScene().getWindow();
			try
			{
				Parent root = FXMLLoader.load(getClass().getResource("MessageView.fxml"));
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
				scene.getWindow().centerOnScreen();

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
			alert.setContentText("Ponovo unesite lozinku");
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
