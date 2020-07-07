package org.unibl.etf.mdp.chat.view;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

import org.unibl.etf.mdp.chat.model.TokenInfo;
import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.ChatThread;
import org.unibl.etf.mdp.chat.service.RMIService;
import org.unibl.etf.mdp.chat.service.UserFilesListThread;
import org.unibl.etf.mdp.chat.service.UserListRequestThread;

import com.google.gson.Gson;

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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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

public class ShowMapViewController implements Initializable
{
	private static String REST_LOCATION = "http://localhost:8080/REST/api/rest/location/";
	private static int MAP_X = 30;
	private static int MAP_Y = 30;
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
	    REST_LOCATION = p.getProperty("REST_LOCATION");
	    MAP_X = Integer.parseInt(p.getProperty("MAP_X"));
	    MAP_Y = Integer.parseInt(p.getProperty("MAP_Y"));
	}
	
	@FXML
	private AnchorPane anchorPane;
	
	@FXML
	private Button btnCancel;
	
	private Button[][] matrix;
	
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
		generateMap();
	}
	
	public void generateMap()
	{
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_LOCATION + Main.token);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();
		
		Gson gson = new Gson();
		String json = response.readEntity(String.class);
		System.out.println(json);
		TokenInfo[] locations = gson.fromJson(json, TokenInfo[].class);
		
		matrix = new Button[MAP_X][MAP_Y]; 
		for(int y = 0; y < MAP_X; y++)
		{
	        for(int x = 0; x < MAP_Y; x++)
	        {
	            matrix[x][y] = new Button(); 
	            matrix[x][y].setDisable(true);
	           
	            for(TokenInfo item : locations)
	            	if(item.x == x && item.y == y)
	            		matrix[x][y].setBackground(new Background(new BackgroundFill(Color.RED,null,null)));	
	            matrix[x][y].setMaxHeight(450.0 / MAP_X - 2);
	            matrix[x][y].setMaxWidth(450.0 / MAP_Y - 2);
	            matrix[x][y].setMinHeight(450.0 / MAP_X - 2);
	            matrix[x][y].setMinWidth(450.0 / MAP_Y - 2);
	            matrix[x][y].setPrefHeight(450.0 / MAP_X - 2);
	            matrix[x][y].setPrefWidth(450.0 / MAP_Y - 2);
	            matrix[x][y].setLayoutX(15.0 + x * (430.0 / MAP_X));
	            matrix[x][y].setLayoutY(15.0 + y * (430.0 / MAP_Y));
	            this.anchorPane.getChildren().add(matrix[x][y]);
	        }
		}        
	}
}
