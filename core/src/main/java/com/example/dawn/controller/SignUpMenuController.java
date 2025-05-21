package com.example.dawn.controller;

import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.Player;
import com.example.dawn.models.Result;
import com.example.dawn.enums.Regex;

public class SignUpMenuController extends Controller {
    private final DatabaseManager databaseManager;

    public SignUpMenuController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
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

        Player player = new Player(username, password, securityAnswer);
        databaseManager.savePlayer(player);
        return new Result(true, "Player registered successfully");
    }

    public Result guestLogin() {
        return new Result(true, "Guest login successful");
    }

    public Result goToLogin() {
        return new Result(true, "Going to login");
    }
}
