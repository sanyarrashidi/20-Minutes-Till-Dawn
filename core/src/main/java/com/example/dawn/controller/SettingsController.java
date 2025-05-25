package com.example.dawn.controller;

import com.badlogic.gdx.utils.Array;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.service.EnhancedMusicService;
import com.example.dawn.view.Settings;

public class SettingsController extends Controller {
    
    private final DatabaseManager databaseManager;
    private EnhancedMusicService enhancedMusicService;
    private Settings settingsView;
    
    public SettingsController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    public void setEnhancedMusicService(EnhancedMusicService service) {
        this.enhancedMusicService = service;
        
        if (settingsView != null) {
            settingsView.setEnhancedMusicService(service);
        }
        updateMusicTracksInView();
    }
    
    public void setSettingsView(Settings view) {
        this.settingsView = view;
        
        if (enhancedMusicService != null) {
            view.setEnhancedMusicService(enhancedMusicService);
        }
        updateMusicTracksInView();
    }

    private void updateMusicTracksInView() {
        if (enhancedMusicService != null && settingsView != null) {
            Array<String> availableTracks = enhancedMusicService.getTrackDisplayNames();
            settingsView.updateMusicTracks(availableTracks);
        }
    }
    
    public void changeMenuMusic(String trackDisplayName) {
        if (enhancedMusicService != null) {
            enhancedMusicService.setMenuMusic(trackDisplayName);
        }
    }
    
    public void changeGameMusic(String trackDisplayName) {
        if (enhancedMusicService != null) {
            enhancedMusicService.setGameMusic(trackDisplayName);
        }
    }
    
    public String getCurrentMenuMusic() {
        return enhancedMusicService != null ? enhancedMusicService.getCurrentMenuMusic() : "Default Theme";
    }

    public String getCurrentGameMusic() {
        return enhancedMusicService != null ? enhancedMusicService.getCurrentGameMusic() : "Default Theme";
    }

    public Array<String> getAvailableMusicTracks() {
        return enhancedMusicService != null ? enhancedMusicService.getTrackDisplayNames() : new Array<String>();
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}