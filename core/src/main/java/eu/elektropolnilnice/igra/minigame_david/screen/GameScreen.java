package eu.elektropolnilnice.igra.minigame_david.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eu.elektropolnilnice.igra.GameConfig;
import eu.elektropolnilnice.igra.Main;
import eu.elektropolnilnice.igra.assets.AssetDescriptors;

public class GameScreen extends ScreenAdapter {

    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;

    private OrthographicCamera camera;

    private float tileWidth;
    private float tileHeight;
    private float mapWidthInPx;
    private float mapHeightInPx;

    private TiledMap tiledMap;
    private TiledMapTileLayer grassLayer;
    private MapObjects mapObjects;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private Sound sound;
    private Texture carTexture;
    private Sprite player;

    private BitmapFont font;

    private int health;
    private int score;

    public GameScreen() {
        this.assetManager = Main.Instance().getAssetManager();
    }

    @Override
    public void show() {
        // Ustvarimo kamero in viewport, ki ohranjata razmerje zemljevida
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, camera);
        stage = new Stage(viewport, Main.Instance().getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        // Nalaganje zemljevida in rendererja
        tiledMap = new TmxMapLoader().load("tiledmap/minigame_road1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Pridobivanje dimenzij zemljevida
        grassLayer = (TiledMapTileLayer) tiledMap.getLayers().get("ozadje");
        tileWidth = grassLayer.getTileWidth();
        tileHeight = grassLayer.getTileHeight();
        mapWidthInPx = grassLayer.getWidth() * tileWidth;
        mapHeightInPx = grassLayer.getHeight() * tileHeight;

        // Kamera je postavljena na središče zemljevida
        camera.setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
        camera.position.set(mapWidthInPx / 2f, mapHeightInPx / 2f, 0);
        camera.update();

        // Nalaganje teksture in nastavljanje igralca
        carTexture = new Texture("gameplay/carBlue.png");
        player = new Sprite(carTexture);
        player.setScale(0.35f); // Pomanjšamo igralca na 50% originalne velikosti
        player.setPosition(mapWidthInPx / 2f - player.getWidth() / 2f, 0);

        font = new BitmapFont();

        health = 100;
        score = 0;

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

        handleConfigurationInput();

        if (health > 0) {
            handleGameplayInput();
            update();
        }

        // Kamera sledi igralcu
        camera.position.set(
            Math.max(camera.viewportWidth / 2f, Math.min(player.getX() + player.getWidth() / 2, mapWidthInPx - camera.viewportWidth / 2f)),
            Math.max(camera.viewportHeight / 2f, Math.min(player.getY() + player.getHeight() / 2, mapHeightInPx - camera.viewportHeight / 2f)),
            0
        );
        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        tiledMapRenderer.getBatch().begin();
        player.draw(tiledMapRenderer.getBatch());
        font.draw(tiledMapRenderer.getBatch(), "SCORE: " + score, 20f, camera.position.y + camera.viewportHeight / 2f - 20f);
        font.draw(tiledMapRenderer.getBatch(), "HEALTH: " + health, 20f, camera.position.y + camera.viewportHeight / 2f - 40f);
        tiledMapRenderer.getBatch().end();

        stage.act(delta);
        stage.draw();
    }



    private void handleGameplayInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setX(player.getX() - 100 * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setX(player.getX() + 100 * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setY(player.getY() + 100 * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setY(player.getY() - 100 * Gdx.graphics.getDeltaTime());
        }
    }

    private void handleConfigurationInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-8, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(8, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 8);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -8);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            camera.position.set(mapWidthInPx / 2f, mapHeightInPx / 2f, 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            tiledMap.getLayers().get("ozadje").setVisible(!tiledMap.getLayers().get("ozadje").isVisible());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            tiledMap.getLayers().get("cesta").setVisible(!tiledMap.getLayers().get("cesta").isVisible());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void update() {
        // calculate tile coordinates
        int tileX = (int) ((player.getX() + player.getWidth() / 2) / tileWidth);
        int tileY = (int) ((player.getY() + player.getHeight() / 2) / tileHeight);


        // check collision between player and obstacles
//        for (MapObject mapObject : mapObjects) {
//            if (player.getBoundingRectangle().overlaps(((RectangleMapObject) mapObject).getRectangle())) {
//                sound.play();
//                health--;
//            }
//        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        carTexture.dispose();
        font.dispose();
        stage.dispose();
    }

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        Label title = new Label("GAME SCREEN", skin);
        TextButton backButton = new TextButton("Back to Menu", skin);
        backButton.addListener(event -> {
            if (event.isHandled()) {
                Main.Instance().setScreen(new DavidMenuScreen());
            }
            return true;
        });

        table.add(title).center();
        table.row();
        table.add(backButton).center();
        table.center();
        table.setFillParent(true);

        return table;
    }
}
