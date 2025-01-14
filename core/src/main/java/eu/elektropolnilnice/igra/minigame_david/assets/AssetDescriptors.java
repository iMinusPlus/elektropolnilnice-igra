package eu.elektropolnilnice.igra.minigame_david.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {

    public static final AssetDescriptor<Skin> UI_SKIN =
        new AssetDescriptor<Skin>(AssetPaths.UI_SKIN, Skin.class);

//    public static final AssetDescriptor<BitmapFont> FONT32 =
//            new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT32, BitmapFont.class);
//
//    public static final AssetDescriptor<BitmapFont> FONT64 =
//        new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT64, BitmapFont.class);
//
//    public static final AssetDescriptor<TextureAtlas> GAME_PLAY =
//            new AssetDescriptor<TextureAtlas>(AssetPaths.GAME_PLAY, TextureAtlas.class);
//
//    public static final AssetDescriptor<Music> BACKGROUND_GAME_MUSIC =
//            new AssetDescriptor<Music>(AssetPaths.BACKGROUND_GAME_MUSIC, Music.class);
//
//    public static final AssetDescriptor<Music> BACKGROUND_MENU_MUSIC =
//        new AssetDescriptor<Music>(AssetPaths.BACKGROUND_MENU_MUSIC, Music.class);
//
//    public static final AssetDescriptor<Sound> CLICK_SOUND =
//        new AssetDescriptor<Sound>(AssetPaths.CLICK_SOUND, Sound.class);

    private AssetDescriptors() {
    }
}
