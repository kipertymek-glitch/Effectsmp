package com.smp.effects.listeners;

import com.smp.effects.EffectManager;
import com.smp.effects.SmpEffectsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
        } else if (effectManager.isEventActive()) {
            // wydarzenie SMP jest aktywne (po /smp start) - nowy gracz od razu dostaje losowy efekt
            plugin.getServer().getScheduler().runTaskLater(plugin,
                    () -> effectManager.assignRandomEffect(event.getPlayer()), 20L);
        }
    }

    /**
     * Minecraft domyślnie czyści wszystkie efekty mikstur przy odrodzeniu po śmierci.
     * Musimy nałożyć zapisany (ewentualnie obniżony) efekt ponownie zaraz po respawnie,
     * inaczej efekt "znika" wizualnie mimo że dane gracza są nienaruszone.
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (effectManager.hasEffect(event.getPlayer().getUniqueId())) {
            plugin.getServer().getScheduler().runTaskLater(plugin,
                    () -> effectManager.applyEffect(event.getPlayer()), 1L);
        }
    }
}
