package eu.elektropolnilnice.igra.minigame_david.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eu.elektropolnilnice.igra.GameConfig;
import eu.elektropolnilnice.igra.Main;
import eu.elektropolnilnice.igra.assets.AssetDescriptors;
import eu.elektropolnilnice.igra.assets.RegionNames;

public class DavidMenuScreen extends ScreenAdapter {

    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas gameplay;

    public DavidMenuScreen() {
        assetManager = Main.Instance().getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, Main.Instance().getBatch());

        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.DAVID_GAMEPLAY);
        assetManager.finishLoading();

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplay = assetManager.get(AssetDescriptors.DAVID_GAMEPLAY);

        stage.addActor(createBackground());
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

    private Actor createBackground() {
        Image background = new Image(gameplay.findRegion(RegionNames.MENU_BACKGROUND));
        background.setFillParent(true);
        return background;
    }

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        Image logo = new Image(gameplay.findRegion(RegionNames.LOGO_1));
        TextButton playButton = new TextButton("PLAY", skin);
        TextButton howToPlayButton = new TextButton("HOW TO PLAY", skin);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new DavidGameScreen());
            }
        });

        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Dodaj funkcionalnost za HOW TO PLAY
                System.out.println("How to play clicked!");
            }
        });

        table.add(logo).center().padBottom(50);
        table.row();
        table.add(playButton).center().width(200).height(50);
        table.row();
        table.add(howToPlayButton).center().width(200).height(50);
        table.center();
        table.setFillParent(true);

        return table;
    }
}
