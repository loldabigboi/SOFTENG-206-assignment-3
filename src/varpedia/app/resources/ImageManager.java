package varpedia.app.resources;

import javafx.scene.image.Image;
import varpedia.app.util.FilePaths;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public abstract class ImageManager {

    private static final HashMap<String, Image> images = new HashMap<>();

    public static void loadImage(String key, String imagePath, int width, int height) {
    	
        Image img;
		try {
			img = new Image(new FileInputStream(new File(imagePath)), width, height, true, true);
	        images.put(key, img);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

    }

    public static Image getImage(String key) {

        return images.get(key);

    }
    
    public static void init() {
    	
		String resourcesDir = FilePaths.RESOURCES_PACKAGE;
		
		// general button icons
		ImageManager.loadImage("play", 		resourcesDir + "play.png", 15, 15);
		ImageManager.loadImage("stop", 		resourcesDir + "stop.png", 15, 15);
		ImageManager.loadImage("delete",    resourcesDir + "delete.png", 15, 15);
		ImageManager.loadImage("refresh",   resourcesDir + "refresh.png", 15, 15);
		ImageManager.loadImage("search", 	resourcesDir + "search.png", 15, 15);
		ImageManager.loadImage("save", 		resourcesDir + "save.png", 15, 15);
		ImageManager.loadImage("add",	    resourcesDir + "add.png", 15, 15);
		ImageManager.loadImage("shiftDown", resourcesDir + "shiftDownIcon.png", 9, 7);
		ImageManager.loadImage("shiftUp",   resourcesDir + "shiftUpIcon.png", 9, 7);
		
		// media player icons
		ImageManager.loadImage("mediaReplay", 	 resourcesDir + "mediaPlayerReplay.png", 15, 15);
		ImageManager.loadImage("mediaPlay",		 resourcesDir + "mediaPlayerPlay.png", 15, 15);
		ImageManager.loadImage("mediaPause",	 resourcesDir + "mediaPlayerPause.png", 15, 15);
		ImageManager.loadImage("mediaMuted", 	 resourcesDir + "mediaPlayerMuted.png", 15, 15);
		ImageManager.loadImage("mediaNotMuted",  resourcesDir + "mediaPlayerNotMuted.png", 15, 15);
		ImageManager.loadImage("mediaBackwards", resourcesDir + "mediaPlayerBackwards.png", 13, 13);
		
		// misc images
		ImageManager.loadImage("logo", resourcesDir + "logo.png", 210, 210);
		ImageManager.loadImage("error", resourcesDir + "errorIcon.png", 40, 40);
		ImageManager.loadImage("questionMark", resourcesDir + "questionMarkIcon.png", 40, 40);
		ImageManager.loadImage("exclamationMark", resourcesDir + "exclamationMarkIcon.png", 40, 40);
		
    }

}