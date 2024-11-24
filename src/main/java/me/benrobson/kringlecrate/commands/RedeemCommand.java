package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public class RedeemCommand implements CommandExecutor {

    private final KringleCrate plugin;

    public RedeemCommand(KringleCrate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check for permission
        if (!player.hasPermission("kringlecrate.redeem")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Check redemption period
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime redemptionStart = plugin.getConfigManager().getRedemptionStart();
        LocalDateTime redemptionEnd = plugin.getConfigManager().getRedemptionEnd();

        if (now.isBefore(redemptionStart)) {
            if (!player.hasPermission("kringlecrate.override")) {
                player.sendMessage(ChatColor.RED + "Redemption hasn't started yet. It begins on "
                        + ChatColor.GOLD + plugin.getConfigManager().getFormattedRedemptionStart() + ChatColor.RED + ".");
                return true;
            }
        }

        if (now.isAfter(redemptionEnd)) {
            if (!player.hasPermission("kringlecrate.override")) {
                player.sendMessage(ChatColor.RED + "The redemption period has ended. It ended on "
                        + ChatColor.GOLD + plugin.getConfigManager().getFormattedRedemptionEnd() + ChatColor.RED + ".");
                return true;
            }
        }

        // Check if the player has any gifts
        if (!plugin.getGiftManager().hasGift(playerUUID)) {
            player.sendMessage(ChatColor.RED + "You do not have any gifts to redeem.");
            return true;
        }

        // Attempt to redeem the gift
        if (!plugin.getGiftManager().redeemGift(playerUUID)) {
            player.sendMessage(ChatColor.RED + "You do not have enough inventory space to redeem your gifts.");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "You have successfully redeemed your gift(s)! Check your inventory.");
        return true;
    }
}