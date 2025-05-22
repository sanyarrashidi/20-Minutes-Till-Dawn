package com.example.dawn.controller;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.example.dawn.Dawn;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.models.Player;
import com.example.dawn.view.MainMenu;

public class ScoreboardMenuController extends Controller {
    private final DatabaseManager databaseManager;  

    public ScoreboardMenuController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    public ArrayList<Player> getPlayersList() {
        if (databaseManager == null) {
            Gdx.app.error("ScoreboardMenuController", "DatabaseManager is null!");
            return new ArrayList<>();
        }
        return databaseManager.getPlayers();
    }

    public void back() {
        Dawn.getInstance().setScreen(new MainMenu(new MainMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public void sortByUsername(ArrayList<Player> players) {
        Collections.sort(players, (p1, p2) -> p1.getUsername().compareTo(p2.getUsername()));
    }

    public void sortByWins(ArrayList<Player> players) {
        Collections.sort(players, (p1, p2) -> p2.getWins() - p1.getWins());
    }

    public void sortByKills(ArrayList<Player> players) {
        Collections.sort(players, (p1, p2) -> p2.getKills() - p1.getKills());
    }

    public void sortBySurvivalDuration(ArrayList<Player> players) {
        Collections.sort(players, (p1, p2) -> p2.getSurvivalDuration() - p1.getSurvivalDuration());
    }

    public void sortByScore(ArrayList<Player> players) {
        Collections.sort(players, (p1, p2) -> p2.getHighScore() - p1.getHighScore());
    }
}