package com.example.dawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager {
    private GameAssetManager instance;
    private Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

    private GameAssetManager() {
        instance = new GameAssetManager();
    }

    public GameAssetManager getInstance() {
        if (instance == null) instance = new GameAssetManager();
        return instance;
    }

    public Skin getSkin() {
        return this.skin;
    }
}
