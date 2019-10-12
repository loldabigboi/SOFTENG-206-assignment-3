package ass3.app.listeners;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	BooleanProperty _invalidChar;
	
	public InvalidCharacterChangeListener(String validCharacters, TextInputControl textInput) {
		
		_validCharacters = validCharacters;
		_textInput = textInput;
		_invalidChar = new SimpleBooleanProperty(false);
		
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
		
		_invalidChar.setValue(invalidChar);
		
		if (invalidChar) {
			_textInput.setText(oldValue);
		} 
		
	}
	
}


