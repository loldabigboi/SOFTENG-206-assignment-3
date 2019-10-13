package ass3.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import ass3.app.Wiki;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class GamemanualController implements Initializable{
	
	@FXML
	private TextField vedioone;
	
	@FXML
	private TextField vediotwo;
	
	@FXML
	private TextField vediothree;

	@FXML
	private MediaView v1;
	
	@FXML
	private MediaView v2;
	
	@FXML
	private MediaView v3;
	
	@FXML
	private Button check;
	
	@FXML
	private Label one;
	
	@FXML
	private Label two;
	
	@FXML
	private Label three;
	
	private static File dir = new File(System.getProperty("user.dir"));
	
	private String answer1;
	private String answer2;
	private String answer3;
	
	public void ClickCheck(ActionEvent e) throws IOException{
		if((vediotwo.getText() != null) && (vedioone.getText().toString()).equals(answer1)) {
			this.one.setText("Correct Answer: " + answer1+ "\n Your answer is correct");
		}else {
			this.one.setText("Correct Answer: " + answer1+ "\n Your answer is wrong");
		}
		
		if((vediotwo.getText() != null)&&(vediotwo.getText().toString()).equals(answer2)){
			this.two.setText("Correct Answer: " + answer2+ "\n Your answer is correct");
		}else {
			this.two.setText("Correct Answer: " + answer2+ "\n Your answer is wrong");
		}
		if((vediothree.getText() != null) && (vediothree.getText().toString()).equals(answer3)){
			this.three.setText("Correct Answer: " + answer3+ "\n Your answer is correct");
		}else {
			this.three.setText("Correct Answer: " + answer3+ "\n Your answer is wrong");
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		String listCommand = "ls " + "creations "+ "| cut -d'.' -f1";
		ProcessBuilder list = new ProcessBuilder("bash", "-c", listCommand);
		Process listprocess;
		try {
			listprocess = list.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(listprocess.getInputStream()));
			List<String> l = new ArrayList<>();
			String line;
			while ((line = stdout.readLine())!= null) {
			l.add(line);
			}
			
				
			
			Random random = new Random();
			String name = l.get(random.nextInt(l.size()));
			File file = new File(dir + "/creations/", name + ".mp4");
			BufferedReader reader = new BufferedReader(new FileReader(new File(dir + "/search_terms/", name + ".txt")));
			answer1 = reader.readLine();
			Media media = new Media(file.toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			v1.setMediaPlayer(mediaPlayer);
			mediaPlayer.play();
			l.remove(name);
			
			String name2 = l.get(random.nextInt(l.size()));
			File file2 = new File(dir + "/creations/", name2 + ".mp4");
			BufferedReader reader2 = new BufferedReader(new FileReader(new File(dir + "/search_terms/", name2 + ".txt")));
			answer2 = reader2.readLine();
			Media media2 = new Media(file2.toURI().toString());
			MediaPlayer mediaPlayer2 = new MediaPlayer(media2);
			v2.setMediaPlayer(mediaPlayer2);
			mediaPlayer2.play();
			l.remove(name2);
			
			String name3 = l.get(random.nextInt(l.size()));
			File file3 = new File(dir + "/creations/", name3 + ".mp4");
			BufferedReader reader3 = new BufferedReader(new FileReader(new File(dir + "/search_terms/", name3 + ".txt")));
			answer3 = reader3.readLine();
			Media media3 = new Media(file3.toURI().toString());
			MediaPlayer mediaPlayer3 = new MediaPlayer(media3);
	
			v3.setMediaPlayer(mediaPlayer3);
			mediaPlayer3.play();
			l.remove(name3);
		} catch (IOException e) {
		}
	}
}
