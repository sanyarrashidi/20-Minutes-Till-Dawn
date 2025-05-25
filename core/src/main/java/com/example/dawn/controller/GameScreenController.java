package com.example.dawn.controller;

import com.example.dawn.Dawn;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.view.MainMenu;

public class GameScreenController extends Controller {
    
    private final DatabaseManager databaseManager;
    
    public GameScreenController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    public void pauseGame() {
        // TODO: Implement pause functionality
    }
    
    public void resumeGame() {
        // TODO: Implement resume functionality
    }
    
    public void endGame() {
        // TODO: Implement game ending logic (save score, etc.)
        returnToMainMenu();
    }
    
    public void returnToMainMenu() {
        Dawn.getInstance().setScreen(new MainMenu(new MainMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
} 