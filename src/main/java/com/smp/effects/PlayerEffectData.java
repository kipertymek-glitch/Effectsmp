package com.smp.effects;

/**
 * Przechowuje informacje o efekcie przypisanym do gracza.
 */
public class PlayerEffectData {

    private String effectTypeName;
    private int level;

    public PlayerEffectData(String effectTypeName, int level) {
        this.effectTypeName = effectTypeName;
        this.level = level;
    }

    public String getEffectTypeName() {
        return effectTypeName;
    }

    public void setEffectTypeName(String effectTypeName) {
        this.effectTypeName = effectTypeName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
