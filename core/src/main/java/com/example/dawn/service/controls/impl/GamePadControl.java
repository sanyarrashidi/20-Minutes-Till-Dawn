package com.example.dawn.service.controls.impl;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.math.MathUtils;
import com.example.dawn.configuration.preferences.ControlsData;
import com.example.dawn.service.controls.AbstractButtonControl;
import com.example.dawn.service.controls.ControlType;


public class GamePadControl extends AbstractButtonControl {
    private static final float DEADZONE = 0.2f;
    
    protected static final int X_LEFT = 0, X_RIGHT = 3, Y_LEFT = 1, Y_RIGHT = 2;
    
    private boolean invertX;
    
    private boolean invertY;
    
    private boolean invertXY;

    protected float axisX;
    protected float axisY;
    private Controller controller;
    private int controllerIndex;
    private final ControllerListener controllerListener = new ControllerAdapter() {
        @Override
        public boolean axisMoved(final Controller controller, final int axisIndex, final float value) {
            if (isAssignedTo(controller)) {
                updateAxisValue(axisIndex, value);
                return true;
            }
            return false;
        }

        @Override
        public boolean buttonDown(final Controller controller, final int buttonIndex) {
            if (isAssignedTo(controller)) {
                if (buttonIndex == up || buttonIndex == down || buttonIndex == left || buttonIndex == right) {
                    pressedButtons.add(buttonIndex);
                    updateMovement();
                    return true;
                } else if (buttonIndex == jump) {
                    getListener().jump();
                    return true;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean buttonUp(final Controller controller, final int buttonIndex) {
            if (isAssignedTo(controller)) {
                if (buttonIndex == up || buttonIndex == down || buttonIndex == left || buttonIndex == right) {
                    pressedButtons.remove(buttonIndex);
                    updateMovement();
                    return true;
                }
                return true;
            }
            return false;
        }


















    };

    public GamePadControl() {
        up = 0;
        down = 2;
        left = 3;
        right = 1;
        jump = 4;
    }

    
    public GamePadControl(final Controller controller) {
        this();
        this.controller = controller;
    }

    
    public Controller getController() {
        return controller;
    }

    
    public void setController(final Controller controller) {
        if (this.controller != null) {
            this.controller.removeListener(controllerListener);
        }
        this.controller = controller;
        if (controller != null) {
            controllerIndex = Controllers.getControllers().indexOf(controller, true);
        }
    }

    public boolean isAssignedTo(final Controller controller) {
        return this.controller.equals(controller);
    }

    protected void updateAxisValue(final int axisIndex, float value) {
        if (isY(axisIndex)) { 
            value = -value;
        }
        if (!invertXY && isX(axisIndex) || invertXY && isY(axisIndex)) {
            if (value > DEADZONE || value < -DEADZONE) {
                axisX = invertX ? -value : value;
            } else {
                axisX = 0f;
            }
        } else {
            if (value > DEADZONE || value < -DEADZONE) {
                axisY = invertY ? -value : value;
            } else {
                axisY = 0f;
            }
        }
        if (Float.compare(axisX, 0f) == 0 && Float.compare(axisY, 0f) == 0) {
            stop();
        } else {
            updateMovementWithAngle(MathUtils.atan2(axisY, axisX));
        }

    }

























































    private static boolean isX(final int axisIndex) {
        return axisIndex == X_LEFT || axisIndex == X_RIGHT;
    }

    private static boolean isY(final int axisIndex) {
        return axisIndex == Y_LEFT || axisIndex == Y_RIGHT;
    }

    protected float getAxisAngle() {
        return MathUtils.atan2(axisY, axisX) * MathUtils.radiansToDegrees;
    }

    
    public boolean isInvertX() {
        return invertX;
    }

    
    public void setInvertX(final boolean invertX) {
        this.invertX = invertX;
    }

    
    public boolean isInvertY() {
        return invertY;
    }

    
    public void setInvertY(final boolean invertY) {
        this.invertY = invertY;
    }

    
    public boolean isInvertXY() {
        return invertXY;
    }

    
    public void setInvertXY(final boolean invertXY) {
        this.invertXY = invertXY;
    }

    @Override
    public void attachInputListener(final InputMultiplexer inputMultiplexer) {
        controller.removeListener(controllerListener); 
        controller.addListener(controllerListener);
    }

    @Override
    public ControlsData toData() {
        final ControlsData data = super.toData();
        data.invertX = invertX;
        data.invertY = invertY;
        data.invertXY = invertXY;
        data.index = controllerIndex;
        return data;
    }

    @Override
    public void copy(final ControlsData data) {
        super.copy(data);
        invertX = data.invertX;
        invertY = data.invertY;
        invertXY = data.invertXY;
    }

    @Override
    public ControlType getType() {
        return ControlType.PAD;
    }
}