package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.example.dawn.Dawn;
import com.example.dawn.controller.Controller;

public abstract class AppMenu implements Screen {
    protected Stage stage;
    protected final Controller controller;

    public AppMenu(Controller controller) {
        this.controller = controller;
    }
    
    @Override
    public abstract void show();

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Dawn.getBatch().begin();
        Dawn.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        controller.handleButtons();
    }

    @Override
    public void resize(int width, int height) {
        // stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
