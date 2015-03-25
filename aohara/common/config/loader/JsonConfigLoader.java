package aohara.common.config.loader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import aohara.common.config.Config;
import aohara.common.config.Constraint.InvalidInputException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonConfigLoader extends ConfigLoader {
	
	private static final String NAME = "name", DATA = "data", KEY = "key", VALUE = "value";
	private final JsonParser parser = new JsonParser();
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public JsonConfigLoader(Path filePath){
		super(filePath);
	}

	@Override
	public void load(Config config) {
		JsonObject configObj = searchConfigJson(config);
		if (configObj != null){
			loadToConfig(configObj, config);
		}
	}
	
	@Override
	public void save(Config config) {
		JsonArray configArray = loadConfigs();
		JsonObject existingConfigObj = searchConfigJson(config);
		if (existingConfigObj != null){
			configArray.remove(existingConfigObj);
		}
		
		configArray.add(configToJson(config));
		save(configArray);
	}
	
	private JsonObject searchConfigJson(Config config){
		for (JsonElement configEle : loadConfigs()){
			JsonObject configObj = configEle.getAsJsonObject();
			if (configObj.get(NAME).getAsString().equals(config.getName())){
				return configObj;
			}
		}
		return null;
	}
	
	private JsonArray loadConfigs(){
		try (FileReader reader = new FileReader(filePath.toFile())){
			return parser.parse(reader).getAsJsonArray();
		} catch (FileNotFoundException | IllegalStateException e) {
			return new JsonArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private JsonObject configToJson(Config config){
		JsonArray data = new JsonArray();
		for (String key : config.keySet()){
			JsonObject pair = new JsonObject();
			pair.addProperty(KEY, key);
			pair.addProperty(VALUE, config.getProperty(key));
			data.add(pair);
		}
		
		JsonObject obj = new JsonObject();
		obj.addProperty(NAME, config.getName());
		obj.add(DATA, data);
		return obj;
	}
	
	private void loadToConfig(JsonObject obj, Config config){
		for (JsonElement ele : obj.get(DATA).getAsJsonArray()){
			
			// Try to set value to config
			JsonObject pair  = ele.getAsJsonObject();
			try {
				String key = pair.get(KEY).getAsString();
				if (config.hasProperty(key)){
					config.setProperty(key, pair.get(VALUE).getAsString());
				}
			} catch (InvalidInputException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void save(JsonArray configArray){
		filePath.getParent().toFile().mkdirs();
		try(FileWriter writer = new FileWriter(filePath.toFile())){
			gson.toJson(configArray, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}