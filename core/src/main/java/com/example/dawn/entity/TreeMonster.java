package com.example.dawn.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.example.dawn.models.App;
import com.example.dawn.models.Player;

public class TreeMonster extends Enemy {
    private static final int TREE_MONSTER_HP = Integer.MAX_VALUE; // Unlimited HP
    private static final float TREE_MONSTER_SIZE = 128f; // Larger trees (2x original size)
    private static final int DAMAGE_TO_PLAYER = 1;
    private static final float DAMAGE_COOLDOWN = 1.0f; // 1 second cooldown between damage
    
    private float lastDamageTime = 0f;
    
    public TreeMonster(float x, float y) {
        super(x, y, TREE_MONSTER_SIZE, TREE_MONSTER_HP);
        loadAnimation();
    }
    
    private void loadAnimation() {
        Array<TextureRegion> frames = new Array<>();
        
        try {
            // Load tree monster animation frames
            for (int i = 0; i < 3; i++) {
                String texturePath = "enemies/T_TreeMonster_" + i + ".png";
                Texture texture = new Texture(Gdx.files.internal(texturePath));
                frames.add(new TextureRegion(texture));
                System.out.println("Loaded tree monster texture: " + texturePath);
            }
            
            // Create animation with slower frame rate for tree (they don't move much)
            animation = new Animation<>(0.5f, frames, Animation.PlayMode.LOOP);
            System.out.println("Created tree monster animation with " + frames.size + " frames");
            
        } catch (Exception e) {
            System.out.println("Failed to load tree monster textures: " + e.getMessage());
            // Create fallback animation
            frames.clear();
            frames.add(new TextureRegion(new Texture(Gdx.files.internal("textures/T_TileGrass.png"))));
            animation = new Animation<>(1f, frames, Animation.PlayMode.LOOP);
        }
    }
    
    @Override
    public void update(float delta) {
        // Tree monsters are stationary, just update animation
        animationTime += delta;
        
        // Update damage cooldown
        if (lastDamageTime > 0) {
            lastDamageTime -= delta;
            if (lastDamageTime < 0) {
                lastDamageTime = 0;
            }
        }
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
        // Only damage player if cooldown has expired
        if (lastDamageTime <= 0) {
            Player player = App.getInstance().getPlayer();
            if (player != null && player.getCharacter() != null) {
                int currentHp = player.getCharacter().getHp();
                int newHp = Math.max(0, currentHp - DAMAGE_TO_PLAYER);
                player.getCharacter().setHp(newHp);
                
                System.out.println("Tree monster damaged player! HP: " + currentHp + " -> " + newHp);
                
                // Start damage cooldown
                lastDamageTime = DAMAGE_COOLDOWN;
                
                // Check if player died
                if (newHp <= 0) {
                    System.out.println("Player died from tree monster damage!");
                    // TODO: Handle player death
                }
            }
        }
    }
    
    @Override
    public void takeDamage(int damage) {
        // Tree monsters have unlimited HP and cannot be killed
        // Do nothing when taking damage
        System.out.println("Tree monster hit but cannot be killed!");
    }
} 