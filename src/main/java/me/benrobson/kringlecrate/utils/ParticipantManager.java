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

    // Method to get the list of participants
    public List<String> getParticipants() {
        return plugin.getConfigManager().getConfig().getStringList("participants");
    }

    // Method to add a participant
    public void addParticipant(UUID playerUUID) {
        List<String> participants = plugin.getConfigManager().getConfig().getStringList("participants");

        // Convert UUID to String for storage
        if (!participants.contains(playerUUID.toString())) {
            participants.add(playerUUID.toString()); // Add the player's UUID as a string
            plugin.getConfigManager().getConfig().set("participants", participants);
            plugin.getConfigManager().saveConfigFile(); // Save the changes
            plugin.getLogger().info("Player " + playerUUID + " added to the Secret Santa participants list.");
        }
    }

    // Method to get the assigned player for a participant
    public String getAssignedPlayer(String playerUUID) {
        return plugin.getConfigManager().getConfig().getString("assignments." + playerUUID);
    }

    // Method to assign participants (for Secret Santa pairing)
    public void assignParticipants() {
        List<String> participants = plugin.getConfigManager().getConfig().getStringList("participants");

        if (participants.size() < 2) {
            plugin.getLogger().severe("Not enough participants to assign recipients.");
            return;
        }

        // Shuffle participants and assign them Secret Santa partners
        Collections.shuffle(participants);

        for (int i = 0; i < participants.size(); i++) {
            String giver = participants.get(i);
            String recipient = participants.get((i + 1) % participants.size());
            plugin.getConfigManager().getConfig().set("assignments." + giver, recipient);
        }

        plugin.getConfigManager().saveConfigFile();
        plugin.getLogger().info("Recipients successfully assigned!");
    }
}
