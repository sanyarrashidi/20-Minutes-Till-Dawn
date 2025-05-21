package com.example.dawn.models;

public class Player {
    private String username;
    private String password;
    private String securityAnswer;

    public Player() {
    }

    public Player(String username, String password, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.securityAnswer = securityAnswer;
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
} 