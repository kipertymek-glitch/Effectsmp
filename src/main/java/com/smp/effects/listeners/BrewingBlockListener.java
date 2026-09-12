package com.smp.effects.listeners;

import com.smp.effects.EffectManager;
import com.smp.effects.SmpEffectsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

/**
 * Blokuje ekonomię mikstur na serwerze, żeby stałe efekty z pluginu (zdobywane
 * przez /smp start i craft) miały realne znaczenie i nie dało się ich obejść
 * warząc zwykłe mikstury. Wyjątkiem jest Weakness (potrzebna np. do leczenia
 * zombie-mieszkańców), która nadal da się uwarzyć.
 *
 * - Nie da się zebrać brodawki Nether (rośnie, ale nie można jej złamać).
 * - Warzenie w warzelni jest zablokowane dla wszystkiego OPRÓCZ Weakness.
 */
public class BrewingBlockListener implements Listener {

    private final SmpEffectsPlugin plugin;
    private final EffectManager effectManager;

    public BrewingBlockListener(SmpEffectsPlugin plugin) {
        this.plugin = plugin;
        this.effectManager = plugin.getEffectManager();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.NETHER_WART) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        player.sendMessage(effectManager.msg("wart-blocked"));
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        boolean isWeakness = false;
        for (ItemStack result : event.getResults()) {
            if (result == null) {
                continue;
            }
            if (result.getItemMeta() instanceof PotionMeta potionMeta) {
                if (potionMeta.getBasePotionType() == PotionType.WEAKNESS) {
                    isWeakness = true;
                    break;
                }
            }
        }
        if (!isWeakness) {
            event.setCancelled(true);
        }
    }
}
