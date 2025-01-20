package eu.elektropolnilnice.igra.minigame_filip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import eu.elektropolnilnice.igra.Main;
import eu.elektropolnilnice.igra.utils.debug.DebugCameraController;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class FilipGameScreen extends ScreenAdapter {

    private OrthographicCamera camera;

    private TiledMap tiledMap;
    private TiledMapTileLayer parkingLot;
    private MapObjects carObjects;
    private MapObjects parking;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    Music carEngine;
    Music carCrash;
    private Texture carB;
    private Texture carG;
    private Texture carR;
    private Texture carY;
    private Player playerController;
    private Array<Integer> cars;
    private int freeParkingPos;
    private Pair<Float, Float> freeParkingPosVector;

    private BitmapFont font;

    private float elapsedTime;

    float timeSinceFinish = 0f;

    boolean hasCollided = false;
    boolean isGameOver = false;

    private Stage stage;

    // Debug
//    private DebugCameraController debugCameraController;
    boolean debug = true;
    ShapeRenderer shapeRenderer = new ShapeRenderer();

    @Override
    public void show() {
        camera = new OrthographicCamera();

        tiledMap = new TmxMapLoader().load("assets_raw/gameplay_filip/parking_lot.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        parkingLot = (TiledMapTileLayer) tiledMap.getLayers().get("Lot");
        carObjects = tiledMap.getLayers().get("Cars").getObjects();
        parking = tiledMap.getLayers().get("Parking").getObjects();

        float mapWidthInPx = parkingLot.getWidth() * parkingLot.getTileWidth();
        float mapHeightInPx = parkingLot.getHeight() * parkingLot.getTileHeight();

        stage = new Stage();
        stage.addActor(sideNavigationUI());
        Gdx.input.setInputProcessor(stage);
        camera.setToOrtho(false, mapWidthInPx, mapHeightInPx);
        camera.update();

        carEngine = Gdx.audio.newMusic(Gdx.files.internal("assets_raw/gameplay_filip/car_engine.wav"));
        carCrash = Gdx.audio.newMusic(Gdx.files.internal("assets_raw/gameplay_filip/car_crash.wav"));
        carB = new Texture("assets_raw/gameplay_filip/carb.png");
        carY = new Texture("assets_raw/gameplay_filip/cary.png");
        carR = new Texture("assets_raw/gameplay_filip/carr.png");
        carG = new Texture("assets_raw/gameplay_filip/carg.png");
        cars = new Array<>();

        boolean foundPosition = false;
        for (MapObject mapObject : carObjects) {

            // Assuming the mapObject is of type RectangleMapObject
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

                // Get the position of the rectangle
                float x = rectangle.x;
                float y = rectangle.y;

                Gdx.app.log("car", "x: " + x + " y: " + y);

                // Set free
                if (!foundPosition) {
                    Random random = new Random();
                    int randomNumber = random.nextInt(100);
                    if (randomNumber < 50) {
                        cars.add(-1);
                        foundPosition = true;
                        freeParkingPos = 0;
                        freeParkingPosVector = new Pair<>(x, y);
                        continue;
                    }
                }

                // Set random type
                Random random = new Random();
                int randomNumber = random.nextInt(4);
                cars.add(randomNumber);
            }
        }

        if (!foundPosition) {
            Random random = new Random();
            int randomNumber = random.nextInt(parking.getCount());
            if (parking.get(randomNumber) instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) parking.get(randomNumber)).getRectangle();
                freeParkingPos = randomNumber;
                freeParkingPosVector = new Pair<>(rectangle.x, rectangle.y);
                cars.add(-1);
            } else {
                throw new RuntimeException("Invalid parking object type");
            }
        }

        Gdx.app.log("Free", freeParkingPosVector.toString());

        Sprite playerSprite = new Sprite(carB);
        playerSprite.setPosition(900f, 850f);
        playerSprite.setRotation(90);

        playerController = new Player(playerSprite);

        font = new BitmapFont();
        font.getData().setScale(5f);
        font.getColor().set(0f, 0f, 0f, 1f);

        elapsedTime = 0;
        isGameOver = false;
        carEngine.play();
        carEngine.setLooping(true);
        carEngine.setVolume(0.5f);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        float deltaTime = Gdx.graphics.getDeltaTime();
        timeSinceFinish += deltaTime;

        if (!isGameOver) {
            elapsedTime += delta;
        }

        camera.update();
        camera.zoom = .65f;
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        tiledMapRenderer.getBatch().begin();


