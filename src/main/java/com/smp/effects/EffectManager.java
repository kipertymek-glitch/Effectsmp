package com.smp.effects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Centralna logika przypisywania, ulepszania, rerollowania i usuwania
 * stałych efektów graczy.
 */
public class EffectManager {

    private final SmpEffectsPlugin plugin;
    private final DataStore dataStore;
    private final Map<UUID, PlayerEffectData> cache = new HashMap<>();
    private final Random random = new Random();

    public EffectManager(SmpEffectsPlugin plugin) {
        this.plugin = plugin;
        this.dataStore = new DataStore(plugin);
    }

    public void load() {
        dataStore.load();
        cache.clear();
        cache.putAll(dataStore.loadAll());
    }

    public void saveAll() {
        dataStore.saveAll(cache);
    }

    public List<String> getEffectPool() {
        return plugin.getConfig().getStringList("effect-pool");
    }

    public int getMaxLevel() {
        return plugin.getConfig().getInt("max-level", 2);
    }

    public PlayerEffectData getData(UUID uuid) {
        return cache.get(uuid);
    }

    public boolean hasEffect(UUID uuid) {
        return cache.containsKey(uuid);
    }

    /**
     * Losuje i przypisuje graczowi nowy, stały efekt z puli (poziom 1).
     */
    public PlayerEffectData assignRandomEffect(Player player) {
        List<String> pool = getEffectPool();
        if (pool.isEmpty()) {
            return null;
        }
        String chosen = pool.get(random.nextInt(pool.size()));
        PlayerEffectData data = new PlayerEffectData(chosen, 1);
        cache.put(player.getUniqueId(), data);
        applyEffect(player);
        saveAll();
        return data;
    }

    /**
     * Rerolluje efekt gracza na nowy losowy z puli, resetując poziom do 1.
     */
    public PlayerEffectData rerollEffect(Player player) {
        PlayerEffectData existing = cache.get(player.getUniqueId());
        if (existing == null) {
            return null;
        }
        removePotionEffect(player, existing.getEffectTypeName());
        List<String> pool = getEffectPool();
        String chosen = pool.get(random.nextInt(pool.size()));
        existing.setEffectTypeName(chosen);
        existing.setLevel(1);
        applyEffect(player);
        saveAll();
        return existing;
    }

    /**
     * Ulepsza efekt gracza o jeden poziom, jeśli nie osiągnął maksimum.
     * Zwraca true jeśli się udało.
     */
    public boolean upgradeEffect(Player player) {
        PlayerEffectData data = cache.get(player.getUniqueId());
        if (data == null) {
            return false;
        }
        if (data.getLevel() >= getMaxLevel()) {
            return false;
        }
        data.setLevel(data.getLevel() + 1);
        applyEffect(player);
        saveAll();
        return true;
    }

    /**
     * Całkowicie usuwa efekt gracza (np. gdy zginie z ulepszonym efektem).
     */
      /**
     * Całkowicie usuwa efekt gracza (np. po użyciu Totemu Nieśmiertelności).
     */
    public void removeEffectEntirely(Player player) {
        PlayerEffectData data = cache.remove(player.getUniqueId());
        if (data != null) {
            removePotionEffect(player, data.getEffectTypeName());
            saveAll();
        }
    }

    /**
     * Obniża poziom efektu gracza o 1 (minimum poziom 1) - używane przy normalnej śmierci.
     * Efekt nie znika całkowicie, traci się tylko ewentualny bonus z Ulepszacza.
     * Zwraca true jeśli poziom faktycznie się obniżył.
     */
    public boolean downgradeLevel(Player player) {
        PlayerEffectData data = cache.get(player.getUniqueId());
        if (data == null) {
            return false;
        }
        if (data.getLevel() <= 1) {
            return false;
        }
        data.setLevel(data.getLevel() - 1);
        applyEffect(player);
        saveAll();
        return true;
    }

    /**
     * Nakłada efekt aktualnie zapisany w danych gracza (np. po dołączeniu do serwera).
     */
    public void applyEffect(Player player) {
        PlayerEffectData data = cache.get(player.getUniqueId());
        if (data == null) {
            return;
        }
        PotionEffectType type = PotionEffectType.getByName(data.getEffectTypeName());
        if (type == null) {
            plugin.getLogger().warning("Nieznany typ efektu w konfiguracji: " + data.getEffectTypeName());
            return;
        }
        int amplifier = Math.max(0, data.getLevel() - 1);
        PotionEffect effect = new PotionEffect(type, Integer.MAX_VALUE, amplifier, true, false, true);
        player.addPotionEffect(effect);
    }

    private void removePotionEffect(Player player, String typeName) {
        PotionEffectType type = PotionEffectType.getByName(typeName);
        if (type != null && player.hasPotionEffect(type)) {
            player.removePotionEffect(type);
        }
    }

    public String formatEffectName(String typeName) {
        String[] parts = typeName.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    public String toRoman(int level) {
        switch (level) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            default: return String.valueOf(level);
        }
    }

    public String msg(String key) {
        String raw = plugin.getConfig().getString("messages." + key, "");
        return ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.prefix", "") + raw);
    }
}
