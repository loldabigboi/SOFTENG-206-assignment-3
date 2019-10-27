package varpedia.app.stages;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;

import javafx.collections.FXCollections;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;

import javafx.scene.image.ImageView;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javafx.stage.Stage;

import javafx.util.Callback;

import varpedia.app.components.MediaPlayerComponent;

import varpedia.app.resources.ImageManager;
import varpedia.app.stages.AlertDialog.AlertType;
import varpedia.app.stages.AlertDialog.ButtonType;
import varpedia.app.tasks.WikiTextRetrievalTask;
import varpedia.app.tasks.WikiTextRetrievalTask.AmbiguousResultsException;
import varpedia.app.tasks.WikiTextRetrievalTask.NoWikiEntryFoundException;

import varpedia.app.util.FileManager;
import varpedia.app.util.BashUtils;
import varpedia.app.util.FilePaths;

public class MainMenu extends Application{
	
	private HBox _layout = new HBox(8);
	
	private VBox _creationLayout = new VBox(8);
	
	private Label _wikiLabel = new Label("Wiki search: ");
	private TextField _wikisearch = new TextField();
	
	private Label _creationLabel = new Label("Filter creations: ");
	private TextField _creationsearch = new TextField();
	
	private Button _wikiButton = new Button();
	private Button _creationButton = new Button();
	
	private Button _helpButton = new Button("Help");
	private Button _quizButton = new Button("Generate quiz..");
	
	private StackPane _listViewContainer = new StackPane();
	private ListView<String> _creationListView = new ListView<String>();
	private ImageView _logoImageView = new ImageView();
		
	private MediaPlayerComponent _creationPlayer = new MediaPlayerComponent();

