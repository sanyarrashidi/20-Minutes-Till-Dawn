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
    
    
    private Texture tileTexture;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> movingAnimation;
    private Array<Texture> idleTextures;
    private Array<Texture> movingTextures;
    private Texture weaponTexture;
    private Texture bulletTexture;
    private Texture cursorTexture;
    private Texture avatarTexture;
    
    
    private Vector2 playerPosition;
    private Vector2 playerVelocity;
    
    
    private static final int TILE_SIZE = 96; 
    private static final int TILES_PER_SCREEN = 20; 
    
    
    private static final float CAMERA_FOLLOW_AREA = 100f; 
    private static final float DEFAULT_PLAYER_SPEED = 300f; 
    
    
    private int moveUpKey = Input.Keys.W; 
    private int moveDownKey = Input.Keys.S; 
    private int moveLeftKey = Input.Keys.A; 
    private int moveRightKey = Input.Keys.D; 
    
    
    private float animationTime = 0f;
    private boolean isMoving = false;
    private boolean facingLeft = false; 
    
    
    private Vector2 mouseWorldPosition = new Vector2();
    private float weaponRotation = 0f;
    
    
    private Array<Bullet> bullets = new Array<>();
    private int shootKey = Input.Keys.SPACE; 
    private int reloadKey = Input.Keys.R; 
    private boolean shootKeyPressed = false; 
    
    
    private int currentAmmo; 
    private int maxAmmo; 
    private boolean isReloading = false;
    private float reloadTimer = 0f;
    private float reloadDuration = 1f; 
    private boolean autoReload = false;
    private Animation<TextureRegion> reloadAnimation;
    private Array<Texture> reloadTextures;
    private float reloadAnimationTime = 0f;
    
    
    private float fireRate = 0.2f; 
    private float fireTimer = 0f;
    private boolean canShoot = true;
    
    
    private Animation<TextureRegion> deathAnimation;
    private Array<Texture> deathTextures;
    private Array<DeathEffect> deathEffects;
    
    
    private boolean playerTookDamage = false;
    private float damageAnimationTimer = 0f;
    private static final float DAMAGE_ANIMATION_DURATION = 0.5f; 
    
    
    private boolean playerImmune = false;
    private float immunityTimer = 0f;
    private static final float IMMUNITY_DURATION = 2.0f; 
    
    
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
    
    
    private BitmapFont font;
    private BitmapFont timerFont; 
    private ShapeRenderer shapeRenderer;
    
    private OrthographicCamera uiCamera;
    
    
    private Table pauseMenuTable;
    private boolean pauseMenuVisible = false;
    
    
    private float gameTimeRemaining; 
    private boolean gameEnded = false;
    private boolean gamePaused = false;
    
    
    private int gameStartKills = 0; 
    private int gameStartScore = 0; 
    private int gameStartXp = 0; 
    private int lastKnownLevel = 1; 
    
    
    private Array<Enemy> enemies;
    private static final float PLAYER_COLLISION_RADIUS = 32f; 
    private static final int TREE_MONSTERS_PER_AREA = 1; 
    private static final float ENEMY_SPAWN_DISTANCE = 200f; 
    
    
    private float tentacleSpawnTimer = 0f;
    private static final float TENTACLE_SPAWN_INTERVAL = 3f; 
    private float gameElapsedTime = 0f;
    
    
    private float eyebatSpawnTimer = 0f;
    private static final float EYEBAT_SPAWN_INTERVAL = 10f; 
    private boolean eyebatSpawningStarted = false;
    
    
    private Enemy elder = null; 
    private boolean elderSpawned = false;
    private float initialBorderSize;
    private float currentBorderSize;
    private static final float BORDER_SHRINK_RATE = 50f; 
    
    
    private Array<Vector2> spawnedTreeAreas = new Array<>();
    private static final float TREE_AREA_SIZE = 800f; 
    
    
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
            float margin = 100f; 
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
        
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        
        batch = new SpriteBatch();
        
        
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        
        
        timerFont = new BitmapFont();
        timerFont.setColor(Color.RED);
        timerFont.getData().setScale(3.0f); 
        
        shapeRenderer = new ShapeRenderer();
        
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        
        gameTimeRemaining = gameController.getGameDurationMinutes() * 60f; 
        
        
        enemies = new Array<>();
        
        
        playerPosition = new Vector2(0, 0);
        playerVelocity = new Vector2();
        
        
        initialBorderSize = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        currentBorderSize = initialBorderSize;
        
        
        spawnedTreeAreas = new Array<>();
        
        
        deathEffects = new Array<>();
        
        
        initializeMagazineSystem();
        
        
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
            
            maxAmmo += player.getCharacter().getBonusMaxAmmo();
            reloadDuration = weapon.getReloadTime() != null ? weapon.getReloadTime() : 1f;
            autoReload = player.getAutoReload() != null ? player.getAutoReload() : false;
        } else {
            maxAmmo = 6; 
            reloadDuration = 1f; 
            autoReload = false;
        }
        
        currentAmmo = maxAmmo; 
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
            
            showPauseMenu();
        } else {
            System.out.println("Game resumed");
            
            hidePauseMenu();
        }
    }
    
    private void showPauseMenu() {
        pauseMenuVisible = true;
        createPauseMenu();
        
        Gdx.input.setInputProcessor(stage);
    }
    
    private void hidePauseMenu() {
        pauseMenuVisible = false;
        if (pauseMenuTable != null) {
            pauseMenuTable.remove();
            pauseMenuTable = null;
        }
        
        Gdx.input.setInputProcessor(null);
    }
    
    private void createPauseMenu() {
        if (pauseMenuTable != null) {
            pauseMenuTable.remove();
        }
        
        pauseMenuTable = new Table();
        pauseMenuTable.setFillParent(true);
        
        
        Table contentTable = new Table();
        contentTable.setBackground(GameAssetManager.getInstance().getSkin().getDrawable("window"));
        contentTable.pad(30);
        
        
        Label titleLabel = new Label("GAME PAUSED", GameAssetManager.getInstance().getSkin());
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(Color.YELLOW);
        contentTable.add(titleLabel).colspan(2).center().padBottom(30);
        contentTable.row();
        
        
        Table leftColumn = new Table();
        Table rightColumn = new Table();
        
        
        createActionButtons(leftColumn);
        
        
        createInfoPanels(rightColumn);
        
        contentTable.add(leftColumn).width(300).top().padRight(20);
        contentTable.add(rightColumn).width(400).top();
        
        pauseMenuTable.add(contentTable).center();
        stage.addActor(pauseMenuTable);
    }
    
    private void createActionButtons(Table leftColumn) {
        
        TextButton resumeButton = new TextButton("Resume Game", GameAssetManager.getInstance().getSkin());
        resumeButton.getLabel().setFontScale(1.3f);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause(); 
            }
        });
        leftColumn.add(resumeButton).width(250).height(60).padBottom(15);
        leftColumn.row();
        
        
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
        
        createAbilitiesPanel(rightColumn);
        
        
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
        
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f); 
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
    }
    
    private void giveUp() {
        
        if (!gameEnded) {
            gameEnded = true;
            endGameWithSummary(GameSummary.GameEndReason.GAVE_UP, false);
        }
    }
    
    private void loadTextures() {
        
        tileTexture = new Texture(Gdx.files.internal("textures/T_TileGrass.png"));
        
        
        idleTextures = new Array<>();
        movingTextures = new Array<>();
        reloadTextures = new Array<>();
        deathTextures = new Array<>();
        
        
        Player player = App.getInstance().getPlayer();
        
        if (player != null && player.getCharacter() != null) {
            System.out.println("Loading animations for character: " + player.getCharacter().getName());
            
            
            if (player.getCharacter().getStillImagePaths() != null && 
                !player.getCharacter().getStillImagePaths().isEmpty()) {
                
                for (String imagePath : player.getCharacter().getStillImagePaths()) {
                    if (imagePath != null && !imagePath.isEmpty()) {
                        
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
                                
                            }
                        }
                        
                        if (!loaded) {
                            System.out.println("Failed to load idle texture with any variant of: " + imagePath);
                        }
                    }
                }
            }
            
            
            if (player.getCharacter().getMovingImagePaths() != null && 
                !player.getCharacter().getMovingImagePaths().isEmpty()) {
                
                for (String imagePath : player.getCharacter().getMovingImagePaths()) {
                    if (imagePath != null && !imagePath.isEmpty()) {
                        
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
        
        
        createAnimations();
        
        
        loadWeaponTexture();
        
        
        loadBulletTexture();
        
        
        loadCursorTexture();
        
        
        loadAvatarTexture();
        
        
        loadReloadAnimations();
        
        
        loadDeathAnimations();
    }
    
    private void createAnimations() {
        
        if (idleTextures.size > 0) {
            Array<TextureRegion> idleFrames = new Array<>();
            for (Texture texture : idleTextures) {
                idleFrames.add(new TextureRegion(texture));
            }
            idleAnimation = new Animation<>(0.15f, idleFrames, Animation.PlayMode.LOOP);
            System.out.println("Created idle animation with " + idleFrames.size + " frames");
        } else {
            
            Array<TextureRegion> fallbackFrames = new Array<>();
            fallbackFrames.add(new TextureRegion(tileTexture));
            idleAnimation = new Animation<>(1f, fallbackFrames, Animation.PlayMode.LOOP);
            System.out.println("Created fallback idle animation");
        }
        
        
        if (movingTextures.size > 0) {
            Array<TextureRegion> movingFrames = new Array<>();
            for (Texture texture : movingTextures) {
                movingFrames.add(new TextureRegion(texture));
            }
            movingAnimation = new Animation<>(0.1f, movingFrames, Animation.PlayMode.LOOP);
            System.out.println("Created moving animation with " + movingFrames.size + " frames");
        } else {
            
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
        String weaponTexturePath = "textures/T_TileGrass.png"; 
        
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
        String avatarTexturePath = "textures/T_TileGrass.png"; 
        
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
                
                if (!cursorTexture.getTextureData().isPrepared()) {
                    cursorTexture.getTextureData().prepare();
                }
                Pixmap pixmap = cursorTexture.getTextureData().consumePixmap();
                
                
                Cursor cursor = Gdx.graphics.newCursor(pixmap, pixmap.getWidth()/2, pixmap.getHeight()/2);
                Gdx.graphics.setCursor(cursor);
                
                System.out.println("Custom cursor set successfully");
            } catch (Exception e) {
                System.out.println("Failed to set custom cursor: " + e.getMessage());
            }
        }
    }
    
    private void spawnInitialEnemies() {
        
        for (int i = 0; i < TREE_MONSTERS_PER_AREA * 4; i++) { 
            float angle = MathUtils.random(0f, 360f);
            float distance = MathUtils.random(ENEMY_SPAWN_DISTANCE, ENEMY_SPAWN_DISTANCE * 2f);
            
            float x = playerPosition.x + MathUtils.cosDeg(angle) * distance;
            float y = playerPosition.y + MathUtils.sinDeg(angle) * distance;
            
            
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
        
        
        Gdx.input.setInputProcessor(null); 
        
        
        if (Dawn.getEnhancedMusicService() != null) {
            Dawn.getEnhancedMusicService().switchToGameMusic();
        }
    }

    @Override
    public void render(float delta) {
        
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        
        handleInput(delta);
        
        
        if (!gamePaused) {
            
            updateTimer(delta);
            
            
            updateMagazineSystem(delta);
            
            
            gameElapsedTime += delta;
            updateTentacleSpawning(delta);
            updateEyeBatSpawning(delta);
            updateElderSpawning(delta);
            updateGlobalTreeSpawning();
            
            
            updateMouseWorldPosition();
            
            
            updatePlayer(delta);
            updateCamera();
            
            
            updateBullets(delta);
            
            
            updateEnemies(delta);
            
            
            updateDeathEffects(delta);
            
            
            updatePlayerDamageAnimation(delta);
            
            
            updatePlayerImmunity(delta);
            
            
            updateAbilities(delta);
            
            
            checkCollisions();
            
            
            animationTime += delta;
            
            
            updateWeaponRotation();
        }
        
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        
        batch.begin();
        renderTiles();
        renderEnemies();
        renderDeathEffects(); 
        renderEyeBatBullets();
        renderPlayer();
        renderWeapon();
        renderBullets(); 
        batch.end();
        
        
        if (elder != null && elder.isActive()) {
            renderBorder();
        }
        
        
        renderUI();
        
        
        if (gamePaused && pauseMenuVisible) {
            renderPauseOverlay();
        }
        
        
        stage.act(delta);
        stage.draw();
    }
    
    private void handleInput(float delta) {
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
            return; 
        }
        
        
        handleCheatCodes();
        
        
        if (gamePaused) {
            return;
        }
        
        playerVelocity.set(0, 0);
        boolean anyKeyPressed = false;
        
        
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
            facingLeft = true; 
        }
        if (Gdx.input.isKeyPressed(moveRightKey)) {
            playerVelocity.x += 1;
            anyKeyPressed = true;
            facingLeft = false; 
        }
        
        
        isMoving = anyKeyPressed;
        
        
        if (playerVelocity.len() > 0) {
            playerVelocity.nor();
        }
        
        
        float baseSpeed = 100f; 
        float speedMultiplier = 3f; 
        Player player = App.getInstance().getPlayer();
        if (player != null && player.getCharacter() != null) {
            speedMultiplier = player.getCharacter().getSpeed();
            
            
            if (player.getCharacter().hasActiveAbility(Ability.SPEEDY)) {
                speedMultiplier *= 2f;
            }
        }
        float playerSpeed = baseSpeed * speedMultiplier;
        playerVelocity.scl(playerSpeed * delta);
        
        
        if (anyKeyPressed && Gdx.graphics.getFrameId() % 180 == 0) { 
            System.out.println("Movement - Keys: W:" + moveUpKey + " S:" + moveDownKey + " A:" + moveLeftKey + " D:" + moveRightKey);
            System.out.println("Velocity: (" + playerVelocity.x + ", " + playerVelocity.y + ")");
        }
        
        
        handleShooting(delta);
        
        
        handleReload();
    }
    
    private void handleShooting(float delta) {
        
        fireTimer += delta;
        
        
        canShoot = !isReloading && currentAmmo > 0 && fireTimer >= fireRate;
        
        
        if (Gdx.input.isKeyPressed(shootKey) && canShoot) {
            shoot();
            fireTimer = 0f; 
        }
    }
    
    private void handleReload() {
        
        if (Gdx.input.isKeyJustPressed(reloadKey) && !isReloading && currentAmmo < maxAmmo) {
            startReload();
        }
    }
    
    private void handleCheatCodes() {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) return;
        
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            gameTimeRemaining = Math.max(0, gameTimeRemaining - 60f);
            gameElapsedTime += 60f; 
            System.out.println("CHEAT: Time decreased by 1 minute. Remaining: " + (int)(gameTimeRemaining / 60) + ":" + String.format("%02d", (int)(gameTimeRemaining % 60)));
            
            
            float totalGameTime = gameController.getGameDurationMinutes() * 60f;
            float elderSpawnTime = totalGameTime / 2f;
            if (!elderSpawned && gameElapsedTime >= elderSpawnTime) {
                spawnElder();
                elderSpawned = true;
                initialBorderSize = Math.max(camera.viewportWidth, camera.viewportHeight);
                currentBorderSize = initialBorderSize;
                System.out.println("CHEAT: Elder spawned due to time advancement!");
            }
            
            
            if (gameTimeRemaining <= 0 && !gameEnded) {
                gameTimeRemaining = 0;
                gameEnded = true;
                endGameWithSummary(com.example.dawn.models.GameSummary.GameEndReason.TIME_UP, true);
            }
        }
        
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            int currentLevel = player.getCharacter().getLevel();
            int newLevel = currentLevel + 1;
            
            
            int xpForNewLevel = 0;
            for (int i = 1; i < newLevel; i++) {
                xpForNewLevel += 20 * i;
            }
            player.getCharacter().setXp(xpForNewLevel);
            
            System.out.println("CHEAT: Player leveled up! Level: " + currentLevel + " -> " + newLevel + ", XP set to: " + xpForNewLevel);
        }
        
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            int currentHp = player.getCharacter().getHp();
            int maxHp = player.getCharacter().getInitialHp(); 
            
            if (currentHp < maxHp) {
                player.getCharacter().setHp(currentHp + 1);
                System.out.println("CHEAT: Health increased! HP: " + currentHp + " -> " + (currentHp + 1));
            } else {
                System.out.println("CHEAT: Health already at maximum (" + maxHp + ")");
            }
        }
        
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            if (!elderSpawned) {
                spawnElder();
                elderSpawned = true;
                
                
                initialBorderSize = Math.max(camera.viewportWidth, camera.viewportHeight);
                currentBorderSize = initialBorderSize;
                
                System.out.println("CHEAT: Elder boss summoned! Border system activated.");
            } else {
                System.out.println("CHEAT: Elder boss already spawned!");
            }
        }
        
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            int enemiesKilled = 0;
            for (Enemy enemy : enemies) {
                if (enemy.isActive() && !(enemy instanceof TreeMonster)) { 
                    
                    DeathEffect deathEffect = new DeathEffect(enemy.getPosition().x, enemy.getPosition().y);
                    deathEffects.add(deathEffect);
                    
                    
                    int scoreGained = getEnemyInitialHp(enemy);
                    int xpGained = scoreGained / 5;
                    
                    
                    int currentKills = player.getCharacter().getKills();
                    int currentScore = player.getCharacter().getScore();
                    player.getCharacter().setKills(currentKills + 1);
                    player.getCharacter().setScore(currentScore + scoreGained);
                    player.getCharacter().addXp(xpGained);
                    
                    
                    enemy.setActive(false);
                    enemiesKilled++;
                }
            }
            
            
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
            
            
            if (reloadTimer >= reloadDuration) {
                completeReload();
            }
        }
        
        
        if (autoReload && currentAmmo <= 0 && !isReloading) {
            startReload();
        }
    }
    
    private void startReload() {
        if (isReloading) return; 
        
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
        
        if (isReloading || currentAmmo <= 0) {
            return;
        }
        
        
        Player player = App.getInstance().getPlayer();
        int weaponDamage = 1; 
        int projectileCount = 1; 
        
        if (player != null && player.getCharacter() != null && player.getCharacter().getWeapon() != null) {
            Weapon weapon = player.getCharacter().getWeapon();
            weaponDamage = weapon.getDamage() != null ? weapon.getDamage() : 1;
            projectileCount = weapon.getProjectile() != null ? weapon.getProjectile() : 1;
            
            
            if (player.getCharacter().hasActiveAbility(Ability.DAMAGER)) {
                weaponDamage = (int) Math.ceil(weaponDamage * 1.25f);
            }
            
            
            projectileCount += player.getCharacter().getBonusProjectiles();
        }
        
        
        float weaponOffsetX = facingLeft ? -20f : 20f;
        float weaponOffsetY = 5f;
        float bulletStartX = playerPosition.x + weaponOffsetX;
        float bulletStartY = playerPosition.y + weaponOffsetY;
        
        
        float deltaX = mouseWorldPosition.x - bulletStartX;
        float deltaY = mouseWorldPosition.y - bulletStartY;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 0) {
            
            float baseDirX = deltaX / distance;
            float baseDirY = deltaY / distance;
            
            
            for (int i = 0; i < projectileCount; i++) {
                
                float spreadAngle = 0f;
                if (projectileCount > 1) {
                    
                    float maxSpread = 15f; 
                    spreadAngle = (i - (projectileCount - 1) / 2f) * (maxSpread / Math.max(1, projectileCount - 1));
                }
                
                
                float radians = (float) Math.toRadians(spreadAngle);
                float cos = (float) Math.cos(radians);
                float sin = (float) Math.sin(radians);
                
                float spreadDirX = baseDirX * cos - baseDirY * sin;
                float spreadDirY = baseDirX * sin + baseDirY * cos;
                
                
                float bulletSpeed = 500f; 
                float velocityX = spreadDirX * bulletSpeed;
                float velocityY = spreadDirY * bulletSpeed;
                
                
                Bullet bullet = new Bullet(bulletStartX, bulletStartY, velocityX, velocityY, weaponRotation, weaponDamage);
                bullets.add(bullet);
            }
            
            
            currentAmmo--;
            
            System.out.println("Shot fired! Projectiles: " + projectileCount + ", Damage: " + weaponDamage + ", Ammo: " + currentAmmo + "/" + maxAmmo);
        }
    }
    
    private void updatePlayer(float delta) {
        
        playerPosition.add(playerVelocity);
    }
    
    private void updateCamera() {
        
        Vector3 cameraCenter = new Vector3(camera.position);
        float playerRelativeX = playerPosition.x - cameraCenter.x;
        float playerRelativeY = playerPosition.y - cameraCenter.y;
        
        
        float newCameraX = camera.position.x;
        float newCameraY = camera.position.y;
        
        
        if (playerRelativeX > CAMERA_FOLLOW_AREA / 2) {
            newCameraX = playerPosition.x - CAMERA_FOLLOW_AREA / 2;
        } else if (playerRelativeX < -CAMERA_FOLLOW_AREA / 2) {
            newCameraX = playerPosition.x + CAMERA_FOLLOW_AREA / 2;
        }
        
        
        if (playerRelativeY > CAMERA_FOLLOW_AREA / 2) {
            newCameraY = playerPosition.y - CAMERA_FOLLOW_AREA / 2;
        } else if (playerRelativeY < -CAMERA_FOLLOW_AREA / 2) {
            newCameraY = playerPosition.y + CAMERA_FOLLOW_AREA / 2;
        }
        
        
        camera.position.set(newCameraX, newCameraY, 0);
    }
    
    private void renderTiles() {
        
        float cameraLeft = camera.position.x - camera.viewportWidth / 2;
        float cameraRight = camera.position.x + camera.viewportWidth / 2;
        float cameraBottom = camera.position.y - camera.viewportHeight / 2;
        float cameraTop = camera.position.y + camera.viewportHeight / 2;
        
        
        int startTileX = (int) Math.floor(cameraLeft / TILE_SIZE) - 1;
        int endTileX = (int) Math.ceil(cameraRight / TILE_SIZE) + 1;
        int startTileY = (int) Math.floor(cameraBottom / TILE_SIZE) - 1;
        int endTileY = (int) Math.ceil(cameraTop / TILE_SIZE) + 1;
        
        
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
        
        Animation<TextureRegion> currentAnimation = isMoving ? movingAnimation : idleAnimation;
        
        
        TextureRegion currentFrame = currentAnimation.getKeyFrame(animationTime);
        
        
        float playerSize = 96f; 
        float playerRenderX = playerPosition.x - playerSize / 2f;
        float playerRenderY = playerPosition.y - playerSize / 2f;
        
        
        if (facingLeft && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!facingLeft && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
        
        
        if (playerTookDamage) {
            batch.setColor(1f, 0.3f, 0.3f, 1f); 
        } else if (playerImmune) {
            
            float flickerSpeed = 10f; 
            float alpha = (float)(0.5f + 0.5f * Math.sin(immunityTimer * flickerSpeed));
            batch.setColor(1f, 1f, 1f, alpha); 
        }
        
        
        batch.draw(currentFrame, playerRenderX, playerRenderY, playerSize, playerSize);
        
        
        if (playerTookDamage || playerImmune) {
            batch.setColor(Color.WHITE);
        }
        
        
        if (Gdx.graphics.getFrameId() % 300 == 0) { 
            System.out.println("Player: (" + (int)playerPosition.x + ", " + (int)playerPosition.y + ") " + (isMoving ? "moving" : "idle"));
        }
    }
    
    private void renderWeapon() {
        if (weaponTexture == null) return;
        
        
        float weaponSize = 48f; 
        float weaponOffsetX = facingLeft ? -20f : 20f; 
        float weaponOffsetY = 5f; 
        
        
        float weaponX = playerPosition.x + weaponOffsetX;
        float weaponY = playerPosition.y + weaponOffsetY;
        
        
        TextureRegion weaponRegion;
        if (isReloading && reloadAnimation != null) {
            
            weaponRegion = reloadAnimation.getKeyFrame(reloadAnimationTime);
        } else {
            
            weaponRegion = new TextureRegion(weaponTexture);
        }
        
        
        
        batch.draw(weaponRegion, 
                   weaponX - weaponSize/2f, weaponY - weaponSize/2f, 
                   weaponSize/2f, weaponSize/2f, 
                   weaponSize, weaponSize, 
                   1f, 1f, 
                   weaponRotation); 
    }
    
    private void renderBullets() {
        for (Bullet bullet : bullets) {
            if (bullet.active) {
                float bulletSize = 16f; 
                float bulletX = bullet.position.x - bulletSize/2f;
                float bulletY = bullet.position.y - bulletSize/2f;
                batch.draw(bulletTexture, bulletX, bulletY, bulletSize, bulletSize);
            }
        }
        
        
        if (Gdx.graphics.getFrameId() % 60 == 0 && bullets.size > 0) { 
            System.out.println("Active bullets: " + bullets.size);
        }
    }
    
    private void renderEyeBatBullets() {
        for (Enemy enemy : enemies) {
            if (enemy instanceof EyeBat && enemy.isActive()) {
                EyeBat eyebat = (EyeBat) enemy;
                for (EyeBat.EyeBatBullet bullet : eyebat.getBullets()) {
                    if (bullet.active) {
                        float bulletSize = 12f; 
                        float bulletX = bullet.position.x - bulletSize/2f;
                        float bulletY = bullet.position.y - bulletSize/2f;
                        batch.draw(bulletTexture, bulletX, bulletY, bulletSize, bulletSize);
                    }
                }
            }
        }
    }
    
    private void renderBorder() {
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        
        float borderCenterX = camera.position.x;
        float borderCenterY = camera.position.y;
        float borderRadius = currentBorderSize / 2f;
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(borderCenterX, borderCenterY, borderRadius, 64); 
        shapeRenderer.end();
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(borderCenterX, borderCenterY, borderRadius - 20f, 32);
        shapeRenderer.end();
    }
    
    private void updateMouseWorldPosition() {
        
        Vector3 mouseScreenPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 mouseWorldPos = camera.unproject(mouseScreenPos);
        mouseWorldPosition.set(mouseWorldPos.x, mouseWorldPos.y);
    }
    
    private void updateWeaponRotation() {
        
        float deltaX = mouseWorldPosition.x - playerPosition.x;
        float deltaY = mouseWorldPosition.y - playerPosition.y;
        weaponRotation = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }
    
    private void updateBullets(float delta) {
        
        for (Bullet bullet : bullets) {
            if (bullet.active) {
                bullet.update(delta);
                
                
                if (bullet.isOffScreen(camera)) {
                    bullet.active = false;
                }
            }
        }
        
        
        for (int i = bullets.size - 1; i >= 0; i--) {
            if (!bullets.get(i).active) {
                bullets.removeIndex(i);
            }
        }
    }
    
    private void updateTimer(float delta) {
        if (!gameEnded) {
            gameTimeRemaining -= delta;
            
            
            if (gameTimeRemaining <= 0) {
                gameTimeRemaining = 0;
                gameEnded = true;
                
                endGameWithSummary(com.example.dawn.models.GameSummary.GameEndReason.TIME_UP, true);
            }
        }
    }
    
    private void updateTentacleSpawning(float delta) {
        tentacleSpawnTimer += delta;
        
        
        if (tentacleSpawnTimer >= TENTACLE_SPAWN_INTERVAL) {
            tentacleSpawnTimer = 0f; 
            
            
            
            int tentaclesToSpawn = Math.max(1, (int)(gameElapsedTime / 30f));
            
            for (int i = 0; i < tentaclesToSpawn; i++) {
                spawnTentacleOnScreen();
            }
            
            System.out.println("Spawned " + tentaclesToSpawn + " tentacle monsters at " + gameElapsedTime + " seconds");
        }
    }
    
    private void spawnTentacleOnScreen() {
        
        float screenWidth = camera.viewportWidth;
        float screenHeight = camera.viewportHeight;
        
        
        float x = camera.position.x + MathUtils.random(-screenWidth/2f, screenWidth/2f);
        float y = camera.position.y + MathUtils.random(-screenHeight/2f, screenHeight/2f);
        
        
        float distanceFromPlayer = playerPosition.dst(x, y);
        if (distanceFromPlayer < ENEMY_SPAWN_DISTANCE) {
            
            float angle = MathUtils.atan2(y - playerPosition.y, x - playerPosition.x);
            x = playerPosition.x + MathUtils.cos(angle) * ENEMY_SPAWN_DISTANCE;
            y = playerPosition.y + MathUtils.sin(angle) * ENEMY_SPAWN_DISTANCE;
        }
        
        
        x = Math.round(x / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
        y = Math.round(y / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
        
        TentacleMonster tentacle = new TentacleMonster(x, y);
        enemies.add(tentacle);
        
        System.out.println("Spawned tentacle monster at (" + x + ", " + y + ")");
    }
    
    private void updateEyeBatSpawning(float delta) {
        
        float totalGameTime = gameController.getGameDurationMinutes() * 60f; 
        float spawnStartTime = totalGameTime / 4f; 
        
        if (!eyebatSpawningStarted && gameElapsedTime >= spawnStartTime) {
            eyebatSpawningStarted = true;
            System.out.println("EyeBat spawning started at " + gameElapsedTime + " seconds (spawn start time: " + spawnStartTime + ")");
        }
        
        if (!eyebatSpawningStarted) {
            return;
        }
        
        eyebatSpawnTimer += delta;
        
        
        if (eyebatSpawnTimer >= EYEBAT_SPAWN_INTERVAL) {
            eyebatSpawnTimer = 0f; 
            
            
            
            int intervals = (int)(gameElapsedTime / EYEBAT_SPAWN_INTERVAL);
            int formulaResult = (4 * intervals - (int)gameElapsedTime + 30) / 30;
            int eyebatsToSpawn = Math.max(1, formulaResult); 
            
            System.out.println("EyeBat spawn calculation: intervals=" + intervals + ", gameElapsedTime=" + (int)gameElapsedTime + ", formula=" + formulaResult + ", spawning=" + eyebatsToSpawn);
            
            for (int i = 0; i < eyebatsToSpawn; i++) {
                spawnEyeBatOnScreen();
            }
            
            System.out.println("Spawned " + eyebatsToSpawn + " eyebats at " + gameElapsedTime + " seconds");
        }
    }
    
    private void spawnEyeBatOnScreen() {
        
        float screenWidth = camera.viewportWidth;
        float screenHeight = camera.viewportHeight;
        
        
        float x = camera.position.x + MathUtils.random(-screenWidth/2f, screenWidth/2f);
        float y = camera.position.y + MathUtils.random(-screenHeight/2f, screenHeight/2f);
        
        
        float distanceFromPlayer = playerPosition.dst(x, y);
        if (distanceFromPlayer < ENEMY_SPAWN_DISTANCE) {
            
            float angle = MathUtils.atan2(y - playerPosition.y, x - playerPosition.x);
            x = playerPosition.x + MathUtils.cos(angle) * ENEMY_SPAWN_DISTANCE;
            y = playerPosition.y + MathUtils.sin(angle) * ENEMY_SPAWN_DISTANCE;
        }
        
        
        x = Math.round(x / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
        y = Math.round(y / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
        
        EyeBat eyebat = new EyeBat(x, y);
        enemies.add(eyebat);
        
        System.out.println("Spawned eyebat at (" + x + ", " + y + ")");
    }
    
    private void updateGlobalTreeSpawning() {
        
        float areaX = Math.round(playerPosition.x / TREE_AREA_SIZE) * TREE_AREA_SIZE;
        float areaY = Math.round(playerPosition.y / TREE_AREA_SIZE) * TREE_AREA_SIZE;
        Vector2 currentArea = new Vector2(areaX, areaY);
        
        
        boolean areaAlreadySpawned = false;
        for (Vector2 spawnedArea : spawnedTreeAreas) {
            if (spawnedArea.equals(currentArea)) {
                areaAlreadySpawned = true;
                break;
            }
        }
        
        
        if (!areaAlreadySpawned) {
            spawnTreesInArea(areaX, areaY);
            spawnedTreeAreas.add(new Vector2(areaX, areaY));
            System.out.println("Spawned trees in new area: (" + areaX + ", " + areaY + ")");
        }
    }
    
    private void spawnTreesInArea(float centerX, float centerY) {
        
        int treesToSpawn = TREE_MONSTERS_PER_AREA * 4; 
        
        for (int i = 0; i < treesToSpawn; i++) {
            
            float x = centerX + MathUtils.random(-TREE_AREA_SIZE/2f, TREE_AREA_SIZE/2f);
            float y = centerY + MathUtils.random(-TREE_AREA_SIZE/2f, TREE_AREA_SIZE/2f);
            
            
            float distanceFromPlayer = playerPosition.dst(x, y);
            if (distanceFromPlayer < ENEMY_SPAWN_DISTANCE) {
                
                continue;
            }
            
            
            x = Math.round(x / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
            y = Math.round(y / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2f;
            
            TreeMonster treeMonster = new TreeMonster(x, y);
            enemies.add(treeMonster);
        }
    }
    
    private void updateElderSpawning(float delta) {
        
        float totalGameTime = gameController.getGameDurationMinutes() * 60f; 
        float elderSpawnTime = totalGameTime / 2f; 
        
        if (!elderSpawned && gameElapsedTime >= elderSpawnTime) {
            spawnElder();
            elderSpawned = true;
            
            
            initialBorderSize = Math.max(camera.viewportWidth, camera.viewportHeight);
            currentBorderSize = initialBorderSize;
            
            System.out.println("Elder spawned at " + gameElapsedTime + " seconds! Border system activated.");
        }
        
        
        if (elder != null && elder.isActive()) {
            currentBorderSize -= BORDER_SHRINK_RATE * delta;
            currentBorderSize = Math.max(currentBorderSize, 100f); 
            
            
            checkBorderCollision();
        }
    }
    
    private void spawnElder() {
        
        float angle = MathUtils.random(0f, 360f);
        float distance = ENEMY_SPAWN_DISTANCE * 1.5f; 
        
        float x = playerPosition.x + MathUtils.cosDeg(angle) * distance;
        float y = playerPosition.y + MathUtils.sinDeg(angle) * distance;
        
        
        try {
            Class<?> elderClass = Class.forName("com.example.dawn.entity.Elder");
            elder = (Enemy) elderClass.getConstructor(float.class, float.class).newInstance(x, y);
            enemies.add(elder);
            System.out.println("Elder boss spawned at (" + x + ", " + y + ")");
        } catch (Exception e) {
            System.out.println("Failed to spawn Elder: " + e.getMessage());
            
            elder = new TreeMonster(x, y);
            enemies.add(elder);
        }
    }
    
    private void checkBorderCollision() {
        
        float borderCenterX = camera.position.x;
        float borderCenterY = camera.position.y;
        float distanceFromCenter = playerPosition.dst(borderCenterX, borderCenterY);
        
        
        if (distanceFromCenter > currentBorderSize / 2f) {
            
            if (!playerImmune) {
                Player player = App.getInstance().getPlayer();
                if (player != null && player.getCharacter() != null) {
                    int currentHp = player.getCharacter().getHp();
                    int newHp = Math.max(0, currentHp - 1);
                    player.getCharacter().setHp(newHp);
                    System.out.println("Player hit border! HP: " + currentHp + " -> " + newHp);
                    
                    
                    playerImmune = true;
                    immunityTimer = 0f;
                    playerTookDamage = true;
                    damageAnimationTimer = 0f;
                    System.out.println("Player took border damage - immunity activated for " + IMMUNITY_DURATION + " seconds");
                }
            }
            
            
            float pushBackDistance = (currentBorderSize / 2f) - 10f; 
            float angle = MathUtils.atan2(playerPosition.y - borderCenterY, playerPosition.x - borderCenterX);
            playerPosition.x = borderCenterX + MathUtils.cos(angle) * pushBackDistance;
            playerPosition.y = borderCenterY + MathUtils.sin(angle) * pushBackDistance;
        }
    }
    
    private void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                
                if (enemy instanceof TentacleMonster) {
                    ((TentacleMonster) enemy).setTargetPosition(playerPosition);
                }
                
                if (enemy instanceof EyeBat) {
                    ((EyeBat) enemy).setTargetPosition(playerPosition);
                }
                
                if (enemy == elder && elder != null) {
                    try {
                        
                        elder.getClass().getMethod("setTargetPosition", Vector2.class).invoke(elder, playerPosition);
                    } catch (Exception e) {
                        
                    }
                }
                enemy.update(delta);
            }
        }
        
        
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (!enemies.get(i).isActive()) {
                enemies.removeIndex(i);
            }
        }
    }
    
    private void checkCollisions() {
        
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && !playerImmune && enemy.collidesWith(playerPosition, PLAYER_COLLISION_RADIUS)) {
                enemy.onPlayerCollision();
                
                playerTookDamage = true;
                damageAnimationTimer = 0f;
                playerImmune = true;
                immunityTimer = 0f;
                System.out.println("Player took damage - immunity activated for " + IMMUNITY_DURATION + " seconds");
                
                
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
        
        
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            if (!bullet.active) continue;
            
            for (Enemy enemy : enemies) {
                if (enemy.isActive() && enemy.collidesWith(bullet.position, 8f)) { 
                    int enemyHpBefore = enemy.getHp();
                    
                    
                    applyKnockback(enemy, bullet.position, bullet.damage);
                    
                    enemy.takeDamage(bullet.damage); 
                    bullet.active = false; 
                    
                    
                    if (!enemy.isActive() && enemyHpBefore > 0) {
                        
                        DeathEffect deathEffect = new DeathEffect(enemy.getPosition().x, enemy.getPosition().y);
                        deathEffects.add(deathEffect);
                        
                        
                        int scoreGained = getEnemyInitialHp(enemy);
                        int xpGained = scoreGained / 5; 
                        
                        
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
                    
                    break; 
                }
            }
        }
        
        
        for (Enemy enemy : enemies) {
            if (enemy instanceof EyeBat && enemy.isActive()) {
                EyeBat eyebat = (EyeBat) enemy;
                for (int i = eyebat.getBullets().size - 1; i >= 0; i--) {
                    EyeBat.EyeBatBullet bullet = eyebat.getBullets().get(i);
                    if (bullet.active) {
                        float distance = playerPosition.dst(bullet.position);
                        if (distance < PLAYER_COLLISION_RADIUS + 6f && !playerImmune) { 
                            
                            Player player = App.getInstance().getPlayer();
                            if (player != null && player.getCharacter() != null) {
                                int currentHp = player.getCharacter().getHp();
                                int newHp = Math.max(0, currentHp - 1); 
                                player.getCharacter().setHp(newHp);
                                System.out.println("Player hit by EyeBat bullet! HP: " + currentHp + " -> " + newHp);
                                
                                
                                playerTookDamage = true;
                                damageAnimationTimer = 0f;
                                playerImmune = true;
                                immunityTimer = 0f;
                                System.out.println("Player took damage - immunity activated for " + IMMUNITY_DURATION + " seconds");
                                
                                if (newHp <= 0) {
                                    System.out.println("Player died from EyeBat bullet!");
                                    if (!gameEnded) {
                                        gameEnded = true;
                                        endGameWithSummary(GameSummary.GameEndReason.PLAYER_DIED, false);
                                    }
                                }
                            }
                            bullet.active = false; 
                        }
                        
                        
                        if (bullet.isOffScreen(camera.position.x, camera.position.y, camera.viewportWidth, camera.viewportHeight)) {
                            bullet.active = false;
                        }
                    }
                }
            }
        }
    }
    
    private int getEnemyInitialHp(Enemy enemy) {
        
        if (enemy instanceof TentacleMonster) {
            return 25; 
        } else if (enemy instanceof EyeBat) {
            return 50; 
        } else if (enemy instanceof Elder) {
            return 400; 
        } else if (enemy instanceof TreeMonster) {
            return 1; 
        }
        return 10; 
    }
    
    private void endGameWithSummary(GameSummary.GameEndReason reason, boolean isWin) {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) {
            System.out.println("No player found, returning to main menu");
            gameController.returnToMainMenu();
            return;
        }
        
        
        int currentKills = player.getCharacter().getKills();
        int currentScore = player.getCharacter().getScore();
        int currentXp = player.getCharacter().getXp();
        
        int killsGained = currentKills - gameStartKills;
        int scoreGained = currentScore - gameStartScore;
        int xpGained = currentXp - gameStartXp;
        
        
        float totalGameTime = gameController.getGameDurationMinutes() * 60f;
        int survivalTimeSeconds = (int)(totalGameTime - gameTimeRemaining);
        
        
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
        
        
        updatePlayerStatistics(player, killsGained, scoreGained, survivalTimeSeconds, isWin);
        
        
        gameController.getDatabaseManager().savePlayer(player);
        
        
        GameSummaryController summaryController = new GameSummaryController(
            gameController.getDatabaseManager(), 
            gameController.getGameDurationMinutes()
        );
        Dawn.getInstance().setScreen(new GameSummaryScreen(summaryController, summary, GameAssetManager.getInstance().getSkin()));
    }
    
    private void updatePlayerStatistics(Player player, int killsGained, int scoreGained, int survivalTime, boolean isWin) {
        
        player.addTotalGames();
        
        
        if (isWin) {
            player.addWin();
        }
        
        
        player.addKills(killsGained);
        
        
        if (player.getCharacter().getScore() > player.getHighScore()) {
            player.setHighScore(player.getCharacter().getScore());
        }
        
        
        player.addSurvivalDuration(survivalTime);
        
        System.out.println("Player statistics updated - Total games: " + player.getTotalGames() + 
                          ", Wins: " + player.getWins() + ", Total kills: " + player.getKills() + 
                          ", High score: " + player.getHighScore() + ", Total survival: " + player.getSurvivalDuration());
    }
    
    private void renderUI() {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) return;
        
        
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        
        
        float boxX = 20f;
        float boxY = Gdx.graphics.getHeight() - 200f;
        float boxWidth = 300f;
        float boxHeight = 180f;
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); 
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();
        
        
        batch.begin();
        
        
        float avatarSize = 60f;
        float avatarX = boxX + 10f;
        float avatarY = boxY + boxHeight - avatarSize - 10f;
        batch.draw(avatarTexture, avatarX, avatarY, avatarSize, avatarSize);
        
        
        float textX = avatarX + avatarSize + 10f;
        float textY = boxY + boxHeight - 20f;
        float lineHeight = 18f;
        
        
        font.draw(batch, "Player: " + player.getUsername(), textX, textY);
        textY -= lineHeight;
        font.draw(batch, "Character: " + player.getCharacter().getName(), textX, textY);
        textY -= lineHeight;
        
        
        font.draw(batch, "HP: " + player.getCharacter().getHp(), textX, textY);
        textY -= lineHeight;
        
        
        int sessionScore = player.getCharacter().getScore() - gameStartScore;
        font.draw(batch, "Score: " + sessionScore, textX, textY);
        textY -= lineHeight;
        
        
        int sessionKills = player.getCharacter().getKills() - gameStartKills;
        font.draw(batch, "Kills: " + sessionKills, textX, textY);
        textY -= lineHeight;
        
        
        int currentLevel = player.getCharacter().getLevel();
        font.draw(batch, "Level: " + currentLevel, textX, textY);
        textY -= lineHeight + 5f;
        
        batch.end();
        
        
        float barX = textX;
        float barY = textY - 15f;
        float barWidth = 150f;
        float barHeight = 12f;
        
        
        int currentXp = player.getCharacter().getXp();
        int xpForCurrentLevel = 0;
        int xpForNextLevel = 0;
        
        
        for (int i = 1; i <= currentLevel; i++) {
            xpForCurrentLevel += 20 * (i - 1);
        }
        xpForNextLevel = xpForCurrentLevel + (20 * currentLevel);
        
        
        int xpInCurrentLevel = currentXp - xpForCurrentLevel;
        int xpNeededForNextLevel = 20 * currentLevel;
        
        
        xpInCurrentLevel = Math.min(xpInCurrentLevel, xpNeededForNextLevel);
        float xpProgress = xpNeededForNextLevel > 0 ? (float) xpInCurrentLevel / xpNeededForNextLevel : 0f;
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f); 
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.end();
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f); 
        shapeRenderer.rect(barX, barY, barWidth * xpProgress, barHeight);
        shapeRenderer.end();
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.end();
        
        
        batch.begin();
        font.draw(batch, xpInCurrentLevel + "/" + xpNeededForNextLevel + " XP", barX + 5f, barY + barHeight - 2f);
        batch.end();
        
        
        renderTimer();
        
        
        renderBulletIcons();
        
        
        renderActiveAbilities();
    }
    
    private void renderTimer() {
        
        int totalSeconds = (int) Math.ceil(gameTimeRemaining);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        
        
        String timerText = String.format("%02d:%02d", minutes, seconds);
        
        
        float timerX = Gdx.graphics.getWidth() - 200f; 
        float timerY = Gdx.graphics.getHeight() - 30f; 
        
        
        if (gameTimeRemaining <= 60f) {
            timerFont.setColor(Color.RED); 
        } else if (gameTimeRemaining <= 180f) {
            timerFont.setColor(Color.ORANGE); 
        } else {
            timerFont.setColor(Color.WHITE); 
        }
        
        
        float textWidth = 180f;
        float textHeight = 60f;
        float boxX = timerX - 10f;
        float boxY = timerY - textHeight + 10f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); 
        shapeRenderer.rect(boxX, boxY, textWidth, textHeight);
        shapeRenderer.end();
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(boxX, boxY, textWidth, textHeight);
        shapeRenderer.end();
        
        
        batch.begin();
        timerFont.draw(batch, timerText, timerX, timerY);
        batch.end();
    }
    
    private void renderBulletIcons() {
        
        float iconSize = 18f; 
        float iconSpacing = 22f; 
        float startX = Gdx.graphics.getWidth() - 300f; 
        float startY = Gdx.graphics.getHeight() - 120f; 
        
        
        int bulletsPerRow = Math.min(maxAmmo, 8);
        int rows = (int) Math.ceil((float) maxAmmo / bulletsPerRow);
        
        
        float backgroundWidth = bulletsPerRow * iconSpacing + 30f; 
        float backgroundHeight = rows * (iconSize + 8f) + 30f; 
        float backgroundX = startX - 15f;
        float backgroundY = startY - backgroundHeight + 15f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); 
        shapeRenderer.rect(backgroundX, backgroundY, backgroundWidth, backgroundHeight);
        shapeRenderer.end();
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(backgroundX, backgroundY, backgroundWidth, backgroundHeight);
        shapeRenderer.end();
        
        
        batch.begin();
        for (int i = 0; i < maxAmmo; i++) {
            int row = i / bulletsPerRow;
            int col = i % bulletsPerRow;
            
            float iconX = startX + col * iconSpacing;
            float iconY = startY - row * (iconSize + 8f);
            
            
            Color iconColor;
            if (i < currentAmmo) {
                iconColor = Color.YELLOW; 
            } else {
                iconColor = Color.GRAY; 
            }
            
            
            batch.setColor(iconColor);
            batch.draw(bulletTexture, iconX, iconY, iconSize, iconSize);
        }
        
        
        batch.setColor(Color.WHITE);
        
        
        if (isReloading) {
            font.setColor(Color.ORANGE);
            float reloadProgress = (reloadTimer / reloadDuration) * 100f;
            String reloadText = "Reloading... " + (int)reloadProgress + "%";
            font.draw(batch, reloadText, startX, startY - backgroundHeight - 10f);
            font.setColor(Color.WHITE); 
        }
        
        batch.end();
    }
    
    private void renderActiveAbilities() {
        Player player = App.getInstance().getPlayer();
        if (player == null || player.getCharacter() == null) return;
        
        List<com.example.dawn.models.ActiveAbility> activeAbilities = player.getCharacter().getActiveAbilities();
        if (activeAbilities.isEmpty()) return;
        
        
        float startX = 20f;
        float startY = Gdx.graphics.getHeight() - 250f; 
        float abilityHeight = 30f;
        float abilitySpacing = 35f;
        
        
        float backgroundWidth = 250f;
        float backgroundHeight = activeAbilities.size() * abilitySpacing + 20f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); 
        shapeRenderer.rect(startX, startY - backgroundHeight, backgroundWidth, backgroundHeight);
        shapeRenderer.end();
        
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW); 
        shapeRenderer.rect(startX, startY - backgroundHeight, backgroundWidth, backgroundHeight);
        shapeRenderer.end();
        
        
        batch.begin();
        font.setColor(Color.YELLOW);
        
        for (int i = 0; i < activeAbilities.size(); i++) {
            com.example.dawn.models.ActiveAbility activeAbility = activeAbilities.get(i);
            float textY = startY - (i * abilitySpacing) - 15f;
            
            String abilityText = activeAbility.getAbility().getName() + " (" + activeAbility.getRemainingSeconds() + "s)";
            font.draw(batch, abilityText, startX + 10f, textY);
        }
        
        font.setColor(Color.WHITE); 
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
        
        
        if (reloadTextures.size > 0) {
            Array<TextureRegion> reloadFrames = new Array<>();
            for (Texture texture : reloadTextures) {
                reloadFrames.add(new TextureRegion(texture));
            }
            
            float frameTime = reloadDuration / reloadFrames.size;
            reloadAnimation = new Animation<>(frameTime, reloadFrames, Animation.PlayMode.NORMAL);
            System.out.println("Created reload animation with " + reloadFrames.size + " frames, frame time: " + frameTime + "s");
        } else {
            
            reloadAnimation = null;
            System.out.println("No reload animation textures found");
        }
    }
    
    private void loadDeathAnimations() {
        
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
        
        
        if (deathTextures.size > 0) {
            Array<TextureRegion> deathFrames = new Array<>();
            for (Texture texture : deathTextures) {
                deathFrames.add(new TextureRegion(texture));
            }
            
            float frameTime = 0.8f / deathFrames.size;
            deathAnimation = new Animation<>(frameTime, deathFrames, Animation.PlayMode.NORMAL);
            System.out.println("Created death animation with " + deathFrames.size + " frames, frame time: " + frameTime + "s");
        } else {
            deathAnimation = null;
            System.out.println("No death animation textures found");
        }
    }
    
    private void updateDeathEffects(float delta) {
        
        for (DeathEffect effect : deathEffects) {
            if (effect.active) {
                effect.update(delta);
                
                
                if (deathAnimation != null && effect.isFinished(deathAnimation.getAnimationDuration())) {
                    effect.active = false;
                }
            }
        }
        
        
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
        
        
        player.getCharacter().updateActiveAbilities(delta);
        
        
        int currentLevel = player.getCharacter().getLevel();
        if (currentLevel > lastKnownLevel) {
            
            for (int level = lastKnownLevel + 1; level <= currentLevel; level++) {
                grantRandomAbility(player.getCharacter());
            }
            lastKnownLevel = currentLevel;
        }
    }
    
    private void grantRandomAbility(com.example.dawn.models.Character character) {
        
        Ability[] abilities = Ability.values();
        Ability randomAbility = abilities[MathUtils.random(abilities.length - 1)];
        
        System.out.println("LEVEL UP! Granted ability: " + randomAbility.getName() + " - " + randomAbility.getDescription());
        
        
        switch (randomAbility) {
            case VITALITY:
                character.addPermanentAbility(randomAbility);
                
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
                
                maxAmmo += 5;
                currentAmmo = Math.min(currentAmmo + 5, maxAmmo); 
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
                float effectSize = 64f; 
                float renderX = effect.position.x - effectSize / 2f;
                float renderY = effect.position.y - effectSize / 2f;
                
                batch.draw(currentFrame, renderX, renderY, effectSize, effectSize);
            }
        }
    }
    
    private void updatePlayerImmunity(float delta) {
        if (playerImmune) {
            immunityTimer += delta;
            if (immunityTimer >= IMMUNITY_DURATION) {
                playerImmune = false;
                immunityTimer = 0f;
                System.out.println("Player immunity ended");
            }
        }
    }
    
    private void applyKnockback(Enemy enemy, Vector2 bulletPosition, int damage) {
        
        if (enemy instanceof TreeMonster) {
            return;
        }
        
        
        Vector2 enemyPos = enemy.getPosition();
        float deltaX = enemyPos.x - bulletPosition.x;
        float deltaY = enemyPos.y - bulletPosition.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 0) {
            
            deltaX /= distance;
            deltaY /= distance;
            
            
            float knockbackStrength = 8f + (damage * 3f); 
            enemyPos.x += deltaX * knockbackStrength;
            enemyPos.y += deltaY * knockbackStrength;
            
            System.out.println("Applied knockback to enemy: " + knockbackStrength + " units");
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
        stage.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        
        font.dispose();
        timerFont.dispose();
        
        
        if (tileTexture != null) tileTexture.dispose();
        if (weaponTexture != null) weaponTexture.dispose();
        if (bulletTexture != null) bulletTexture.dispose();
        if (cursorTexture != null) cursorTexture.dispose();
        if (avatarTexture != null) avatarTexture.dispose();
        
        
        for (Texture texture : idleTextures) {
            texture.dispose();
        }
        for (Texture texture : movingTextures) {
            texture.dispose();
        }
        for (Texture texture : reloadTextures) {
            texture.dispose();
        }
        for (Texture texture : deathTextures) {
            texture.dispose();
        }
    }
} 