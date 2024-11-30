package me.benrobson.kringlecrate.utils;

import me.benrobson.kringlecrate.KringleCrate;

import java.util.Collections;
import java.util.List;

public class ParticipantManager {

    private final KringleCrate plugin;

    public ParticipantManager(KringleCrate plugin) {
        this.plugin = plugin;
    }

    public void assignParticipants() {
        List<String> participants = plugin.getConfigManager().getDataConfig().getStringList("participants");
        if (participants.size() < 2) {
            plugin.getLogger().severe("Not enough participants to assign recipients.");
            return;
        }

        Collections.shuffle(participants);

        for (int i = 0; i < participants.size(); i++) {
            String giver = participants.get(i);
            String recipient = participants.get((i + 1) % participants.size());
            plugin.getConfigManager().getDataConfig().set("assignments." + giver, recipient);
        }

        plugin.getConfigManager().saveDataFile();
        plugin.getLogger().info("Recipients successfully assigned!");
    }
}

