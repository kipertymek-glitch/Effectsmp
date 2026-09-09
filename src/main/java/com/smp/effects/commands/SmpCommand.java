package com.smp.effects.commands;

import com.smp.effects.EffectManager;
import com.smp.effects.PlayerEffectData;
import com.smp.effects.SmpEffectsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SmpCommand implements CommandExecutor {

    private final SmpEffectsPlugin plugin;
    private final EffectManager effectManager;

    public SmpCommand(SmpEffectsPlugin plugin) {
        this.plugin = plugin;
        this.effectManager = plugin.getEffectManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("smp.admin")) {
            sender.sendMessage(effectManager.msg("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                handleStart(sender);
                return true;
            case "effect":
                handleEffectInfo(sender, args);
                return true;
            case "reset":
                handleReset(sender, args);
                return true;
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void handleStart(CommandSender sender) {
        int count = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerEffectData data = effectManager.assignRandomEffect(online);
            if (data != null) {
                String text = effectManager.msg("effect-assigned")
                        .replace("%effect%", effectManager.formatEffectName(data.getEffectTypeName()))
                        .replace("%level%", effectManager.toRoman(data.getLevel()));
                online.sendMessage(text);
                count++;
            }
        }
        Bukkit.broadcastMessage(effectManager.msg("start-broadcast"));
        sender.sendMessage(ChatColor.GREEN + "Przypisano losowe efekty " + count + " graczom.");
    }

    private void handleEffectInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Użycie: /smp effect <gracz>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Gracz nie jest online.");
            return;
        }
        PlayerEffectData data = effectManager.getData(target.getUniqueId());
        if (data == null) {
            sender.sendMessage(ChatColor.YELLOW + target.getName() + " nie ma jeszcze przypisanego efektu.");
            return;
        }
        sender.sendMessage(ChatColor.AQUA + target.getName() + ": "
                + effectManager.formatEffectName(data.getEffectTypeName())
                + " " + effectManager.toRoman(data.getLevel()));
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Użycie: /smp reset <gracz>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Gracz nie jest online.");
            return;
        }
        effectManager.removeEffectEntirely(target);
        sender.sendMessage(ChatColor.GREEN + "Usunięto efekt gracza " + target.getName() + ".");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- SmpEffects ---");
        sender.sendMessage(ChatColor.YELLOW + "/smp start " + ChatColor.GRAY + "- losuje efekty wszystkim online");
        sender.sendMessage(ChatColor.YELLOW + "/smp effect <gracz> " + ChatColor.GRAY + "- pokazuje efekt gracza");
        sender.sendMessage(ChatColor.YELLOW + "/smp reset <gracz> " + ChatColor.GRAY + "- usuwa efekt gracza");
    }
}
