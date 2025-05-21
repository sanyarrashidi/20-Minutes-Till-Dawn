package com.example.dawn.models;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class DatabaseManager {
    private static final String PLAYERS_FILE = "players.json";
    private final Json json;
    private final FileHandle fileHandle;
    private Map<String, Player> playersMap;

    public DatabaseManager() {
        json = new Json();
        fileHandle = Gdx.files.local(PLAYERS_FILE);
        loadPlayers();
    }

    @SuppressWarnings("unchecked")
    private void loadPlayers() {
        if (fileHandle.exists()) {
            String jsonData = fileHandle.readString();
            if (jsonData != null && !jsonData.isEmpty()) {
                // Deserialize to an Array of Player objects first
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
        // Convert Map values to an Array for JSON serialization
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
} 