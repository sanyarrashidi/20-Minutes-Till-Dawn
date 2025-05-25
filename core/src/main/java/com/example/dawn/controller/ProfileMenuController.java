package com.example.dawn.controller;

import com.example.dawn.Dawn;
import com.example.dawn.enums.Regex;
import com.example.dawn.models.App;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.models.Player;
import com.example.dawn.models.Result;
import com.example.dawn.view.MainMenu;
import com.example.dawn.view.SignUpMenu;


public class ProfileMenuController extends Controller {
    private final DatabaseManager databaseManager;

    public ProfileMenuController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Result changeUsername(String newUsername) {
        Player player = App.getInstance().getPlayer();
        if (newUsername.isEmpty()) {
            return new Result(false, "Username cannot be empty");
        }
        if (newUsername.equals(player.getUsername())) {
            return new Result(false, "Username is the same as the current username");
        }
        if (databaseManager.playerExists(newUsername)) {
            return new Result(false, "Username already exists");
        }
        
        databaseManager.deletePlayer(player.getUsername());
        player.setUsername(newUsername);
        databaseManager.savePlayer(player);
        return new Result(true, "Username changed successfully");
    }

    public Result changePassword(String newPassword, String newPasswordConfirm) {
        Player player = App.getInstance().getPlayer();
        if (newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
            return new Result(false, "Fields cannot be empty");
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            return new Result(false, "Passwords do not match");
        }
        if (Regex.Password.getMatcher(newPassword) == null) {
            return new Result(false, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }
        if (newPassword.equals(player.getPassword())) {
            return new Result(false, "Password is the same as the current password");
        }

        player.setPassword(newPassword);
        databaseManager.savePlayer(player);
        return new Result(true, "Password changed successfully");
    }

    public void deleteAccount() {
        Player player = App.getInstance().getPlayer();
        databaseManager.deletePlayer(player.getUsername());
        App.getInstance().setPlayer(null);
        Dawn.getInstance().setScreen(new SignUpMenu(new SignUpMenuController(databaseManager, Dawn.getEnhancedMusicService()), GameAssetManager.getInstance().getSkin()));
    }

    public void goToMainMenu() {
        Dawn.getInstance().setScreen(new MainMenu(new MainMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public void logout() {
        App.getInstance().setPlayer(null);
        Dawn.getInstance().setScreen(new SignUpMenu(new SignUpMenuController(databaseManager, Dawn.getEnhancedMusicService()), GameAssetManager.getInstance().getSkin()));
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}