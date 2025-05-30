package com.example.dawn.service.controls.impl;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.example.dawn.models.App;
import com.example.dawn.models.Player;
import com.example.dawn.service.controls.AbstractButtonControl;
import com.example.dawn.service.controls.ControlType;


public class KeyboardControl extends AbstractButtonControl {
    public KeyboardControl() {
        
        loadControlsFromPlayer();
    }
    
    public void loadControlsFromPlayer() {
        Player player = App.getInstance().getPlayer();
        if (player != null) {
            
            up = player.getMoveUpKey() != null ? player.getMoveUpKey() : Keys.W;
            down = player.getMoveDownKey() != null ? player.getMoveDownKey() : Keys.S;
            left = player.getMoveLeftKey() != null ? player.getMoveLeftKey() : Keys.A;
            right = player.getMoveRightKey() != null ? player.getMoveRightKey() : Keys.D;
            jump = player.getShootKey() != null ? player.getShootKey() : Keys.SPACE;
        } else {
            
            up = Keys.W;
            down = Keys.S;
            left = Keys.A;
            right = Keys.D;
            jump = Keys.SPACE;
        }
    }
    
    public void updateControlsFromPlayer() {
        loadControlsFromPlayer();
    }

    @Override
    public void attachInputListener(final InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(final int keycode) {
                if (keycode == up || keycode == down || keycode == left || keycode == right) {
                    pressedButtons.add(keycode);
                    updateMovement();
                    return true;
                } else if (keycode == jump) {
                    getListener().jump();
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(final int keycode) {
                if (keycode == up || keycode == down || keycode == left || keycode == right) {
                    pressedButtons.remove(keycode);
                    updateMovement();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public ControlType getType() {
        return ControlType.KEYBOARD;
    }
}