	@Override
	public void start(Stage primaryStage) throws Exception {
				
		_layout.setPadding(new Insets(5));
		
		HBox wikiSearchLayout = new HBox(5);
		wikiSearchLayout.setAlignment(Pos.CENTER);
		
		_wikiButton.setGraphic(new ImageView(ImageManager.getImage("search")));
		_wikiButton.getStyleClass().add("plain-graphic-button");
		_wikiButton.setDisable(true);
		
		_creationButton.setGraphic(new ImageView(ImageManager.getImage("refresh")));
		_creationButton.getStyleClass().add("plain-graphic-button");
		
		HBox.setHgrow(_wikisearch, Priority.ALWAYS);
		HBox.setHgrow(_creationsearch, Priority.ALWAYS);
		
		wikiSearchLayout.getChildren().addAll(_wikiLabel, _wikisearch, _wikiButton);
		
		Separator horizSeparator = new Separator();
		horizSeparator.setPadding(new Insets(2, 0, -2, 0));
		horizSeparator.setOrientation(Orientation.HORIZONTAL);
		
		HBox creationSearchLayout = new HBox(5);
		creationSearchLayout.setAlignment(Pos.CENTER);
		creationSearchLayout.getChildren().addAll(_creationLabel, _creationsearch, _creationButton);
		
		VBox.setVgrow(_creationListView, Priority.ALWAYS);
		_creationListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				return new CreationCell();
			}
		});
		
		_logoImageView.setImage(ImageManager.getImage("logo"));
		_logoImageView.setStyle("-fx-opacity: 0.2");  // make it somewhat transparent
		
		VBox.setVgrow(_listViewContainer, Priority.ALWAYS);
		_listViewContainer.setAlignment(Pos.CENTER);
		_listViewContainer.getChildren().setAll(_creationListView);
		
		HBox quizButtonLayout = new HBox();
		
		_helpButton.getStyleClass().add("blue-button");
		_helpButton.setOnAction((e) -> {
			// TODO: implement help pop-up
		});
		
		Pane spacer = new Pane();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		_quizButton.getStyleClass().add("blue-button");
		_quizButton.setOnAction((e) -> {
			
			Stage quizWindow = new QuizWindow(primaryStage);
			quizWindow.showAndWait();
			
		});
		
		quizButtonLayout.getChildren().setAll(_helpButton, spacer, _quizButton);
		
		_creationLayout.setPadding(new Insets(8));
		_creationLayout.setPrefWidth(400);
		_creationLayout.setMaxHeight(Double.MAX_VALUE);
		_creationLayout.getChildren().setAll(wikiSearchLayout, horizSeparator, creationSearchLayout, _listViewContainer, quizButtonLayout); 
		
		Separator vertSeparator = new Separator();
		vertSeparator.setPadding(new Insets(0, 4, 0, 0));
		vertSeparator.setOrientation(Orientation.VERTICAL);
		
		// trying to make media view (MediaPlayerComponent) expand to fit remaining space without this redundant container didnt work,
		// hence this solution.
		Pane creationPlayerContainer = new Pane();
		HBox.setHgrow(creationPlayerContainer, Priority.ALWAYS);
		creationPlayerContainer.getChildren().setAll(_creationPlayer);		
		
		_creationPlayer.prefWidthProperty().bind(creationPlayerContainer.widthProperty());
		_creationPlayer.prefHeightProperty().bind(creationPlayerContainer.heightProperty());
		
		_layout.getChildren().setAll(_creationLayout, vertSeparator, creationPlayerContainer);
		
		updateCreationList();
		
		Scene scene = new Scene(_layout, 900, 400);
		scene.getStylesheets().add("varpedia/app/main.css");

		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
		
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setWidth(1300);
		primaryStage.setHeight(700);
		
		primaryStage.setMaximized(true);

		primaryStage.setTitle("VARpedia - Main menu");
		
		_creationLayout.setMinWidth(_creationLayout.getWidth());
		_creationLayout.setMinHeight(_creationLayout.getHeight());
		
		_wikisearch.setOnKeyReleased((e) -> {
			_wikiButton.setDisable(_wikisearch.getText().length() == 0);
			if (e.getCode() == KeyCode.ENTER) {
				_wikiButton.fire();
			}
		});

		_wikiButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override public void handle(ActionEvent event) {
				
				_wikiButton.setDisable(true);

				String searchTerm = _wikisearch.getText();
				WikiTextRetrievalTask wikiTask = new WikiTextRetrievalTask(searchTerm);

				wikiTask.setOnSucceeded((e) -> {
					Stage wikiCreationWindow = new WikiCreationMenu(MainMenu.this, primaryStage, searchTerm, wikiTask.getValue());
					wikiCreationWindow.showAndWait();
					_wikiButton.setDisable(false);
				});
				
				wikiTask.setOnFailed((e) -> {
					
					String contentText = "An error has occurred, please enter a different search term",
						   headerText  = "Error";
					
					if (wikiTask.getException() instanceof NoWikiEntryFoundException) { // wikit could not find wiki
						contentText = "No wiki entry found for \"" + searchTerm + "\". Please enter a different search term.";
						headerText = "Error: Wiki entry not found";
					} else if (wikiTask.getException() instanceof AmbiguousResultsException) {  // wikit found multiple results for search term
						contentText = "Ambiguous results found for \"" + searchTerm + "\". Please enter a more specific search term.";
						headerText = "Error: Ambiguous results";
					}
					
					AlertDialog errorDialog = new AlertDialog(AlertType.ERROR, "Error", headerText, contentText);
					errorDialog.showAndWait();
					_wikiButton.setDisable(false);
					
				});

				Service<String> wikiService = new Service<String>() {

					@Override
					protected Task<String> createTask() {
						return wikiTask;
					}
					
				};
				wikiService.start();
					
			}
			
		});


		//EventAction of creation search button
		_creationButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				updateCreationList();
			}
		});	 
		
		_creationsearch.textProperty().addListener((c) -> {
			_creationButton.fire();
		});
	}

	// Delete creation with passed name
	private void delete(String creationName){
		
		// ask user if they're sure they want to delete creation
		AlertDialog alertDialog = new AlertDialog(AlertType.CONFIRMATION, "Confirm deletion", "Are you sure you want to delete this creation?",
												 "Are you sure you want to delete \"" + creationName + "\"?");
		ButtonType buttonType = alertDialog.showAndReturn();
		
		if(buttonType == ButtonType.OK) {
			
			FileManager.deleteCreation(null, creationName);
			updateCreationList();
			
		}
	}
	
	// Update the creation list view
	public void updateCreationList() {
		
		String keyword  = _creationsearch.getText();
		
		// Get the list of creation file names
		String listCommand = "ls " + FilePaths.CREATIONS_DIR + " | grep mp4 | sort";// | cut -d'.' -f1";
		String output = BashUtils.runCommand(null, listCommand, true).getStdOut();
		String[] fileNames = output.split(".mp4");
		
		// filter by keyword
		List<String> filteredFileNames = new ArrayList<>();
		for (String fileName : fileNames) {
			String trimmedFileName = fileName.trim();
			if (trimmedFileName.length() > 0 && trimmedFileName.contains(keyword)) {
				filteredFileNames.add(trimmedFileName);
			}
		}

		_creationListView.setItems(FXCollections.observableArrayList(filteredFileNames));
		
		if (_creationListView.getItems().isEmpty()) {  // replace list view with VARpedia logo image
			_listViewContainer.getChildren().setAll(_creationListView);
			_listViewContainer.getChildren().add( _logoImageView);
		} else {  // remove overlaid logo image
			_listViewContainer.getChildren().setAll(_creationListView);
		}
		
	}

	public static void main(String[] args) {
		
		// load resources
		ImageManager.init();
		Font.loadFont(MainMenu.class.getResource("/varpedia/app/resources/Roboto-Regular.ttf").toExternalForm(), 12);		
		
		launch(args);
		
	}
	
	// Class for each cell in the creation list view
	class CreationCell extends ListCell<String>{
		
		HBox layout;
		Label nameLabel;
		String lastItem;

		public CreationCell() {
			
			super();
			
			layout = new HBox(8);
			layout.setAlignment(Pos.CENTER);
			layout.setPadding(new Insets(5));
			
			nameLabel = new Label("<empty>");
			
			Pane spacer = new Pane();
			HBox.setHgrow(spacer, Priority.ALWAYS);
						
			Button deleteButton = new Button();
			deleteButton.setGraphic(new ImageView(ImageManager.getImage("delete")));
			deleteButton.getStyleClass().add("plain-graphic-button");
			deleteButton.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					delete(lastItem);
				}
				
			});
				
			Button playButton = new Button();
			playButton.setGraphic(new ImageView(ImageManager.getImage("play")));
			playButton.getStyleClass().add("plain-graphic-button");
			playButton.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					_creationPlayer.playMedia(FilePaths.CREATIONS_DIR + lastItem + ".mp4");
				}
			});
			
			layout.getChildren().setAll(nameLabel, spacer, playButton, deleteButton);

			
		}

		@Override
		protected void updateItem(String item, boolean empty) {
			
			super.updateItem(item, empty);
			setText(null);
			
			if (empty) {
				
				lastItem = null;
				setGraphic(null);
				
			} else {
				
				lastItem = item;
				nameLabel.setText(item!=null ? item : "<null>");
				setGraphic(layout);
				
			}
			
		}

	}
	
}
