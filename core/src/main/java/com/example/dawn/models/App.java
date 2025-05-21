package com.example.dawn.models;


public class App {
    private static App instance;
    private Game game;
    private Player player;
    
    private App() {
        instance = this;
    }

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}