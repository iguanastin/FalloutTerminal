package fot.terminal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.Random;

/**
 * @author austinbt
 */
public abstract class TerminalAudio {

    private static Music backgroundAudioA, backgroundAudioB, backgroundAudioC;
    private static Sound buttonClickSound1, buttonClickSound2, buttonClickSound3;
    private static Sound menuCancelSound;
    private static Sound menuSelectSound;
    private static Sound charScrollSound;
    private static Sound charSingleSound1, charSingleSound2, charSingleSound3, charSingleSound4, charSingleSound5, charSingleSound6;
    private static Sound passGoodSound, passBadSound;
    private static Sound charMultipleSound1, charMultipleSound2, charMultipleSound3, charMultipleSound4;

    private static boolean playAudio = true;
    private static float backgroundAudioVolume = 1f;
    private static float soundFXVolume = 1f;
    private static float masterVolume = 1f;
    private static boolean loaded = false;

    public static void setMute(boolean mute) {
        playAudio = !mute;
    }

    public static void setSoundEffectsVolume(float volume) {
        soundFXVolume = volume;
    }

    public static void setBackgroundVolume(float volume) {
        backgroundAudioVolume = volume;
    }

    public static void setMasterVolume(float volume) {
        masterVolume = volume;
    }

    public static float getBackgroundAudioVolume() {
        return backgroundAudioVolume;
    }

    public static float getMasterVolume() {
        return masterVolume;
    }

    public static float getSoundFXVolume() {
        return soundFXVolume;
    }

    /**
     * Starts a random background audio file
     */
    public static void startBackgroundAudio() {
        if (!playAudio || !isLoaded()) {
            return;
        }

        //Play background music
        switch (new Random().nextInt(3)) {
            case 0:
                backgroundAudioA.play();
                break;
            case 1:
                backgroundAudioB.play();
                break;
            case 2:
                backgroundAudioC.play();
                break;
        }

        Gdx.app.log("Setup", "(" + TerminalMain.getRunTime() + ") Started background audio. (A:" + backgroundAudioA.isPlaying() + ", B:" + backgroundAudioB.isPlaying() + ", C:" + backgroundAudioC.isPlaying() + ")");
    }

    /**
     * Plays a randomized button click sound effect.
     *
     * If audio has not been loaded, a NullPointerException will be thrown.
     */
    public static void playButtonClick() {
        if (!playAudio || !isLoaded()) {
            return;
        }

        switch (new Random().nextInt(3)) {
            case 0:
                buttonClickSound1.play(soundFXVolume);
                break;
            case 1:
                buttonClickSound2.play(soundFXVolume);
                break;
            case 2:
                buttonClickSound3.play(soundFXVolume);
                break;
        }
    }

    public static void playMenuCancel() {
        if (!playAudio || !isLoaded()) {
            return;
        }

        menuCancelSound.play(soundFXVolume);
    }

    public static void playMenuSelect() {
        if (!playAudio || !isLoaded()) {
            return;
        }

        menuSelectSound.play(soundFXVolume);
    }

    public static void playCharScroll() {
        if (!playAudio || !isLoaded()) {
            return;
        }

        charScrollSound.play(soundFXVolume);
    }

    public static void playCharSingle() {
        if (!playAudio || !isLoaded()) {
            return;
        }

        switch (new Random().nextInt(6)) {
            case 0:
                charSingleSound1.play(soundFXVolume);
                break;
            case 1:
                charSingleSound2.play(soundFXVolume);
                break;
            case 2:
                charSingleSound3.play(soundFXVolume);
                break;
            case 3:
                charSingleSound4.play(soundFXVolume);
                break;
            case 4:
                charSingleSound5.play(soundFXVolume);
                break;
            case 5:
                charSingleSound6.play(soundFXVolume);
                break;
        }
    }

    public static void playPassGood() {
        if (!playAudio || !isLoaded()) {
            return;
        }

        passGoodSound.play(soundFXVolume);
    }

