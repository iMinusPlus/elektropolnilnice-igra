package eu.elektropolnilnice.igra;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import eu.elektropolnilnice.igra.screen.MapScreen;
import eu.elektropolnilnice.igra.screen.MenuScreen;

public class IntroScreen extends ScreenAdapter {
    public static final float INTRO_DURATION_IN_SEC = 3f;   // duration of the intro animation

    private final Game game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private BitmapFont font;

    private float duration = 0f;

    Group group = new Group();
    public IntroScreen(Game game) {
        this.game = game;
        assetManager = new AssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport);

        // Load font
        font = new BitmapFont();
        font.getData().setScale(2f); // Scale font to make it bigger

        stage.addActor(createCentralI());
        stage.addActor(createDash());
        stage.addActor(createPlus());
        group.setScale(10f);
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
             game.setScreen(new MapScreen());
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
        font.dispose();
    }


    private Actor createCentralI() {
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label centralI = new Label("i", style);
        group.addActor(centralI);

        centralI.setPosition(
            viewport.getWorldWidth() / 2f - centralI.getWidth() / 2f,
            viewport.getWorldHeight() / 2f - centralI.getHeight() / 2f
        );
        return centralI;
    }

    private Actor createDash() {
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label dash = new Label("-", style);
        group.addActor(dash);

        // Start position: Bottom of the screen
        dash.setPosition(
            viewport.getWorldWidth() / 2f - dash.getWidth() / 2f,
            -dash.getHeight()
        );

        // Final position: Center of the screen (slightly below "i")
        float finalX = viewport.getWorldWidth() / 2f - dash.getWidth() / 2f + 20f;
        float finalY = viewport.getWorldHeight() / 2f - 20;

        // Add animation with progressively smaller bounces
        dash.addAction(Actions.sequence(
            Actions.moveTo(finalX, finalY + 40, 0.6f),  // Large bounce
            Actions.moveBy(0, -30, 0.4f),              // Medium downward bounce
            Actions.moveBy(0, 20, 0.4f),               // Small upward bounce
            Actions.moveBy(0, -10, 0.3f),              // Final small downward bounce
            Actions.moveTo(finalX, finalY, 0.2f)       // Smoothly settle at the final position
        ));

        return dash;
    }


    private Actor createPlus() {
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label plus = new Label("+", style);
        group.addActor(plus);

        // Start position: Top of the screen
        plus.setPosition(
            viewport.getWorldWidth() / 2f - plus.getWidth() / 2f,
            viewport.getWorldHeight()
        );

        // Final position: Center of the screen (slightly above "i")
        float finalX = viewport.getWorldWidth() / 2f - plus.getWidth() / 2f + 50f;
        float finalY = viewport.getWorldHeight() / 2f - 20;

        // Add animation with progressively smaller bounces
        plus.addAction(Actions.sequence(
            Actions.moveTo(finalX, finalY - 40, 0.6f),  // Large bounce
            Actions.moveBy(0, 30, 0.4f),               // Medium upward bounce
            Actions.moveBy(0, -20, 0.4f),              // Small downward bounce
            Actions.moveBy(0, 10, 0.3f),               // Final small upward bounce
            Actions.moveTo(finalX, finalY, 0.2f)       // Smoothly settle at the final position
        ));

        return plus;
    }


}
