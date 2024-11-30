package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import me.benrobson.kringlecrate.utils.ParticipantManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class reveal implements CommandExecutor {

    private final KringleCrate plugin;

    public reveal(KringleCrate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Check for permission
        if (!player.hasPermission("kringlecrate.reveal")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Check if the reveal date has passed or if the player has an override permission
        if (!plugin.getConfigManager().isRevealDay() && !player.hasPermission("kringlecrate.override")) {
            player.sendMessage(ChatColor.RED + "You cannot reveal your recipient until the reveal day: "
                    + ChatColor.GOLD + plugin.getConfigManager().getFormattedRevealDate());
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ParticipantManager participantManager = plugin.getParticipantManager(); // Now this works

            List<UUID> participants = plugin.getGiftManager().getParticipants();

            // Check if the player is part of the event
            if (!participants.contains(player.getUniqueId())) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "You are not part of the Secret Santa event!")
                );
                return;
            }

            // Check if there are enough participants
            if (participants.size() <= 1) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "Not enough participants to assign a recipient!")
                );
                return;
            }

            // Assign recipients if not already done
            if (!plugin.getConfigManager().getDataConfig().contains("assignments")) {
                participantManager.assignParticipants();
            }

            // Get the recipient for the player
            String recipientUUID = plugin.getConfigManager()
                    .getDataConfig()
                    .getString("assignments." + player.getUniqueId().toString());

            if (recipientUUID == null) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "No recipient assigned. Please contact an administrator.")
                );
                return;
            }

            UUID recipientId = UUID.fromString(recipientUUID);
            OfflinePlayer recipientPlayer = Bukkit.getOfflinePlayer(recipientId);

            Bukkit.getScheduler().runTask(plugin, () -> {
                player.sendMessage(ChatColor.GREEN + "Your assigned recipient is: "
                        + ChatColor.GOLD + recipientPlayer.getName());
            });
        });

        return true;
    }
}
