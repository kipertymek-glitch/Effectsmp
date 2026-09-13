package com.smp.effects.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Podpowiada podkomendy /smp oraz nazwy graczy przy pisaniu komendy w czacie.
 */
public class SmpTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("start", "stop", "effect", "reset", "help");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("effect") || args[0].equalsIgnoreCase("reset"))) {
            String partial = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partial)) {
                    completions.add(player.getName());
                }
            }
            return completions;
        }

        return completions;
    }
}
