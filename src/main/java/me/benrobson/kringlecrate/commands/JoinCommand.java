package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import me.benrobson.kringlecrate.utils.GiftManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JoinCommand implements CommandExecutor {

    private final KringleCrate plugin;
    private final GiftManager giftManager;

    public JoinCommand(KringleCrate plugin) {
        this.plugin = plugin;
        this.giftManager = KringleCrate.getGiftManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check if the player is already opted in
        if (giftManager.getParticipants().contains(playerUUID)) {
            player.sendMessage(ChatColor.RED + "You have already joined the Secret Santa event!");
            return true;
        }

        // Add the player as a participant
        giftManager.addParticipant(playerUUID);

        // Notify the player
        player.sendMessage(ChatColor.GREEN + "You have successfully joined the Secret Santa event!");
        return true;
    }
}
