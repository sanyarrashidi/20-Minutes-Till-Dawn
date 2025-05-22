package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.LoginMenuController;
import com.example.dawn.models.Result;


public class LoginMenu extends AppMenu {
    private final TextButton loginButton;
    private final TextButton goToSignUpButton;
    private final TextButton forgotPasswordButton;
    private final TextButton resetPasswordButton;
    private final Label errorMessage;
    private final Label welcomeMessage;
    private final Label forgotPasswordMessage;
    private final TextField usernameField;
    private final TextField passwordField;
    private final TextField passwordConfirmField;
    private final TextField securityQuestionField;
    private final Table table;

    public LoginMenu(LoginMenuController controller, Skin skin) {
        super(controller);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.loginButton = new TextButton("Login", skin);
        this.goToSignUpButton = new TextButton("Go to Sign Up", skin);
        this.forgotPasswordButton = new TextButton("Forgot Password", skin);
        this.resetPasswordButton = new TextButton("Reset Password", skin);
        this.welcomeMessage = new Label("Welcome back to Dawn!", skin);
        this.forgotPasswordMessage = new Label("Answer your security question to reset your password", skin);
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
        table.add(loginButton);
        table.row().pad(10, 0, 10, 0);
        table.add(forgotPasswordButton);
        table.row().pad(10, 0, 10, 0);
        table.add(goToSignUpButton);
        
        stage.addActor(table);

        this.passwordField.setPasswordMode(true);
        this.passwordField.setPasswordCharacter('*');

        this.loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result result = ((LoginMenuController) controller).login(usernameField.getText(), passwordField.getText());
                if (!result.isSuccessful()) {
                    errorMessage.setText(result.toString());
                    usernameField.setText("");
                    passwordField.setText("");
                    usernameField.setMessageText("Your username");
                    passwordField.setMessageText("Your password");
                }
                else {
                    ((LoginMenuController) controller).goToMainMenu();
                }
            }
        });

        this.goToSignUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((LoginMenuController) controller).goToSignUp();
            }
        });

        this.forgotPasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showForgotPassword();
            }
        });
        
    }
            
    public void showForgotPassword() {
        passwordField.setText("");
        passwordField.setMessageText("Your password");
        table.clear();

        table.add(errorMessage);
        table.row().pad(10, 0, 10, 0);
        table.add(forgotPasswordMessage);
        table.row().pad(10, 0, 10, 0);
        table.add(usernameField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(securityQuestionField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(passwordField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(passwordConfirmField).width(600);
        table.row().pad(10, 0, 10, 0);
        table.add(resetPasswordButton);
        
        stage.addActor(table);

        this.passwordField.setPasswordMode(true);
        this.passwordField.setPasswordCharacter('*');
        this.passwordConfirmField.setPasswordMode(true);
        this.passwordConfirmField.setPasswordCharacter('*');

        this.resetPasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result result = ((LoginMenuController) controller).forgotPassword(usernameField.getText(), securityQuestionField.getText(), passwordField.getText(), passwordConfirmField.getText());
                if (!result.isSuccessful()) {
                    errorMessage.setText(result.toString());
                    usernameField.setText("");
                    securityQuestionField.setText("");
                    passwordField.setText("");
                    passwordConfirmField.setText("");
                    usernameField.setMessageText("Your username");
                    securityQuestionField.setMessageText("What is your favorite football team?");
                    passwordField.setMessageText("Your password");
                    passwordConfirmField.setMessageText("Confirm your password");
                }
                else {
                    ((LoginMenuController) controller).goToMainMenu();
                }
            }
        });
    }
}