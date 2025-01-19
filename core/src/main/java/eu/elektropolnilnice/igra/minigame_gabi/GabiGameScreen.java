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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private float elapsedTime;
    private int laps;
    private boolean raceCompleted;

    float timeSinceFinish = 0f;
    float timeSinceCollision = 0f;

    private List<Float> leaderboard;

    Boolean hasCollided = false;

    // Dynamic speed variables
    private float playerSpeed = 700f;  // Initial speed
    private float maxSpeed = 1000f;    // Maximum speed the player can reach
    private float accelerationRate = 150f; // How fast the player speeds up
    private float decelerationRate = 40f; // How fast the player slows down after a collision

    @Override
    public void show() {
        camera = new OrthographicCamera();

        tiledMap = new TmxMapLoader().load("assets_raw/gameplay_gabi/Track1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        background = (TiledMapTileLayer) tiledMap.getLayers().get("Background");
        road = (TiledMapTileLayer) tiledMap.getLayers().get("Road");
        blockersObj = tiledMap.getLayers().get("Barriers").getObjects();
        blockers = (TiledMapTileLayer) tiledMap.getLayers().get("Blockers");
        finishLine = tiledMap.getLayers().get("Finish line").getObjects();

        tileWidth = background.getTileWidth();
        tileHeight = background.getTileHeight();
        mapWidthInPx = background.getWidth() * background.getTileWidth();
        mapHeightInPx = background.getHeight() * background.getTileHeight();

        camera.setToOrtho(false, mapWidthInPx, mapHeightInPx);
        camera.update();

        car = new Texture("assets_raw/gameplay_gabi/car.png");
        player = new Sprite(car);
        player.setPosition(600f, 850f);
        player.setRotation(90);

        font = new BitmapFont();
        font.getData().setScale(5f);
        font.getColor().set(0f, 0f, 0f, 1f);

        elapsedTime = 0;
        laps = 0;
        raceCompleted = false;

        leaderboard = new ArrayList<>();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        float deltaTime = Gdx.graphics.getDeltaTime();
        timeSinceFinish += deltaTime;
        timeSinceCollision += deltaTime;

        if (!raceCompleted) {
            elapsedTime += delta;
            handleGameplayInput(delta);
            checkCollisions();
        }

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        tiledMapRenderer.getBatch().begin();

        player.draw(tiledMapRenderer.getBatch());
        font.draw(tiledMapRenderer.getBatch(), "Laps: " + laps + "/3", 20f, 70f);
        font.draw(tiledMapRenderer.getBatch(), "Time: " + String.format("%.2f", elapsedTime), 20f, 120f);

        if (raceCompleted) {
            font.draw(tiledMapRenderer.getBatch(), "Race Complete!", camera.viewportWidth / 2f - 50f, camera.viewportHeight / 2f);
            font.draw(tiledMapRenderer.getBatch(), "Leaderboard:", camera.viewportWidth / 2f - 50f, camera.viewportHeight / 2f - 150f);
            for (int i = 0; i < leaderboard.size(); i++) {
                font.draw(tiledMapRenderer.getBatch(), (i + 1) + ". " + String.format("%.2f", leaderboard.get(i)), camera.viewportWidth / 2f - 50f, camera.viewportHeight / 2f - 75f - (i * 20));
            }
        }
        if (timeSinceCollision > 2f){
            playerSpeed = Math.min((playerSpeed + accelerationRate * delta), maxSpeed);
        }

        tiledMapRenderer.getBatch().end();
    }

    private void handleGameplayInput(float delta) {
        // Gradually increase the speed over time
        playerSpeed = Math.min(playerSpeed + accelerationRate * delta, maxSpeed);

        // Movement deltas
        float dx = 0;
        float dy = 0;

        // Forward movement (W key)
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dx += (float) Math.cos(Math.toRadians(player.getRotation())) * playerSpeed * delta;
            dy += (float) Math.sin(Math.toRadians(player.getRotation())) * playerSpeed * delta;
        }

        // Backward movement (S key)
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dx -= (float) Math.cos(Math.toRadians(player.getRotation())) * playerSpeed * delta;
            dy -= (float) Math.sin(Math.toRadians(player.getRotation())) * playerSpeed * delta;
        }

        // Rotation (A and D keys)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.rotate(200f * delta); // Rotation speed
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.rotate(-200f * delta); // Rotation speed
        }

        // Calculate the next position
        float nextX = player.getX() + dx;
        float nextY = player.getY() + dy;

        // Check for collisions before applying the movement
        if (!isCollidingWithStructures(nextX, nextY)) {
            player.setPosition(nextX, nextY);
        }
    }

    private void checkCollisions() {
        for (MapObject mapObject : blockersObj) {
            if (player.getBoundingRectangle().overlaps(((RectangleMapObject) mapObject).getRectangle())) {
                // Collision with barrier - slow down the player
                playerSpeed = Math.max(playerSpeed - decelerationRate, 200f); // Don't go below 200 speed
                timeSinceCollision = 0f;
            }
        }

        for (MapObject mapObject : finishLine) {
            if (timeSinceFinish > 2f) {
                hasCollided = false;
                timeSinceFinish = 0f;
            }
            if (hasCollided) {
                return;
            }

            if (player.getBoundingRectangle().overlaps(((RectangleMapObject) mapObject).getRectangle())) {
                laps++;
                if (laps >= 3) {
                    raceCompleted = true;
                    leaderboard.add(elapsedTime);
                    Collections.sort(leaderboard);
                }
                hasCollided = true;
            }
        }
    }

    private boolean isCollidingWithStructures(float nextX, float nextY) {
        int tileX = (int) ((nextX + player.getWidth() / 2) / tileWidth);
        int tileY = (int) ((nextY + player.getHeight() / 2) / tileHeight);

        return blockers.getCell(tileX, tileY) != null;
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        car.dispose();
        font.dispose();
    }
}
