package com.example.dawn.view;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.Dawn;
import com.example.dawn.controller.GameScreenController;
import com.example.dawn.controller.GameSummaryController;
import com.example.dawn.entity.Elder;
import com.example.dawn.entity.Enemy;
import com.example.dawn.entity.EyeBat;
import com.example.dawn.entity.TentacleMonster;
import com.example.dawn.entity.TreeMonster;
import com.example.dawn.models.Ability;
import com.example.dawn.models.App;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.models.GameSummary;
import com.example.dawn.models.Player;
import com.example.dawn.models.Weapon;

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
    private Texture bulletTexture;
    private Texture cursorTexture;
    private Texture avatarTexture;
    
    // Game state
    private Vector2 playerPosition;
    private Vector2 playerVelocity;
    
    // Tile system
    private static final int TILE_SIZE = 96; // Size of each tile in pixels (1.5x bigger)
    private static final int TILES_PER_SCREEN = 20; // How many tiles to render around the camera
    
    // Camera following
    private static final float CAMERA_FOLLOW_AREA = 100f; // Dead zone in the center where camera doesn't move
    private static final float DEFAULT_PLAYER_SPEED = 300f; // Default player movement speed in pixels per second
    
    // Player controls (will be loaded from player settings)
    private int moveUpKey = Input.Keys.W; // 51
    private int moveDownKey = Input.Keys.S; // 47
    private int moveLeftKey = Input.Keys.A; // 29
    private int moveRightKey = Input.Keys.D; // 32
    
    // Animation state
    private float animationTime = 0f;
    private boolean isMoving = false;
    private boolean facingLeft = false; // Track which direction character is facing
    
    // Weapon and mouse state
    private Vector2 mouseWorldPosition = new Vector2();
    private float weaponRotation = 0f;
    
    // Bullet system
    private Array<Bullet> bullets = new Array<>();
    private int shootKey = Input.Keys.SPACE; // Default shoot key (62)
    private int reloadKey = Input.Keys.R; // Default reload key (46)
    private boolean shootKeyPressed = false; // Track shoot key state for single shots
    
    // Magazine system
    private int currentAmmo; // Current bullets in magazine
    private int maxAmmo; // Magazine capacity
    private boolean isReloading = false;
    private float reloadTimer = 0f;
    private float reloadDuration = 1f; // Default reload time
    private boolean autoReload = false;
    private Animation<TextureRegion> reloadAnimation;
    private Array<Texture> reloadTextures;
    private float reloadAnimationTime = 0f;
    
    // Rapid fire system
    private float fireRate = 0.2f; // Time between shots (5 shots per second)
    private float fireTimer = 0f;
    private boolean canShoot = true;
    
    // Death animation system
    private Animation<TextureRegion> deathAnimation;
    private Array<Texture> deathTextures;
    private Array<DeathEffect> deathEffects;
    
    // Player damage animation
    private boolean playerTookDamage = false;
    private float damageAnimationTimer = 0f;
    private static final float DAMAGE_ANIMATION_DURATION = 0.5f; // Red tint duration
    
    // Death effect class
    private static class DeathEffect {
        Vector2 position;
        float animationTime;
        boolean active;
        
        public DeathEffect(float x, float y) {
            this.position = new Vector2(x, y);
            this.animationTime = 0f;
            this.active = true;
        }
        
        public void update(float delta) {
            animationTime += delta;
        }
        
        public boolean isFinished(float animationDuration) {
            return animationTime >= animationDuration;
        }
    }
    
    // UI elements
    private BitmapFont font;
    private BitmapFont timerFont; // Large font for the timer
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera uiCamera;
    
    // Pause menu elements
    private Table pauseMenuTable;
    private boolean pauseMenuVisible = false;
    
    // Timer system
    private float gameTimeRemaining; // in seconds
    private boolean gameEnded = false;
    private boolean gamePaused = false;
    
    // Game scoring and tracking system
    private int gameStartKills = 0; // Kills at start of game
    private int gameStartScore = 0; // Score at start of game
    private int gameStartXp = 0; // XP at start of game
    private int lastKnownLevel = 1; // Track level for level-up detection
    
    // Enemy system
    private Array<Enemy> enemies;
    private static final float PLAYER_COLLISION_RADIUS = 32f; // Player collision radius
    private static final int TREE_MONSTERS_PER_AREA = 1; // Reduced from 2 to 1 tree per area
    private static final float ENEMY_SPAWN_DISTANCE = 200f; // Minimum distance from player to spawn enemies
    
    // Tentacle spawning system
    private float tentacleSpawnTimer = 0f;
    private static final float TENTACLE_SPAWN_INTERVAL = 3f; // Spawn every 3 seconds
    private float gameElapsedTime = 0f;
    
    // EyeBat spawning system
    private float eyebatSpawnTimer = 0f;
    private static final float EYEBAT_SPAWN_INTERVAL = 10f; // Spawn every 10 seconds
    private boolean eyebatSpawningStarted = false;
    
    // Elder boss system
    private Enemy elder = null; // Using Enemy base class to avoid import issues
    private boolean elderSpawned = false;
    private float initialBorderSize;
    private float currentBorderSize;
    private static final float BORDER_SHRINK_RATE = 50f; // pixels per second
    
    // Global tree spawning system
    private Array<Vector2> spawnedTreeAreas = new Array<>();
    private static final float TREE_AREA_SIZE = 800f; // Size of each tree spawning area
    
    // Bullet class
    private static class Bullet {
        Vector2 position;
        Vector2 velocity;
        float rotation;
        boolean active;
        int damage;
        
        public Bullet(float x, float y, float velocityX, float velocityY, float rotation, int damage) {
            this.position = new Vector2(x, y);
            this.velocity = new Vector2(velocityX, velocityY);
            this.rotation = rotation;
            this.active = true;
            this.damage = damage;
        }
        
        public void update(float delta) {
            position.add(velocity.x * delta, velocity.y * delta);
        }
        
        public boolean isOffScreen(OrthographicCamera camera) {
            float margin = 100f; // Allow bullets to go slightly off screen before removing
            return position.x < camera.position.x - camera.viewportWidth/2 - margin ||
                   position.x > camera.position.x + camera.viewportWidth/2 + margin ||
                   position.y < camera.position.y - camera.viewportHeight/2 - margin ||
                   position.y > camera.position.y + camera.viewportHeight/2 + margin;
        }
    }
    
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
        
        // Initialize UI elements
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        
        // Initialize timer font (larger, title-style)
        timerFont = new BitmapFont();
        timerFont.setColor(Color.RED);
        timerFont.getData().setScale(3.0f); // Much larger for title effect
        
        shapeRenderer = new ShapeRenderer();
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Initialize timer
        gameTimeRemaining = gameController.getGameDurationMinutes() * 60f; // Convert minutes to seconds
        
        // Initialize enemy system
        enemies = new Array<>();
        
        // Initialize player position (start at center)
        playerPosition = new Vector2(0, 0);
        playerVelocity = new Vector2();
        
        // Initialize border system
        initialBorderSize = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        currentBorderSize = initialBorderSize;
        
        // Initialize tree spawning areas
        spawnedTreeAreas = new Array<>();
        
        // Initialize death effects
        deathEffects = new Array<>();
        
        // Initialize magazine system
        initializeMagazineSystem();
        
        // Initialize game tracking
        initializeGameTracking();
        
        System.out.println("GameScreen: Loading textures...");
        loadTextures();
        
        System.out.println("GameScreen: Loading player controls...");
        loadPlayerControls();
        
        System.out.println("GameScreen: Setting up custom cursor...");
        setupCustomCursor();
        
        System.out.println("GameScreen: Spawning initial enemies...");
        spawnInitialEnemies();
        
        System.out.println("GameScreen: Initialization complete!");
    }
    
    private void initializeMagazineSystem() {
        Player player = App.getInstance().getPlayer();
        if (player != null && player.getCharacter() != null && player.getCharacter().getWeapon() != null) {
            Weapon weapon = player.getCharacter().getWeapon();
            maxAmmo = weapon.getMagazineSize() != null ? weapon.getMagazineSize() : 6;
            // Add bonus ammo from AMOCREASE ability
            maxAmmo += player.getCharacter().getBonusMaxAmmo();
            reloadDuration = weapon.getReloadTime() != null ? weapon.getReloadTime() : 1f;
            autoReload = player.getAutoReload() != null ? player.getAutoReload() : false;
        } else {
            maxAmmo = 6; // Default magazine size
            reloadDuration = 1f; // Default reload time
            autoReload = false;
        }
        
        currentAmmo = maxAmmo; // Start with full magazine
        isReloading = false;
        reloadTimer = 0f;
        fireTimer = 0f;
        canShoot = true;
        
        System.out.println("Magazine system initialized: " + currentAmmo + "/" + maxAmmo + " ammo, reload time: " + reloadDuration + "s, auto-reload: " + autoReload);
    }
    
    private void initializeGameTracking() {
        Player player = App.getInstance().getPlayer();
        if (player != null && player.getCharacter() != null) {
            gameStartKills = player.getCharacter().getKills();
            gameStartScore = player.getCharacter().getScore();
            gameStartXp = player.getCharacter().getXp();
            lastKnownLevel = player.getCharacter().getLevel();
            System.out.println("Game tracking initialized - Start kills: " + gameStartKills + ", Start score: " + gameStartScore + ", Start XP: " + gameStartXp + ", Level: " + lastKnownLevel);
        }
    }
    
    private void togglePause() {
        gamePaused = !gamePaused;
        if (gamePaused) {
            System.out.println("Game paused");
            // Show pause menu
            showPauseMenu();
        } else {
            System.out.println("Game resumed");
            // Hide pause menu
            hidePauseMenu();
        }
    }
    
    private void showPauseMenu() {
        pauseMenuVisible = true;
        createPauseMenu();
        // Set input processor to stage for pause menu interaction
        Gdx.input.setInputProcessor(stage);
    }
    
    private void hidePauseMenu() {
        pauseMenuVisible = false;
        if (pauseMenuTable != null) {
            pauseMenuTable.remove();
            pauseMenuTable = null;
        }
        // Return input processor to null for game controls
        Gdx.input.setInputProcessor(null);
    }
    
    private void createPauseMenu() {
        if (pauseMenuTable != null) {
            pauseMenuTable.remove();
        }
        
        pauseMenuTable = new Table();
        pauseMenuTable.setFillParent(true);
        
        // Create main content table
        Table contentTable = new Table();
        contentTable.setBackground(GameAssetManager.getInstance().getSkin().getDrawable("window"));
        contentTable.pad(30);
        
        // Title
        Label titleLabel = new Label("GAME PAUSED", GameAssetManager.getInstance().getSkin());
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(Color.YELLOW);
        contentTable.add(titleLabel).colspan(2).center().padBottom(30);
        contentTable.row();
        
        // Create two columns: left for buttons, right for info
        Table leftColumn = new Table();
        Table rightColumn = new Table();
        
        // Left column - Action buttons
        createActionButtons(leftColumn);
        
        // Right column - Information panels
        createInfoPanels(rightColumn);
        
        contentTable.add(leftColumn).width(300).top().padRight(20);
        contentTable.add(rightColumn).width(400).top();
        
        pauseMenuTable.add(contentTable).center();
        stage.addActor(pauseMenuTable);
    }
    
    private void createActionButtons(Table leftColumn) {
        // Resume Game button
        TextButton resumeButton = new TextButton("Resume Game", GameAssetManager.getInstance().getSkin());
        resumeButton.getLabel().setFontScale(1.3f);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause(); // This will resume the game
            }
        });
        leftColumn.add(resumeButton).width(250).height(60).padBottom(15);
        leftColumn.row();
        
        // Give Up button
        TextButton giveUpButton = new TextButton("Give Up", GameAssetManager.getInstance().getSkin());
        giveUpButton.getLabel().setFontScale(1.3f);
        giveUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                giveUp();
            }
        });
        leftColumn.add(giveUpButton).width(250).height(60);
        leftColumn.row();
    }
    
    private void createInfoPanels(Table rightColumn) {
        // Current Abilities panel
        createAbilitiesPanel(rightColumn);
        
        // Cheat Codes panel
        createCheatCodesPanel(rightColumn);
    }
    
    private void createAbilitiesPanel(Table rightColumn) {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) return;
        
        Table abilitiesTable = new Table();
        abilitiesTable.setBackground(GameAssetManager.getInstance().getSkin().getDrawable("window"));
        abilitiesTable.pad(15);
        
        Label abilitiesTitle = new Label("Current Abilities", GameAssetManager.getInstance().getSkin());
        abilitiesTitle.setFontScale(1.4f);
        abilitiesTitle.setColor(Color.CYAN);
        abilitiesTable.add(abilitiesTitle).center().padBottom(15);
        abilitiesTable.row();
        
        // Active abilities
        List<com.example.dawn.models.ActiveAbility> activeAbilities = player.getCharacter().getActiveAbilities();
        if (!activeAbilities.isEmpty()) {
            Label activeTitle = new Label("Active:", GameAssetManager.getInstance().getSkin());
            activeTitle.setFontScale(1.1f);
            activeTitle.setColor(Color.YELLOW);
            abilitiesTable.add(activeTitle).left().padBottom(5);
            abilitiesTable.row();
            
            for (com.example.dawn.models.ActiveAbility activeAbility : activeAbilities) {
                Label abilityLabel = new Label("• " + activeAbility.getAbility().getName() + 
                    " (" + activeAbility.getRemainingSeconds() + "s)", GameAssetManager.getInstance().getSkin());
                abilityLabel.setFontScale(0.9f);
                abilityLabel.setColor(Color.WHITE);
                abilitiesTable.add(abilityLabel).left().padBottom(3);
                abilitiesTable.row();
            }
        }
        
        // Permanent abilities
        List<Ability> permanentAbilities = player.getCharacter().getPermanentAbilities();
        if (!permanentAbilities.isEmpty()) {
            Label permanentTitle = new Label("Permanent:", GameAssetManager.getInstance().getSkin());
            permanentTitle.setFontScale(1.1f);
            permanentTitle.setColor(Color.GREEN);
            abilitiesTable.add(permanentTitle).left().padBottom(5).padTop(10);
            abilitiesTable.row();
            
            for (Ability ability : permanentAbilities) {
                Label abilityLabel = new Label("• " + ability.getName(), GameAssetManager.getInstance().getSkin());
                abilityLabel.setFontScale(0.9f);
                abilityLabel.setColor(Color.WHITE);
                abilitiesTable.add(abilityLabel).left().padBottom(3);
                abilitiesTable.row();
            }
        }
        
        if (activeAbilities.isEmpty() && permanentAbilities.isEmpty()) {
            Label noAbilitiesLabel = new Label("No abilities acquired yet", GameAssetManager.getInstance().getSkin());
            noAbilitiesLabel.setFontScale(0.9f);
            noAbilitiesLabel.setColor(Color.GRAY);
            abilitiesTable.add(noAbilitiesLabel).center();
            abilitiesTable.row();
        }
        
        rightColumn.add(abilitiesTable).width(380).padBottom(20);
        rightColumn.row();
    }
    
    private void createCheatCodesPanel(Table rightColumn) {
        Table cheatTable = new Table();
        cheatTable.setBackground(GameAssetManager.getInstance().getSkin().getDrawable("window"));
        cheatTable.pad(15);
        
        Label cheatTitle = new Label("Cheat Codes", GameAssetManager.getInstance().getSkin());
        cheatTitle.setFontScale(1.4f);
        cheatTitle.setColor(Color.MAGENTA);
        cheatTable.add(cheatTitle).center().padBottom(15);
        cheatTable.row();
        
        // Create scrollable content for cheat codes
        Table cheatContent = new Table();
        
        String[] cheatCodes = {
            "T - Decrease time by 1 minute",
            "L - Level up player",
            "H - Increase health by 1",
            "B - Summon Elder boss",
            "K - Kill all enemies"
        };
        
        for (String cheat : cheatCodes) {
            Label cheatLabel = new Label(cheat, GameAssetManager.getInstance().getSkin());
            cheatLabel.setFontScale(0.8f);
            cheatLabel.setColor(Color.WHITE);
            cheatContent.add(cheatLabel).left().padBottom(5);
            cheatContent.row();
        }
        
        ScrollPane cheatScrollPane = new ScrollPane(cheatContent, GameAssetManager.getInstance().getSkin());
        cheatScrollPane.setScrollingDisabled(true, false);
        cheatScrollPane.setFadeScrollBars(false);
        
        cheatTable.add(cheatScrollPane).width(350).height(120);
        
        rightColumn.add(cheatTable).width(380);
    }
    
    private void renderPauseOverlay() {
        // Draw semi-transparent background overlay
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f); // Semi-transparent black
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
    }
    
    private void giveUp() {
        // Player chose to give up from pause menu
        if (!gameEnded) {
            gameEnded = true;
            endGameWithSummary(GameSummary.GameEndReason.GAVE_UP, false);
        }
    }
    
    private void loadTextures() {
        // Load tile texture
        tileTexture = new Texture(Gdx.files.internal("textures/T_TileGrass.png"));
        
        // Initialize texture arrays
        idleTextures = new Array<>();
        movingTextures = new Array<>();
        reloadTextures = new Array<>();
        deathTextures = new Array<>();
        
        // Load player animations from character
        Player player = App.getInstance().getPlayer();
        
        if (player != null && player.getCharacter() != null) {
            System.out.println("Loading animations for character: " + player.getCharacter().getName());
            
            // Load idle animation textures
            if (player.getCharacter().getStillImagePaths() != null && 
                !player.getCharacter().getStillImagePaths().isEmpty()) {
                
                for (String imagePath : player.getCharacter().getStillImagePaths()) {
                    if (imagePath != null && !imagePath.isEmpty()) {
                        // Try the path as-is first, then try with case correction
                        String[] pathsToTry = {
                            imagePath,
                            imagePath.replace("idle_", "Idle_"),
                            imagePath.replace("Idle_", "idle_")
                        };
                        
                        boolean loaded = false;
                        for (String pathToTry : pathsToTry) {
                            try {
                                Texture texture = new Texture(Gdx.files.internal(pathToTry));
                                idleTextures.add(texture);
                                System.out.println("Loaded idle texture: " + pathToTry);
                                loaded = true;
                                break;
                            } catch (Exception e) {
                                // Try next path variant
                            }
                        }
                        
                        if (!loaded) {
                            System.out.println("Failed to load idle texture with any variant of: " + imagePath);
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
        
        // Load bullet texture
        loadBulletTexture();
        
        // Load cursor texture
        loadCursorTexture();
        
        // Load avatar texture
        loadAvatarTexture();
        
        // Load reload animations
        loadReloadAnimations();
        
        // Load death animations
        loadDeathAnimations();
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
            shootKey = player.getShootKey() != null ? player.getShootKey() : Input.Keys.SPACE;
            reloadKey = player.getReloadKey() != null ? player.getReloadKey() : Input.Keys.R;
            
            System.out.println("Loaded player controls - W:" + moveUpKey + " S:" + moveDownKey + " A:" + moveLeftKey + " D:" + moveRightKey);
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
    
    private void loadBulletTexture() {
        try {
            bulletTexture = new Texture(Gdx.files.internal("textures/T_SoulIcon.png"));
            System.out.println("Successfully loaded bullet texture: textures/T_SoulIcon.png");
        } catch (Exception e) {
            System.out.println("Failed to load bullet texture, using fallback. Error: " + e.getMessage());
            bulletTexture = new Texture(Gdx.files.internal("textures/T_TileGrass.png"));
        }
    }
    
    private void loadCursorTexture() {
        try {
            cursorTexture = new Texture(Gdx.files.internal("textures/T_Cursor.png"));
            System.out.println("Successfully loaded cursor texture: textures/T_Cursor.png");
        } catch (Exception e) {
            System.out.println("Failed to load cursor texture, using fallback. Error: " + e.getMessage());
            cursorTexture = new Texture(Gdx.files.internal("textures/T_TileGrass.png"));
        }
    }
    
    private void loadAvatarTexture() {
        Player player = App.getInstance().getPlayer();
        String avatarTexturePath = "textures/T_TileGrass.png"; // Default fallback
        
        if (player != null && player.getCharacter() != null) {
            String characterName = player.getCharacter().getName();
            if (characterName != null && !characterName.isEmpty()) {
                avatarTexturePath = "avatars/" + characterName + "_Portrait.png";
                System.out.println("Loading avatar texture for character: " + characterName);
            }
        }
        
        try {
            avatarTexture = new Texture(Gdx.files.internal(avatarTexturePath));
            System.out.println("Successfully loaded avatar texture: " + avatarTexturePath);
        } catch (Exception e) {
            System.out.println("Failed to load avatar texture: " + avatarTexturePath + ", using fallback. Error: " + e.getMessage());
            avatarTexture = new Texture(Gdx.files.internal("textures/T_TileGrass.png"));
        }
    }
    
    private void setupCustomCursor() {
        if (cursorTexture != null) {
            try {
                // Get the texture data as a Pixmap
                if (!cursorTexture.getTextureData().isPrepared()) {
                    cursorTexture.getTextureData().prepare();
                }
                Pixmap pixmap = cursorTexture.getTextureData().consumePixmap();
                
                // Create cursor with hotspot at center
                Cursor cursor = Gdx.graphics.newCursor(pixmap, pixmap.getWidth()/2, pixmap.getHeight()/2);
                Gdx.graphics.setCursor(cursor);
                
                System.out.println("Custom cursor set successfully");
            } catch (Exception e) {
                System.out.println("Failed to set custom cursor: " + e.getMessage());
            }
        }
    }
    
    private void spawnInitialEnemies() {
        // Spawn tree monsters around the player's starting area
        for (int i = 0; i < TREE_MONSTERS_PER_AREA * 4; i++) { // Spawn in 4 directions around player
            float angle = MathUtils.random(0f, 360f);
            float distance = MathUtils.random(ENEMY_SPAWN_DISTANCE, ENEMY_SPAWN_DISTANCE * 2f);
            
            float x = playerPosition.x + MathUtils.cosDeg(angle) * distance;
            float y = playerPosition.y + MathUtils.sinDeg(angle) * distance;
            
            // Snap to tile grid for better placement
            x = Math.round(x / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
            y = Math.round(y / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
            
            TreeMonster treeMonster = new TreeMonster(x, y);
            enemies.add(treeMonster);
            
            System.out.println("Spawned tree monster at (" + x + ", " + y + ")");
        }
        
        System.out.println("Spawned " + enemies.size + " tree monsters");
    }

    @Override
    public void show() {
        // Don't set input processor to stage for game controls
        // Gdx.input.setInputProcessor(stage);
        Gdx.input.setInputProcessor(null); // Use direct input polling instead
        
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
        
        // Always handle input (for pause/unpause)
        handleInput(delta);
        
        // Only update game logic if not paused
        if (!gamePaused) {
            // Update timer
            updateTimer(delta);
            
            // Update magazine system
            updateMagazineSystem(delta);
            
            // Update game elapsed time and enemy spawning
            gameElapsedTime += delta;
            updateTentacleSpawning(delta);
            updateEyeBatSpawning(delta);
            updateElderSpawning(delta);
            updateGlobalTreeSpawning();
            
            // Update mouse world position
            updateMouseWorldPosition();
            
            // Update player
            updatePlayer(delta);
            updateCamera();
            
            // Update bullets
            updateBullets(delta);
            
            // Update enemies
            updateEnemies(delta);
            
            // Update death effects
            updateDeathEffects(delta);
            
            // Update player damage animation
            updatePlayerDamageAnimation(delta);
            
            // Update abilities and check for level-ups
            updateAbilities(delta);
            
            // Check collisions
            checkCollisions();
            
            // Update animation time
            animationTime += delta;
            
            // Update weapon rotation to point towards mouse
            updateWeaponRotation();
        }
        
        // Set camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Render
        batch.begin();
        renderTiles();
        renderEnemies();
        renderDeathEffects(); // Render death effects
        renderEyeBatBullets(); // Render enemy bullets
        renderPlayer();
        renderWeapon();
        renderBullets(); // Render player bullets
        batch.end();
        
        // Render border if Elder is active
        if (elder != null && elder.isActive()) {
            renderBorder();
        }
        
        // Render UI
        renderUI();
        
        // Render pause overlay if paused
        if (gamePaused && pauseMenuVisible) {
            renderPauseOverlay();
        }
        
        // Render UI stage
        stage.act(delta);
        stage.draw();
    }
    
    private void handleInput(float delta) {
        // Check for pause key (ESC)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
            return; // Don't process other input when pausing
        }
        
        // Handle cheat codes (work even when paused)
        handleCheatCodes();
        
        // Don't process game input if paused
        if (gamePaused) {
            return;
        }
        
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
        
        // Apply speed (use character's speed if available, otherwise default)
        float baseSpeed = 100f; // Base speed multiplier (pixels per second per speed point)
        float speedMultiplier = 3f; // Default speed value (like before)
        Player player = App.getInstance().getPlayer();
        if (player != null && player.getCharacter() != null) {
            speedMultiplier = player.getCharacter().getSpeed();
            
            // Apply SPEEDY ability (double speed)
            if (player.getCharacter().hasActiveAbility(Ability.SPEEDY)) {
                speedMultiplier *= 2f;
            }
        }
        float playerSpeed = baseSpeed * speedMultiplier;
        playerVelocity.scl(playerSpeed * delta);
        
        // Debug output for movement (reduced frequency)
        if (anyKeyPressed && Gdx.graphics.getFrameId() % 180 == 0) { // Print every 3 seconds when moving
            System.out.println("Movement - Keys: W:" + moveUpKey + " S:" + moveDownKey + " A:" + moveLeftKey + " D:" + moveRightKey);
            System.out.println("Velocity: (" + playerVelocity.x + ", " + playerVelocity.y + ")");
        }
        
        // Handle shooting (rapid fire)
        handleShooting(delta);
        
        // Handle reload
        handleReload();
    }
    
    private void handleShooting(float delta) {
        // Update fire timer
        fireTimer += delta;
        
        // Check if we can shoot (not reloading, have ammo, fire rate allows)
        canShoot = !isReloading && currentAmmo > 0 && fireTimer >= fireRate;
        
        // Handle rapid fire - shoot while key is held
        if (Gdx.input.isKeyPressed(shootKey) && canShoot) {
            shoot();
            fireTimer = 0f; // Reset fire timer
        }
    }
    
    private void handleReload() {
        // Manual reload with reload key
        if (Gdx.input.isKeyJustPressed(reloadKey) && !isReloading && currentAmmo < maxAmmo) {
            startReload();
        }
    }
    
    private void handleCheatCodes() {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) return;
        
        // T - Decrease time by 1 minute
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            gameTimeRemaining = Math.max(0, gameTimeRemaining - 60f);
            gameElapsedTime += 60f; // Also advance elapsed time to trigger spawning events
            System.out.println("CHEAT: Time decreased by 1 minute. Remaining: " + (int)(gameTimeRemaining / 60) + ":" + String.format("%02d", (int)(gameTimeRemaining % 60)));
            
            // Check Elder spawning after time cheat
            float totalGameTime = gameController.getGameDurationMinutes() * 60f;
            float elderSpawnTime = totalGameTime / 2f;
            if (!elderSpawned && gameElapsedTime >= elderSpawnTime) {
                spawnElder();
                elderSpawned = true;
                initialBorderSize = Math.max(camera.viewportWidth, camera.viewportHeight);
                currentBorderSize = initialBorderSize;
                System.out.println("CHEAT: Elder spawned due to time advancement!");
            }
            
            // Check if time is up after cheat
            if (gameTimeRemaining <= 0 && !gameEnded) {
                gameTimeRemaining = 0;
                gameEnded = true;
                endGameWithSummary(com.example.dawn.models.GameSummary.GameEndReason.TIME_UP, true);
            }
        }
        
        // L - Level up player
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            int currentLevel = player.getCharacter().getLevel();
            int newLevel = currentLevel + 1;
            
            // Calculate XP needed for new level (sum of 20*i for i=1 to newLevel-1)
            int xpForNewLevel = 0;
            for (int i = 1; i < newLevel; i++) {
                xpForNewLevel += 20 * i;
            }
            player.getCharacter().setXp(xpForNewLevel);
            
            System.out.println("CHEAT: Player leveled up! Level: " + currentLevel + " -> " + newLevel + ", XP set to: " + xpForNewLevel);
        }
        
        // H - Increase health by 1 (if not full)
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            int currentHp = player.getCharacter().getHp();
            int maxHp = player.getCharacter().getInitialHp(); // Use initial HP as max HP
            
            if (currentHp < maxHp) {
                player.getCharacter().setHp(currentHp + 1);
                System.out.println("CHEAT: Health increased! HP: " + currentHp + " -> " + (currentHp + 1));
            } else {
                System.out.println("CHEAT: Health already at maximum (" + maxHp + ")");
            }
        }
        
        // B - Summon Elder boss
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            if (!elderSpawned) {
                spawnElder();
                elderSpawned = true;
                
                // Initialize border system when Elder spawns
                initialBorderSize = Math.max(camera.viewportWidth, camera.viewportHeight);
                currentBorderSize = initialBorderSize;
                
                System.out.println("CHEAT: Elder boss summoned! Border system activated.");
            } else {
                System.out.println("CHEAT: Elder boss already spawned!");
            }
        }
        
        // K - Kill all enemies
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            int enemiesKilled = 0;
            for (Enemy enemy : enemies) {
                if (enemy.isActive() && !(enemy instanceof TreeMonster)) { // Don't kill TreeMonsters
                    // Create death effect at enemy position
                    DeathEffect deathEffect = new DeathEffect(enemy.getPosition().x, enemy.getPosition().y);
                    deathEffects.add(deathEffect);
                    
                    // Calculate score and XP based on enemy's initial HP
                    int scoreGained = getEnemyInitialHp(enemy);
                    int xpGained = scoreGained / 5;
                    
                    // Increment kill count, score, and XP
                    int currentKills = player.getCharacter().getKills();
                    int currentScore = player.getCharacter().getScore();
                    player.getCharacter().setKills(currentKills + 1);
                    player.getCharacter().setScore(currentScore + scoreGained);
                    player.getCharacter().addXp(xpGained);
                    
                    // Deactivate enemy
                    enemy.setActive(false);
                    enemiesKilled++;
                }
            }
            
            // Also kill elder if active
            if (elder != null && elder.isActive()) {
                DeathEffect deathEffect = new DeathEffect(elder.getPosition().x, elder.getPosition().y);
                deathEffects.add(deathEffect);
                
                int scoreGained = getEnemyInitialHp(elder);
                int xpGained = scoreGained / 5;
                
                int currentKills = player.getCharacter().getKills();
                int currentScore = player.getCharacter().getScore();
                player.getCharacter().setKills(currentKills + 1);
                player.getCharacter().setScore(currentScore + scoreGained);
                player.getCharacter().addXp(xpGained);
                
                elder.setActive(false);
                enemiesKilled++;
            }
            
            System.out.println("CHEAT: Killed all " + enemiesKilled + " enemies!");
        }
    }
    
    private void updateMagazineSystem(float delta) {
        if (isReloading) {
            reloadTimer += delta;
            reloadAnimationTime += delta;
            
            // Check if reload is complete
            if (reloadTimer >= reloadDuration) {
                completeReload();
            }
        }
        
        // Auto-reload when magazine is empty
        if (autoReload && currentAmmo <= 0 && !isReloading) {
            startReload();
        }
    }
    
    private void startReload() {
        if (isReloading) return; // Already reloading
        
        isReloading = true;
        reloadTimer = 0f;
        reloadAnimationTime = 0f;
        System.out.println("Started reloading! Duration: " + reloadDuration + "s");
    }
    
    private void completeReload() {
        isReloading = false;
        currentAmmo = maxAmmo;
        reloadTimer = 0f;
        reloadAnimationTime = 0f;
        System.out.println("Reload complete! Ammo: " + currentAmmo + "/" + maxAmmo);
    }
    
    private void shoot() {
        // Check if we can shoot
        if (isReloading || currentAmmo <= 0) {
            return;
        }
        
        // Get weapon properties
        Player player = App.getInstance().getPlayer();
        int weaponDamage = 1; // Default damage
        int projectileCount = 1; // Default projectile count
        
        if (player != null && player.getCharacter() != null && player.getCharacter().getWeapon() != null) {
            Weapon weapon = player.getCharacter().getWeapon();
            weaponDamage = weapon.getDamage() != null ? weapon.getDamage() : 1;
            projectileCount = weapon.getProjectile() != null ? weapon.getProjectile() : 1;
            
            // Apply DAMAGER ability (25% damage boost)
            if (player.getCharacter().hasActiveAbility(Ability.DAMAGER)) {
                weaponDamage = (int) Math.ceil(weaponDamage * 1.25f);
            }
            
            // Apply PROCREASE ability (bonus projectiles)
            projectileCount += player.getCharacter().getBonusProjectiles();
        }
        
        // Calculate bullet spawn position (at weapon tip)
        float weaponOffsetX = facingLeft ? -20f : 20f;
        float weaponOffsetY = 5f;
        float bulletStartX = playerPosition.x + weaponOffsetX;
        float bulletStartY = playerPosition.y + weaponOffsetY;
        
        // Calculate base direction towards mouse
        float deltaX = mouseWorldPosition.x - bulletStartX;
        float deltaY = mouseWorldPosition.y - bulletStartY;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 0) {
            // Normalize base direction
            float baseDirX = deltaX / distance;
            float baseDirY = deltaY / distance;
            
            // Fire multiple projectiles
            for (int i = 0; i < projectileCount; i++) {
                // Calculate spread for multiple projectiles
                float spreadAngle = 0f;
                if (projectileCount > 1) {
                    // Spread projectiles in a cone (15 degrees total spread)
                    float maxSpread = 15f; // degrees
                    spreadAngle = (i - (projectileCount - 1) / 2f) * (maxSpread / Math.max(1, projectileCount - 1));
                }
                
                // Apply spread to direction
                float radians = (float) Math.toRadians(spreadAngle);
                float cos = (float) Math.cos(radians);
                float sin = (float) Math.sin(radians);
                
                float spreadDirX = baseDirX * cos - baseDirY * sin;
                float spreadDirY = baseDirX * sin + baseDirY * cos;
                
                // Apply bullet speed
                float bulletSpeed = 500f; // pixels per second
                float velocityX = spreadDirX * bulletSpeed;
                float velocityY = spreadDirY * bulletSpeed;
                
                // Create and add bullet with weapon damage
                Bullet bullet = new Bullet(bulletStartX, bulletStartY, velocityX, velocityY, weaponRotation, weaponDamage);
                bullets.add(bullet);
            }
            
            // Consume ammo (one ammo per shot, regardless of projectile count)
            currentAmmo--;
            
            System.out.println("Shot fired! Projectiles: " + projectileCount + ", Damage: " + weaponDamage + ", Ammo: " + currentAmmo + "/" + maxAmmo);
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
    
    private void renderEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                enemy.render(batch);
            }
        }
    }
    
    private void renderPlayer() {
        // Choose the appropriate animation based on movement state
        Animation<TextureRegion> currentAnimation = isMoving ? movingAnimation : idleAnimation;
        
        // Get the current frame
        TextureRegion currentFrame = currentAnimation.getKeyFrame(animationTime);
        
        // Make player larger and more visible
        float playerSize = 96f; // Make player same size as tiles (1.5x bigger)
        float playerRenderX = playerPosition.x - playerSize / 2f;
        float playerRenderY = playerPosition.y - playerSize / 2f;
        
        // Flip the texture region if facing left
        if (facingLeft && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!facingLeft && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
        
        // Apply damage tint if player took damage
        if (playerTookDamage) {
            batch.setColor(1f, 0.3f, 0.3f, 1f); // Red tint
        }
        
        // Render the current animation frame
        batch.draw(currentFrame, playerRenderX, playerRenderY, playerSize, playerSize);
        
        // Reset color
        if (playerTookDamage) {
            batch.setColor(Color.WHITE);
        }
        
        // Debug output (reduced frequency)
        if (Gdx.graphics.getFrameId() % 300 == 0) { // Print every 5 seconds
            System.out.println("Player: (" + (int)playerPosition.x + ", " + (int)playerPosition.y + ") " + (isMoving ? "moving" : "idle"));
        }
    }
    
    private void renderWeapon() {
        if (weaponTexture == null) return;
        
        // Weapon size and position (made bigger)
        float weaponSize = 48f; // Increased from 32f to 48f (50% bigger)
        float weaponOffsetX = facingLeft ? -20f : 20f; // Position weapon on the side the character is facing
        float weaponOffsetY = 5f; // Slightly above center
        
        // Calculate weapon position relative to player
        float weaponX = playerPosition.x + weaponOffsetX;
        float weaponY = playerPosition.y + weaponOffsetY;
        
        // Choose texture based on reload state
        TextureRegion weaponRegion;
        if (isReloading && reloadAnimation != null) {
            // Show reload animation
            weaponRegion = reloadAnimation.getKeyFrame(reloadAnimationTime);
        } else {
            // Show normal weapon texture
            weaponRegion = new TextureRegion(weaponTexture);
        }
        
        // Render weapon with rotation towards mouse
        // The origin is set to the center of the weapon for proper rotation
        batch.draw(weaponRegion, 
                   weaponX - weaponSize/2f, weaponY - weaponSize/2f, // position
                   weaponSize/2f, weaponSize/2f, // origin (center of weapon)
                   weaponSize, weaponSize, // size
                   1f, 1f, // scale
                   weaponRotation); // rotation
    }
    
    private void renderBullets() {
        for (Bullet bullet : bullets) {
            if (bullet.active) {
                float bulletSize = 16f; // Make bullets half the previous size
                float bulletX = bullet.position.x - bulletSize/2f;
                float bulletY = bullet.position.y - bulletSize/2f;
                batch.draw(bulletTexture, bulletX, bulletY, bulletSize, bulletSize);
            }
        }
        
        // Debug output for bullet count
        if (Gdx.graphics.getFrameId() % 60 == 0 && bullets.size > 0) { // Print once per second
            System.out.println("Active bullets: " + bullets.size);
        }
    }
    
    private void renderEyeBatBullets() {
        for (Enemy enemy : enemies) {
            if (enemy instanceof EyeBat && enemy.isActive()) {
                EyeBat eyebat = (EyeBat) enemy;
                for (EyeBat.EyeBatBullet bullet : eyebat.getBullets()) {
                    if (bullet.active) {
                        float bulletSize = 12f; // Slightly smaller than player bullets
                        float bulletX = bullet.position.x - bulletSize/2f;
                        float bulletY = bullet.position.y - bulletSize/2f;
                        batch.draw(bulletTexture, bulletX, bulletY, bulletSize, bulletSize);
                    }
                }
            }
        }
    }
    
    private void renderBorder() {
        // Set up shape renderer for border
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Calculate border position (centered on camera)
        float borderCenterX = camera.position.x;
        float borderCenterY = camera.position.y;
        float borderRadius = currentBorderSize / 2f;
        
        // Draw border circle outline
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(borderCenterX, borderCenterY, borderRadius, 64); // 64 segments for smooth circle
        shapeRenderer.end();
        
        // Draw warning area (slightly inside border)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(borderCenterX, borderCenterY, borderRadius - 20f, 32);
        shapeRenderer.end();
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
    
    private void updateBullets(float delta) {
        // Update bullet positions
        for (Bullet bullet : bullets) {
            if (bullet.active) {
                bullet.update(delta);
                
                // Remove bullets that are off screen
                if (bullet.isOffScreen(camera)) {
                    bullet.active = false;
                }
            }
        }
        
        // Remove inactive bullets using traditional loop
        for (int i = bullets.size - 1; i >= 0; i--) {
            if (!bullets.get(i).active) {
                bullets.removeIndex(i);
            }
        }
    }
    
    private void updateTimer(float delta) {
        if (!gameEnded) {
            gameTimeRemaining -= delta;
            
            // Check if time is up
            if (gameTimeRemaining <= 0) {
                gameTimeRemaining = 0;
                gameEnded = true;
                // End the game with victory
                endGameWithSummary(com.example.dawn.models.GameSummary.GameEndReason.TIME_UP, true);
            }
        }
    }
    
    private void updateTentacleSpawning(float delta) {
        tentacleSpawnTimer += delta;
        
        // Check if it's time to spawn tentacles
        if (tentacleSpawnTimer >= TENTACLE_SPAWN_INTERVAL) {
            tentacleSpawnTimer = 0f; // Reset timer
            
            // Calculate how many tentacles to spawn based on elapsed time
            // i/30 tentacles where i is seconds elapsed
            int tentaclesToSpawn = Math.max(1, (int)(gameElapsedTime / 30f));
            
            for (int i = 0; i < tentaclesToSpawn; i++) {
                spawnTentacleOnScreen();
            }
            
            System.out.println("Spawned " + tentaclesToSpawn + " tentacle monsters at " + gameElapsedTime + " seconds");
        }
    }
    
    private void spawnTentacleOnScreen() {
        // Spawn tentacle within current screen bounds
        float screenWidth = camera.viewportWidth;
        float screenHeight = camera.viewportHeight;
        
        // Get random position within screen bounds
        float x = camera.position.x + MathUtils.random(-screenWidth/2f, screenWidth/2f);
        float y = camera.position.y + MathUtils.random(-screenHeight/2f, screenHeight/2f);
        
        // Ensure minimum distance from player
        float distanceFromPlayer = playerPosition.dst(x, y);
        if (distanceFromPlayer < ENEMY_SPAWN_DISTANCE) {
            // Move spawn point away from player
            float angle = MathUtils.atan2(y - playerPosition.y, x - playerPosition.x);
            x = playerPosition.x + MathUtils.cos(angle) * ENEMY_SPAWN_DISTANCE;
            y = playerPosition.y + MathUtils.sin(angle) * ENEMY_SPAWN_DISTANCE;
        }
        
        // Snap to tile grid for better placement
        x = Math.round(x / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
        y = Math.round(y / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
        
        TentacleMonster tentacle = new TentacleMonster(x, y);
        enemies.add(tentacle);
        
        System.out.println("Spawned tentacle monster at (" + x + ", " + y + ")");
    }
    
    private void updateEyeBatSpawning(float delta) {
        // Check if EyeBat spawning should start (after t/4 seconds, where t is total game time)
        float totalGameTime = gameController.getGameDurationMinutes() * 60f; // Total game time in seconds
        float spawnStartTime = totalGameTime / 4f; // Start spawning after 1/4 of total game time
        
        if (!eyebatSpawningStarted && gameElapsedTime >= spawnStartTime) {
            eyebatSpawningStarted = true;
            System.out.println("EyeBat spawning started at " + gameElapsedTime + " seconds (spawn start time: " + spawnStartTime + ")");
        }
        
        if (!eyebatSpawningStarted) {
            return;
        }
        
        eyebatSpawnTimer += delta;
        
        // Check if it's time to spawn eyebats
        if (eyebatSpawnTimer >= EYEBAT_SPAWN_INTERVAL) {
            eyebatSpawnTimer = 0f; // Reset timer
            
            // Calculate how many eyebats to spawn: (4i-t+30)/30
            // where i is the number of 10-second intervals that have passed
            int intervals = (int)(gameElapsedTime / EYEBAT_SPAWN_INTERVAL);
            int formulaResult = (4 * intervals - (int)gameElapsedTime + 30) / 30;
            int eyebatsToSpawn = Math.max(1, formulaResult); // Ensure at least 1 spawns
            
            System.out.println("EyeBat spawn calculation: intervals=" + intervals + ", gameElapsedTime=" + (int)gameElapsedTime + ", formula=" + formulaResult + ", spawning=" + eyebatsToSpawn);
            
            for (int i = 0; i < eyebatsToSpawn; i++) {
                spawnEyeBatOnScreen();
            }
            
            System.out.println("Spawned " + eyebatsToSpawn + " eyebats at " + gameElapsedTime + " seconds");
        }
    }
    
    private void spawnEyeBatOnScreen() {
        // Spawn eyebat within current screen bounds
        float screenWidth = camera.viewportWidth;
        float screenHeight = camera.viewportHeight;
        
        // Get random position within screen bounds
        float x = camera.position.x + MathUtils.random(-screenWidth/2f, screenWidth/2f);
        float y = camera.position.y + MathUtils.random(-screenHeight/2f, screenHeight/2f);
        
        // Ensure minimum distance from player
        float distanceFromPlayer = playerPosition.dst(x, y);
        if (distanceFromPlayer < ENEMY_SPAWN_DISTANCE) {
            // Move spawn point away from player
            float angle = MathUtils.atan2(y - playerPosition.y, x - playerPosition.x);
            x = playerPosition.x + MathUtils.cos(angle) * ENEMY_SPAWN_DISTANCE;
            y = playerPosition.y + MathUtils.sin(angle) * ENEMY_SPAWN_DISTANCE;
        }
        
        // Snap to tile grid for better placement
        x = Math.round(x / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
        y = Math.round(y / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
        
        EyeBat eyebat = new EyeBat(x, y);
        enemies.add(eyebat);
        
        System.out.println("Spawned eyebat at (" + x + ", " + y + ")");
    }
    
    private void updateGlobalTreeSpawning() {
        // Calculate which tree area the player is currently in
        float areaX = Math.round(playerPosition.x / TREE_AREA_SIZE) * TREE_AREA_SIZE;
        float areaY = Math.round(playerPosition.y / TREE_AREA_SIZE) * TREE_AREA_SIZE;
        Vector2 currentArea = new Vector2(areaX, areaY);
        
        // Check if we've already spawned trees in this area
        boolean areaAlreadySpawned = false;
        for (Vector2 spawnedArea : spawnedTreeAreas) {
            if (spawnedArea.equals(currentArea)) {
                areaAlreadySpawned = true;
                break;
            }
        }
        
        // If not spawned, spawn trees in this area
        if (!areaAlreadySpawned) {
            spawnTreesInArea(areaX, areaY);
            spawnedTreeAreas.add(new Vector2(areaX, areaY));
            System.out.println("Spawned trees in new area: (" + areaX + ", " + areaY + ")");
        }
    }
    
    private void spawnTreesInArea(float centerX, float centerY) {
        // Spawn trees randomly within this area
        int treesToSpawn = TREE_MONSTERS_PER_AREA * 4; // Same as initial spawning
        
        for (int i = 0; i < treesToSpawn; i++) {
            // Random position within the area
            float x = centerX + MathUtils.random(-TREE_AREA_SIZE/2f, TREE_AREA_SIZE/2f);
            float y = centerY + MathUtils.random(-TREE_AREA_SIZE/2f, TREE_AREA_SIZE/2f);
            
            // Ensure minimum distance from player
            float distanceFromPlayer = playerPosition.dst(x, y);
            if (distanceFromPlayer < ENEMY_SPAWN_DISTANCE) {
                // Skip this tree if too close to player
                continue;
            }
            
            // Snap to tile grid for better placement
            x = Math.round(x / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
            y = Math.round(y / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
            
            TreeMonster treeMonster = new TreeMonster(x, y);
            enemies.add(treeMonster);
        }
    }
    
    private void updateElderSpawning(float delta) {
        // Check if Elder should spawn (at halfway point of total game time)
        float totalGameTime = gameController.getGameDurationMinutes() * 60f; // Total game time in seconds
        float elderSpawnTime = totalGameTime / 2f; // Spawn at halfway point
        
        if (!elderSpawned && gameElapsedTime >= elderSpawnTime) {
            spawnElder();
            elderSpawned = true;
            
            // Initialize border system when Elder spawns
            initialBorderSize = Math.max(camera.viewportWidth, camera.viewportHeight);
            currentBorderSize = initialBorderSize;
            
            System.out.println("Elder spawned at " + gameElapsedTime + " seconds! Border system activated.");
        }
        
        // Update border shrinking if Elder is alive
        if (elder != null && elder.isActive()) {
            currentBorderSize -= BORDER_SHRINK_RATE * delta;
            currentBorderSize = Math.max(currentBorderSize, 100f); // Minimum border size
            
            // Check if player is outside border and damage them
            checkBorderCollision();
        }
    }
    
    private void spawnElder() {
        // Spawn Elder near the player but not too close
        float angle = MathUtils.random(0f, 360f);
        float distance = ENEMY_SPAWN_DISTANCE * 1.5f; // Spawn further away
        
        float x = playerPosition.x + MathUtils.cosDeg(angle) * distance;
        float y = playerPosition.y + MathUtils.sinDeg(angle) * distance;
        
        // Create Elder using reflection to avoid import issues
        try {
            Class<?> elderClass = Class.forName("com.example.dawn.entity.Elder");
            elder = (Enemy) elderClass.getConstructor(float.class, float.class).newInstance(x, y);
            enemies.add(elder);
            System.out.println("Elder boss spawned at (" + x + ", " + y + ")");
        } catch (Exception e) {
            System.out.println("Failed to spawn Elder: " + e.getMessage());
            // Fallback: create a tree monster as placeholder
            elder = new TreeMonster(x, y);
            enemies.add(elder);
        }
    }
    
    private void checkBorderCollision() {
        // Calculate distance from player to center of border (camera center)
        float borderCenterX = camera.position.x;
        float borderCenterY = camera.position.y;
        float distanceFromCenter = playerPosition.dst(borderCenterX, borderCenterY);
        
        // Check if player is outside the border
        if (distanceFromCenter > currentBorderSize / 2f) {
            // Damage player
            Player player = App.getInstance().getPlayer();
            if (player != null && player.getCharacter() != null) {
                int currentHp = player.getCharacter().getHp();
                int newHp = Math.max(0, currentHp - 1);
                player.getCharacter().setHp(newHp);
                System.out.println("Player hit border! HP: " + currentHp + " -> " + newHp);
                
                // Push player back inside border
                float pushBackDistance = (currentBorderSize / 2f) - 10f; // Slightly inside border
                float angle = MathUtils.atan2(playerPosition.y - borderCenterY, playerPosition.x - borderCenterX);
                playerPosition.x = borderCenterX + MathUtils.cos(angle) * pushBackDistance;
                playerPosition.y = borderCenterY + MathUtils.sin(angle) * pushBackDistance;
            }
        }
    }
    
    private void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                // Update tentacle monster target position
                if (enemy instanceof TentacleMonster) {
                    ((TentacleMonster) enemy).setTargetPosition(playerPosition);
                }
                // Update eyebat target position
                if (enemy instanceof EyeBat) {
                    ((EyeBat) enemy).setTargetPosition(playerPosition);
                }
                // Update elder target position
                if (enemy == elder && elder != null) {
                    try {
                        // Use reflection to call setTargetPosition
                        elder.getClass().getMethod("setTargetPosition", Vector2.class).invoke(elder, playerPosition);
                    } catch (Exception e) {
                        // Ignore reflection errors
                    }
                }
                enemy.update(delta);
            }
        }
        
        // Remove inactive enemies
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (!enemies.get(i).isActive()) {
                enemies.removeIndex(i);
            }
        }
    }
    
    private void checkCollisions() {
        // Check player collision with enemies
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.collidesWith(playerPosition, PLAYER_COLLISION_RADIUS)) {
                enemy.onPlayerCollision();
                // Trigger player damage animation
                playerTookDamage = true;
                damageAnimationTimer = 0f;
                
                // Check if player died after collision
                Player player = App.getInstance().getPlayer();
                if (player != null && player.getCharacter() != null && player.getCharacter().getHp() <= 0) {
                    System.out.println("Player died from enemy collision!");
                    if (!gameEnded) {
                        gameEnded = true;
                        endGameWithSummary(GameSummary.GameEndReason.PLAYER_DIED, false);
                    }
                }
            }
        }
        
        // Check player bullet collision with enemies
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            if (!bullet.active) continue;
            
            for (Enemy enemy : enemies) {
                if (enemy.isActive() && enemy.collidesWith(bullet.position, 8f)) { // Small bullet radius
                    int enemyHpBefore = enemy.getHp();
                    enemy.takeDamage(bullet.damage); // Apply bullet damage
                    bullet.active = false; // Bullet is consumed
                    
                    // Check if enemy died and create death effect
                    if (!enemy.isActive() && enemyHpBefore > 0) {
                        // Create death effect at enemy position
                        DeathEffect deathEffect = new DeathEffect(enemy.getPosition().x, enemy.getPosition().y);
                        deathEffects.add(deathEffect);
                        
                        // Calculate score and XP based on enemy's initial HP
                        int scoreGained = getEnemyInitialHp(enemy);
                        int xpGained = scoreGained / 5; // XP is 1/5 of score gained
                        
                        // Increment kill count, score, and XP
                        Player player = App.getInstance().getPlayer();
                        if (player != null && player.getCharacter() != null) {
                            int currentKills = player.getCharacter().getKills();
                            int currentScore = player.getCharacter().getScore();
                            player.getCharacter().setKills(currentKills + 1);
                            player.getCharacter().setScore(currentScore + scoreGained);
                            player.getCharacter().addXp(xpGained);
                            System.out.println("Enemy killed! Total kills: " + (currentKills + 1) + ", Score gained: " + scoreGained + ", XP gained: " + xpGained + ", Total score: " + (currentScore + scoreGained));
                        }
                    }
                    
                    break; // One bullet can only hit one enemy
                }
            }
        }
        
        // Check EyeBat bullet collision with player
        for (Enemy enemy : enemies) {
            if (enemy instanceof EyeBat && enemy.isActive()) {
                EyeBat eyebat = (EyeBat) enemy;
                for (int i = eyebat.getBullets().size - 1; i >= 0; i--) {
                    EyeBat.EyeBatBullet bullet = eyebat.getBullets().get(i);
                    if (bullet.active) {
                        float distance = playerPosition.dst(bullet.position);
                        if (distance < PLAYER_COLLISION_RADIUS + 6f) { // Player radius + bullet radius
                            // EyeBat bullet hit player (fixed damage of 1)
                            Player player = App.getInstance().getPlayer();
                            if (player != null && player.getCharacter() != null) {
                                int currentHp = player.getCharacter().getHp();
                                int newHp = Math.max(0, currentHp - 1); // EyeBat bullets do 1 damage
                                player.getCharacter().setHp(newHp);
                                System.out.println("Player hit by EyeBat bullet! HP: " + currentHp + " -> " + newHp);
                                
                                // Trigger player damage animation
                                playerTookDamage = true;
                                damageAnimationTimer = 0f;
                                
                                if (newHp <= 0) {
                                    System.out.println("Player died from EyeBat bullet!");
                                    if (!gameEnded) {
                                        gameEnded = true;
                                        endGameWithSummary(GameSummary.GameEndReason.PLAYER_DIED, false);
                                    }
                                }
                            }
                            bullet.active = false; // Remove bullet
                        }
                        
                        // Remove bullets that are off screen
                        if (bullet.isOffScreen(camera.position.x, camera.position.y, camera.viewportWidth, camera.viewportHeight)) {
                            bullet.active = false;
                        }
                    }
                }
            }
        }
    }
    
    private int getEnemyInitialHp(Enemy enemy) {
        // Return the initial HP of different enemy types for scoring
        if (enemy instanceof TentacleMonster) {
            return 25; // TentacleMonster initial HP
        } else if (enemy instanceof EyeBat) {
            return 50; // EyeBat initial HP
        } else if (enemy instanceof Elder) {
            return 400; // Elder initial HP
        } else if (enemy instanceof TreeMonster) {
            return 1; // TreeMonster can't be killed, but give minimal score if somehow killed
        }
        return 10; // Default score for unknown enemy types
    }
    
    private void endGameWithSummary(GameSummary.GameEndReason reason, boolean isWin) {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) {
            System.out.println("No player found, returning to main menu");
            gameController.returnToMainMenu();
            return;
        }
        
        // Calculate game statistics
        int currentKills = player.getCharacter().getKills();
        int currentScore = player.getCharacter().getScore();
        int currentXp = player.getCharacter().getXp();
        
        int killsGained = currentKills - gameStartKills;
        int scoreGained = currentScore - gameStartScore;
        int xpGained = currentXp - gameStartXp;
        
        // Calculate survival time
        float totalGameTime = gameController.getGameDurationMinutes() * 60f;
        int survivalTimeSeconds = (int)(totalGameTime - gameTimeRemaining);
        
        // Create game summary
        GameSummary summary = new GameSummary(
            player.getUsername(),
            player.getAvatarPath(),
            player.getCharacter().getName(),
            killsGained,
            scoreGained,
            player.getCharacter().getLevel(),
            currentXp,
            xpGained,
            survivalTimeSeconds,
            reason,
            isWin
        );
        
        // Update player statistics
        updatePlayerStatistics(player, killsGained, scoreGained, survivalTimeSeconds, isWin);
        
        // Save player data
        gameController.getDatabaseManager().savePlayer(player);
        
        // Show game summary screen
        GameSummaryController summaryController = new GameSummaryController(
            gameController.getDatabaseManager(), 
            gameController.getGameDurationMinutes()
        );
        Dawn.getInstance().setScreen(new GameSummaryScreen(summaryController, summary, GameAssetManager.getInstance().getSkin()));
    }
    
    private void updatePlayerStatistics(Player player, int killsGained, int scoreGained, int survivalTime, boolean isWin) {
        // Update total games
        player.addTotalGames();
        
        // Update wins if player won
        if (isWin) {
            player.addWin();
        }
        
        // Update total kills (add kills gained this game to player's total)
        player.addKills(killsGained);
        
        // Update high score if current score is higher
        if (player.getCharacter().getScore() > player.getHighScore()) {
            player.setHighScore(player.getCharacter().getScore());
        }
        
        // Update total survival duration
        player.addSurvivalDuration(survivalTime);
        
        System.out.println("Player statistics updated - Total games: " + player.getTotalGames() + 
                          ", Wins: " + player.getWins() + ", Total kills: " + player.getKills() + 
                          ", High score: " + player.getHighScore() + ", Total survival: " + player.getSurvivalDuration());
    }
    
    private void renderUI() {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) return;
        
        // Set UI camera
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        
        // UI Box dimensions and position
        float boxX = 20f;
        float boxY = Gdx.graphics.getHeight() - 200f;
        float boxWidth = 300f;
        float boxHeight = 180f;
        
        // Draw UI background box
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); // Semi-transparent black
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();
        
        // Draw UI border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();
        
        // Draw UI content
        batch.begin();
        
        // Character avatar
        float avatarSize = 60f;
        float avatarX = boxX + 10f;
        float avatarY = boxY + boxHeight - avatarSize - 10f;
        batch.draw(avatarTexture, avatarX, avatarY, avatarSize, avatarSize);
        
        // Text information
        float textX = avatarX + avatarSize + 10f;
        float textY = boxY + boxHeight - 20f;
        float lineHeight = 18f;
        
        // Username and Character name
        font.draw(batch, "Player: " + player.getUsername(), textX, textY);
        textY -= lineHeight;
        font.draw(batch, "Character: " + player.getCharacter().getName(), textX, textY);
        textY -= lineHeight;
        
        // HP, Score (session only), Kills (session only)
        font.draw(batch, "HP: " + player.getCharacter().getHp(), textX, textY);
        textY -= lineHeight;
        
        // Show score gained in this game session only
        int sessionScore = player.getCharacter().getScore() - gameStartScore;
        font.draw(batch, "Score: " + sessionScore, textX, textY);
        textY -= lineHeight;
        
        // Show kills gained in this game session only
        int sessionKills = player.getCharacter().getKills() - gameStartKills;
        font.draw(batch, "Kills: " + sessionKills, textX, textY);
        textY -= lineHeight;
        
        // Level (session-based: start from level 1 each game)
        int displayLevel = 1 + (player.getCharacter().getLevel() - lastKnownLevel);
        font.draw(batch, "Level: " + displayLevel, textX, textY);
        textY -= lineHeight + 5f;
        
        batch.end();
        
        // XP Bar (session progress only)
        float barX = textX;
        float barY = textY - 15f;
        float barWidth = 150f;
        float barHeight = 12f;
        
        // Calculate session XP progress (show progress within current session level)
        int sessionXpGained = player.getCharacter().getXp() - gameStartXp;
        int sessionLevel = 1 + (player.getCharacter().getLevel() - lastKnownLevel);
        int xpNeededForNextSessionLevel = 20 * sessionLevel; // XP needed to reach next session level
        int sessionXpForCurrentLevel = sessionXpGained % xpNeededForNextSessionLevel;
        if (sessionLevel > 1) {
            // If we've leveled up, show progress within current level
            int xpForPreviousLevels = 0;
            for (int i = 1; i < sessionLevel; i++) {
                xpForPreviousLevels += 20 * i;
            }
            sessionXpForCurrentLevel = sessionXpGained - xpForPreviousLevels;
        } else {
            sessionXpForCurrentLevel = sessionXpGained;
        }
        float xpProgress = xpNeededForNextSessionLevel > 0 ? (float) sessionXpForCurrentLevel / xpNeededForNextSessionLevel : 0f;
        
        // Draw XP bar background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f); // Dark gray
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.end();
        
        // Draw XP bar fill
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f); // Green
        shapeRenderer.rect(barX, barY, barWidth * xpProgress, barHeight);
        shapeRenderer.end();
        
        // Draw XP bar border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.end();
        
        // Draw XP text on bar (session progress)
        batch.begin();
        font.draw(batch, sessionXpForCurrentLevel + "/" + xpNeededForNextSessionLevel + " XP", barX + 5f, barY + barHeight - 2f);
        batch.end();
        
        // Render timer in top-right corner
        renderTimer();
        
        // Render bullet icons under timer
        renderBulletIcons();
        
        // Render active abilities
        renderActiveAbilities();
    }
    
    private void renderTimer() {
        // Calculate minutes and seconds
        int totalSeconds = (int) Math.ceil(gameTimeRemaining);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        
        // Format timer text
        String timerText = String.format("%02d:%02d", minutes, seconds);
        
        // Position in top-right corner
        float timerX = Gdx.graphics.getWidth() - 200f; // 200 pixels from right edge
        float timerY = Gdx.graphics.getHeight() - 30f; // 30 pixels from top
        
        // Change color based on remaining time
        if (gameTimeRemaining <= 60f) {
            timerFont.setColor(Color.RED); // Red when less than 1 minute
        } else if (gameTimeRemaining <= 180f) {
            timerFont.setColor(Color.ORANGE); // Orange when less than 3 minutes
        } else {
            timerFont.setColor(Color.WHITE); // White otherwise
        }
        
        // Draw timer background box
        float textWidth = 180f;
        float textHeight = 60f;
        float boxX = timerX - 10f;
        float boxY = timerY - textHeight + 10f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); // Semi-transparent black
        shapeRenderer.rect(boxX, boxY, textWidth, textHeight);
        shapeRenderer.end();
        
        // Draw timer border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(boxX, boxY, textWidth, textHeight);
        shapeRenderer.end();
        
        // Draw timer text
        batch.begin();
        timerFont.draw(batch, timerText, timerX, timerY);
        batch.end();
    }
    
    private void renderBulletIcons() {
        // Position bullet icons under the timer
        float iconSize = 18f; // Slightly smaller icons to fit more
        float iconSpacing = 22f; // Tighter spacing
        float startX = Gdx.graphics.getWidth() - 300f; // Move further left for more space
        float startY = Gdx.graphics.getHeight() - 120f; // Below timer
        
        // Calculate how many bullets to show per row (max 8 per row for better fit)
        int bulletsPerRow = Math.min(maxAmmo, 8);
        int rows = (int) Math.ceil((float) maxAmmo / bulletsPerRow);
        
        // Draw background for bullet icons (bigger box)
        float backgroundWidth = bulletsPerRow * iconSpacing + 30f; // More padding
        float backgroundHeight = rows * (iconSize + 8f) + 30f; // More vertical spacing
        float backgroundX = startX - 15f;
        float backgroundY = startY - backgroundHeight + 15f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); // Semi-transparent black
        shapeRenderer.rect(backgroundX, backgroundY, backgroundWidth, backgroundHeight);
        shapeRenderer.end();
        
        // Draw background border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(backgroundX, backgroundY, backgroundWidth, backgroundHeight);
        shapeRenderer.end();
        
        // Draw bullet icons
        batch.begin();
        for (int i = 0; i < maxAmmo; i++) {
            int row = i / bulletsPerRow;
            int col = i % bulletsPerRow;
            
            float iconX = startX + col * iconSpacing;
            float iconY = startY - row * (iconSize + 8f);
            
            // Choose color based on ammo status
            Color iconColor;
            if (i < currentAmmo) {
                iconColor = Color.YELLOW; // Available ammo
            } else {
                iconColor = Color.GRAY; // Empty
            }
            
            // Tint the bullet texture
            batch.setColor(iconColor);
            batch.draw(bulletTexture, iconX, iconY, iconSize, iconSize);
        }
        
        // Reset batch color
        batch.setColor(Color.WHITE);
        
        // Draw reload status text if reloading
        if (isReloading) {
            font.setColor(Color.ORANGE);
            float reloadProgress = (reloadTimer / reloadDuration) * 100f;
            String reloadText = "Reloading... " + (int)reloadProgress + "%";
            font.draw(batch, reloadText, startX, startY - backgroundHeight - 10f);
            font.setColor(Color.WHITE); // Reset font color
        }
        
        batch.end();
    }
    
    private void renderActiveAbilities() {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) return;
        
        List<com.example.dawn.models.ActiveAbility> activeAbilities = player.getCharacter().getActiveAbilities();
        if (activeAbilities.isEmpty()) return;
        
        // Position abilities in top-left corner
        float startX = 20f;
        float startY = Gdx.graphics.getHeight() - 250f; // Below the main UI box
        float abilityHeight = 30f;
        float abilitySpacing = 35f;
        
        // Draw background for abilities
        float backgroundWidth = 250f;
        float backgroundHeight = activeAbilities.size() * abilitySpacing + 20f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); // Semi-transparent black
        shapeRenderer.rect(startX, startY - backgroundHeight, backgroundWidth, backgroundHeight);
        shapeRenderer.end();
        
        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW); // Yellow border for abilities
        shapeRenderer.rect(startX, startY - backgroundHeight, backgroundWidth, backgroundHeight);
        shapeRenderer.end();
        
        // Draw ability text
        batch.begin();
        font.setColor(Color.YELLOW);
        
        for (int i = 0; i < activeAbilities.size(); i++) {
            com.example.dawn.models.ActiveAbility activeAbility = activeAbilities.get(i);
            float textY = startY - (i * abilitySpacing) - 15f;
            
            String abilityText = activeAbility.getAbility().getName() + " (" + activeAbility.getRemainingSeconds() + "s)";
            font.draw(batch, abilityText, startX + 10f, textY);
        }
        
        font.setColor(Color.WHITE); // Reset font color
        batch.end();
    }
    
    private void loadReloadAnimations() {
        Player player = App.getInstance().getPlayer();
        if (player != null && player.getCharacter() != null && player.getCharacter().getWeapon() != null) {
            Weapon weapon = player.getCharacter().getWeapon();
            if (weapon.getAnimations() != null && !weapon.getAnimations().isEmpty()) {
                for (String animationPath : weapon.getAnimations()) {
                    if (animationPath != null && !animationPath.isEmpty()) {
                        try {
                            Texture texture = new Texture(Gdx.files.internal(animationPath));
                            reloadTextures.add(texture);
                            System.out.println("Loaded reload animation texture: " + animationPath);
                        } catch (Exception e) {
                            System.out.println("Failed to load reload animation texture: " + animationPath + " - " + e.getMessage());
                        }
                    }
                }
            }
        }
        
        // Create reload animation
        if (reloadTextures.size > 0) {
            Array<TextureRegion> reloadFrames = new Array<>();
            for (Texture texture : reloadTextures) {
                reloadFrames.add(new TextureRegion(texture));
            }
            // Animation duration should match reload time
            float frameTime = reloadDuration / reloadFrames.size;
            reloadAnimation = new Animation<>(frameTime, reloadFrames, Animation.PlayMode.NORMAL);
            System.out.println("Created reload animation with " + reloadFrames.size + " frames, frame time: " + frameTime + "s");
        } else {
            // No reload animation available
            reloadAnimation = null;
            System.out.println("No reload animation textures found");
        }
    }
    
    private void loadDeathAnimations() {
        // Load death effect textures
        for (int i = 0; i < 4; i++) {
            try {
                String texturePath = "textures/DeathFX_" + i + ".png";
                Texture texture = new Texture(Gdx.files.internal(texturePath));
                deathTextures.add(texture);
                System.out.println("Loaded death effect texture: " + texturePath);
            } catch (Exception e) {
                System.out.println("Failed to load death effect texture: DeathFX_" + i + ".png - " + e.getMessage());
            }
        }
        
        // Create death animation
        if (deathTextures.size > 0) {
            Array<TextureRegion> deathFrames = new Array<>();
            for (Texture texture : deathTextures) {
                deathFrames.add(new TextureRegion(texture));
            }
            // Death animation plays once over 0.8 seconds
            float frameTime = 0.8f / deathFrames.size;
            deathAnimation = new Animation<>(frameTime, deathFrames, Animation.PlayMode.NORMAL);
            System.out.println("Created death animation with " + deathFrames.size + " frames, frame time: " + frameTime + "s");
        } else {
            deathAnimation = null;
            System.out.println("No death animation textures found");
        }
    }
    
    private void updateDeathEffects(float delta) {
        // Update death effect animations
        for (DeathEffect effect : deathEffects) {
            if (effect.active) {
                effect.update(delta);
                
                // Check if animation is finished
                if (deathAnimation != null && effect.isFinished(deathAnimation.getAnimationDuration())) {
                    effect.active = false;
                }
            }
        }
        
        // Remove finished death effects
        for (int i = deathEffects.size - 1; i >= 0; i--) {
            if (!deathEffects.get(i).active) {
                deathEffects.removeIndex(i);
            }
        }
    }
    
    private void updatePlayerDamageAnimation(float delta) {
        if (playerTookDamage) {
            damageAnimationTimer += delta;
            if (damageAnimationTimer >= DAMAGE_ANIMATION_DURATION) {
                playerTookDamage = false;
                damageAnimationTimer = 0f;
            }
        }
    }
    
    private void updateAbilities(float delta) {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) return;
        
        // Update active ability timers
        player.getCharacter().updateActiveAbilities(delta);
        
        // Check for level-up
        int currentLevel = player.getCharacter().getLevel();
        if (currentLevel > lastKnownLevel) {
            // Player leveled up!
            for (int level = lastKnownLevel + 1; level <= currentLevel; level++) {
                grantRandomAbility(player.getCharacter());
            }
            lastKnownLevel = currentLevel;
        }
    }
    
    private void grantRandomAbility(com.example.dawn.models.Character character) {
        // Get random ability
        Ability[] abilities = Ability.values();
        Ability randomAbility = abilities[MathUtils.random(abilities.length - 1)];
        
        System.out.println("LEVEL UP! Granted ability: " + randomAbility.getName() + " - " + randomAbility.getDescription());
        
        // Apply ability effect
        switch (randomAbility) {
            case VITALITY:
                character.addPermanentAbility(randomAbility);
                // Increase max HP and heal 1 HP
                int currentHp = character.getHp();
                int maxHp = character.getMaxHp();
                character.setHp(Math.min(currentHp + 1, maxHp));
                System.out.println("VITALITY: HP increased to " + character.getHp() + "/" + maxHp);
                break;
                
            case DAMAGER:
                character.addActiveAbility(randomAbility);
                System.out.println("DAMAGER: Weapon damage increased by 25% for 10 seconds!");
                break;
                
            case PROCREASE:
                character.addPermanentAbility(randomAbility);
                character.addBonusProjectiles(1);
                System.out.println("PROCREASE: Projectile count increased by 1!");
                break;
                
            case AMOCREASE:
                character.addPermanentAbility(randomAbility);
                character.addBonusMaxAmmo(5);
                // Update current magazine system
                maxAmmo += 5;
                currentAmmo = Math.min(currentAmmo + 5, maxAmmo); // Add some ammo too
                System.out.println("AMOCREASE: Max ammo increased by 5! New max: " + maxAmmo);
                break;
                
            case SPEEDY:
                character.addActiveAbility(randomAbility);
                System.out.println("SPEEDY: Movement speed doubled for 10 seconds!");
                break;
        }
    }
    
    private void renderDeathEffects() {
        if (deathAnimation == null) return;
        
        for (DeathEffect effect : deathEffects) {
            if (effect.active) {
                TextureRegion currentFrame = deathAnimation.getKeyFrame(effect.animationTime);
                float effectSize = 64f; // Size of death effect
                float renderX = effect.position.x - effectSize / 2f;
                float renderY = effect.position.y - effectSize / 2f;
                
                batch.draw(currentFrame, renderX, renderY, effectSize, effectSize);
            }
        }
    }
    
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        uiCamera.setToOrtho(false, width, height);
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
        
        // Dispose bullet texture
        if (bulletTexture != null) {
            bulletTexture.dispose();
        }
        
        // Dispose cursor texture
        if (cursorTexture != null) {
            cursorTexture.dispose();
        }
        
        // Dispose avatar texture
        if (avatarTexture != null) {
            avatarTexture.dispose();
        }
        
        // Dispose reload textures
        if (reloadTextures != null) {
            for (Texture texture : reloadTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
        
        // Dispose death textures
        if (deathTextures != null) {
            for (Texture texture : deathTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
        
        // Dispose UI elements
        if (font != null) {
            font.dispose();
        }
        if (timerFont != null) {
            timerFont.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        
        if (batch != null) {
            batch.dispose();
        }
        stage.dispose();
    }
} 