package com.example.dawn.models;

import java.util.ArrayList;
import java.util.List;

public class Character {
    private String imagePath;
    private String name;
    private String description;
    private int unlockCost;
    private int hp;
    private int initialHp; // Store the original HP value for resetting
    private int speed = 300; // Movement speed in pixels per second (default)
    private int score = 0;
    private int kills = 0;
    private int survivalDuration = 0;
    private int xp = 0;
    private Weapon weapon;
    private ArrayList<String> stillImagePaths;
    private ArrayList<String> movingImagePaths;
    
    // Ability system
    private List<Ability> permanentAbilities;
    private List<ActiveAbility> activeAbilities;
    private int bonusMaxAmmo = 0; // From AMOCREASE ability
    private int bonusProjectiles = 0; // From PROCREASE ability
    
    public Character() {
        this.permanentAbilities = new ArrayList<>();
        this.activeAbilities = new ArrayList<>();
    }

    public Character(String name, String imagePath, int hp, int unlockCost, String description) {
        this.imagePath = imagePath;
        this.name = name;
        this.description = description;
        this.unlockCost = unlockCost;
        this.hp = hp;
        this.initialHp = hp; // Store the initial HP value
        this.speed = 300; // Default speed
        this.stillImagePaths = new ArrayList<>();
        this.movingImagePaths = new ArrayList<>();
        this.permanentAbilities = new ArrayList<>();
        this.activeAbilities = new ArrayList<>();
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getUnlockCost() {
        return unlockCost;
    }

    public int getHp() {
        return hp;
    }

    public int getScore() {
        return score;
    }

    public int getKills() {
        return kills;
    }

    public int getSurvivalDuration() {
        return survivalDuration;
    }

    public int getXp() {
        return xp;
    }
    
    public int getLevel() {
        // Level starts from 1, needs 20*i XP to reach level i+1
        // Level 1: 0-19 XP, Level 2: 20-59 XP, Level 3: 60-119 XP, etc.
        int level = 1;
        int totalXpNeeded = 0;
        
        while (totalXpNeeded <= xp) {
            totalXpNeeded += 20 * level;
            if (totalXpNeeded <= xp) {
                level++;
            }
        }
        
        return level;
    }
    
    public int getXpForCurrentLevel() {
        // XP accumulated for the current level
        int level = 1;
        int totalXpUsed = 0;
        
        while (true) {
            int xpNeededForThisLevel = 20 * level;
            if (totalXpUsed + xpNeededForThisLevel <= xp) {
                totalXpUsed += xpNeededForThisLevel;
                level++;
            } else {
                break;
            }
        }
        
        return xp - totalXpUsed;
    }
    
    public int getXpNeededForNextLevel() {
        // XP needed to reach the next level
        return 20 * getLevel();
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnlockCost(int unlockCost) {
        this.unlockCost = unlockCost;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setSurvivalDuration(int survivalDuration) {
        this.survivalDuration = survivalDuration;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
    
    public void addXp(int amount) {
        this.xp += amount;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public ArrayList<String> getStillImagePaths() {
        return stillImagePaths;
    }

    public ArrayList<String> getMovingImagePaths() {
        return movingImagePaths;
    }

    public void setStillImagePaths(ArrayList<String> stillImagePaths) {
        this.stillImagePaths = stillImagePaths;
    }

    public void setMovingImagePaths(ArrayList<String> movingImagePaths) {
        this.movingImagePaths = movingImagePaths;
    }
    
    public int getInitialHp() {
        return initialHp;
    }
    
    public void setInitialHp(int initialHp) {
        this.initialHp = initialHp;
    }
    
    public void resetHp() {
        this.hp = this.initialHp;
    }
    
    public void resetForNewGame() {
        // Reset HP to initial value
        this.hp = this.initialHp;
        
        // DO NOT reset accumulated stats (score, kills, survivalDuration, xp) - these should persist across games
        // Only reset temporary game state:
        
        // Clear all abilities (both permanent and active)
        this.permanentAbilities.clear();
        this.activeAbilities.clear();
        
        // Reset ability bonuses
        this.bonusMaxAmmo = 0;
        this.bonusProjectiles = 0;
        
        System.out.println("Character reset for new game - HP: " + this.hp + ", Accumulated stats preserved (Score: " + this.score + ", Kills: " + this.kills + ", XP: " + this.xp + "), Abilities cleared, Bonuses reset");
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    // Ability system methods
    public List<Ability> getPermanentAbilities() {
        return permanentAbilities;
    }
    
    public List<ActiveAbility> getActiveAbilities() {
        return activeAbilities;
    }
    
    public void addPermanentAbility(Ability ability) {
        if (!permanentAbilities.contains(ability)) {
            permanentAbilities.add(ability);
        }
    }
    
    public void addActiveAbility(Ability ability) {
        // Remove existing instance of same ability if present
        activeAbilities.removeIf(active -> active.getAbility() == ability);
        // Add new instance
        activeAbilities.add(new ActiveAbility(ability, ability.getDuration()));
    }
    
    public boolean hasActiveAbility(Ability ability) {
        return activeAbilities.stream().anyMatch(active -> active.getAbility() == ability);
    }
    
    public int getBonusMaxAmmo() {
        return bonusMaxAmmo;
    }
    
    public void addBonusMaxAmmo(int amount) {
        this.bonusMaxAmmo += amount;
    }
    
    public int getBonusProjectiles() {
        return bonusProjectiles;
    }
    
    public void addBonusProjectiles(int amount) {
        this.bonusProjectiles += amount;
    }
    
    public int getMaxHp() {
        // Base HP + VITALITY bonuses
        int vitalityCount = (int) permanentAbilities.stream().filter(ability -> ability == Ability.VITALITY).count();
        return initialHp + vitalityCount;
    }
    
    public void updateActiveAbilities(float delta) {
        // Update timers and remove expired abilities
        activeAbilities.removeIf(active -> {
            active.update(delta);
            return active.isExpired();
        });
    }
}