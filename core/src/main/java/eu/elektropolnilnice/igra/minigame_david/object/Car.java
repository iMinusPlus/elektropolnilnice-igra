package eu.elektropolnilnice.igra.minigame_david.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Car {
    private String name;
    private float x;
    private float y;
    private float speed;
    private float friction;
    private TextureAtlas.AtlasRegion texture;

    public Car(String name, float x, float y, float friction, TextureAtlas.AtlasRegion texture) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.speed = 0f;
        this.friction = friction;
        this.texture = texture;
    }

    // Posodobi hitrost in pozicijo avtomobila
    public void update(float delta, boolean accelerating) {
        if (accelerating) {
            // Pospeši do največje hitrosti
            speed += 100f * delta; // Faktor pospeševanja
        }
        speed -= friction * delta;
        if (speed < 0) {
            speed = 0; // Prepreči negativno hitrost
        }

        // Posodobi pozicijo glede na hitrost
        x += speed * delta;
    }

    // Nariši avtomobil na trenutni poziciji
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    // Ponastavi pozicijo in hitrost avtomobila
    public void reset() {
        x = 0;
        speed = 0;
    }

    // Getterji in setterji za lastnosti avtomobila
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public TextureAtlas.AtlasRegion getTexture() {
        return texture;
    }

    public void setTexture(TextureAtlas.AtlasRegion texture) {
        this.texture = texture;
    }

    public String getName() {
        return name;
    }
}
