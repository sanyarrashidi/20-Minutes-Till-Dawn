package com.example.dawn.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.example.dawn.configuration.Configuration;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;

@Component
public class EnhancedMusicService implements ActionContainer, Disposable {
    
    @Inject private MusicService musicService;
    
    private final Array<MusicTrack> availableTracks = new Array<>();
    private MusicTrack currentMenuTrack;
    private MusicTrack currentGameTrack;
    private String selectedMenuMusic = "theme.ogg";
    private String selectedGameMusic = "theme.ogg";
    private boolean isInitialized = false;
    
    
    private float directMusicVolume = 0.7f;
    private boolean directMusicEnabled = true;
    
    public enum MusicContext {
        MENU, GAME, NONE
    }
    
    private MusicContext currentContext = MusicContext.NONE;
    
    public static class MusicTrack {
        public final String fileName;
        public final String displayName;
        public final FileHandle fileHandle;
        private Music music;
        
        public MusicTrack(String fileName, String displayName, FileHandle fileHandle) {
            this.fileName = fileName;
            this.displayName = displayName;
            this.fileHandle = fileHandle;
        }
        
        public Music getMusic() {
            if (music == null && fileHandle.exists()) {
                try {
                    music = Gdx.audio.newMusic(fileHandle);
                } catch (Exception e) {
                    Gdx.app.error("EnhancedMusicService", "Failed to load music: " + fileName, e);
                }
            }
            return music;
        }
        
        public void dispose() {
            if (music != null) {
                music.dispose();
                music = null;
            }
        }
    }
    
    @Initiate
    public void initialize() {
        loadAvailableTracks();
        
        
        loadPreferences();
        
        isInitialized = true;
        Gdx.app.log("EnhancedMusicService", "Enhanced music service initialized with " + availableTracks.size + " tracks");
    }
    
    private void loadAvailableTracks() {
        FileHandle musicDir = Gdx.files.internal("music");
        Gdx.app.log("EnhancedMusicService", "Loading tracks from music directory: " + musicDir.path());
        Gdx.app.log("EnhancedMusicService", "Directory exists: " + musicDir.exists());
        
        if (musicDir.exists()) {
            FileHandle[] musicFiles = musicDir.list();
            Gdx.app.log("EnhancedMusicService", "Found " + musicFiles.length + " files in music directory");
            
            for (FileHandle file : musicFiles) {
                Gdx.app.log("EnhancedMusicService", "Checking file: " + file.name() + " (extension: " + file.extension() + ")");
                
                if (isMusicFile(file.extension())) {
                    String displayName = getDisplayName(file.nameWithoutExtension());
                    availableTracks.add(new MusicTrack(file.name(), displayName, file));
                    Gdx.app.log("EnhancedMusicService", "Added track: " + file.name() + " -> " + displayName);
                } else {
                    Gdx.app.log("EnhancedMusicService", "Skipped non-music file: " + file.name());
                }
            }
        } else {
            Gdx.app.error("EnhancedMusicService", "Music directory does not exist!");
        }
        
        
        if (availableTracks.size == 0) {
            Gdx.app.log("EnhancedMusicService", "No tracks found, adding default theme");
            FileHandle defaultTrack = Gdx.files.internal("music/theme.ogg");
            if (defaultTrack.exists()) {
                availableTracks.add(new MusicTrack("theme.ogg", "Default Theme", defaultTrack));
            }
        }
        
        Gdx.app.log("EnhancedMusicService", "Total tracks loaded: " + availableTracks.size);
    }
    
    private boolean isMusicFile(String extension) {
        return extension.equalsIgnoreCase("ogg") || 
               extension.equalsIgnoreCase("mp3") || 
               extension.equalsIgnoreCase("wav");
    }
    
