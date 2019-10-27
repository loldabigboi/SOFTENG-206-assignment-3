package varpedia.app.stages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import varpedia.app.components.MediaPlayerComponent;
import varpedia.app.util.BashUtils;
import varpedia.app.util.CreationUtils;
import varpedia.app.util.FilePaths;

public class QuizWindow extends Stage {
	
	private int _numQuestions;
	private IntegerProperty _currentQuestion;
	
	private MediaPlayerComponent _mediaPlayer;
	
	private List<QuestionDetails> _questionDetailsList;
			
	public QuizWindow(Stage parentStage) {
		
		super();
		
		this.initOwner(parentStage);
		this.initModality(Modality.APPLICATION_MODAL);
		this.setTitle("VARpedia - Quiz options");
		
		switchScene(new Scene(createOptionSelectionLayout()));
		this.setHeight(250);
		this.setResizable(false);
		this.setOnCloseRequest((e) -> {
			if (_mediaPlayer != null) {
				_mediaPlayer.stopMedia();
			}
		});
		
	}
	
	private VBox createOptionSelectionLayout() {
		
		VBox layout = new VBox(25);
		layout.setPadding(new Insets(30));
		layout.setAlignment(Pos.CENTER);
		
		Label descriptionLabel = new Label("Please enter the number of questions you want to appear in the quiz:");
		descriptionLabel.getStyleClass().add("heading3");
		descriptionLabel.setMinWidth(Control.USE_COMPUTED_SIZE);
		
		Slider numQuestionsSlider = new Slider();
		numQuestionsSlider.setBlockIncrement(1);
		numQuestionsSlider.setMajorTickUnit(1);
		numQuestionsSlider.setMinorTickCount(0);
		numQuestionsSlider.setShowTickLabels(true);
		numQuestionsSlider.setSnapToTicks(true);
		numQuestionsSlider.setMin(1);
		numQuestionsSlider.setMax(15);
		numQuestionsSlider.setMaxWidth(Double.MAX_VALUE);
		
		HBox buttonLayout = new HBox(10);
		Button confirmButton = new Button("Create quiz");
		confirmButton.getStyleClass().add("blue-button");
		confirmButton.setOnAction((e) -> {
			
			_numQuestions = (int) numQuestionsSlider.getValue();
			_questionDetailsList = new ArrayList<QuestionDetails>();
			
			// Get the list of creation file names
			String listCommand = "ls " + FilePaths.CREATIONS_DIR + " | grep mp4 | sort";
			String output = BashUtils.runCommand(null, listCommand, true).getStdOut();
			String[] fileNames = output.split(".mp4");
			
			List<String> tempList = new ArrayList<String>(Arrays.asList(fileNames));
			
			int max = _numQuestions;
			for (int i = 0; i < max; i++) {
				String name = tempList.get((int) (Math.random() * tempList.size())).trim();
				if (name.length() == 0) {
					max++;  // skip
				} else {
					_questionDetailsList.add(new QuestionDetails(name, CreationUtils.getCreationWikiTerm(name)));
				}
			}
			
			_currentQuestion = new SimpleIntegerProperty(1);
			
			switchScene(new Scene(createQuizLayout()));
			
		});
		
		Button cancelButton = new Button("Back to main menu");
		cancelButton.setOnAction((e) -> {
			this.close();
		});
		cancelButton.getStyleClass().add("plain-text-button");
		
		buttonLayout.getChildren().setAll(confirmButton, cancelButton);
		buttonLayout.setAlignment(Pos.CENTER);
		
		layout.getChildren().setAll(descriptionLabel, numQuestionsSlider, buttonLayout);
		
		return layout;
		
	}
	
	public VBox createQuizLayout() {
				
		VBox layout = new VBox(25);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(25));
		
		Label headerText = new Label("Question 1/" + _numQuestions);
		headerText.getStyleClass().add("heading2");
		
		MediaPlayerComponent mediaPlayer = new MediaPlayerComponent();
		mediaPlayer.playMedia(FilePaths.SLIDESHOWS_DIR + _questionDetailsList.get(0).creationName + ".mp4");
		mediaPlayer.setPrefWidth(600);
		mediaPlayer.setPrefHeight(400);
		
		TextField answerTextField = new TextField();
		answerTextField.setMinWidth(150);
		answerTextField.setMaxWidth(150);
		answerTextField.setPromptText("Enter answer here..");
		
		TextFlow answerFeedbackText = new TextFlow();
		answerFeedbackText.setMinHeight(20);
		answerFeedbackText.setMaxHeight(20);
		answerFeedbackText.getStyleClass().add("heading3");
		answerFeedbackText.setTextAlignment(TextAlignment.CENTER);
		
		_currentQuestion.addListener((obsValue, oldValue, newValue) -> {
			
			headerText.setText("Question " + newValue + "/" + _numQuestions);
			answerTextField.setText("");
			
			mediaPlayer.playMedia(FilePaths.SLIDESHOWS_DIR + _questionDetailsList.get((int) newValue-1).creationName + ".mp4");
			
		});
		
		HBox buttonLayout = new HBox(15);
		buttonLayout.setAlignment(Pos.CENTER);
		
