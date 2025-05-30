package com.example.dawn.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.example.dawn.controller.dialog.NotEnoughPlayersErrorController;
import com.example.dawn.service.ControlsService;
import com.example.dawn.service.controls.Control;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;

@View(id = "menu", value = "ui/templates/menu.lml")
public class MenuController implements ActionContainer {
    @Inject private InterfaceService interfaceService;
    @Inject private ControlsService controlsService;
    @Inject private com.example.dawn.service.EnhancedMusicService enhancedMusicService;

    @LmlAction("startGame")
    public void startPlaying() {
        if (isAnyPlayerActive()) {
            
            enhancedMusicService.switchToGameMusic();
            interfaceService.show(GameController.class);
        } else {
            interfaceService.showDialog(NotEnoughPlayersErrorController.class);
        }
    }
    
    @Initiate
    public void initialize() {
        
        if (enhancedMusicService != null) {
            
            Array<String> tracks = enhancedMusicService.getTrackDisplayNames();
            Gdx.app.log("MenuController", "Enhanced music service found " + tracks.size + " tracks:");
            for (String track : tracks) {
                Gdx.app.log("MenuController", "  - " + track);
            }
            enhancedMusicService.switchToMenuMusic();
        } else {
            Gdx.app.error("MenuController", "Enhanced music service is null!");
        }
    }
    
    public void show() {
        
        if (enhancedMusicService != null) {
            enhancedMusicService.switchToMenuMusic();
        }
    }

    private boolean isAnyPlayerActive() {
        final Array<Control> controls = controlsService.getControls();
        for (final Control control : controls) {
            if (control.isActive()) {
                return true;
            }
        }
        return false;
    }
}