package com.example.dawn.controller.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.util.LmlUtilities;
import com.example.dawn.configuration.Configuration;
import com.example.dawn.service.ControlsService;
import com.example.dawn.service.controls.Control;


@ViewDialog(id = "controls", value = "ui/templates/dialogs/controls.lml", cacheInstance = true)
public class ControlsController implements ActionContainer, ViewDialogShower {
    @Inject ControlsService service;
    @Inject ControlsEditController controlsEdit;
    @Inject ControlsSwitchController controlsSwitch;
    @Inject InterfaceService interfaceService;
    
    @LmlActor("edit[0," + (Configuration.PLAYERS_AMOUNT - 1) + "]") private ObjectMap<String, Button> editButtons;

    @Override
    public void doBeforeShow(final Window dialog) {
        final Array<Control> controls = service.getControls();
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            refreshPlayerView(index, controls.get(index));
        }
    }

    public void refreshPlayerView(final int playerId, final Control control) {
        final String editId = "edit" + playerId;
        if (control.isActive()) {
            editButtons.get(editId).setDisabled(false);
        } else {
            editButtons.get(editId).setDisabled(true);
        }
    }

    @LmlAction("edit")
    public void editControls(final Actor actor) {
        final int playerId = Integer.parseInt(LmlUtilities.getActorId(actor).replace("edit", ""));
        controlsEdit.setControl(service.getControl(playerId));
        interfaceService.showDialog(ControlsEditController.class);
    }

    @LmlAction("switch")
    public void switchControls(final Actor actor) {
        final int playerId = Integer.parseInt(LmlUtilities.getActorId(actor).replace("switch", ""));
        controlsSwitch.setPlayerId(playerId);
        interfaceService.showDialog(ControlsSwitchController.class);
    }
}