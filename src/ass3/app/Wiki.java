package ass3.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;

//This class is using javafx test to generate threading.
class Wiki extends Task<String>{
	private String _key;
	private List<String> _fulltext = new ArrayList<String>();

	public Wiki(String keyword) {
		_key = keyword;	
	}

	@Override
	protected String call() throws Exception {
		Thread.sleep(100);
		//read input word from user
		String command = "wikit " + _key;
		ProcessBuilder pk = new ProcessBuilder("bash", "-c", command);
		Process process = pk.start();
		BufferedReader text = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = text.readLine();

		while (line != null){
			// If Input is invalid, call the alert
			if(line.contains(" not found :^(")) {
				Invalidkeyword i = new Invalidkeyword();
				Platform.runLater(i);
				line = text.readLine();
			}
			else {

				File file = new File("fake"+_key + ".txt");
				List<String> content = new ArrayList<String>();
				content.add(line);

				//split text into lines
				String[] outputText = content.get(0).split("(?<=[a-z])\\.\\s+");

				int numofLine = outputText.length;
				for(int i = 0; i < numofLine; i++) {
					_fulltext.add((i+1)  + ". " + outputText[i]+ "\n");
				}
				
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				for(int i = 0; i < numofLine; i++) {
					bw.write((i+1)  + ". " + outputText[i]+ "\n");	  	
				}
				bw.close();  
				line = null;
				Validkeyword j = new Validkeyword(_fulltext);
				Platform.runLater(j);

				//Delete useless file
				String deletecommand = "rm -f " + file;
				ProcessBuilder delc = new ProcessBuilder("bash", "-c", deletecommand);
				Process dec = delc.start();
				dec.waitFor();

			}
		}
		return null;
	}
}

