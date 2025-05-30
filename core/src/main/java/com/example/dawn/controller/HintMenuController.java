package com.example.dawn.controller;

import com.example.dawn.Dawn;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.view.MainMenu;

public class HintMenuController extends Controller {
    private final DatabaseManager databaseManager;

    public HintMenuController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void goToMainMenu() {
        Dawn.getInstance().setScreen(new MainMenu(new MainMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
} 