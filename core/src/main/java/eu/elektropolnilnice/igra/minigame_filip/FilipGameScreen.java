package eu.elektropolnilnice.igra.minigame_filip;

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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import eu.elektropolnilnice.igra.Main;

public class FilipGameScreen extends ScreenAdapter {

    private OrthographicCamera camera;

    private TiledMap tiledMap;
    private TiledMapTileLayer parkingLot;
    private MapObjects carObjects;
    private MapObjects parking;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private Texture carB;
    private Texture carG;
    private Texture carR;
    private Texture carY;
    private Sprite player;

    private BitmapFont font;

    private float elapsedTime;

    float timeSinceFinish = 0f;

    boolean hasCollided = false;
    boolean isGameOver = false;

    private Stage stage;

    @Override
    public void show() {
        camera = new OrthographicCamera();

        tiledMap = new TmxMapLoader().load("assets_raw/gameplay_filip/parking_lot.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        parkingLot = (TiledMapTileLayer) tiledMap.getLayers().get("Lot");
        carObjects = tiledMap.getLayers().get("Cars").getObjects();
        parking = tiledMap.getLayers().get("Parking").getObjects();

//        tileWidth = parkingLot.getTileWidth();
//        tileHeight = parkingLot.getTileHeight();
        float mapWidthInPx = parkingLot.getWidth() * parkingLot.getTileWidth();
        float mapHeightInPx = parkingLot.getHeight() * parkingLot.getTileHeight();

        stage = new Stage();
        stage.addActor(sideNavigationUI());
        Gdx.input.setInputProcessor(stage);
        camera.setToOrtho(false, mapWidthInPx, mapHeightInPx);
        camera.update();

        carB = new Texture("assets_raw/gameplay_filip/carb.png");
        carY = new Texture("assets_raw/gameplay_filip/cary.png");
        carR = new Texture("assets_raw/gameplay_filip/carr.png");
        carG = new Texture("assets_raw/gameplay_filip/carg.png");

        for (MapObject mapObject : carObjects) {
            // get the loactions of these objects
            mapObject.getProperties().get("x");
            mapObject.getProperties().get("y");

            // Assuming the mapObject is of type RectangleMapObject
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

                // Get the position of the rectangle
                float x = rectangle.x;
                float y = rectangle.y;

                // Now you can render your car at (x, y)
//                renderCar(x, y);//
                Gdx.app.log("car", "x: " + x + " y: " + y);
            }
        }

        player = new Sprite(carB);
        player.setPosition(900f, 850f);
        player.setRotation(90);

        font = new BitmapFont();
        font.getData().setScale(5f);
        font.getColor().set(0f, 0f, 0f, 1f);

        elapsedTime = 0;
        isGameOver = false;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        float deltaTime = Gdx.graphics.getDeltaTime();
        timeSinceFinish += deltaTime;

        if (!isGameOver) {
            elapsedTime += delta;
            handleGameplayInput(delta);
        }

        camera.update();
        camera.zoom = .65f;
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        tiledMapRenderer.getBatch().begin();



        player.draw(tiledMapRenderer.getBatch());
        font.draw(tiledMapRenderer.getBatch(), "Time to park: " + String.format("%.2f", elapsedTime), 2525f, 2000f);

        for (MapObject mapObject : carObjects) {
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

                // Calculate the center of the rectangle
                float centerX = rectangle.x + rectangle.width / 2;
                float centerY = rectangle.y + rectangle.height / 2;

                // Create a new sprite for each car
                Sprite carSprite = new Sprite(carB);
                carSprite.setPosition(centerX - carB.getHeight(), centerY- carB.getWidth() / 2);

                // Optionally, set the size of the sprite
//                carSprite.setSize(rectangle.width, rectangle.height);
                carSprite.setRotation(90);

                // Draw the car sprite
                carSprite.draw(tiledMapRenderer.getBatch());
            }
        }

        tiledMapRenderer.getBatch().end();

        stage.act(deltaTime);
        stage.draw();

    }

    private void handleGameplayInput(float delta) {
        float speed = 250f; // Movement speed in pixels per second
        float rotationSpeed = 50f; // Rotation speed in degrees per second
        float dx = 0;
        float dy = 0;

        // Forward movement (W key)
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dx += (float) Math.cos(Math.toRadians(player.getRotation())) * speed * delta;
            dy += (float) Math.sin(Math.toRadians(player.getRotation())) * speed * delta;
        }

        // Backward movement (S key)
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dx -= (float) Math.cos(Math.toRadians(player.getRotation())) * speed * delta;
            dy -= (float) Math.sin(Math.toRadians(player.getRotation())) * speed * delta;
        }

        // Rotation (A and D keys)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.rotate(rotationSpeed * delta); // Rotate counterclockwise
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.rotate(-rotationSpeed * delta); // Rotate clockwise
        }

        // Calculate the next position
        float nextX = player.getX() + dx;
        float nextY = player.getY() + dy;

        // Check for collisions before applying the movement
//        if (!isCollidingWithStructures(nextX, nextY)) {
            player.setPosition(nextX, nextY);
//        }
    }

    private Table sideNavigationUI() {
        Table table = new Table();
        table.defaults().pad(20);

        TextButton backButton = new TextButton("BACK", Main.Instance().getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new FilipMenuScreen());
            }
        });

        table.bottom().pad(20);
        table.add(backButton).right();

        table.setFillParent(true);

        stage.addActor(table);

        return table;
    }

    @Override
    public void hide() {
        super.hide();
        this.dispose();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        carB.dispose();
        carG.dispose();
        carR.dispose();
        carY.dispose();
        font.dispose();
    }
}
