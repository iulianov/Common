package aohara.common;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

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

	public static String joinStrings(Object[] strings, String joinOn){
		return joinStrings(Arrays.asList(strings), joinOn);
	}
	
	public static String joinStrings(Collection<Object> strings, String joinOn){
		Iterator<Object> iterator = strings.iterator();
		StringBuilder builder = new StringBuilder();
		
		// Append first string if it exists
		if (iterator.hasNext()){
			builder.append(iterator.next());
		}
		
		// For all other strings, append them, with the joinOn character preceeding them
		while (iterator.hasNext()){
			builder.append(joinOn);
			builder.append(iterator.next());
		}
		
		return builder.toString();
	}
}
