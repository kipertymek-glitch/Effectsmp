package com.smp.effects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

/**
 * Tworzy i rozpoznaje customowe itemy pluginu (Upgrader efektu, Reroll efektu).
 */
public class CustomItems {

    public static final String UPGRADER_KEY = "smp_upgrader";
    public static final String REROLL_KEY = "smp_reroll";

    private final NamespacedKey upgraderKey;
    private final NamespacedKey rerollKey;

    public CustomItems(SmpEffectsPlugin plugin) {
        this.upgraderKey = new NamespacedKey(plugin, UPGRADER_KEY);
        this.rerollKey = new NamespacedKey(plugin, REROLL_KEY);
    }

    public NamespacedKey getUpgraderKey() {
        return upgraderKey;
    }

    public NamespacedKey getRerollKey() {
        return rerollKey;
    }

    public ItemStack createUpgraderItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&lUlepszacz Efektu"));
        meta.setLore(color(Arrays.asList(
                "&7Użyj (PPM), aby ulepszyć",
                "&7swój stały efekt o 1 poziom.",
                "&7Maksymalny poziom: &eII"
        )));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(upgraderKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createRerollItem() {
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d&lLosowanie Efektu"));
        meta.setLore(color(Arrays.asList(
                "&7Użyj (PPM), aby wylosować",
                "&7nowy stały efekt.",
                "&7Poziom efektu wraca do &eI"
        )));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(rerollKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createPlayerHead(OfflinePlayer owner) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(owner);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                "&fGłowa gracza &e" + owner.getName()));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isUpgrader(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(upgraderKey, PersistentDataType.BYTE);
    }

    public boolean isReroll(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(rerollKey, PersistentDataType.BYTE);
    }

    private List<String> color(List<String> lines) {
        return lines.stream()
                .map(l -> ChatColor.translateAlternateColorCodes('&', l))
                .toList();
    }
}
