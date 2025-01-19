package eu.elektropolnilnice.igra.minigame_david.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eu.elektropolnilnice.igra.GameConfig;
import eu.elektropolnilnice.igra.Main;
import eu.elektropolnilnice.igra.assets.AssetDescriptors;
import eu.elektropolnilnice.igra.assets.RegionNames;
import eu.elektropolnilnice.igra.minigame_david.object.Car;

public class DavidGameScreen extends ScreenAdapter {

    private final AssetManager assetManager;
    private final SpriteBatch batch;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas gameplay;
    private TextureAtlas cars;

    private float backgroundX;
    private static float BACKGROUND_SPEED = 0f;

    Car player1Car;
    Car player2Car;
    Car leadCar;
    Car lastCar;

    Label labelPlayer1;
    Label labelPlayer2;

    private boolean gameover = false;

    public DavidGameScreen() {
        assetManager = Main.Instance().getAssetManager();
        batch = Main.Instance().getBatch();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, Main.Instance().getBatch());

        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.DAVID_GAMEPLAY);
        assetManager.load(AssetDescriptors.DAVID_CARS);
        assetManager.finishLoading();

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplay = assetManager.get(AssetDescriptors.DAVID_GAMEPLAY);
        cars = assetManager.get(AssetDescriptors.DAVID_CARS);

        player1Car = new Car("Player 1", 50f, 300f, 50f, cars.findRegion(RegionNames.CAR_1));
        player2Car = new Car("Player 2", 50f, 550f, 50f, cars.findRegion(RegionNames.CAR_2));

        stage.addActor(speedometersUI());
        stage.addActor(bottomNavigationUI());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.LIGHT_GRAY);

        if (player1Car.getSpeed() >= 1000 || player2Car.getSpeed() >= 1000) {
            gameover = true;
        }

        if (gameover) {
            // Ustavitev ozadja
            BACKGROUND_SPEED = 0f;

            // Premik avtov izven okna
            player1Car.setX(player1Car.getX() + player1Car.getSpeed() * Gdx.graphics.getDeltaTime());
            player2Car.setX(player2Car.getX() + player2Car.getSpeed() * Gdx.graphics.getDeltaTime());

            // Premik ozadja
            backgroundX -= BACKGROUND_SPEED * delta;
            if (backgroundX <= -gameplay.findRegion(RegionNames.BACKGROUND).getRegionWidth()) {
                backgroundX = 0; // Ponovi ozadje
            }

            // Prikaži modalno okno samo, če še ni bilo prikazano
            if (stage.getRoot().findActor("gameoverDialog") == null) {
                showGameOverDialog();
            }
        }
        else {
            player1Car.update(delta, Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT));
            player2Car.update(delta, Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D));

            labelPlayer1.setText((int) player1Car.getSpeed()/10);
            labelPlayer2.setText((int) player2Car.getSpeed()/10);

            // dolocanje hitrosti ozadja na podlagi hitrejsega avtomobila
            BACKGROUND_SPEED = Math.max(player1Car.getSpeed(), player2Car.getSpeed());

            // dolocanje vodilnega in zadnjega
            if (player1Car.getSpeed() > player2Car.getSpeed()) {
                leadCar = player1Car;
                lastCar = player2Car;
            } else if (player2Car.getSpeed() > player1Car.getSpeed()) {
                leadCar = player2Car;
                lastCar = player1Car;
            }

            // izracun trenutne razlike med hitrostmi
            float speedDifference, targetLeadOffset, currentLeadOffset;
            if (leadCar != null & lastCar != null){
                speedDifference = Math.abs(leadCar.getSpeed() - lastCar.getSpeed());
                targetLeadOffset = 100 + speedDifference;
                currentLeadOffset = leadCar.getX() - lastCar.getX();
            } else {
                speedDifference = Math.abs(player1Car.getSpeed() - player2Car.getSpeed());
                targetLeadOffset = 100 + speedDifference;
                currentLeadOffset = player1Car.getX() - player2Car.getX();
            }

            // interpolacija za gladko spremembo pozicije
            float interpolationSpeed = 5f; // visja vrednost = hitrejsa sprememba
            float interpolatedLeadOffset = currentLeadOffset + (targetLeadOffset - currentLeadOffset) * delta * interpolationSpeed;

            // nastavljanje pozicij avtomobilov glede na vodstvo
            if (player1Car.getSpeed() > player2Car.getSpeed()) {
                player1Car.setX(Math.min(player1Car.getX(), GameConfig.HUD_WIDTH - player1Car.getTexture().getRegionWidth()));
                player2Car.setX(player1Car.getX() - interpolatedLeadOffset);
            } else if (player2Car.getSpeed() > player1Car.getSpeed()) {
                player2Car.setX(Math.min(player2Car.getX(), GameConfig.HUD_WIDTH - player2Car.getTexture().getRegionWidth()));
                player1Car.setX(player2Car.getX() - interpolatedLeadOffset);
            }

            // avtomobili ne gredo izven zaslona
            player1Car.setX(Math.max(50f, Math.min(player1Car.getX(), GameConfig.HUD_WIDTH - player1Car.getTexture().getRegionWidth()*1.2f)));
            player2Car.setX(Math.max(50f, Math.min(player2Car.getX(), GameConfig.HUD_WIDTH - player2Car.getTexture().getRegionWidth()*1.2f)));

            // premik ozadja
            backgroundX -= BACKGROUND_SPEED * delta;
            if (backgroundX <= -gameplay.findRegion(RegionNames.BACKGROUND).getRegionWidth()) {
                backgroundX = 0;
            }
        }

        batch.begin();

        // ozadje (pomikanje ozadja)
        batch.draw(gameplay.findRegion(RegionNames.BACKGROUND), backgroundX, 0);
        batch.draw(gameplay.findRegion(RegionNames.BACKGROUND), backgroundX + gameplay.findRegion(RegionNames.BACKGROUND).getRegionWidth(), 0);

        // avtomobili
        player1Car.render(batch);
        player2Car.render(batch);

        batch.end();

        stage.act(delta);
        stage.draw();
    }


    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Table speedometersUI() {
        // Glavna tabela
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().pad(20);

        // Ustvarimo slike za števce
        Image speedometerLeft = new Image(gameplay.findRegion(RegionNames.SPEEDOMETER));
        Image speedometerRight = new Image(gameplay.findRegion(RegionNames.SPEEDOMETER));

        // Ustvarimo besedili za števce
        labelPlayer1 = new Label("0", skin, "david");
        labelPlayer2 = new Label("0", skin, "david");

        labelPlayer1.setAlignment(Align.center);
        labelPlayer2.setAlignment(Align.center);

        // Uporabimo Stack za levega igralca
        Stack leftStack = new Stack();
        leftStack.add(speedometerLeft); // Dodamo sliko števca
        leftStack.add(labelPlayer1);   // Dodamo besedilo na sredino slike

        // Uporabimo Stack za desnega igralca
        Stack rightStack = new Stack();
        rightStack.add(speedometerRight); // Dodamo sliko števca
        rightStack.add(labelPlayer2);     // Dodamo besedilo na sredino slike

        // Dodamo Stack elemente v glavno tabelo
        table.add(leftStack).expandX().left().padRight(50).width(200).height(200); // Leva stran
        table.add(rightStack).expandX().right().padLeft(50).width(200).height(200); // Desna stran

        return table;
    }


    private Table bottomNavigationUI() {
        Table table = new Table();
        table.defaults().pad(20);

        TextButton backButton = new TextButton("BACK", skin, "david");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new DavidMenuScreen());
            }
        });

        table.bottom().pad(20);
        table.add(backButton).center();

        table.setFillParent(true);

        stage.addActor(table);

        return table;
    }

    private void showGameOverDialog() {
        Dialog gameOverDialog = new Dialog("", skin, "david") {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    Main.Instance().setScreen(new DavidMenuScreen());
                }
            }
        };

        gameOverDialog.setMovable(false);

        TextureAtlas.AtlasRegion winnerTexture;
        String winnerText;

        if (leadCar == player1Car) {
            winnerTexture = player1Car.getTexture();
            winnerText = player1Car.getName() + " is the Winner!";
        } else if (leadCar == player2Car) {
            winnerTexture = player2Car.getTexture();
            winnerText = player2Car.getName() + " is the Winner!";
        } else {
            winnerTexture = gameplay.findRegion(RegionNames.CAR_1);
            winnerText = "It's a TIE!";
        }

        Table contentTable = new Table();
        contentTable.defaults().pad(20);

        Image winnerImage = new Image(winnerTexture);
        contentTable.add(winnerImage).center().size(200, 100);
        contentTable.row();

        Label winnerLabel = new Label(winnerText, skin, "david");
//        winnerLabel.setFontScale(1.5f);
        contentTable.add(winnerLabel).center();

        gameOverDialog.getContentTable().add(contentTable).center();
        gameOverDialog.getContentTable().row();

        TextButton okButton = new TextButton("OK", skin, "david");
        gameOverDialog.button(okButton, true).pad(20);

        gameOverDialog.setSize(600, 400);
        gameOverDialog.setPosition(
            (GameConfig.HUD_WIDTH - gameOverDialog.getWidth()) / 2,
            (GameConfig.HUD_HEIGHT - gameOverDialog.getHeight()) / 2
        );

        gameOverDialog.setName("gameoverDialog");

        gameOverDialog.show(stage);
    }
}

