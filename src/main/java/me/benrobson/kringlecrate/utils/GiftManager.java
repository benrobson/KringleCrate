package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.*;

public class GiftManager {
    private final KringleCrate plugin;

    public GiftManager(KringleCrate plugin) {
        this.plugin = plugin;
    }

    // Save a gift submission for a recipient
    public void saveGiftSubmission(String recipient, String senderName, ItemStack gift) {
        UUID recipientUUID = UUID.fromString(recipient);
        String basePath = "gifts." + recipientUUID;

        List<Map<String, Object>> gifts = (List<Map<String, Object>>) plugin.getConfigManager().getConfig().getList(basePath);
        if (gifts == null) {
            gifts = new ArrayList<>();
        }

        // Create a map to represent the new gift
        Map<String, Object> giftData = new HashMap<>();
        giftData.put("item", serializeItemStack(gift));
        giftData.put("sender", senderName);

        // Add the new gift to the list
        gifts.add(giftData);

        // Save the updated list back to the config file
        plugin.getConfigManager().getConfig().set(basePath, gifts);
        plugin.getConfigManager().saveConfigFile();

        plugin.getLogger().info("Gift saved for recipient " + recipientUUID + ": " + giftData);
    }

    // Retrieve all gifts for a recipient
    public List<ItemStack> getGifts(UUID recipientUUID) {
        String basePath = "gifts." + recipientUUID;
        List<Map<String, Object>> giftsData = (List<Map<String, Object>>) plugin.getConfigManager().getConfig().getList(basePath);
        List<ItemStack> gifts = new ArrayList<>();

        if (giftsData != null) {
            for (Map<String, Object> giftData : giftsData) {
                String serializedItem = (String) giftData.get("item");
                if (serializedItem != null) {
                    ItemStack gift = deserializeItemStack(serializedItem);
                    if (gift != null) {
                        gifts.add(gift);
                    }
                }
            }
        }
        return gifts;
    }

    // Clear all gifts for a recipient
    public void clearGifts(UUID recipientUUID) {
        String basePath = "gifts." + recipientUUID;
        plugin.getConfigManager().getConfig().set(basePath, null);
        plugin.getConfigManager().saveConfigFile();
        plugin.getLogger().info("Cleared gifts for recipient: " + recipientUUID);
    }

    // Serialize an ItemStack into a Base64 string
    private String serializeItemStack(ItemStack item) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream bukkitStream = new BukkitObjectOutputStream(byteStream)) {
            bukkitStream.writeObject(item);
            return Base64.getEncoder().encodeToString(byteStream.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().severe("Could not serialize ItemStack: " + e.getMessage());
            return null;
        }
    }

    // Deserialize a Base64 string into an ItemStack
    private ItemStack deserializeItemStack(String base64) {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
             BukkitObjectInputStream bukkitStream = new BukkitObjectInputStream(byteStream)) {
            return (ItemStack) bukkitStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            plugin.getLogger().severe("Could not deserialize ItemStack: " + e.getMessage());
            return null;
        }
    }
}
