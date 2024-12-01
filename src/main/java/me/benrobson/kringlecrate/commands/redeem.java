package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import me.benrobson.kringlecrate.utils.ConfigManager;
import me.benrobson.kringlecrate.utils.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class redeem implements CommandExecutor {

    private final KringleCrate plugin;
    private final ConfigManager configManager;

    public redeem(KringleCrate plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager(); // Get ConfigManager instance
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Debug: Check UUID
        plugin.getLogger().info("Redeem command triggered by player: " + playerUUID);

        // Check if the current date is within the redemption period
        if (!DateUtils.isInRedemptionPeriod()) {
            player.sendMessage(ChatColor.RED + "Gifts can only be redeemed during the redemption period: "
                    + ChatColor.GOLD + DateUtils.getFormattedRedemptionPeriod());
            return true;
        }

        // Retrieve the gifts from config.yml
        List<?> rawGifts = configManager.getConfig().getList("gifts." + playerUUID.toString());

        // Ensure the list is not null and of the correct type
        if (rawGifts == null || rawGifts.isEmpty() || !(rawGifts.get(0) instanceof ItemStack)) {
            player.sendMessage(ChatColor.RED + "You have no gifts to redeem.");
            plugin.getLogger().info("No gifts found for player: " + playerUUID);
            return true;
        }

        List<ItemStack> gifts = (List<ItemStack>) rawGifts; // Safely cast to List<ItemStack>

        plugin.getLogger().info("Gifts found for player: " + playerUUID + ", count: " + gifts.size());

        // Process the gifts
        for (ItemStack gift : gifts) {
            // Check inventory space
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(ChatColor.RED + "You don't have enough inventory space.");
                return true;
            }

            // Add the item to the player's inventory
            player.getInventory().addItem(gift);
        }

        // Remove redeemed gifts from config.yml
        configManager.getConfig().set("gifts." + playerUUID.toString(), null);
        configManager.saveConfigFile();

        player.sendMessage(ChatColor.GREEN + "All your gifts have been redeemed!");
        return true;
    }
}
