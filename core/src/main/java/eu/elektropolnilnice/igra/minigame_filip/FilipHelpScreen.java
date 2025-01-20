package eu.elektropolnilnice.igra.minigame_filip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eu.elektropolnilnice.igra.GameConfig;
import eu.elektropolnilnice.igra.Main;
import eu.elektropolnilnice.igra.assets.AssetDescriptors;
import eu.elektropolnilnice.igra.minigame_gabi.GabiMenuScreen;

public class FilipHelpScreen extends ScreenAdapter {

    private Stage stage;// Replace `MyGame` with your main game class
    private Skin skin;
    AssetManager assetManager;
    Texture background;

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        background = new Texture("assets_raw/gameplay_filip/wallpaper.jpg");
        assetManager = Main.Instance().getAssetManager();
        skin = assetManager.get(AssetDescriptors.UI_SKIN); // Adjust path to your skin file
        Gdx.input.setInputProcessor(stage);
        createUi();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Main.Instance().getBatch().begin();
        Main.Instance().getBatch().setColor(1, 1, 1, 0.5f);
        Main.Instance().getBatch().draw(background, 0, 0, GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        Main.Instance().getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    private void createUi() {
        // Help text
        Label helpLabel = new Label(
            "Welcome to the Time to Park minigame!\n" +
                "Objective: Try to park your car in the shortest time possible\n\n" +
                "- Use WASD keys to control your movement.\n" +
                "- DO NOT bump into other cars\n",
            skin);
        helpLabel.setWrap(true);
        helpLabel.setPosition(Gdx.graphics.getWidth() / 2f - helpLabel.getWidth() / 2, Gdx.graphics.getHeight() * 0.6f);
        helpLabel.setScale(2f);

        // Back button
        TextButton backButton = new TextButton("Back to Menu", skin);
        backButton.setSize(200, 50);
        backButton.setPosition(
            (Gdx.graphics.getWidth() - backButton.getWidth()) / 2,
            Gdx.graphics.getHeight() * 0.2f);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Main.Instance().setScreen(new FilipMenuScreen()); // Replace with your main menu screen
            }
        });

        // Add actors to stage
        stage.addActor(helpLabel);
        stage.addActor(backButton);
    }
}
