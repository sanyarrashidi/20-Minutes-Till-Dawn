package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.Dawn;
import com.example.dawn.controller.SettingsController;
import com.example.dawn.controller.MainMenuController;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.service.EnhancedMusicService;

public class Settings extends AppMenu {
    
    private final Skin skin;
    private EnhancedMusicService enhancedMusicService;
    
    // UI Components
    private final Label titleLabel;
    private final Label musicVolumeLabel;
    private final Slider musicVolumeSlider;
    private final CheckBox musicToggleCheckBox;
    private final Label menuMusicLabel;
    private final SelectBox<String> menuMusicSelectBox;
    private final Label gameMusicLabel;
    private final SelectBox<String> gameMusicSelectBox;
    private final Label sfxVolumeLabel;
    private final Slider sfxVolumeSlider;
    private final CheckBox sfxToggleCheckBox;
    private final TextButton backButton;
    private final Label statusLabel;
    
    public Settings(SettingsController controller, Skin skin) {
        super(controller);
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        
        this.enhancedMusicService = null; 
        
        this.titleLabel = new Label("Settings", skin);
        this.titleLabel.setColor(Color.GOLD);
        
        this.musicVolumeLabel = new Label("Music Volume:", skin);
        this.musicVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        this.musicToggleCheckBox = new CheckBox("Music ON", skin);
        
        this.menuMusicLabel = new Label("Menu Music:", skin);
        this.menuMusicSelectBox = new SelectBox<String>(skin);
        
        this.gameMusicLabel = new Label("Game Music:", skin);
        this.gameMusicSelectBox = new SelectBox<String>(skin);
        
        this.sfxVolumeLabel = new Label("SFX Volume:", skin);
        this.sfxVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        this.sfxToggleCheckBox = new CheckBox("SFX ON", skin);
        
        this.backButton = new TextButton("Back", skin);
        this.statusLabel = new Label("", skin);
        this.statusLabel.setColor(Color.GREEN);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        
        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);
        mainTable.center();
        
        titleLabel.setFontScale(1.8f);
        mainTable.add(titleLabel).colspan(2).center().padBottom(40);
        mainTable.row();
        
        Label musicSectionLabel = new Label("Music Settings", skin);
        musicSectionLabel.setFontScale(1.4f);
        mainTable.add(musicSectionLabel).colspan(2).center().padBottom(30);
        mainTable.row();
        
        musicVolumeLabel.setFontScale(1.2f);
        mainTable.add(musicVolumeLabel).left().padRight(30).padBottom(15);
        mainTable.add(musicVolumeSlider).width(400).height(40).left().padBottom(15);
        mainTable.row();
        
        musicToggleCheckBox.getLabel().setFontScale(1.2f);
        mainTable.add(musicToggleCheckBox).colspan(2).center().padBottom(30);
        mainTable.row();
        
        Label trackSectionLabel = new Label("Music Tracks", skin);
        trackSectionLabel.setFontScale(1.4f);
        mainTable.add(trackSectionLabel).colspan(2).center().padBottom(30);
        mainTable.row();
        
        menuMusicLabel.setFontScale(1.2f);
        mainTable.add(menuMusicLabel).left().padRight(30).padBottom(15);
        mainTable.add(menuMusicSelectBox).width(400).height(50).left().padBottom(15);
        mainTable.row();
        
        gameMusicLabel.setFontScale(1.2f);
        mainTable.add(gameMusicLabel).left().padRight(30).padBottom(30);
        mainTable.add(gameMusicSelectBox).width(400).height(50).left().padBottom(30);
        mainTable.row();
        
        Label sfxSectionLabel = new Label("Sound Effects", skin);
        sfxSectionLabel.setFontScale(1.4f);
        mainTable.add(sfxSectionLabel).colspan(2).center().padBottom(30);
        mainTable.row();
        
        sfxVolumeLabel.setFontScale(1.2f);
        mainTable.add(sfxVolumeLabel).left().padRight(30).padBottom(15);
        mainTable.add(sfxVolumeSlider).width(400).height(40).left().padBottom(15);
        mainTable.row();
        
        sfxToggleCheckBox.getLabel().setFontScale(1.2f);
        mainTable.add(sfxToggleCheckBox).colspan(2).center().padBottom(40);
        mainTable.row();

        statusLabel.setFontScale(1.1f);
        mainTable.add(statusLabel).colspan(2).center().padTop(20).padBottom(20);
        mainTable.row();
        
        backButton.getLabel().setFontScale(1.3f);
        mainTable.add(backButton).colspan(2).center().width(300).height(80).padTop(30);
        
        stage.addActor(mainTable);
        
