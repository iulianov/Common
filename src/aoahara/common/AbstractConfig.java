package aoahara.common;

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
		
		try(FileInputStream is = new FileInputStream(filePath.toFile())){
			props.load(is);
		} catch (FileNotFoundException ex){
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void setProperty(String key, String value){
		props.setProperty(key, value);
		save();
	}
	
	protected String getProperty(String key){
		return props.getProperty(key);
	}
	
	protected boolean hasProperty(String key){
		return props.containsKey(key);
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
