package varpedia.app.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.concurrent.Task;
import varpedia.app.util.BashUtils;

public class WikiTextRetrievalTask extends Task<String>{
	
	private String _searchTerm;

	public WikiTextRetrievalTask(String searchTerm) {
		_searchTerm = searchTerm;	
	}

	@Override
	protected String call() throws Exception {
		
		// Cannot use BashUtils for this as if there are ambiguous results then reading all lines of output will cause task to halt,
		// as wikit waits for the user to say what action they want to take. This can be circumvented by checking each individual
		// as it is read from standard output, hence we must use ProcessBuilder directly.
		
		ProcessBuilder pb = new ProcessBuilder("wikit", _searchTerm);
		Process p = pb.start();
		
		BufferedReader stdOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		   
		String line, wikiText = "";
		while ((line = stdOutputReader.readLine()) != null) {
			if (line.contains("Ambiguous results, \"")) {
				throw new AmbiguousResultsException();
			} else if (line.contains(" not found :^(")) {
				throw new NoWikiEntryFoundException();
			}
			wikiText += line + "\n";
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