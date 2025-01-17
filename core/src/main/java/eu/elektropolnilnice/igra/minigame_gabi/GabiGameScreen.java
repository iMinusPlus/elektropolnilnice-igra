package eu.elektropolnilnice.igra.minigame_gabi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GabiGameScreen extends ScreenAdapter {
    private OrthographicCamera camera;

    private float tileWidth;
    private float tileHeight;
    private float mapWidthInPx;
    private float mapHeightInPx;

    private TiledMap tiledMap;
    private TiledMapTileLayer background;
    private TiledMapTileLayer road;
    private TiledMapTileLayer blockers;
    private MapObjects blockersObj;
    private MapObjects finishLine;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private Texture car;
    private Sprite player;

    private BitmapFont font;

    private int health;
    private int score;

    @Override
    public void show() {
        camera = new OrthographicCamera();

        tiledMap = new TmxMapLoader().load("assets_raw/gameplay_gabi/Track1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        background = (TiledMapTileLayer) tiledMap.getLayers().get("Background");
        road = (TiledMapTileLayer) tiledMap.getLayers().get("Road");
        blockersObj = tiledMap.getLayers().get("Barriers").getObjects();
        blockers = (TiledMapTileLayer) tiledMap.getLayers().get("Blockers");

        tileWidth = background.getTileWidth();
        tileHeight = background.getTileHeight();
        mapWidthInPx = background.getWidth() * background.getTileWidth();
        mapHeightInPx = background.getHeight() * background.getTileHeight();

        camera.setToOrtho(false, mapWidthInPx, mapHeightInPx);
        camera.update();

        //hitSound = Gdx.audio.newSound(Gdx.files.internal("tiled/hit.wav"));
        //drinkSound = Gdx.audio.newSound(Gdx.files.internal("tiled/drink.mp3"));
        car = new Texture("assets_raw/gameplay_gabi/car.png");
        player = new Sprite(car);
        player.setPosition(camera.viewportWidth /2f - player.getWidth() / 2f, camera.viewportHeight /2f - player.getHeight() / 2f);

        font = new BitmapFont();

        health = 100;
        score = 0;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        handleConfigurationInput();

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        tiledMapRenderer.getBatch().begin();

        if (health > 0) {
            handleGameplayInput();
            update();
        }else{
            font.draw(tiledMapRenderer.getBatch(), "GAME OVER", camera.viewportWidth / 2f - 50f, camera.viewportHeight / 2f);
        }


        player.draw(tiledMapRenderer.getBatch());
        font.draw(tiledMapRenderer.getBatch(), "SCORE: " + score, 20f, 20f);
        font.draw(tiledMapRenderer.getBatch(), "HEALTH: " + health, 20f, 30f + font.getCapHeight());
        tiledMapRenderer.getBatch().end();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            camera.position.set(mapWidthInPx / 2f, mapHeightInPx / 2f, 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            tiledMap.getLayers().get("Background").setVisible(!tiledMap.getLayers().get("Background").isVisible());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            tiledMap.getLayers().get("Background").setVisible(!tiledMap.getLayers().get("Background").isVisible());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            tiledMap.getLayers().get("Structures").setVisible(!tiledMap.getLayers().get("Structures").isVisible());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void handleGameplayInput() {
        float nextX = player.getX();
        float nextY = player.getY();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            nextX -= 100 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            nextX += 100 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            nextY += 100 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            nextY -= 100 * Gdx.graphics.getDeltaTime();
        }

        // Check collision with structures
        if (!isCollidingWithStructures(nextX, nextY)) {
            player.setPosition(nextX, nextY);
        }
    }

    private void update() {
        // calculate tile coordinates
        int tileX = (int) ((player.getX() + player.getWidth() / 2) / tileWidth);
        int tileY = (int) ((player.getY() + player.getHeight() / 2) / tileHeight);


        // check collision between player and obstacles
        for (MapObject mapObject : blockersObj) {
            if (player.getBoundingRectangle().overlaps(((RectangleMapObject) mapObject).getRectangle())) {
                if (isCollidingWithStructures(player.getX(), player.getY())) {
                    //TODO: make collision for barriers
                }
            }
        }
    }

    private boolean isCollidingWithStructures(float nextX, float nextY) {
        int tileX = (int) ((nextX + player.getWidth() / 2) / tileWidth);
        int tileY = (int) ((nextY + player.getHeight() / 2) / tileHeight);

        // Return true if there's a non-null cell in the structure layer
        return blockers.getCell(tileX, tileY) != null;
    }


    @Override
    public void dispose() {
        tiledMap.dispose();
        car.dispose();
        font.dispose();
    }
}
