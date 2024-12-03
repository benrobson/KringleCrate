package me.benrobson.kringlecrate;

import me.benrobson.kringlecrate.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

public class KringleCrate extends JavaPlugin {

    private static ConfigManager configManager;
    private static GiftManager giftManager;
    private static ParticipantManager participantManager;

    private static FormatterUtils formatterUtils;
    private static DateUtils dateUtils;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        giftManager = new GiftManager(this);
        participantManager = new ParticipantManager(this);
        formatterUtils = new FormatterUtils(this);
        dateUtils = new DateUtils(this);

        // Register command and tab completer
        getCommand("kc").setExecutor(new CommandManager(this));
        getCommand("kc").setTabCompleter(new CommandCompleteManager());
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static GiftManager getGiftManager() {
        return giftManager;
    }

    public ParticipantManager getParticipantManager() {
        return participantManager;
    }
}
