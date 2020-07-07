package org.unibl.etf.mdp.chat.view;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.axis.encoding.Base64;
import org.unibl.etf.mdp.chat.model.TokenInfo;
import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.ChatThread;
import org.unibl.etf.mdp.chat.service.LoginService;
import org.unibl.etf.mdp.chat.service.RMIService;
import org.unibl.etf.mdp.chat.service.ScreenSendingThread;
import org.unibl.etf.mdp.chat.service.UserFilesListThread;
import org.unibl.etf.mdp.chat.service.UserListRequestThread;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.service.directions.DirectionsService;
import com.sun.research.ws.wadl.Resources;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import net.etfbl.api.Service;
import net.etfbl.api.ServiceServiceLocator;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MessageViewController implements Initializable
{
	public Button[][] matrix;
	
	private static String SERIALIZATION = "serialization";
	private static String REST_LOCATION = "http://localhost:8080/REST/api/rest/location/";
	private static String REST_CONTACT = "http://localhost:8080/REST/api/rest/contact/";
	private static String RESOURCES = "resources";
	private static String TOKEN = "token.txt";
	private static String PASSWORD = "password.txt";
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
	    REST_CONTACT = p.getProperty("REST_CONTACT");
	    RESOURCES = p.getProperty("RESOURCES");
	    TOKEN = p.getProperty("TOKEN");
	    PASSWORD = p.getProperty("PASSWORD");
	    MAP_X = Integer.parseInt(p.getProperty("MAP_X"));
	    MAP_Y = Integer.parseInt(p.getProperty("MAP_Y"));
	    MULTICAST_ADDRESS = p.getProperty("MULTICAST_ADDRESS");
	    MULTICAST_PORT = Integer.parseInt(p.getProperty("MULTICAST_PORT"));
	}
	
	private FileChooser fc;
	
	private MulticastSocket clientSocket;
	
	private Gson gson = new Gson();
	
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private MenuBar menuBar;
	
	@FXML
	private Label lblUsage;
	
	@FXML
	private TextArea txtMessage;
	
	@FXML
	private Label txtAdminMessage;

	@FXML
	private ListView<String> listMessages;

	@FXML
	private ListView<String> listUsers;
	
	@FXML
	private ListView<String> listFiles;

	@FXML
	private Button btnSend;
	
	@FXML
	private Button btnBroadcast;
	
	@FXML
	private Button btnSendFile1;
	@FXML
	private Button btnSendFile2;
	@FXML
	private Button btnSendFile3;
	@FXML
	private Button btnSendFile4;
	@FXML
	private Button btnSendFile5;
	@FXML
	private Button btnSendFile6;
	
	@FXML
	private Button btnDownload;
	
	@FXML
	private Button btnRiskMap;
	
	@FXML
    private GridPane gridMap;
	
	@FXML
	private TextArea txtX;
	
	@FXML
	private TextArea txtY;
	
	@FXML
	private Button btnLocate;
	
	
	//List<Button> buttonlist = new ArrayList<>();

	// cuvamo parove korisnika i svih poruka koje smo od njih dobili
	private ArrayList<String> messages = new ArrayList<>();
	
	void adminMessage(String message)
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
		
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_CONTACT + Main.token);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();

		Gson gson = new Gson();
		String json = response.readEntity(String.class);
		TokenInfo[] contacts = gson.fromJson(json, TokenInfo[].class);
		
		byte[] buf = new byte[256];

		DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
		try
		{
			clientSocket.receive(msgPacket);
			String notification = new String(msgPacket.getData(), msgPacket.getOffset(), msgPacket.getLength());
			boolean show = false;
			String[] items = notification.split("#");
			if(notification.startsWith(("CITIZEN")))
			{
				if(Long.parseLong(items[1]) == Main.token)
				{
					btnRiskMap.setDisable(false);
					serializeNotification(notification);
					Platform.runLater(new Runnable()
					{
						@Override
						public void run()
						{
							txtAdminMessage.setText("ZARAZENI STE!");
						}
					});
				}
				else
				{
					for(TokenInfo contact : contacts)
						if(contact.token == Long.parseLong(items[1]))
							show = true;
					if (show)
					{
						btnRiskMap.setDisable(false);
						serializeNotification(notification);
						Platform.runLater(new Runnable()
						{
							@Override
							public void run()
							{
								txtAdminMessage.setText("IMALI STE KONTAKT SA INFICIRANOM OSOBOM!");
							}
						});
					}
				}
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
			File dir = new File(SERIALIZATION + File.separator + Main.username);
			if(!dir.exists()) dir.mkdir();
			//logger.log(Level.INFO, "File " + fileName + " sent from user " + userFrom + " to user " + userTo);
		
			int numberOfSerializedFiles = dir.listFiles().length;
			File file = new File(SERIALIZATION + File.separator + Main.username + File.separator + numberOfSerializedFiles);
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
			e.printStackTrace();
		}
	}
	
	void sendFile(int option)
	{
		fc = new FileChooser();
		File file = fc.showOpenDialog(Main.myStage);
		if(file != null)
		{
			try
			{
				byte[] data = Files.readAllBytes(file.toPath());
				RMIService.save
				(
					Main.token,
					"" + option + ".pdf",
					data
				);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	void onSendFile1(ActionEvent event)
	{
		sendFile(1);
		btnSendFile2.setDisable(false);
		btnSendFile1.setDisable(true);
	}
	@FXML
	void onSendFile2(ActionEvent event)
	{
		sendFile(2);
		btnSendFile3.setDisable(false);
		btnSendFile2.setDisable(true);
	}
	@FXML
	void onSendFile3(ActionEvent event)
	{
		sendFile(3);
		btnSendFile4.setDisable(false);
		btnSendFile3.setDisable(true);
	}
	@FXML
	void onSendFile4(ActionEvent event)
	{
		sendFile(4);
		btnSendFile5.setDisable(false);
		btnSendFile4.setDisable(true);
	}
	@FXML
	void onSendFile5(ActionEvent event)
	{
		sendFile(5);
		btnSendFile6.setDisable(false);
		btnSendFile5.setDisable(true);
	}
	@FXML
	void onSendFile6(ActionEvent event)
	{
		sendFile(6);
		btnSendFile6.setDisable(true);
	}
	
	@FXML
	void onDownload(ActionEvent event)
	{
//		fc = new FileChooser();
//		File file = fc.showSaveDialog(Main.myStage);
//		if(file != null)
//		{
//			try
//			{
//				byte[] data =
//						RMIService.download
//						(
//							listFiles.getSelectionModel().getSelectedItem(),
//							listUsers.getSelectionModel().getSelectedItem().getUsername(),
//							Main.username
//						);
//				Files.write(file.toPath(), data, StandardOpenOption.CREATE);
//			}
//			catch(IOException e)
//			{
//				e.printStackTrace();
//			}
//		}
	}
	
	@FXML
	void onMap(ActionEvent event)
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("ShowMapView.fxml"));
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
	
	@FXML
	void onRiskMap(ActionEvent event)
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("RiskMapView.fxml"));
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
	
	@FXML
	void onUnregister(ActionEvent event)
	{
		try
		{
			ServiceServiceLocator locator = new ServiceServiceLocator();
			Service service = locator.getService();
			service.unregister(Main.token);
			
			Files.delete(Paths.get(RESOURCES + File.separator + PASSWORD));
			Files.delete(Paths.get(RESOURCES + File.separator + TOKEN));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		exit();
	}
	
	@FXML
	void onUsage(ActionEvent event)
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("ActivityView.fxml"));
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
	
	@FXML
	void onChangePassword(ActionEvent event)
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("ChangePasswordView.fxml"));
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
	
	@FXML
	void onExit(ActionEvent event)
	{
		exit();
	}
	public void exit()
	{
		Main.isRunning = false;
		Main.service.disconnect();
		Platform.exit();
        System.exit(0);
	}
	
	@FXML
	void onSend(ActionEvent event)
	{
		String message = txtMessage.getText();
		ChatService.sendMessage("" + Main.token, message);
		Main.chatThread.addMessage(Main.token + ": " + message);
		txtMessage.clear();
	}
	
	@FXML
	void onBroadcast(ActionEvent event)
	{
		/*String message = txtMessage.getText();
		ChatService.sendBroadcast(message);
		
		for(User user : listUsers.getItems())
		{
			if (allMessages.containsKey(user))
			{
				allMessages.get(user).add(message);
			}
			else
			{
				ArrayList<String> msgs = new ArrayList<>();
				msgs.add(message);
				allMessages.put(user, msgs);
			}
			txtMessage.clear();
		}*/
	}
	
	@FXML
	void onLocate(ActionEvent event)
	{
		int xx = Integer.parseInt(txtX.getText());
		int yy = Integer.parseInt(txtY.getText());
		matrix[xx][yy].setBackground(new Background(new BackgroundFill(Color.RED,null,null)));
		
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_LOCATION + Main.token + "/" + xx + "/" + yy);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.post(Entity.json(""));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		Main.myStage.setTitle("Citizen");
		generateMap();
		Main.messageWindow = this;
		btnSendFile2.setDisable(true);
		btnSendFile3.setDisable(true);
		btnSendFile4.setDisable(true);
		btnSendFile5.setDisable(true);
		btnSendFile6.setDisable(true);
		
		btnRiskMap.setDisable(true);
		Platform.runLater
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					listMessages.setItems(FXCollections.observableArrayList(messages));
				}
			}
		);

		Main.chatThread = new ChatThread();
		Main.chatThread.setListView(listMessages);
		Main.chatThread.start();

		/*UserListRequestThread userListThread = new UserListRequestThread();
		userListThread.start();
		
		UserFilesListThread userFilesThread = new UserFilesListThread(listFiles, listUsers);
		userFilesThread.start();
		
		ScreenSendingThread screenSendingThread = new ScreenSendingThread();
		screenSendingThread.start();*/
		
		adminMessage("");
		adminMessage();
		
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				ArrayList<String> myMessages = new ArrayList<String>();
				for(String message : messages)
					if("CITIZEN".startsWith(message.split("#")[0]))
						myMessages.add(message.split("#")[1]);
					else
						myMessages.add("\t\t\t" + message.split("#")[1]);
				if (messages != null)
				{
					listMessages.setItems(FXCollections.observableArrayList(myMessages));
				}
				ChatService.startChat("" + Main.token);
			}
		});
	}
	public void generateMap()
	{
		matrix = new Button[MAP_X][MAP_Y]; 
		for(int y = 0; y < MAP_X; y++)
		{
	        for(int x = 0; x < MAP_Y; x++)
	        {
	            matrix[x][y] = new Button(); 
	            
	            /*matrix[x][y].setOnAction(new EventHandler<ActionEvent>() {

	                @Override
	                public void handle(ActionEvent event) {
	                    System.out.println("Random Binary Matrix (JavaFX)");
	                }
	            });*/
	            final int xx = x;
	            final int yy = y;
	            matrix[x][y].setOnAction
	            (
	            	new EventHandler<ActionEvent>()
		            {
	            		@Override
	            		public void handle(ActionEvent e)
	            		{
            				matrix[xx][yy].setBackground(new Background(new BackgroundFill(Color.RED,null,null)));
            				
            				Client client = ClientBuilder.newClient();
            				WebTarget resource = client.target(REST_LOCATION + Main.token + "/" + xx + "/" + yy);
            				Builder request = resource.request();
            				request.accept(MediaType.APPLICATION_JSON);
            				Response response = request.post(Entity.json(""));
	            	    }
		            }
	            );
	            matrix[x][y].setMaxHeight(450.0 / MAP_X - 2);
	            matrix[x][y].setMaxWidth(450.0 / MAP_Y - 2);
	            matrix[x][y].setMinHeight(450.0 / MAP_X - 2);
	            matrix[x][y].setMinWidth(450.0 / MAP_Y - 2);
	            matrix[x][y].setPrefHeight(450.0 / MAP_X - 2);
	            matrix[x][y].setPrefWidth(450.0 / MAP_Y - 2);
	            matrix[x][y].setLayoutX(290.0 + x * (430.0 / MAP_X));
	            matrix[x][y].setLayoutY(60.0 + y * (430.0 / MAP_Y));
	            this.anchorPane.getChildren().add(matrix[x][y]);
	        }
		}        
	}
	
	public static void sendScreen()
	{
		/*Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					BufferedImage originalImage = new Robot()
							.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
					int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
					BufferedImage resizedImage = new BufferedImage(1000, 700, type);
					Graphics2D g = resizedImage.createGraphics();
					g.drawImage(originalImage, 0, 0, 1000, 700, null);
					g.dispose();
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ImageIO.write(resizedImage, "jpg", bos);
					byte[] data = bos.toByteArray();
					Base64 codec = new Base64();
					String encodedData = codec.encode(data);
					ChatService.sendScreen(encodedData);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});*/
	}
}
