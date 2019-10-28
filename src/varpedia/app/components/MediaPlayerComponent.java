package varpedia.app.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
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
import javafx.scene.text.Font;
import javafx.util.Duration;
import varpedia.app.resources.ImageManager;
import varpedia.app.util.FilePaths;

public class MediaPlayerComponent extends StackPane {
	
	private VBox _mediaInterfaceContainer = new VBox();
	private HBox _mediaControlsLayout = new HBox(8);
	private MediaView _mediaView = new MediaView();
	
	private Slider _mediaProgressSlider = new Slider(0, 1, 0);
	private Label _mediaDurationLabel = new Label();
	
	private Button _pauseButton = new Button(),
				   _muteButton = new Button(),
				   _backToStartButton = new Button();
	
	private boolean seeking = false;
	
	public MediaPlayerComponent() {
		
		super();
		
		this.setStyle("-fx-background-color: black");
		this.setMouseTransparent(true);  // only until a video is played
		this.setFocusTraversable(false);  // ^^
				
		_mediaView.fitWidthProperty().bind(this.prefWidthProperty());
		_mediaView.fitHeightProperty().bind(this.prefHeightProperty());
		
		this.getChildren().add(_mediaView);
		
		_muteButton.setMinWidth(Control.USE_PREF_SIZE);
		_muteButton.setStyle("-fx-background-color: transparent");
		_muteButton.setGraphic(new ImageView(ImageManager.getImage("mediaNotMuted")));
		
		_pauseButton.setMinWidth(Control.USE_PREF_SIZE);
		_pauseButton.setStyle("-fx-background-color: transparent");
		_pauseButton.setGraphic(new ImageView(ImageManager.getImage("mediaPlay")));
		
		_backToStartButton.setMinWidth(Control.USE_PREF_SIZE);
		_backToStartButton.setPadding(new Insets(0, 15, 0, 7));
		_backToStartButton.setStyle("-fx-background-color: transparent");
		_backToStartButton.setGraphic(new ImageView(ImageManager.getImage("mediaBackwards")));
		
		_pauseButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override public void handle(ActionEvent event) {
				
				MediaPlayer mp = _mediaView.getMediaPlayer();
				if (mp.getCurrentTime().equals(mp.getStopTime())) {
					playMedia(null);  // restart media. using seek() when paused was buggy so this is a hack workaround
					updatePlaybackControls();
				} else {
					togglePause();
				}
				
			}
			
		});

		_backToStartButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				MediaPlayer MP = _mediaView.getMediaPlayer();
				MP.seek(Duration.ZERO);
			}
		});
		
		_mediaDurationLabel.setTextFill(Color.WHITE);
		_mediaDurationLabel.setStyle("-fx-font-size: 13px");
	    
	    _mediaProgressSlider.setMaxWidth(Double.MAX_VALUE);
	    _mediaProgressSlider.setPadding(new Insets(0, 5, 0, 5));
	    _mediaProgressSlider.setId("media-slider");
		
		ProgressBar mediaProgressBar = new ProgressBar(0);
		mediaProgressBar.setMouseTransparent(true);
		mediaProgressBar.setMaxWidth(Double.MAX_VALUE);
		mediaProgressBar.setId("media-progress-bar");
		mediaProgressBar.setPadding(new Insets(0, 5, 0, 5));
		mediaProgressBar.progressProperty().bind(_mediaProgressSlider.valueProperty());
		
		HBox.setHgrow(mediaProgressBar, Priority.ALWAYS);
		HBox.setHgrow(_mediaProgressSlider, Priority.ALWAYS);
		
		StackPane progressContainer = new StackPane();
		progressContainer.getChildren().add(mediaProgressBar);
		progressContainer.getChildren().add(_mediaProgressSlider);
		
		_muteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				toggleMute();
			}
		});
		
		
		Pane interfaceSpacer = new Pane();
		VBox.setVgrow(interfaceSpacer, Priority.ALWAYS);
		
		Pane controlsSpacer = new Pane();
		HBox.setHgrow(controlsSpacer, Priority.ALWAYS);
		
		_mediaControlsLayout.setStyle("-fx-background-color: rgba(0,0,0,0.5)");
		_mediaControlsLayout.setMaxWidth(Double.MAX_VALUE);
		_mediaControlsLayout.setPadding(new Insets(3, 5, 3, 5));
		_mediaControlsLayout.setAlignment(Pos.CENTER);
		_mediaControlsLayout.getChildren().addAll(_pauseButton, _backToStartButton, _mediaDurationLabel, controlsSpacer, _muteButton);
		
		_mediaInterfaceContainer.getChildren().setAll(interfaceSpacer, progressContainer, _mediaControlsLayout);
		
		this.setAlignment(Pos.CENTER);
		this.getChildren().add(_mediaInterfaceContainer);
				
	}
	
	public void togglePause() {
		
		MediaPlayer mp = _mediaView.getMediaPlayer();
		
		if (mp != null) {
			if (mp.getStatus().equals(Status.PLAYING)) {
				mp.pause();
			} else if (mp.getStatus().equals(Status.PAUSED)) {
				mp.play();
			}	
		}
		
	}
	
	public void setPause(boolean pause) {
		
		MediaPlayer mp = _mediaView.getMediaPlayer();
		if (mp != null) {
			if (pause) {
				mp.pause();
			} else {
				mp.play();
			}
		}
		
	}
	
	public void toggleMute() {
		
		MediaPlayer mp = _mediaView.getMediaPlayer();
		if (mp != null) {
			setMute(!mp.isMute());
		}
		
	}
	
	public void setMute(boolean mute) {
		
		MediaPlayer mp = _mediaView.getMediaPlayer();
		if (mp != null) {
			if (mute) {
				_muteButton.setGraphic(new ImageView(ImageManager.getImage("mediaMuted")));
			} else {
				_muteButton.setGraphic(new ImageView(ImageManager.getImage("mediaNotMuted")));
			}
			mp.setMute(mute);
		}
		
	}
	
	public void stopMedia() {
		
		MediaPlayer mp = _mediaView.getMediaPlayer();
		if (mp != null) {
			mp.pause();
			mp.stop();
		}
		
	}

	public void playMedia(String mediaFilePath){
		
		this.setMouseTransparent(false);
		this.setFocusTraversable(true);
		
		MediaPlayer currPlayer = _mediaView.getMediaPlayer();
		if (currPlayer != null) {
			currPlayer.stop();
		}
		
		MediaPlayer mediaPlayer;
		if (mediaFilePath != null) {
		
			File file = new File(mediaFilePath);
			Media media = new Media(file.toURI().toString());
			mediaPlayer = new MediaPlayer(media);
		
		} else {
			
			// just re-initialises the current media player - this solves an issue where
			// if the video is paused and then the user seeks to the end of the media and then
			// restarts, the pause button becomes unresponsive
			mediaPlayer = new MediaPlayer(_mediaView.getMediaPlayer().getMedia());
		
		}
			
		_mediaView.setMediaPlayer(mediaPlayer);
		mediaPlayer.play();
		
		// update progress label with current time
		mediaPlayer.currentTimeProperty().addListener((obsValue, oldDuration, newDuration) -> {
						
			double totalSeconds = mediaPlayer.getTotalDuration().toSeconds(),
				   seconds = newDuration.toSeconds();
			
			// to prevent feedback loop
			if (!seeking) {
				_mediaProgressSlider.setValue(seconds / totalSeconds);
			}
			
			int currSeconds = (int) seconds;
			
			String formatString = "%d:%02d";
			if ((((int) totalSeconds) / 60) / 10 > 1) {
				formatString = "%02d:%02d";
			}	
		    
		    String formattedCurrTime = String.format(
		        formatString,
		        currSeconds / 60,
		        currSeconds % 60
		    );
		    
		    String formattedTotalTime = String.format(
		    	formatString,
		    	((int) totalSeconds) / 60,
		    	((int) totalSeconds) % 60
		    );
		    	
		    _mediaDurationLabel.setText(formattedCurrTime + " / " + formattedTotalTime);
		    updatePlaybackControls();
		    
		});
		
		// can't use the slider's value property as this would then mean if the user just holds the slider "thumb" in
		// place the media continues playing, so we have to use the seeking variable to determine when the user is holding
		// down the thumb.
		
		_mediaProgressSlider.setOnMousePressed((e) -> {
			seeking = true;
			mediaPlayer.seek(Duration.seconds(mediaPlayer.getMedia().getDuration().toSeconds() * _mediaProgressSlider.getValue()));
		});
		
		_mediaProgressSlider.setOnMouseDragged((e) -> {
			seeking = true;
			mediaPlayer.seek(Duration.seconds(mediaPlayer.getMedia().getDuration().toSeconds() * _mediaProgressSlider.getValue()));
		});
		
		_mediaProgressSlider.setOnMouseReleased((e) -> {
			mediaPlayer.seek(Duration.seconds(mediaPlayer.getMedia().getDuration().toSeconds() * _mediaProgressSlider.getValue()));
			seeking = false;
		});
		
		mediaPlayer.statusProperty().addListener((c) -> {
			updatePlaybackControls();
		});
		
		mediaPlayer.setOnEndOfMedia(() -> {
			_pauseButton.setGraphic(new ImageView(ImageManager.getImage("mediaReplay")));	
			_mediaProgressSlider.setValue(1);
		});

	}
			
	private void updatePlaybackControls() {
		
		Status currStatus = _mediaView.getMediaPlayer().getStatus();
		if (currStatus == null) {
			return;
		}
		
		if (currStatus.equals(Status.PLAYING)) {
			_pauseButton.setGraphic(new ImageView(ImageManager.getImage("mediaPause")));
		} else if (currStatus.equals(Status.PAUSED)) {
			_pauseButton.setGraphic(new ImageView(ImageManager.getImage("mediaPlay")));
		} else if (currStatus == Status.STOPPED) {
			_pauseButton.setGraphic(new ImageView(ImageManager.getImage("mediaReplay")));
		}
		
	}

}
