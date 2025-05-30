package com.example.dawn.view;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.PregameMenuController;
import com.example.dawn.models.Character;
import com.example.dawn.models.Weapon;

public class PregameMenu extends AppMenu {
    private final Skin skin;
    private final PregameMenuController pregameController;
    
    
    private final Label titleLabel;
    private final Label characterLabel;
    private final Label weaponLabel;
    private final Label gameTimeLabel;
    private final Label selectedCharacterLabel;
    private final Label selectedWeaponLabel;
    private final Label selectedTimeLabel;
    private final TextButton startButton;
    private final TextButton backButton;
    
    
    private ButtonGroup<TextButton> characterButtonGroup;
    private ButtonGroup<TextButton> weaponButtonGroup;
    private ButtonGroup<TextButton> timeButtonGroup;

    public PregameMenu(PregameMenuController controller, Skin skin) {
        super(controller);
        this.stage = new Stage(new ScreenViewport());
        this.skin = skin;
        this.pregameController = controller;

        
        titleLabel = new Label("Ready for the Dark?", skin);
        titleLabel.setFontScale(2f);
        titleLabel.setColor(Color.RED);

        characterLabel = new Label("Choose Character:", skin);
        characterLabel.setFontScale(1.5f);
        characterLabel.setColor(Color.GOLD);

        weaponLabel = new Label("Choose Weapon:", skin);
        weaponLabel.setFontScale(1.5f);
        weaponLabel.setColor(Color.GOLD);

        gameTimeLabel = new Label("Game Duration:", skin);
        gameTimeLabel.setFontScale(1.5f);
        gameTimeLabel.setColor(Color.GOLD);

        selectedCharacterLabel = new Label("Selected: " + 
            (controller.getSelectedCharacter() != null ? controller.getSelectedCharacter().getName() : "None"), skin);
        selectedCharacterLabel.setColor(Color.WHITE);

        selectedWeaponLabel = new Label("Selected: " + 
            (controller.getSelectedWeapon() != null ? controller.getSelectedWeapon().getName() : "None"), skin);
        selectedWeaponLabel.setColor(Color.WHITE);

        selectedTimeLabel = new Label("Selected: " + controller.getSelectedGameDuration() + " minutes", skin);
        selectedTimeLabel.setColor(Color.WHITE);

        startButton = new TextButton("Start Game", skin);
        startButton.getLabel().setFontScale(1.3f);
        
        backButton = new TextButton("Back", skin);
        backButton.getLabel().setFontScale(1.3f);

        
        characterButtonGroup = new ButtonGroup<>();
        weaponButtonGroup = new ButtonGroup<>();
        timeButtonGroup = new ButtonGroup<>();
        
        characterButtonGroup.setMaxCheckCount(1);
        characterButtonGroup.setMinCheckCount(1);
        weaponButtonGroup.setMaxCheckCount(1);
        weaponButtonGroup.setMinCheckCount(1);
        timeButtonGroup.setMaxCheckCount(1);
        timeButtonGroup.setMinCheckCount(1);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(skin.getDrawable("window"));
        mainTable.pad(15);
        stage.addActor(mainTable);

        
        Table titleTable = new Table();
        titleTable.setBackground(skin.getDrawable("button"));
        titleTable.pad(20);
        titleTable.add(titleLabel).center();
        
        mainTable.add(titleTable).colspan(3).center().padBottom(25);
        mainTable.row();

        
        
        Table leftSection = new Table();
        leftSection.setBackground(skin.getDrawable("window"));
        leftSection.pad(15);
        
        Table characterHeaderTable = new Table();
        characterHeaderTable.setBackground(skin.getDrawable("button"));
        characterHeaderTable.pad(12);
        characterHeaderTable.add(characterLabel).center();
        
        leftSection.add(characterHeaderTable).fillX().padBottom(15);
        leftSection.row();
        leftSection.add(createCharacterSelectionSection()).fillX().padBottom(15);
        leftSection.row();
        
        Table selectedCharTable = new Table();
        selectedCharTable.setBackground(skin.getDrawable("button"));
        selectedCharTable.pad(10);
        selectedCharTable.add(selectedCharacterLabel).center();
        leftSection.add(selectedCharTable).fillX();

        
        Table middleSection = new Table();
        middleSection.setBackground(skin.getDrawable("window"));
        middleSection.pad(15);
        
        Table weaponHeaderTable = new Table();
        weaponHeaderTable.setBackground(skin.getDrawable("button"));
        weaponHeaderTable.pad(12);
        weaponHeaderTable.add(weaponLabel).center();
        
        middleSection.add(weaponHeaderTable).fillX().padBottom(15);
        middleSection.row();
        middleSection.add(createWeaponSelectionSection()).fillX().padBottom(15);
        middleSection.row();
        
        Table selectedWeaponTable = new Table();
        selectedWeaponTable.setBackground(skin.getDrawable("button"));
        selectedWeaponTable.pad(10);
        selectedWeaponTable.add(selectedWeaponLabel).center();
        middleSection.add(selectedWeaponTable).fillX();

        
        Table rightSection = new Table();
        rightSection.setBackground(skin.getDrawable("window"));
        rightSection.pad(15);
        
        Table timeHeaderTable = new Table();
        timeHeaderTable.setBackground(skin.getDrawable("button"));
        timeHeaderTable.pad(12);
        timeHeaderTable.add(gameTimeLabel).center();
        
        rightSection.add(timeHeaderTable).fillX().padBottom(15);
        rightSection.row();
        rightSection.add(createTimeSelectionSection()).fillX().padBottom(15);
        rightSection.row();
        
        Table selectedTimeTable = new Table();
        selectedTimeTable.setBackground(skin.getDrawable("button"));
        selectedTimeTable.pad(10);
        selectedTimeTable.add(selectedTimeLabel).center();
        rightSection.add(selectedTimeTable).fillX().padBottom(25);
        rightSection.row();

        
        Table buttonTable = new Table();
        buttonTable.setBackground(skin.getDrawable("button"));
        buttonTable.pad(25);
        
        
        startButton.getLabel().setFontScale(1.6f);
        backButton.getLabel().setFontScale(1.6f);
        
        buttonTable.add(backButton).width(280).height(100).padBottom(15);
        buttonTable.row();
        buttonTable.add(startButton).width(280).height(100);
        
        rightSection.add(buttonTable).center();

        
        mainTable.add(leftSection).width(480).fillY().padRight(15);
        mainTable.add(middleSection).width(450).fillY().padRight(15);
        mainTable.add(rightSection).width(400).fillY();

        setupButtonListeners();
    }

