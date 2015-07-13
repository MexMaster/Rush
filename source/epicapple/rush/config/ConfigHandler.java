package epicapple.rush.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

	private FileConfiguration config;
	private File configFile;

	public ConfigHandler(FileConfiguration config, File configFile, HashMap<String, Object> defaults){
		if(configFile == null || !configFile.exists()){
			try{
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
			}catch(Exception ex){
				ex.printStackTrace();
				return;
			}
		}
		if(config == null){
			config = YamlConfiguration.loadConfiguration(configFile);
		}
		if(defaults != null){
			for(Map.Entry<String, Object> entry : defaults.entrySet()){
				if(entry.getKey().contains(".")){
					if(!config.isSet(entry.getKey())){
						config.set(entry.getKey(), entry.getValue());
					}
				}else{
					if(!config.contains(entry.getKey())){
						config.set(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		this.config = config;
		this.configFile = configFile;
		save();
	}

	public ConfigHandler(FileConfiguration config, File configFile){
		this(config, configFile, null);
	}

	public Object getValue(String path){
		return config.get(path);
	}

	public void setValue(String path, Object value){
		config.set(path, value);
		save();
	}

	public boolean isSet(String path){
		return config.isSet(path);
	}

	public boolean contains(String path){
		return config.contains(path);
	}

	public void save(){
		try {
			config.save(configFile);
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}

	public void reload(){
		config = YamlConfiguration.loadConfiguration(configFile);
	}
}