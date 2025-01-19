package eu.elektropolnilnice.igra.minigame_david.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eu.elektropolnilnice.igra.GameConfig;
import eu.elektropolnilnice.igra.Main;
import eu.elektropolnilnice.igra.assets.AssetDescriptors;
import eu.elektropolnilnice.igra.assets.RegionNames;
import eu.elektropolnilnice.igra.minigame_david.object.Car;
import eu.elektropolnilnice.igra.minigame_david.object.Player;

public class DavidMenuScreen extends ScreenAdapter {

    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas gameplay;
    private TextureAtlas cars;

    private Player player1;
    private Player player2;

    private TextField leftTextField;
    private TextField rightTextField;
    private TextureAtlas.AtlasRegion leftImage;
    private TextureAtlas.AtlasRegion rightImage;

    public DavidMenuScreen() {
        assetManager = Main.Instance().getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, Main.Instance().getBatch());

        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.DAVID_GAMEPLAY);
        assetManager.load(AssetDescriptors.DAVID_CARS);
        assetManager.finishLoading();

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplay = assetManager.get(AssetDescriptors.DAVID_GAMEPLAY);
        cars = assetManager.get(AssetDescriptors.DAVID_CARS);

        stage.addActor(backgroundUI());
        stage.addActor(menuUI());
        stage.addActor(createUI());
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

    private Actor backgroundUI() {
        Image background = new Image(gameplay.findRegion(RegionNames.MENU_BACKGROUND));
        background.setFillParent(true);
        return background;
    }

    private Actor menuUI() {
        Table table = new Table();
        table.defaults().pad(20);

        Image logo = new Image(gameplay.findRegion(RegionNames.LOGO_1));
        TextButton playButton = new TextButton("PLAY", skin, "david");
        TextButton howToPlayButton = new TextButton("HOW TO PLAY", skin, "david");

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player1 = new Player(
                    leftTextField.getText(),
                    new Car(50f, 550f, 50f, leftImage)
                );

                player2 = new Player(
                    rightTextField.getText(),
                    new Car(50f, 300f, 50f, rightImage)
                );

                Main.Instance().setScreen(new DavidGameScreen(player1, player2));
            }
        });

        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new DavidHelpScreen());
            }
        });

        table.add(logo).center().padBottom(50);
        table.row();
        table.add(playButton).center().width(270).height(50);
        table.row();
        table.add(howToPlayButton).center().width(270).height(50);
        table.center();
        table.setFillParent(true);

        return table;
    }


    private Actor createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(20);

        Array<TextureAtlas.AtlasRegion> allCars = new Array<>();
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_1)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_2)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_3)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_4)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_5)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_6)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_7)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_8)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_9)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_10)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_11)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_12)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_13)));
        allCars.add(new TextureAtlas.AtlasRegion(cars.findRegion(RegionNames.CAR_14)));

        final int[] leftCarIndex = {0};
        final int[] rightCarIndex = {1};

        TextureAtlas.AtlasRegion leftImage = allCars.get(leftCarIndex[0]);
        TextureAtlas.AtlasRegion rightImage = allCars.get(rightCarIndex[0]);

        TextField leftTextField = new TextField("Player 1", skin);
        TextButton leftButton1 = new TextButton("Next", skin);
        TextButton leftButton2 = new TextButton("Prev", skin);

        TextField rightTextField = new TextField("Player 2", skin);
        TextButton rightButton1 = new TextButton("Next", skin);
        TextButton rightButton2 = new TextButton("Prev", skin);

        Table leftContainer = new Table(skin);
        leftContainer.setBackground("content-background");
        leftContainer.add(new Image(leftImage)).pad(10).center().colspan(3).row();
        leftContainer.add(leftButton1).size(50, 50).left().padBottom(5);
        leftContainer.add(leftTextField).width(200).center().pad(5);
        leftContainer.add(leftButton2).size(50, 50).right().row();

        Table rightContainer = new Table(skin);
        rightContainer.setBackground("content-background");
        rightContainer.add(new Image(rightImage)).pad(10).center().colspan(3).row();
        rightContainer.add(rightButton1).size(50, 50).left().padBottom(5);
        rightContainer.add(rightTextField).width(200).center().pad(5);
        rightContainer.add(rightButton2).size(50, 50).right().row();

        table.add(leftContainer).bottom().left().expand();
        table.add(rightContainer).bottom().right().expand();

        this.leftTextField = leftTextField;
        this.rightTextField = rightTextField;
        this.leftImage = leftImage;
        this.rightImage = rightImage;

        leftButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Naslednji avto za leva stran
                leftCarIndex[0] = (leftCarIndex[0] + 1) % allCars.size;
                leftImage.setRegion(allCars.get(leftCarIndex[0]));
            }
        });

        leftButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Prejšnji avto za leva stran
                leftCarIndex[0] = (leftCarIndex[0] - 1 + allCars.size) % allCars.size;
                leftImage.setRegion(allCars.get(leftCarIndex[0]));
            }
        });

        rightButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Naslednji avto za desno stran
                rightCarIndex[0] = (rightCarIndex[0] + 1) % allCars.size;
                rightImage.setRegion(allCars.get(rightCarIndex[0]));
            }
        });

        rightButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Prejšnji avto za desno stran
                rightCarIndex[0] = (rightCarIndex[0] - 1 + allCars.size) % allCars.size;
                rightImage.setRegion(allCars.get(rightCarIndex[0]));
            }
        });

        return table;
    }



}
