package com.example.dawn.controller;

import java.util.ArrayList;

import com.example.dawn.Dawn;
import com.example.dawn.enums.Regex;
import com.example.dawn.models.App;
import com.example.dawn.models.Character;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.models.Player;
import com.example.dawn.models.Result;
import com.example.dawn.service.EnhancedMusicService;
import com.example.dawn.view.LoginMenu;
import com.example.dawn.view.MainMenu;
import com.example.dawn.view.PregameMenu;

public class SignUpMenuController extends Controller {
    private final DatabaseManager databaseManager;
    private final EnhancedMusicService enhancedMusicService;

    public SignUpMenuController(DatabaseManager databaseManager, EnhancedMusicService enhancedMusicService) {
        this.databaseManager = databaseManager;
        this.enhancedMusicService = enhancedMusicService;
    }

    public Result register(String username, String password, String passwordConfirm, String securityAnswer) {
        if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || securityAnswer.isEmpty()) {
            return new Result(false, "All fields are required");
        }
        
        if (databaseManager.playerExists(username)) {
            return new Result(false, "Username already exists");
        }

        if (!password.equals(passwordConfirm)) {
            return new Result(false, "Passwords do not match");
        }

        if (Regex.Password.getMatcher(password) == null) {
            return new Result(false, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }

        Character shana = Dawn.getDatabaseManager().getCharacter("Shana");
        ArrayList<Character> characters = new ArrayList<>();
        characters.add(shana);
        Player player = new Player(username, password, securityAnswer, 0, 0, 0, 0, 0, shana, characters);
        databaseManager.savePlayer(player);
        App.getInstance().setPlayer(player);
        return new Result(true, "Player registered successfully");
    }

    public void guestLogin() {
        
        Character shana = Dawn.getDatabaseManager().getCharacter("Shana");
        if (shana == null) {
            System.out.println("Error: Default character 'Shana' not found!");
            return;
        }
        
        ArrayList<Character> characters = new ArrayList<>();
        characters.add(shana);
        Player guestPlayer = new Player("Guest", "", "", 0, 0, 0, 0, 0, shana, characters);
        App.getInstance().setPlayer(guestPlayer);
        
        Dawn.getInstance().setScreen(new PregameMenu(new PregameMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public void goToLogin() {
        Dawn.getInstance().setScreen(new LoginMenu(new LoginMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public void goToMainMenu() {
        Dawn.getInstance().setScreen(new MainMenu(new MainMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public EnhancedMusicService getEnhancedMusicService() {
        return enhancedMusicService;
    }
}
