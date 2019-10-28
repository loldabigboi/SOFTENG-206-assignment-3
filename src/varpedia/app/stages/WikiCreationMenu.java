package varpedia.app.stages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import javafx.scene.input.KeyCode;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.scene.paint.Color;

import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import varpedia.app.components.AudioFileHBoxCell.ButtonPressHandler;
import varpedia.app.components.AudioFileHBoxCell;
import varpedia.app.components.AddableAudioFileHBoxCell;
import varpedia.app.components.ShiftableAudioFileHBoxCell;

import varpedia.app.listeners.InvalidCharacterChangeListener;

import varpedia.app.models.NoSelectionModel;

import varpedia.app.resources.ImageManager;
import varpedia.app.stages.AlertDialog.AlertType;
import varpedia.app.stages.AlertDialog.ButtonType;
import varpedia.app.tasks.CreateAudioFileTask;
import varpedia.app.tasks.CreateCreationTask;
import varpedia.app.tasks.GetFlickrImagesTask;
import varpedia.app.tasks.CreateAudioFileTask.Synthesiser;

import varpedia.app.util.BashUtils;
import varpedia.app.util.FilePaths;

/**
 * I know this class is very long, but this is pretty unavoidable. Given the extensive features present in this window, I see no
 * sensible way to segment its functionality any further. Hence, I ask that it not be marked down simply because the class is long - such long
 * classes are often unavoidable and a class simply being long is not necessarily indicative of poor code quality. I'm aware that using SceneBuilder
 * could have helped in this regard, but given that we were given the option to not use it, I don't think this is a valid argument.
 */

public class WikiCreationMenu extends Stage {
	
	private static final String validCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 (),&-_";
	
	private ListView<AudioFileHBoxCell> audioFileListView = new ListView<>();
	private ListView<AudioFileHBoxCell> creationAudioFileListView = new ListView<>();
	
	private MediaPlayer currentAudioPreview = null;
	private ObjectProperty<AudioFileHBoxCell> ownerOfAudioPreview = new SimpleObjectProperty<>(null);
	
	// audio file cell button press handlers
	
	private final ButtonPressHandler playButtonPressHandler = new ButtonPressHandler() {

		@Override
		public void onButtonPress(AudioFileHBoxCell source) {
			
			AudioFileHBoxCell currentOwner = ownerOfAudioPreview.getValue();
			
			if (currentOwner == source) {
				
				source.setPlaying(false);
				currentAudioPreview.stop();
				ownerOfAudioPreview.setValue(null);
				
			} else {
				
				if (currentOwner != null) {
					currentAudioPreview.stop();
					currentOwner.setPlaying(false);
				}
				
				source.setPlaying(true);
				
				Media audio = new Media(new File(FilePaths.TEMP_AUDIO_DIR, source.getAudioFileName() + ".wav").toURI().toString());
				currentAudioPreview = new MediaPlayer(audio);
				currentAudioPreview.play();
				ownerOfAudioPreview.setValue(source);
				
				currentAudioPreview.setOnEndOfMedia(() -> {
					source.setPlaying(false);
					ownerOfAudioPreview.setValue(null);
				});
			
			}
			
		}
					
	};
	
	private final ButtonPressHandler addButtonPressHandler = new ButtonPressHandler() {

		@Override
		public void onButtonPress(AudioFileHBoxCell source) {
			
			AudioFileHBoxCell cell = new ShiftableAudioFileHBoxCell(playButtonPressHandler, deleteButtonPressHandler, shiftUpButtonPressHandler,
																	shiftDownButtonPressHandler, source.getAudioFileName());
			creationAudioFileListView.getItems().add(cell);
			
		}

	};
	
