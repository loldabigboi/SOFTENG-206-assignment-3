package varpedia.app.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.application.Platform;
import javafx.concurrent.Task;
import varpedia.app.util.BashUtils;

public class WikiTextRetrievalTask extends Task<String>{
	
	private String _searchTerm;

	public WikiTextRetrievalTask(String searchTerm) {
		_searchTerm = searchTerm;	
	}

	@Override
	protected String call() throws Exception {

		String wikiText = BashUtils.runCommand(null, "wikit " + _searchTerm, true).getStdOut();
		
		if (wikiText.contains(" not found :^(")) {
			throw new NoWikiEntryFoundException();
		} else if (wikiText.contains("Ambiguous results, \"")) {
			throw new AmbiguousResultsException();
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