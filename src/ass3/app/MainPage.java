package ass3.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainPage extends Application{
	class Xcell extends ListCell<String>{
		 HBox hbox = new HBox();
	        Label label = new Label("(empty)");
	        Pane pane = new Pane();
	        Button delete = new Button("(D)");
	        Button play = new Button("(P)");
	        String lastItem;

	        public Xcell() {
	            super();
	            hbox.setSpacing(5.0);
	            hbox.getChildren().addAll(label, pane, delete, play);
	            HBox.setHgrow(pane, Priority.ALWAYS);
	            delete.setOnAction(new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(ActionEvent event) {
	                	delete(lastItem);
	                }
	            });
	            
	            play.setOnAction(new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(ActionEvent event) {
	                    System.out.println(lastItem + " : " + event);
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
	private TextField _wikisearch;

	private TextField _creationsearch;
	
	private Button _wikibutton;

	private Button _creationbutton;
	
	private Label _wikilable;
	
	private Label _creationlable;
	
	private Label _select;
	
	private Button _delete;
	
	private Button _play;
	
	private String _txt = null;
	
	private static File dir = new File(System.getProperty("user.dir"));
	
	private ListView<String> l = new ListView<String>();
	
	private List<String> f = new ArrayList<String>();
	
	private Stage _primary = new Stage();
	public MainPage(){
		_wikibutton = new Button("Search");
		_creationbutton = new Button("Search");
		_wikilable = new Label("Enter key word:");
		_creationlable = new Label("Search creation:");
		_wikisearch = new TextField();
		_creationsearch = new TextField();
		_select = new Label("Your selection ");
		_delete = new Button("Delete");
		_play = new Button("Play");
	}
	
	public Stage getStage() {
		return _primary;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		_primary = primaryStage;
	//	File dir = new File(System.getProperty("user.dir"));
		dir.mkdir();
		AnchorPane pane = new AnchorPane();
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(12);
		
//		_wikisearch.setPromptText("Enter the key word");
	//	_creationsearch.setPromptText("Enter the creation name");
		grid.add(_wikilable, 0, 0);
		grid.add(_creationlable, 0, 1);
		grid.add(_wikisearch, 1, 0);
		grid.add(_creationsearch, 1, 1);
		grid.add(_wikibutton, 2, 0);
		grid.add(_creationbutton, 2, 1);
		
		AnchorPane.setTopAnchor(grid, 14.0);
	    AnchorPane.setLeftAnchor(grid, 28.0);
	    AnchorPane.setRightAnchor(grid, 34.0);
	    
	  //Get the list of creation
	  		String listCommand = "ls " + dir + "/ | grep mp4 | sort | cut -d'.' -f1";
	  		ProcessBuilder list = new ProcessBuilder("bash", "-c", listCommand);
	  		Process listprocess = list.start();
	  		BufferedReader stdout = new BufferedReader(new InputStreamReader(listprocess.getInputStream()));
	  		String line;
	  		List<String> fileList = new ArrayList<String>();
	  		while ((line = stdout.readLine()) != null) {
	  			fileList.add(line);
	  		}
	  		f = fileList;//ok
	    
	    //ListView<String> lvList = new ListView<String>();
	  	ListView<String> lvList = new ListView<String>();
		lvList.getItems().addAll(fileList);
		lvList.setPrefHeight(250.00);
	    lvList.setPrefWidth(385.00);
		lvList.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		lvList.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		l = lvList;
	//	ObservableList<String> selectedCreation = lvList.getSelectionModel().getSelectedItems();
//
//		//When select item from the list
//		lvList.setOnMouseClicked(new EventHandler<MouseEvent>(){
//			@Override
//			public void handle(MouseEvent arg0) {   
//				_select.setText("Selected: " + selectedCreation);
//			}
//		});
//		_select.setWrapText(true);
//
//	    ScrollPane scroll = new ScrollPane();
//	    scroll.setPrefHeight(229.00);
//	    scroll.setPrefWidth(300.00);
//	    scroll.setLayoutX(23.00);
//	    scroll.setLayoutY(147.00);
//	    
       
//
//	    
//        VBox vb = new VBox();
//		vb.setSpacing(25);
//		vb.setPadding(new Insets(0, 20, 10, 20)); 
//		vb.getChildren().addAll(_play, _delete, _select);
//		vb.setLayoutX(370.0);
//		vb.setLayoutX(160.0);
//		AnchorPane.setTopAnchor(vb, 164.0);
//	    AnchorPane.setLeftAnchor(vb, 371.0);
//	    AnchorPane.setRightAnchor(vb, 29.0);
	  		
		StackPane p = new StackPane();
        Scene scene = new Scene(p, 300, 150);
        primaryStage.setScene(scene);
        
        lvList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new Xcell();
            }
        });
        p.getChildren().add(lvList);
        
	  	//	ScrollPane scrollPane = new ScrollPane();
	      //  scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	     AnchorPane.setTopAnchor(p, 100.0);
	    AnchorPane.setLeftAnchor(p, 23.0);
	      //  scrollPane.setPannable(true);
	        
	       // StackPane pane = new StackPane();
	     //   Scene scene = new Scene(pane, 300, 150);
	  //     primaryStage.setScene(scene);
	        p.setPrefHeight(250.00);
		    p.setPrefWidth(400.00);
	        p.setLayoutX(23.00);
		    p.setLayoutY(147.00);
	          // scrollPane.setContent(p);
	        
	        
//	        
//	        VBox creationsPane = new VBox();
//	        creationsPane.setSpacing(8);
//	        creationsPane.setPadding(new Insets(10));
//	        creationsPane.setStyle(
//	                "-fx-border-width: 1;" +
//	                "-fx-border-radius: 4;" +
//	                "-fx-border-color: rgb(170, 170, 170);" +
//	                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 2, 2);");

	      //  scrollPane.setContent(creationsPane);
	    //    VBox.setVgrow(creationsPane, Priority.ALWAYS);

	       // loadCreations();
//
//	        VBox root = new VBox();
//	        root.setPadding(new Insets(10));
//	        root.setSpacing(15);
//	        root.getChildren().addAll(grid, creationsPane);

       
	       
		pane.getChildren().addAll(grid, p);
		Scene s = new Scene(pane,450,400);
		s.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(s);
		primaryStage.show();
		primaryStage.setTitle("Home");
		
		//When click search button
		_wikibutton.setOnAction(new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent event) {
						String inputkey = _wikisearch.getText();
						//check invalid input
						if(!((inputkey.matches(".*[A-Za-z].*"))|| (inputkey.matches("[0-9]*")))) {
							Alert alert1 = new Alert(AlertType.WARNING);
							alert1.setTitle("Warning Dialog");
							alert1.setHeaderText("The input is invalid");
							alert1.setContentText("Please retype!");
							alert1.showAndWait();
						}
						else {
						_txt = _wikisearch.getText();
						Wiki wiki = new Wiki(_txt);
						wiki.setOnSucceeded((e) -> {
							WikiCreationMenu.createWindow(primaryStage, wiki.getValue());
						});
						try { 
							Thread w = new Thread(wiki);
							w.start();
							ProgressBar pb = new ProgressBar();
							
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					}
				});
		
		
		//EventAction of delete button
		_creationbutton.setOnAction(new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent event) {
						String input = _creationsearch.getText();
						System.out.println(input);
						lvList.getSelectionModel().clearSelection();
						lvList.getItems().removeAll(fileList);
						String command = "ls " + dir + "/ | grep .mp4 | grep " + input  + " | sort | cut -d'.' -f1";
						ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
						Process process;
						try {
							process = pb.start();
							BufferedReader number = new BufferedReader(new InputStreamReader(process.getInputStream()));
							String li;
							List<String> fl = new ArrayList<String>();
							while ((li = number.readLine()) != null) {
								fl.add(li);
							}
							f = fl;
							System.out.println(f);
							lvList.getItems().addAll(fl);	
						} catch (IOException e) {
						} 
					}
				});	
	}
	private void delete(String item){

		//pop out the confirmation 
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmate Deletion");
		alert.setHeaderText("Are you sure to delete " + item + ".mp4 :(");
		alert.setTitle("Wanna Delete ???");

		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			String deleteCommand = "rm -f " + dir + "/" + item + ".mp4";
			ProcessBuilder dcProcess = new ProcessBuilder("bash", "-c", deleteCommand);
			try {
				Process deleteprocess = dcProcess.start();
				//Display the original board again
				l.getSelectionModel().clearSelection();
				l.getItems().removeAll(f);
				String listcommand = "ls " + dir + "/ | grep mp4 | sort | cut -d'.' -f1";
				ProcessBuilder p = new ProcessBuilder("bash", "-c", listcommand);
				Process listProcess = p.start();
				BufferedReader stout = new BufferedReader(new InputStreamReader(listProcess.getInputStream()));
				String li;
				List<String> filelist = new ArrayList<String>();
				while ((li = stout.readLine()) != null) {
					filelist.add(li);
				}
				f = filelist;
				l.getItems().addAll(filelist);			
			} catch (Exception e) {
			} 
		}
}
	public static void main(String[] args) {
		launch(args);
	}

}
