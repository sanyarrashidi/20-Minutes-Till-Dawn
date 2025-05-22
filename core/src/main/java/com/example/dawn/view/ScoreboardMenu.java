package com.example.dawn.view;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.ScoreboardMenuController;
import com.example.dawn.models.App;
import com.example.dawn.models.Player;

public class ScoreboardMenu extends AppMenu {
    private final Skin skin;
    private final Label fontLabel;
    private final TextButton backButton;
    private final TextButton sortByUsernameButton;
    private final TextButton sortByWinsButton;
    private final TextButton sortByKillsButton;
    private final TextButton sortBySurvivalDurationButton;
    private final TextButton sortByScoreButton;
    private ScrollPane currentScrollPane;
    private ArrayList<Player> players;
    private Table mainTable;

    public ScoreboardMenu(ScoreboardMenuController controller, Skin skin) {
        super(controller);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin = skin;

        this.fontLabel = new Label("Scoreboard", skin);
        this.backButton = new TextButton("Back", skin);
        this.sortByUsernameButton = new TextButton("Username", skin);
        this.sortByWinsButton = new TextButton("Wins", skin);
        this.sortByKillsButton = new TextButton("Kills", skin);
        this.sortBySurvivalDurationButton = new TextButton("Survival", skin);
        this.sortByScoreButton = new TextButton("Score", skin);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        mainTable = new Table(skin);
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Table buttonHeaderTable = new Table(skin);
        buttonHeaderTable.add(fontLabel).colspan(5).center().padBottom(10);
        buttonHeaderTable.row();
        buttonHeaderTable.add(sortByUsernameButton).width(300).height(80).pad(5);
        buttonHeaderTable.add(sortByWinsButton).width(300).height(80).pad(5);
        buttonHeaderTable.add(sortByKillsButton).width(300).height(80).pad(5);
        buttonHeaderTable.add(sortBySurvivalDurationButton).width(300).height(80).pad(5);
        buttonHeaderTable.add(sortByScoreButton).width(300).height(80).pad(5);
        
        mainTable.add(buttonHeaderTable).top().pad(10);
        mainTable.row();
        
        this.players = ((ScoreboardMenuController) controller).getPlayersList();
        ((ScoreboardMenuController) controller).sortByScore(this.players);
        
        Table playerListTable = createPlayerTable(this.players);

        currentScrollPane = new ScrollPane(playerListTable, skin);
        currentScrollPane.setScrollingDisabled(true, false);
        currentScrollPane.setFadeScrollBars(false);
        currentScrollPane.setForceScroll(false, true);

        mainTable.add(currentScrollPane).expand().fill().pad(10);
        mainTable.row();
        mainTable.add(backButton).width(300).height(80).pad(10);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((ScoreboardMenuController) controller).back();
            }
        });

        sortByUsernameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((ScoreboardMenuController) controller).sortByUsername(players);
                Table newPlayerTable = createPlayerTable(players);
                currentScrollPane.setActor(newPlayerTable);
            }
        });

        sortByWinsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((ScoreboardMenuController) controller).sortByWins(players);
                Table newPlayerTable = createPlayerTable(players);
                currentScrollPane.setActor(newPlayerTable);
            }
        });
        
        sortByKillsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((ScoreboardMenuController) controller).sortByKills(players);
                Table newPlayerTable = createPlayerTable(players);
                currentScrollPane.setActor(newPlayerTable);
            }
        });

        sortBySurvivalDurationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((ScoreboardMenuController) controller).sortBySurvivalDuration(players);
                Table newPlayerTable = createPlayerTable(players);
                currentScrollPane.setActor(newPlayerTable);
            }
        });

        sortByScoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((ScoreboardMenuController) controller).sortByScore(players);
                Table newPlayerTable = createPlayerTable(players);
                currentScrollPane.setActor(newPlayerTable);
            }
        });
    }

    public Table createPlayerTable(ArrayList<Player> playersToDisplay) {
        Table playerTable = new Table(this.skin);

        playerTable.add("Rank").pad(10);
        playerTable.add("Username").pad(10);
        playerTable.add("Avatar").pad(10);
        playerTable.add("Wins").pad(10);
        playerTable.add("Kills").pad(10);
        playerTable.add("Survival").pad(10);
        playerTable.add("High Score").pad(10);
        playerTable.row();

        int rank = 1;
        if (playersToDisplay == null || playersToDisplay.isEmpty()) {
            playerTable.add("No data available").colspan(7).center().pad(20);
            return playerTable;
        }

        for (Player player : playersToDisplay) {
            Color rankColor = Color.WHITE;
            if (player.getUsername().equals(App.getInstance().getPlayer().getUsername())) rankColor = Color.SKY;
            else if (rank == 1) rankColor = Color.GOLD;
            else if (rank == 2) rankColor = Color.GRAY;
            else if (rank == 3) rankColor = new Color(0.6f, 0.3f, 0.1f, 1);

            playerTable.add("#" + rank, "font", rankColor).pad(5);
            playerTable.add(player.getUsername(), "font", rankColor).pad(5);
            
            if (player.getCharacter() != null && player.getCharacter().getImagePath() != null && !player.getCharacter().getImagePath().isEmpty()) {
                try {
                    Texture avatarTexture = new Texture(Gdx.files.internal(player.getCharacter().getImagePath()));
                    Image avatarImage = new Image(avatarTexture);
                    avatarImage.setScaling(Scaling.fit);
                    playerTable.add(avatarImage).size(60, 60).pad(5);
                } catch (Exception e) {
                    Gdx.app.error("ScoreboardMenu", "Failed to load avatar: " + player.getCharacter().getImagePath(), e);
                    playerTable.add("N/A").pad(5);
                }
            } else {
                playerTable.add("N/A").pad(5);
            }
            
            playerTable.add(String.valueOf(player.getWins()), "font", rankColor).pad(5);
            playerTable.add(String.valueOf(player.getKills()), "font", rankColor).pad(5);
            playerTable.add(String.valueOf(player.getSurvivalDuration()), "font", rankColor).pad(5);
            playerTable.add(String.valueOf(player.getHighScore()), "font", rankColor).pad(5);
            playerTable.row();
            
            rank++;
        }
        return playerTable;
    }
}