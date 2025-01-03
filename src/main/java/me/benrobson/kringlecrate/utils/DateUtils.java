package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Standard format
    private static KringleCrate plugin;

    public DateUtils(KringleCrate plugin) {
        this.plugin = plugin;
    }

    // Get the reveal date from the config
    public static LocalDateTime getRevealDate() {
        String dateString = plugin.getConfig().getString("reveal-date");
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            plugin.getLogger().severe("Invalid reveal date format in config.yml: " + dateString);
            return LocalDateTime.now(); // Return the current time if the config value is invalid
        }
    }

    public static boolean isRevealDay() {
        LocalDateTime revealDate = FormatterUtils.getRevealDate();
        return !LocalDateTime.now().isBefore(revealDate);
    }

    public static boolean isBeforeRevealDate() {
        LocalDateTime revealDate = FormatterUtils.getRevealDate();
        return LocalDateTime.now().isBefore(revealDate);
    }

    public static LocalDateTime getRedemptionStart() {
        String startDateString = plugin.getConfig().getString("redemption-start");
        try {
            return LocalDateTime.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            plugin.getLogger().severe("Invalid redemption-start format in config.yml: " + startDateString);
            return LocalDateTime.MIN; // Return a minimal value to ensure it won't validate
        }
    }

    public static LocalDateTime getRedemptionEnd() {
        String endDateString = plugin.getConfig().getString("redemption-end");
        try {
            return LocalDateTime.parse(endDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            plugin.getLogger().severe("Invalid redemption-end format in config.yml: " + endDateString);
            return LocalDateTime.MAX; // Return a maximal value to ensure it won't validate
        }
    }

    public static boolean isInRedemptionPeriod() {
        LocalDateTime redemptionStart = getRedemptionStart();
        LocalDateTime redemptionEnd = getRedemptionEnd();
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(redemptionStart) && !now.isAfter(redemptionEnd);
    }

    public static String getFormattedRedemptionPeriod() {
        return getRedemptionStart().format(formatter) +
                " to " +
                getRedemptionEnd().format(formatter);
    }
}
