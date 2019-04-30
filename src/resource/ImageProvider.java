package resource;

import java.net.URL;

import javax.swing.ImageIcon;

public class ImageProvider {

	private static ImageProvider _instance = new ImageProvider();

	public static final String ICON_WP = "icon_wp.png";

	private ImageProvider() {
	}

	public ImageIcon getImage(String fileName) {
		String path = "/resource/" + fileName;
		URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static ImageProvider getInstance() {
		return _instance;
	}
}
