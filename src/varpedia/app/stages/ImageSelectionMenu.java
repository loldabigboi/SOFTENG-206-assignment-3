package varpedia.app.stages;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import varpedia.app.util.FilePaths;
import varpedia.app.util.BashUtils;
import varpedia.app.util.FileManager;

public class ImageSelectionMenu extends Stage {
	
	private static final int imagesPerRow = 4;
	private static final int cellWidth = 125,
							 cellHeight = 125;
	private static final int cellBorderPadding = 10;
	
	private ObservableMap<String, Boolean> imageSelectionMap;
	private List<String> selectedImagePaths;
	
	public ImageSelectionMenu(Stage parentWindow, List<String> imagePaths) {
		
		super();
		
		this.initModality(Modality.APPLICATION_MODAL);
		this.initOwner(parentWindow);
		
		VBox root = new VBox(20);
		root.setPadding(new Insets(20));
		root.setAlignment(Pos.CENTER);
		
		Label instructionsLabel = new Label("Please choose the images you want in your creation.");
		instructionsLabel.getStyleClass().add("heading2");
		
		imageSelectionMap = FXCollections.observableMap(new HashMap<String, Boolean>());
		
		GridPane imageLayoutGrid = new GridPane();
		
		for (int i = 0; i < imagesPerRow; i++) {
			imageLayoutGrid.getColumnConstraints().add(new ColumnConstraints(cellWidth + 2*cellBorderPadding));
		}
		for (int i = 0; i < Math.ceil(imagePaths.size() / (double) imagesPerRow); i++) {
			imageLayoutGrid.getRowConstraints().add(new RowConstraints(cellHeight + 2*cellBorderPadding));
		}
		
		imageLayoutGrid.setHgap(18);
		imageLayoutGrid.setVgap(18);
		
		int maxRow = imagePaths.size() / imagesPerRow;	
		for (int i = 0; i < imagePaths.size(); i++) {
			
			int row = i / imagesPerRow,
				col = i % imagesPerRow;
			
			if (row == maxRow) {
				col += Math.ceil(((double) imagesPerRow)/2) - 1;  // center cell
			}
			
			try {
				
				String filePath = imagePaths.get(i);
				
				imageSelectionMap.put(filePath, Boolean.FALSE);
				
				File imageFile = new File(filePath);
				Image originalImage = new Image(imageFile.toURI().toURL().toExternalForm());
				
				double scaleFactor,
					   width  = originalImage.getWidth(),
					   height = originalImage.getHeight();
				
				if (width > height) {
					scaleFactor = cellWidth / width;
				} else {
					scaleFactor = cellHeight / height;
				}
				
				width  *= scaleFactor;
				height *= scaleFactor;
				
				BorderPane cellContainer = new BorderPane();
				cellContainer.setStyle(
						  "-fx-background-color: rgb(255,255,255);\n"
						+ "-fx-border-radius: 2px;\n"
						+ "-fx-border-color: rgb(200,200,200)"
				);
				cellContainer.setPadding(new Insets(cellBorderPadding));
				
				StackPane cellContentContainer = new StackPane();
				cellContentContainer.setMinWidth(cellWidth);
				cellContentContainer.setMaxWidth(cellWidth);
				cellContentContainer.setMinHeight(cellHeight);
				cellContentContainer.setMaxHeight(cellHeight);
				
				Image image = new Image(imageFile.toURI().toURL().toExternalForm(), width, height, true, true);
				ImageView imageView = new ImageView(image);
				StackPane.setAlignment(imageView, Pos.CENTER);
				
				CheckBox cellCheckBox = new CheckBox();
				cellCheckBox.setOnAction((e) -> {
					
					boolean oldValue = imageSelectionMap.get(filePath).booleanValue();
					imageSelectionMap.put(filePath, Boolean.valueOf(!oldValue));
					
				});
				StackPane.setAlignment(cellCheckBox, Pos.TOP_RIGHT);
				
				imageView.setOnMouseClicked((e) -> {
					cellCheckBox.requestFocus();
					cellCheckBox.fire();  // when image is clicked toggle the associated checkbox
				});
				
				cellContentContainer.getChildren().setAll(imageView, cellCheckBox);
				cellContainer.setCenter(cellContentContainer);
				
				GridPane.setHalignment(cellContainer, HPos.CENTER);
				GridPane.setValignment(cellContainer, VPos.CENTER);
				
				imageLayoutGrid.add(cellContainer, col, row);
				
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			}
			
		}
		
		HBox buttonLayout = new HBox(15);
		
		boolean confirmed = false;
		
		Button confirmButton = new Button("Confirm Selection");
		confirmButton.setDisable(true);
		confirmButton.getStyleClass().add("blue-button");
		confirmButton.setOnAction((e) -> {
			
			selectedImagePaths = new ArrayList<String>();
			for (String imagePath : imageSelectionMap.keySet()) {
				if (imageSelectionMap.get(imagePath)) {
					selectedImagePaths.add(imagePath);
				}
			}
			
			this.close();
			
		});
		
		imageSelectionMap.addListener((
			new MapChangeListener<String, Boolean>() {

				@Override
				public void onChanged(Change<? extends String, ? extends Boolean> arg0) {
					
					boolean imageSelected = false;
					for (Boolean key : imageSelectionMap.values()) {
						if (key) {
							imageSelected = true;
							break;
						}
					}
					
					confirmButton.setDisable(!imageSelected);
					
				}
				
			}
		));
		
		
		Button cancelButton = new Button("Cancel");
		cancelButton.getStyleClass().add("plain-text-button");
		cancelButton.setOnAction((e) -> {
			this.close();
		});
		
		buttonLayout.getChildren().addAll(confirmButton, cancelButton);
		buttonLayout.setAlignment(Pos.CENTER);
		
		root.getChildren().addAll(instructionsLabel, imageLayoutGrid, buttonLayout);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add("varpedia/app/main.css");
		
		this.setScene(scene);
		this.sizeToScene();
		this.setResizable(false);
		
	}
		
	public List<String> showAndReturn() {
				
		this.showAndWait();
		return selectedImagePaths;
		
	}

}
