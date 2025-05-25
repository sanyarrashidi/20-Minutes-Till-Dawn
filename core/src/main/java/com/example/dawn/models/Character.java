package com.example.dawn.models;

import java.util.ArrayList;

public class Character {
    private String imagePath;
    private String name;
    private String description;
    private int unlockCost;
    private int hp;
    private int score = 0;
    private int kills = 0;
    private int survivalDuration = 0;
    private Weapon weapon;
    private ArrayList<String> stillImagePaths;
    private ArrayList<String> movingImagePaths;
    
    public Character() {
    }

    public Character(String name, String imagePath, int hp, int unlockCost, String description) {
        this.imagePath = imagePath;
        this.name = name;
        this.description = description;
        this.unlockCost = unlockCost;
        this.hp = hp;
        this.stillImagePaths = new ArrayList<>();
        this.movingImagePaths = new ArrayList<>();
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
}