package me.benrobson.kringlecrate.utils;

import com.google.gson.Gson;
import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GiftManager {
    private final KringleCrate plugin;
    private static final Gson gson = new Gson();

    public GiftManager(KringleCrate plugin) {
        this.plugin = plugin;
    }

    public void saveGiftSubmission(String recipient, String senderName, ItemStack gift) {
        UUID recipientUUID = UUID.fromString(recipient);
        String basePath = "gifts." + recipientUUID;

        List<Map<String, Object>> gifts = (List<Map<String, Object>>) plugin.getConfigManager().getConfig().getList(basePath);
        if (gifts == null) {
            gifts = new ArrayList<>();
        }

        // Create a map to represent the new gift
        Map<String, Object> giftData = new HashMap<>();
        String serializedItem = serializeItemStack(gift);
        if (serializedItem != null) {
            giftData.put("item", serializedItem);
            giftData.put("sender", senderName);

            // Add the new gift to the list
            gifts.add(giftData);

            // Save the updated list back to the config file
            plugin.getConfigManager().getConfig().set(basePath, gifts);
            plugin.getConfigManager().saveConfigFile();

            plugin.getLogger().info("[KringleCrate] [SAVE] Gift saved for recipient " + recipientUUID + ": " + giftData);
        } else {
            plugin.getLogger().severe("[KringleCrate] [SAVE] Failed to serialize ItemStack for recipient UUID: " + recipientUUID);
        }
    }

    // Retrieve all gifts for a recipient
    public List<ItemStack> getGifts(UUID recipientUUID) {
        String basePath = "gifts." + recipientUUID;
        List<Map<String, Object>> giftsData = (List<Map<String, Object>>) plugin.getConfigManager().getConfig().getList(basePath);
        List<ItemStack> gifts = new ArrayList<>();

        if (giftsData != null) {
            for (Map<String, Object> giftData : giftsData) {
                Object serializedItem = giftData.get("item");
                if (serializedItem instanceof String) {
                    ItemStack gift = deserializeItemStack((String) serializedItem);
                    if (gift != null) {
                        gifts.add(gift);
                    } else {
                        plugin.getLogger().warning("[KringleCrate] [REDEEM] Failed to deserialize ItemStack for recipient UUID: " + recipientUUID);
                    }
                } else {
                    plugin.getLogger().warning("[KringleCrate] [REDEEM] Unexpected item format for recipient UUID: " + recipientUUID);
                }
            }
        }
        return gifts;
    }

    public void clearGifts(UUID recipientUUID) {
        String basePath = "gifts." + recipientUUID;
        plugin.getConfigManager().getConfig().set(basePath, null);
        plugin.getConfigManager().saveConfigFile();
        plugin.getLogger().info("[KringleCrate] [CLEAR] Cleared gifts for recipient: " + recipientUUID);
    }

    // Serialize an ItemStack to a JSON string
    private String serializeItemStack(ItemStack item) {
        try {
            // Convert ItemStack to Map
            Map<String, Object> itemMap = item.serialize();
            // Serialize the map to JSON
            return gson.toJson(itemMap);
        } catch (Exception e) {
            plugin.getLogger().severe("[SERIALIZE] Could not serialize ItemStack: " + e.getMessage());
            return null;
        }
    }

    // Deserialize a JSON string into an ItemStack
    public static ItemStack deserializeItemStack(String json) {
        try {
            // Convert JSON string back to Map
            Map<String, Object> itemMap = gson.fromJson(json, Map.class);
            // Deserialize the map into an ItemStack
            return ItemStack.deserialize(itemMap);
        } catch (Exception e) {
            System.err.println("[DESERIALIZE] Could not deserialize ItemStack: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
