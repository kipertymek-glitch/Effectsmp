package com.smp.effects.listeners;

import com.smp.effects.EffectManager;
import com.smp.effects.SmpEffectsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.BrewEvent;

/**
 * Blokuje ekonomię mikstur na serwerze, żeby stałe efekty z pluginu (zdobywane
 * przez /smp start i craft) miały realne znaczenie i nie dało się ich obejść
 * warząc zwykłe mikstury.
 *
 * - Nie da się zebrać brodawki Nether (rośnie, ale nie można jej złamać).
 * - Nawet gdyby ktoś zdobył brodawkę skądinąd (np. ze skrzyni w Nether Fortress),
 *   warzenie w warzelni jest całkowicie zablokowane.
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
        event.setCancelled(true);
    }
}
