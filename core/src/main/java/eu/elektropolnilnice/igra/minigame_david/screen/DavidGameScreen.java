package eu.elektropolnilnice.igra.minigame_david.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class DavidGameScreen extends ScreenAdapter {

    private final AssetManager assetManager;
    private final SpriteBatch batch;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas gameplay;

    private float backgroundX; // X koordinata ozadja
    private static final float BACKGROUND_SPEED = 100f; // Hitrost premikanja ozadja

    public DavidGameScreen() {
        assetManager = Main.Instance().getAssetManager();
        batch = Main.Instance().getBatch();
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

        stage.addActor(bottomNavigation());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.LIGHT_GRAY);

        backgroundX -= BACKGROUND_SPEED * delta;
        if (backgroundX <= -gameplay.findRegion(RegionNames.BACKGROUND).getRegionWidth()) {
            backgroundX = 0; // Ponovi ozadje
        }

        batch.begin();
        batch.draw(gameplay.findRegion(RegionNames.BACKGROUND), backgroundX, 0); // Prvi del ozadja
        batch.draw(gameplay.findRegion(RegionNames.BACKGROUND), backgroundX + gameplay.findRegion(RegionNames.BACKGROUND).getRegionWidth(), 0); // Drugi del ozadja za ponovitev

        batch.draw(gameplay.findRegion(RegionNames.CAR_1), 50f, 300f);
        batch.draw(gameplay.findRegion(RegionNames.CAR_2), 50f, 550f);

        batch.end();

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

    private Actor bottomNavigation() {
        Table table = new Table();
        table.defaults().pad(20);

        TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new DavidMenuScreen());
            }
        });

        table.add(backButton).expand().bottom().left().pad(20);
        table.setFillParent(true);

        return table;
    }
}

