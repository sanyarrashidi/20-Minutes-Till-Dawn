package com.example.dawn.controller;

import com.example.dawn.Dawn;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.view.MainMenu;
import com.example.dawn.view.PregameMenu;

public class GameSummaryController extends Controller {
    
    private final DatabaseManager databaseManager;
    private final int gameDurationMinutes;
    
    public GameSummaryController(DatabaseManager databaseManager, int gameDurationMinutes) {
        this.databaseManager = databaseManager;
        this.gameDurationMinutes = gameDurationMinutes;
    }
    
    public void playAgain() {
        // Reset character for new game (HP, abilities, bonuses)
        if (com.example.dawn.models.App.getInstance().getPlayer() != null && 
            com.example.dawn.models.App.getInstance().getPlayer().getCharacter() != null) {
            com.example.dawn.models.App.getInstance().getPlayer().getCharacter().resetForNewGame();
        }
        
        // Return to pregame menu to start a new game
        Dawn.getInstance().setScreen(new PregameMenu(new PregameMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }
    
    public void returnToMainMenu() {
        // Reset character for new game (HP, abilities, bonuses)
        if (com.example.dawn.models.App.getInstance().getPlayer() != null && 
            com.example.dawn.models.App.getInstance().getPlayer().getCharacter() != null) {
            com.example.dawn.models.App.getInstance().getPlayer().getCharacter().resetForNewGame();
        }
        
        Dawn.getInstance().setScreen(new MainMenu(new MainMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public int getGameDurationMinutes() {
        return gameDurationMinutes;
    }
} 