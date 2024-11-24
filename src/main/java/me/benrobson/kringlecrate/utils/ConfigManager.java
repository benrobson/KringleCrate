package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ConfigManager {

    private final KringleCrate plugin;
    private final DateTimeFormatter formatter;

    public ConfigManager(KringleCrate plugin) {
        this.plugin = plugin;
        this.formatter = DateTimeFormatter.ofPattern("d MMMM yyyy"); // Example: 25th December 2024
    }

    // Retrieve the reveal date from config and parse it to LocalDateTime
    public LocalDateTime getRevealDate() {
        String revealDateStr = plugin.getConfig().getString("reveal-date");
        return parseDate(revealDateStr);
    }

    // Retrieve the redemption start date from config and parse it to LocalDateTime
    public LocalDateTime getRedemptionStart() {
        String redemptionStartStr = plugin.getConfig().getString("redemption-start");
        return parseDate(redemptionStartStr);
    }

    // Retrieve the redemption end date from config and parse it to LocalDateTime
    public LocalDateTime getRedemptionEnd() {
        String redemptionEndStr = plugin.getConfig().getString("redemption-end");
        return parseDate(redemptionEndStr);
    }

    // Check if the current date is the reveal day or after the reveal date
    public boolean isRevealDay() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(getRevealDate());  // Returns true if now is on or after reveal date
    }

    // Format and retrieve the reveal date in "d MMMM yyyy" format
    public String getFormattedRevealDate() {
        return getRevealDate().format(formatter);
    }

    // Format and retrieve the redemption start date in "d MMMM yyyy" format
    public String getFormattedRedemptionStart() {
        return getRedemptionStart().format(formatter);
    }

    // Format and retrieve the redemption end date in "d MMMM yyyy" format
    public String getFormattedRedemptionEnd() {
        return getRedemptionEnd().format(formatter);
    }

    // Retrieve the assigned player for a given player (from data.yml or another config section)
    public String getAssignedPlayer(String playerName) {
        FileConfiguration config = plugin.getConfig();
        // Assuming assigned players are stored in a section like 'assigned-players' in the config
        String assignedPlayer = config.getString("assigned-players." + playerName);
        if (assignedPlayer == null) {
            plugin.getLogger().warning("No assigned player found for: " + playerName);
            return null;
        }
        return assignedPlayer;
    }

    // Private helper method to parse a date string into LocalDateTime, handling errors
    private LocalDateTime parseDate(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            plugin.getLogger().warning("Invalid date format in configuration: " + dateTime);
            return LocalDateTime.MIN;  // Return an invalid date (could be handled better)
        }
    }
}