		Button guessButton = new Button("Guess");
		guessButton.getStyleClass().add("blue-button");
		guessButton.setOnAction((e) -> {
			
			int currQuestion = _currentQuestion.getValue();
			QuestionDetails questionDetails = _questionDetailsList.get(currQuestion-1);
			
			String answer = answerTextField.getText().toLowerCase();
			questionDetails.guessedAnswer = answer;
			
			if (answer.equals(questionDetails.wikiTerm)) {
								
				Text feedbackText = new Text("Correct");
				feedbackText.setFill(Color.GREEN);
				answerFeedbackText.getChildren().setAll(feedbackText);
				
			} else {
				
				Text feedbackText = new Text("Incorrect");
				feedbackText.setFill(Color.RED);
				
				Text answerText = new Text(_questionDetailsList.get(currQuestion-1).wikiTerm);
				answerText.setFill(Color.BLUE);
				
				answerFeedbackText.getChildren().setAll(feedbackText, new Text(" - the correct answer was \""), answerText, new Text("\""));
				
			}
			
			if (_currentQuestion.getValue() == _numQuestions) {
				
				mediaPlayer.stopMedia();
				switchScene(new Scene(createQuizFinishedLayout()));
				this.setMinWidth(500);
				
			} else {
				_currentQuestion.setValue(_currentQuestion.getValue() + 1);
			}
			
		});
		
		Button skipButton = new Button("Skip");
		skipButton.getStyleClass().add("plain-text-button");
		skipButton.setOnAction((e) -> {

			int currQuestion = _currentQuestion.getValue();
				
			Text prefixText = new Text("The correct answer was: ");
			Text answerText = new Text(_questionDetailsList.get(currQuestion-1).wikiTerm);
			answerText.setFill(Color.BLUE);
			
			answerFeedbackText.getChildren().setAll(prefixText, answerText);
			
			if (_currentQuestion.getValue() == _numQuestions) {
				
				mediaPlayer.stopMedia();
				switchScene(new Scene(createQuizFinishedLayout()));
				this.setMinWidth(500);
				
			} else {
				_currentQuestion.setValue(_currentQuestion.getValue() + 1);
			}
			
		});
		
		buttonLayout.getChildren().setAll(guessButton, skipButton);
		
		layout.getChildren().setAll(headerText, mediaPlayer, answerFeedbackText, answerTextField, buttonLayout);
		
		return layout;
		
	}
	
	public VBox createQuizFinishedLayout() {
		
		VBox layout = new VBox(25);
		layout.setPadding(new Insets(25));
		layout.setAlignment(Pos.CENTER);
		
		Label totalQuestionsCorrectLabel = new Label();
		totalQuestionsCorrectLabel.getStyleClass().add("heading3");
		int numQuestionsCorrect = 0;
		for (QuestionDetails q : _questionDetailsList) {
			if (q.wikiTerm.equals(q.guessedAnswer)) {
				numQuestionsCorrect++;
			}
		}
		totalQuestionsCorrectLabel.setText("Number of questions correct: " + numQuestionsCorrect + "/" + _numQuestions);
		
		ListView<QuestionDetails> answersListView = new ListView<QuestionDetails>();
		answersListView.setMinWidth(Control.USE_COMPUTED_SIZE);
		ObservableList<QuestionDetails> answersList = FXCollections.observableArrayList(_questionDetailsList);
		
		answersListView.setItems(answersList);
		answersListView.setCellFactory((item) -> {
			return new QuestionCell();
		});
		
		HBox buttonsLayout = new HBox(25);
		buttonsLayout.setAlignment(Pos.CENTER);
		
		Button replayButton = new Button("Generate another quiz..");
		replayButton.getStyleClass().add("blue-button");
		replayButton.setOnAction((e) -> {
			switchScene(new Scene(createOptionSelectionLayout()));
		});
		
		Button quitButton = new Button("Back to main menu");
		quitButton.getStyleClass().add("plain-text-button");
		quitButton.setOnAction((e) -> {
			this.close();
		});
		
		buttonsLayout.getChildren().setAll(replayButton, quitButton);
		
		layout.getChildren().addAll(totalQuestionsCorrectLabel, answersListView, buttonsLayout);
		return layout;
		
	}
	
	private void switchScene(Scene newScene) {
		
		newScene.getStylesheets().add("varpedia/app/main.css");
		newScene.getStylesheets().add("varpedia/app/quizWindow.css");
		this.setScene(newScene);
		this.sizeToScene();
		
	}
	
	private class QuestionDetails {
		
		public String creationName;
		public String wikiTerm;
		public String guessedAnswer;
		
		public QuestionDetails(String creationName, String wikiTerm) {
			
			this.creationName = creationName;
			this.wikiTerm = wikiTerm;
			
		}
		
	}
	
	private class QuestionCell extends ListCell<QuestionDetails> {
		
		@Override
		public void updateItem(QuestionDetails item, boolean empty) {
			
			super.updateItem(item, empty);
			
			if (item != null) {
				
				TextFlow content = new TextFlow();
				Text responseText = new Text();
				
				Text answerText = new Text(item.wikiTerm);
				answerText.setFill(Color.GREEN);

				boolean skipped = item.guessedAnswer == null;
				
				if (item.wikiTerm.equals(item.guessedAnswer)) {
					
					responseText.setText("Correct");
					responseText.setFill(Color.GREEN);
					
					content.getChildren().addAll(responseText, new Text(" - the correct answer was: \""), answerText, new Text("\""));
								
				} else if (skipped){
					
					responseText.setText("Skipped");
					responseText.setFill(Color.BLUE);
					
					content.getChildren().addAll(responseText, new Text(" - the correct answer was: \""), answerText, new Text("\""));
					
				} else {
					
					responseText.setText("Incorrect");
					responseText.setFill(Color.RED);
					
					Text guessText = new Text(item.guessedAnswer);
					guessText.setFill(Color.RED);
					
					content.getChildren().addAll(responseText, new Text(" - you guessed: \""), guessText, 
												 new Text("\", but the correct answer was: \""), answerText, new Text("\""));
										
				}
								
				setGraphic(content);
				
			}
			
		}
		
	}

}
