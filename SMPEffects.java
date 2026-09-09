package pl.smp.effects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public final class SMPEffects extends JavaPlugin implements CommandExecutor, Listener {

    private final List<PotionEffectType> allowedEffects = Arrays.asList(
            PotionEffectType.SPEED,
            PotionEffectType.STRENGTH,
            PotionEffectType.HASTE,
            PotionEffectType.RESISTANCE,
            PotionEffectType.JUMP_BOOST
    );

    private final Map<UUID, PotionEffectType> playerEffects = new HashMap<>();
    private final Map<UUID, Integer> playerLevels = new HashMap<>();

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("smpstart")).setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        registerCustomRecipes();
        getLogger().info("SMPEffects zostal uruchomiony poprawnie!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("smpstart")) {
            if (!sender.hasPermission("smpeffects.admin")) {
                sender.sendMessage(ChatColor.RED + "Nie masz uprawnień do tej komendy!");
                return true;
            }

            Random random = new Random();
            for (Player player : Bukkit.getOnlinePlayers()) {
                PotionEffectType randomEffect = allowedEffects.get(random.nextInt(allowedEffects.size()));
                playerEffects.put(player.getUniqueId(), randomEffect);
                playerLevels.put(player.getUniqueId(), 1);
                
                applyEffect(player);
                player.sendMessage(ChatColor.GREEN + "SMP wystartowało! Twój losowy efekt to: " + ChatColor.YELLOW + formatEffectName(randomEffect));
            }
            sender.sendMessage(ChatColor.GREEN + "Rozdano losowe efekty wszystkim graczom!");
            return true;
        }
        return false;
    }

    private void applyEffect(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerEffects.containsKey(uuid)) return;

        PotionEffectType type = playerEffects.get(uuid);
        int level = playerLevels.getOrDefault(uuid, 1) - 1;

        player.removePotionEffect(type);
        player.addPotionEffect(new PotionEffect(type, PotionEffect.INFINITE_DURATION, level, true, false, true));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        UUID uuid = victim.getUniqueId();

        if (playerEffects.containsKey(uuid)) {
            PotionEffectType type = playerEffects.get(uuid);
            int level = playerLevels.getOrDefault(uuid, 1);

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(victim);
                meta.setDisplayName(ChatColor.GOLD + "Głowa gracza: " + victim.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Efekt: " + formatEffectName(type));
                lore.add(ChatColor.GRAY + "Poziom: " + level);
                meta.setLore(lore);
                skull.setItemMeta(meta);
            }

            victim.getWorld().dropItemNaturally(victim.getLocation(), skull);
        }
    }

    private void registerCustomRecipes() {
        ItemStack upgradeBook = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = upgradeBook.getItemMeta();
        if (bookMeta != null) {
            bookMeta.setDisplayName(ChatColor.AQUA + "Ulepszenie Efektu (lvl 2)");
            bookMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Przeciągnij na siebie, aby zużyć"));
            upgradeBook.setItemMeta(bookMeta);
        }

        NamespacedKey upgradeKey = new NamespacedKey(this, "effect_upgrade_recipe");
        ShapedRecipe recipe1 = new ShapedRecipe(upgradeKey, upgradeBook);
        recipe1.shape(" H ", " H ", " S ");
        recipe1.setIngredient('H', Material.PLAYER_HEAD);
        recipe1.setIngredient('S', Material.NETHER_STAR);
        
        Bukkit.addRecipe(recipe1);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() == Material.BOOK && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Ulepszenie Efektu (lvl 2)")) {
                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();

                if (!playerEffects.containsKey(uuid)) {
                    player.sendMessage(ChatColor.RED + "Nie masz przypisanego żadnego efektu SMP!");
                    event.setCancelled(true);
                    return;
                }

                if (playerLevels.get(uuid) >= 2) {
                    player.sendMessage(ChatColor.RED + "Masz już maksymalny (2) poziom efektu!");
                    event.setCancelled(true);
                    return;
                }

                playerLevels.put(uuid, 2);
                applyEffect(player);
                player.sendMessage(ChatColor.GREEN + "Twój efekt został ulepszony do poziomu 2!");
                
                item.setAmount(item.getAmount() - 1);
                event.setCancelled(true);
            }
        }
    }

    private String formatEffectName(PotionEffectType type) {
        if (type.equals(PotionEffectType.SPEED)) return "Speed";
        if (type.equals(PotionEffectType.STRENGTH)) return "Strength";
        if (type.equals(PotionEffectType.HASTE)) return "Haste";
        if (type.equals(PotionEffectType.RESISTANCE)) return "Resistance";
        if (type.equals(PotionEffectType.JUMP_BOOST)) return "Jump Boost";
        return type.getName();
    }
}
