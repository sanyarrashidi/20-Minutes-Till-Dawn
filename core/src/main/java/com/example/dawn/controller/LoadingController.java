package com.example.dawn.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.lml.annotation.LmlActor;
import com.kotcrab.vis.ui.widget.VisProgressBar;

@View(value = "ui/templates/loading.lml", first = true)
public class LoadingController implements ViewRenderer {
    
    @Inject private AssetService assetService;
    
    @LmlActor("loadingBar") private VisProgressBar loadingBar;

    
    
    @Override
    public void render(final Stage stage, final float delta) {
        assetService.update();
        loadingBar.setValue(assetService.getLoadingProgress());
        stage.act(delta);
        stage.draw();
    }
}