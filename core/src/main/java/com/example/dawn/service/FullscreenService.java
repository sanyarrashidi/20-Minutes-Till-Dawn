package com.example.dawn.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.kiwi.util.common.Strings;
import com.example.dawn.Dawn;


@Component
public class FullscreenService {
    
    public DisplayMode[] getDisplayModes() {
        return Gdx.graphics.getDisplayModes();
    }

    public String serialize(final DisplayMode displayMode) {
        return displayMode.width + "x" + displayMode.height;
    }

    public DisplayMode deserialize(final String displayMode) {
        final String[] sizes = Strings.split(displayMode, 'x');
        final int width = Integer.parseInt(sizes[0]);
        final int height = Integer.parseInt(sizes[1]);
        for (final DisplayMode mode : Gdx.graphics.getDisplayModes()) {
            if (mode.width == width && mode.height == height) {
                return mode;
            }
        }
        return null;
    }

    
    public void setFullscreen(final DisplayMode displayMode) {
        if (Gdx.graphics.setFullscreenMode(displayMode)) {
            
            Gdx.app.getApplicationListener().resize(displayMode.width, displayMode.height);
        }
    }

    
    public void resetFullscreen() {
        if (Gdx.graphics.setWindowedMode(Dawn.WIDTH, Dawn.HEIGHT)) {
            
            Gdx.app.getApplicationListener().resize(Dawn.WIDTH, Dawn.HEIGHT);
        }
    }
}