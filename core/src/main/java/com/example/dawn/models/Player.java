package com.example.dawn.models;

import java.util.ArrayList;

public class Player {
    private String username;
    private String password;
    private String securityAnswer;
    private String avatarPath;
    private Integer wins;
    private Integer highScore;
    private Integer totalGames;
    private Integer kills;
    private Integer survivalDuration;
    private Character character;
    private ArrayList<Character> characters;

    // No-argument constructor for JSON deserialization
    public Player() {
    }

    public Player(String username, String password, String securityAnswer, Integer wins, Integer highScore, Integer totalGames, Integer kills, Integer survivalDuration, Character character, ArrayList<Character> characters) {
        this.username = username;
        this.password = password;
        this.securityAnswer = securityAnswer;
        this.wins = wins;
        this.highScore = highScore;
        this.totalGames = totalGames;
        this.kills = kills;
        this.survivalDuration = survivalDuration;
        this.character = character;
        this.characters = characters;
        this.avatarPath = character.getImagePath();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='''" + username + "'''" +
                ", securityAnswer='''" + securityAnswer + "'''" +
                '}';
    }
    
    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public void addWin() {
        wins++;
    }

    public Integer getHighScore() {
        return highScore;
    }

    public void setHighScore(Integer highScore) {
        this.highScore = highScore;
    }

    public Integer getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(Integer totalGames) {
        this.totalGames = totalGames;
    }

    public void addTotalGames() {
        totalGames++;
    }

    public Integer getKills() {
        return kills;
    }

    public void setKills(Integer kills) {
        this.kills = kills;
    }

    public void addKills(Integer amount) {
        this.kills += amount;
    }

    public Integer getSurvivalDuration() {
        return survivalDuration;
    }

    public void setSurvivalDuration(Integer survivalDuration) {
        this.survivalDuration = survivalDuration;
    }

    public void addSurvivalDuration(Integer amount) {
        this.survivalDuration += amount;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList<Character> characters) {
        this.characters = characters;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
} 