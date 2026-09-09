package com.smp.effects.listeners;

import com.smp.effects.EffectManager;
import com.smp.effects.PlayerEffectData;
import com.smp.effects.SmpEffectsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;

/**
 * Gdy gracz przeżyje śmierć dzięki Totemowi Nieśmiertelności, traci swój
 * stały efekt całkowicie (ryzyko dla mocno rozwiniętych graczy).
 */
public class TotemListener implements Listener {

    private final SmpEffectsPlugin plugin;
    private final EffectManager effectManager;

    public TotemListener(SmpEffectsPlugin plugin) {
        this.plugin = plugin;
        this.effectManager = plugin.getEffectManager();
    }

    @EventHandler
    public void onResurrect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        PlayerEffectData data = effectManager.getData(player.getUniqueId());
        if (data != null) {
            effectManager.removeEffectEntirely(player);
            player.sendMessage(effectManager.msg("effect-lost-totem"));
        }
    }
}
