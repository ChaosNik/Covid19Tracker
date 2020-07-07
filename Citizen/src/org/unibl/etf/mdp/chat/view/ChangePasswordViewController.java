package org.unibl.etf.mdp.chat.view;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.ChatThread;
import org.unibl.etf.mdp.chat.service.RMIService;
import org.unibl.etf.mdp.chat.service.UserFilesListThread;
import org.unibl.etf.mdp.chat.service.UserListRequestThread;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChangePasswordViewController implements Initializable
{
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
	    RESOURCES = p.getProperty("RESOURCES");
	    PASSWORD = p.getProperty("PASSWORD");
	}
	@FXML
	private AnchorPane ap;
	/*Stage stage = (Stage) ap.getScene().getWindow();
	{
		stage.setOnCloseRequest(
			new EventHandler<WindowEvent>()
			{
			    @Override
			    public void handle(WindowEvent e)
			    {
			    	goBack();
			    }
			}
		);
	}*/
	
	@FXML
	private TextField txtOldPassword;
	
	@FXML
	private PasswordField txtNewPassword;
	
	@FXML
	private PasswordField txtNewPasswordAgain;
	
	@FXML
	private Button btnChangePassword;
	
	@FXML
	private Button btnCancel;

	@FXML
	public void changePassword(ActionEvent event)
	{
		if(!txtNewPassword.getText().equals(txtNewPasswordAgain.getText()))
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Upozorenje");
			alert.setHeaderText("Upozorenje");
			alert.setContentText("Nove lozinke treba da se podudaraju!");
			alert.showAndWait();
		}
		else
		{
			if(txtOldPassword.getText().equals(Main.password))
			{
				try
				{
					//Files.createFile(Paths.get(RESOURCES + File.separator + PASSWORD));
					Files.write(Paths.get(RESOURCES + File.separator + PASSWORD), txtNewPassword.getText().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
					Main.password = txtNewPassword.getText();
					
					/*Client client = ClientBuilder.newClient();
					WebTarget resource = client.target(REST_PASSWORD + Main.username + "/" + Main.password);
					Builder request = resource.request();
					request.accept(MediaType.APPLICATION_JSON);
					Response response = request.post(Entity.json(""));
					
					if(("SUCCESS").equals(response.readEntity(String.class)))*/
						//correctPassword = true;
					
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Uspjeh");
					alert.setHeaderText("Uspjeh");
					alert.setContentText("Uspjesno izmijenjena lozinka!");
					alert.showAndWait();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Upozorenje");
				alert.setHeaderText("Upozorenje");
				alert.setContentText("Unijeli ste netacnu lozinku!");
				alert.showAndWait();
			}
			
		}
		
	}
	
	@FXML
	public void cancel(ActionEvent event)
	{
		goBack();
	}
	
	public void goBack()
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("MessageView.fxml"));
			Scene scene = new Scene(root);
			Main.myStage.setScene(scene);
			Main.myStage.show();
			scene.getWindow().centerOnScreen();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		Main.myStage.setTitle("Employee " + Main.username);
	}
}
