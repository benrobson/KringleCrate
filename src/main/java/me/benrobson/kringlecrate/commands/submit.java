package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import me.benrobson.kringlecrate.utils.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class submit implements CommandExecutor {

    private final KringleCrate plugin;

    public submit(KringleCrate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();

        // Check if it's past the reveal date
        if (DateUtils.isRevealDay()) {
            player.sendMessage(ChatColor.RED + "You cannot submit gifts after the reveal date!");
            return true;
        }

        // Debug: Log the player's UUID
        plugin.getLogger().info("Submitting gift for player UUID: " + playerUUID);

        // Check if the player has an assigned recipient
        String assignedPlayer = plugin.getParticipantManager().getAssignedPlayer(playerUUID);
        if (assignedPlayer == null) {
            player.sendMessage(ChatColor.RED + "You do not have an assigned recipient!");
            plugin.getLogger().info("No assigned recipient found for UUID: " + playerUUID);
            return true;
        }

        // Debug: Log the assigned player
        plugin.getLogger().info("Assigned recipient for " + playerUUID + ": " + assignedPlayer);

        // Check if the player is holding an item
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "You need to hold an item to submit it as a gift.");
            return true;
        }

        // Store item details in data.yml
        plugin.getConfigManager().saveGiftSubmission(assignedPlayer, player.getName(), itemInHand);

        // Remove the item from the player's hand
        player.getInventory().setItemInMainHand(null);

        player.sendMessage(ChatColor.GREEN + "Your gift has been submitted to your recipient!");
        return true;
    }
}
