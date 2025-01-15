package eu.elektropolnilnice.igra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class GameManager {
    public static final String PREFS_NAME = "game_settings";
    public static final String PREF_MUSIC_ENABLED = "music_enabled";
    public static final String PREF_MUSIC_VOLUME = "music_volume";
    public static final String PREF_SFX_VOLUME = "sfx_volume";

//    private static final String RESULTS_FILE = "score.json";

    private Preferences prefs;
    private Json json;
//TODO:    private List<Result> score;

    public GameManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public void saveSetting(String key, String value) {
        prefs.putString(key, value);
        prefs.flush();
    }

    public String getSetting(String key) {
        return prefs.getString(key);
    }

    public void playMusic(Music music) {
        boolean musicEnabled = prefs.getBoolean(PREF_MUSIC_ENABLED, true);
        if (musicEnabled) {
            music.setVolume(prefs.getFloat(PREF_MUSIC_VOLUME, 1));
            music.setLooping(true);
            music.play();
        }
    }

    public void stopMusic(Music music) {
        music.stop();
    }

    public void setMusicVolume(Music music) {
        music.setVolume(prefs.getFloat(PREF_MUSIC_VOLUME, 1));
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public void playSound(Sound sound) {
        sound.play(prefs.getFloat(PREF_SFX_VOLUME, 1));
    }
}
