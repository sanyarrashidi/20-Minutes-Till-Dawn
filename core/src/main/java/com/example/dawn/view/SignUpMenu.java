package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.SignUpMenuController;

public class SignUpMenu extends AppMenu {
    private final TextButton registerButton;
    private final TextButton guestButton;
    private final Label welcomeMessage;
    private final TextField usernameField;
    private final TextField passwordField;
    private final TextField passwordConfirmField;
    public Table table;

    public SignUpMenu(SignUpMenuController controller, Skin skin) {
        super(controller);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        this.registerButton = new TextButton("Register", skin);
        this.guestButton = new TextButton("Play as Guest", skin);
        this.welcomeMessage = new Label("Think 20 minutes isn't that long?", skin);
        this.usernameField = new TextField("Your username", skin);
        this.passwordField = new TextField("Your password", skin);
        this.passwordConfirmField = new TextField("Confirm your password", skin);
        this.table = new Table();
    }

    @Override
    public void show() {
        table.setFillParent(true);
        table.center();

        table.add(welcomeMessage);
        table.row().pad(10, 0, 10, 0);
        table.add(usernameField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(passwordField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(passwordConfirmField).width(600);
        table.row().pad(10, 0, 20, 0);
        table.add(registerButton);
        table.row().pad(10, 0, 10, 0);
        table.add(guestButton);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resize'");
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hide'");
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispose'");
    }
}
