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

public class EyeBat extends Enemy {
    private static final int EYEBAT_HP = 50;
    private static final float EYEBAT_SIZE = 80f; 
    private static final int DAMAGE_TO_PLAYER = 1;
    private static final float DAMAGE_COOLDOWN = 1.0f; 
    private static final float MOVEMENT_SPEED = 40f; 
    private static final float SHOOT_INTERVAL = 3.0f; 
    private static final float BULLET_SPEED = 200f; 
    
    private float lastDamageTime = 0f;
    private float shootTimer = 0f;
    private Vector2 targetPosition; 
    private Array<EyeBatBullet> bullets; 
    
    
    public static class EyeBatBullet {
        public Vector2 position;
        public Vector2 velocity;
        public boolean active;
        
        public EyeBatBullet(float x, float y, float velocityX, float velocityY) {
            this.position = new Vector2(x, y);
            this.velocity = new Vector2(velocityX, velocityY);
            this.active = true;
        }
        
        public void update(float delta) {
            position.add(velocity.x * delta, velocity.y * delta);
        }
        
        public boolean isOffScreen(float cameraX, float cameraY, float screenWidth, float screenHeight) {
            float margin = 100f;
            return position.x < cameraX - screenWidth/2 - margin ||
                   position.x > cameraX + screenWidth/2 + margin ||
                   position.y < cameraY - screenHeight/2 - margin ||
                   position.y > cameraY + screenHeight/2 + margin;
        }
    }
    
    public EyeBat(float x, float y) {
        super(x, y, EYEBAT_SIZE, EYEBAT_HP);
        this.targetPosition = new Vector2();
        this.bullets = new Array<>();
        loadAnimation();
    }
    
    private void loadAnimation() {
        Array<TextureRegion> frames = new Array<>();
        
        try {
            
            for (int i = 0; i < 4; i++) {
                String texturePath = "enemies/T_EyeBat_" + i + ".png";
                Texture texture = new Texture(Gdx.files.internal(texturePath));
                frames.add(new TextureRegion(texture));
                System.out.println("Loaded eyebat texture: " + texturePath);
            }
            
            
            animation = new Animation<>(0.15f, frames, Animation.PlayMode.LOOP);
            System.out.println("Created eyebat animation with " + frames.size + " frames");
            
        } catch (Exception e) {
            System.out.println("Failed to load eyebat textures: " + e.getMessage());
            
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
        
        
        shootTimer += delta;
        if (shootTimer >= SHOOT_INTERVAL) {
            shootTimer = 0f;
            shootAtPlayer();
        }
        
        
        moveTowardTarget(delta);
        
        
        updateBullets(delta);
    }
    
    private void moveTowardTarget(float delta) {
        
        float deltaX = targetPosition.x - position.x;
        float deltaY = targetPosition.y - position.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 5f) { 
            
            float moveX = (deltaX / distance) * MOVEMENT_SPEED * delta;
            float moveY = (deltaY / distance) * MOVEMENT_SPEED * delta;
            
            position.add(moveX, moveY);
        }
    }
    
    private void shootAtPlayer() {
        
        float deltaX = targetPosition.x - position.x;
        float deltaY = targetPosition.y - position.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 0) {
            
            float velocityX = (deltaX / distance) * BULLET_SPEED;
            float velocityY = (deltaY / distance) * BULLET_SPEED;
            
            
            EyeBatBullet bullet = new EyeBatBullet(position.x, position.y, velocityX, velocityY);
            bullets.add(bullet);
            
            System.out.println("EyeBat shot bullet toward player!");
        }
    }
    
    private void updateBullets(float delta) {
        
        for (EyeBatBullet bullet : bullets) {
            if (bullet.active) {
                bullet.update(delta);
                
                
                
            }
        }
        
        
        for (int i = bullets.size - 1; i >= 0; i--) {
            if (!bullets.get(i).active) {
                bullets.removeIndex(i);
            }
        }
    }
    
    public void setTargetPosition(Vector2 target) {
        this.targetPosition.set(target);
    }
    
    public Array<EyeBatBullet> getBullets() {
        return bullets;
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (!active || animation == null) return;
        
        TextureRegion currentFrame = animation.getKeyFrame(animationTime);
        float renderX = position.x - size / 2f;
        float renderY = position.y - size / 2f;
        
        batch.draw(currentFrame, renderX, renderY, size, size);
    }
    
    @Override
    public void onPlayerCollision() {
        
        if (lastDamageTime <= 0) {
            Player player = App.getInstance().getPlayer();
            if (player != null && player.getCharacter() != null) {
                int currentHp = player.getCharacter().getHp();
                int newHp = Math.max(0, currentHp - DAMAGE_TO_PLAYER);
                player.getCharacter().setHp(newHp);
                
                System.out.println("EyeBat damaged player! HP: " + currentHp + " -> " + newHp);
                
                
                lastDamageTime = DAMAGE_COOLDOWN;
                
                
                if (newHp <= 0) {
                    System.out.println("Player died from EyeBat damage!");
                    
                }
            }
        }
    }
    
    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        System.out.println("EyeBat took " + damage + " damage! HP: " + hp);
    }
} 