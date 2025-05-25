package com.example.dawn.service;

import com.badlogic.gdx.utils.Array;
import com.example.dawn.service.controls.Control;
import com.example.dawn.service.controls.ControlType;
import com.example.dawn.service.controls.impl.KeyboardControl;

/**
 * Service to manage player control settings and apply them to active controls
 * This is a simplified version that doesn't rely on dependency injection
 */
public class PlayerControlsService {
    
    private ControlsService controlsService;
    
    public PlayerControlsService() {
        // We'll need to set the controls service manually when it's available
    }
    
    public void setControlsService(ControlsService controlsService) {
        this.controlsService = controlsService;
    }
    
    /**
     * Update all active keyboard controls to use the current player's settings
     * Call this whenever the player changes their control bindings
     */
    public void updateAllKeyboardControls() {
        if (controlsService == null) {
            return; // No controls service available yet
        }
        
        Array<Control> controls = controlsService.getControls();
        
        for (Control control : controls) {
            if (control.getType() == ControlType.KEYBOARD && control instanceof KeyboardControl) {
                KeyboardControl keyboardControl = (KeyboardControl) control;
                keyboardControl.updateControlsFromPlayer();
            }
        }
    }
    
    /**
     * Get the ControlsService instance
     * @return the controls service
     */
    public ControlsService getControlsService() {
        return controlsService;
    }
} 