package com.example.dawn;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.dawn.controller.SignUpMenuController;
import com.example.dawn.models.GameAssetManager;
import com.example.dawn.view.SignUpMenu;

/** This class serves only as the application scanning root. Any classes in its package (or any of the sub-packages)
 * with proper Autumn MVC annotations will be found, scanned and initiated. */
public class Dawn extends Game {
    /** Default application size. */
    public static final int WIDTH = 450, HEIGHT = 600;
    private static Dawn dawn;
    private static SpriteBatch batch;
    
    @Override
    public void create() {
        dawn = this;
        batch = new SpriteBatch();
        dawn.setScreen(new SignUpMenu(new SignUpMenuController(), GameAssetManager.getInstance().getSkin()));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public static SpriteBatch getBatch() {
        return batch;
    }
}