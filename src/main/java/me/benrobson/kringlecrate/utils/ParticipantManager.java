package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ParticipantManager {
    private final KringleCrate plugin;

    public ParticipantManager(KringleCrate plugin) {
        this.plugin = plugin;
    }

    // Method to get the list of participants (for checking in the join command)
    public List<String> getParticipants() {
        return plugin.getConfigManager().getDataConfig().getStringList("participants");
    }

    // Method to add a participant
    public void addParticipant(UUID playerUUID) {
        List<String> participants = plugin.getConfigManager().getDataConfig().getStringList("participants");

        // Convert UUID to String for storage
        if (!participants.contains(playerUUID.toString())) {
            participants.add(playerUUID.toString()); // Add the player's UUID as a string
            plugin.getConfigManager().getDataConfig().set("participants", participants);
            plugin.getConfigManager().savePlayerData();  // Save the changes
            plugin.getLogger().info("Player " + playerUUID + " added to the Secret Santa participants list.");
        }
    }

    public String getAssignedPlayer(String playerUUID) {
        String assignedPlayer = plugin.getConfigManager().getDataConfig().getString("assignments." + playerUUID.toString());
        if (assignedPlayer != null) {
            return assignedPlayer;
        }
        return null; // No assigned player, return null
    }

    // Method to assign participants (for Secret Santa pairing)
    public void assignParticipants() {
        List<String> participants = plugin.getConfigManager().getDataConfig().getStringList("participants");
        if (participants.size() < 2) {
            plugin.getLogger().severe("Not enough participants to assign recipients.");
            return;
        }

        // Shuffle participants and assign them Secret Santa partners
        Collections.shuffle(participants);

        for (int i = 0; i < participants.size(); i++) {
            String giver = participants.get(i);
            String recipient = participants.get((i + 1) % participants.size());
            plugin.getConfigManager().getDataConfig().set("assignments." + giver, recipient);
        }

        plugin.getConfigManager().savePlayerData();
        plugin.getLogger().info("Recipients successfully assigned!");
    }
}
