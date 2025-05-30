package com.example.dawn.controller;

import com.badlogic.gdx.Input;
import com.example.dawn.Dawn;
import com.example.dawn.models.App;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.models.Player;
import com.example.dawn.service.PlayerControlsService;
import com.example.dawn.view.Settings;

public class ControlsMenuController extends Controller {
    
    private final DatabaseManager databaseManager;
    private PlayerControlsService playerControlsService;
    
    
    public static final int DEFAULT_MOVE_UP = Input.Keys.W;
    public static final int DEFAULT_MOVE_DOWN = Input.Keys.S;
    public static final int DEFAULT_MOVE_LEFT = Input.Keys.A;
    public static final int DEFAULT_MOVE_RIGHT = Input.Keys.D;
    public static final int DEFAULT_RELOAD = Input.Keys.R;
    public static final int DEFAULT_SHOOT = Input.Keys.SPACE;
    public static final int DEFAULT_SPRINT = Input.Keys.SHIFT_LEFT;
    
    public ControlsMenuController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        
        try {
            this.playerControlsService = Dawn.getInstance().getPlayerControlsService();
        } catch (Exception e) {
            this.playerControlsService = null;
        }
    }
    
    public void goBackToSettings() {
        SettingsController settingsController = new SettingsController(databaseManager);
        Settings settingsView = new Settings(settingsController, GameAssetManager.getInstance().getSkin());
        
        settingsController.setSettingsView(settingsView);
        settingsController.setEnhancedMusicService(Dawn.getEnhancedMusicService());
        settingsView.setEnhancedMusicService(Dawn.getEnhancedMusicService());
        
        Dawn.getInstance().setScreen(settingsView);
    }
    
    public void saveControls(int moveUp, int moveDown, int moveLeft, int moveRight, int reload, int shoot, int sprint) {
        Player player = App.getInstance().getPlayer();
        if (player != null) {
            
            player.setMoveUpKey(moveUp);
            player.setMoveDownKey(moveDown);
            player.setMoveLeftKey(moveLeft);
            player.setMoveRightKey(moveRight);
            player.setReloadKey(reload);
            player.setShootKey(shoot);
            player.setSprintKey(sprint);
            
            
            databaseManager.savePlayer(player);
            
            
            if (playerControlsService != null) {
                playerControlsService.updateAllKeyboardControls();
            }
        }
    }
    
    public void resetToDefaults() {
        saveControls(DEFAULT_MOVE_UP, DEFAULT_MOVE_DOWN, DEFAULT_MOVE_LEFT, 
                    DEFAULT_MOVE_RIGHT, DEFAULT_RELOAD, DEFAULT_SHOOT, DEFAULT_SPRINT);
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    
    public int getMoveUp() { 
        Player player = App.getInstance().getPlayer();
        return player != null && player.getMoveUpKey() != null ? player.getMoveUpKey() : DEFAULT_MOVE_UP;
    }
    
    public int getMoveDown() { 
        Player player = App.getInstance().getPlayer();
        return player != null && player.getMoveDownKey() != null ? player.getMoveDownKey() : DEFAULT_MOVE_DOWN;
    }
    
    public int getMoveLeft() { 
        Player player = App.getInstance().getPlayer();
        return player != null && player.getMoveLeftKey() != null ? player.getMoveLeftKey() : DEFAULT_MOVE_LEFT;
    }
    
    public int getMoveRight() { 
        Player player = App.getInstance().getPlayer();
        return player != null && player.getMoveRightKey() != null ? player.getMoveRightKey() : DEFAULT_MOVE_RIGHT;
    }
    
    public int getReload() { 
        Player player = App.getInstance().getPlayer();
        return player != null && player.getReloadKey() != null ? player.getReloadKey() : DEFAULT_RELOAD;
    }
    
    public int getShoot() { 
        Player player = App.getInstance().getPlayer();
        return player != null && player.getShootKey() != null ? player.getShootKey() : DEFAULT_SHOOT;
    }
    
    public int getSprint() { 
        Player player = App.getInstance().getPlayer();
        return player != null && player.getSprintKey() != null ? player.getSprintKey() : DEFAULT_SPRINT;
    }
} 