package varpedia.app.util;

public class FilePaths {
	
	public static final String ROOT_DIR  = System.getProperty("user.dir") + "/";
	public static final String APP_PACKAGE = ROOT_DIR + "src/varpedia/app/";
	public static final String RESOURCES_PACKAGE = APP_PACKAGE + "resources/";
	public static final String OTHER_DIR = ROOT_DIR + "other/";
	public static final String PROGRAM_FILES_DIR = ROOT_DIR + "program_files/";
	public static final String CREATION_FILES_DIR = PROGRAM_FILES_DIR + "creation_files/";
	public static final String CREATIONS_DIR = CREATION_FILES_DIR + "creations/";
	public static final String SLIDESHOWS_DIR = CREATION_FILES_DIR + "slideshows/";
	public static final String MUSIC_DIR = PROGRAM_FILES_DIR + "music/";
	public static final String TEMP_DIR = PROGRAM_FILES_DIR + "temp/";
	public static final String TEMP_AUDIO_DIR = TEMP_DIR + "audio/";
	public static final String LIBRARIES_DIR = ROOT_DIR + "lib/";
	public static final String TTS_DIR = OTHER_DIR + "tts/";
	public static final String FESTIVAL_VOICES_DIR = TTS_DIR + "festival_voices/";
	
	public static String toRelativePath(String path) {
		
		return path.substring(ROOT_DIR.length(), path.length());
		
	}

}
