package eu.elektropolnilnice.igra;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eu.elektropolnilnice.igra.assets.AssetDescriptors;
import eu.elektropolnilnice.igra.utils.Station;

import java.io.IOException;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private static Main instance;

    private AssetManager assetManager;
    private GameManager gameManager;
    private SpriteBatch batch;
    private Array<Station> stations;

    public Main() {
        instance = this;
    }

    public static Main Instance() {
        if (instance != null) {
            return instance;
        } else {
            throw new IllegalStateException("Main instance is null");
        }
    }

    private void init() {
        assetManager = new AssetManager();
        batch = new SpriteBatch();
    }

    @Override
    public void create() {
        init();
        loadAssets();
        gameManager = new GameManager();

        try {
            stations = Station.getStations();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //TODO: make intro screen mby
        setScreen(new IntroScreen(this));
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    //TODO: load all of the game assets here
    private void loadAssets() {
        // Sounds
//        assetManager.load(AssetDescriptors.GAME_MUSIC);
//        assetManager.load(AssetDescriptors.BUTTON_HOVER);
//        assetManager.load(AssetDescriptors.INVALID_SOUND);
//        assetManager.load(AssetDescriptors.TAKE_LAND);
//
         // UI
        assetManager.load(AssetDescriptors.UI_SKIN);
//        assetManager.load(AssetDescriptors.GAMEPLAY);
//        assetManager.load(AssetDescriptors.UI_FONT);
//        assetManager.load(AssetDescriptors.SKIN);
//        assetManager.load(AssetDescriptors.GAME_TITLE);
//
//        // Load the map last i think mby
//        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
//        assetManager.load(AssetDescriptors.MAP);
//        assetManager.load(AssetDescriptors.TILE_MAP);
        assetManager.finishLoading();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public Array<Station> getStations() {
        return stations;
    }
    public Skin getSkin() {return assetManager.get(AssetDescriptors.UI_SKIN);}


}
