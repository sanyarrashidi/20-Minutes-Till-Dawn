package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.Dawn;
import com.example.dawn.controller.GameScreenController;
import com.example.dawn.models.App;
import com.example.dawn.models.Player;

public class GameScreen extends AppMenu {
    
    private final GameScreenController gameController;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    
    // Textures
    private Texture tileTexture;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> movingAnimation;
    private Array<Texture> idleTextures;
    private Array<Texture> movingTextures;
    private Texture weaponTexture;
    
    // Game state
    private Vector2 playerPosition;
    private Vector2 playerVelocity;
    
    // Tile system
    private static final int TILE_SIZE = 64; // Size of each tile in pixels
    private static final int TILES_PER_SCREEN = 20; // How many tiles to render around the camera
    
    // Camera following
    private static final float CAMERA_FOLLOW_AREA = 100f; // Dead zone in the center where camera doesn't move
    private static final float PLAYER_SPEED = 300f; // Player movement speed in pixels per second
    
    // Player controls (will be loaded from player settings)
    private int moveUpKey = Input.Keys.W;
    private int moveDownKey = Input.Keys.S;
    private int moveLeftKey = Input.Keys.A;
    private int moveRightKey = Input.Keys.D;
    
    // Animation state
    private float animationTime = 0f;
    private boolean isMoving = false;
    private boolean facingLeft = false; // Track which direction character is facing
    
    // Weapon and mouse state
    private Vector2 mouseWorldPosition = new Vector2();
    private float weaponRotation = 0f;
    
    public GameScreen(GameScreenController controller) {
        super(controller);
        this.gameController = controller;
        this.stage = new Stage(new ScreenViewport());
        
        System.out.println("GameScreen: Initializing...");
        
        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Initialize batch
        batch = new SpriteBatch();
        
        // Initialize player position (start at center)
        playerPosition = new Vector2(0, 0);
        playerVelocity = new Vector2();
        
        System.out.println("GameScreen: Loading textures...");
        loadTextures();
        
        System.out.println("GameScreen: Loading player controls...");
        loadPlayerControls();
        
        System.out.println("GameScreen: Initialization complete!");
    }
    
    private void loadTextures() {
        // Load tile texture
        tileTexture = new Texture(Gdx.files.internal("textures/T_TileGrass.png"));
        
        // Initialize texture arrays
        idleTextures = new Array<>();
        movingTextures = new Array<>();
        
        // Load player animations from character
        Player player = App.getInstance().getPlayer();
        
        if (player != null && player.getCharacter() != null) {
            System.out.println("Loading animations for character: " + player.getCharacter().getName());
            
            // Load idle animation textures
            if (player.getCharacter().getStillImagePaths() != null && 
                !player.getCharacter().getStillImagePaths().isEmpty()) {
                
                for (String imagePath : player.getCharacter().getStillImagePaths()) {
                    if (imagePath != null && !imagePath.isEmpty()) {
                        // Fix case mismatch: convert "idle_" to "Idle_"
                        String correctedPath = imagePath.replace("idle_", "Idle_");
                        try {
                            Texture texture = new Texture(Gdx.files.internal(correctedPath));
                            idleTextures.add(texture);
                            System.out.println("Loaded idle texture: " + correctedPath);
                        } catch (Exception e) {
                            System.out.println("Failed to load idle texture: " + correctedPath + " - " + e.getMessage());
                        }
                    }
                }
            }
            
            // Load moving animation textures
            if (player.getCharacter().getMovingImagePaths() != null && 
                !player.getCharacter().getMovingImagePaths().isEmpty()) {
                
                for (String imagePath : player.getCharacter().getMovingImagePaths()) {
                    if (imagePath != null && !imagePath.isEmpty()) {
                        // Fix case mismatch: convert "Run_" to "Run_" (already correct)
                        String correctedPath = imagePath.replace("Run_", "Run_");
                        try {
                            Texture texture = new Texture(Gdx.files.internal(correctedPath));
                            movingTextures.add(texture);
                            System.out.println("Loaded moving texture: " + correctedPath);
                        } catch (Exception e) {
                            System.out.println("Failed to load moving texture: " + correctedPath + " - " + e.getMessage());
                        }
                    }
                }
            }
        }
        
        // Create animations from loaded textures
        createAnimations();
        
        // Load weapon texture
        loadWeaponTexture();
    }
    
    private void createAnimations() {
        // Create idle animation
        if (idleTextures.size > 0) {
            Array<TextureRegion> idleFrames = new Array<>();
            for (Texture texture : idleTextures) {
                idleFrames.add(new TextureRegion(texture));
            }
            idleAnimation = new Animation<>(0.15f, idleFrames, Animation.PlayMode.LOOP);
            System.out.println("Created idle animation with " + idleFrames.size + " frames");
        } else {
            // Fallback: create single frame animation with tile texture
            Array<TextureRegion> fallbackFrames = new Array<>();
            fallbackFrames.add(new TextureRegion(tileTexture));
            idleAnimation = new Animation<>(1f, fallbackFrames, Animation.PlayMode.LOOP);
            System.out.println("Created fallback idle animation");
        }
        
        // Create moving animation
        if (movingTextures.size > 0) {
            Array<TextureRegion> movingFrames = new Array<>();
            for (Texture texture : movingTextures) {
                movingFrames.add(new TextureRegion(texture));
            }
            movingAnimation = new Animation<>(0.1f, movingFrames, Animation.PlayMode.LOOP);
            System.out.println("Created moving animation with " + movingFrames.size + " frames");
        } else {
            // Fallback: use idle animation for moving
            movingAnimation = idleAnimation;
            System.out.println("Using idle animation as fallback for moving animation");
        }
    }
    