    private String getDisplayName(String fileName) {
        
        StringBuilder result = new StringBuilder();
        String cleaned = fileName.replace("_", " ").replace("-", " ");
        String[] words = cleaned.split(" ");
        
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    private void loadPreferences() {
        try {
            selectedMenuMusic = Gdx.app.getPreferences(Configuration.PREFERENCES)
                                     .getString("selectedMenuMusic", "theme.ogg");
            selectedGameMusic = Gdx.app.getPreferences(Configuration.PREFERENCES)
                                     .getString("selectedGameMusic", "theme.ogg");
        } catch (Exception e) {
            Gdx.app.error("EnhancedMusicService", "Failed to load music preferences", e);
        }
    }
    
    private void savePreferences() {
        try {
            Gdx.app.getPreferences(Configuration.PREFERENCES)
                   .putString("selectedMenuMusic", selectedMenuMusic)
                   .putString("selectedGameMusic", selectedGameMusic)
                   .flush();
        } catch (Exception e) {
            Gdx.app.error("EnhancedMusicService", "Failed to save music preferences", e);
        }
    }
    
    public void switchToMenuMusic() {
        if (!isInitialized) return;
        
        currentContext = MusicContext.MENU;
        MusicTrack track = findTrack(selectedMenuMusic);
        if (track != null && track != currentMenuTrack) {
            currentMenuTrack = track;
            playTrack(track);
        }
    }
    
    public void switchToGameMusic() {
        if (!isInitialized) return;
        
        currentContext = MusicContext.GAME;
        MusicTrack track = findTrack(selectedGameMusic);
        if (track != null && track != currentGameTrack) {
            currentGameTrack = track;
            playTrack(track);
        }
    }
    
    public void stopMusic() {
        currentContext = MusicContext.NONE;
        
        if (currentMenuTrack != null) {
            Music music = currentMenuTrack.getMusic();
            if (music != null && music.isPlaying()) {
                music.stop();
            }
        }
        if (currentGameTrack != null) {
            Music music = currentGameTrack.getMusic();
            if (music != null && music.isPlaying()) {
                music.stop();
            }
        }
    }
    
    private MusicTrack findTrack(String fileName) {
        for (MusicTrack track : availableTracks) {
            if (track.fileName.equals(fileName)) {
                return track;
            }
        }
        return availableTracks.size > 0 ? availableTracks.first() : null;
    }
    
    private void playTrack(MusicTrack track) {
        if (track == null) {
            Gdx.app.error("EnhancedMusicService", "playTrack called with null track");
            return;
        }
        
        Gdx.app.log("EnhancedMusicService", "Attempting to play track: " + track.displayName + " (" + track.fileName + ")");
        
        
        stopAllMusic();
        
        Music music = track.getMusic();
        if (music != null) {
            
            boolean musicEnabled = musicService != null ? musicService.isMusicEnabled() : directMusicEnabled;
            float musicVolume = musicService != null ? musicService.getMusicVolume() : directMusicVolume;
            
            Gdx.app.log("EnhancedMusicService", "Music enabled: " + musicEnabled + ", Volume: " + musicVolume);
            Gdx.app.log("EnhancedMusicService", "Using " + (musicService != null ? "MusicService" : "direct control"));
            
            if (musicEnabled) {
                music.setLooping(true);
                music.setVolume(musicVolume);
                music.play();
                Gdx.app.log("EnhancedMusicService", "Started playing: " + track.displayName + " at volume " + musicVolume);
            } else {
                Gdx.app.log("EnhancedMusicService", "Music is disabled, not playing track");
            }
        } else {
            Gdx.app.error("EnhancedMusicService", "Failed to load music for track: " + track.displayName);
        }
    }
    
    private void stopAllMusic() {
        
        for (MusicTrack track : availableTracks) {
            Music music = track.getMusic();
            if (music != null && music.isPlaying()) {
                music.stop();
            }
        }
    }
    
    
    
    @LmlAction("getMenuMusicTracks")
    public Array<String> getMenuMusicTracks() {
        Gdx.app.log("EnhancedMusicService", "getMenuMusicTracks called - available tracks: " + availableTracks.size);
        Array<String> trackNames = new Array<>();
        for (MusicTrack track : availableTracks) {
            trackNames.add(track.displayName);
            Gdx.app.log("EnhancedMusicService", "Track: " + track.displayName);
        }
        return trackNames;
    }
    
    @LmlAction("getGameMusicTracks")
    public Array<String> getGameMusicTracks() {
        return getMenuMusicTracks(); 
    }
    
    @LmlAction("getCurrentMenuMusic")
    public String getCurrentMenuMusic() {
        MusicTrack track = findTrack(selectedMenuMusic);
        String result = track != null ? track.displayName : "Default Theme";
        Gdx.app.log("EnhancedMusicService", "getCurrentMenuMusic called - selectedMenuMusic: " + selectedMenuMusic + " -> " + result);
        return result;
    }
    
    @LmlAction("getCurrentGameMusic")
    public String getCurrentGameMusic() {
        MusicTrack track = findTrack(selectedGameMusic);
        return track != null ? track.displayName : "Default Theme";
    }
    
    @LmlAction("setMenuMusic")
    public void setMenuMusic(String displayName) {
        Gdx.app.log("EnhancedMusicService", "setMenuMusic called with: " + displayName);
        MusicTrack track = findTrackByDisplayName(displayName);
        if (track != null) {
            selectedMenuMusic = track.fileName;
            Gdx.app.log("EnhancedMusicService", "Menu music changed to: " + selectedMenuMusic);
            savePreferences();
            
            
            if (currentContext == MusicContext.MENU) {
                Gdx.app.log("EnhancedMusicService", "Currently in menu context, switching immediately");
                switchToMenuMusic();
            } else {
                Gdx.app.log("EnhancedMusicService", "Not in menu context (" + currentContext + "), will switch later");
            }
        } else {
            Gdx.app.error("EnhancedMusicService", "Could not find track with display name: " + displayName);
        }
    }
    
    @LmlAction("setGameMusic")
    public void setGameMusic(String displayName) {
        MusicTrack track = findTrackByDisplayName(displayName);
        if (track != null) {
            selectedGameMusic = track.fileName;
            savePreferences();
            
            
            if (currentContext == MusicContext.GAME) {
                switchToGameMusic();
            }
        }
    }
    
    private MusicTrack findTrackByDisplayName(String displayName) {
        for (MusicTrack track : availableTracks) {
            if (track.displayName.equals(displayName)) {
                return track;
            }
        }
        return null;
    }
    
    public Array<String> getTrackDisplayNames() {
        Array<String> names = new Array<>();
        for (MusicTrack track : availableTracks) {
            names.add(track.displayName);
        }
        return names;
    }
    
    public String getCurrentTrackName() {
        if (currentContext == MusicContext.MENU && currentMenuTrack != null) {
            return currentMenuTrack.displayName;
        } else if (currentContext == MusicContext.GAME && currentGameTrack != null) {
            return currentGameTrack.displayName;
        }
        return "None";
    }
    
    public void setMusicVolume(float volume) {
        Gdx.app.log("EnhancedMusicService", "setMusicVolume called with: " + volume);
        if (musicService != null) {
            musicService.setMusicVolume(volume);
            Gdx.app.log("EnhancedMusicService", "Volume set to " + volume + " in MusicService");
        } else {
            directMusicVolume = volume;
            Gdx.app.log("EnhancedMusicService", "Volume set to " + volume + " in direct control");
        }
        
        
        updateCurrentTrackVolume();
    }
    
    public float getMusicVolume() {
        return musicService != null ? musicService.getMusicVolume() : directMusicVolume;
    }
    
    public void setMusicEnabled(boolean enabled) {
        Gdx.app.log("EnhancedMusicService", "setMusicEnabled called with: " + enabled);
        if (musicService != null) {
            musicService.setMusicEnabled(enabled);
            Gdx.app.log("EnhancedMusicService", "Music enabled set to " + enabled + " in MusicService");
        } else {
            directMusicEnabled = enabled;
            Gdx.app.log("EnhancedMusicService", "Music enabled set to " + enabled + " in direct control");
        }
        
        if (enabled) {
            Gdx.app.log("EnhancedMusicService", "Music enabled - resuming in context: " + currentContext);
            
            if (currentContext == MusicContext.MENU) {
                
                MusicTrack track = findTrack(selectedMenuMusic);
                if (track != null) {
                    currentMenuTrack = track;
                    playTrack(track);
                }
            } else if (currentContext == MusicContext.GAME) {
                
                MusicTrack track = findTrack(selectedGameMusic);
                if (track != null) {
                    currentGameTrack = track;
                    playTrack(track);
                }
            } else {
                
                Gdx.app.log("EnhancedMusicService", "No context set, defaulting to menu music");
                switchToMenuMusic();
            }
        } else {
            Gdx.app.log("EnhancedMusicService", "Music disabled - stopping all music");
            
            stopAllMusic();
        }
    }
    
    public boolean isMusicEnabled() {
        return musicService != null ? musicService.isMusicEnabled() : directMusicEnabled;
    }
    
    private void updateCurrentTrackVolume() {
        float volume = getMusicVolume();
        Gdx.app.log("EnhancedMusicService", "Updating current track volume to: " + volume);
        
        if (currentMenuTrack != null) {
            Music music = currentMenuTrack.getMusic();
            if (music != null && music.isPlaying()) {
                music.setVolume(volume);
                Gdx.app.log("EnhancedMusicService", "Updated menu track volume to: " + volume);
            }
        }
        
        if (currentGameTrack != null) {
            Music music = currentGameTrack.getMusic();
            if (music != null && music.isPlaying()) {
                music.setVolume(volume);
                Gdx.app.log("EnhancedMusicService", "Updated game track volume to: " + volume);
            }
        }
    }
    
    @Destroy
    @Override
    public void dispose() {
        for (MusicTrack track : availableTracks) {
            track.dispose();
        }
        availableTracks.clear();
    }
} 