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

public class TentacleMonster extends Enemy {
    private static final int TENTACLE_MONSTER_HP = 25;
    private static final float TENTACLE_MONSTER_SIZE = 64f; 
    private static final int DAMAGE_TO_PLAYER = 1;
    private static final float DAMAGE_COOLDOWN = 1.0f; 
    private static final float MOVEMENT_SPEED = 30f; 
    private static final float SPAWN_ANIMATION_DURATION = 1.0f; 
    
    private float lastDamageTime = 0f;
    private boolean isSpawning = true;
    private float spawnTime = 0f;
    private Animation<TextureRegion> spawnAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Vector2 targetPosition; 
    
    public TentacleMonster(float x, float y) {
        super(x, y, TENTACLE_MONSTER_SIZE, TENTACLE_MONSTER_HP);
        this.targetPosition = new Vector2();
        loadAnimations();
    }
    
    private void loadAnimations() {
        Array<TextureRegion> spawnFrames = new Array<>();
        Array<TextureRegion> idleFrames = new Array<>();
        
        try {
            
            for (int i = 0; i < 3; i++) {
                String texturePath = "enemies/TentacleSpawn" + i + ".png";
                Texture texture = new Texture(Gdx.files.internal(texturePath));
                spawnFrames.add(new TextureRegion(texture));
                System.out.println("Loaded tentacle spawn texture: " + texturePath);
            }
            
            
            for (int i = 0; i < 4; i++) {
                String texturePath = "enemies/TentacleIdle" + i + ".png";
                Texture texture = new Texture(Gdx.files.internal(texturePath));
                idleFrames.add(new TextureRegion(texture));
                System.out.println("Loaded tentacle idle texture: " + texturePath);
            }
            
            
            spawnAnimation = new Animation<>(0.33f, spawnFrames, Animation.PlayMode.NORMAL); 
            idleAnimation = new Animation<>(0.2f, idleFrames, Animation.PlayMode.LOOP);
            animation = spawnAnimation; 
            
            System.out.println("Created tentacle monster animations");
            
        } catch (Exception e) {
            System.out.println("Failed to load tentacle monster textures: " + e.getMessage());
            
            spawnFrames.clear();
            spawnFrames.add(new TextureRegion(new Texture(Gdx.files.internal("textures/T_TileGrass.png"))));
            spawnAnimation = new Animation<>(1f, spawnFrames, Animation.PlayMode.NORMAL);
            idleAnimation = spawnAnimation;
            animation = spawnAnimation;
        }
    }
    
    @Override
    public void update(float delta) {
        animationTime += delta;
        
        
        if (isSpawning) {
            spawnTime += delta;
            if (spawnTime >= SPAWN_ANIMATION_DURATION) {
                isSpawning = false;
                animation = idleAnimation; 
                animationTime = 0f; 
                System.out.println("Tentacle monster finished spawning");
            }
            return; 
        }
        
        
        if (lastDamageTime > 0) {
            lastDamageTime -= delta;
            if (lastDamageTime < 0) {
                lastDamageTime = 0;
            }
        }
        
        
        Player player = App.getInstance().getPlayer();
        if (player != null) {
            
            
            moveTowardTarget(delta);
        }
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
    
    public void setTargetPosition(Vector2 target) {
        this.targetPosition.set(target);
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
        
        if (!isSpawning && lastDamageTime <= 0) {
            Player player = App.getInstance().getPlayer();
            if (player != null && player.getCharacter() != null) {
                int currentHp = player.getCharacter().getHp();
                int newHp = Math.max(0, currentHp - DAMAGE_TO_PLAYER);
                player.getCharacter().setHp(newHp);
                
                System.out.println("Tentacle monster damaged player! HP: " + currentHp + " -> " + newHp);
                
                
                lastDamageTime = DAMAGE_COOLDOWN;
                
                
                if (newHp <= 0) {
                    System.out.println("Player died from tentacle monster damage!");
                    
                }
            }
        }
    }
    
    @Override
    public void takeDamage(int damage) {
        if (!isSpawning) { 
            super.takeDamage(damage);
            System.out.println("Tentacle monster took " + damage + " damage! HP: " + hp);
        }
    }
    
    public boolean isSpawning() {
        return isSpawning;
    }
} 