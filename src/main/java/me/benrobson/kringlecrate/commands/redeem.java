package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import me.benrobson.kringlecrate.utils.ConfigManager;
import me.benrobson.kringlecrate.utils.DateUtils;
import me.benrobson.kringlecrate.utils.GiftManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class redeem implements CommandExecutor {

    private final KringleCrate plugin;
    private final ConfigManager configManager;
    private final GiftManager giftManager;
    private DateUtils dateUtils;

    public redeem(KringleCrate plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.giftManager = plugin.getGiftManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        plugin.getLogger().info("[REDEEM] Command triggered by player UUID: " + playerUUID);

        // Check redemption period
        if (!dateUtils.isInRedemptionPeriod()) {
            String redemptionPeriod = dateUtils.getFormattedRedemptionPeriod();
            player.sendMessage(ChatColor.RED + "Gifts can only be redeemed during the redemption period: "
                    + ChatColor.GOLD + redemptionPeriod);
            plugin.getLogger().info("[REDEEM] Redemption attempt outside period by " + playerUUID + ". Period: " + redemptionPeriod);
            return true;
        }

        // Retrieve and deserialize gifts using GiftManager
        List<ItemStack> gifts = giftManager.getGifts(playerUUID);
        if (gifts.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You have no valid gifts to redeem.");
            plugin.getLogger().info("[REDEEM] No valid gifts found for player UUID: " + playerUUID);
            return true;
        }

        // Check inventory space
        int freeSlots = countFreeInventorySlots(player);
        if (freeSlots < gifts.size()) {
            player.sendMessage(ChatColor.RED + "You don't have enough inventory space for all your gifts.");
            plugin.getLogger().info("[REDEEM] Insufficient inventory space for player UUID: " + playerUUID + ". Needed: " + gifts.size() + ", Available: " + freeSlots);
            return true;
        }

        // Add gifts to inventory and clear the gifts
        addGiftsToInventoryAndClearConfig(player, gifts, playerUUID);
        player.sendMessage(ChatColor.GREEN + "All your gifts have been redeemed!");
        plugin.getLogger().info("[REDEEM] Successfully redeemed gifts for player UUID: " + playerUUID);

        return true;
    }

    private int countFreeInventorySlots(Player player) {
        int freeSlots = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) freeSlots++;
        }
        return freeSlots;
    }

    private void addGiftsToInventoryAndClearConfig(Player player, List<ItemStack> gifts, UUID playerUUID) {
        try {
            for (ItemStack gift : gifts) {
                player.getInventory().addItem(gift);
            }
            giftManager.clearGifts(playerUUID);  // Use GiftManager's clearGifts method to remove redeemed gifts from the config
        } catch (Exception e) {
            plugin.getLogger().severe("[REDEEM] Error adding gifts to inventory or updating config for player UUID: " + playerUUID);
            plugin.getLogger().severe(e.getMessage());
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "An error occurred while redeeming your gifts. Please contact an administrator.");
        }
    }
}