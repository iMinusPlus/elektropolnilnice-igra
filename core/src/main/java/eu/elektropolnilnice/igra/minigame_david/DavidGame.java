package eu.elektropolnilnice.igra.minigame_david;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import eu.elektropolnilnice.igra.minigame_david.screen.MenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DavidGame extends Game {

    private AssetManager assetManager;
    private SpriteBatch batch;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        assetManager = new AssetManager();
        assetManager.getLogger().setLevel(Logger.DEBUG);

        batch = new SpriteBatch();

        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
