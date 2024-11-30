package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class FormatterUtils {

    private static KringleCrate plugin;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Format for display

    public FormatterUtils(KringleCrate plugin) {
        this.plugin = plugin;
    }

    // Fetches and formats the redemption period from config
    public static String getFormattedRedemptionPeriod() {
        try {
            LocalDateTime redemptionStart = getRedemptionStart(); // Get start date-time
            LocalDateTime redemptionEnd = getRedemptionEnd(); // Get end date-time
            return "from " + redemptionStart.format(formatter) + " to " + redemptionEnd.format(formatter);
        } catch (Exception e) {
            plugin.getLogger().severe("Error formatting redemption period: " + e.getMessage());
            return "Unknown redemption period";
        }
    }

    // Retrieves redemption start time from config (defaults if invalid)
    public static LocalDateTime getRedemptionStart() {
        String startDateString = plugin.getConfig().getString("redemption-start");
        try {
            return LocalDateTime.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException | NullPointerException e) {
            plugin.getLogger().severe("Invalid redemption-start format in config.yml: " + startDateString);
            return LocalDateTime.MIN; // Return a minimal value to ensure it won't validate
        }
    }

    // Retrieves redemption end time from config (defaults if invalid)
    public static LocalDateTime getRedemptionEnd() {
        String endDateString = plugin.getConfig().getString("redemption-end");
        try {
            return LocalDateTime.parse(endDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException | NullPointerException e) {
            plugin.getLogger().severe("Invalid redemption-end format in config.yml: " + endDateString);
            return LocalDateTime.MAX; // Return a maximal value to ensure it won't validate
        }
    }

    // Retrieves the reveal date from config (defaults if invalid)
    public LocalDateTime getRevealDate() {
        String dateString = plugin.getConfig().getString("reveal-date");
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            plugin.getLogger().severe("Invalid reveal date format in config.yml: " + dateString);
            return LocalDateTime.now(); // Return current date-time if invalid
        }
    }

    // Formats the reveal date using the defined formatter
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
