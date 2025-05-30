package com.example.dawn.controller.action;

import com.github.czyzby.autumn.mvc.stereotype.ViewActionContainer;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.example.dawn.configuration.Configuration;

@ViewActionContainer("global")
public class Global implements ActionContainer {
    @LmlAction("close")
    public void noOp() {
    }

    
    @LmlAction("isMobile")
    public boolean isOnMobilePlatform() {
        return GdxUtilities.isMobile();
    }

    
    @LmlAction("playersAmount")
    public int getPlayersAmount() {
        return Configuration.PLAYERS_AMOUNT;
    }
}