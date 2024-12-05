package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import me.benrobson.kringlecrate.utils.DateUtils;
import me.benrobson.kringlecrate.utils.FormatterUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class submit implements CommandExecutor {

    private final KringleCrate plugin;
    private DateUtils dateUtils;

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

        // Check if submissions are not yet open (before the reveal date)
        if (DateUtils.isBeforeRevealDate()) {
            player.sendMessage(ChatColor.RED + "You cannot submit gifts before the reveal date: "
                    + ChatColor.GOLD + FormatterUtils.getFormattedRevealDate());
            return true;
        }

        if (DateUtils.isInRedemptionPeriod()) {
            player.sendMessage(ChatColor.RED + "You submit any more gifts during as the redemption period has now started.");
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

        // Get the display name of the item
        String itemDisplayName = itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()
                ? itemInHand.getItemMeta().getDisplayName()
                : itemInHand.getType().name().toLowerCase().replace('_', ' ');

        // Store item details in data.yml
        plugin.getGiftManager().saveGiftSubmission(assignedPlayer, player.getName(), itemInHand);

        // Remove the item from the player's hand
        player.getInventory().setItemInMainHand(null);

        // Send a success message with the item's display name
        player.sendMessage(ChatColor.GREEN + "Your gift has been submitted to your recipient: " + ChatColor.AQUA + itemDisplayName + ChatColor.GREEN + "!");
        plugin.getLogger().info("Gift successfully submitted for recipient UUID: " + assignedPlayer);
        return true;
    }
}
