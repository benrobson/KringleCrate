package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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

        // Check if the player has the override permission to bypass reveal day
        if (!plugin.getConfigManager().isRevealDay() && !player.hasPermission("kringlecrate.override")) {
            player.sendMessage(ChatColor.RED + "You cannot reveal your recipient until the reveal day: "
                    + ChatColor.GOLD + plugin.getConfigManager().getFormattedRevealDate());
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<UUID> participants = plugin.getGiftManager().getParticipants();
            if (!participants.contains(player.getUniqueId())) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "You are not part of the Secret Santa event!")
                );
                return;
            }

            if (participants.size() <= 1) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "Not enough participants to assign a recipient!")
                );
                return;
            }

            UUID recipient;
            do {
                recipient = participants.get(ThreadLocalRandom.current().nextInt(participants.size()));
            } while (recipient.equals(player.getUniqueId()));

            UUID finalRecipient = recipient;
            Bukkit.getScheduler().runTask(plugin, () -> {
                OfflinePlayer recipientPlayer = Bukkit.getOfflinePlayer(finalRecipient);
                player.sendMessage(ChatColor.GREEN + "Your assigned recipient is: "
                        + ChatColor.GOLD + recipientPlayer.getName());
            });
        });

        return true;
    }
}