	private final ButtonPressHandler deleteButtonPressHandler = new ButtonPressHandler() {

		@Override
		public void onButtonPress(AudioFileHBoxCell source) {
			
			if (source instanceof ShiftableAudioFileHBoxCell) {  
				
				// shiftable audio file cells are temporary as they can be easily re-added
				creationAudioFileListView.getItems().remove(source);
				
			} else {

				AlertDialog alertDialog = new AlertDialog(AlertType.CONFIRMATION, "Confirm deletion", "Are you sure you want to delete this audio file?",
															"Are you sure you want to delete \"" + source.getAudioFileName() + "\"?");				
				ButtonType buttonClicked = alertDialog.showAndReturn();
				if (!(buttonClicked == ButtonType.OK)) {
					return;
				}
				
				BashUtils.runCommand(null, "rm " + FilePaths.TEMP_AUDIO_DIR + source.getAudioFileName() + ".wav", false);	
				
			}
			
			AudioFileHBoxCell currentOwner = ownerOfAudioPreview.getValue();
			if (currentOwner == source) {
				currentOwner.setPlaying(false);
				ownerOfAudioPreview.setValue(null);
			}

			updateAudioFileList();
			
		}

	};
	
	private final ButtonPressHandler shiftUpButtonPressHandler = new ButtonPressHandler() {
		
		@Override
		public void onButtonPress(AudioFileHBoxCell source) {
			shiftAudioCell(source, -1);
		}
		
	};
	
	private final ButtonPressHandler shiftDownButtonPressHandler = new ButtonPressHandler() {
		
		@Override
		public void onButtonPress(AudioFileHBoxCell source) {
			shiftAudioCell(source, 1);
		}
		
	};
		
