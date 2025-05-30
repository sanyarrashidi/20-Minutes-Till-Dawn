package com.example.dawn.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.example.dawn.service.controls.Control;
import com.example.dawn.service.controls.ControlListener;


public class Player implements ControlListener {
    private static final float DELAY = 0.75f; 
    private static final float SPEED = 750f; 
    private static final float JUMP = 1000f; 

    private final Control control;
    private final Body body;
    private final Viewport viewport;
    private boolean jumped;
    private float timeSinceLastJump = DELAY;

    public Player(final Control control, final Body body, final Viewport viewport) {
        this.control = control;
        this.body = body;
        this.viewport = viewport;
        control.setControlListener(this);
    }

    
    public Control getControl() {
        return control;
    }

    
    public Body getBody() {
        return body;
    }

    
    public void update(final float delta) {
        control.update(viewport, body.getPosition().x, body.getPosition().y);
        timeSinceLastJump += delta;
        final Vector2 movement = control.getMovementDirection();
        if (jumped && timeSinceLastJump > DELAY) {
            timeSinceLastJump = 0f;
            if (movement.x == 0f && movement.y == 0f) {
                body.applyForceToCenter(0f, JUMP, true);
            } else {
                body.applyForceToCenter(movement.x * JUMP, movement.y * JUMP, true);
            }
        }
        body.setActive(true);
        body.applyForceToCenter(movement.x * SPEED * delta, movement.y * SPEED * delta, true);
        jumped = false;
    }

    @Override
    public void jump() {
        jumped = true;
    }
}