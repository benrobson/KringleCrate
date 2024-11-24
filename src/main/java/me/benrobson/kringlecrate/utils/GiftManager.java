package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GiftManager {
    private final KringleCrate plugin;
    private final File dataFile;
    private final YamlConfiguration data;

    public GiftManager(KringleCrate plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Store a gift for a player identified by UUID
    public void storeGift(UUID recipient, ItemStack gift, String note, UUID sender) {
        String path = "gifts." + recipient.toString();  // Use UUID as string for path
        data.set(path + ".item", gift);
        data.set(path + ".note", note);
        data.set(path + ".sender", sender.toString());
        saveData();
    }

    // Retrieve the gift for a player by UUID
    public Map<String, Object> getGift(UUID recipient) {
        String path = "gifts." + recipient.toString();  // Use UUID as string for path
        return data.getConfigurationSection(path) != null ? data.getConfigurationSection(path).getValues(false) : null;
    }

    // Check if a player has a gift
    public boolean hasGift(UUID recipient) {
        return getGift(recipient) != null;
    }

    // Redeem a gift for a player by UUID
    public boolean redeemGift(UUID recipient) {
        Map<String, Object> giftData = getGift(recipient);
        if (giftData == null) {
            return false; // No gift to redeem
        }

        ItemStack gift = (ItemStack) giftData.get("item");
        if (gift == null || gift.getAmount() <= 0) {
            return false; // Invalid gift
        }

        // Check if the player's inventory can hold the gift
        if (!plugin.getServer().getPlayer(recipient).getInventory().addItem(gift).isEmpty()) {
            return false; // Not enough space in inventory
        }

        // Remove the gift after redemption
        data.set("gifts." + recipient.toString(), null);
        saveData();
        return true; // Successfully redeemed the gift
    }

    // Add a participant to the participants list
    public void addParticipant(UUID player) {
        List<String> participants = data.getStringList("participants");
        if (!participants.contains(player.toString())) {
            participants.add(player.toString());
            data.set("participants", participants);
            saveData();
        }
    }

    // Retrieve the list of participants as UUIDs
    public List<UUID> getParticipants() {
        List<String> participants = data.getStringList("participants");
        List<UUID> uuids = new ArrayList<>();
        for (String id : participants) {
            uuids.add(UUID.fromString(id));
        }
        return uuids;
    }
}