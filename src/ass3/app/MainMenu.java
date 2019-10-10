package ass3.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ass3.app.listeners.InvalidCharacterChangeListener;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class MainMenu extends Application{
	
	//Inside the creation lists
	class Xcell extends ListCell<String>{
		
		HBox hbox = new HBox(10);
		Label label = new Label("(empty)");
		Pane pane = new Pane();
		Button delete = new Button();
		Button play = new Button();
		String lastItem;

		public Xcell() {
			
			super();
			
			hbox.setAlignment(Pos.CENTER);
			hbox.setPadding(new Insets(5));
			
			hbox.getChildren().setAll(label, pane, play, delete);
			HBox.setHgrow(pane, Priority.ALWAYS);
			
			delete.setGraphic(new ImageView(imageManager.getImage("delete")));
			delete.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					delete(lastItem);
				}
				
			});

			play.setGraphic(new ImageView(imageManager.getImage("play")));
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
	
	public ImageManager imageManager;

	private HBox _layout = new HBox(10);
	
	private VBox _creationLayout = new VBox(8);
	
	private TextField _wikisearch;
	private TextField _creationsearch;
	private Button _wikibutton;
	private Button _creationbutton;
	private Label _wikilable;
	private Label _creationlable;
	private String _txt = null;
	
	private static File dir = new File(System.getProperty("user.dir"));
	private ListView<String> lvList = new ListView<String>();
	private List<String> fileList = new ArrayList<String>();
	
	private StackPane _mediaViewLayout = new StackPane();
	private boolean _seekingForwards = false;
	private MediaView _mediaView = new MediaView();
	private Scene s = new Scene(_layout, 900, 400);
	private Label time = new Label();
	private ProgressBar pb  = new ProgressBar();
	private Button mute = new Button();
	private Button pause = new Button();
	private Button forward = new Button();
	private Button backward = new Button();

	public MainMenu(){
		
		_wikibutton = new Button();
		_creationbutton = new Button();
		_wikilable = new Label("Wiki search:");
		_creationlable = new Label("Filter creations:");
		_wikisearch = new TextField();
		HBox.setHgrow(_wikisearch, Priority.ALWAYS);
		_creationsearch = new TextField();
		HBox.setHgrow(_creationsearch, Priority.ALWAYS);
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		loadImages();
		
		_layout.setPadding(new Insets(5));
		
		dir.mkdir();

		HBox wikiSearchLayout = new HBox(10);
		wikiSearchLayout.setAlignment(Pos.CENTER);
		
		_wikibutton.setGraphic(new ImageView(imageManager.getImage("search")));
		_wikibutton.setDisable(true);
		
		_creationbutton.setGraphic(new ImageView(imageManager.getImage("refresh")));
		
		wikiSearchLayout.getChildren().addAll(_wikilable, _wikisearch, _wikibutton);
		
		Separator horizSeparator = new Separator();
		horizSeparator.setOrientation(Orientation.HORIZONTAL);
		
		HBox creationSearchLayout = new HBox(10);
		creationSearchLayout.setAlignment(Pos.CENTER);
		creationSearchLayout.getChildren().addAll(_creationlable, _creationsearch, _creationbutton);
		
		// MEDIA VIEW
		
		_mediaViewLayout.setAlignment(Pos.BOTTOM_CENTER); // to push controls to bottom
		HBox.setHgrow(_mediaViewLayout, Priority.ALWAYS);
		_mediaViewLayout.setStyle("-fx-background-color: rgb(200,200,200)");
		_mediaViewLayout.setMouseTransparent(true);  // only until a video is played
		_mediaViewLayout.setFocusTraversable(false);  // ^^
		
		_mediaViewLayout.getChildren().add(_mediaView);
		_mediaView.fitWidthProperty().bind(_mediaViewLayout.widthProperty());
		_mediaView.fitHeightProperty().bind(_mediaViewLayout.heightProperty());

		mute.setMinWidth(Control.USE_PREF_SIZE);
		mute.setStyle("-fx-background-color: rgba(0,0,0,0)");
		mute.setGraphic(new ImageView(imageManager.getImage("mediaNotMuted")));
		
		pause.setMinWidth(Control.USE_PREF_SIZE);
		pause.setStyle("-fx-background-color: rgba(0,0,0,0)");
		pause.setGraphic(new ImageView(imageManager.getImage("mediaPlay")));
		
		forward.setMinWidth(Control.USE_PREF_SIZE);
		forward.setStyle("-fx-background-color: rgba(0,0,0,0)");
		forward.setGraphic(new ImageView(imageManager.getImage("mediaForwards")));
		
		backward.setMinWidth(Control.USE_PREF_SIZE);
		backward.setStyle("-fx-background-color: rgba(0,0,0,0)");
		backward.setGraphic(new ImageView(imageManager.getImage("mediaBackwards")));
		
		pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				MediaPlayer MP = _mediaView.getMediaPlayer();
				if (MP.getCurrentTime().equals(MP.getStopTime())) {
					play(null);  // restart media. using seek() when paused was buggy so this is a hack workaround
					updatePlaybackControls();
				} else if (MP.getStatus() == Status.PLAYING) {
					MP.pause();
				} else {
					MP.play();
				}
			}
		});

		forward.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				MediaPlayer MP = _mediaView.getMediaPlayer();
				_seekingForwards = true;
				MP.seek(MP.getCurrentTime().add(Duration.seconds(2)));
			}
		});

		backward.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				MediaPlayer MP = _mediaView.getMediaPlayer();
				MP.seek(MP.getCurrentTime().add(Duration.seconds(-2)));
				updatePlaybackControls();
			}
		});
		
		time.setText("0:00:00");
		time.setTextFill(Color.WHITE);

		//Listener of progress bar and time.
		pb = new ProgressBar(0);
		pb.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(pb, Priority.ALWAYS);
		
		mute.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				
				if (_mediaView.getMediaPlayer() == null) {
					return;
				}
				// toggle mute
				boolean isMute = _mediaView.getMediaPlayer().isMute();
				_mediaView.getMediaPlayer().setMute(!isMute);
				if (isMute) {
					mute.setGraphic(new ImageView(imageManager.getImage("mediaNotMuted")));
				} else {
					mute.setGraphic(new ImageView(imageManager.getImage("mediaMuted")));
				}
				
			}
		});
		
		VBox mediaControlsContainer = new VBox();
		Pane controlsSpacer = new Pane();
		VBox.setVgrow(controlsSpacer, Priority.ALWAYS);

		HBox mediaControlsLayout = new HBox(5);
		mediaControlsLayout.setStyle("-fx-background-color: rgba(0,0,0,0.5)");
		mediaControlsLayout.setMaxWidth(Double.MAX_VALUE);
		mediaControlsLayout.setAlignment(Pos.CENTER);
		mediaControlsLayout.getChildren().addAll(pause, backward, forward, time, pb, mute);
		
		mediaControlsContainer.getChildren().setAll(controlsSpacer, mediaControlsLayout);
		
		_mediaViewLayout.setAlignment(Pos.CENTER);
		_mediaViewLayout.getChildren().add(mediaControlsContainer);
				
		// END MEDIA VIEW

		lvList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				return new Xcell();
			}
		});
		
		_creationLayout.setPadding(new Insets(8));
		_creationLayout.getChildren().setAll(wikiSearchLayout, horizSeparator, creationSearchLayout, lvList); 
		
		Separator vertSeparator = new Separator();
		vertSeparator.setPadding(new Insets(5));
		vertSeparator.setOrientation(Orientation.VERTICAL);
		
		_layout.getChildren().setAll(_creationLayout, vertSeparator, _mediaViewLayout);
		
		updateCreationList();

		primaryStage.setScene(s);
		primaryStage.sizeToScene();
		//primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setTitle("VARpedia - Main menu");
		
		primaryStage.heightProperty().addListener((obsValue, oldValue, newValue) -> {
			_mediaViewLayout.setPrefHeight(_mediaViewLayout.getPrefHeight() + (double) newValue - (double) oldValue); 
		});
		
		_wikisearch.textProperty().addListener(new InvalidCharacterChangeListener("0123456789abcdefghijklmnopqrstuvwxyz,.- ", _wikisearch));
		
		_wikisearch.setOnKeyReleased((e) -> {
			_wikibutton.setDisable(_wikisearch.getText().length() == 0);
			if (e.getCode() == KeyCode.ENTER) {
				_wikibutton.fire();
			}
		});


		//When click wiki search button
		_wikibutton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override public void handle(ActionEvent event) {
				

				_txt = _wikisearch.getText();
				Wiki wiki = new Wiki(_txt);

				wiki.setOnSucceeded((e) -> {
					WikiCreationMenu.createWindow(MainMenu.this, primaryStage, _txt, wiki.getValue());
				});

				try { 
					Thread w = new Thread(wiki);
					w.start();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
					
			}
			
		});


		//EventAction of creation search button
		_creationbutton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				updateCreationList();
			}
		});	 
		
		_creationsearch.textProperty().addListener((c) -> {
			_creationbutton.fire();
		});
	}

	// When click delete button of each creation.
	private void delete(String item){
		
		//pop out the confirmation 
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText("Are you sure to delete this creation?");
		alert.setContentText("Are you sure you want to delete \"" + item + ".mp4\"? :(");
		alert.setTitle("Confirm deletion");

		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			
			String deleteCommand = "rm -f " + dir + "/creations/" + item + ".mp4";
			ProcessBuilder dcProcess = new ProcessBuilder("bash", "-c", deleteCommand);
			try {
				dcProcess.start();
				updateCreationList();	
			} catch (Exception e) {
				e.printStackTrace();
			} 
			updateCreationList();
			
		}
	}

	// When click play button of each creation.
	private void play(String item){
		
		_mediaViewLayout.setMouseTransparent(false);
		_mediaViewLayout.setFocusTraversable(true);
		
		MediaPlayer currPlayer = _mediaView.getMediaPlayer();
		if (currPlayer != null) {
			currPlayer.stop();
		}
		
		MediaPlayer mediaPlayer;
		if (item != null) {
		
			File file = new File(dir + "/creations/", item + ".mp4");
			Media media = new Media(file.toURI().toString());
			mediaPlayer = new MediaPlayer(media);
		
		} else {
			mediaPlayer = new MediaPlayer(_mediaView.getMediaPlayer().getMedia());
		}
			
		_mediaView.setMediaPlayer(mediaPlayer);
		mediaPlayer.play();
				
		mediaPlayer.currentTimeProperty().addListener((obsValue, oldDuration, newDuration) -> {
						
			double totalSeconds = mediaPlayer.getTotalDuration().toSeconds(),
				   currSeconds = newDuration.toSeconds();
			
			pb.setProgress(currSeconds / totalSeconds);
			
			long seconds = (long) currSeconds;
		    long absSeconds = Math.abs(seconds);
		    String formattedTime = String.format(
		        "%d:%02d:%02d",
		        absSeconds / 3600,
		        (absSeconds % 3600) / 60,
		        absSeconds % 60);
		    time.setText(formattedTime);
			
		});
		
		mediaPlayer.statusProperty().addListener((change) -> {
			updatePlaybackControls();
		});
		
		mediaPlayer.setOnEndOfMedia(() -> {
			System.out.println("yea");
			pause.setGraphic(new ImageView(imageManager.getImage("mediaReplay")));	
			pb.setProgress(1);
		});

	}
	
	public void updateCreationList() {
		try {
			
			String keyword  = _creationsearch.getText();
			
			//Get the list of creation
			String listCommand = "ls " + dir + "/creations/ | grep mp4 | sort | cut -d'.' -f1";
			ProcessBuilder list = new ProcessBuilder("bash", "-c", listCommand);
			Process listprocess = list.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(listprocess.getInputStream()));
			String line;
			List<String> fileList = new ArrayList<String>();
			while ((line = stdout.readLine()) != null) {
				if (line.contains(keyword)) {
					fileList.add(line);
				}
			}

			lvList.setItems(FXCollections.observableArrayList(fileList));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public ImageManager getImageManager() {
		return imageManager;
	}
	
	private void loadImages() {
		
		imageManager = new ImageManager();
		
		// general button icons
		imageManager.loadImage("play", "resources/play.png", 15, 15);
		imageManager.loadImage("delete", "resources/delete.png", 15, 15);
		imageManager.loadImage("refresh", "resources/refresh.png", 15, 15);
		imageManager.loadImage("search", "resources/search.png", 15, 15);
		imageManager.loadImage("save", "resources/save.png", 15, 15);
		imageManager.loadImage("add", "resources/add.png", 15, 15);
		imageManager.loadImage("shiftDown", "resources/shiftDownIcon.png", 9, 7);
		imageManager.loadImage("shiftUp", "resources/shiftUpIcon.png", 9, 7);
		
		// media player icons
		imageManager.loadImage("mediaReplay", "resources/mediaPlayerReplay.png", 30, 30);
		imageManager.loadImage("mediaPlay", "resources/mediaPlayerPlay.png", 30, 30);
		imageManager.loadImage("mediaPause", "resources/mediaPlayerPause.png", 30, 30);
		imageManager.loadImage("mediaMuted", "resources/mediaPlayerMuted.png", 30, 30);
		imageManager.loadImage("mediaNotMuted", "resources/mediaPlayerNotMuted.png", 30, 30);
		imageManager.loadImage("mediaForwards", "resources/mediaPlayerForwards.png", 30, 30);
		imageManager.loadImage("mediaBackwards", "resources/mediaPlayerBackwards.png", 30, 30);
		

		
	}
	
	private void updatePlaybackControls() {
		
		Status currStatus = _mediaView.getMediaPlayer().getStatus();
		if (currStatus == null) {
			return;
		}
		
		if (currStatus.equals(Status.PLAYING)) {
			pause.setGraphic(new ImageView(imageManager.getImage("mediaPause")));
		} else if (currStatus.equals(Status.PAUSED)) {
			pause.setGraphic(new ImageView(imageManager.getImage("mediaPlay")));
		} else if (currStatus == Status.STOPPED) {
			pause.setGraphic(new ImageView(imageManager.getImage("mediaReplay")));
		}
		
	}
}
