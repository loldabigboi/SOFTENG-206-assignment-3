package ass3.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class MainPage extends Application{
	//Inside the creation lists
	class Xcell extends ListCell<String>{
		HBox hbox = new HBox();
		Label label = new Label("(empty)");
		Pane pane = new Pane();
		Button delete = new Button("Delete");
		Button play = new Button("Play");
		String lastItem;

		public Xcell() {
			super();
			hbox.setSpacing(5.0);
			hbox.getChildren().addAll(label, pane, play,delete);
			HBox.setHgrow(pane, Priority.ALWAYS);
			delete.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					delete(lastItem);
				}
			});

			play.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

					play(lastItem);

				}
			});
		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			setText(null);  // No text in label of super class
			if (empty) {
				lastItem = null;
				setGraphic(null);
			} else {
				lastItem = item;
				label.setText(item!=null ? item : "<null>");
				setGraphic(hbox);
			}
		}

	}

	private AnchorPane pane = new AnchorPane();
	private TextField _wikisearch;
	private TextField _creationsearch;
	private Button _wikibutton;
	private Button _creationbutton;
	private Label _wikilable;
	private Label _creationlable;
	private String _txt = null;
	private static File dir = new File(System.getProperty("user.dir"));
	private ListView<String> l = new ListView<String>();
	private List<String> f = new ArrayList<String>();
	private MediaPlayer MP = null;
	private Scene s = new Scene(pane, 830, 350);
	private Label t = new Label();
	private Button mute = new Button("Mute");
	private Button pause = new Button("Pause");
	private Button forward = new Button(">>");
	private Button backward = new Button("<<");

	public MainPage(){
		_wikibutton = new Button("Search");
		_creationbutton = new Button("Search");
		_wikilable = new Label("Enter key word:");
		_creationlable = new Label("Search creation:");
		_wikisearch = new TextField();
		_creationsearch = new TextField();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		dir.mkdir();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(12);

		grid.add(_wikilable, 0, 0);
		grid.add(_creationlable, 0, 1);
		grid.add(_wikisearch, 1, 0);
		grid.add(_creationsearch, 1, 1);
		grid.add(_wikibutton, 2, 0);
		grid.add(_creationbutton, 2, 1);

		AnchorPane.setTopAnchor(grid, 14.0);
		AnchorPane.setLeftAnchor(grid, 28.0);
		AnchorPane.setRightAnchor(grid, 34.0);

		//Get the list of creation
		String listCommand = "ls " + dir + "/ | grep mp4 | sort | cut -d'.' -f1";
		ProcessBuilder list = new ProcessBuilder("bash", "-c", listCommand);
		Process listprocess = list.start();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(listprocess.getInputStream()));
		String line;
		List<String> fileList = new ArrayList<String>();
		while ((line = stdout.readLine()) != null) {
			fileList.add(line);
		}
		f = fileList;

		ListView<String> lvList = new ListView<String>();
		lvList.getItems().addAll(fileList);
		lvList.setPrefHeight(250.00);
		lvList.setPrefWidth(385.00);
		lvList.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		lvList.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		l = lvList;

		lvList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				return new Xcell();
			}
		});

		StackPane p = new StackPane();
		p.getChildren().add(lvList);
		AnchorPane.setTopAnchor(p, 90.0);
		AnchorPane.setLeftAnchor(p, 20.0);
		AnchorPane.setBottomAnchor(p, 5.0);

		p.setPrefHeight(300.00);
		p.setPrefWidth(400.00);
		p.setLayoutX(23.00);
		p.setLayoutY(147.00);

		pane.getChildren().addAll(grid, p);
		s.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(s);

		primaryStage.sizeToScene();
		primaryStage.show();
		primaryStage.setTitle("Home -- Welcome");


		//When click wiki search button
		_wikibutton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				String inputkey = _wikisearch.getText();
				//check invalid input
				if(!((inputkey.matches(".*[A-Za-z].*"))|| (inputkey.matches("[0-9]*")))) {
					Alert alert1 = new Alert(AlertType.WARNING);
					alert1.setTitle("Warning Dialog");
					alert1.setHeaderText("The input is invalid");
					alert1.setContentText("Please retype!");
					alert1.showAndWait();
				}
				else {
					_txt = _wikisearch.getText();
					Wiki wiki = new Wiki(_txt);

					wiki.setOnSucceeded((e) -> {
						WikiCreationMenu.createWindow(primaryStage, wiki.getValue());
					});

					try { 
						Thread w = new Thread(wiki);
						w.start();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});


		//EventAction of creation search button
		_creationbutton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				String input = _creationsearch.getText();
				lvList.getSelectionModel().clearSelection();
				lvList.getItems().removeAll(fileList);
				String command = "ls " + dir + "/ | grep .mp4 | grep " + input  + " | sort | cut -d'.' -f1";
				ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
				Process process;
				try {
					process = pb.start();
					BufferedReader number = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String li;
					List<String> fl = new ArrayList<String>();
					while ((li = number.readLine()) != null) {
						fl.add(li);
					}
					f = fl;
					lvList.getItems().addAll(fl);	
				} catch (IOException e) {
				} 
			}
		});	 
	}

	// When click delete button of each creation.
	private void delete(String item){
		//pop out the confirmation 
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmate Deletion");
		alert.setHeaderText("Are you sure to delete " + item + ".mp4 :(");
		alert.setTitle("Wanna Delete ???");

		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			String deleteCommand = "rm -f " + dir + "/" + item + ".mp4";
			ProcessBuilder dcProcess = new ProcessBuilder("bash", "-c", deleteCommand);
			try {
				dcProcess.start();
				//Display the original board again
				l.getSelectionModel().clearSelection();
				l.getItems().removeAll(f);
				String listcommand = "ls " + dir + "/ | grep mp4 | sort | cut -d'.' -f1";
				ProcessBuilder p = new ProcessBuilder("bash", "-c", listcommand);
				Process listProcess = p.start();
				BufferedReader stout = new BufferedReader(new InputStreamReader(listProcess.getInputStream()));
				String li;
				List<String> filelist = new ArrayList<String>();
				while ((li = stout.readLine()) != null) {
					filelist.add(li);
				}
				f = filelist;
				l.getItems().addAll(filelist);			
			} catch (Exception e) {
			} 
		}
	}

	// When click play button of each creation.
	private void play(String item){
		if(MP != null) {
			MP.dispose();
		}
		BorderPane bp = new BorderPane();
		bp.setPrefHeight(350.0);

//		Button mute = new Button("Mute");
//		Button pause = new Button("Pause");
//		Button forward = new Button(">>");
//		Button backward = new Button("<<");
		mute.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		pause.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		forward.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		backward.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		mute.setMinWidth(Control.USE_PREF_SIZE);
		pause.setMinWidth(Control.USE_PREF_SIZE);
		forward.setMinWidth(Control.USE_PREF_SIZE);
		backward.setMinWidth(Control.USE_PREF_SIZE);
		Label t = new Label();

		File f = new File(dir, item + ".mp4");
		Media m = new Media(f.toURI().toString());
		MediaPlayer mp = new MediaPlayer(m);
		MP = mp;
		MP.setAutoPlay(true);

		MediaView mv = new MediaView(MP);

		mute.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				MP.setMute(MP.isMute());
			}
		});

		pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if (MP.getStatus() == Status.PLAYING) {
					MP.pause();
					pause.setText("Play");
				} else {
					MP.play();
					pause.setText("Pause");
				}
			}
		});

		forward.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				MP.seek(MP.getCurrentTime().add(Duration.seconds(2)));
			}
		});

		backward.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				MP.seek(MP.getCurrentTime().add(Duration.seconds(-2)));
			}
		});

		//Listener of progress bar and time.
		ProgressBar pb = new ProgressBar(0.1);
		MP.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				pb.setProgress(MP.getCurrentTime().toSeconds()/m.getDuration().toSeconds());
				String time = "";
				time += String.format("%02d", (int)newValue.toMinutes());
				time += ":";
				time += String.format("%02d", (int)newValue.toSeconds());
				t.setText(time);
			}

		});

		HBox vb = new HBox();
		vb.setSpacing(2);
		vb.setPadding(new Insets(0, 20, 10, 20)); 

		vb.getChildren().addAll(pause, backward, forward, pb, t, mute);

		bp.setCenter(mv);
		bp.setBottom(vb);
		AnchorPane.setTopAnchor(bp, 0.0);
		AnchorPane.setLeftAnchor(bp, 430.0);
		AnchorPane.setRightAnchor(bp, 20.0);
		AnchorPane.setBottomAnchor(bp, 10.0);
		pane.getChildren().add(bp);

		MP.play();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
