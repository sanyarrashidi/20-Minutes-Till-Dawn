package com.example.dawn.models;

public record Result(boolean success, String message) {
    public boolean isSuccessful() {
        return this.success;
    }
    
    @Override
    public String toString() {
        return this.message;
    }
}