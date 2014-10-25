package aohara.common.test;

import java.io.IOException;
import java.nio.file.Files;

import aohara.common.config.Config;
import aohara.common.config.ConfigBuilder;

public class TestOptionsWindow {

	public static void main(String[] args) throws IOException{
		ConfigBuilder builder = new ConfigBuilder();
		builder.addIntProperty("age", 22, 0, null, false);
		builder.addTrueFalseProperty("Human", null, false);
		
		Config config = builder.createConfig(Files.createTempFile("temp", ".properties"));
		config.openOptionsWindow(true, true);
		
		System.out.println("You Entered:\n");
		for (String key : config.keySet()){
			System.out.println(String.format("%s: %s", key, config.getProperty(key)));
		}
	}
}
