package me.benrobson.kringlecrate.commands;

import me.benrobson.kringlecrate.KringleCrate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandManager implements CommandExecutor {

    private final KringleCrate plugin;

    public CommandManager(KringleCrate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /kk <join|reveal|submit|redeem>");
            return true;
        }

        // Dispatch to the correct subcommand
        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "join":
                return new JoinCommand(plugin).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            case "reveal":
                return new RevealCommand(plugin).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            case "submit":
                return new SubmitCommand(plugin).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            case "redeem":
                return new RedeemCommand(plugin).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Usage: /kk <join|reveal|submit|redeem>");
                return true;
        }
    }
}
