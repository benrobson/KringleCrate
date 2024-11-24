package me.benrobson.kringlecrate.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandCompleteManager implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            // Suggest main subcommands
            return filter(Arrays.asList("join", "reveal", "submit", "redeem"), args[0]);
        }

        // Return an empty list if no matches
        return new ArrayList<>();
    }

    /**
     * Filters suggestions based on the user's input.
     */
    private List<String> filter(List<String> options, String input) {
        List<String> results = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(input.toLowerCase())) {
                results.add(option);
            }
        }
        return results;
    }
}
