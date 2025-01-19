package eu.elektropolnilnice.igra.minigame_david.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eu.elektropolnilnice.igra.GameConfig;
import eu.elektropolnilnice.igra.Main;
import eu.elektropolnilnice.igra.assets.AssetDescriptors;
import eu.elektropolnilnice.igra.assets.RegionNames;

public class DavidHelpScreen extends ScreenAdapter {

    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas gameplay;

    public DavidHelpScreen() {
        assetManager = Main.Instance().getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, Main.Instance().getBatch());

        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.DAVID_GAMEPLAY);
        assetManager.finishLoading();

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplay = assetManager.get(AssetDescriptors.DAVID_GAMEPLAY);

        stage.addActor(createBackground());
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

    private Actor createBackground() {
        Image background = new Image(gameplay.findRegion(RegionNames.MENU_BACKGROUND));
        background.setFillParent(true);
        return background;
    }

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(10);
        table.setFillParent(true);

        // Ustvari temni prosojni kvadrat za besedilo
        Table textTable = new Table();
        Drawable textTableBackground = skin.getDrawable("content-background");
        textTable.setBackground(textTableBackground);
        textTable.pad(20);

        Label instructions = new Label(
            "Game Instructions:\n\n" +
                "This game is designed for two players. The goal is to reach a speed of 100 km/h as quickly as possible.\n\n" +
                "1. Player 1 uses the LEFT and RIGHT keys.\n" +
                "2. Player 2 uses the A and D keys.\n\n" +
                "If a player stops pressing the keys, their speed will start to decrease. The winner is the first to reach 100 km/h!",
            skin, "david"
        );
        instructions.setWrap(true);
        instructions.setAlignment(Align.center);

        textTable.add(instructions).growX();

        // Ustvari slike za kontrolne sheme
        Table imagesTable = new Table();
//        Image leftImage = new Image(gameplay);
//        Image rightImage = new Image(rightImageTexture);

//        imagesTable.add(leftImage).width(150).height(150).pad(20);
//        imagesTable.add(rightImage).width(150).height(150).pad(20);

        // Gumb za vrnitev v meni
        TextButton backButton = new TextButton("BACK", skin, "david");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.Instance().setScreen(new DavidMenuScreen());
            }
        });

        // Postavitev vsebine
        table.add(textTable).growX().padBottom(20).row();
        table.add(imagesTable).padBottom(20).row();
        table.add(backButton).center().width(200).height(50);

        return table;
    }

}
