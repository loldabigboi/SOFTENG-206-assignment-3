package ass3.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GameselectController implements Initializable{
	
	@FXML
	private ComboBox vedioone;
	
	@FXML
	private ComboBox vediotwo;
	
	@FXML
	private ComboBox vediothree;
	
	@FXML
	private Label one;
	
	@FXML
	private Label two;
	
	@FXML
	private Label three;

	@FXML
	private MediaView v1;
	
	@FXML
	private MediaView v2;
	
	@FXML
	private MediaView v3;
	
	@FXML
	private Button check;
	
	@FXML
	private ImageView i1;
	
	@FXML
	private ImageView i2;
	
	@FXML
	private ImageView i3;
	public ImageManager imageManager;
	private static File dir = new File(System.getProperty("user.dir"));
	private String answer1;
	private String answer2;
	private String answer3;
	
	public void ClickCheck(ActionEvent e) throws IOException{
		//vedioone.getValue().toString();
		this.one.setText("Correct Answer: " + answer1);
		this.two.setText("Correct Answer: " + answer2);
		this.three.setText("Correct Answer: " + answer3);
		//add correction images
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String cn = "ls " + "creations "+ "| shuf -n 1 | cut -d'.' -f1";
		ProcessBuilder p = new ProcessBuilder("bash", "-c", cn);
		try {
			Process pr = p.start();
			String l;
			vedioone.getItems().addAll("a", "b", "c", "d");
			vediotwo.getItems().addAll("a", "b", "c", "d");
			vediothree.getItems().addAll("a", "b", "c", "d");
		} catch (IOException e1) {
		}
		
		MediaPlayer currPlayer = v1.getMediaPlayer();
		if (currPlayer != null) {
			currPlayer.stop();
		}
		String listCommand = "ls " + "creations "+ "| shuf -n 1 | cut -d'.' -f1";
		String listCommand1 = "ls " + "creations "+ "| shuf -z -n 1 | cut -d'.' -f1";
		String listCommand2 = "ls " + "creations "+ "| shuf -n 1 | cut -d'.' -f1";
		ProcessBuilder list = new ProcessBuilder("bash", "-c", listCommand);
		ProcessBuilder list2 = new ProcessBuilder("bash", "-c", listCommand1);
		ProcessBuilder list3 = new ProcessBuilder("bash", "-c", listCommand2);
		Process listprocess;
		Process listprocess2;
		Process listprocess3;
		try {
			listprocess = list.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(listprocess.getInputStream()));
			String line = stdout.readLine();
			answer1 = line;
			File file = new File(dir + "/creations/", line + ".mp4");
			Media media = new Media(file.toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(media);
	
			v1.setMediaPlayer(mediaPlayer);
			mediaPlayer.play();
			
			
			listprocess2 = list2.start();
			BufferedReader stdout2 = new BufferedReader(new InputStreamReader(listprocess2.getInputStream()));
			String line2 = stdout2.readLine();
			File file2 = new File(dir + "/creations/", line2 + ".mp4");
			answer2 = line2;
			Media media2 = new Media(file2.toURI().toString());
			MediaPlayer mediaPlayer2 = new MediaPlayer(media2);
	
			v2.setMediaPlayer(mediaPlayer2);
			mediaPlayer2.play();
			
			listprocess3 = list3.start();
			BufferedReader stdout3 = new BufferedReader(new InputStreamReader(listprocess3.getInputStream()));
			String line3 = stdout3.readLine();
			answer3 = line3;
			File file3 = new File(dir + "/creations/", line3 + ".mp4");
			Media media3 = new Media(file3.toURI().toString());
			MediaPlayer mediaPlayer3 = new MediaPlayer(media3);
	
			v3.setMediaPlayer(mediaPlayer3);
			mediaPlayer3.play();
		} catch (IOException e) {
		}
	}


}
