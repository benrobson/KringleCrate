package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ConfigManager {

    private final KringleCrate plugin;
    private final File dataFile;
    private final FileConfiguration dataConfig;
    private final DateTimeFormatter formatter;

    public ConfigManager(KringleCrate plugin) {
        this.plugin = plugin;
        this.formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

        // Initialize data file
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml!");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveGiftSubmission(String recipientUUID, String sender, ItemStack item) {
        String path = "gifts." + recipientUUID;

        // Store sender and serialized item
        dataConfig.set(path + ".sender", sender);
        dataConfig.set(path + ".item", item.serialize());

        saveDataFile();
    }

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public LocalDateTime getRevealDate() {
        String dateString = plugin.getConfig().getString("reveal-date");
        if (dateString == null || dateString.isEmpty()) {
            plugin.getLogger().severe("Reveal date is missing or empty in config.yml!");
            return LocalDateTime.now(); // Fallback to now
        }
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME); // Use ISO format
        } catch (DateTimeParseException e) {
            plugin.getLogger().severe("Invalid reveal date format in config.yml: " + dateString);
            return LocalDateTime.now(); // Fallback to now
        }
    }

    public String getFormattedRevealDate() {
        try {
            LocalDateTime revealDate = getRevealDate();
            return revealDate.format(formatter); // Use the predefined formatter
        } catch (Exception e) {
            plugin.getLogger().severe("Error formatting reveal date: " + e.getMessage());
            return "Unknown date"; // Fallback for formatting errors
        }
    }


    public boolean isRevealDay() {
        LocalDateTime now = LocalDateTime.now();
        return now.isEqual(getRevealDate()) || now.isAfter(getRevealDate());
    }

    public String getAssignedPlayer(String uuid) {
        return dataConfig.getString("assignments." + uuid);
    }

    public ItemStack getGift(String recipientUUID) {
        String path = "gifts." + recipientUUID + ".item";
        if (!dataConfig.contains(path)) return null;

        // Deserialize the item from stored data
        try {
            return ItemStack.deserialize(dataConfig.getConfigurationSection(path).getValues(false));
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to deserialize gift for recipient: " + recipientUUID);
            return null;
        }
    }

    public void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml!");
        }
    }
}