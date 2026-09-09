package com.smp.effects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Odpowiada za trwały zapis danych graczy (efekt + poziom) do pliku data.yml,
 * aby przetrwały restart serwera.
 */
public class DataStore {

    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration yaml;

    public DataStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
    }

    public void load() {
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Nie udało się utworzyć data.yml", e);
            }
        }
        yaml = YamlConfiguration.loadConfiguration(file);
    }

    public Map<UUID, PlayerEffectData> loadAll() {
        Map<UUID, PlayerEffectData> map = new HashMap<>();
        if (yaml.contains("players")) {
            for (String key : yaml.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String type = yaml.getString("players." + key + ".type");
                    int level = yaml.getInt("players." + key + ".level", 1);
                    map.put(uuid, new PlayerEffectData(type, level));
                } catch (IllegalArgumentException ignored) {
                    // niepoprawny UUID w pliku - pomijamy
                }
            }
        }
        return map;
    }

    public void saveAll(Map<UUID, PlayerEffectData> data) {
        yaml.set("players", null); // czyścimy przed zapisem
        for (Map.Entry<UUID, PlayerEffectData> entry : data.entrySet()) {
            String path = "players." + entry.getKey();
            yaml.set(path + ".type", entry.getValue().getEffectTypeName());
            yaml.set(path + ".level", entry.getValue().getLevel());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Nie udało się zapisać data.yml", e);
        }
    }
}
