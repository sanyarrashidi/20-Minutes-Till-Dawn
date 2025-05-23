package com.example.dawn.controller;

import com.badlogic.gdx.Gdx;
import com.example.dawn.Dawn;
import com.example.dawn.models.App;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.view.LoginMenu;
import com.example.dawn.view.ProfileMenu;
import com.example.dawn.view.ScoreboardMenu;

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
}
