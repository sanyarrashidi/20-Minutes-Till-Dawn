package com.example.dawn.models;

public enum Ability {
    VITALITY("Vitality", "Increase maximum HP by 1 and heal 1 HP"),
    DAMAGER("Damager", "Increase weapon damage by 25% for 10 seconds"),
    PROCREASE("Procrease", "Increase weapon projectile count by 1"),
    AMOCREASE("Amocrease", "Increase maximum ammo by 5"),
    SPEEDY("Speedy", "Double movement speed for 10 seconds");
    
    private final String name;
    private final String description;
    
    Ability(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isTemporary() {
        return this == DAMAGER || this == SPEEDY;
    }
    
    public float getDuration() {
        if (isTemporary()) {
            return 10f; // 10 seconds for temporary abilities
        }
        return 0f;
    }
} 