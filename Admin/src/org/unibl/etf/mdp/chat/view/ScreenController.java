package org.unibl.etf.mdp.chat.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.ChatThread;

public class ScreenController implements Initializable {

	@FXML
	private ImageView imgScreen;
	private Stage secondaryStage;
	private AnchorPane screenScene;
	
	@FXML Button btnCancel;

	public static ImageView imageScreen;

	public void showAddStage()
	{
		/*try
		{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(LoginViewController.class.getResource("../view/ScreenView.fxml"));
			screenScene = loader.load();
			Scene scene = new Scene(screenScene);
			secondaryStage = new Stage();
			secondaryStage.setResizable(false);
			secondaryStage.setTitle("Nadgledanje radnika");
			secondaryStage.setScene(scene);
			secondaryStage.initModality(Modality.APPLICATION_MODAL);
			secondaryStage.setOnCloseRequest(new EventHandler()
			{
				@Override
				public void handle(Event arg0)
				{
					secondaryStage.close();
				}
			});
			secondaryStage.showAndWait();
		}
		catch (IOException io)
		{
			io.printStackTrace();
		}*/
	}

	public static void updateImage()
	{

		/*Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				Image image = ChatThread.image;
				if (imageScreen != null)
					imageScreen.setImage(image);
			}
		});*/
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		//imgScreen.setImage(ChatThread.image);
		//imageScreen = imgScreen;
	}
	
	public void goBack()
	{
		/*try
		{
			ChatService.sendStopScreen(Main.selectedUser);
			
			Parent root = FXMLLoader.load(getClass().getResource("MessageView.fxml"));
			Scene scene = new Scene(root);
			Main.myStage.setScene(scene);
			Main.myStage.show();
			scene.getWindow().centerOnScreen();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/
	}
}
