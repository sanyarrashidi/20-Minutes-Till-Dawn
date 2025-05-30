package com.example.dawn.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.ControlsMenuController;

public class ControlsMenu extends AppMenu {
    
    private final Skin skin;
    private final ControlsMenuController controlsController;
    
    
    private final Label titleLabel;
    private final Label statusLabel;
    
    
    private final TextButton moveUpButton;
    private final TextButton moveDownButton;
    private final TextButton moveLeftButton;
    private final TextButton moveRightButton;
    private final TextButton reloadButton;
    private final TextButton shootButton;
    private final TextButton sprintButton;
    
    private final TextButton resetDefaultsButton;
    private final TextButton backButton;
    
    
    private int currentMoveUp;
    private int currentMoveDown;
    private int currentMoveLeft;
    private int currentMoveRight;
    private int currentReload;
    private int currentShoot;
    private int currentSprint;
    
    
    private TextButton waitingForInput = null;
    
    public ControlsMenu(ControlsMenuController controller, Skin skin) {
        super(controller);
        this.skin = skin;
        this.controlsController = controller;
        this.stage = new Stage(new ScreenViewport());
        
        this.titleLabel = new Label("Controls Settings", skin);
        this.titleLabel.setColor(Color.GOLD);
        
        this.statusLabel = new Label("Click on a control to change it, then press the desired key", skin);
        this.statusLabel.setColor(Color.WHITE);
        
        
        this.moveUpButton = new TextButton("Move Up: W", skin);
        this.moveDownButton = new TextButton("Move Down: S", skin);
        this.moveLeftButton = new TextButton("Move Left: A", skin);
        this.moveRightButton = new TextButton("Move Right: D", skin);
        this.reloadButton = new TextButton("Reload: R", skin);
        this.shootButton = new TextButton("Shoot: SPACE", skin);
        this.sprintButton = new TextButton("Sprint: LSHIFT", skin);
        
        this.resetDefaultsButton = new TextButton("Reset to Defaults", skin);
        this.backButton = new TextButton("Back", skin);
        
        
        loadCurrentControls();
    }
    
    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        
        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);
        mainTable.center();
        
        
        titleLabel.setFontScale(1.8f);
        mainTable.add(titleLabel).colspan(2).center().padBottom(40);
        mainTable.row();
        
        
        statusLabel.setFontScale(1.0f);
        mainTable.add(statusLabel).colspan(2).center().padBottom(30);
        mainTable.row();
        
        
        addControlRow(mainTable, "Movement Controls:", null);
        addControlRow(mainTable, null, moveUpButton);
        addControlRow(mainTable, null, moveDownButton);
        addControlRow(mainTable, null, moveLeftButton);
        addControlRow(mainTable, null, moveRightButton);
        
        addControlRow(mainTable, "Action Controls:", null);
        addControlRow(mainTable, null, reloadButton);
        addControlRow(mainTable, null, shootButton);
        addControlRow(mainTable, null, sprintButton);
        
        
        mainTable.add(new Label("", skin)).colspan(2).padTop(30);
        mainTable.row();
        
        resetDefaultsButton.getLabel().setFontScale(1.2f);
        mainTable.add(resetDefaultsButton).colspan(2).center().width(500).height(80).padBottom(20);
        mainTable.row();
        
        backButton.getLabel().setFontScale(1.3f);
        mainTable.add(backButton).colspan(2).center().width(300).height(80);
        
        stage.addActor(mainTable);
        
        setupListeners();
        updateButtonTexts();
    }
    
    private void addControlRow(Table table, String label, TextButton button) {
        if (label != null) {
            Label sectionLabel = new Label(label, skin);
            sectionLabel.setFontScale(1.4f);
            sectionLabel.setColor(Color.CYAN);
            table.add(sectionLabel).colspan(2).center().padTop(20).padBottom(15);
            table.row();
        }
        
        if (button != null) {
            button.getLabel().setFontScale(1.1f);
            table.add(button).colspan(2).center().width(400).height(50).padBottom(10);
            table.row();
        }
    }
    
    private void loadCurrentControls() {
        currentMoveUp = controlsController.getMoveUp();
        currentMoveDown = controlsController.getMoveDown();
        currentMoveLeft = controlsController.getMoveLeft();
        currentMoveRight = controlsController.getMoveRight();
        currentReload = controlsController.getReload();
        currentShoot = controlsController.getShoot();
        currentSprint = controlsController.getSprint();
    }
    
    private void updateButtonTexts() {
        moveUpButton.setText("Move Up: " + Input.Keys.toString(currentMoveUp));
        moveDownButton.setText("Move Down: " + Input.Keys.toString(currentMoveDown));
        moveLeftButton.setText("Move Left: " + Input.Keys.toString(currentMoveLeft));
        moveRightButton.setText("Move Right: " + Input.Keys.toString(currentMoveRight));
        reloadButton.setText("Reload: " + Input.Keys.toString(currentReload));
        shootButton.setText("Shoot: " + Input.Keys.toString(currentShoot));
        sprintButton.setText("Sprint: " + Input.Keys.toString(currentSprint));
    }
    
    private void setupListeners() {
        
        setupControlButton(moveUpButton, "Move Up");
        setupControlButton(moveDownButton, "Move Down");
        setupControlButton(moveLeftButton, "Move Left");
        setupControlButton(moveRightButton, "Move Right");
        setupControlButton(reloadButton, "Reload");
        setupControlButton(shootButton, "Shoot");
        setupControlButton(sprintButton, "Sprint");
        
        resetDefaultsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetToDefaults();
            }
        });
        
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveAndGoBack();
            }
        });
    }
    
    private void setupControlButton(TextButton button, String controlName) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startWaitingForInput(button, controlName);
            }
        });
    }
    
    private void startWaitingForInput(TextButton button, String controlName) {
        waitingForInput = button;
        statusLabel.setText("Press a key for " + controlName + " (ESC to cancel)");
        statusLabel.setColor(Color.YELLOW);
        
        
        stage.setKeyboardFocus(null);
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    cancelInput();
                    return true;
                }
                
                setControl(button, keycode);
                return true;
            }
        });
    }
    
    private void setControl(TextButton button, int keycode) {
        if (button == moveUpButton) currentMoveUp = keycode;
        else if (button == moveDownButton) currentMoveDown = keycode;
        else if (button == moveLeftButton) currentMoveLeft = keycode;
        else if (button == moveRightButton) currentMoveRight = keycode;
        else if (button == reloadButton) currentReload = keycode;
        else if (button == shootButton) currentShoot = keycode;
        else if (button == sprintButton) currentSprint = keycode;
        
        updateButtonTexts();
        finishInput();
    }
    
    private void cancelInput() {
        finishInput();
        statusLabel.setText("Input cancelled");
        statusLabel.setColor(Color.RED);
    }
    
    private void finishInput() {
        waitingForInput = null;
        Gdx.input.setInputProcessor(stage);
        
        
        stage.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
            com.badlogic.gdx.scenes.scene2d.actions.Actions.delay(2f),
            com.badlogic.gdx.scenes.scene2d.actions.Actions.run(() -> {
                statusLabel.setText("Click on a control to change it, then press the desired key");
                statusLabel.setColor(Color.WHITE);
            })
        ));
    }
    
    private void resetToDefaults() {
        currentMoveUp = ControlsMenuController.DEFAULT_MOVE_UP;
        currentMoveDown = ControlsMenuController.DEFAULT_MOVE_DOWN;
        currentMoveLeft = ControlsMenuController.DEFAULT_MOVE_LEFT;
        currentMoveRight = ControlsMenuController.DEFAULT_MOVE_RIGHT;
        currentReload = ControlsMenuController.DEFAULT_RELOAD;
        currentShoot = ControlsMenuController.DEFAULT_SHOOT;
        currentSprint = ControlsMenuController.DEFAULT_SPRINT;
        
        updateButtonTexts();
        statusLabel.setText("Controls reset to defaults");
        statusLabel.setColor(Color.GREEN);
    }
    
    private void saveAndGoBack() {
        controlsController.saveControls(currentMoveUp, currentMoveDown, currentMoveLeft, 
                                      currentMoveRight, currentReload, currentShoot, currentSprint);
        controlsController.goBackToSettings();
    }
} 