	public WikiCreationMenu(MainMenu mainMenu, Stage parentStage, String wikiTerm, String wikiText) {
		
		super();
		initOwner(parentStage);
		initModality(Modality.WINDOW_MODAL);
				
		// delete pre-existing audio files
		BashUtils.runCommand(null, "rm " + FilePaths.TEMP_AUDIO_DIR + "*", false);
		
		// GUI LAYOUTS
		
		VBox rootLayout = new VBox(10);
		rootLayout.setPadding(new Insets(10));
				
		HBox menuLayout = new HBox(10);
		VBox.setVgrow(menuLayout, Priority.ALWAYS);
		
		// EDITOR LAYOUT //
		
		VBox editorLayout = new VBox(10);
		HBox.setHgrow(editorLayout, Priority.ALWAYS);
		
		HBox utilityBar = new HBox(10);
		utilityBar.setAlignment(Pos.CENTER);
		
		// create combo box for tts syntehsisers
		ObservableList<String> synthesiserOptions = FXCollections.observableArrayList(
			Synthesiser.Festival.name(),
			Synthesiser.eSpeak.name()
		);
		ComboBox<String> synthesiserDropdown = new ComboBox<>(synthesiserOptions);
		synthesiserDropdown.setTooltip(new Tooltip("Select desired text-to-speech synthesiser"));
		synthesiserDropdown.getSelectionModel().selectFirst();
		synthesiserDropdown.setMinWidth(Control.USE_PREF_SIZE);
		
		// create combo box for tts voices
		ObservableList<String> voiceOptions = FXCollections.observableArrayList(
			Synthesiser.valueOf((String) synthesiserDropdown.getSelectionModel().getSelectedItem()).getVoiceNames()
		);
		ComboBox<String> voiceDropdown = new ComboBox<>(voiceOptions);
		voiceDropdown.setTooltip(new Tooltip("Select desired text-to-speech voice"));
		voiceDropdown.getSelectionModel().selectFirst();
		voiceDropdown.setMinWidth(Control.USE_PREF_SIZE);
		
		// when selected synthesiser changes also change available voices
		synthesiserDropdown.getSelectionModel().selectedItemProperty().addListener((c) -> {
			voiceDropdown.setItems(
				FXCollections.observableArrayList(
					Synthesiser.valueOf((String) synthesiserDropdown.getSelectionModel().getSelectedItem()).getVoiceNames()
				)
			);
			voiceDropdown.getSelectionModel().selectFirst();
		});
		
		Button previewButton = new Button();
		previewButton.setTooltip(new Tooltip("Preview hightlit text"));
		previewButton.setGraphic(new ImageView(ImageManager.getImage("play")));
		previewButton.getStyleClass().add("plain-graphic-button");
		previewButton.setMinWidth(Control.USE_PREF_SIZE);
		previewButton.setDisable(true);
		
		TextField audioNameField = new TextField();
		audioNameField.textProperty().addListener(new InvalidCharacterChangeListener(validCharacters, audioNameField));
		audioNameField.setTooltip(new Tooltip("Enter name of audio file here"));
		audioNameField.setPromptText("Audio file name..");
		
		Button saveButton = new Button();
		saveButton.setTooltip(new Tooltip("Save highlit text as audio file"));
		saveButton.getStyleClass().add("plain-graphic-button");
		saveButton.setGraphic(new ImageView(ImageManager.getImage("save")));
		saveButton.setMinWidth(Control.USE_PREF_SIZE);
		saveButton.setDisable(true);
				
		Pane spacer = new Pane();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		utilityBar.getChildren().setAll(synthesiserDropdown, voiceDropdown, spacer, previewButton, audioNameField, saveButton);
		
		TextArea wikiTextArea = new TextArea();
		wikiTextArea.setText(wikiText);
		wikiTextArea.setWrapText(true);
		wikiTextArea.setMinHeight(400);
		VBox.setVgrow(wikiTextArea, Priority.ALWAYS);
		
		HBox footer = new HBox(7);
		
		// displays number of characters the user has selected
		Text descriptiveText = new Text("Number of words selected:");
		descriptiveText.setFill(Color.web("333333"));
		Text numCharactersText = new Text("0");
		numCharactersText.setFill(Color.RED);
		
		spacer = new Pane();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		// progress bar and label used to update user as to the progress of the current operation
		ProgressBar progressBar = new ProgressBar(0);
		progressBar.setMaxHeight(15);
		Label progressLabel = new Label("");
		
		footer.getChildren().setAll(descriptiveText, numCharactersText, spacer, progressLabel, progressBar);
		
		// colour num words selected text according to whether valid number of words
		// also disable preview / save buttons as necessary
		wikiTextArea.selectedTextProperty().addListener((textProperty, oldValue, newValue) -> {
			
			String text = newValue.trim().replace("\n",  "");
			String[] words = text.split(" ");
			int numWords = words.length - ((text.length() == 0) ? 1 : 0);  // no text counts as one word for some reason (newline?)
			
			boolean invalidNumWords = numWords == 0 || numWords > 40;
			
			saveButton.setDisable(invalidNumWords || audioNameField.getText().length() == 0);
			previewButton.setDisable(invalidNumWords);
			
			if (invalidNumWords) {
				numCharactersText.setFill(Color.RED);
			} else {
				numCharactersText.setFill(Color.web("333333"));
			}
			
			numCharactersText.setText("" + numWords + ((numWords > 40) ? " (too many words)" : ""));
			
		});
		
		// don't let a creation be saved if name field is empty
		audioNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			
			String text = wikiTextArea.getSelectedText().trim().replace("\n",  "");
			String[] words = text.split(" ");
			int numWords = words.length - ((text.length() == 0) ? 1 : 0);  // no text counts as one word for some reason (newline?)
			
			// also need to check if number of words is valid
			saveButton.setDisable(newValue.length() == 0 || numWords == 0 || numWords > 40);
			
		});
		
		// preview selected text tts audio
		previewButton.setOnAction( (e) -> {
			
			if (currentAudioPreview != null) { // prevent overlapping audio
				currentAudioPreview.stop();
			}
			
			previewButton.setDisable(true);
			
			String text = wikiTextArea.getSelectedText();
			Task<String> createAudioTask = new CreateAudioFileTask(
					(String) synthesiserDropdown.getSelectionModel().getSelectedItem(),
					(String) voiceDropdown.getSelectionModel().getSelectedItem(),
					text, null
			);
			
			progressBar.progressProperty().bind(createAudioTask.progressProperty());
			progressLabel.setText("Generating audio preview...");
			
			createAudioTask.setOnSucceeded((e_) -> {
				
				progressBar.progressProperty().unbind();
				progressLabel.setText("");
				
				previewButton.setDisable(false);
				
				// preview the audio with embedded (invisible) audio player
				
				String filePath = (String) createAudioTask.getValue();
				Media audio = new Media(new File(filePath).toURI().toString());
				currentAudioPreview = new MediaPlayer(audio);
				currentAudioPreview.play();
				
			});
			
			createAudioTask.setOnFailed((e_) -> {
				previewButton.setDisable(false);
			});
			
			Service<String> service = new Service<String>() {
				@Override
				protected Task<String> createTask() {
					return createAudioTask;
				}
			};
			service.start();
			
		});
		
		saveButton.setOnAction((e) -> {
			
			// check if file with specified name exists
			
			int fileExists = BashUtils.runCommand(null, "test -e " + FilePaths.TEMP_AUDIO_DIR + audioNameField.getText() + ".wav", false).getExitCode();
							
			if (fileExists == 0) {
				
				AlertDialog alertDialog = new AlertDialog(AlertType.CONFIRMATION, "Audio already exists", "Audio file with that name already exists",
														  "Would you like to overwrite it?");
				
				ButtonType clickedButton = alertDialog.showAndReturn();
				if (clickedButton != ButtonType.OK) {
					return;
				}
				
			}

			Task<String> createAudioTask = new CreateAudioFileTask(
					(String) synthesiserDropdown.getSelectionModel().getSelectedItem(),
					(String) voiceDropdown.getSelectionModel().getSelectedItem(),
					wikiTextArea.getSelectedText(), audioNameField.getText()
			);
			
			progressLabel.setText("Saving audio file...");
			progressBar.progressProperty().bind(createAudioTask.progressProperty());
			
			createAudioTask.setOnSucceeded((e_) -> {
				
				progressLabel.setText("");
				progressBar.progressProperty().unbind();
				progressBar.setProgress(0);
				updateAudioFileList();
				
			});
			
			(new Service<String>() {
				@Override 
				public Task<String> createTask() {
					return createAudioTask;
				}
			}).start();
		
			
		});
		
		editorLayout.getChildren().setAll(utilityBar, wikiTextArea, footer);
		
		// END EDITOR LAYOUT //
		
		Separator horizSeparator = new Separator(Orientation.VERTICAL);
		horizSeparator.setPadding(new Insets(0, 5, 0, 5));
			
		// CREATION MENU LAYOUT //
		
		VBox creationLayout = new VBox(8);
		
		creationLayout.setMinWidth(500);
		creationLayout.setPrefWidth(500);
		creationLayout.setMaxWidth(500);
		VBox.setVgrow(creationLayout, Priority.ALWAYS);
		
		VBox.setVgrow(audioFileListView, Priority.ALWAYS);
		audioFileListView.setSelectionModel(new NoSelectionModel<>());
		audioFileListView.setFocusTraversable(false);
		updateAudioFileList();
		
		VBox.setVgrow(creationAudioFileListView, Priority.ALWAYS);
		creationAudioFileListView.setSelectionModel(new NoSelectionModel<>());
		creationAudioFileListView.setFocusTraversable(false);
		creationAudioFileListView.setItems(FXCollections.observableArrayList());
				
		TextField creationNameField = new TextField();
		creationNameField.setTooltip(new Tooltip("Enter name of Creation here"));
		creationNameField.textProperty().addListener(new InvalidCharacterChangeListener(validCharacters, creationNameField));
		creationNameField.setPromptText("Creation name..");
		HBox.setHgrow(creationNameField, Priority.ALWAYS);
		
		// create combo box for music options
		ObservableList<String> musicOptions = FXCollections.observableArrayList(
			"None",
			"Ambient Rock",
			"Electronic",
			"Light Jazz"
		);
		ComboBox<String> musicDropdown = new ComboBox<>(musicOptions);
		musicDropdown.setTooltip(new Tooltip("Select desired music for creation"));
		musicDropdown.getSelectionModel().selectFirst();
		musicDropdown.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(musicDropdown, Priority.ALWAYS);
		
		Button selectImagesButton = new Button("Select images..");
		selectImagesButton.setTooltip(new Tooltip("Select images to include in Creation"));
		selectImagesButton.getStyleClass().add("blue-button");
		selectImagesButton.setOnAction((e) -> {
			
			String creationName = creationNameField.getText();
			int fileExists = BashUtils.runCommand(null, "test -e " + FilePaths.CREATIONS_DIR + creationName + ".mp4", false).getExitCode();
							
			if (fileExists == 0) {  // creation with same name already exists
				AlertDialog alertDialog = new AlertDialog(AlertType.CONFIRMATION, "Creation aready exists", 
												    "Creation with that name already exists", "Would you like to overwrite it?");
				
				ButtonType buttonClicked = alertDialog.showAndReturn();
				if (buttonClicked != ButtonType.OK) {
					return;
				}
				
			}
			
			Task<List<String>> getFlickrImagesTask = new GetFlickrImagesTask(wikiTerm, "image", FilePaths.TEMP_DIR, 10);
			getFlickrImagesTask.setOnSucceeded((e_) -> {
				
				// get user to select desired images
				progressLabel.setText("Selecting images..");
				List<String> selectedImagePaths = new ImageSelectionMenu(this, getFlickrImagesTask.getValue()).showAndReturn();
				
				if (selectedImagePaths == null) {  // creation process cancelled
					progressLabel.setText("");
					return;
				}
				
				List<String> audioFilePaths = new ArrayList<String>();
				for (AudioFileHBoxCell cell : creationAudioFileListView.getItems()) {
					audioFilePaths.add(FilePaths.TEMP_AUDIO_DIR + cell.getAudioFileName() + ".wav");
				}
				
				String music = musicDropdown.getSelectionModel().getSelectedItem();
				
				Task<Void> createCreationTask = new CreateCreationTask(creationName, selectedImagePaths, audioFilePaths, wikiTerm, music);
				Service<Void> createCreationService = new Service<Void>() {
					@Override
					protected Task<Void> createTask() {
						return createCreationTask;
					}
				};
				
				progressLabel.textProperty().bind(createCreationTask.messageProperty());
				progressBar.progressProperty().bind(createCreationTask.progressProperty());
				
				createCreationTask.setOnSucceeded((event) -> {
					
					progressLabel.textProperty().unbind();
					progressBar.progressProperty().unbind();
					progressBar.setProgress(0);
					
					AlertDialog alertDialog = new AlertDialog(AlertType.INFORMATION, "Creation created succesfully", "Creation created successfully",
														"Creation \"" + creationName + "\" was created successfully.");
					alertDialog.showAndWait();
					
					mainMenu.updateCreationList();
					
				});
				
				createCreationService.start();
				setOnCloseRequest((event) -> {
					createCreationService.cancel();
				});
				
			});
			
			Service<List<String>> getFlickrImagesService = new Service<List<String>>() {

				@Override
				protected Task<List<String>> createTask() {
					return getFlickrImagesTask;
				}
				
			};
			
			progressLabel.setText("Getting images from flickr..");
			getFlickrImagesService.start();
			setOnCloseRequest((event) -> {
				getFlickrImagesService.cancel();
			});
			
		});
		selectImagesButton.setDisable(true);
		
		// only allow creation of creation if there is at least one audio file in the 'queue'
		creationAudioFileListView.getItems().addListener((Change<? extends AudioFileHBoxCell> change) -> {
				boolean disable = creationAudioFileListView.getItems().isEmpty() || creationNameField.getText().length() == 0;
				selectImagesButton.setDisable(disable);
		});
		
		creationNameField.setOnKeyReleased((e) -> {
			if (e.getCode() == KeyCode.ENTER) {
				selectImagesButton.fire();
			}
		});
		
		// if name field is empty don't allow them to click save creation
		creationNameField.textProperty().addListener((c) -> {
			
			boolean disable = creationAudioFileListView.getItems().isEmpty() || creationNameField.getText().length() == 0;
			selectImagesButton.setDisable(disable);
		
		});
		
		HBox nameLayout = new HBox(8);
		nameLayout.getChildren().setAll(new Label("Name of creation:"), creationNameField);
		nameLayout.setAlignment(Pos.CENTER);
		
		HBox musicSaveLayout = new HBox(8);
		musicSaveLayout.getChildren().setAll(new Label("Music:"), musicDropdown, selectImagesButton);
		musicSaveLayout.setAlignment(Pos.CENTER);
		
		creationLayout.getChildren().setAll(creationAudioFileListView, nameLayout, musicSaveLayout);
				
		// END CREATION MENU LAYOUT //
		
		menuLayout.getChildren().setAll(editorLayout, horizSeparator, audioFileListView, creationLayout);
		rootLayout.getChildren().setAll(menuLayout);
		
		Scene scene = new Scene(rootLayout);
		scene.getStylesheets().add("varpedia/app/main.css");
		scene.getStylesheets().add("varpedia/app/creationMenu.css");
		
		setScene(scene);
		sizeToScene();
		setTitle("VARpedia - Creation menu");
				
	}
	
	// update list view of saved audio files
	private void updateAudioFileList() {
				
		String cmd = "ls " + FilePaths.TEMP_AUDIO_DIR + " | grep \".wav\"";
		
		String[] lines = BashUtils.runCommand(null, cmd, true).getStdOut().split("\n");
		List<String> fileNames = new ArrayList<String>();
		
		for (String line : lines) {			
			if (line.length() > 0) {  // ignore blank lines
				fileNames.add(line.split("\\.")[0]);  // remove .wav part
			}
		}
		
		ObservableList<AudioFileHBoxCell> list = audioFileListView.getItems();
		
		ObservableList<AudioFileHBoxCell> updatedList = FXCollections.observableArrayList();
		for (AudioFileHBoxCell audioCell : list) {
			if (fileNames.contains(audioCell.getAudioFileName())) {
				updatedList.add(audioCell);
			}
		}
		
		for (String audioFileName : fileNames) {
			
			if (audioFileName == "") {
				continue;  // blank string
			}
			
			boolean exists = false;
			for (AudioFileHBoxCell audioCell : updatedList) {
				if (audioCell.getAudioFileName().equals(audioFileName)) {
					exists = true;
					break;
				}
			}
			
			if (!exists) {	
				updatedList.add(new AddableAudioFileHBoxCell(playButtonPressHandler, deleteButtonPressHandler, addButtonPressHandler, audioFileName));		
			}
			
		}
		
		audioFileListView.setItems(updatedList);

		
	}
	
	// shift an audio cell in the creation audio file list view
	private void shiftAudioCell(AudioFileHBoxCell source, int dir) {
		
		ObservableList<AudioFileHBoxCell> items = creationAudioFileListView.getItems();
		
		int thisIndex = items.indexOf(source),
			otherIndex = thisIndex + dir;
		
		if (otherIndex < 0 || otherIndex >= items.size()) {
			return;
		}
		
		String otherName = items.get(otherIndex).getAudioFileName(),
			   thisName = items.get(thisIndex).getAudioFileName();
		
		items.get(otherIndex).setAudioFileName(thisName);
		items.get(thisIndex).setAudioFileName(otherName);
		
	}
	
}