//        player.draw(tiledMapRenderer.getBatch());
        font.draw(tiledMapRenderer.getBatch(), "Time to park: " + String.format("%.2f", elapsedTime), 2525f, 2000f);

        int i = 0;
        for (MapObject mapObject : carObjects) {
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

                // Calculate the center of the rectangle
                float centerX = rectangle.x + rectangle.width / 2;
                float centerY = rectangle.y + rectangle.height / 2;

                // Create a new sprite for each car
                Texture sprite = getCarSprite(cars.get(i));
                if (sprite == null) {
                    i++;
                    continue;
                }
                Sprite carSprite = new Sprite(sprite);
                carSprite.setPosition(centerX - carB.getHeight(), centerY - carB.getWidth() / 2);
                carSprite.setRotation(90);

                // Draw the car sprite
                carSprite.draw(tiledMapRenderer.getBatch());
                i++;

//                System.out.println(playerController.checkOverlapPercentage(carSprite));
            }
        }

        playerController.update(deltaTime, tiledMapRenderer.getBatch());
        tiledMapRenderer.getBatch().end();

        if (debug) {
            // Draw the rectangles for all cars
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setProjectionMatrix(tiledMapRenderer.getBatch().getProjectionMatrix());
            shapeRenderer.setColor(Color.BLUE); // Set the color for the car rectangles

            for (MapObject mapObject : carObjects) {
                if (mapObject instanceof RectangleMapObject) {
                    Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                    shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height); // Draw the rectangle
                }
            }
            shapeRenderer.end();
        }


//         Draw the polygon for debugging
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED); // Set the color for the polygon
//        Gdx.app.log("pol", Arrays.toString(playerController.getPolygon().getTransformedVertices()));
//        shapeRenderer.polygon(playerController.getPolygon().getTransformedVertices()); // Draw the polygon
//        shapeRenderer.end();

        // Check for overlaps with the player's polygon
        checkCarOverlapsWithPlayer();

        if (false) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Uporabite Filled za debelo črto
            shapeRenderer.setProjectionMatrix(tiledMapRenderer.getBatch().getProjectionMatrix());
            // Nastavite barvo črte
            shapeRenderer.setColor(Color.RED);

            // Pridobite koordinate iz poligona
            float[] vertices = playerController.getPolygon().getTransformedVertices();

            // Narišite povezave med točkami z debelo črto
            for (int j = 0; j < vertices.length - 2; j += 2) {
                shapeRenderer.rectLine(vertices[j], vertices[j + 1], vertices[j + 2], vertices[j + 3], 10f); // Širina črte = 10 pikslov
            }

            // Povezava zadnje točke s prvo za zaključitev poligona
            shapeRenderer.rectLine(
                    vertices[vertices.length - 2], vertices[vertices.length - 1], // Zadnja točka
                    vertices[0], vertices[1],                                   // Prva točka
                    10f                                                         // Širina črte
            );

            shapeRenderer.end();
        }


        stage.act(deltaTime);
        stage.draw();
    }

    private void checkCarOverlapsWithPlayer() {
        Polygon playerPolygon = playerController.getPolygon();

        int carCount = 0;
        for (MapObject mapObject : carObjects) {
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

                // Create a polygon for the car
                Polygon carPolygon = new Polygon(new float[] {
                        rectangle.x, rectangle.y, // Bottom-left
                        rectangle.x + rectangle.width, rectangle.y, // Bottom-right
                        rectangle.x + rectangle.width, rectangle.y + rectangle.height, // Top-right
                        rectangle.x, rectangle.y + rectangle.height // Top-left
                });

                if (playerController.getSprite().getBoundingRectangle().overlaps(((RectangleMapObject) mapObject).getRectangle())) {
                    if (cars.get(carCount) == -1) {
                        carCount++;
                        //TODO check overlapping
//                        Gdx.app.log("free", "Car is free, no overlaps detected!");
                        float overlapPercentage = calculateOverlapPercentage(playerController.getSprite().getBoundingRectangle(), rectangle); // Uporabimo prejšnjo metodo
                        if (overlapPercentage > 50f) {
                            Gdx.app.log("prekrivanje", "Igralec prekriva avto z " + overlapPercentage + "%!");
                        }
                        continue;
                    }
                    Gdx.app.log("overlap", "Player is overlapping with a car!");
                    carCrash.setVolume(0.7f);
                    carCrash.play();
                }
            }

            carCount++;
        }
    }

    private Texture getCarSprite(int carType) {
        switch (carType) {
            case 0:
                return carB;
            case 1:
                return carG;
            case 2:
                return carR;
            case 3:
                return carY;
            default:
                return null;
        }
    }

    private Table sideNavigationUI() {
        Table table = new Table();
        table.defaults().pad(20);

        TextButton restartButton = new TextButton("Reset", Main.Instance().getSkin());
        TextButton backButton = new TextButton("BACK", Main.Instance().getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new FilipMenuScreen());
            }
        });

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new FilipGameScreen());
            }
        });

        table.right().pad(100);
        table.add(restartButton).right().row();
        table.add(backButton).right().row();

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
        carEngine.dispose();
        shapeRenderer.dispose();
    }

    private float calculateOverlapPercentage(Rectangle playerRect, Rectangle carRect) {
        Rectangle intersection = new Rectangle();
        if (Intersector.intersectRectangles(playerRect, carRect, intersection)) {
            float intersectionArea = intersection.width * intersection.height;
            float playerArea = playerRect.width * playerRect.height;
            return (intersectionArea / playerArea) * 100f; // Izračuna odstotek prekrivanja glede na igralčevo območje
        }
        return 0f; // Če ni prekrivanja
    }
}
