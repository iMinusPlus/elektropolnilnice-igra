package eu.elektropolnilnice.igra.minigame_filip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Disposable;
import eu.elektropolnilnice.igra.Main;

public class Player {
    private Sprite sprite;
    private Polygon polygon;

    public Player(Sprite sprite) {
        this.sprite = sprite;
        // Initialize the polygon based on the sprite's dimensions
        // Initialize the polygon based on the sprite's dimensions
        polygon = new Polygon(new float[] {
                -sprite.getWidth() / 2, -sprite.getHeight() / 2, // Bottom-left
                sprite.getWidth() / 2, -sprite.getHeight() / 2 , // Bottom-right
                sprite.getWidth() / 2 , sprite.getHeight() / 2, // Top-right
                -sprite.getWidth() / 2, sprite.getHeight() / 2 // Top-left (y, x)
        });
        // Izračunaj središče poligona
        float[] vertices = polygon.getVertices();
        float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;

        // Poišči meje poligona
        for (int i = 0; i < vertices.length; i += 2) {
            float x = vertices[i];
            float y = vertices[i + 1];
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        // Nastavi center kot origin
        float centerX = (minX + maxX) / 2f;
        float centerY = (minY + maxY) / 2f;
        polygon.setOrigin(centerX, centerY);
        polygon.setPosition(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2 );
        updatePolygon();
    }

    public void update(float deltaTime, Batch batch) {

        float speed = 250f; // Movement speed in pixels per second
        float rotationSpeed = 50f; // Rotation speed in degrees per second
        float dx = 0;
        float dy = 0;

        // Forward movement (W key)
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dx += (float) Math.cos(Math.toRadians(sprite.getRotation())) * speed * deltaTime;
            dy += (float) Math.sin(Math.toRadians(sprite.getRotation())) * speed * deltaTime;
        }

        // Backward movement (S key)
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dx -= (float) Math.cos(Math.toRadians(sprite.getRotation())) * speed * deltaTime;
            dy -= (float) Math.sin(Math.toRadians(sprite.getRotation())) * speed * deltaTime;
        }

        // Rotation (A and D keys)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            sprite.rotate(rotationSpeed * deltaTime); // Rotate counterclockwise
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            sprite.rotate(-rotationSpeed * deltaTime); // Rotate clockwise
        }

        // Calculate the next position
        float nextX = sprite.getX() + dx;
        float nextY = sprite.getY() + dy;

        sprite.setPosition(nextX, nextY);
        updatePolygon();
        render(batch);
    }

    private void updatePolygon() {
        // Set the position and rotation of the polygon
        polygon.setPosition(sprite.getX(), sprite.getY());
        polygon.setRotation(sprite.getRotation());
    }

    public void render(Batch batch) {
        sprite.draw(batch);
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
