package aohara.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public abstract class AbstractConfig {
	
	private final Properties props;
	private final Path filePath;
	private boolean loadOnGet = false;
	
	public AbstractConfig(String documentsFolder){
		this(documentsFolder, "config.properties");
	}
	
	public AbstractConfig(String documentsFolder, String fileName){
		this(Paths.get(
			System.getProperty("user.home"), "Documents",
			documentsFolder, fileName
		));
	}
	
	public AbstractConfig(Path filePath){
		props = new Properties();
		this.filePath = filePath;
		load();
	}
	
	protected void setLoadOnGet(boolean loadOnGet){
		this.loadOnGet = loadOnGet;
	}
	
	protected void setProperty(String key, String value){
		props.setProperty(key, value);
		save();
	}
	
	protected String getProperty(String key){
		if (loadOnGet){
			load();
		}
		return props.getProperty(key);
	}
	
	protected boolean hasProperty(String key){
		if (loadOnGet){
			load();
		}
		return props.containsKey(key);
	}
	
	protected void load(){
		try(FileInputStream is = new FileInputStream(filePath.toFile())){
			props.load(is);
		} catch (FileNotFoundException ex){
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void save(){
		try(FileOutputStream os = new FileOutputStream(filePath.toFile())){
			props.store(os, null);
		} catch (FileNotFoundException e){
			filePath.toFile().getParentFile().mkdirs();
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
