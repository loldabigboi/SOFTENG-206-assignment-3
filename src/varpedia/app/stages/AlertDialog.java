package varpedia.app.stages;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import varpedia.app.resources.ImageManager;

/*
 * Class used to create custom Alert-esque dialogs.
 */

public class AlertDialog extends Stage{
	
	public enum ButtonType {
		CONFIRM,
		OK,
		CANCEL,
	};
	
	public enum AlertType {
		CONFIRMATION,
		INFORMATION,
		ERROR
	};
	
	private ButtonType clickedButton;
	
	public AlertDialog(AlertType alertType, String title, String headerText, String contentText) {
		
		super();
		this.setTitle(title);
		
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(25));
		
		HBox headerLayout = new HBox(20);
		headerLayout.setAlignment(Pos.CENTER_LEFT);
		
		ImageView iconImageView = new ImageView();
		
		Label headerLabel = new Label(headerText);
		headerLabel.getStyleClass().add("heading2");
		
		Separator separator = new Separator();
		separator.setMaxWidth(Double.MAX_VALUE);
		separator.setOrientation(Orientation.HORIZONTAL);
		
		Label contentLabel = new Label(contentText);
		contentLabel.getStyleClass().add("heading4");
		
		HBox buttonContainer = new HBox(10);
		buttonContainer.setMaxWidth(Double.MAX_VALUE);
		buttonContainer.setAlignment(Pos.CENTER);
		
		if (alertType == AlertType.CONFIRMATION) {  // Confirmation dialog
			
			iconImageView.setImage(ImageManager.getImage("questionMark"));
			
			Button confirmButton = new Button("Okay");
			confirmButton.getStyleClass().add("blue-button");
			confirmButton.setOnAction((e) -> {
				clickedButton = ButtonType.OK;
				this.close();
			});
			
			Button cancelButton = new Button("Cancel");
			cancelButton.getStyleClass().add("plain-text-button");
			cancelButton.setOnAction((e) -> {
				clickedButton = ButtonType.CANCEL;
				this.close();
			});
			
			buttonContainer.getChildren().setAll(confirmButton, cancelButton);
			
		} else if (alertType == AlertType.ERROR || alertType == AlertType.INFORMATION) {  // Error / information dialog
			
			Button okButton = new Button("Okay");
			okButton.getStyleClass().add("blue-button");
			okButton.setOnAction((e) -> {
				clickedButton = ButtonType.OK;
				this.close();
			});
			
			if (alertType == AlertType.ERROR) {
				iconImageView.setImage(ImageManager.getImage("error"));
			} else {
				iconImageView.setImage(ImageManager.getImage("exclamationMark"));
			}
			
			buttonContainer.getChildren().setAll(okButton);
			
		}
		
		headerLayout.getChildren().setAll(iconImageView, headerLabel);
		
		layout.getChildren().setAll(headerLayout, separator, contentLabel, buttonContainer);
		
		Scene scene = new Scene(layout);
		scene.getStylesheets().add("varpedia/app/main.css");
		
		this.setScene(scene);
		this.sizeToScene();
 
	}
	
	public ButtonType showAndReturn() {
		
		this.showAndWait();
		return clickedButton;
		
	}

}