        initializeValues();
        setupListeners();
    }
    
    private void initializeValues() {
        if (enhancedMusicService != null) {
            musicVolumeSlider.setValue(enhancedMusicService.getMusicVolume());
            musicToggleCheckBox.setChecked(enhancedMusicService.isMusicEnabled());
        } else {
            musicVolumeSlider.setValue(0.7f);
            musicToggleCheckBox.setChecked(true);
        }
        
        sfxVolumeSlider.setValue(0.8f);
        sfxToggleCheckBox.setChecked(true);
        
        if (enhancedMusicService != null) {
            try {
                Array<String> availableTracks = enhancedMusicService.getTrackDisplayNames();
                if (availableTracks != null && availableTracks.size > 0) {
                    menuMusicSelectBox.setItems(availableTracks);
                    gameMusicSelectBox.setItems(availableTracks);
                    
                    String currentMenuMusic = enhancedMusicService.getCurrentMenuMusic();
                    String currentGameMusic = enhancedMusicService.getCurrentGameMusic();
                    
                    menuMusicSelectBox.setSelected(currentMenuMusic);
                    gameMusicSelectBox.setSelected(currentGameMusic);
                    
                    statusLabel.setText("Loaded " + availableTracks.size + " music tracks");
                } 
                else {
                    setDefaultTracks();
                }
            } 
            catch (Exception e) {
                Gdx.app.error("Settings", "Failed to load music tracks", e);
                setDefaultTracks();
            }
        } 
        else {
            setDefaultTracks();
            statusLabel.setText("Music service not available - using defaults");
        }
    }
    
    private void setDefaultTracks() {
        Array<String> defaultTracks = new Array<String>();
        defaultTracks.add("Default Theme");
        
        menuMusicSelectBox.setItems(defaultTracks);
        gameMusicSelectBox.setItems(defaultTracks);
        
        menuMusicSelectBox.setSelected("Default Theme");
        gameMusicSelectBox.setSelected("Default Theme");
    }
    
    private void setupListeners() {
        musicVolumeSlider.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float volume = musicVolumeSlider.getValue();
                if (enhancedMusicService != null) {
                    enhancedMusicService.setMusicVolume(volume);
                    statusLabel.setText("Music volume: " + Math.round(volume * 100) + "%");
                } else {
                    statusLabel.setText("Music service unavailable");
                }
                Gdx.app.log("Settings", "Music volume set to: " + volume);
            }
        });
        
        musicToggleCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enabled = musicToggleCheckBox.isChecked();
                if (enhancedMusicService != null) {
                    enhancedMusicService.setMusicEnabled(enabled);
                    statusLabel.setText("Music " + (enabled ? "enabled" : "disabled"));
                } else {
                    statusLabel.setText("Music service unavailable");
                }
                Gdx.app.log("Settings", "Music toggled: " + enabled);
            }
        });
        
        menuMusicSelectBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String selected = menuMusicSelectBox.getSelected();
                if (enhancedMusicService != null) {
                    enhancedMusicService.setMenuMusic(selected);
                    enhancedMusicService.switchToMenuMusic();
                    statusLabel.setText("Menu music: " + selected);
                } 
                else {
                    statusLabel.setText("Music service unavailable");
                }
            }
        });
        
        gameMusicSelectBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String selected = gameMusicSelectBox.getSelected();
                if (enhancedMusicService != null) {
                    enhancedMusicService.setGameMusic(selected);
                    statusLabel.setText("Game music: " + selected + " (will play in game)");
                } 
                else {
                    statusLabel.setText("Music service unavailable");
                }
            }
        });
        
        sfxVolumeSlider.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float volume = sfxVolumeSlider.getValue();
                // TODO: Update actual SFX service volume here
                statusLabel.setText("SFX volume: " + Math.round(volume * 100) + "%");
            }
        });
        
        // SFX toggle checkbox
        sfxToggleCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enabled = sfxToggleCheckBox.isChecked();
                // TODO: Update actual SFX service enabled state here
                statusLabel.setText("SFX " + (enabled ? "enabled" : "disabled"));
                Gdx.app.log("Settings", "SFX toggled: " + enabled);
            }
        });
        
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToMainMenu();
            }
        });
    }
    
    private void returnToMainMenu() {
        enhancedMusicService.switchToMenuMusic();
            
        SettingsController settingsController = (SettingsController) controller;
        MainMenuController mainMenuController = new MainMenuController(settingsController.getDatabaseManager());
        MainMenu mainMenu = new MainMenu(mainMenuController, GameAssetManager.getInstance().getSkin());
        Dawn.getInstance().setScreen(mainMenu);
        statusLabel.setText("Returning to main menu...");
        Gdx.app.log("Settings", "Returning to main menu");
    }
    
    public void updateMusicTracks(Array<String> availableTracks) {
        if (availableTracks != null && availableTracks.size > 0) {
            menuMusicSelectBox.setItems(availableTracks);
            gameMusicSelectBox.setItems(availableTracks);
            statusLabel.setText("Found " + availableTracks.size + " music tracks");
        }
    }
    
    public void setEnhancedMusicService(EnhancedMusicService service) {
        this.enhancedMusicService = service;
        
        if (service != null) {
            try {
                Array<String> availableTracks = service.getTrackDisplayNames();
                if (availableTracks != null && availableTracks.size > 0) {
                    updateMusicTracks(availableTracks);
                    
                    String currentMenuMusic = service.getCurrentMenuMusic();
                    String currentGameMusic = service.getCurrentGameMusic();
                    
                    menuMusicSelectBox.setSelected(currentMenuMusic);
                    gameMusicSelectBox.setSelected(currentGameMusic);
                    
                    musicVolumeSlider.setValue(service.getMusicVolume());
                    musicToggleCheckBox.setChecked(service.isMusicEnabled());
                    
                    service.switchToMenuMusic();
                    
                    statusLabel.setText("Music service connected - " + availableTracks.size + " tracks available");
                } 
                else {
                    statusLabel.setText("No music tracks found");
                }
            } 
            catch (Exception e) {
                statusLabel.setText("Music service error");
            }
        }
    }
}