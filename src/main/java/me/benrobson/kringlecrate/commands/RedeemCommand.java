package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        // Get the player's submitted gift
        String recipientUUID = player.getUniqueId().toString();
        ItemStack gift = plugin.getConfigManager().getGift(recipientUUID);

        if (gift == null) {
            player.sendMessage(ChatColor.RED + "You have no gifts to redeem.");
            return true;
        }

        // Ensure inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "You do not have enough inventory space to redeem your gift!");
            return true;
        }

        // Add gift to inventory and remove from config
        player.getInventory().addItem(gift);
        plugin.getConfigManager().getDataConfig().set("gifts." + recipientUUID, null);
        plugin.getConfigManager().saveDataFile();

        player.sendMessage(ChatColor.GREEN + "You have successfully redeemed your gift!");
        return true;
    }
}