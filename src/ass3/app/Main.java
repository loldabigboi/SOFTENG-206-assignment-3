package ass3.app;
<<<<<<< HEAD
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
=======

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	public static void main(String[] args) {
		
		launch(args);
		
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		
		WikiCreationMenu.createWindow(mainStage);
		
	}

>>>>>>> ded9d20799729f25f394b4bc2c49fc606e402443
}
