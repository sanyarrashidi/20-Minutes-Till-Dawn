package com.example.dawn.service.controls;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;


public abstract class AbstractControl implements Control {
    
    protected static final float SIN = MathUtils.sin(MathUtils.atan2(1f, 1f));
    
    protected static final float COS = MathUtils.cos(MathUtils.atan2(1f, 1f));

    private ControlListener listener;
    protected Vector2 movement = new Vector2();

    @Override
    public Vector2 getMovementDirection() {
        return movement;
    }

    @Override
    public void setControlListener(final ControlListener listener) {
        this.listener = listener;
    }

    
    protected ControlListener getListener() {
        return listener;
    }

    
    protected void updateMovementWithAngle(final float angle) {
        movement.x = MathUtils.cos(angle);
        movement.y = MathUtils.sin(angle);
    }

    
    protected void stop() {
        movement.set(0f, 0f);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void reset() {
        movement.set(0f, 0f);
    }
}