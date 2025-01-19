package eu.elektropolnilnice.igra.screen;

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
import eu.elektropolnilnice.igra.minigame_david.screen.DavidMenuScreen;
import eu.elektropolnilnice.igra.minigame_gabi.GabiMenuScreen;
import eu.elektropolnilnice.igra.utils.Station;

public class MenuScreen extends ScreenAdapter {

    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;

    private Station station;

    public MenuScreen(Station station) {
        this.assetManager = Main.Instance().getAssetManager();
        this.station = station;
    }
    public MenuScreen() { this.assetManager = Main.Instance().getAssetManager(); }

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

        Image logo = new Image(new Texture("assets_raw/el_icon.png"));

        Table stationData = new Table();

        if (station != null) {
            Window okno = new Window(station.title, skin);
            okno.defaults().pad(10);

            // Dodajamo podatke o polnilnici v okno
            Label titleLabel = new Label("Title: " + station.title, skin);
            Label postcodeLabel = new Label("Postcode: " + station.postcode, skin);
            Label countryLabel = new Label("Country: " + station.country, skin);
            Label townLabel = new Label("Town: " + station.town, skin);
            Label latitudeLabel = new Label("Latitude: " + station.lattitude, skin);
            Label longitudeLabel = new Label("Longitude: " + station.longitude, skin);

            okno.add(titleLabel).left().row();
            okno.add(postcodeLabel).left().row();
            okno.add(countryLabel).left().row();
            okno.add(townLabel).left().row();
            okno.add(latitudeLabel).left().row();
            okno.add(longitudeLabel).left().row();

            stationData.add(okno).pad(20).row();
        }



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

        table.add(logo).center().colspan(3).row();
        table.add(stationData).center().colspan(3).row();
        table.add(playButtonDavid).center().left();
        table.add(playButtonFilip).center().center();
        table.add(playButtonGabi).center().right().row();
        table.add(backButton).center().colspan(3).row();
        table.center();
        table.setFillParent(true);

        return table;
    }
}
