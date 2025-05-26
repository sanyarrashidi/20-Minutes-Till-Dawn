package com.example.dawn.models;

public class ActiveAbility {
    private Ability ability;
    private float remainingTime;
    
    // No-arg constructor for JSON serialization
    public ActiveAbility() {
    }
    
    public ActiveAbility(Ability ability, float duration) {
        this.ability = ability;
        this.remainingTime = duration;
    }
    
    public Ability getAbility() {
        return ability;
    }
    
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    public float getRemainingTime() {
        return remainingTime;
    }
    
    public void setRemainingTime(float remainingTime) {
        this.remainingTime = remainingTime;
    }
    
    public void update(float delta) {
        remainingTime -= delta;
    }
    
    public boolean isExpired() {
        return remainingTime <= 0;
    }
    
    public int getRemainingSeconds() {
        return Math.max(0, (int) Math.ceil(remainingTime));
    }
} 