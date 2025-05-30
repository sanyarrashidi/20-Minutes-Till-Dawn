package com.example.dawn.controller;

import com.badlogic.gdx.Gdx;
import com.example.dawn.Dawn;
import com.example.dawn.models.App;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.service.EnhancedMusicService;
import com.example.dawn.view.HintMenu;
import com.example.dawn.view.LoginMenu;
import com.example.dawn.view.PregameMenu;
import com.example.dawn.view.ProfileMenu;
import com.example.dawn.view.ScoreboardMenu;
import com.example.dawn.view.Settings;

public class MainMenuController extends Controller {
    private final DatabaseManager databaseManager;

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
        
        EnhancedMusicService musicService = Dawn.getEnhancedMusicService();
        
        settingsController.setSettingsView(settingsView);
        settingsController.setEnhancedMusicService(musicService);
        settingsView.setEnhancedMusicService(musicService);
        
        Dawn.getInstance().setScreen(settingsView);
    }
    
    public void goToHintMenu() {
        Dawn.getInstance().setScreen(new HintMenu(new HintMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }
    
    public EnhancedMusicService getMusicService() {
        return Dawn.getEnhancedMusicService();
    }
    
    public void startMenuMusic() {
        EnhancedMusicService musicService = Dawn.getEnhancedMusicService();
        if (musicService != null) {
            musicService.switchToMenuMusic();
        }
    }

    public void goToPregame() {
        Dawn.getInstance().setScreen(new PregameMenu(new PregameMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }
}
