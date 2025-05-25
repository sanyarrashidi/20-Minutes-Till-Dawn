package com.example.dawn.controller;

import com.badlogic.gdx.Gdx;
import com.example.dawn.Dawn;
import com.example.dawn.models.App;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.service.EnhancedMusicService;
import com.example.dawn.view.LoginMenu;
import com.example.dawn.view.PregameMenu;
import com.example.dawn.view.ProfileMenu;
import com.example.dawn.view.ScoreboardMenu;
import com.example.dawn.view.Settings;

public class MainMenuController extends Controller {
    private final DatabaseManager databaseManager;
    private static EnhancedMusicService sharedMusicService;

    public MainMenuController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void exitGame() {
        Gdx.app.exit();
    }

    public void logout() {
        App.getInstance().setPlayer(null);
        Dawn.getInstance().setScreen(new LoginMenu(new LoginMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public void goToScoreboard() {
        Dawn.getInstance().setScreen(new ScoreboardMenu(new ScoreboardMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public void goToProfile() {
        Dawn.getInstance().setScreen(new ProfileMenu(new ProfileMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public void goToSettings() {
        SettingsController settingsController = new SettingsController(databaseManager);
        Settings settingsView = new Settings(settingsController, GameAssetManager.getInstance().getSkin());
        
        initializeMusicService();
        
        settingsController.setSettingsView(settingsView);
        settingsController.setEnhancedMusicService(sharedMusicService);
        settingsView.setEnhancedMusicService(sharedMusicService);
        
        Dawn.getInstance().setScreen(settingsView);
    }
    
    private void initializeMusicService() {
        if (sharedMusicService == null) {
            sharedMusicService = new EnhancedMusicService();
            sharedMusicService.initialize();
        }
    }
    
    public EnhancedMusicService getMusicService() {
        initializeMusicService();
        return sharedMusicService;
    }
    
    public void startMenuMusic() {
        initializeMusicService();
        sharedMusicService.switchToMenuMusic();
    }

    public void goToPregame() {
        Dawn.getInstance().setScreen(new PregameMenu(new PregameMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }
}
