package ass3.app.listeners;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class InvalidCharacterChangeListenerWithGraphic extends InvalidCharacterChangeListener {
	
	Tooltip _currentTooltip;

	public InvalidCharacterChangeListenerWithGraphic(String validCharacters, TextInputControl textInput) {
		
		super(validCharacters, textInput);
		_currentTooltip = null;
		
		_invalidChar.addListener((obs, oldValue, newValue) -> {
			if (newValue.booleanValue() == true) {
				showTooltip();
			}
		});
		
		_textInput.textProperty().addListener((c) -> {
	    	if (_currentTooltip != null) {
	    		_currentTooltip.hide();
	    	}
	    });
		
	}
	
	
	private void showTooltip() {

		Scene scene = _textInput.getScene();
		Stage owner = (Stage) scene.getWindow();
		
	    Point2D p = _textInput.localToScene(0.0, 0.0);
	    
	    if (_currentTooltip != null) {
	    	_currentTooltip.hide();
	    }
	    _currentTooltip = new Tooltip();
	    _currentTooltip.setText("Valid characters: \"0123456789abcdefghijklmnopqrstuvwxyz ,\".");
	    //_currentTooltip.setAutoHide(true);
	    
	    // generate size of tooltip
	    Stage tempStage = new Stage();
	    Pane root = new Pane();
	    TextField tempField = new TextField();
	    root.getChildren().add(tempField);
	    tempStage.setScene(new Scene(root, 500, 500));
	    tempField.setTooltip(_currentTooltip);
	    
	    _currentTooltip.show(tempStage, 0, 0);
	    
	    _textInput.setTooltip(_currentTooltip);
	    _currentTooltip.show(owner, p.getX() + scene.getX() + scene.getWindow().getX() + _textInput.getWidth()/2 - _currentTooltip.getWidth()/2,
	    									   scene.getY() + scene.getWindow().getY() + _textInput.getHeight());
	    
	    System.out.println(owner);

	}

}
