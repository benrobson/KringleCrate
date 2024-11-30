package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ConfigManager {
    private final KringleCrate plugin;
    private final File configFile;
    private final File dataFile;
    private final FileConfiguration config;
    private final FileConfiguration dataConfig;
    private final DateTimeFormatter formatter;

    public ConfigManager(KringleCrate plugin) {
        this.plugin = plugin;
        this.formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

        // Initialize configuration and data files
        configFile = new File(plugin.getDataFolder(), "config.yml");
        dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml!");
            }
        }

        this.config = plugin.getConfig();
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config data.yml!");
        }
    }

    public void saveConfigFile() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml!");
        }
    }

    public void savePlayerData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml!");
        }
    }

    public LocalDateTime getRevealDate() {
        String dateString = config.getString("reveal-date");
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            plugin.getLogger().severe("Invalid reveal date format in config.yml: " + dateString);
            return LocalDateTime.now();
        }
    }

    public String getFormattedRevealDate() {
        try {
            LocalDateTime revealDate = getRevealDate();
            return revealDate.format(formatter);
        } catch (Exception e) {
            plugin.getLogger().severe("Error formatting reveal date: " + e.getMessage());
            return "Unknown date";
        }
    }


}
