package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.SignUpMenuController;
import com.example.dawn.models.Result;
import com.example.dawn.service.EnhancedMusicService;


public class SignUpMenu extends AppMenu {
    private final TextButton registerButton;
    private final TextButton guestButton;
    private final TextButton goToLoginButton;
    private final Label welcomeMessage;
    private final Label errorMessage;
    private final TextField usernameField;
    private final TextField passwordField;
    private final TextField passwordConfirmField;
    private final TextField securityQuestionField;
    public Table table;
    private final EnhancedMusicService enhancedMusicService;

    public SignUpMenu(SignUpMenuController controller, Skin skin) {
        super(controller);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.enhancedMusicService = controller.getEnhancedMusicService();
        
        this.registerButton = new TextButton("Register", skin);
        this.guestButton = new TextButton("Play as Guest", skin);
        this.goToLoginButton = new TextButton("Go to Login", skin);
        this.welcomeMessage = new Label("Think 20 minutes isn't that long?", skin);
        this.errorMessage = new Label("", skin);
        this.usernameField = new TextField("", skin);
        this.usernameField.setMessageText("Your username");
        this.passwordField = new TextField("", skin);
        this.passwordField.setMessageText("Your password");
        this.passwordConfirmField = new TextField("", skin);
        this.passwordConfirmField.setMessageText("Confirm your password");
        this.securityQuestionField = new TextField("", skin);
        this.securityQuestionField.setMessageText("What is your favorite football team?");
        this.table = new Table();
        
        this.errorMessage.setColor(Color.RED);
    }

    @Override
    public void show() {
        
        if (enhancedMusicService != null) {
            enhancedMusicService.switchToMenuMusic();
        }

        table.setFillParent(true);
        table.center();

        table.add(errorMessage);
        table.row().pad(10, 0, 10, 0);
        table.add(welcomeMessage);
        table.row().pad(10, 0, 10, 0);
        table.add(usernameField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(passwordField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(passwordConfirmField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(securityQuestionField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(registerButton);
        table.row().pad(10, 0, 10, 0);
        table.add(guestButton);
        table.row().pad(10, 0, 10, 0);
        table.add(goToLoginButton);

        stage.addActor(table);

        this.passwordField.setPasswordMode(true);
        this.passwordConfirmField.setPasswordMode(true);
        this.passwordField.setPasswordCharacter('*');
        this.passwordConfirmField.setPasswordCharacter('*');

        registerButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
                Result result = ((SignUpMenuController) controller).register(usernameField.getText(), passwordField.getText(), passwordConfirmField.getText(), securityQuestionField.getText());
                if (!result.isSuccessful()) {
                    errorMessage.setText(result.toString());

                    usernameField.setText("");
                    passwordField.setText("");
                    passwordConfirmField.setText("");
                    securityQuestionField.setText("");
                    usernameField.setMessageText("Your username");
                    passwordField.setMessageText("Your password");
                    passwordConfirmField.setMessageText("Confirm your password");
                    securityQuestionField.setMessageText("What is your favorite football team?");
                }
                else {
                    ((SignUpMenuController) controller).goToMainMenu();
                }
            }
        });

        guestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((SignUpMenuController) controller).guestLogin();
            }
        });

        goToLoginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((SignUpMenuController) controller).goToLogin();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }
}
