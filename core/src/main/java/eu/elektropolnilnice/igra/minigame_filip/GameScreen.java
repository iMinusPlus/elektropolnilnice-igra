package eu.elektropolnilnice.igra.minigame_filip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eu.elektropolnilnice.igra.GameConfig;
import eu.elektropolnilnice.igra.Main;
import eu.elektropolnilnice.igra.assets.AssetDescriptors;
import eu.elektropolnilnice.igra.utils.debug.DebugCameraController;

public class GameScreen extends ScreenAdapter {

    private final AssetManager assetManager;
    private Viewport viewportHUD;
    private Viewport viewportGameplay;
    private Stage stageHUD;
    private Stage stageGameplay;
    private Skin skin;
    private DebugCameraController debugCameraController;


    private ShapeRenderer shapeRenderer;
    private Vector2 lineStart; // Starting point of the line
    private Vector2 lineEnd;   // Ending point (mouse position)

    public GameScreen() {
        this.assetManager = Main.Instance().getAssetManager();
    }

    @Override
    public void show() {
        viewportHUD = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        viewportGameplay = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        stageHUD = new Stage(viewportHUD);
        stageGameplay = new Stage(viewportGameplay);
        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);

//        stage.addActor(createUI);
        addRectangles();
        Gdx.input.setInputProcessor(stageHUD);

        // Add these lines in the constructor or show() method
        shapeRenderer = new ShapeRenderer();
        lineStart = new Vector2();
        lineEnd = new Vector2();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.LIGHT_GRAY);

        stageHUD.act(delta);
        stageHUD.draw();
        stageGameplay.act(delta);
        stageGameplay.draw();

        debugCameraController.handleDebugInput(delta);

        // Draw the dragging line

        if ((lineStart == null) || (lineEnd == null)) {
            return;
        }

        if (!lineStart.isZero() && !lineEnd.isZero()) {
            float thickness = 30f;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);

            // Calculate the angle and draw the rectangle as the "line"
            float dx = lineEnd.x - lineStart.x;
            float dy = lineEnd.y - lineStart.y;
            float angle = (float) Math.atan2(dy, dx); // Angle of the line
            float length = (float) Math.sqrt(dx * dx + dy * dy); // Length of the line

            shapeRenderer.rectLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y, thickness); // A wide line
            shapeRenderer.end();
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stageHUD.dispose();
        shapeRenderer.dispose();
    }


    private void addRectangles() {
        float rectWidth = 80f; // Width of each rectangle
        float rectHeight = 80f; // Height of each rectangle
        float spacing = 10f; // Spacing between rectangles

        Drawable rectangleDrawable = skin.getDrawable("white"); // Use default drawable if available

        // Group to hold all rectangles for easier layout adjustments
        Group rectanglesGroup = new Group();

        Color[] colorArray = new Color[4];
        colorArray[0] = Color.RED;
        colorArray[1] = Color.BLUE;
        colorArray[2] = Color.GREEN;
        colorArray[3] = Color.YELLOW;

        // Left and Right sides
        for (int i = 0; i < 4; i++) {
            float yPosition = Gdx.graphics.getHeight() / 2f + (i - 1.5f) * (rectHeight + spacing);

            // Left side rectangles
            Image leftRect = new Image(rectangleDrawable);
            leftRect.setColor(colorArray[i]);
            leftRect.setSize(rectWidth, rectHeight);
            leftRect.setPosition(0, yPosition);
            leftRect.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    // Set the starting point to the center of the clicked rectangle
                    lineStart.set(leftRect.getX() + leftRect.getWidth() / 2f,
                        leftRect.getY() + leftRect.getHeight() / 2f);
                    lineEnd.set(lineStart); // Initialize the line end
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    // Update the end point to the current mouse position while dragging
                    lineEnd.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    // Reset the line when the mouse is released
                    lineStart.set(0, 0);
                    lineEnd.set(0, 0);
                }
            });

            // Right side rectangles
            Image rightRect = new Image(rectangleDrawable);
            rightRect.setColor(colorArray[i]);
            rightRect.setSize(rectWidth, rectHeight);
            rightRect.setPosition(Gdx.graphics.getWidth() - rectWidth, yPosition);

            rectanglesGroup.addActor(leftRect);
            rectanglesGroup.addActor(rightRect);
        }

        // Add the group of rectangles to the stage
        stageHUD.addActor(rectanglesGroup);
    }


}
