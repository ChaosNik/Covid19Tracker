package org.unibl.etf.mdp.chat.view;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.mdp.chat.model.Stat;
import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.ChatThread;
import org.unibl.etf.mdp.chat.service.RMIService;
import org.unibl.etf.mdp.chat.service.UserFilesListThread;
import org.unibl.etf.mdp.chat.service.UserListRequestThread;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.google.gson.Gson;

public class ActivityViewController implements Initializable
{
	public static String REST_STAT = "http://localhost:8080/REST/api/rest/stat/";
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
	    REST_STAT = p.getProperty("REST_STAT");
	}
	@FXML
	private AnchorPane ap;
	
	@FXML
	private Button btnCancel;
	
	public static class TableDataType
	{
		private final SimpleStringProperty  arrival;
		private final SimpleStringProperty  departure;
		private final SimpleStringProperty  total;
		TableDataType(String arrival, String departure, String total)
		{
			this.arrival = new SimpleStringProperty(arrival);
			this.departure = new SimpleStringProperty(departure);
			this.total = new SimpleStringProperty(total);
		}
		public void setArrival(String arrival)
		{
			this.arrival.set(arrival);
		}
		public void setDeparture(String departure)
		{
			this.departure.set(departure);
		}
		public void setTotal(String total)
		{
			this.total.set(total);
		}
		public String getArrival()
		{
			return arrival.get();
		}
		public String getDeparture()
		{
			return departure.get();
		}
		public String getTotal()
		{
			return total.get();
		}
	}
	@FXML
	private TableView<TableDataType> table;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		//ap.getScene().getWindow().setOnHiding( event -> {System.out.println("Closing Stage");} );
		
		Main.myStage.setTitle("Activity");
		
        Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_STAT + Main.token);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();
		
		Gson gson = new Gson();
		String json = response.readEntity(String.class);
		Stat[] stats = gson.fromJson(json, Stat[].class);
		Arrays.sort
		(
			stats,
			new Comparator<Stat>()
			{
			    @Override
			    public int compare(Stat first, Stat second)
			    {  
			        return first.datetime.compareTo(second.datetime); 
			    }
		    }
		);
		
		boolean isFirst = true;
		Stat first = null;
		ArrayList<TableDataType> items = new ArrayList<TableDataType>();
		for(Stat stat : stats)
		{
			if(isFirst) first = stat;
			else
			{
//				/*TEST*/System.out.print(" ");
				Calendar a = Calendar.getInstance();
				Calendar b = Calendar.getInstance();
				
				a.setTime(first.getDateTime());
				b.setTime(stat.getDateTime());
				
				String firstString = "";
				firstString += a.get(Calendar.YEAR) + ".";
				firstString += (a.get(Calendar.MONTH) + 1) + ".";
				firstString += a.get(Calendar.DAY_OF_MONTH) + ". ";
				firstString += a.get(Calendar.HOUR) + ":";
				firstString += a.get(Calendar.MINUTE) + ":";
				firstString += a.get(Calendar.SECOND);
				String secondString = "";
				secondString += b.get(Calendar.YEAR) + ".";
				secondString += (b.get(Calendar.MONTH) + 1) + ".";
				secondString += b.get(Calendar.DAY_OF_MONTH) + ". ";
				secondString += b.get(Calendar.HOUR) + ":";
				secondString += b.get(Calendar.MINUTE) + ":";
				secondString += b.get(Calendar.SECOND);
				
				int diff = (int)(Math.abs(stat.getDateTime().getTime() - first.getDateTime().getTime()) / 1000);
				int seconds = diff % 60;
				int minutes = (diff / 60) % 60;
				int hours = (diff / 3600);
				items.add
				(
					new TableDataType
					(
						firstString,
						secondString,
						"" + hours + ":" + minutes + ":" + seconds
					)
				);
			}
			isFirst = !isFirst;
//			/*TEST*/System.out.println(stat.getDateTime().toString());
		}
		
		table.setEditable(true);
		TableColumn colArrival = new TableColumn("Arrival");
		colArrival.setCellValueFactory(
                new PropertyValueFactory<TableDataType, String>("arrival"));
        TableColumn colDeparture = new TableColumn("Departure");
        colDeparture.setCellValueFactory(
                new PropertyValueFactory<TableDataType, String>("departure"));
        TableColumn colTotal = new TableColumn("Total");
        colTotal.setCellValueFactory(
                new PropertyValueFactory<TableDataType, String>("total"));
		table.setItems(FXCollections.observableArrayList(items));
		table.getColumns().addAll(colArrival, colDeparture, colTotal);
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
}
