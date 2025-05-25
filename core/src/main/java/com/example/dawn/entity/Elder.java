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
    private static final float ELDER_SIZE = 160f; // Large boss size
    private static final int DAMAGE_TO_PLAYER = 2; // More damage than other enemies
    private static final float DAMAGE_COOLDOWN = 1.0f;
    private static final float DASH_INTERVAL = 5.0f; // Dash every 5 seconds
    private static final float DASH_SPEED = 400f; // Fast dash speed
    private static final float DASH_DURATION = 1.0f; // Dash lasts 1 second
    private static final float NORMAL_SPEED = 20f; // Slow normal movement
    
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
            // Load elder brain texture
            Texture texture = new Texture(Gdx.files.internal("enemies/ElderBrain.png"));
            frames.add(new TextureRegion(texture));
            System.out.println("Loaded elder texture: enemies/ElderBrain.png");
            
            // Create simple animation (single frame for now)
            animation = new Animation<>(1f, frames, Animation.PlayMode.LOOP);
            System.out.println("Created elder animation");
            
        } catch (Exception e) {
            System.out.println("Failed to load elder texture: " + e.getMessage());
            // Create fallback animation
            frames.clear();
            frames.add(new TextureRegion(new Texture(Gdx.files.internal("textures/T_TileGrass.png"))));
            animation = new Animation<>(1f, frames, Animation.PlayMode.LOOP);
        }
    }
    
    @Override
    public void update(float delta) {
        animationTime += delta;
        
        // Update damage cooldown
        if (lastDamageTime > 0) {
            lastDamageTime -= delta;
            if (lastDamageTime < 0) {
                lastDamageTime = 0;
            }
        }
        
        // Update dash timer
        dashTimer += delta;
        
        // Handle dashing
        if (isDashing) {
            dashTimeRemaining -= delta;
            if (dashTimeRemaining <= 0) {
                isDashing = false;
                System.out.println("Elder finished dashing");
            } else {
                // Continue dashing in the same direction
                position.add(dashDirection.x * DASH_SPEED * delta, dashDirection.y * DASH_SPEED * delta);
            }
        } else {
            // Check if it's time to dash
            if (dashTimer >= DASH_INTERVAL) {
                startDash();
                dashTimer = 0f;
            } else {
                // Normal slow movement toward player
                moveTowardTarget(delta);
            }
        }
    }
    
    private void startDash() {
        // Calculate direction to player for dash
        float deltaX = targetPosition.x - position.x;
        float deltaY = targetPosition.y - position.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 0) {
            // Normalize direction for dash
            dashDirection.set(deltaX / distance, deltaY / distance);
            isDashing = true;
            dashTimeRemaining = DASH_DURATION;
            System.out.println("Elder started dashing toward player!");
        }
    }
    
    private void moveTowardTarget(float delta) {
        // Calculate direction to target
        float deltaX = targetPosition.x - position.x;
        float deltaY = targetPosition.y - position.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 10f) { // Only move if not very close
            // Normalize direction and apply normal movement speed
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
        
        // Make elder slightly red when dashing
        if (isDashing) {
            batch.setColor(1f, 0.7f, 0.7f, 1f); // Slight red tint
        }
        
        batch.draw(currentFrame, renderX, renderY, size, size);
        
        // Reset color
        batch.setColor(1f, 1f, 1f, 1f);
    }
    
    @Override
    public void onPlayerCollision() {
        // Only damage player if cooldown has expired
        if (lastDamageTime <= 0) {
            Player player = App.getInstance().getPlayer();
            if (player != null && player.getCharacter() != null) {
                int currentHp = player.getCharacter().getHp();
                int newHp = Math.max(0, currentHp - DAMAGE_TO_PLAYER);
                player.getCharacter().setHp(newHp);
                
                System.out.println("Elder damaged player! HP: " + currentHp + " -> " + newHp);
                
                // Start damage cooldown
                lastDamageTime = DAMAGE_COOLDOWN;
                
                // Check if player died
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