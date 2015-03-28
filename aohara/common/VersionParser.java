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
			// Split numbers
			String group = m.group().replace("-", ".");
			List<String> numbers = new ArrayList<>(Arrays.asList(group.split("\\.")));
			
			// If less than 3 numbers, add a third
			if (numbers.size() < MAX_PERIODS + 1){
				numbers.add("0");
			}
			
			// Join numbers back into a version string
			StringBuilder builder = new StringBuilder();
			for (int i=0; i<numbers.size(); i++){
				builder.append(numbers.get(i));
				if (i < MAX_PERIODS){
					builder.append(".");
				}
				
			}

			return builder.toString();
		}
		return null;
	}

}
