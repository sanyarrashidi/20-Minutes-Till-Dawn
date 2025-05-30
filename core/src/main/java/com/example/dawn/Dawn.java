package com.example.dawn;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.dawn.controller.SignUpMenuController;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.service.EnhancedMusicService;
import com.example.dawn.service.PlayerControlsService;
import com.example.dawn.view.SignUpMenu;

public class Dawn extends Game {
    
    public static final int WIDTH = 450, HEIGHT = 600;
    private static Dawn dawn;
    private static SpriteBatch batch;
    private static DatabaseManager databaseManager;
    private static EnhancedMusicService enhancedMusicService;
    private static PlayerControlsService playerControlsService;


    @Override
    public void create() {
        dawn = this;
        batch = new SpriteBatch();
        databaseManager = new DatabaseManager();
        
        
        enhancedMusicService = new EnhancedMusicService();
        enhancedMusicService.initialize();
        
        
        playerControlsService = new PlayerControlsService();
        
        dawn.setScreen(new SignUpMenu(new SignUpMenuController(databaseManager, enhancedMusicService), GameAssetManager.getInstance().getSkin()));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (enhancedMusicService != null) {
            enhancedMusicService.dispose();
        }
    }

    public static SpriteBatch getBatch() {
        return batch;
    }
    
    public static Dawn getInstance() {
        return dawn;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public static EnhancedMusicService getEnhancedMusicService() {
        return enhancedMusicService;
    }
    
    public static PlayerControlsService getPlayerControlsService() {
        return playerControlsService;
    }
}