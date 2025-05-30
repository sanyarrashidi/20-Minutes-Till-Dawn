package com.example.dawn.service.controls;

import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.example.dawn.configuration.preferences.ControlsData;


public abstract class AbstractButtonControl extends AbstractControl {
    protected IntSet pressedButtons = new IntSet(4);

    protected int up;
    protected int down;
    protected int left;
    protected int right;
    protected int jump;

    
    protected void updateMovement() {
        if (pressedButtons.size == 0) {
            stop();
        } else if (isPressed(up)) {
            if (isPressed(left)) { 
                movement.set(-COS, SIN);
            } else if (isPressed(right)) { 
                movement.set(COS, SIN);
            } else { 
                movement.set(0f, 1f);
            }
        } else if (isPressed(down)) {
            if (isPressed(left)) { 
                movement.set(-COS, -SIN);
            } else if (isPressed(right)) { 
                movement.set(COS, -SIN);
            } else { 
                movement.set(0f, -1f);
            }
        } else if (isPressed(left)) { 
            movement.set(-1f, 0f);
        } else if (isPressed(right)) { 
            movement.set(1f, 0f);
        } else {
            stop();
        }
    }

    @Override
    public void update(final Viewport gameViewport, final float gameX, final float gameY) {
        
    }

    protected boolean isPressed(final int key) {
        return pressedButtons.contains(key);
    }

    @Override
    public ControlsData toData() {
        final ControlsData data = new ControlsData(getType());
        data.up = up;
        data.down = down;
        data.left = left;
        data.right = right;
        data.jump = jump;
        return data;
    }

    @Override
    public void copy(final ControlsData data) {
        up = data.up;
        down = data.down;
        left = data.left;
        right = data.right;
        jump = data.jump;
    }

    @Override
    public void reset() {
        super.reset();
        pressedButtons.clear();
    }

    
    public int getUp() {
        return up;
    }

    
    public void setUp(final int up) {
        pressedButtons.remove(this.up);
        updateMovement();
        this.up = up;
    }

    
    public int getDown() {
        return down;
    }

    
    public void setDown(final int down) {
        pressedButtons.remove(this.down);
        updateMovement();
        this.down = down;
    }

    
    public int getLeft() {
        return left;
    }

    
    public void setLeft(final int left) {
        pressedButtons.remove(this.left);
        updateMovement();
        this.left = left;
    }

    
    public int getRight() {
        return right;
    }

    
    public void setRight(final int right) {
        pressedButtons.remove(this.right);
        updateMovement();
        this.right = right;
    }

    
    public int getJump() {
        return jump;
    }

    
    public void setJump(final int jump) {
        this.jump = jump;
    }
}