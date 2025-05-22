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


public class LoginMenuController extends Controller {
    private final DatabaseManager databaseManager;

    public LoginMenuController(DatabaseManager databaseManager) {
        super();
        this.databaseManager = databaseManager;
    }

    public Result login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return new Result(false, "Username and password are required");
        }
        
        if (!databaseManager.playerExists(username)) {
            return new Result(false, "Player does not exist");
        }

        Player player = databaseManager.getPlayer(username);
        if (!player.getPassword().equals(password)) {
            return new Result(false, "Invalid password");
        }

        App.getInstance().setPlayer(player);
        return new Result(true, "Login successful");
    }

    public void goToSignUp() {
        Dawn.getInstance().setScreen(new SignUpMenu(new SignUpMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }

    public Result forgotPassword(String username, String securityAnswer, String password, String passwordConfirm) {
        if (username.isEmpty() || securityAnswer.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            return new Result(false, "All fields are required");
        }

        if (!databaseManager.playerExists(username)) {
            return new Result(false, "Player does not exist");
        }

        Player player = databaseManager.getPlayer(username);
        if (!player.getSecurityAnswer().equals(securityAnswer)) {
            return new Result(false, "Invalid security answer");
        }

        if (!password.equals(passwordConfirm)) {
            return new Result(false, "Passwords do not match");
        }

        if (Regex.Password.getMatcher(password) == null) {
            return new Result(false, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }

        player.setPassword(password);
        databaseManager.savePlayer(player);
        return new Result(true, "Password reset successful");
    }

    public void goToMainMenu() {
        Dawn.getInstance().setScreen(new MainMenu(new MainMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }
}