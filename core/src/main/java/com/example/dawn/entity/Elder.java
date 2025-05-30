package com.example.dawn.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.example.dawn.models.App;
import com.example.dawn.models.Player;

public class Elder extends Enemy {
    private static final int ELDER_HP = 400;
    private static final float ELDER_SIZE = 160f; 
    private static final int DAMAGE_TO_PLAYER = 2; 
    private static final float DAMAGE_COOLDOWN = 1.0f;
    private static final float DASH_INTERVAL = 5.0f; 
    private static final float DASH_SPEED = 400f; 
    private static final float DASH_DURATION = 1.0f; 
    private static final float NORMAL_SPEED = 20f; 
    
    private float lastDamageTime = 0f;
    private float dashTimer = 0f;
    private boolean isDashing = false;
    private float dashTimeRemaining = 0f;
    private Vector2 targetPosition;
    private Vector2 dashDirection;
    
    public Elder(float x, float y) {
        super(x, y, ELDER_SIZE, ELDER_HP);
        this.targetPosition = new Vector2();
        this.dashDirection = new Vector2();
        loadAnimation();
    }
    
    private void loadAnimation() {
        Array<TextureRegion> frames = new Array<>();
        
        try {
            
            Texture texture = new Texture(Gdx.files.internal("enemies/ElderBrain.png"));
            frames.add(new TextureRegion(texture));
            System.out.println("Loaded elder texture: enemies/ElderBrain.png");
            
            
            animation = new Animation<>(1f, frames, Animation.PlayMode.LOOP);
            System.out.println("Created elder animation");
            
        } catch (Exception e) {
            System.out.println("Failed to load elder texture: " + e.getMessage());
            
            frames.clear();
            frames.add(new TextureRegion(new Texture(Gdx.files.internal("textures/T_TileGrass.png"))));
            animation = new Animation<>(1f, frames, Animation.PlayMode.LOOP);
        }
    }
    
    @Override
    public void update(float delta) {
        animationTime += delta;
        
        
        if (lastDamageTime > 0) {
            lastDamageTime -= delta;
            if (lastDamageTime < 0) {
                lastDamageTime = 0;
            }
        }
        
        
        dashTimer += delta;
        
        
        if (isDashing) {
            dashTimeRemaining -= delta;
            if (dashTimeRemaining <= 0) {
                isDashing = false;
                System.out.println("Elder finished dashing");
            } else {
                
                position.add(dashDirection.x * DASH_SPEED * delta, dashDirection.y * DASH_SPEED * delta);
            }
        } else {
            
            if (dashTimer >= DASH_INTERVAL) {
                startDash();
                dashTimer = 0f;
            } else {
                
                moveTowardTarget(delta);
            }
        }
    }
    
    private void startDash() {
        
        float deltaX = targetPosition.x - position.x;
        float deltaY = targetPosition.y - position.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 0) {
            
            dashDirection.set(deltaX / distance, deltaY / distance);
            isDashing = true;
            dashTimeRemaining = DASH_DURATION;
            System.out.println("Elder started dashing toward player!");
        }
    }
    
    private void moveTowardTarget(float delta) {
        
        float deltaX = targetPosition.x - position.x;
        float deltaY = targetPosition.y - position.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 10f) { 
            
            float moveX = (deltaX / distance) * NORMAL_SPEED * delta;
            float moveY = (deltaY / distance) * NORMAL_SPEED * delta;
            
            position.add(moveX, moveY);
        }
    }
    
    public void setTargetPosition(Vector2 target) {
        this.targetPosition.set(target);
    }
    
    public boolean isDashing() {
        return isDashing;
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (!active || animation == null) return;
        
        TextureRegion currentFrame = animation.getKeyFrame(animationTime);
        float renderX = position.x - size / 2f;
        float renderY = position.y - size / 2f;
        
        
        if (isDashing) {
            batch.setColor(1f, 0.7f, 0.7f, 1f); 
        }
        
        batch.draw(currentFrame, renderX, renderY, size, size);
        
        
        batch.setColor(1f, 1f, 1f, 1f);
    }
    
    @Override
    public void onPlayerCollision() {
        
        if (lastDamageTime <= 0) {
            Player player = App.getInstance().getPlayer();
            if (player != null && player.getCharacter() != null) {
                int currentHp = player.getCharacter().getHp();
                int newHp = Math.max(0, currentHp - DAMAGE_TO_PLAYER);
                player.getCharacter().setHp(newHp);
                
                System.out.println("Elder damaged player! HP: " + currentHp + " -> " + newHp);
                
                
                lastDamageTime = DAMAGE_COOLDOWN;
                
                
                if (newHp <= 0) {
                    System.out.println("Player died from Elder damage!");
                }
            }
        }
    }
    
    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        System.out.println("Elder took " + damage + " damage! HP: " + hp);
        
        if (hp <= 0) {
            System.out.println("Elder has been defeated!");
        }
    }
} 