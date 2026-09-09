package com.smp.effects.listeners;

import com.smp.effects.EffectManager;
import com.smp.effects.SmpEffectsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final SmpEffectsPlugin plugin;
    private final EffectManager effectManager;

    public JoinListener(SmpEffectsPlugin plugin) {
        this.plugin = plugin;
        this.effectManager = plugin.getEffectManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (effectManager.hasEffect(event.getPlayer().getUniqueId())) {
            // opóźnienie, aby efekt nałożył się poprawnie po pełnym załadowaniu gracza
            plugin.getServer().getScheduler().runTaskLater(plugin,
                    () -> effectManager.applyEffect(event.getPlayer()), 20L);
        }
    }
}
