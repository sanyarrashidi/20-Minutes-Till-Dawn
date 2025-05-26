package com.example.dawn.view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.dawn.controller.ProfileMenuController;
import com.example.dawn.models.App;
import com.example.dawn.models.Player;
import com.example.dawn.models.Result;


public class ProfileMenu extends AppMenu {
    private final Skin skin;
    private final Label errorLabel;
    private final Label fontLabel;
    private final Label usernameLabel;
    private final Label winsLabel;
    private final Label highScoreLabel;
    private final Label totalGamesLabel;
    private final Label killsLabel;
    private final Label survivalDurationLabel;
    private final Label characterLabel;
    private final Label scoreLabel;
    private final TextButton changePasswordButton;
    private final TextButton changeUsernameButton;
    private final TextButton changeAvatarButton;
    private final TextButton deleteAccountButton;
    private final TextButton backButton;
    private Table mainTable;

    private final TextField newUsernameField;
    private final TextButton saveUsernameButton;
    private final TextButton backUsernameButton;

    private final TextField newPasswordField;
    private final TextField newPasswordConfirmField;
    private final TextButton savePasswordButton;
    private final TextButton backPasswordButton;


    public ProfileMenu(ProfileMenuController controller, Skin skin) {
        super(controller);
        this.stage = new Stage(new ScreenViewport());
        this.skin = skin;
        
        this.errorLabel = new Label("", skin);
        this.errorLabel.setColor(Color.RED);
        this.fontLabel = new Label("Your Profile", skin);
        this.fontLabel.setColor(Color.RED);
        this.usernameLabel = new Label("Username: " + App.getInstance().getPlayer().getUsername(), skin);
        this.usernameLabel.setColor(Color.GOLD);
        this.winsLabel = new Label("Wins: " + App.getInstance().getPlayer().getWins().toString(), skin);
        this.highScoreLabel = new Label("High Score: " + App.getInstance().getPlayer().getHighScore().toString(), skin);
        this.totalGamesLabel = new Label("Total Games: " + App.getInstance().getPlayer().getTotalGames().toString(), skin);
        this.killsLabel = new Label("Kills: " + App.getInstance().getPlayer().getKills().toString(), skin);
        this.killsLabel.setColor(Color.RED);
        this.survivalDurationLabel = new Label("Survival Duration: " + App.getInstance().getPlayer().getSurvivalDuration().toString(), skin);
        this.characterLabel = new Label(App.getInstance().getPlayer().getCharacter().getName(), skin);
        this.scoreLabel = new Label(String.valueOf(App.getInstance().getPlayer().getHighScore()), skin);

        this.changePasswordButton = new TextButton("Change Password", skin);
        this.changeUsernameButton = new TextButton("Change Username", skin);
        this.changeAvatarButton = new TextButton("Change Avatar", skin);
        this.deleteAccountButton = new TextButton("Delete Account", skin);
        this.backButton = new TextButton("Back", skin);

        this.newUsernameField = new TextField("", skin);
        this.newUsernameField.setMessageText("Enter new username");
        this.saveUsernameButton = new TextButton("Save", skin);
        this.backUsernameButton = new TextButton("Back", skin);

        this.newPasswordField = new TextField("", skin);
        this.newPasswordField.setMessageText("Enter new password");
        this.newPasswordConfirmField = new TextField("", skin);
        this.newPasswordConfirmField.setMessageText("Confirm new password");
        this.savePasswordButton = new TextButton("Save", skin);
        this.backPasswordButton = new TextButton("Back", skin);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        // Update all labels with current player data
        Player currentPlayer = App.getInstance().getPlayer();
        this.usernameLabel.setText("Username: " + currentPlayer.getUsername());
        this.winsLabel.setText("Wins: " + currentPlayer.getWins().toString());
        this.highScoreLabel.setText("High Score: " + currentPlayer.getHighScore().toString());
        this.totalGamesLabel.setText("Total Games: " + currentPlayer.getTotalGames().toString());
        this.killsLabel.setText("Kills: " + currentPlayer.getKills().toString());
        this.survivalDurationLabel.setText("Survival Duration: " + currentPlayer.getSurvivalDuration().toString());
        this.characterLabel.setText(currentPlayer.getCharacter().getName());
        this.scoreLabel.setText(String.valueOf(currentPlayer.getHighScore()));

        mainTable = new Table(skin);
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        String currentAvatarPath = App.getInstance().getPlayer().getAvatarPath();
        FileHandle avatarFileHandle;
        if (currentAvatarPath != null && currentAvatarPath.startsWith("custom_avatars/")) {
            avatarFileHandle = Gdx.files.local(currentAvatarPath); 
        } 
        else if (currentAvatarPath != null) {
            avatarFileHandle = Gdx.files.internal(currentAvatarPath);
        } 
        else {
            avatarFileHandle = Gdx.files.internal(App.getInstance().getPlayer().getCharacter().getImagePath());
        }

        Texture avatarTexture;
        if (avatarFileHandle.exists()) {
            avatarTexture = new Texture(avatarFileHandle);
        } 
        else {
            avatarTexture = new Texture(Gdx.files.internal(App.getInstance().getPlayer().getCharacter().getImagePath()));
        }
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
        mainTable.row();

        Table buttonHeaderTable = new Table(skin);
        buttonHeaderTable.add(fontLabel).colspan(5).center().padBottom(10);
        buttonHeaderTable.row();
        buttonHeaderTable.add(changeUsernameButton).width(500).height(100).pad(10);
        buttonHeaderTable.add(changePasswordButton).width(500).height(100).pad(10);
        buttonHeaderTable.row();
        buttonHeaderTable.add(changeAvatarButton).width(500).height(100).pad(10);
        buttonHeaderTable.add(deleteAccountButton).width(500).height(100).pad(10);

        mainTable.add(buttonHeaderTable).top().pad(10);
        mainTable.row();

        mainTable.add(usernameLabel).width(300).height(80).pad(5);
        mainTable.row();

        mainTable.add(winsLabel).width(300).height(80).pad(5);
        mainTable.row();

        mainTable.add(highScoreLabel).width(300).height(80).pad(5);
        mainTable.row();

        mainTable.add(totalGamesLabel).width(300).height(80).pad(5);
        mainTable.row();

        mainTable.add(killsLabel).width(300).height(80).pad(5);
        mainTable.row();

        mainTable.add(survivalDurationLabel).width(300).height(80).pad(5);
        mainTable.row();

        mainTable.add(backButton).width(200).height(100).pad(5);
        
        changeUsernameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToChangeUsername();
            }
        });

        changePasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToChangePassword();
            }
        });
        
        changeAvatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToChangeAvatar();
            }
        });
        
        deleteAccountButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((ProfileMenuController) controller).deleteAccount();
            }
        });
        
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((ProfileMenuController) controller).goToMainMenu();
            }
        });
    }

    public void goToChangeUsername() {
        stage.clear();

        Table changeUsernameTable = new Table(skin);
        changeUsernameTable.setFillParent(true);
        changeUsernameTable.center();

        changeUsernameTable.add(errorLabel).colspan(2).center().padBottom(10);
        changeUsernameTable.row();
        changeUsernameTable.add(new Label("Change Username", skin)).colspan(2).center().padBottom(10);
        changeUsernameTable.row();
        changeUsernameTable.add(newUsernameField).width(500).height(80).pad(5).colspan(2).center();
        changeUsernameTable.row();
        Table buttonTable = new Table(skin);
        buttonTable.add(saveUsernameButton).width(200).height(100).pad(5);
        buttonTable.add(backUsernameButton).width(200).height(100).pad(5);
        changeUsernameTable.add(buttonTable).colspan(2).center();

        stage.addActor(changeUsernameTable);

        saveUsernameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result result = ((ProfileMenuController) controller).changeUsername(newUsernameField.getText());
                if (!result.isSuccessful()) {
                    errorLabel.setText(result.toString());
                    newUsernameField.setText("");
                } 
                else {
                    errorLabel.setText("");
                    newUsernameField.setText("");
                    show();
                }
            }
        });
        
        backUsernameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                show();
            }
        });
    }

    public void goToChangePassword() {
        stage.clear();

        Table changePasswordTable = new Table(skin);
        changePasswordTable.setFillParent(true);
        changePasswordTable.center();

        changePasswordTable.add(errorLabel).colspan(2).center().padBottom(10);
        changePasswordTable.row();
        changePasswordTable.add(new Label("Change Password", skin)).colspan(2).center().padBottom(10);
        changePasswordTable.row();
        changePasswordTable.add(newPasswordField).width(500).height(80).pad(5).colspan(2).center();
        changePasswordTable.row();
        changePasswordTable.add(newPasswordConfirmField).width(500).height(80).pad(5).colspan(2).center();
        changePasswordTable.row();
        Table buttonTable = new Table(skin);
        buttonTable.add(savePasswordButton).width(200).height(100).pad(5);
        buttonTable.add(backPasswordButton).width(200).height(100).pad(5);
        changePasswordTable.add(buttonTable).colspan(2).center();

        newPasswordField.setPasswordMode(true);
        newPasswordConfirmField.setPasswordMode(true);
        newPasswordField.setPasswordCharacter('*');
        newPasswordConfirmField.setPasswordCharacter('*');

        stage.addActor(changePasswordTable);

        savePasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result result = ((ProfileMenuController) controller).changePassword(newPasswordField.getText(), newPasswordConfirmField.getText());
                if (!result.isSuccessful()) {
                    errorLabel.setText(result.toString());
                    newPasswordField.setText("");
                    newPasswordConfirmField.setText("");
                }
                else {
                    errorLabel.setText("");
                    newPasswordField.setText("");
                    newPasswordConfirmField.setText("");
                    show();
                }
            }
        });
        
        backPasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                show();
            }
        });
    }

    public void goToChangeAvatar() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        Table avatarTable = new Table(skin);
        avatarTable.setFillParent(true);
        avatarTable.center();

        Label titleLabel = new Label("Choose Your Avatar", skin);
        avatarTable.add(titleLabel).colspan(4).center().padBottom(20);
        avatarTable.row();

        FileHandle avatarDir = Gdx.files.internal("avatars");

        FileHandle[] avatarFiles = avatarDir.list();

        int count = 0;
        for (FileHandle file : avatarFiles) {
            if (file.extension().equalsIgnoreCase("png") || file.extension().equalsIgnoreCase("jpg") || file.extension().equalsIgnoreCase("jpeg")) {
                Texture avatarTexture = new Texture(file);
                Image avatarImage = new Image(avatarTexture);

                avatarImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Player currentPlayer = App.getInstance().getPlayer();
                        String newAvatarPath = file.path();
                        currentPlayer.setAvatarPath(newAvatarPath);
                        if (currentPlayer.getCharacter() != null) {
                            currentPlayer.getCharacter().setImagePath(newAvatarPath);
                        }

                        ((ProfileMenuController) controller).getDatabaseManager().savePlayer(currentPlayer);
                        show();
                    }
                });

                avatarTable.add(avatarImage).width(100).height(100).pad(10);
                count++;
                if (count % 4 == 0) avatarTable.row();
            }
        }

        avatarTable.row().padTop(20);
        TextButton uploadButton = new TextButton("Upload Custom Avatar", skin);
        avatarTable.add(uploadButton).colspan(4).center().padBottom(10);
        uploadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SwingUtilities.invokeLater(() -> {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (png, jpg, jpeg)", "png", "jpg", "jpeg");
                    fileChooser.setFileFilter(filter);
                    fileChooser.setDialogTitle("Select Custom Avatar");

                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        FileHandle source = Gdx.files.absolute(selectedFile.getAbsolutePath());
                        FileHandle customAvatarDir = Gdx.files.local("custom_avatars/");
                        if (!customAvatarDir.exists()) {
                            customAvatarDir.mkdirs();
                        }
                        FileHandle destination = customAvatarDir.child(source.name());
                        source.copyTo(destination);

                        Gdx.app.postRunnable(() -> {
                            Player currentPlayer = App.getInstance().getPlayer();
                            String newAvatarPath = destination.path();
                            currentPlayer.setAvatarPath(newAvatarPath);
                            if (currentPlayer.getCharacter() != null) {
                                currentPlayer.getCharacter().setImagePath(newAvatarPath);
                            }
                            
                            ((ProfileMenuController) controller).getDatabaseManager().savePlayer(currentPlayer);
                            errorLabel.setText("Custom avatar uploaded!");
                            errorLabel.setColor(Color.GREEN);
                            show();
                        });
                    }
                });
            }
        });
        
        avatarTable.row();
        TextButton backFromAvatarButton = new TextButton("Back", skin);
        avatarTable.add(backFromAvatarButton).colspan(4).center().padTop(10);
        backFromAvatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                show();
            }
        });
        
        ScrollPane scrollPane = new ScrollPane(avatarTable, skin);
        scrollPane.setFillParent(true);
        stage.addActor(scrollPane);
    }
}