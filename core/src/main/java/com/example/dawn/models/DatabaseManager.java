package com.example.dawn.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class DatabaseManager {
    private static final String PLAYERS_FILE = "players.json";
    private static final String CHARACTERS_FILE = "characters.json";
    private static final String WEAPONS_FILE = "weapons.json";
    private Json json;
    private FileHandle fileHandle;
    private Map<String, Player> playersMap;
    private Map<String, Character> charactersMap;
    private Map<String, Weapon> weaponsMap;

    public DatabaseManager() {
        json = new Json();
        fileHandle = Gdx.files.local(PLAYERS_FILE);
        loadPlayers();
        loadCharacters();
        loadWeapons();
    }

    @SuppressWarnings("unchecked")
    private void loadPlayers() {
        if (fileHandle.exists()) {
            String jsonData = fileHandle.readString();
            if (jsonData != null && !jsonData.isEmpty()) {
                Array<Player> playerArray = json.fromJson(Array.class, Player.class, jsonData);
                playersMap = new HashMap<>();
                if (playerArray != null) {
                    for (Player player : playerArray) {
                        playersMap.put(player.getUsername(), player);
                    }
                }
            } else {
                playersMap = new HashMap<>();
            }
        } else {
            playersMap = new HashMap<>();
        }
    }

    public void savePlayer(Player player) {
        if (player == null || player.getUsername() == null || player.getUsername().isEmpty()) {
            Gdx.app.error("DatabaseManager", "Cannot save player with null or empty username.");
            return;
        }
        playersMap.put(player.getUsername(), player);
        saveAllPlayers();
    }

    private void saveAllPlayers() {
        this.fileHandle = Gdx.files.local(PLAYERS_FILE);
        Array<Player> playerArray = new Array<>(playersMap.values().toArray(new Player[0]));
        String jsonData = json.prettyPrint(playerArray);
        fileHandle.writeString(jsonData, false);
    }

    public Player getPlayer(String username) {
        return playersMap.get(username);
    }

    public boolean playerExists(String username) {
        return playersMap.containsKey(username);
    }

    public ArrayList<Player> getPlayers() {
        if (playersMap == null) {
            return new ArrayList<>(); 
        }
        return new ArrayList<>(playersMap.values());
    }

    public void deletePlayer(String username) {
        playersMap.remove(username);
        saveAllPlayers();
    }

    private void loadCharacters() {
        fileHandle = Gdx.files.internal(CHARACTERS_FILE);
        if (fileHandle.exists()) {
            String jsonData = fileHandle.readString();
            if (jsonData != null && !jsonData.isEmpty()) {
                Array<Character> characterArray = json.fromJson(Array.class, Character.class, jsonData);
                charactersMap = new HashMap<>();
                if (characterArray != null) {
                    for (Character character : characterArray) {
                        
                        if (character.getInitialHp() == 0 && character.getHp() > 0) {
                            character.setInitialHp(character.getHp());
                        }
                        charactersMap.put(character.getName(), character);
                    }
                }
            } else {
                charactersMap = new HashMap<>();
            }
        } else {
            Gdx.app.error("DatabaseManager", CHARACTERS_FILE + " not found!");
            charactersMap = new HashMap<>();
        }
    }

    public Character getCharacter(String name) {
        return charactersMap.get(name);
    }

    public ArrayList<Character> getCharacters() {
        if (charactersMap == null) {
            return new ArrayList<>(); 
        }
        return new ArrayList<>(charactersMap.values());
    }

    private void loadWeapons() {
        fileHandle = Gdx.files.internal(WEAPONS_FILE);
        if (fileHandle.exists()) {
            String jsonData = fileHandle.readString();
            if (jsonData != null && !jsonData.isEmpty()) {
                Array<Weapon> weaponArray = json.fromJson(Array.class, Weapon.class, jsonData);
                weaponsMap = new HashMap<>();
                if (weaponArray != null) {
                    for (Weapon weapon : weaponArray) {
                        weaponsMap.put(weapon.getName(), weapon);
                    }
                }
            } else {
                weaponsMap = new HashMap<>();
            }
        } else {
            Gdx.app.error("DatabaseManager", WEAPONS_FILE + " not found!");
            weaponsMap = new HashMap<>();
        }
    }

    public Weapon getWeapon(String name) {
        return weaponsMap.get(name);
    }

    public ArrayList<Weapon> getWeapons() {
        if (weaponsMap == null) {
            return new ArrayList<>(); 
        }
        return new ArrayList<>(weaponsMap.values());
    }
}