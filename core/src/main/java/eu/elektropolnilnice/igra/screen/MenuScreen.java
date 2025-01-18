package eu.elektropolnilnice.igra.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eu.elektropolnilnice.igra.GameConfig;
import eu.elektropolnilnice.igra.Main;
import eu.elektropolnilnice.igra.assets.AssetDescriptors;
import eu.elektropolnilnice.igra.minigame_david.screen.DavidMenuScreen;
import eu.elektropolnilnice.igra.minigame_gabi.GabiMenuScreen;

public class MenuScreen extends ScreenAdapter {

    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;

    public MenuScreen() {
        this.assetManager = Main.Instance().getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, Main.Instance().getBatch());

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

        Label title = new Label("MINIGAMEs", skin);
        TextButton playButtonDavid = new TextButton("David", skin);
        TextButton playButtonFilip = new TextButton("Filip", skin);
        TextButton playButtonGabi = new TextButton("Gabi", skin);
        TextButton backButton = new TextButton("Back", skin);

        playButtonDavid.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Setting david screen");
                Main.Instance().setScreen(new DavidMenuScreen());
            }
        });

        playButtonFilip.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Setting filip screen");
                Main.Instance().setScreen(new eu.elektropolnilnice.igra.minigame_filip.StartScreen());
            }
        });

        playButtonGabi.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Setting gabi screen");
                Main.Instance().setScreen(new GabiMenuScreen());
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Setting Tile screen");
                Main.Instance().setScreen(new MapScreen());
            }
        });

        table.add(title).center();
        table.row();
        table.add(playButtonDavid).center().row();
        table.add(playButtonFilip).center().row();
        table.add(playButtonGabi).center().row();
        table.add(backButton).center().row();
        table.center();
        table.setFillParent(true);

        return table;
    }
}
