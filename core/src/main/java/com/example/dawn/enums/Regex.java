package com.example.dawn.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum Regex {
    Password("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    private final String pattern;

    Regex(String pattern) {
        this.pattern = pattern;
    }
    
    public Matcher getMatcher(String input) {
        Matcher matcher = Pattern.compile(this.pattern).matcher(input);
        if (matcher.matches()) {
            return matcher;
        }
        return null;
    }
}