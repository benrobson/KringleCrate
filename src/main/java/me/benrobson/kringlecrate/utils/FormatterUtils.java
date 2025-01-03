package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FormatterUtils {

    private static KringleCrate plugin;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy"); // Format for display

    public FormatterUtils(KringleCrate plugin) {
        this.plugin = plugin;
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

    public static LocalDateTime getRevealDate() {
        String dateString = plugin.getConfig().getString("reveal-date");
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            plugin.getLogger().severe("Invalid reveal date format in config.yml: " + dateString);
            return LocalDateTime.now(); // Return current date-time if invalid
        }
    }

    public static String getFormattedRedemptionPeriod() {
        try {
            LocalDateTime redemptionStart = DateUtils.getRedemptionStart();
            LocalDateTime redemptionEnd = DateUtils.getRedemptionEnd();
            return "from " + redemptionStart.format(formatter) + " to " + redemptionEnd.format(formatter);
        } catch (Exception e) {
            plugin.getLogger().severe("Error formatting redemption period: " + e.getMessage());
            return "Unknown redemption period";
        }
    }

    public static String getFormattedRevealDate() {
        try {
            LocalDateTime revealDate = DateUtils.getRevealDate();
            return revealDate.format(formatter);
        } catch (Exception e) {
            plugin.getLogger().severe("Error formatting reveal date: " + e.getMessage());
            return "Unknown date";
        }
    }
}
