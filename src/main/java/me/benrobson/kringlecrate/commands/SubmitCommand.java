package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SubmitCommand implements CommandExecutor {

    private final KringleCrate plugin;

    public SubmitCommand(KringleCrate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = plugin.getConfig();

        // Check if the reveal date has passed
        long revealDate = config.getLong("revealDate"); // Assuming revealDate is stored as a timestamp
        long currentDate = System.currentTimeMillis();
        if (currentDate > revealDate) {
            player.sendMessage(ChatColor.RED + "The reveal date has passed. You can no longer submit gifts.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Please specify an item to submit.");
            return false;
        }

        // Get the assigned recipient from the data.yml
        String assignedPlayer = plugin.getAssignedPlayer(player.getName()); // Assuming you have a method to get this

        if (assignedPlayer == null) {
            player.sendMessage(ChatColor.RED + "You do not have an assigned person to submit to.");
            return true;
        }

        // Check if the player is holding an item
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must be holding an item to submit.");
            return true;
        }

        // Create a new entry in the data.yml for the assigned player
        String path = "submissions." + assignedPlayer; // Store under the assigned player's name
        List<String> gifts = config.getStringList(path);
        String itemData = serializeItem(item);

        gifts.add(itemData);
        config.set(path, gifts);
        plugin.saveConfig(); // Save the updated config

        player.sendMessage(ChatColor.GREEN + "Your gift has been submitted to " + assignedPlayer + "!");
        return true;
    }

    // Method to serialize item data (name, enchantments, etc.)
    private String serializeItem(ItemStack item) {
        StringBuilder itemData = new StringBuilder();

        // Store item name and lore (if available)
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            itemData.append("Name: ").append(meta.getDisplayName()).append("\n");
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (String line : lore) {
                    itemData.append("Lore: ").append(line).append("\n");
                }
            }

            // Store enchantments
            meta.getEnchants().forEach((enchantment, level) -> {
                itemData.append("Enchantment: ").append(enchantment.getKey().getKey())
                        .append(" Level: ").append(level).append("\n");
            });
        }

        // Store item type
        itemData.append("Type: ").append(item.getType().name()).append("\n");
        itemData.append("Amount: ").append(item.getAmount()).append("\n");

        return itemData.toString();
    }
}
