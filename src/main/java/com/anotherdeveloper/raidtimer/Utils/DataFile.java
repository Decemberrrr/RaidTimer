package com.anotherdeveloper.raidtimer.Utils;

import com.google.common.io.Files;
import com.google.common.io.Files;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DataFile {

    private final JavaPlugin plugin;
    private final boolean hasDefault;
    private final File file;
    private final String fileName;
    private FileConfiguration configuration;

    public DataFile(JavaPlugin plugin, String fileName, boolean hasDefault) {
        this.plugin = plugin;
        this.hasDefault = hasDefault;
        this.fileName = fileName;
        file = new File(plugin.getDataFolder() + File.separator + fileName + ".yml");
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                if (hasDefault) {
                    plugin.saveResource(fileName + ".yml", false);
                } else {
                    file.createNewFile();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        loadConfig();
    }

    public void loadConfig() {
        configuration = new YamlConfiguration();
        try {
            configuration.loadFromString(Files.toString(file, StandardCharsets.UTF_8));

        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return configuration;
    }

    public List<String> getStringList(String key) {
        return configuration.getStringList(key);
    }

    public String getString(String key) {
        return configuration.getString(key);
    }

    public int getInteger(String key) {
        return configuration.getInt(key);
    }

    public double getDouble(String key) {
        return configuration.getDouble(key);
    }

    public void saveConfig() {
        try {
            configuration.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }
}
