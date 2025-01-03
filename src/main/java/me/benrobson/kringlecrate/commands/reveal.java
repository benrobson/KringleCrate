package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import me.benrobson.kringlecrate.utils.DateUtils;
import me.benrobson.kringlecrate.utils.FormatterUtils;
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
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Verify the reveal date or override permission
        if (!DateUtils.isRevealDay() && !player.hasPermission("kringlecrate.override")) {
            player.sendMessage(ChatColor.RED + "You cannot reveal your recipient until the reveal day: "
                    + ChatColor.GOLD + FormatterUtils.getFormattedRevealDate());
            return true;
        }

        // Run the recipient lookup asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ParticipantManager participantManager = plugin.getParticipantManager();

            // Validate if the player is part of the Secret Santa event
            List<String> participants = participantManager.getParticipants();
            if (!participants.contains(player.getUniqueId().toString())) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "You are not part of the Secret Santa event. Use /kc join.")
                );
                return;
            }

            // Ensure enough participants for assignments
            if (participants.size() <= 1) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "Not enough participants to assign a recipient!")
                );
                return;
            }

            // Assign recipients if not done already
            if (!plugin.getGiftConfigManager().getConfig().contains("assignments")) {
                participantManager.assignParticipants();
            }

            // Retrieve the recipient for the player
            String recipientUUID = plugin.getGiftConfigManager()
                    .getConfig()
                    .getString("assignments." + player.getUniqueId().toString());

            if (recipientUUID == null) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "No recipient assigned. Please contact an administrator.")
                );
                return;
            }

            // Fetch the recipient's name
            UUID recipientId = UUID.fromString(recipientUUID);
            OfflinePlayer recipientPlayer = Bukkit.getOfflinePlayer(recipientId);

            // Notify the player of their assigned recipient
            Bukkit.getScheduler().runTask(plugin, () ->
                    player.sendMessage(ChatColor.GREEN + "Your assigned recipient is: "
                            + ChatColor.GOLD + (recipientPlayer.getName() != null
                            ? recipientPlayer.getName()
                            : "Unknown Player")));
        });

        return true;
    }
}