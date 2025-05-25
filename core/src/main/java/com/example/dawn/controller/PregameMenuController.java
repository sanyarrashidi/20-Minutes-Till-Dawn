package com.example.dawn.controller;

import java.util.ArrayList;

import com.example.dawn.Dawn;
import com.example.dawn.models.App;
import com.example.dawn.models.Character;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.models.Weapon;
import com.example.dawn.view.GameScreen;
import com.example.dawn.view.MainMenu;

public class PregameMenuController extends Controller {
    private final DatabaseManager databaseManager;
    private Character selectedCharacter;
    private Weapon selectedWeapon;
    private int selectedGameDuration; // in minutes

    public PregameMenuController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        // Set default selections
        if (!databaseManager.getCharacters().isEmpty()) {
            this.selectedCharacter = databaseManager.getCharacters().get(0);
        }
        if (!databaseManager.getWeapons().isEmpty()) {
            this.selectedWeapon = databaseManager.getWeapons().get(0);
        }
        this.selectedGameDuration = 5; // default to 5 minutes
    }

    public ArrayList<Character> getCharacters() {
        return databaseManager.getCharacters();
    }

    public ArrayList<Weapon> getWeapons() {
        return databaseManager.getWeapons();
    }

    public void selectCharacter(Character character) {
        this.selectedCharacter = character;
    }

    public void selectWeapon(Weapon weapon) {
        this.selectedWeapon = weapon;
    }

    public void selectGameDuration(int minutes) {
        this.selectedGameDuration = minutes;
    }

    public Character getSelectedCharacter() {
        return selectedCharacter;
    }

    public Weapon getSelectedWeapon() {
        return selectedWeapon;
    }

    public int getSelectedGameDuration() {
        return selectedGameDuration;
    }

    public void startGame() {
        // Check if player is logged in
        if (App.getInstance().getPlayer() == null) {
            System.out.println("Error: No player logged in!");
            return;
        }
        
        // Update player's character and weapon before starting the game
        if (selectedCharacter != null) {
            App.getInstance().getPlayer().setCharacter(selectedCharacter);
        }
        if (selectedWeapon != null && App.getInstance().getPlayer().getCharacter() != null) {
            App.getInstance().getPlayer().getCharacter().setWeapon(selectedWeapon);
        }
        
        // Save the updated player data
        databaseManager.savePlayer(App.getInstance().getPlayer());
        
        // Start the actual game with the new GameScreen
        GameScreenController gameController = new GameScreenController(databaseManager);
        GameScreen gameScreen = new GameScreen(gameController);
        Dawn.getInstance().setScreen(gameScreen);
        
        System.out.println("Starting game with:");
        System.out.println("Character: " + (selectedCharacter != null ? selectedCharacter.getName() : "None"));
        System.out.println("Weapon: " + (selectedWeapon != null ? selectedWeapon.getName() : "None"));
        System.out.println("Duration: " + selectedGameDuration + " minutes");
    }

    public void goBack() {
        Dawn.getInstance().setScreen(new MainMenu(new MainMenuController(databaseManager), GameAssetManager.getInstance().getSkin()));
    }
}