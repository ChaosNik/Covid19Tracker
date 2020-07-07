package org.unibl.etf.mdp.chat.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

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
import org.unibl.etf.mdp.chat.service.LoginService;
import org.unibl.etf.mdp.chat.service.RMIService;
import org.unibl.etf.mdp.chat.service.RemindingThread;
import org.unibl.etf.mdp.chat.service.UserFilesListThread;
import org.unibl.etf.mdp.chat.service.UserListRequestThread;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import net.etfbl.api.Service;
import net.etfbl.api.ServiceServiceLocator;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CitizensViewController implements Initializable
{
	private static String SERIALIZATION = "serialization";
	private static String REST_LOCATION = "http://localhost:8080/REST/api/rest/location/";
	private static String REST_TOKEN = "http://localhost:8080/REST/api/rest/location/";
	private static String REST_CONTACT = "http://localhost:8080/REST/api/rest/contact/";
	private static String REST_INFECTED = "http://localhost:8080/REST/api/rest/infected/";
	private static String REST_POTENTIALY_INFECTED = "http://localhost:8080/REST/api/rest/is-infected-potentialy/";
	private static String REST_NOT_INFECTED = "http://localhost:8080/REST/api/rest/infected-not/";
	private static String MULTICAST_ADDRESS = "224.0.0.3";
	private static int MULTICAST_PORT = 8888;
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
	    SERIALIZATION = p.getProperty("SERIALIZATION");
	    REST_LOCATION = p.getProperty("REST_LOCATION");
	    REST_TOKEN = p.getProperty("REST_TOKEN");
	    REST_CONTACT = p.getProperty("REST_CONTACT");
	    REST_INFECTED = p.getProperty("REST_INFECTED");
	    REST_POTENTIALY_INFECTED = p.getProperty("REST_POTENTIALY_INFECTED");
	    REST_NOT_INFECTED = p.getProperty("REST_NOT_INFECTED");
	    MULTICAST_ADDRESS = p.getProperty("MULTICAST_ADDRESS");
	    MULTICAST_PORT = Integer.parseInt(p.getProperty("MULTICAST_PORT"));
	    MAP_X = Integer.parseInt(p.getProperty("MAP_X"));
	    MAP_Y = Integer.parseInt(p.getProperty("MAP_Y"));
	}
	
	@FXML
	AnchorPane anchorPane;
	
	@FXML
	Label txtAdminMessage;
	
	@FXML
	ListView<String> listUsers;
	
	@FXML
	ListView<String> listPotentialyInfected;
	
	@FXML
	AnchorPane gridMap;
	
	@FXML
	TextArea txtSearch;
	
	@FXML
	Button btnBlock;
	
	@FXML
	Button btnNotInfected;
	
	@FXML
	Button btnPotentialyInfected;
	
	@FXML
	Button btnInfected;
	
	public Button[][] matrix;
	private String selectedUser = "1";
	private MulticastSocket clientSocket;
	
	private Gson gson = new Gson();
	
	void logout()
	{
		/*Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_LOGOUT + Main.username);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);*/
	}
	
	@FXML
	void onBlock(ActionEvent event)
	{
		try
		{
			ServiceServiceLocator locator = new ServiceServiceLocator();
			Service service = locator.getService();
			service.blockUser(Main.token);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	@FXML
	void onInfected(ActionEvent event)
	{
		String header = "CITIZEN#" + Main.token + "#";
		multicast(header);
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_INFECTED + Main.token);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.post(Entity.json(""));
	}
	@FXML
	void onPotentialyInfected(ActionEvent event)
	{
		String header = "CITIZEN#" + Main.token + "#";
		multicast(header);
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_POTENTIALY_INFECTED + Main.token);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.post(Entity.json(""));
	}
	@FXML
	void onNotInfected(ActionEvent event)
	{
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_NOT_INFECTED + Main.token);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.post(Entity.json(""));
	}
	private void multicast(String header)
	{
		String notificationText = header + "POTENCIJALNO STE ZARAZENI";
		InetAddress address = null;
		try
		{
			//address = InetAddress.getByName(prop.getProperty("IP_ADDRESS_MULTICAST"));
			address = InetAddress.getByName(MULTICAST_ADDRESS);
		}
		catch (UnknownHostException e1)
		{
			//LOGGER.log(Level.SEVERE, e1.getMessage());
			return;
		}

		try (DatagramSocket socket = new DatagramSocket())
		{

			//Integer PORT = Integer.parseInt(prop.getProperty("PORT_MULTICAST"));
			Integer PORT = MULTICAST_PORT;
			DatagramPacket packet = new DatagramPacket(notificationText.getBytes(), notificationText.getBytes().length,
					address, PORT);
			socket.send(packet);
			txtAdminMessage.setText("");
		}
		catch (IOException e)
		{
			//LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	private void adminMessage(String message)
	{
		txtAdminMessage.setText("HITNA OBAVJEŠTENJA: " + message);
	}
	private void adminMessage()
	{

		InetAddress groupAddress;
		try
		{
			//groupAddress = InetAddress.getByName(prop.getProperty("IP_ADDRESS_MULTICAST"));
			groupAddress = InetAddress.getByName(MULTICAST_ADDRESS);

			clientSocket = new MulticastSocket(MULTICAST_PORT);
			clientSocket.joinGroup(groupAddress);
		}
		catch (UnknownHostException e2)
		{
			//LOGGER.log(Level.SEVERE, e2.getMessage());
		}
		catch (NumberFormatException | IOException e1)
		{
			//LOGGER.log(Level.SEVERE, e1.getMessage());
		}

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while (true)
				{
					getAdminMessage();
				}
			}
		}).start();
	}
	
	private void getAdminMessage()
	{

		byte[] buf = new byte[256];

		DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
		try
		{
			clientSocket.receive(msgPacket);
			String notification = new String(msgPacket.getData(), msgPacket.getOffset(), msgPacket.getLength());
			boolean show = false;
			String[] items = notification.split("#");
			if (notification != null && notification.startsWith("MEDIC"))
			{
				serializeNotification(notification);
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						txtAdminMessage.setText(items[1]);
					}
				});
			}
		}
		catch (IOException e)
		{
			//LOGGER.log(Level.SEVERE, e.getMessage());
		}

	}
	
	private void serializeNotification(String notification)
	{
		try
		{
			File root = new File(SERIALIZATION);
			if(!root.exists()) root.mkdir();
			File dir = new File(SERIALIZATION + File.separator + "MEDIC");
			if(!dir.exists()) dir.mkdir();
			//logger.log(Level.INFO, "File " + fileName + " sent from user " + userFrom + " to user " + userTo);
		
			int numberOfSerializedFiles = dir.listFiles().length;
			File file = new File(SERIALIZATION + File.separator + "MEDIC" + File.separator + numberOfSerializedFiles);
			if(!file.exists()) file.createNewFile();
			int serializationNumber = numberOfSerializedFiles % 4;
			if (serializationNumber == 0)
					Files.write(file.toPath(), notification.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
			else if (serializationNumber == 1)
				Files.write(file.toPath(), gson.toJson(notification).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
			else if (serializationNumber == 2)
			{
					Kryo kryo = new Kryo();
					Output output = new Output(new FileOutputStream(file.getPath()));
					kryo.writeClassAndObject(output, notification);
				    output.close();
			}
			else if (serializationNumber == 3)
			{
				FileOutputStream fos1 = new FileOutputStream(file.getPath());
				java.beans.XMLEncoder xe1 = new java.beans.XMLEncoder(fos1);
				xe1.writeObject(notification);
				xe1.close();
			}
		}
		catch (Exception e)
		{
			//logger.log(Level.INFO, e.toString(), e);
		}
	}
	
	@FXML
	void onExit(ActionEvent event)
	{
		logout();
		Main.isRunning = false;
		Main.service.disconnect();
		Main.reminder.disconnect();
		Platform.exit();
        System.exit(0);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		Main.myStage.setTitle("Medic");
		Main.citizensStage = Main.myStage;
		Main.overviewWindow = this;
		
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_TOKEN);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();
		
		Gson gson = new Gson();
		String json = response.readEntity(String.class);
		long[] tokens = gson.fromJson(json, long[].class);
		
		ObservableList<String> users = FXCollections.observableArrayList();
		for(long token : tokens)
			users.add("" + token);
	    FilteredList<String> filteredList = new FilteredList<>(users, x -> true);
	    listUsers.setItems(filteredList);
	    
		generatePotentialContacts();
		generateMap("" + tokens[0]);
		//listUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
		listUsers.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			//public void changed(ObservableValue arg0, String user, String arg1)
			public void handle(MouseEvent event)
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						String arg1 = listUsers.getSelectionModel().getSelectedItem();
						if (!selectedUser.equals(arg1))
						{
							selectedUser = arg1;
							try
							{
								Main.token = Long.parseLong(selectedUser);
								generateMap(selectedUser);
							}
							catch(Exception e)
							{
							}
						}
					}
				});
			}
		});
		txtSearch.textProperty().addListener
		(
			obs->
			{
		        String filter = txtSearch.getText(); 
		        if(filter == null || filter.length() == 0)
		        	filteredList.setPredicate(s -> true);
		        else
		        	filteredList.setPredicate(s -> s.toLowerCase().contains(filter.toLowerCase()));
			}
		);
		Main.chatThread = new ChatThread();
		Main.chatThread.start();
		
		Main.remindingThread = new RemindingThread();
		Main.remindingThread.start();

		/*UserListRequestThread userListThread = new UserListRequestThread();
		userListThread.start();
		
		UserFilesListThread userFilesThread = new UserFilesListThread(listFiles, listUsers);
		userFilesThread.start();*/
		adminMessage("");
		adminMessage();
	}
	public void generatePotentialContacts()
	{	
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_CONTACT);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();

		Gson gson = new Gson();
		String json = response.readEntity(String.class);
		TokenInfo[] locations = gson.fromJson(json, TokenInfo[].class);
		
		listPotentialyInfected.getItems().clear();
		for(TokenInfo info : locations)
			listPotentialyInfected.getItems().add("" + info.token + " (" + info.x + "," + info.y + ") " + info.datetime.toGMTString());
	}
	public void generateMap(String token)
	{
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_LOCATION + Main.token);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();
		
		Gson gson = new Gson();
		String json = response.readEntity(String.class);
		TokenInfo[] locations = gson.fromJson(json, TokenInfo[].class);
		
		this.gridMap.getChildren().clear();
		matrix = new Button[MAP_X][MAP_Y]; 
		for(int y = 0; y < MAP_X; y++)
		{
	        for(int x = 0; x < MAP_Y; x++)
	        {
	            matrix[x][y] = new Button(); 
	           
	            for(TokenInfo item : locations)
	            	if(item.x == x && item.y == y)
	            		matrix[x][y].setBackground(new Background(new BackgroundFill(Color.RED,null,null)));	
	            matrix[x][y].setMaxHeight(450.0 / MAP_X - 2);
	            matrix[x][y].setMaxWidth(450.0 / MAP_Y - 2);
	            matrix[x][y].setMinHeight(450.0 / MAP_X - 2);
	            matrix[x][y].setMinWidth(450.0 / MAP_Y - 2);
	            matrix[x][y].setPrefHeight(450.0 / MAP_X - 2);
	            matrix[x][y].setPrefWidth(450.0 / MAP_Y - 2);
	            matrix[x][y].setLayoutX(0.0 + x * (430.0 / MAP_X));
	            matrix[x][y].setLayoutY(0.0 + y * (430.0 / MAP_Y));
	            this.gridMap.getChildren().add(matrix[x][y]);
	        }
		}
	}
	public void openMessagingWindow()
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
				Parent root = FXMLLoader.load(getClass().getResource("MessageView.fxml"));
				Main.myStage.hide();
				Scene scene = new Scene(root);
				Main.myStage.setResizable(false);
				//stage.initStyle(StageStyle.UNDECORATED);
				Main.myStage.setTitle("Medic ");
				Main.myStage.setScene(scene);
				Main.myStage.show();
				scene.getWindow().centerOnScreen();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
