package varpedia.app.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import javafx.scene.layout.VBox;

import varpedia.app.resources.ImageManager;

// class to represent each cell of the list view
public class ShiftableAudioFileHBoxCell extends AudioFileHBoxCell {
	
	private VBox shiftButtonContainer;
	private Button shiftUpButton, shiftDownButton;   // allow rearranging of audio file list
	
	public ShiftableAudioFileHBoxCell(ButtonPressHandler playHandler, ButtonPressHandler deleteHandler, ButtonPressHandler shiftUpHandler, 
									  ButtonPressHandler shiftDownHandler, String audioFileName) {
		
		super(playHandler, deleteHandler, audioFileName);
					
		shiftButtonContainer = new VBox(6);
		
		shiftUpButton = new Button();
		shiftUpButton.setTooltip(new Tooltip("Shift this audio file up (earlier in creation)"));
		shiftUpButton.getStyleClass().add("plain-graphic-button");
		shiftUpButton.setOnAction((e) -> {
			shiftUpHandler.onButtonPress(this);
		});
		shiftUpButton.setGraphic(new ImageView(ImageManager.getImage("shiftUp")));
		shiftUpButton.setPadding(new Insets(0, 3, 0, 3));
		
		shiftDownButton = new Button();
		shiftDownButton.setTooltip(new Tooltip("Shift this audio file down (later in creation)"));
		shiftDownButton.getStyleClass().add("plain-graphic-button");
		shiftDownButton.setOnAction((e) -> {
			shiftDownHandler.onButtonPress(this);
		});
		shiftDownButton.setGraphic(new ImageView(ImageManager.getImage("shiftDown")));
		shiftDownButton.setPadding(new Insets(0, 3, 0, 3));
		
		shiftButtonContainer.getChildren().addAll(shiftUpButton, shiftDownButton);	
		
		ObservableList<Node> tempList = FXCollections.observableArrayList(
				shiftButtonContainer
		);
		tempList.addAll(this.getChildren());
		this.getChildren().setAll(tempList);
			
	}
	
	@Override
	public boolean equals(Object o) {
		
		return o == this;
		
	}
	
}