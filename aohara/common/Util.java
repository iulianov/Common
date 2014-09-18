package aohara.common;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Class containing utility methods that do not yet belon anywhere.
 * 
 * @author Andrew O'Hara
 */
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
