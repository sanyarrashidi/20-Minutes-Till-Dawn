package com.example.dawn.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {
    protected Vector2 position;
    protected float size;
    protected int hp;
    protected boolean active;
    protected Animation<TextureRegion> animation;
    protected float animationTime;
    
    public Enemy(float x, float y, float size, int hp) {
        this.position = new Vector2(x, y);
        this.size = size;
        this.hp = hp;
        this.active = true;
        this.animationTime = 0f;
    }
    
    public abstract void update(float delta);
    
    public abstract void render(SpriteBatch batch);
    
    public abstract void onPlayerCollision();
    
    public Vector2 getPosition() {
        return position;
    }
    
    public float getSize() {
        return size;
    }
    
    public int getHp() {
        return hp;
    }
    
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            active = false;
        }
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    
    public boolean collidesWith(Vector2 otherPosition, float otherRadius) {
        float distance = position.dst(otherPosition);
        return distance < (size / 2f + otherRadius);
    }
} 