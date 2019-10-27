package varpedia.app.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.image.ImageView;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import varpedia.app.resources.ImageManager;

public class AudioFileHBoxCell extends HBox {
	
	protected Label nameLabel;
	protected Pane spacer;
	protected Button playButton, deleteButton;
		
	public AudioFileHBoxCell(ButtonPressHandler playHandler, ButtonPressHandler deleteHandler, String audioFileName) {
		
		super(8);
		setAlignment(Pos.CENTER);
		setPadding(new Insets(4, 3, 4, 3));
		
		nameLabel = new Label(audioFileName);
		
		spacer = new Pane();
		HBox.setHgrow(spacer,  Priority.ALWAYS);
		
		playButton = new Button();
		playButton.getStyleClass().add("plain-graphic-button");
		playButton.setGraphic(new ImageView(ImageManager.getImage("play")));
		playButton.setOnAction((e) -> {
			
			playHandler.onButtonPress(this);
			
		});
		
		
		deleteButton = new Button();
		deleteButton.getStyleClass().add("plain-graphic-button");
		deleteButton.setGraphic(new ImageView(ImageManager.getImage("delete")));
		deleteButton.setOnAction((e) -> {
			
			deleteHandler.onButtonPress(this);
			
		});
								
		getChildren().addAll(nameLabel, spacer, playButton, deleteButton);
		
	}
	
	public void setPlaying(boolean playing) {
		
		if (playing) {
			playButton.setGraphic(new ImageView(ImageManager.getImage("play")));
		} else {
			playButton.setGraphic(new ImageView(ImageManager.getImage("stop")));
		}
		
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof AudioFileHBoxCell)) {
			return false;
		}
		
		AudioFileHBoxCell other = (AudioFileHBoxCell) o;
		return other.getAudioFileName().equals(this.getAudioFileName());
		
	}
	
	public String getAudioFileName() {
		return nameLabel.getText();
	}
	
	public void setAudioFileName(String name) {
		nameLabel.setText(name);
	}
	
	public static interface ButtonPressHandler {
		
		public void onButtonPress(AudioFileHBoxCell source);
		
	}
	
}
