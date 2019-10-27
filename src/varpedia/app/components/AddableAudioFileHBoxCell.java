package varpedia.app.components;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import varpedia.app.components.AudioFileHBoxCell.ButtonPressHandler;
import varpedia.app.resources.ImageManager;

public class AddableAudioFileHBoxCell extends AudioFileHBoxCell {
	
	private Button addButton;

	public AddableAudioFileHBoxCell(ButtonPressHandler playHandler, ButtonPressHandler deleteHandler, ButtonPressHandler addHandler, String audioFileName) {
		
		super(playHandler, deleteHandler, audioFileName);
		
		addButton = new Button();
		addButton.getStyleClass().add("plain-graphic-button");
		addButton.setGraphic(new ImageView(ImageManager.getImage("add")));
		
		addButton.setOnAction((e) -> {
			addHandler.onButtonPress(this);
		});
		
		this.getChildren().add(addButton);
		
	}
	
}