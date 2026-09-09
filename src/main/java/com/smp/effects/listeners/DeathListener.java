package com.smp.effects.listeners;

import com.smp.effects.CustomItems;
import com.smp.effects.EffectManager;
import com.smp.effects.PlayerEffectData;
import com.smp.effects.SmpEffectsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {

    private final SmpEffectsPlugin plugin;
    private final EffectManager effectManager;
    private final CustomItems customItems;

    public DeathListener(SmpEffectsPlugin plugin) {
        this.plugin = plugin;
        this.effectManager = plugin.getEffectManager();
        this.customItems = plugin.getCustomItems();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Zabójca (jeśli to gracz) dostaje głowę ofiary - surowiec do craftingu.
        if (killer != null) {
            ItemStack head = customItems.createPlayerHead(victim);
            if (killer.getInventory().firstEmpty() != -1) {
                killer.getInventory().addItem(head);
            } else {
                killer.getWorld().dropItemNaturally(killer.getLocation(), head);
            }
            killer.sendMessage(effectManager.msg("kill-head-received").replace("%player%", victim.getName()));
        }

        // Każda śmierć obniża poziom efektu o 1 (efekt nie znika całkowicie).
        PlayerEffectData data = effectManager.getData(victim.getUniqueId());
        if (data != null && effectManager.downgradeLevel(victim)) {
            victim.sendMessage(effectManager.msg("effect-downgraded")
                    .replace("%level%", effectManager.toRoman(data.getLevel())));
        }
    }
}
