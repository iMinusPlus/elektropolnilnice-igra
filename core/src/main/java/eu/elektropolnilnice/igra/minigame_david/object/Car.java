package eu.elektropolnilnice.igra.minigame_david.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Car {
    private float x;
    private float y;
    private float speed;
    private float friction;
    private TextureAtlas.AtlasRegion texture;

    public Car(float x, float y, float friction, TextureAtlas.AtlasRegion texture) {
        this.x = x;
        this.y = y;
        this.speed = 0f;
        this.friction = friction;
        this.texture = texture;
    }

    public void update(float delta, boolean accelerating) {
        if (accelerating) {
            speed += 100f * delta;
        }
        speed -= friction * delta;
        if (speed < 0) {
            speed = 0;
        }

        x += speed * delta;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void reset() {
        x = 0;
        speed = 0;
    }

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
}
