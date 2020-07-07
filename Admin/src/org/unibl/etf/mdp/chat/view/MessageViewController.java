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
import java.net.DatagramSocket;
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
import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.ChatThread;
import org.unibl.etf.mdp.chat.service.LoginService;
import org.unibl.etf.mdp.chat.service.RMIService;
import org.unibl.etf.mdp.chat.service.UserFilesListThread;
import org.unibl.etf.mdp.chat.service.UserListRequestThread;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;
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
	private Button btnMulticast;
	
	@FXML
	private Button btnRecieveFile1;
	@FXML
	private Button btnRecieveFile2;
	@FXML
	private Button btnRecieveFile3;
	@FXML
	private Button btnRecieveFile4;
	@FXML
	private Button btnRecieveFile5;
	@FXML
	private Button btnRecieveFile6;
	
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
	public static ArrayList<String> messages = new ArrayList<>();
	
	@FXML
	void onMulticast(ActionEvent event)
	{
		String notificationText = "MEDIC#" + txtMessage.getText();
		txtMessage.clear();
		
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
			e.printStackTrace();
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
	void onRecieveFile1(ActionEvent event)
	{
		download(Main.token, 1);
	}
	@FXML
	void onRecieveFile2(ActionEvent event)
	{
		download(Main.token, 2);
	}
	@FXML
	void onRecieveFile3(ActionEvent event)
	{
		download(Main.token, 3);
	}
	@FXML
	void onRecieveFile4(ActionEvent event)
	{
		download(Main.token, 4);
	}
	@FXML
	void onRecieveFile5(ActionEvent event)
	{
		download(Main.token, 5);
	}
	@FXML
	void onRecieveFile6(ActionEvent event)
	{
		download(Main.token, 6);
	}
	
	void download(long token, int option)
	{
		fc = new FileChooser();
		File file = fc.showSaveDialog(Main.myStage);
		if(file != null)
		{
			try
			{
				byte[] data =
						RMIService.download
						(
							token,
							"" + option + ".pdf"
						);
				Files.write(file.toPath(), data, StandardOpenOption.CREATE);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	@FXML
	void onLeaveChat(ActionEvent event)
	{
		ChatService.endChat();
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
				Parent root = FXMLLoader.load(getClass().getResource("CitizensView.fxml"));
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
	public void endChat()
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
				Parent root = FXMLLoader.load(getClass().getResource("CitizensView.fxml"));
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
	void onExit(ActionEvent event)
	{
		Main.isRunning = false;
		Main.service.disconnect();
		Main.reminder.disconnect();
		Platform.exit();
        System.exit(0);
	}
	
	@FXML
	void onSend(ActionEvent event)
	{
		String message = txtMessage.getText();
		ChatService.sendMessage(message);
		Main.chatThread.addMessage("MEDIC: " + message);
		txtMessage.clear();
	}
	
	@FXML
	void onLocate(ActionEvent event)
	{
		int x = Integer.parseInt(txtX.getText());
		int y = Integer.parseInt(txtY.getText());
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_LOCATION + Main.token + "/" + x + "/" + y);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.post(Entity.json(""));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		generateMap("" + Main.token);
		Main.myStage.setTitle("MEDIC");
		Main.messageWindow = this;
		Platform.runLater
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					ArrayList<String> myMessages = new ArrayList<String>();
					for(String message : messages)
						if("MEDIC".startsWith(message.split("#")[0]))
							myMessages.add(message.split("#")[1]);
						else
						{
							myMessages.add("\t\t\t" + message.split("#")[1] + "   " + message.split("#")[2]);
						}
					if (messages != null)
					{
						listMessages.setItems(FXCollections.observableArrayList(myMessages));
					}
				}
			}
		);

		Main.chatThread.setListView(listMessages);
		//ChatThread t = new ChatThread(messages);
		//t.start();

		/*UserListRequestThread userListThread = new UserListRequestThread();
		userListThread.start();
		
		UserFilesListThread userFilesThread = new UserFilesListThread(listFiles, listUsers);
		userFilesThread.start();
		
		ScreenSendingThread screenSendingThread = new ScreenSendingThread();
		screenSendingThread.start();*/
		
		adminMessage("");
		adminMessage();
	}
	public void generateMap(String token)
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
            				WebTarget resource = client.target(REST_LOCATION + token + "/" + xx + "/" + yy);
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
