package aohara.common.config.loader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import aohara.common.config.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonConfigLoader extends ConfigLoader {
	
	private static final String KEY = "key", VALUE = "value";
	private static final JsonParser parser = new JsonParser();
	private final Gson gson;
	
	public JsonConfigLoader(Path filePath){
		this(filePath, new GsonBuilder().setPrettyPrinting().create());
	}
	
	public JsonConfigLoader(Path filePath, Gson gson){
		super(filePath);
		this.gson = gson;
	}

	@Override
	public Map<String, String> loadProperties(Config config) {
		Map<String, String> properties = new LinkedHashMap<>();

		for (JsonElement ele : loadConfigProperties()){
			try {
				JsonObject pair  = ele.getAsJsonObject();
				properties.put(
					pair.get(KEY).getAsString(),
					pair.get(VALUE).getAsString()
				);
			} catch (NullPointerException e){
				// Case for invalid config (and legacy support)
				// Do Nothing
			}
		}
		
		return properties;		
	}
	
	@Override
	public void save(Config config) throws IOException {
		// Convert Properties to JSON
		JsonArray properties = new JsonArray();
		for (String key : config.keySet()){
			JsonObject pair = new JsonObject();
			pair.addProperty(KEY, key);
			pair.addProperty(VALUE, config.getProperty(key));
			properties.add(pair);
		}
		
		// Save JsonArray to disk
		saveConfigProperties(properties);
	}
	
	private void saveConfigProperties(JsonArray properties) throws IOException{
		// Save JsonArray to disk
		filePath.getParent().toFile().mkdirs();
		try(FileWriter writer = new FileWriter(filePath.toFile())){
			gson.toJson(properties, writer);
		}
	}
	
	private JsonArray loadConfigProperties(){
		try (FileReader reader = new FileReader(filePath.toFile())){
			return parser.parse(reader).getAsJsonArray();
		} catch (FileNotFoundException | IllegalStateException e) {
			return new JsonArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}