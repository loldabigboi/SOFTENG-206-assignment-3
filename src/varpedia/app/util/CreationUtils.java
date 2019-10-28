package varpedia.app.util;

import java.util.ArrayList;
import java.util.List;

public class CreationUtils {
	
	public static String getCreationWikiTerm(String creationName) {
		
		String[] lines = BashUtils.runCommand(null, "cat " + FilePaths.CREATION_FILES_DIR + "search_terms.txt", true).getStdOut().split("\n");
		for (String line : lines) {
			
			String trimmedLine = line.trim();
			if (trimmedLine != "") {
				
				String[] segments = trimmedLine.split(":");
				String name = segments[0];
				
				if (segments.length > 1 && name.equals(creationName)) {
					return segments[1];  // wiki term
				}
				
			}
			
		}
		
		return null;  // could not find creation name
		
	}
	
	public static List<String> getCreationNames(String keyword) {
		
		// Get the list of creation file names
		String listCommand = "ls " + FilePaths.CREATIONS_DIR + " | grep mp4 | sort";// | cut -d'.' -f1";
		String output = BashUtils.runCommand(null, listCommand, true).getStdOut();
		String[] fileNames = output.split(".mp4");
		
		// filter by keyword
		List<String> filteredFileNames = new ArrayList<>();
		for (String fileName : fileNames) {
			String trimmedFileName = fileName.trim();
			if (trimmedFileName.length() > 0 && trimmedFileName.contains(keyword)) {
				filteredFileNames.add(trimmedFileName);
			}
		}
		
		return filteredFileNames;
		
	}

}
