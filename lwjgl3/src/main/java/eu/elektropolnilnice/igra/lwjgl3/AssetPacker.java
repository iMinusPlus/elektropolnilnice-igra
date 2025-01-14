package eu.elektropolnilnice.igra.lwjgl3;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {

    private static final boolean DRAW_DEBUG_OUTLINE = false;

    private static final String RAW_ASSETS_PATH = "lwjgl3/assets-raw";
    private static final String ASSETS_PATH = "assets";

    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.debug = DRAW_DEBUG_OUTLINE;
        settings.useIndexes = false; // Onemogoči uporabo indexov za razlikovanje slik

        TexturePacker.process(settings,
            RAW_ASSETS_PATH + "/gameplay",
            ASSETS_PATH + "/gameplay",
            "gameplay"
        );

//        TexturePacker.process(settings,
//            RAW_ASSETS_PATH + "/skin",
//            ASSETS_PATH + "/ui",
//            "uiskin"
//        );
    }
}
