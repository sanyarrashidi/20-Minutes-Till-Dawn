package com.example.dawn.service.controls.impl;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.example.dawn.configuration.preferences.ControlsData;
import com.example.dawn.service.controls.Control;
import com.example.dawn.service.controls.ControlListener;
import com.example.dawn.service.controls.ControlType;


public class InactiveControl implements Control {
    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public ControlType getType() {
        return ControlType.INACTIVE;
    }

    @Override
    public void attachInputListener(final InputMultiplexer inputMultiplexer) {
    }

    @Override
    public void update(final Viewport gameViewport, final float gameX, final float gameY) {
    }

    @Override
    public Vector2 getMovementDirection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setControlListener(final ControlListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ControlsData toData() {
        return new ControlsData(getType());
    }

    @Override
    public void copy(final ControlsData data) {
    }

    @Override
    public void reset() {
    }
}