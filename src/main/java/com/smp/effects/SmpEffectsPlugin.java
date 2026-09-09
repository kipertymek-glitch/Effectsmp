package com.smp.effects;

import com.smp.effects.commands.SmpCommand;
import com.smp.effects.listeners.DeathListener;
import com.smp.effects.listeners.ItemUseListener;
import com.smp.effects.listeners.JoinListener;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class SmpEffectsPlugin extends JavaPlugin {

    private EffectManager effectManager;
    private CustomItems customItems;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.effectManager = new EffectManager(this);
        this.customItems = new CustomItems(this);
        effectManager.load();

        getCommand("smp").setExecutor(new SmpCommand(this));

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemUseListener(this), this);

        registerRecipes();

        getLogger().info("SmpEffects załadowany pomyślnie!");
    }

    @Override
    public void onDisable() {
        if (effectManager != null) {
            effectManager.saveAll();
        }
        getLogger().info("SmpEffects wyłączony, dane zapisane.");
    }

    private void registerRecipes() {
        // Ulepszacz Efektu: 4x głowa gracza + 4x złote jabłko + 1x diament
        NamespacedKey upgraderRecipeKey = new NamespacedKey(this, "smp_upgrader_recipe");
        ItemStack upgraderResult = customItems.createUpgraderItem();
        ShapedRecipe upgraderRecipe = new ShapedRecipe(upgraderRecipeKey, upgraderResult);
        upgraderRecipe.shape("PGP", "GDG", "PGP");
        upgraderRecipe.setIngredient('P', Material.PLAYER_HEAD);
        upgraderRecipe.setIngredient('G', Material.GOLDEN_APPLE);
        upgraderRecipe.setIngredient('D', Material.DIAMOND);
        getServer().addRecipe(upgraderRecipe);

        // Losowanie Efektu: 2x głowa gracza + 1x perła Endermana + 1x sztabka złota
        NamespacedKey rerollRecipeKey = new NamespacedKey(this, "smp_reroll_recipe");
        ItemStack rerollResult = customItems.createRerollItem();
        ShapelessRecipe rerollRecipe = new ShapelessRecipe(rerollRecipeKey, rerollResult);
        rerollRecipe.addIngredient(2, Material.PLAYER_HEAD);
        rerollRecipe.addIngredient(1, Material.ENDER_PEARL);
        rerollRecipe.addIngredient(1, Material.GOLD_INGOT);
        getServer().addRecipe(rerollRecipe);
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public CustomItems getCustomItems() {
        return customItems;
    }
}
