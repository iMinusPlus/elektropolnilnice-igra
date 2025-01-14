package eu.elektropolnilnice.igra.minigame_david.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eu.elektropolnilnice.igra.minigame_david.DavidGame;
import eu.elektropolnilnice.igra.minigame_david.assets.AssetDescriptors;
import eu.elektropolnilnice.igra.minigame_david.config.GameConfig;

public class MenuScreen extends ScreenAdapter {

    private final DavidGame game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;

    public MenuScreen(DavidGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.finishLoading();

        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        stage.addActor(createUi());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.LIGHT_GRAY);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        Label title = new Label("David's MINIGAME", skin);
        TextButton btn = new TextButton("Button", skin);

        table.add(title).center();
        table.row();
        table.add(btn).center();
        table.center();
        table.setFillParent(true);

        return table;
    }
}
