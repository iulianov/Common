package aohara.common.profiles;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

public class ProfileManager {
	
	private static final JsonParser parser = new JsonParser();
	private static final Gson gson = new Gson();
	private final Path profilesPath;
	private JsonArray cached;
	
	public ProfileManager(Path profilesPath){
		this.profilesPath = profilesPath;
		if (!profilesPath.toFile().isFile()){
			throw new IllegalArgumentException("Path must ba a file");
		}
	}
	
	// -- Accessors ---------------------------------------------------
	
	public Set<String> getProfileNames(){
		Set<String> names = new HashSet<>();
		for (JsonElement ele : load()){
			names.add(ele.getAsJsonObject().get(Profile.NAME).getAsString());
		}
		return names;
	}
	
	public boolean hasProfile(String name){
		return getProfileNames().contains(name);
	}
	
	public Profile getProfile(String name){
		JsonObject obj = findProfile(name);
		
		Profile profile = new HashMapProfile(name);
		for (Entry<String, JsonElement> entry : obj.entrySet()){
			if (!entry.getKey().equals(Profile.NAME)){
				profile.putProperty(entry.getKey(), entry.getValue().getAsString());
			}
		}
		return profile;
	}
	
	public int size(){
		return load().size();
	}
	
	// -- Mutators --------------------------------------------------------
	
	public Profile newProfile(String name){
		return new HashMapProfile(name);
	}
	
	public Profile saveProfile(Profile profile){
		if(hasProfile(profile.name)){
			deleteProfile(profile);
		}
		
		JsonArray json = load();
		json.add(profileToJson(profile));
		save(json);
		cached = json;
		return profile;
	}
	
	public void deleteProfile(Profile profile){
		JsonArray json = load();
		if (!json.remove(findProfile(profile.name))){
			throw new IllegalStateException("Could not remove profile: " + profile.name);
		}
		save(json);
	}
	
	// -- Helpers --------------------------------------------------
	
	private JsonObject profileToJson(Profile profile){
		JsonObject obj = new JsonObject();
		obj.addProperty(Profile.NAME, profile.name);
		for (Entry<String, String> entry : profile.entrySet()){
			obj.addProperty(entry.getKey(), entry.getValue());
		}
		return obj;
	}
	
	private JsonObject findProfile(String name){
		for (JsonElement ele : load()){
			if (ele.getAsJsonObject().get(Profile.NAME).getAsString().equals(name)){
				return ele.getAsJsonObject();
			}
		}
		throw new IllegalArgumentException(name + " profile does not exist!");
	}
	
	private JsonArray load(){
		if (cached == null){			
			try(FileReader reader = new FileReader(profilesPath.toFile())){
				JsonElement ele = parser.parse(reader);
				if (!ele.isJsonArray()){
					throw new FileNotFoundException();
				}
				cached = parser.parse(reader).getAsJsonArray();
			} catch (FileNotFoundException e) {
				cached = new JsonArray();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return cached;
	}
	
	private void save(JsonArray json){
		cached = json;
		try(JsonWriter writer = new JsonWriter(new FileWriter(profilesPath.toFile()))){
			gson.toJson(json, writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}