package aohara.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionParser {
	
	private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(\\.|-)(\\d+)((\\.|-)\\d+)*");
	private static final int MAX_PERIODS = 2;
	
	public static String parseVersionString(String string){
		if (string == null){
			return null;
		}
		
		Matcher m = VERSION_PATTERN.matcher(string);
		if (m.find()){
			return m.group().replace("-", ".");
		}
		return null;
	}

}
