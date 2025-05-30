package com.example.dawn.service;

import com.badlogic.gdx.utils.Array;
import com.example.dawn.service.controls.Control;
import com.example.dawn.service.controls.ControlType;
import com.example.dawn.service.controls.impl.KeyboardControl;

public class PlayerControlsService {
    
    private ControlsService controlsService;
    
    public PlayerControlsService() {
        
    }
    
    public void setControlsService(ControlsService controlsService) {
        this.controlsService = controlsService;
    }
    
    public void updateAllKeyboardControls() {
        if (controlsService == null) {
            return; 
        }
        
        Array<Control> controls = controlsService.getControls();
        
        for (Control control : controls) {
            if (control.getType() == ControlType.KEYBOARD && control instanceof KeyboardControl) {
                KeyboardControl keyboardControl = (KeyboardControl) control;
                keyboardControl.updateControlsFromPlayer();
            }
        }
    }
    
    public ControlsService getControlsService() {
        return controlsService;
    }
} 