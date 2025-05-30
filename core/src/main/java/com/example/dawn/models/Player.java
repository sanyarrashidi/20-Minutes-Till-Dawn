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
    private Boolean autoReload = false;
    private Boolean blackAndWhiteScreen = false;

    
    private Integer moveUpKey = 51; 
    private Integer moveDownKey = 47; 
    private Integer moveLeftKey = 29; 
    private Integer moveRightKey = 32; 
    private Integer reloadKey = 46; 
    private Integer shootKey = 62; 
    private Integer sprintKey = 59; 

    
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

    public Boolean getAutoReload() {
        return autoReload;
    }

    public void setAutoReload(Boolean autoReload) {
        this.autoReload = autoReload;
    }

    public Boolean getBlackAndWhiteScreen() {
        return blackAndWhiteScreen;
    }

    public void setBlackAndWhiteScreen(Boolean blackAndWhiteScreen) {
        this.blackAndWhiteScreen = blackAndWhiteScreen;
    }

    public Integer getMoveUpKey() {
        return moveUpKey;
    }

    public void setMoveUpKey(Integer moveUpKey) {
        this.moveUpKey = moveUpKey;
    }

    public Integer getMoveDownKey() {
        return moveDownKey;
    }

    public void setMoveDownKey(Integer moveDownKey) {
        this.moveDownKey = moveDownKey;
    }

    public Integer getMoveLeftKey() {
        return moveLeftKey;
    }

    public void setMoveLeftKey(Integer moveLeftKey) {
        this.moveLeftKey = moveLeftKey;
    }

    public Integer getMoveRightKey() {
        return moveRightKey;
    }

    public void setMoveRightKey(Integer moveRightKey) {
        this.moveRightKey = moveRightKey;
    }

    public Integer getReloadKey() {
        return reloadKey;
    }

    public void setReloadKey(Integer reloadKey) {
        this.reloadKey = reloadKey;
    }

    public Integer getShootKey() {
        return shootKey;
    }

    public void setShootKey(Integer shootKey) {
        this.shootKey = shootKey;
    }

    public Integer getSprintKey() {
        return sprintKey;
    }

    public void setSprintKey(Integer sprintKey) {
        this.sprintKey = sprintKey;
    }
} 