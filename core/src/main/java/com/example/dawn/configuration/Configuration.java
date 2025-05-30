package com.example.dawn.configuration;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.SkinService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;
import com.github.czyzby.autumn.mvc.stereotype.preference.AvailableLocales;
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nBundle;
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nLocale;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlMacro;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlParserSyntax;
import com.github.czyzby.autumn.mvc.stereotype.preference.Preference;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicEnabled;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicVolume;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundEnabled;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundVolume;
import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax;
import com.kotcrab.vis.ui.VisUI;

@Component
public class Configuration {
  
  public static final String PREFERENCES = "20 Minutes Till Dawn";
  
  public static final int PLAYERS_AMOUNT = 3;
  
  @LmlMacro private final String globalMacro = "ui/templates/macros/global.lml";
  
  @I18nBundle private final String bundlePath = "i18n/bundle";
  
  @LmlParserSyntax private final LmlSyntax syntax = new VisLmlSyntax();

  @SoundVolume(preferences = PREFERENCES) private final String soundVolume = "soundVolume";
  @SoundEnabled(preferences = PREFERENCES) private final String soundEnabled = "soundOn";
  @MusicVolume(preferences = PREFERENCES) private final String musicVolume = "musicVolume";
  @MusicEnabled(preferences = PREFERENCES) private final String musicEnabledPreference = "musicOn";

  @I18nLocale(propertiesPath = PREFERENCES, defaultLocale = "en") private final String localePreference = "locale";
  @AvailableLocales private final String[] availableLocales = new String[] { "en" };

  
  @Preference private final String preferencesPath = PREFERENCES;

  @Initiate
  public void initiateConfiguration(final SkinService skinService) {
    
    VisUI.load(VisUI.SkinScale.X2);
    
    skinService.addSkin("default", VisUI.getSkin());
    
    InterfaceService.DEFAULT_VIEW_RESIZER = new ViewResizer() {
      @Override
      public void resize(final Stage stage, final int width, final int height) {
        stage.getViewport().update(width, height, true);
        for (final Actor actor : stage.getActors()) {
          Actors.centerActor(actor);
        }
      }
    };
  }
}