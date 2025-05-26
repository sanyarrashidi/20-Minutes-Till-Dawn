package com.example.dawn.models;

public class GameSummary {
    private String playerUsername;
    private String playerAvatarPath;
    private String characterName;
    private int kills;
    private int score;
    private int finalLevel;
    private int finalXp;
    private int xpGained;
    private int survivalTimeSeconds;
    private GameEndReason endReason;
    private boolean isWin;
    
    public enum GameEndReason {
        TIME_UP,        // Player survived the full duration (win)
        PLAYER_DIED,    // Player's HP reached 0 (loss)
        GAVE_UP         // Player quit from pause menu (loss)
    }
    
    public GameSummary() {
    }
    
    public GameSummary(String playerUsername, String playerAvatarPath, String characterName, 
                      int kills, int score, int finalLevel, int finalXp, int xpGained,
                      int survivalTimeSeconds, GameEndReason endReason, boolean isWin) {
        this.playerUsername = playerUsername;
        this.playerAvatarPath = playerAvatarPath;
        this.characterName = characterName;
        this.kills = kills;
        this.score = score;
        this.finalLevel = finalLevel;
        this.finalXp = finalXp;
        this.xpGained = xpGained;
        this.survivalTimeSeconds = survivalTimeSeconds;
        this.endReason = endReason;
        this.isWin = isWin;
    }
    
    public String getPlayerUsername() {
        return playerUsername;
    }
    
    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }
    
    public String getPlayerAvatarPath() {
        return playerAvatarPath;
    }
    
    public void setPlayerAvatarPath(String playerAvatarPath) {
        this.playerAvatarPath = playerAvatarPath;
    }
    
    public String getCharacterName() {
        return characterName;
    }
    
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
    
    public int getKills() {
        return kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getFinalLevel() {
        return finalLevel;
    }
    
    public void setFinalLevel(int finalLevel) {
        this.finalLevel = finalLevel;
    }
    
    public int getFinalXp() {
        return finalXp;
    }
    
    public void setFinalXp(int finalXp) {
        this.finalXp = finalXp;
    }
    
    public int getXpGained() {
        return xpGained;
    }
    
    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
    }
    
    public int getSurvivalTimeSeconds() {
        return survivalTimeSeconds;
    }
    
    public void setSurvivalTimeSeconds(int survivalTimeSeconds) {
        this.survivalTimeSeconds = survivalTimeSeconds;
    }
    
    public GameEndReason getEndReason() {
        return endReason;
    }
    
    public void setEndReason(GameEndReason endReason) {
        this.endReason = endReason;
    }
    
    public boolean isWin() {
        return isWin;
    }
    
    public void setWin(boolean win) {
        isWin = win;
    }
    
    public String getFormattedSurvivalTime() {
        int minutes = survivalTimeSeconds / 60;
        int seconds = survivalTimeSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public String getEndReasonText() {
        switch (endReason) {
            case TIME_UP:
                return "Victory! Time survived!";
            case PLAYER_DIED:
                return "Defeat! Player died";
            case GAVE_UP:
                return "Game abandoned";
            default:
                return "Game ended";
        }
    }
} 