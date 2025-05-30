package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.Controller;
import com.example.dawn.controller.HintMenuController;
import com.example.dawn.controller.ControlsMenuController;
import com.example.dawn.models.DatabaseManager;
import com.example.dawn.models.Ability;
import com.example.dawn.models.App;

import java.util.ArrayList;

public class HintMenu extends AppMenu {
    private final Label titleLabel;
    private final TextButton backButton;
    private final TextButton heroesButton;
    private final TextButton abilitiesButton;
    private final TextButton cheatCodesButton;
    private final TextButton controlsButton;
    private ScrollPane contentScrollPane;
    private Table contentTable;
    private Skin skin;

    public HintMenu(Controller controller, Skin skin) {
        super(controller);
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.titleLabel = new Label("Game Hints & Information", skin);
        this.backButton = new TextButton("Back to Main Menu", skin);
        this.heroesButton = new TextButton("Heroes", skin);
        this.abilitiesButton = new TextButton("Abilities", skin);
        this.cheatCodesButton = new TextButton("Cheat Codes", skin);
        this.controlsButton = new TextButton("Controls", skin);
        
        this.contentTable = new Table();
        this.contentScrollPane = new ScrollPane(contentTable, skin);
        this.contentScrollPane.setFadeScrollBars(false);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(Color.GOLD);
        mainTable.add(titleLabel).center().padBottom(30).row();

        
        Table buttonRow = new Table();
        buttonRow.add(heroesButton).width(200).height(70).pad(15);
        buttonRow.add(abilitiesButton).width(200).height(70).pad(15);
        buttonRow.add(cheatCodesButton).width(200).height(70).pad(15);
        buttonRow.add(controlsButton).width(200).height(70).pad(15);
        
        mainTable.add(buttonRow).center().padBottom(30).row();

        
        contentTable.top();
        contentScrollPane.setScrollingDisabled(true, false);
        mainTable.add(contentScrollPane).expand().fill().padBottom(20).row();

        
        mainTable.add(backButton).width(250).height(70).center();

        stage.addActor(mainTable);

        
        showHeroes();

        
        heroesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showHeroes();
            }
        });

        abilitiesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showAbilities();
            }
        });

        cheatCodesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCheatCodes();
            }
        });

        controlsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showControls();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((HintMenuController) controller).goToMainMenu();
            }
        });
    }

    private void showHeroes() {
        contentTable.clear();
        
        Label sectionTitle = new Label("Heroes", skin);
        sectionTitle.setFontScale(1.5f);
        sectionTitle.setColor(Color.CYAN);
        contentTable.add(sectionTitle).center().padBottom(20).row();

        DatabaseManager dbManager = ((HintMenuController) controller).getDatabaseManager();
        ArrayList<com.example.dawn.models.Character> characters = dbManager.getCharacters();

        for (com.example.dawn.models.Character character : characters) {
            Table heroCard = new Table();
            heroCard.setBackground(skin.getDrawable("window"));
            heroCard.pad(15);

            
            try {
                Texture avatarTexture = new Texture(Gdx.files.internal(character.getImagePath()));
                Image avatarImage = new Image(avatarTexture);
                heroCard.add(avatarImage).size(80, 80).padRight(20);
            } catch (Exception e) {
                Label noImageLabel = new Label("[NO IMAGE]", skin);
                heroCard.add(noImageLabel).size(80, 80).padRight(20);
            }

            
            Table infoTable = new Table();
            
            Label nameLabel = new Label(character.getName(), skin);
            nameLabel.setFontScale(1.2f);
            nameLabel.setColor(Color.WHITE);
            infoTable.add(nameLabel).left().row();

            Label hpLabel = new Label("HP: " + character.getHp(), skin);
            hpLabel.setColor(Color.RED);
            infoTable.add(hpLabel).left().row();

            Label speedLabel = new Label("Speed: " + character.getSpeed(), skin);
            speedLabel.setColor(Color.GREEN);
            infoTable.add(speedLabel).left().row();

            Label costLabel = new Label("Unlock Cost: " + character.getUnlockCost(), skin);
            costLabel.setColor(Color.YELLOW);
            infoTable.add(costLabel).left().row();

            Label descLabel = new Label(character.getDescription(), skin);
            descLabel.setWrap(true);
            descLabel.setColor(Color.LIGHT_GRAY);
            infoTable.add(descLabel).width(400).left().row();

            heroCard.add(infoTable).expand().fill().left();
            contentTable.add(heroCard).fillX().padBottom(10).row();
        }
    }

    private void showAbilities() {
        contentTable.clear();
        
        Label sectionTitle = new Label("Abilities", skin);
        sectionTitle.setFontScale(1.5f);
        sectionTitle.setColor(Color.CYAN);
        contentTable.add(sectionTitle).center().padBottom(20).row();

        Label introLabel = new Label("Abilities can be acquired during gameplay by leveling up:", skin);
        introLabel.setColor(Color.WHITE);
        contentTable.add(introLabel).left().padBottom(15).row();

        for (Ability ability : Ability.values()) {
            Table abilityCard = new Table();
            abilityCard.setBackground(skin.getDrawable("window"));
            abilityCard.pad(10);

            Label nameLabel = new Label(ability.getName(), skin);
            nameLabel.setFontScale(1.1f);
            nameLabel.setColor(ability.isTemporary() ? Color.ORANGE : Color.LIME);
            abilityCard.add(nameLabel).left().padBottom(5).row();

            Label descLabel = new Label(ability.getDescription(), skin);
            descLabel.setWrap(true);
            descLabel.setColor(Color.LIGHT_GRAY);
            abilityCard.add(descLabel).width(600).left().padBottom(5).row();

            if (ability.isTemporary()) {
                Label durationLabel = new Label("Duration: " + (int)ability.getDuration() + " seconds", skin);
                durationLabel.setColor(Color.ORANGE);
                abilityCard.add(durationLabel).left().row();
            } else {
                Label permanentLabel = new Label("Permanent effect", skin);
                permanentLabel.setColor(Color.LIME);
                abilityCard.add(permanentLabel).left().row();
            }

            contentTable.add(abilityCard).fillX().padBottom(10).row();
        }
    }

    private void showCheatCodes() {
        contentTable.clear();
        
        Label sectionTitle = new Label("Cheat Codes", skin);
        sectionTitle.setFontScale(1.5f);
        sectionTitle.setColor(Color.CYAN);
        contentTable.add(sectionTitle).center().padBottom(20).row();

        Label warningLabel = new Label("Warning: Cheat codes only work during gameplay!", skin);
        warningLabel.setColor(Color.RED);
        warningLabel.setFontScale(1.1f);
        contentTable.add(warningLabel).center().padBottom(15).row();

        String[] cheatCodes = {
            "T - Decrease time by 1 minute",
            "L - Level up player",
            "H - Increase health by 1",
            "B - Summon Elder boss",
            "K - Kill all enemies"
        };

        for (String cheat : cheatCodes) {
            Table cheatCard = new Table();
            cheatCard.setBackground(skin.getDrawable("window"));
            cheatCard.pad(10);

            String[] parts = cheat.split(" - ");
            if (parts.length == 2) {
                Label keyLabel = new Label(parts[0], skin);
                keyLabel.setFontScale(1.2f);
                keyLabel.setColor(Color.MAGENTA);
                cheatCard.add(keyLabel).left().padRight(20);

                Label descLabel = new Label(parts[1], skin);
                descLabel.setColor(Color.WHITE);
                cheatCard.add(descLabel).left().expand();
            } else {
                Label fullLabel = new Label(cheat, skin);
                fullLabel.setColor(Color.WHITE);
                cheatCard.add(fullLabel).left();
            }

            contentTable.add(cheatCard).fillX().padBottom(5).row();
        }
    }

    private String getKeyName(int keyCode) {
        return Input.Keys.toString(keyCode);
    }

    private void showControls() {
        contentTable.clear();
        
        Label sectionTitle = new Label("Game Controls", skin);
        sectionTitle.setFontScale(1.5f);
        sectionTitle.setColor(Color.CYAN);
        contentTable.add(sectionTitle).center().padBottom(20).row();

        Label introLabel = new Label("Current keyboard controls (can be customized in Settings):", skin);
        introLabel.setColor(Color.WHITE);
        contentTable.add(introLabel).left().padBottom(15).row();

        
        DatabaseManager dbManager = ((HintMenuController) controller).getDatabaseManager();
        ControlsMenuController controlsController = new ControlsMenuController(dbManager);
        
        String[] controls = {
            getKeyName(controlsController.getMoveUp()) + " - Move Up",
            getKeyName(controlsController.getMoveDown()) + " - Move Down", 
            getKeyName(controlsController.getMoveLeft()) + " - Move Left",
            getKeyName(controlsController.getMoveRight()) + " - Move Right",
            getKeyName(controlsController.getShoot()) + " - Shoot",
            getKeyName(controlsController.getReload()) + " - Reload",
            "ESC - Pause Game"
        };

        for (String control : controls) {
            Table controlCard = new Table();
            controlCard.setBackground(skin.getDrawable("window"));
            controlCard.pad(10);

            String[] parts = control.split(" - ");
            if (parts.length == 2) {
                Label keyLabel = new Label(parts[0], skin);
                keyLabel.setFontScale(1.2f);
                keyLabel.setColor(Color.YELLOW);
                controlCard.add(keyLabel).left().padRight(20);

                Label actionLabel = new Label(parts[1], skin);
                actionLabel.setColor(Color.WHITE);
                controlCard.add(actionLabel).left().expand();
            } else {
                Label fullLabel = new Label(control, skin);
                fullLabel.setColor(Color.WHITE);
                controlCard.add(fullLabel).left();
            }

            contentTable.add(controlCard).fillX().padBottom(5).row();
        }

        
        Label tipsTitle = new Label("Gameplay Tips:", skin);
        tipsTitle.setFontScale(1.2f);
        tipsTitle.setColor(Color.CYAN);
        contentTable.add(tipsTitle).left().padTop(20).padBottom(10).row();

        String[] tips = {
            "• Survive for 20 minutes to win the game",
            "• Kill enemies to gain XP and level up",
            "• Choose abilities when leveling up to become stronger",
            "• Watch your ammo - reload when safe",
            "• The Elder boss spawns as time runs out",
            "• Different heroes have unique special abilities"
        };

        for (String tip : tips) {
            Label tipLabel = new Label(tip, skin);
            tipLabel.setColor(Color.LIGHT_GRAY);
            contentTable.add(tipLabel).left().padBottom(3).row();
        }
    }
} 