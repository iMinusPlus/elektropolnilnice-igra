package eu.elektropolnilnice.igra;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import eu.elektropolnilnice.igra.screen.MenuScreen;

public class IntroScreen extends ScreenAdapter {
    public static final float INTRO_DURATION_IN_SEC = 3f; // duration of the intro animation

    private final Game game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private float duration = 0f;

    public IntroScreen(Game game) {
        this.game = game;
        assetManager = new AssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport);

        // Load assets
        Texture carLogoTexture = new Texture("assets_raw/intro/carlogo.png");
        Texture lightningTexture = new Texture("assets_raw/intro/lightning.png");

        // Create car logo actor
        Image carLogo = new Image(carLogoTexture);
        carLogo.setSize(carLogo.getWidth() * 0.5f, carLogo.getHeight() * 0.5f); // Scale down
        carLogo.setPosition(viewport.getWorldWidth(), viewport.getWorldHeight() / 2 - carLogo.getHeight() / 2);

        // Create lightning actor
        Image lightning = new Image(lightningTexture);
        lightning.setSize(lightning.getWidth() * 0.5f, lightning.getHeight() * 0.5f); // Scale down
        lightning.setPosition(viewport.getWorldWidth() / 2 - lightning.getWidth() / 2,
            viewport.getWorldHeight()); // Start at the top of the screen
        lightning.setColor(1, 1, 1, 0); // Start invisible

        // Add animation for car logo
        carLogo.addAction(Actions.sequence(
            Actions.moveTo(viewport.getWorldWidth() / 2 - carLogo.getWidth() / 2, carLogo.getY(), 1.5f), // Drive in
            Actions.run(() -> lightning.addAction(Actions.sequence(
                Actions.fadeIn(0.1f),
                Actions.moveTo(viewport.getWorldWidth() / 2 - lightning.getWidth() / 2,
                    viewport.getWorldHeight() / 2 - lightning.getHeight() / 2, 0.1f)// Fall fast
            ))), // Trigger lightning
            Actions.delay(1f)
        ));

        // Add actors to the stage
        stage.addActor(carLogo);
        stage.addActor(lightning);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        duration += delta;

        // Go to the MenuScreen after INTRO_DURATION_IN_SEC seconds
        if (duration > INTRO_DURATION_IN_SEC) {
            game.setScreen(new MenuScreen());
        }

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
        assetManager.dispose();
    }
}
