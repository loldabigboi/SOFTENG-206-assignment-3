package varpedia.app.util;

public class FileManager {
	
	public static int deleteCreation(ProcessBuilder _pb, String creationName) {
		
		ProcessBuilder pb = (_pb == null) ? new ProcessBuilder() : _pb;
		
		// remove search term from search_terms.txt
		String command = "cat " + FilePaths.CREATION_FILES_DIR + "search_terms.txt";
		String[] lines = BashUtils.runCommand(null, command, true).getStdOut().split("\n");
		String newFileContents = "";
		
		for (String line : lines) {
			String trimmedLine = line.trim();
			if (trimmedLine.length() > 0 && !trimmedLine.substring(0, creationName.length()).equals(creationName)) {
				
				String[] lineContents = trimmedLine.split(":");
				if (lineContents[0] != creationName) {  // if this entry is for the specified creation, do not add back to txt file
					newFileContents += trimmedLine + "\n";
				}
				
			}
		}
		
		// replace search terms with edited version
		command = "echo \"" + newFileContents + "\" > " + FilePaths.CREATION_FILES_DIR + "search_terms.txt";
		BashUtils.runCommand(pb, command, false);
		
		command = "rm " + FilePaths.CREATIONS_DIR + creationName + ".mp4 " + 
				  FilePaths.SLIDESHOWS_DIR + creationName + ".mp4 ";
		
		return BashUtils.runCommand(pb, command, false).getExitCode();
		
	}
	
	public static int cleanTempDirectory(ProcessBuilder _pb) {
		
		String command = "rm " + FilePaths.TEMP_DIR + "*";
		ProcessBuilder pb = (_pb == null) ? new ProcessBuilder() : _pb;
		return BashUtils.runCommand(pb, command, false).getExitCode();
		
	}

}