    private void loadPlayerControls() {
        Player player = App.getInstance().getPlayer();
        if (player != null) {
            moveUpKey = player.getMoveUpKey() != null ? player.getMoveUpKey() : Input.Keys.W;
            moveDownKey = player.getMoveDownKey() != null ? player.getMoveDownKey() : Input.Keys.S;
            moveLeftKey = player.getMoveLeftKey() != null ? player.getMoveLeftKey() : Input.Keys.A;
            moveRightKey = player.getMoveRightKey() != null ? player.getMoveRightKey() : Input.Keys.D;
        }
    }
    
    private void loadWeaponTexture() {
        Player player = App.getInstance().getPlayer();
        String weaponTexturePath = "textures/T_TileGrass.png"; // Default fallback
        
        if (player != null && player.getCharacter() != null && 
            player.getCharacter().getWeapon() != null) {
            
            String weaponImagePath = player.getCharacter().getWeapon().getImagePath();
            if (weaponImagePath != null && !weaponImagePath.isEmpty()) {
                weaponTexturePath = weaponImagePath;
                System.out.println("Loading weapon texture: " + weaponTexturePath);
            }
        }
        
        try {
            weaponTexture = new Texture(Gdx.files.internal(weaponTexturePath));
            System.out.println("Successfully loaded weapon texture: " + weaponTexturePath);
        } catch (Exception e) {
            System.out.println("Failed to load weapon texture: " + weaponTexturePath + ", using fallback. Error: " + e.getMessage());
            weaponTexture = new Texture(Gdx.files.internal("textures/T_TileGrass.png"));
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        // Start game music
        if (Dawn.getEnhancedMusicService() != null) {
            Dawn.getEnhancedMusicService().switchToGameMusic();
        }
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update mouse world position
        updateMouseWorldPosition();
        
        // Handle input and update player
        handleInput(delta);
        updatePlayer(delta);
        updateCamera();
        
        // Update animation time
        animationTime += delta;
        
        // Update weapon rotation to point towards mouse
        updateWeaponRotation();
        
        // Set camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Render
        batch.begin();
        renderTiles();
        renderPlayer();
        renderWeapon();
        batch.end();
        
        // Render UI
        stage.act(delta);
        stage.draw();
    }
    
    private void handleInput(float delta) {
        playerVelocity.set(0, 0);
        boolean anyKeyPressed = false;
        
        // Check movement keys
        if (Gdx.input.isKeyPressed(moveUpKey)) {
            playerVelocity.y += 1;
            anyKeyPressed = true;
        }
        if (Gdx.input.isKeyPressed(moveDownKey)) {
            playerVelocity.y -= 1;
            anyKeyPressed = true;
        }
        if (Gdx.input.isKeyPressed(moveLeftKey)) {
            playerVelocity.x -= 1;
            anyKeyPressed = true;
            facingLeft = true; // Character faces left when moving left
        }
        if (Gdx.input.isKeyPressed(moveRightKey)) {
            playerVelocity.x += 1;
            anyKeyPressed = true;
            facingLeft = false; // Character faces right when moving right
        }
        
        // Update movement state for animation
        isMoving = anyKeyPressed;
        
        // Normalize diagonal movement
        if (playerVelocity.len() > 0) {
            playerVelocity.nor();
        }
        
        // Apply speed
        playerVelocity.scl(PLAYER_SPEED * delta);
        
        // Debug output for movement
        if (anyKeyPressed && Gdx.graphics.getFrameId() % 30 == 0) { // Print twice per second when moving
            System.out.println("Movement detected! Velocity: (" + playerVelocity.x + ", " + playerVelocity.y + ")");
            System.out.println("Keys: W=" + moveUpKey + " A=" + moveLeftKey + " S=" + moveDownKey + " D=" + moveRightKey);
            System.out.println("Facing left: " + facingLeft);
        }
    }
    
    private void updatePlayer(float delta) {
        // Update player position
        playerPosition.add(playerVelocity);
    }
    
    private void updateCamera() {
        // Get camera center and player position relative to camera
        Vector3 cameraCenter = new Vector3(camera.position);
        float playerRelativeX = playerPosition.x - cameraCenter.x;
        float playerRelativeY = playerPosition.y - cameraCenter.y;
        
        // Check if player is outside the follow area
        float newCameraX = camera.position.x;
        float newCameraY = camera.position.y;
        
        // Horizontal camera movement
        if (playerRelativeX > CAMERA_FOLLOW_AREA / 2) {
            newCameraX = playerPosition.x - CAMERA_FOLLOW_AREA / 2;
        } else if (playerRelativeX < -CAMERA_FOLLOW_AREA / 2) {
            newCameraX = playerPosition.x + CAMERA_FOLLOW_AREA / 2;
        }
        
        // Vertical camera movement
        if (playerRelativeY > CAMERA_FOLLOW_AREA / 2) {
            newCameraY = playerPosition.y - CAMERA_FOLLOW_AREA / 2;
        } else if (playerRelativeY < -CAMERA_FOLLOW_AREA / 2) {
            newCameraY = playerPosition.y + CAMERA_FOLLOW_AREA / 2;
        }
        
        // Update camera position
        camera.position.set(newCameraX, newCameraY, 0);
    }
    
    private void renderTiles() {
        // Calculate which tiles to render based on camera position
        float cameraLeft = camera.position.x - camera.viewportWidth / 2;
        float cameraRight = camera.position.x + camera.viewportWidth / 2;
        float cameraBottom = camera.position.y - camera.viewportHeight / 2;
        float cameraTop = camera.position.y + camera.viewportHeight / 2;
        
        // Convert to tile coordinates
        int startTileX = (int) Math.floor(cameraLeft / TILE_SIZE) - 1;
        int endTileX = (int) Math.ceil(cameraRight / TILE_SIZE) + 1;
        int startTileY = (int) Math.floor(cameraBottom / TILE_SIZE) - 1;
        int endTileY = (int) Math.ceil(cameraTop / TILE_SIZE) + 1;
        
        // Render tiles
        for (int tileX = startTileX; tileX <= endTileX; tileX++) {
            for (int tileY = startTileY; tileY <= endTileY; tileY++) {
                float worldX = tileX * TILE_SIZE;
                float worldY = tileY * TILE_SIZE;
                batch.draw(tileTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
            }
        }
    }
    
    private void renderPlayer() {
        // Choose the appropriate animation based on movement state
        Animation<TextureRegion> currentAnimation = isMoving ? movingAnimation : idleAnimation;
        
        // Get the current frame
        TextureRegion currentFrame = currentAnimation.getKeyFrame(animationTime);
        
        // Make player larger and more visible
        float playerSize = 64f; // Make player same size as tiles
        float playerRenderX = playerPosition.x - playerSize / 2f;
        float playerRenderY = playerPosition.y - playerSize / 2f;
        
        // Flip the texture region if facing left
        if (facingLeft && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!facingLeft && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
        
        // Render the current animation frame
        batch.draw(currentFrame, playerRenderX, playerRenderY, playerSize, playerSize);
        
        // Debug output (only print occasionally to avoid spam)
        if (Gdx.graphics.getFrameId() % 60 == 0) { // Print once per second at 60fps
            System.out.println("Player position: (" + playerPosition.x + ", " + playerPosition.y + ")");
            System.out.println("Camera position: (" + camera.position.x + ", " + camera.position.y + ")");
            System.out.println("Animation state: " + (isMoving ? "moving" : "idle"));
            System.out.println("Facing left: " + facingLeft);
        }
    }
    
    private void renderWeapon() {
        if (weaponTexture == null) return;
        
        // Weapon size and position
        float weaponSize = 32f;
        float weaponOffsetX = facingLeft ? -20f : 20f; // Position weapon on the side the character is facing
        float weaponOffsetY = 5f; // Slightly above center
        
        // Calculate weapon position relative to player
        float weaponX = playerPosition.x + weaponOffsetX;
        float weaponY = playerPosition.y + weaponOffsetY;
        
        // Create TextureRegion for rotation
        TextureRegion weaponRegion = new TextureRegion(weaponTexture);
        
        // Render weapon with rotation towards mouse
        // The origin is set to the center of the weapon for proper rotation
        batch.draw(weaponRegion, 
                   weaponX - weaponSize/2f, weaponY - weaponSize/2f, // position
                   weaponSize/2f, weaponSize/2f, // origin (center of weapon)
                   weaponSize, weaponSize, // size
                   1f, 1f, // scale
                   weaponRotation); // rotation
    }
    
    private void updateMouseWorldPosition() {
        // Convert screen coordinates to world coordinates
        Vector3 mouseScreenPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 mouseWorldPos = camera.unproject(mouseScreenPos);
        mouseWorldPosition.set(mouseWorldPos.x, mouseWorldPos.y);
    }
    
    private void updateWeaponRotation() {
        // Calculate angle from player to mouse
        float deltaX = mouseWorldPosition.x - playerPosition.x;
        float deltaY = mouseWorldPosition.y - playerPosition.y;
        weaponRotation = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }
    
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        if (tileTexture != null) {
            tileTexture.dispose();
        }
        
        // Dispose weapon texture
        if (weaponTexture != null) {
            weaponTexture.dispose();
        }
        
        // Dispose idle animation textures
        if (idleTextures != null) {
            for (Texture texture : idleTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
        
        // Dispose moving animation textures
        if (movingTextures != null) {
            for (Texture texture : movingTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
        
        if (batch != null) {
            batch.dispose();
        }
        stage.dispose();
    }
} 