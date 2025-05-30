package com.example.dawn.controller;

import com.example.dawn.Dawn;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.view.MainMenu;

public class GameScreenController extends Controller {
    
    private final DatabaseManager databaseManager;
    private final int gameDurationMinutes;
    
    public GameScreenController(DatabaseManager databaseManager, int gameDurationMinutes) {
        this.databaseManager = databaseManager;
        this.gameDurationMinutes = gameDurationMinutes;
    }
    
    public void pauseGame() {
        
    }
    
    public void resumeGame() {
        
    }
    
    public void endGame() {
        endGame(com.example.dawn.models.GameSummary.GameEndReason.TIME_UP);
    }
    
    public void endGame(com.example.dawn.models.GameSummary.GameEndReason reason) {
        
        
        System.out.println("Game ended with reason: " + reason);
    }
    
    public void returnToMainMenu() {
        Dawn.getInstance().setScreen(new MainMenu(new MainMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public int getGameDurationMinutes() {
        return gameDurationMinutes;
    }
} 