package ass3.app.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javafx.concurrent.Task;

public class CreateCreationTask extends Task<Void> {
		
	private List<String> _audioFilePaths;
	private String _wikiTerm;
	private String _creationName;
	private String _musicFile;
	private int _numImages;
	
	public CreateCreationTask(String creationName, List<String> audioFilePaths, String wikiTerm, String musicFile, int numImages) {
		
		super();
		
		_audioFilePaths = audioFilePaths;
		_wikiTerm = wikiTerm;
		_creationName = creationName;
		_musicFile = musicFile.replace(' ', '_');
		_numImages = numImages;
		
	}

	@Override
	protected Void call() throws Exception {
		
		String command;
		
		// overwrites by default, so checking for preexisting creations
		// needs to be done elsewhere
		
		updateProgress(0, 5);
		updateMessage("Performing house-cleaning...");
		
		// delete preexisting temp and creation files
		command = "rm creations/" + _creationName + ".mp4 " + 
				  "creation_audios/"   + _creationName + ".wav " + 
				  "slideshows/" 	   + _creationName + ".mp4 " +
				  "temp/*";
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
		System.out.println(pb.start().waitFor());

		// create text file to store paths of audio files
		String textFileContents = "";
		for (String filePath : _audioFilePaths) {
			textFileContents += "file '" + filePath + "'\n";
		}
		
		command = "echo \"" + textFileContents + "\" > temp/temp.txt";
		pb.command("bash", "-c", command);
		System.out.println(pb.start().waitFor());
		
		updateProgress(1, 5);
		updateMessage("Concatenating audio files");
		
		// concatenate audio files and save
		command = "ffmpeg -f concat -i temp/temp.txt -c copy creation_audios/" + _creationName + ".wav";
		pb.command("bash", "-c", command);
		System.out.println(pb.start().waitFor());
		
		updateProgress(2, 5);
		updateMessage("Retrieving images...");
		
		// loop music to same length as tts audio
		
		if (_musicFile != "None") {
			
			// create temporary music file with reduced volume
			command = "ffmpeg -i \"music/" + _musicFile + "\" -filter:a \"volume=0.4\" temp/music.mp3";
			pb.command("bash", "-c", command);
			pb.start().waitFor();
			
			// get length of tts audio and music
			// adapted from https://stackoverflow.com/questions/6239350/how-to-extract-duration-time-from-ffmpeg-output
			
			// tts audio
			command = "ffprobe -i \"creation_audios/" + _creationName + ".wav\" -show_entries format=duration -v quiet -of csv=\"p=0\"";
			pb.command("bash", "-c", command);
			Process p = pb.start();
			System.out.println(p.waitFor());
			
			BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
			float ttsDuration = Float.parseFloat(out.readLine());
		    
			// music audio
			command = "ffprobe -i temp/music.mp3 -show_entries format=duration -v quiet -of csv=\"p=0\"";
			pb.command("bash", "-c", command);
			p = pb.start();
			System.out.println(p.waitFor());
			
			out = new BufferedReader(new InputStreamReader(p.getInputStream()));
			float musicDuration = Float.parseFloat(out.readLine());
		    		    
		    int numLoops = (int) (ttsDuration / musicDuration);
		    
		    if (numLoops > 0) {
		    	
			    // create txt file to use with ffmpeg concat, adapted from https://superuser.com/questions/820830/loop-audio-file-to-a-given-length
			    textFileContents = "";
			    for (int i = 0; i < numLoops; i++) {
			    	textFileContents += "file 'temp/music.mp3'\n";
			    }
			    command = "echo \"" + textFileContents + "\" > temp/temp.txt";
			    pb.command("bash", "-c", command);
			    System.out.println(pb.start().waitFor());
			    
			    command = "ffmpeg -t " + ttsDuration + " -f concat -i temp/temp.txt -c copy -t " + ttsDuration + " temp/music.mp3";
			    pb.command("bash", "-c", command);
			    System.out.println(pb.start().waitFor());
			    
			    command = "cp \"creation_audios/" + _creationName + ".wav\" temp/tts.wav ; rm \"creation_audios/" + _creationName + ".wav\" ; " + 
			    		  "ffmpeg -i temp/music.wav -i temp/music.mp3 -filter_complex amix=inputs=2:duration=longest \"creation_audios/" + _creationName + ".wav\"";
			    
		    } else {
		    	command = "ffmpeg -i \"creation_audios/" + _creationName + ".wav\" -i temp/music.mp3 " + 
		    			  "-filter_complex amix=inputs=2:duration=shortest \"creation_audios/" + _creationName + ".mp3\"";
		    }
		    
		    // merge tts audio and music audio
		    		    
		    pb.command("bash", "-c", command);
		    p = pb.start();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	System.out.println(line);
		    }
		    System.out.println(p.waitFor());
		    
		}
		
		// get images from flickr
		List<String> filePaths = FlickrUtils.getImages(_numImages, _wikiTerm, "image", "temp");
				
		updateProgress(3, 5);
		updateMessage("Creating slideshow...");
		
		// get length of audio to help determine required slideshow framerate (image duration)
		File file = new File("creation_audios", _creationName + ".wav");
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
		AudioFormat format = audioInputStream.getFormat();
		long frames = audioInputStream.getFrameLength();
		double durationInSeconds = (frames+0.0) / format.getFrameRate();
		
		// create slideshow
		String frameRate = Double.toString( 1 / (durationInSeconds / _numImages) );
		command = "cat temp/image*.jpg | ffmpeg -framerate " + frameRate + " -f image2pipe -i - -vf scale=-2:400 -r 25 slideshows/" + _creationName + ".mp4";
		pb.command("bash", "-c", command);
		System.out.println(pb.start().waitFor());
		
		// overlay text on slideshow
		command = "ffmpeg -i slideshows/" + _creationName + ".mp4 -vf \"drawtext=fontfile=comicsans.ttf:fontsize=30:" +
				  "fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _wikiTerm + "'\" temp/textslideshow.mp4";
		pb.command("bash", "-c", command);
		System.out.println(pb.start().waitFor());

		updateProgress(4, 5);
		updateMessage("Merging slideshow and audio...");
		
		// merge video and audio
		command = "ffmpeg -i temp/textslideshow.mp4 -i creation_audios/" + _creationName + 
				  ".mp3 -c:v copy -c:a aac -strict experimental creations/" + _creationName + ".mp4";
		pb.command("bash", "-c", command);
		System.out.println(pb.start().waitFor());
		
		// save txt file with search term
		command = "echo \"" + _wikiTerm + "\" > search_terms/" + _creationName + ".txt";
		pb.command("bash", "-c", command);
		System.out.println(pb.start().waitFor());
		
		// clean up temp dir
		pb.command("rm", "temp/*");
		pb.start().waitFor();
		
		updateProgress(5, 5);
		updateMessage("Finished!");
		
		return null;
			
	}
	
	

}
