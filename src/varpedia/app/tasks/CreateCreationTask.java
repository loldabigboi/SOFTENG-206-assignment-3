package varpedia.app.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javafx.concurrent.Task;
import varpedia.app.util.BashUtils;
import varpedia.app.util.FileManager;
import varpedia.app.util.FilePaths;

public class CreateCreationTask extends Task<Void> {
		
	private List<String> _audioFilePaths;
	private List<String> _imagePaths;
	private String _wikiTerm;
	private String _creationName;
	private String _musicFile;
	
	public CreateCreationTask(String creationName, List<String> imagePaths, List<String> audioFilePaths, String wikiTerm, String music) {
		
		super();
		
		_audioFilePaths = audioFilePaths;
		_wikiTerm = wikiTerm.toLowerCase();
		_creationName = creationName;
		
		_musicFile = music;
		if (music.equals("Ambient Rock")) {
			_musicFile = "Lav - Standing on the edge (Ambient Rock).mp3";
		} else if (music.equals("Electronic")) {
			_musicFile = "Loveshadow - These Tears (Sadness) (Electronic).mp3";
		} else if (music.equals("Light Jazz")) {
			_musicFile = "Panumoon - Another perspective (Light Jazz).mp3";
		}
		_musicFile = _musicFile.replace(' ', '_');
		
		_imagePaths = imagePaths;
		
	}

