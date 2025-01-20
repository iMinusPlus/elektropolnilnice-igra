package eu.elektropolnilnice.igra.minigame_david.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
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
    private TiledMapTileLayer roadLayer;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

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

        grassLayer = (TiledMapTileLayer) tiledMap.getLayers().get("ozadje");
        roadLayer = (TiledMapTileLayer) tiledMap.getLayers().get("cesta");
        tileWidth = grassLayer.getTileWidth();
        tileHeight = grassLayer.getTileHeight();
        mapWidthInPx = grassLayer.getWidth() * tileWidth;
        mapHeightInPx = grassLayer.getHeight() * tileHeight;

        camera.setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
        camera.position.set(mapWidthInPx / 2f, mapHeightInPx / 2f, 0);
        camera.update();

        carTexture = new Texture("gameplay/carBlue.png");
        player = new Sprite(carTexture);
        player.setScale(0.35f);
        setPlayerStartPosition();

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

        handleGameplayInput();


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

    private boolean isTileWalkable(int tileX, int tileY) {
        TiledMapTileLayer.Cell cell = roadLayer.getCell(tileX, tileY);

        MapProperties properties = cell.getTile().getProperties();
        return properties.containsKey("walkable") && properties.get("walkable", Boolean.class);
    }


    private void handleGameplayInput() {
        float moveAmount = 100 * Gdx.graphics.getDeltaTime();
        float newPlayerX = player.getX();
        float newPlayerY = player.getY();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newPlayerX -= moveAmount;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newPlayerX += moveAmount;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            newPlayerY += moveAmount;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            newPlayerY -= moveAmount;
        }

        int tileX = (int) ((newPlayerX + player.getWidth() / 2) / tileWidth);
        int tileY = (int) ((newPlayerY + player.getHeight() / 2) / tileHeight);

//        player.setPosition(newPlayerX, newPlayerY);
        // Debug izpis
        System.out.println("TileX: " + tileX + ", TileY: " + tileY + ", Walkable: " + isTileWalkable(tileX, tileY));

        if (isTileWalkable(tileX, tileY)) {
            System.out.println("Moving player to X: " + newPlayerX + ", Y: " + newPlayerY);
            player.setPosition(newPlayerX, newPlayerY);
        } else {
            System.out.println("Tile not walkable!");
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
            camera.zoom = Math.max(0.1f, camera.zoom - 0.1f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            camera.zoom = Math.min(2f, camera.zoom + 0.1f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        camera.update();
    }

    private void setPlayerStartPosition() {
        MapObjects objects = tiledMap.getLayers().get("PlayerSpawn").getObjects();
        MapObject spawnPoint = objects.get("PlayerSpawn");

        float scaledWidth = player.getWidth() * player.getScaleX();
        float scaledHeight = player.getHeight() * player.getScaleY();

        if (spawnPoint instanceof RectangleMapObject) {
            RectangleMapObject rectangleObject = (RectangleMapObject) spawnPoint;

            float spawnX = rectangleObject.getRectangle().getX();
            float spawnY = rectangleObject.getRectangle().getY();

            player.setPosition(spawnX - scaledWidth / 2f, spawnY - scaledHeight / 2f);
        } else {
            player.setPosition(mapWidthInPx / 2f - scaledWidth / 2f, 0);
        }
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
