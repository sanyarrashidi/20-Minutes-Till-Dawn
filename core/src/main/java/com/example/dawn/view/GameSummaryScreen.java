package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.GameSummaryController;
import com.example.dawn.models.GameSummary;

public class GameSummaryScreen extends AppMenu {
    
    private final GameSummaryController controller;
    private final GameSummary gameSummary;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont headerFont;
    private BitmapFont bodyFont;
    private ShapeRenderer shapeRenderer;
    private Texture avatarTexture;
    
    public GameSummaryScreen(GameSummaryController controller, GameSummary gameSummary, Skin skin) {
        super(controller);
        this.controller = controller;
        this.gameSummary = gameSummary;
        this.stage = new Stage(new ScreenViewport());
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        
        headerFont = new BitmapFont();
        headerFont.getData().setScale(1.8f);
        
        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.2f);
        
        
        loadAvatarTexture();
        
        
        createUI(skin);
    }
    
    private void loadAvatarTexture() {
        try {
            if (gameSummary.getPlayerAvatarPath() != null && !gameSummary.getPlayerAvatarPath().isEmpty()) {
                avatarTexture = new Texture(Gdx.files.internal(gameSummary.getPlayerAvatarPath()));
            } else {
                
                avatarTexture = new Texture(Gdx.files.internal("characters/T_Character_0.png"));
            }
        } catch (Exception e) {
            System.out.println("Failed to load avatar texture: " + e.getMessage());
            avatarTexture = new Texture(Gdx.files.internal("characters/T_Character_0.png"));
        }
    }
    
    private void createUI(Skin skin) {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        
        
        Label titleLabel = new Label(gameSummary.isWin() ? "VICTORY!" : "GAME OVER", skin);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(gameSummary.isWin() ? Color.GREEN : Color.RED);
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();
        
        
        Label endReasonLabel = new Label(gameSummary.getEndReasonText(), skin);
        endReasonLabel.setFontScale(1.5f);
        endReasonLabel.setColor(Color.YELLOW);
        mainTable.add(endReasonLabel).colspan(2).padBottom(30).row();
        
        
        Table playerInfoTable = new Table();
        
        
        Label avatarLabel = new Label("", skin);
        playerInfoTable.add(avatarLabel).size(100, 100).padRight(30);
        
        
        Table detailsTable = new Table();
        Label playerLabel = new Label("Player: " + gameSummary.getPlayerUsername(), skin);
        playerLabel.setFontScale(1.3f);
        detailsTable.add(playerLabel).left().padBottom(10).row();
        
        Label characterLabel = new Label("Character: " + gameSummary.getCharacterName(), skin);
        characterLabel.setFontScale(1.3f);
        detailsTable.add(characterLabel).left().padBottom(10).row();
        
        Label survivalLabel = new Label("Survival Time: " + gameSummary.getFormattedSurvivalTime(), skin);
        survivalLabel.setFontScale(1.3f);
        detailsTable.add(survivalLabel).left().padBottom(10).row();
        
        playerInfoTable.add(detailsTable).left();
        mainTable.add(playerInfoTable).colspan(2).center().padBottom(40).row();
        
        
        Table statsTable = new Table();
        Label statsTitle = new Label("GAME STATISTICS", skin);
        statsTitle.setFontScale(1.8f);
        statsTitle.setColor(Color.CYAN);
        statsTable.add(statsTitle).colspan(2).center().padBottom(25).row();
        
        
        Table statsGrid = new Table();
        
        
        Label killsLabel = new Label("Kills: " + String.valueOf(gameSummary.getKills()), skin);
        killsLabel.setFontScale(1.4f);
        killsLabel.setColor(Color.RED);
        statsGrid.add(killsLabel).center().padRight(80).padBottom(15);
        
        Label scoreLabel = new Label("Score: " + String.valueOf(gameSummary.getScore()), skin);
        scoreLabel.setFontScale(1.4f);
        scoreLabel.setColor(Color.YELLOW);
        statsGrid.add(scoreLabel).center().padBottom(15).row();
        
        
        Label levelLabel = new Label("Final Level: " + String.valueOf(gameSummary.getFinalLevel()), skin);
        levelLabel.setFontScale(1.4f);
        levelLabel.setColor(Color.GREEN);
        statsGrid.add(levelLabel).center().padRight(80).padBottom(15);
        
        Label xpLabel = new Label("XP Gained: +" + gameSummary.getXpGained(), skin);
        xpLabel.setFontScale(1.4f);
        xpLabel.setColor(Color.MAGENTA);
        statsGrid.add(xpLabel).center().padBottom(15).row();
        
        statsTable.add(statsGrid).colspan(2).center().row();
        
        mainTable.add(statsTable).colspan(2).center().padBottom(50).row();
        
        
        Table buttonTable = new Table();
        
        TextButton playAgainButton = new TextButton("Play Again", skin);
        playAgainButton.getLabel().setFontScale(1.2f);
        playAgainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.playAgain();
            }
        });
        
        TextButton mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.getLabel().setFontScale(1.2f);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.returnToMainMenu();
            }
        });
        
        buttonTable.add(playAgainButton).padRight(30).width(180).height(60);
        buttonTable.add(mainMenuButton).width(180).height(60);
        
        mainTable.add(buttonTable).colspan(2).center();
        
        stage.addActor(mainTable);
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void render(float delta) {
        
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        
        drawBackground();
        
        
        drawCustomElements();
        
        
        stage.act(delta);
        stage.draw();
    }
    
    private void drawBackground() {
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        
        shapeRenderer.setColor(0.05f, 0.05f, 0.15f, 1f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        
        shapeRenderer.setColor(gameSummary.isWin() ? 0.2f : 0.3f, 
                              gameSummary.isWin() ? 0.4f : 0.1f, 
                              gameSummary.isWin() ? 0.2f : 0.1f, 0.8f);
        float borderWidth = 10f;
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), borderWidth); 
        shapeRenderer.rect(0, Gdx.graphics.getHeight() - borderWidth, Gdx.graphics.getWidth(), borderWidth); 
        shapeRenderer.rect(0, 0, borderWidth, Gdx.graphics.getHeight()); 
        shapeRenderer.rect(Gdx.graphics.getWidth() - borderWidth, 0, borderWidth, Gdx.graphics.getHeight()); 
        
        shapeRenderer.end();
    }
    
    private void drawCustomElements() {
        batch.begin();
        
        
        if (avatarTexture != null) {
            float avatarSize = 100f;
            float avatarX = Gdx.graphics.getWidth() / 2f - 180f; 
            float avatarY = Gdx.graphics.getHeight() / 2f + 80f; 
            batch.draw(avatarTexture, avatarX, avatarY, avatarSize, avatarSize);
        }
        
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        titleFont.dispose();
        headerFont.dispose();
        bodyFont.dispose();
        if (avatarTexture != null) {
            avatarTexture.dispose();
        }
    }
} 