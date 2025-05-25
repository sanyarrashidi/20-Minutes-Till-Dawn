package com.example.dawn.controller.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.example.dawn.service.FullscreenService;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.util.LmlUtilities;

/** This is a settings dialog, which can be shown in any view by using "show:settings" LML action or - in Java code -
 * through InterfaceService.showDialog(Class) method. Thanks to the fact that it implements ActionContainer, its methods
 * will be available in the LML template. */
@ViewDialog(id = "settings", value = "ui/templates/dialogs/settings.lml", cacheInstance = true)
public class SettingsController implements ActionContainer {
    @Inject private FullscreenService fullscreenService;
    @Inject private com.example.dawn.service.EnhancedMusicService enhancedMusicService;
    @Inject private MusicService musicService;

    /** @return array of serialized display modes' names. */
    @LmlAction("displayModes")
    public Array<String> getDisplayModes() {
        final ObjectSet<String> alreadyAdded = GdxSets.newSet(); // Removes duplicates.
        final Array<String> displayModes = GdxArrays.newArray(); // Keeps display modes sorted.
        for (final DisplayMode mode : fullscreenService.getDisplayModes()) {
            final String modeName = fullscreenService.serialize(mode);
            if (alreadyAdded.contains(modeName)) {
                continue; // Same size already added.
            }
            displayModes.add(modeName);
            alreadyAdded.add(modeName);
        }
        return displayModes;
    }

    /** @param actor its ID must match name of a display mode. */
    @LmlAction("setFullscreen")
    public void setFullscreenMode(final Actor actor) {
        final String modeName = LmlUtilities.getActorId(actor);
        final DisplayMode mode = fullscreenService.deserialize(modeName);
        fullscreenService.setFullscreen(mode);
    }

    /** Attempts to return to the original window size. */
    @LmlAction("resetFullscreen")
    public void setWindowedMode() {
        fullscreenService.resetFullscreen();
    }
    
    // Enhanced Music Service LML Actions
    
    /** @return array of available menu music track names. */
    @LmlAction("getMenuMusicTracks")
    public Array<String> getMenuMusicTracks() {
        if (enhancedMusicService == null) {
            System.out.println("DEBUG: enhancedMusicService is null in getMenuMusicTracks");
            Array<String> fallback = new Array<String>();
            fallback.add("Service Not Available");
            return fallback;
        }
        Array<String> tracks = enhancedMusicService.getMenuMusicTracks();
        System.out.println("DEBUG: getMenuMusicTracks returning " + tracks.size + " tracks: " + tracks);
        return tracks;
    }
    
    /** @return array of available game music track names. */
    @LmlAction("getGameMusicTracks")
    public Array<String> getGameMusicTracks() {
        if (enhancedMusicService == null) {
            System.out.println("DEBUG: enhancedMusicService is null in getGameMusicTracks");
            Array<String> fallback = new Array<String>();
            fallback.add("Service Not Available");
            return fallback;
        }
        Array<String> tracks = enhancedMusicService.getGameMusicTracks();
        System.out.println("DEBUG: getGameMusicTracks returning " + tracks.size + " tracks: " + tracks);
        return tracks;
    }
    
    /** @return currently selected menu music track name. */
    @LmlAction("getCurrentMenuMusic")
    public String getCurrentMenuMusic() {
        if (enhancedMusicService == null) {
            System.out.println("DEBUG: enhancedMusicService is null in getCurrentMenuMusic");
            return "Service Not Available";
        }
        String current = enhancedMusicService.getCurrentMenuMusic();
        System.out.println("DEBUG: getCurrentMenuMusic returning: " + current);
        return current;
    }
    
    /** @return currently selected game music track name. */
    @LmlAction("getCurrentGameMusic")
    public String getCurrentGameMusic() {
        if (enhancedMusicService == null) {
            System.out.println("DEBUG: enhancedMusicService is null in getCurrentGameMusic");
            return "Service Not Available";
        }
        String current = enhancedMusicService.getCurrentGameMusic();
        System.out.println("DEBUG: getCurrentGameMusic returning: " + current);
        return current;
    }
    
    /** Sets the menu music track. */
    @LmlAction("setMenuMusic")
    public void setMenuMusic(String displayName) {
        enhancedMusicService.setMenuMusic(displayName);
    }
    
    /** Sets the game music track. */
    @LmlAction("setGameMusic")
    public void setGameMusic(String displayName) {
        enhancedMusicService.setGameMusic(displayName);
    }
}