package eu.elektropolnilnice.igra.minigame_filip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import eu.elektropolnilnice.igra.screen.MenuScreen;

public class FilipMenuScreen extends ScreenAdapter {

    private final AssetManager assetManager;
    private Viewport viewport;
    private Stage stage;
    private Skin skin;

    Texture background;
    Texture title;

    public FilipMenuScreen() {
        this.assetManager = Main.Instance().getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, Main.Instance().getBatch());
        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        background = new Texture("assets_raw/gameplay_filip/wallpaper.jpg");
        title = new Texture("assets_raw/gameplay_filip/title.png");

        stage.addActor(createUi());
        Gdx.input.setInputProcessor(stage);
    }

    public void dispose() {
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.LIGHT_GRAY);
        Main.Instance().getBatch().begin();
        Main.Instance().getBatch().setColor(1, 1, 1, 0.5f);
        Main.Instance().getBatch().draw(background, 0, 0, GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        Main.Instance().getBatch().end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

//        Label title = new Label("Not time to park", skin);
        Image titleImage = new Image(title);
        TextButton playButton = new TextButton("PLAY", skin);
        TextButton helpButton = new TextButton("How to play", skin);
        TextButton backButton = new TextButton("BACK", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new FilipGameScreen());
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new MenuScreen());
            }
        });

        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new FilipHelpScreen());
            }
        });

        table.add(titleImage).center();
        table.row();
        table.add(playButton).center().row();
        table.add(helpButton).center().row();
        table.add(backButton).center();
        table.center();
        table.setFillParent(true);

        return table;
    }
}
