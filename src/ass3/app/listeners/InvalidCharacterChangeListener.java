package ass3.app.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class InvalidCharacterChangeListener implements ChangeListener<String> {
	
	String _validCharacters;
	TextInputControl _textInput;
	Tooltip _currentTooltip;
	
	public InvalidCharacterChangeListener(String validCharacters, TextInputControl textInput) {
		_validCharacters = validCharacters;
		_textInput = textInput;
	}

	@Override
	public void changed(ObservableValue observable, String oldValue, String newValue) {
		
		boolean invalidChar = false;
		for (char c : newValue.toCharArray()) {
			if (!_validCharacters.contains(Character.toString(c))) {  // invalid character
				invalidChar = true;
				break;
			}
		}
		
		if (invalidChar) {
			
			_textInput.setText(oldValue);
			showTooltip();
			
		}
		
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
	    _currentTooltip.setAutoHide(true);
	    
	    // generate size of tooltip
	    Stage tempStage = new Stage();
	    tempStage.setScene(new Scene(new Pane(), 500, 500));
	    TextField tempField = new TextField();
	    tempField.setTooltip(_currentTooltip);
	    _currentTooltip.show(tempStage, 0, 0);
	    
	    _textInput.setTooltip(_currentTooltip);
	    _currentTooltip.show(owner, p.getX() + scene.getX() + scene.getWindow().getX() + _textInput.getWidth()/2 - _currentTooltip.getWidth()/2,
	    									   scene.getY() + scene.getWindow().getY() + _textInput.getHeight());
	    _textInput.textProperty().addListener((c) -> {
	    	_currentTooltip.hide();
	    });

	}
	
}


