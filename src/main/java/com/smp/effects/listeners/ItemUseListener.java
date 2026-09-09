package com.smp.effects.listeners;

import com.smp.effects.CustomItems;
import com.smp.effects.EffectManager;
import com.smp.effects.PlayerEffectData;
import com.smp.effects.SmpEffectsPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemUseListener implements Listener {

    private final SmpEffectsPlugin plugin;
    private final EffectManager effectManager;
    private final CustomItems customItems;

    public ItemUseListener(SmpEffectsPlugin plugin) {
        this.plugin = plugin;
        this.effectManager = plugin.getEffectManager();
        this.customItems = plugin.getCustomItems();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (customItems.isUpgrader(item)) {
            event.setCancelled(true);
            handleUpgrader(event, item);
        } else if (customItems.isReroll(item)) {
            event.setCancelled(true);
            handleReroll(event, item);
        }
    }

    private void handleUpgrader(PlayerInteractEvent event, ItemStack item) {
        var player = event.getPlayer();
        if (!effectManager.hasEffect(player.getUniqueId())) {
            player.sendMessage(effectManager.msg("upgrade-no-effect"));
            return;
        }
        boolean success = effectManager.upgradeEffect(player);
        if (!success) {
            player.sendMessage(effectManager.msg("upgrade-max"));
            return;
        }
        consumeOne(event, item);
        PlayerEffectData data = effectManager.getData(player.getUniqueId());
        player.sendMessage(effectManager.msg("upgrade-success")
                .replace("%level%", effectManager.toRoman(data.getLevel())));
    }

    private void handleReroll(PlayerInteractEvent event, ItemStack item) {
        var player = event.getPlayer();
        if (!effectManager.hasEffect(player.getUniqueId())) {
            player.sendMessage(effectManager.msg("reroll-no-effect"));
            return;
        }
        PlayerEffectData data = effectManager.rerollEffect(player);
        if (data == null) {
            player.sendMessage(effectManager.msg("reroll-no-effect"));
            return;
        }
        consumeOne(event, item);
        player.sendMessage(effectManager.msg("reroll-success")
                .replace("%effect%", effectManager.formatEffectName(data.getEffectTypeName()))
                .replace("%level%", effectManager.toRoman(data.getLevel())));
    }

    private void consumeOne(PlayerInteractEvent event, ItemStack item) {
        if (event.getPlayer().getGameMode().name().equals("CREATIVE")) {
            return;
        }
        item.setAmount(item.getAmount() - 1);
    }
}
