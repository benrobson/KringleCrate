package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
                plugin.getLogger().severe("Could not create data.yml: " + e.getMessage());
            }
        }
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    private synchronized void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    public void storeGift(UUID recipient, ItemStack gift, String note, UUID sender) {
        String path = "gifts." + recipient.toString();
        data.set(path + ".item", serializeItemStack(gift));
        data.set(path + ".note", note);
        data.set(path + ".sender", sender.toString());
        saveData();
    }

    public Map<String, Object> getGift(UUID recipient) {
        String path = "gifts." + recipient.toString();
        return data.getConfigurationSection(path) != null ? data.getConfigurationSection(path).getValues(false) : null;
    }

    public boolean hasGift(UUID recipient) {
        return data.contains("gifts." + recipient.toString());
    }

    public boolean redeemGift(UUID recipient) {
        Map<String, Object> giftData = getGift(recipient);
        if (giftData == null) {
            return false;
        }

        ItemStack gift = deserializeItemStack((String) giftData.get("item"));
        if (gift == null || gift.getAmount() <= 0) {
            return false;
        }

        if (!Bukkit.getPlayer(recipient).getInventory().addItem(gift).isEmpty()) {
            return false;
        }

        data.set("gifts." + recipient.toString(), null);
        saveData();
        return true;
    }

    public void addParticipant(UUID player) {
        Set<String> participants = new HashSet<>(data.getStringList("participants"));
        if (participants.add(player.toString())) {
            data.set("participants", new ArrayList<>(participants));
            saveData();
        }
    }

    public List<UUID> getParticipants() {
        return data.getStringList("participants").stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }

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