    public static void playPassBad() {
        if (!playAudio || !isLoaded()) {
            return;
        }

        passBadSound.play(soundFXVolume);
    }

    public static void playCharMultiple() {
        if (!playAudio || !isLoaded()) {
            return;
        }

        switch (new Random().nextInt(4)) {
            case 0:
                charMultipleSound1.play(soundFXVolume);
                break;
            case 1:
                charMultipleSound2.play(soundFXVolume);
                break;
            case 2:
                charMultipleSound3.play(soundFXVolume);
                break;
            case 3:
                charMultipleSound4.play(soundFXVolume);
                break;
        }
    }

    /**
     * Loads all audio files required for this application.
     *
     * Automatically called by create() when the application is launched
     *
     * @see TerminalMain#create()
     */
    public static void loadAudio() {
        backgroundAudioA = Gdx.audio.newMusic(Gdx.files.internal("audio/obj_console_03_a_lp.wav"));
        backgroundAudioA.setLooping(true);
        backgroundAudioA.setVolume(backgroundAudioVolume);

        backgroundAudioB = Gdx.audio.newMusic(Gdx.files.internal("audio/obj_console_03_b_lp.wav"));
        backgroundAudioB.setLooping(true);
        backgroundAudioB.setVolume(backgroundAudioVolume);

        backgroundAudioC = Gdx.audio.newMusic(Gdx.files.internal("audio/obj_console_03_c_lp.wav"));
        backgroundAudioC.setLooping(true);
        backgroundAudioC.setVolume(backgroundAudioVolume);

        buttonClickSound1 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charenter_01.wav"));
        buttonClickSound2 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charenter_02.wav"));
        buttonClickSound3 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charenter_03.wav"));

        menuCancelSound = Gdx.audio.newSound(Gdx.files.internal("audio/ui_menu_cancel.wav"));
        menuSelectSound = Gdx.audio.newSound(Gdx.files.internal("audio/ui_menu_focus.wav"));

        charScrollSound = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charscroll.wav"));

        charSingleSound1 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charsingle_01.wav"));
        charSingleSound2 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charsingle_02.wav"));
        charSingleSound3 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charsingle_03.wav"));
        charSingleSound4 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charsingle_04.wav"));
        charSingleSound5 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charsingle_05.wav"));
        charSingleSound6 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charsingle_06.wav"));

        passGoodSound = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_passgood.wav"));
        passBadSound = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_passbad.wav"));

        charMultipleSound1 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charmultiple_01.wav"));
        charMultipleSound2 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charmultiple_02.wav"));
        charMultipleSound3 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charmultiple_03.wav"));
        charMultipleSound4 = Gdx.audio.newSound(Gdx.files.internal("audio/ui_hacking_charmultiple_04.wav"));

        loaded = true;

        Gdx.app.log("Setup", "(" + TerminalMain.getRunTime() + ") Loaded all audio files");
    }

    public static boolean isLoaded() {
        return loaded;
    }

    /**
     * Disposes of this application's audio clips
     */
    public static void disposeAudio() {
        //Dispose audio
        backgroundAudioA.dispose();
        backgroundAudioB.dispose();
        backgroundAudioC.dispose();

        buttonClickSound1.dispose();
        buttonClickSound2.dispose();
        buttonClickSound3.dispose();

        menuCancelSound.dispose();
        menuSelectSound.dispose();

        charScrollSound.dispose();

        charSingleSound1.dispose();
        charSingleSound2.dispose();
        charSingleSound3.dispose();
        charSingleSound4.dispose();
        charSingleSound5.dispose();
        charSingleSound6.dispose();

        passGoodSound.dispose();
        passBadSound.dispose();

        charMultipleSound1.dispose();
        charMultipleSound2.dispose();
        charMultipleSound3.dispose();
        charMultipleSound4.dispose();

        loaded = false;

        Gdx.app.log("Dispose", "(" + TerminalMain.getRunTime() + ") Disposed of audio");
    }
}
