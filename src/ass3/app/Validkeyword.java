package ass3.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class Validkeyword implements Runnable {	
	private List<String> _fulltext = new ArrayList<String>();
	private Label _text;
	private Label _cname;
	private TextField _creationname;
	private Label _number;
	private TextField _numInput;
	private String _num;
	private String _name;
	public Validkeyword(List<String> fulltext) {
		_fulltext = fulltext;
		_text = new Label("Here are the results: ");
		_cname= new Label("Enter the creation name: ");
		_creationname = new TextField();
		_number = new Label("Enter the number of lines: ");
		_numInput = new TextField();
		_num = null;
		_name = null;
	}


	@Override
	public void run() {	
		alert();
	}
	
	//The alert that show the full text and other buttons
	public void alert() {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Create new file");
		dialog.setHeaderText("Let's create your new file :>");

		ListView<String> lvList = new ListView<String>();
		lvList.getItems().addAll(_fulltext);
		lvList.setPrefHeight(300.0);
		lvList.setMinSize(900.0, Control.USE_PREF_SIZE);
		lvList.setMaxSize(900.0, Control.USE_PREF_SIZE);
		// Set the button types.
		ButtonType submitkw = new ButtonType("Apply", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(submitkw, ButtonType.CANCEL);
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.setMaxWidth(Double.MAX_VALUE);
		grid.add(_text, 0, 0);
		grid.add(lvList, 0, 1);
		grid.add(_number, 0, 2);
		grid.add(_numInput, 1, 2);

		grid.add(_cname, 0, 3);
		grid.add(_creationname, 1, 3);


		// Enable/Disable button depending on whether a number was entered.
		Node searchButton = dialog.getDialogPane().lookupButton(submitkw);
		searchButton.setDisable(true);

		// Do some validation
		_creationname.textProperty().addListener((observable, oldValue, newValue) -> {
			searchButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the number field by default.
		Platform.runLater(() -> _numInput.requestFocus());

		// Convert the result to a pair when the number button is clicked.

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == submitkw) {
				return new Pair<>(_numInput.getText(), _creationname.getText());
			}
			return null;
		});

		//Get the user's inputs
		try {
			Optional<Pair<String, String>> result = dialog.showAndWait();
			result.ifPresent(tot -> {
				_num = tot.getKey();
				_name = tot.getValue();

				if(!(_num.matches("^[0-9]+$"))) {    	
					throw new InvalidNumberException("Bad Number");
				}
			});
		}
		catch(InvalidNumberException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText("Don't input anything except numbers");
			alert.setContentText("Please retype!");

			alert.showAndWait();
			return;
		}
		
		int numofline=0;

		try {
			numofline = Integer.parseInt(_num);
		}
		catch(Exception e) {
			return;
		}

		//check if the input of line number is avalid
		File file = new File( "new" + _name + ".txt");
		if(numofline > _fulltext.size()){
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText("The input line number is invalid! ");
			alert.setContentText("Please enter a new number!");

			alert.showAndWait();
			
			//check if the input file name is existing
			if(file.exists()) {
				Alert alert1 = new Alert(AlertType.WARNING);
				alert1.setTitle("Warning Dialog");
				alert1.setHeaderText("The creation name is existing");
				alert1.setContentText("Please rename cration!");

				alert1.showAndWait();
			}
		}

		else {
			FileWriter fw;
			try {
				fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				int i = Integer.parseInt(_num);
				int j = 0;
				while(j < i) {
					bw.write(_fulltext.get(j).toString() + "\n");
					j++;
				}

				bw.close();

				String directory = System.getProperty("user.dir");

				//Generate audio file
				String audiocommand = "espeak -f " + directory+"/" +file + " -w " +"new"+ _name + ".wav";
				ProcessBuilder ac = new ProcessBuilder("bash", "-c", audiocommand);
				Process auc = ac.start();
				auc.waitFor();

				//Generate the duration of the audio
				String durationcommand = "soxi -D " + directory+"/" + "new"+ _name+ ".wav";
				ProcessBuilder dc = new ProcessBuilder("bash", "-c", durationcommand);
				Process duc = dc.start();
				duc.waitFor();
				BufferedReader duration= new BufferedReader(new InputStreamReader(duc.getInputStream()));
				String dur = duration.readLine();

				//Generate no-sound video
				String combinecommand = "ffmpeg -f lavfi -i color=c=blue:s=320x240:d=" + dur+ " -vf \"drawtext=fontfile=lili.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" +_name +"'\" " + "fake" +_name + ".mp4";
				ProcessBuilder cc = new ProcessBuilder("bash", "-c", combinecommand);
				Process coc = cc.start();
				coc.waitFor();

				//Generate final video
				String videocommand = "ffmpeg -i " + directory+"/"+ "fake" +_name +".mp4 -i " + directory+"/"+"new"+ _name+".wav \\-c:v copy -c:a aac -strict experimental " +directory+"/"+ _name + ".mp4";
				ProcessBuilder vc = new ProcessBuilder("bash", "-c", videocommand);
				Process vic = vc.start();
				vic.waitFor();
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Congradulations!!!!");
				alert.setHeaderText("You created a new creation :b");
				alert.setContentText("Go to View to chech your creations!");

				alert.showAndWait();

				//Remove useless files
				String deletecommand = "rm -f " + "fake" +_name +".mp4";
				ProcessBuilder delc = new ProcessBuilder("bash", "-c", deletecommand);
				Process dec = delc.start();
				dec.waitFor();
				
				
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			catch(InvalidNumberException e) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning Dialog");
				alert.setHeaderText("Don't input anything except numbers");
				alert.setContentText("Please retype!");
				alert.showAndWait();
			}
		}
	}
}

