package varpedia.app.util;

public class CreationUtils {
	
	public static String getCreationWikiTerm(String creationName) {
		
		String[] lines = BashUtils.runCommand(null, "cat " + FilePaths.CREATION_FILES_DIR + "search_terms.txt", true).getStdOut().split("\n");
		for (String line : lines) {
			
			String trimmedLine = line.trim();
			if (trimmedLine != "") {
				
				String[] segments = trimmedLine.split(":");
				String name = segments[0];
				
				if (segments.length == 1) {
					System.out.println("output: " + segments[0]);
				}
				
				if (segments.length > 1 && name.equals(creationName)) {
					return segments[1];  // wiki term
				}
				
			}
			
		}
		
		return null;  // could not find creation name
		
	}

}
