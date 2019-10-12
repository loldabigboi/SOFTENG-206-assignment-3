package ass3.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.application.Platform;
import javafx.concurrent.Task;

//This class is using javafx test to generate threading.
class Wiki extends Task<String>{
	
	private String _searchTerm;

	public Wiki(String searchTerm) {
		_searchTerm = searchTerm;	
	}

	@Override
	protected String call() throws Exception {

		ProcessBuilder pk = new ProcessBuilder("wikit", _searchTerm);
		Process process = pk.start();
		BufferedReader text = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String wikiText = "";
		String line;

		//read output text from wiki.s
		while ((line = text.readLine()) != null){
			
			if(line.contains(" not found :^(")) {
				throw new NoWikiEntryFoundException();
			} else if (line.contains("Ambiguous results, \"")){
				throw new AmbiguousResultsException();
			} else {
				wikiText += line + System.getProperty("line.separator");
			}
			
		}
		
		return wikiText.trim();
		
	}
	
	public static class NoWikiEntryFoundException extends Exception {

		private static final long serialVersionUID = 1L;	
		
	}
	
	public static class AmbiguousResultsException extends Exception {
		
		private static final long serialVersionUID = 1L;
		
	}
	
}