    private Table createCharacterSelectionSection() {
        Table characterTable = new Table();
        characterTable.setBackground(skin.getDrawable("window"));
        characterTable.pad(25);
        
        ArrayList<Character> characters = pregameController.getCharacters();
        int charactersPerRow = 1;
        int currentCount = 0;

        for (Character character : characters) {
            Table characterCard = new Table();
            characterCard.setBackground(skin.getDrawable("button"));
            characterCard.pad(50);
            
            
            Texture avatarTexture = new Texture(Gdx.files.internal(character.getImagePath()));
            Image avatarImage = new Image(avatarTexture);
            avatarImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            
            
            Label nameLabel = new Label(character.getName(), skin);
            nameLabel.setColor(Color.WHITE);
            nameLabel.setFontScale(1.6f);
            
            
            Label hpLabel = new Label("HP: " + character.getHp(), skin);
            hpLabel.setColor(Color.GREEN);
            hpLabel.setFontScale(1.4f);
            
            
            TextButton selectButton = new TextButton("Select", skin, "toggle");
            selectButton.setUserObject(character);
            selectButton.getLabel().setFontScale(1.5f);
            characterButtonGroup.add(selectButton);
            
            
            if (character == pregameController.getSelectedCharacter()) {
                selectButton.setChecked(true);
            }
            
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    pregameController.selectCharacter(character);
                    selectedCharacterLabel.setText("Selected: " + character.getName());
                }
            });

            
            characterCard.add(avatarImage).size(130, 130).padBottom(15);
            characterCard.row();
            characterCard.add(nameLabel).padBottom(12);
            characterCard.row();
            characterCard.add(hpLabel).padBottom(18);
            characterCard.row();
            characterCard.add(selectButton).width(180).height(60);
            
            characterTable.add(characterCard).pad(15).width(220).height(300);
            currentCount++;
            
            if (currentCount % charactersPerRow == 0) {
                characterTable.row();
            }
        }

        ScrollPane characterScrollPane = new ScrollPane(characterTable, skin);
        characterScrollPane.setScrollingDisabled(false, false);
        characterScrollPane.setFadeScrollBars(false);
        
        Table containerTable = new Table();
        containerTable.setBackground(skin.getDrawable("window"));
        containerTable.pad(15);
        containerTable.add(characterScrollPane).height(380).fillX();
        
        return containerTable;
    }

    private Table createWeaponSelectionSection() {
        Table weaponTable = new Table();
        weaponTable.setBackground(skin.getDrawable("window"));
        weaponTable.pad(25);
        
        ArrayList<Weapon> weapons = pregameController.getWeapons();

        
        for (Weapon weapon : weapons) {
            Table weaponCard = new Table();
            weaponCard.setBackground(skin.getDrawable("button"));
            weaponCard.pad(50); 
            
            
            Label nameLabel = new Label(weapon.getName(), skin);
            nameLabel.setColor(Color.WHITE);
            nameLabel.setFontScale(2.0f); 
            
            Label damageLabel = new Label("Damage: " + weapon.getDamage(), skin);
            damageLabel.setColor(Color.RED);
            damageLabel.setFontScale(1.6f); 
            
            Label projectileLabel = new Label("Projectiles: " + weapon.getProjectile(), skin);
            projectileLabel.setColor(Color.YELLOW);
            projectileLabel.setFontScale(1.6f);
            
            Label reloadLabel = new Label("Reload Time: " + weapon.getReloadTime() + "s", skin);
            reloadLabel.setColor(Color.CYAN);
            reloadLabel.setFontScale(1.6f);
            
            Label magazineLabel = new Label("Magazine: " + weapon.getMagazineSize(), skin);
            magazineLabel.setColor(Color.GREEN);
            magazineLabel.setFontScale(1.6f);
            
            
            TextButton selectButton = new TextButton("Select", skin, "toggle");
            selectButton.setUserObject(weapon);
            selectButton.getLabel().setFontScale(1.6f); 
            weaponButtonGroup.add(selectButton);
            
            
            if (weapon == pregameController.getSelectedWeapon()) {
                selectButton.setChecked(true);
            }
            
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    pregameController.selectWeapon(weapon);
                    selectedWeaponLabel.setText("Selected: " + weapon.getName());
                }
            });

            
            weaponCard.add(nameLabel).padBottom(20); 
            weaponCard.row();
            weaponCard.add(damageLabel).left().padBottom(12);
            weaponCard.row();
            weaponCard.add(projectileLabel).left().padBottom(12);
            weaponCard.row();
            weaponCard.add(reloadLabel).left().padBottom(12);
            weaponCard.row();
            weaponCard.add(magazineLabel).left().padBottom(20);
            weaponCard.row();
            weaponCard.add(selectButton).width(250).height(70); 
            
            weaponTable.add(weaponCard).pad(80).fillX().height(280); 
            weaponTable.row();
        }

        
        ScrollPane weaponScrollPane = new ScrollPane(weaponTable, skin);
        weaponScrollPane.setScrollingDisabled(false, false); 
        weaponScrollPane.setFadeScrollBars(false);
        
        Table containerTable = new Table();
        containerTable.setBackground(skin.getDrawable("window"));
        containerTable.pad(15);
        
        containerTable.add(weaponScrollPane).height(380).fillX(); 
        
        return containerTable;
    }

    private Table createTimeSelectionSection() {
        Table timeTable = new Table();
        timeTable.setBackground(skin.getDrawable("window"));
        timeTable.pad(25);
        
        int[] durations = {2, 5, 10, 20}; 
        
        
        int count = 0;
        for (int duration : durations) {
            TextButton timeButton = new TextButton(duration + " min", skin, "toggle");
            timeButton.setUserObject(duration);
            timeButton.getLabel().setFontScale(1.4f);
            timeButtonGroup.add(timeButton);
            
            
            if (duration == pregameController.getSelectedGameDuration()) {
                timeButton.setChecked(true);
            }
            
            timeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    pregameController.selectGameDuration(duration);
                    selectedTimeLabel.setText("Selected: " + duration + " minutes");
                }
            });
            
            timeTable.add(timeButton).width(160).height(80).pad(12);
            count++;
            if (count % 2 == 0) {
                timeTable.row();
            }
        }

        Table containerTable = new Table();
        containerTable.setBackground(skin.getDrawable("window"));
        containerTable.pad(10);
        containerTable.add(timeTable).fillX();
        
        return containerTable;
    }

    private void setupButtonListeners() {
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pregameController.startGame();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pregameController.goBack();
            }
        });
    }
}