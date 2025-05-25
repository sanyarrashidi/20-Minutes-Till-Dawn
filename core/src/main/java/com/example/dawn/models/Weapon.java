package com.example.dawn.models;

import java.util.ArrayList;

public class Weapon {
    private String name;
    private Integer damage;
    private Integer projectile;
    private Integer reloadTime;
    private Integer magazineSize;
    private String imagePath;
    private ArrayList<String> animations;
    
    public Weapon() { 
    }

    public Weapon(String name, Integer damage, Integer projectile, Integer reloadTime, Integer magazineSize, String imagePath, ArrayList<String> animations) {
        this.name = name;
        this.damage = damage;
        this.projectile = projectile;
        this.reloadTime = reloadTime;
        this.magazineSize = magazineSize;
        this.imagePath = imagePath;
        this.animations = animations;
    }
    
    public String getName() {
        return name;
    }

    public Integer getDamage() {
        return damage;
    }

    public Integer getProjectile() {
        return projectile;
    }

    public Integer getReloadTime() {
        return reloadTime;
    }

    public Integer getMagazineSize() {
        return magazineSize;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public void setProjectile(Integer projectile) {
        this.projectile = projectile;
    }

    public void setReloadTime(Integer reloadTime) {
        this.reloadTime = reloadTime;
    }

    public void setMagazineSize(Integer magazineSize) {
        this.magazineSize = magazineSize;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ArrayList<String> getAnimations() {
        return animations;
    }
}