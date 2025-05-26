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
        // TODO: Implement pause functionality
    }
    
    public void resumeGame() {
        // TODO: Implement resume functionality
    }
    
    public void endGame() {
        endGame(com.example.dawn.models.GameSummary.GameEndReason.TIME_UP);
    }
    
    public void endGame(com.example.dawn.models.GameSummary.GameEndReason reason) {
        // This method will be called from GameScreen with the appropriate reason
        // The GameScreen will handle creating and showing the summary
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