	@Override
	protected Void call() throws Exception {
		
		String command;
		
		// overwrites by default, so checking for preexisting creations
		// needs to be done elsewhere
		
		updateProgress(0, 5);
		updateMessage("Performing house-cleaning...");
		
		ProcessBuilder pb = new ProcessBuilder();
		
		// delete preexisting creation files
		FileManager.deleteCreation(pb, _creationName);

		// create text file to store paths of audio files
		String textFileContents = "";
		for (String filePath : _audioFilePaths) {
			textFileContents += "file '" + filePath + "'\n";
		}
		BashUtils.runCommand(pb, "echo \"" + textFileContents + "\" > '" + FilePaths.TEMP_DIR + "temp.txt'", false);
		
		updateProgress(1, 5);
		updateMessage("Concatenating audio files");
		
		// concatenate audio files and save
		command = "ffmpeg -f concat -safe 0 -i " + FilePaths.TEMP_DIR + "temp.txt -c copy " + FilePaths.TEMP_DIR + "tts.wav";
		BashUtils.runCommand(pb, command, false);
		
		updateProgress(2, 5);
		updateMessage("Overlaying music..");
		
		// loop music to same length as tts audio
		
		if (_musicFile != "None") {
			
			// create temporary music file with reduced volume
			command = "ffmpeg -i '" + FilePaths.MUSIC_DIR + _musicFile + "' -filter:a \"volume=0.4\" '" + FilePaths.TEMP_DIR + "music.mp3'";
			BashUtils.runCommand(pb, command, false);
			
			// get length of tts audio and music
			// adapted from https://stackoverflow.com/questions/6239350/how-to-extract-duration-time-from-ffmpeg-output
			
			// tts audio
			command = "ffprobe -i '" + FilePaths.TEMP_DIR + "tts.wav' -show_entries format=duration -v quiet -of csv=\"p=0\"";
			float ttsDuration = Float.parseFloat(BashUtils.runCommand(pb, command, true).getStdOut());
		    
			// music audio
			command = "ffprobe -i '" + FilePaths.TEMP_DIR + "music.mp3' -show_entries format=duration -v quiet -of csv=\"p=0\"";
			float musicDuration = Float.parseFloat(BashUtils.runCommand(pb, command, true).getStdOut());
		    		    
		    int numLoops = (int) (ttsDuration / musicDuration);
		    
		    if (numLoops > 0) {
		    	
			    // create txt file to use with ffmpeg concat, adapted from https://superuser.com/questions/820830/loop-audio-file-to-a-given-length
			    textFileContents = "";
			    for (int i = 0; i < numLoops; i++) {
			    	textFileContents += "file '" + FilePaths.TEMP_DIR + "music.mp3'\n";
			    }
			    command = "echo \"" + textFileContents + "\" > '" + FilePaths.TEMP_DIR + "temp.txt'";
			    BashUtils.runCommand(pb, command, false);
			    
			    command = "ffmpeg -t " + ttsDuration + " -f concat -i '" + FilePaths.TEMP_DIR + "temp.txt' -c copy -t " + ttsDuration + " temp/music.mp3";
			    BashUtils.runCommand(pb, command, false);
			    
			    command = "ffmpeg -i '" + FilePaths.TEMP_DIR + "tts.wav' -i '" + FilePaths.TEMP_DIR + "/music.mp3' -filter_complex amix=inputs=2:duration=longest '" + FilePaths.TEMP_DIR + "audio.wav'";
			    
		    } else {
		    	command = "ffmpeg -i '" + FilePaths.TEMP_DIR + "tts.wav' -i '" + FilePaths.TEMP_DIR + "music.mp3' " + 
		    			  "-filter_complex amix=inputs=2:duration=shortest '" + FilePaths.TEMP_DIR + "audio.wav'";
		    }
		    
		    // merge tts audio and music audio
		    		    
		    BashUtils.runCommand(pb, command, false).getStdOut();
		    
		} else {
			
			// rename tts audio file
			BashUtils.runCommand(pb, "mv " + FilePaths.TEMP_DIR + "/tts.wav " + FilePaths.TEMP_DIR + "audio.wav", false);
			
		}
				
		updateProgress(3, 5);
		updateMessage("Creating slideshow...");
				
		// get length of audio to help determine required slideshow framerate (image duration)
		File file = new File(FilePaths.TEMP_DIR + "audio.wav");
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
		AudioFormat format = audioInputStream.getFormat();
		long frames = audioInputStream.getFrameLength();
		double durationInSeconds = (frames+0.0) / format.getFrameRate();
		
		// create slideshow
		String frameRate = Double.toString( 1 / (durationInSeconds / _imagePaths.size()) );
		String imageFilesString = "";
		for (String imagePath : _imagePaths) {
			imageFilesString += imagePath + " ";
		}
		imageFilesString.trim();
		imageFilesString += "";
		command = "cat " + imageFilesString + " | ffmpeg -framerate " + frameRate + " -f image2pipe -i - -vf scale=-2:400 -r 25 '" + FilePaths.TEMP_DIR + "slideshow.mp4'";
		BashUtils.runCommand(pb, command, false);
		
		// merge video and audio
		command = "ffmpeg -i '" + FilePaths.TEMP_DIR + "slideshow.mp4' -i '" + FilePaths.TEMP_DIR + "audio.wav' " + 
				  "-c:v copy -c:a aac -strict experimental '" + FilePaths.SLIDESHOWS_DIR + _creationName + ".mp4'";
		BashUtils.runCommand(pb, command, false);
		
		// overlay text on slideshow
		command = "ffmpeg -i " + FilePaths.SLIDESHOWS_DIR + _creationName + ".mp4 -vf \"drawtext=fontfile=comicsans.ttf:fontsize=30:" +
				  "fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _wikiTerm + "'\" " + FilePaths.CREATIONS_DIR + _creationName + ".mp4";
		BashUtils.runCommand(pb, command, false);

		updateProgress(4, 5);
		updateMessage("Merging slideshow and audio...");
		
		// append search term information to txt file containing other search terms
		command = "printf '" + _creationName + ":" + _wikiTerm + "\n' >> '" + FilePaths.CREATION_FILES_DIR + "search_terms.txt'";
		BashUtils.runCommand(pb, command, false);
		
		// clean up temp dir
		FileManager.cleanTempDirectory(pb);
		
		updateProgress(5, 5);
		updateMessage("Finished!");
		
		return null;
			
	}
	
	

}
