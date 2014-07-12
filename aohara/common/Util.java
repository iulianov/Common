package aohara.common;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Util {
	
	public static void goToHyperlink(URL url) throws IOException {
		if (Desktop.isDesktopSupported()) {
               try {
				Desktop.getDesktop().browse(url.toURI());
			} catch (URISyntaxException e) {
				throw new IOException(e);
			}
        }
	}

}
