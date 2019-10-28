package varpedia.app.tasks;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.Size;

import javafx.concurrent.Task;
import varpedia.app.util.BashUtils;
import varpedia.app.util.FileManager;
import varpedia.app.util.FilePaths;

public class GetFlickrImagesTask extends Task<List<String>> {
		
	private String _searchTerm, _prefix, _dir;
	private int _numImages;
	
	public GetFlickrImagesTask(String searchTerm, String prefix, String dir, int numImages) {
		
		super();
		
		_searchTerm = searchTerm;
		_prefix = prefix;
		_dir = dir;
		_numImages = numImages;
		
	}

	@Override
	protected List<String> call() throws Exception {
		
		try {
			String config = FilePaths.OTHER_DIR + "flickr-api-keys.txt"; 

			File file = new File(config); 
			BufferedReader br = new BufferedReader(new FileReader(file)); 
			
			String line;
			String apiKey = "",
				   sharedSecret = "";
			
			boolean shouldClose = false;
			while ( (line = br.readLine()) != null ) {
				if (line.trim().startsWith("apiKey")) {
					apiKey =  line.substring(line.indexOf("=")+1).trim();
					if (shouldClose) {
						br.close();
						break;
					} else {
						shouldClose = true;
					}
				} else if (line.trim().startsWith("sharedSecret")) {
					sharedSecret = line.substring(line.indexOf("=")+1).trim();
					if (shouldClose) {
						br.close();
						break;
					} else {
						shouldClose = true;
					}
				}
			}
			br.close();
			
			Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
			
			int page = 0;
			
			PhotosInterface photos = flickr.getPhotosInterface();
			SearchParameters params = new SearchParameters();
			params.setSort(SearchParameters.RELEVANCE);
			params.setMedia("photos"); 
			params.setText(_searchTerm);
			
			PhotoList<Photo> results = photos.search(params, _numImages, page);
			
			List<String> filePaths = new ArrayList<String>();
			for (int i = 0; i < results.size(); i++) {
				Photo photo = results.get(i);
				BufferedImage image = photos.getImage(photo,Size.LARGE);
		    	String fileName = _prefix + i + ".jpg";
		    	filePaths.add(_dir + fileName);
		    	File outputfile = new File(_dir, fileName);
		    	ImageIO.write(image, "jpg", outputfile);
			}
			return filePaths;
								
		} catch (IOException | FlickrException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	

}
