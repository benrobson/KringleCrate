package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;
import java.util.UUID;

public class GiftManager {
    private final KringleCrate plugin;

    public GiftManager(KringleCrate plugin) {
        this.plugin = plugin;
    }

    // Save the data file to disk
    private synchronized void saveDataFile() {
        plugin.getConfigManager().saveDataFile();
    }

    // Store a gift for a recipient
    public void storeGift(UUID recipient, ItemStack gift, String note, UUID sender) {
        String path = "gifts." + recipient.toString();
        plugin.getConfigManager().getDataConfig().set(path + ".item", serializeItemStack(gift));
        plugin.getConfigManager().getDataConfig().set(path + ".note", note);
        plugin.getConfigManager().getDataConfig().set(path + ".sender", sender.toString());
        saveDataFile();
    }

    // Retrieve a gift for a recipient
    public ItemStack getGift(UUID recipientUUID) {
        String path = "gifts." + recipientUUID.toString() + ".item";
        if (!plugin.getConfigManager().getDataConfig().contains(path)) return null;

        try {
            String serializedItem = plugin.getConfigManager().getDataConfig().getString(path);
            return deserializeItemStack(serializedItem);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to deserialize gift for recipient: " + recipientUUID);
            return null;
        }
    }

    // Check if a player has a gift
    public boolean hasGift(UUID recipient) {
        return plugin.getConfigManager().getDataConfig().contains("gifts." + recipient.toString());
    }

    // Redeem a gift for a player
    public boolean redeemGift(UUID recipient) {
        Player player = Bukkit.getPlayer(recipient);
        if (player == null) {
            plugin.getLogger().warning("Player with UUID " + recipient + " is not online.");
            return false;
        }

        ItemStack gift = getGift(recipient);
        if (gift == null) {
            plugin.getLogger().warning("No gift found for UUID: " + recipient);
            return false;
        }

        // Add the gift to the player's inventory
        if (!player.getInventory().addItem(gift).isEmpty()) {
            player.sendMessage("Your inventory is full! Clear some space to redeem your gift.");
            return false;
        }

        // Remove the gift from the data file
        plugin.getConfigManager().getDataConfig().set("gifts." + recipient.toString(), null);
        saveDataFile();
        player.sendMessage("You have redeemed your gift!");
        return true;
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