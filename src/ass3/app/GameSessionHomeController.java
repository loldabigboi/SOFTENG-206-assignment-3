package ass3.app;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class GameSessionHomeController implements Initializable{

	@FXML
	private RadioButton manual;
	
	@FXML
	private RadioButton select;
	
	@FXML
	private Label title;
	
	@FXML
	private Button generate;
	
	private ToggleGroup fav;
	
	public void ClickButton(ActionEvent e) throws IOException {
		if(this.fav.getSelectedToggle().equals(this.manual)) {
			Parent root = FXMLLoader.load(getClass().getResource("Gamemanual.fxml"));
			Scene s = new Scene(root);
			Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
			window.setScene(s);
			window.show();
		}
		
		if(this.fav.getSelectedToggle().equals(this.select)) {
			Parent root = FXMLLoader.load(getClass().getResource("Gameselect.fxml"));
			Scene s = new Scene(root);
			Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
			window.setScene(s);
			window.show();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fav = new ToggleGroup();
		this.manual.setToggleGroup(fav);
		this.select.setToggleGroup(fav);
		
	}
	

	
	
}
