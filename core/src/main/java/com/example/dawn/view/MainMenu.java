package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.Controller;
import com.example.dawn.controller.MainMenuController;
import com.example.dawn.models.App;

public class MainMenu extends AppMenu {
    private final TextButton startGameButton;
    private final TextButton loadGameButton;
    private final TextButton profileButton;
    private final TextButton settingsButton;
    private final TextButton logoutButton;
    private final TextButton exitButton;
    private final TextButton leaderboardButton;
    private final TextButton hintButton;
    private final Label usernameLabel;
    private final Label characterLabel;
    private final Label scoreLabel;

    public MainMenu(Controller controller, Skin skin) {
        super(controller);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.startGameButton = new TextButton("Start Game", skin);
        this.loadGameButton = new TextButton("Load Game", skin);
        this.profileButton = new TextButton("Profile", skin);
        this.leaderboardButton = new TextButton("Leaderboard", skin);
        this.settingsButton = new TextButton("Settings", skin);
        this.hintButton = new TextButton("Hint", skin);
        this.logoutButton = new TextButton("Logout", skin);
        this.exitButton = new TextButton("Exit", skin);
        this.usernameLabel = new Label(App.getInstance().getPlayer().getUsername(), skin);
        this.characterLabel = new Label(App.getInstance().getPlayer().getCharacter().getName(), skin);
        this.scoreLabel = new Label(String.valueOf(App.getInstance().getPlayer().getHighScore()), skin);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        
        ((MainMenuController) controller).startMenuMusic();

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Texture avatarTexture = new Texture(Gdx.files.internal(App.getInstance().getPlayer().getCharacter().getImagePath()));
        Image avatarImage = new Image(avatarTexture);

        Table playerInfoTable = new Table();

        playerInfoTable.add(avatarImage).padRight(10);

        Table textTable = new Table();
        textTable.add(usernameLabel).left();
        textTable.row();
        textTable.add(characterLabel).left();
        textTable.row();
        textTable.add(scoreLabel).left();

        playerInfoTable.add(textTable).left();
        playerInfoTable.pack();
        playerInfoTable.setTransform(true);
        playerInfoTable.setScale(1.5f);
        playerInfoTable.setOrigin(Align.topLeft);
        playerInfoTable.setPosition(10, Gdx.graphics.getHeight() - 10);

        mainTable.add(playerInfoTable).top().left().pad(10);

        usernameLabel.setText(App.getInstance().getPlayer().getUsername());
        characterLabel.setText(App.getInstance().getPlayer().getCharacter().getName());

        Table buttonTable = new Table();
        buttonTable.add(startGameButton).width(400).height(90).pad(10).row();
        buttonTable.add(loadGameButton).width(400).height(90).pad(10).row();
        buttonTable.add(profileButton).width(400).height(90).pad(10).row();
        buttonTable.add(leaderboardButton).width(400).height(90).pad(10).row();
        buttonTable.add(settingsButton).width(400).height(90).pad(10).row();
        buttonTable.add(hintButton).width(400).height(90).pad(10).row();
        buttonTable.add(logoutButton).width(400).height(90).pad(10).row();
        buttonTable.add(exitButton).width(400).height(90).pad(10).row();

        mainTable.row();
        mainTable.add(buttonTable).expand().center();

        stage.addActor(mainTable);

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((MainMenuController) controller).goToPregame();
            }
        });

        loadGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // load last game
            }
        });

        profileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((MainMenuController) controller).goToProfile();
            }
        });

        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((MainMenuController) controller).goToScoreboard();
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((MainMenuController) controller).goToSettings();
            }
        });
        
        hintButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // go to hint menu
            }
        });

        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((MainMenuController) controller).logout();
            }
        });
        
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((MainMenuController) controller).exitGame();
            }
        });
    }
}