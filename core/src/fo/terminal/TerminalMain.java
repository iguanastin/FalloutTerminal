package fo.terminal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import gui.Gui;

import java.math.BigDecimal;
import java.util.Random;

public class TerminalMain extends ApplicationAdapter {

    /**
     * Distance from the top of the canvas to the top of the title text
     */
    private static final int TITLE_DISTANCE_FROM_TOP = 75;


    /**
     * Current position of the polling light
     */
    private int pollingLightPos = 0;
    /**
     * Current color of the polling light.
     * <p>
     * The alpha value of this color object is updated every frame.
     */
    private Color pollingLightColor;


    /**
     * Current alpha of the glow
     */
    private int glow = 0;
    /**
     * Whether or not this glow cycle is increasing glow, or decreasing glow.
     */
    private boolean glowIncrease = true;
    /**
     * Maximum glow value
     */
    private static final int MAX_GLOW = 30;
    /**
     * Current glow color object
     */
    private Color glowColor;


    /**
     * Currently active TerminalScreen.
     *
     * Can be null.
     */
    private TerminalScreen screen;


    /**
     * Smallest terminal font
     */
    public static BitmapFont smallFont;
    /**
     * Standard terminal font
     */
    public static BitmapFont mediumFont;
    /**
     * Largest terminal font
     */
    public static BitmapFont largeFont;


    /**
     * Vignette texture to be rendered over the screen
     */
    private Texture vignetteTexture;
    /**
     * Dirt texture to be rendered over the screen
     */
    private Texture noiseTexture;


    /**
     * Whether or not debug info is being drawn
     */
    private boolean drawDebug = false;


    /**
     * LwjglFrame containing this application
     */
    private LwjglFrame lwjglFrame;


    private Music backgroundAudioA, backgroundAudioB, backgroundAudioC;
    private Sound buttonClickSound1, buttonClickSound2, buttonClickSound3;
    private Sound menuCancelSound;
    private Sound menuSelectSound;
    private Sound charScrollSound;
    private Sound charSingleSound1, charSingleSound2, charSingleSound3, charSingleSound4, charSingleSound5, charSingleSound6;
    private Sound passGoodSound, passBadSound;
    private Sound charMultipleSound1, charMultipleSound2, charMultipleSound3, charMultipleSound4;
    /**
     * Whether or not any audio will be played
     */
    private boolean playAudio = true;
    /**
     * Scalar value of the volume of background music [0f, 1f]
     */
    private float backgroundAudioVolume = 0.8f;
    /**
     * Scalar value of the volume of sound effects [0f, 1f]
     */
    private float soundFXVolume = 0.8f;


    /**
     * Time in milliseconds that create was called.
     *
     * @see TerminalMain#create()
     */
    private static long startTime;


    /**
     * Set the LwjglFrame that contains this application
     *
     * @param lwjglFrame LwjglFrame to set
     */
    public void setLwjglFrame(LwjglFrame lwjglFrame) {
        this.lwjglFrame = lwjglFrame;
    }

    /**
     * Called when this application is created.
     *
     * Initializes and loads all resources that are needed.
     */
    @Override
    public void create() {
        startTime = System.currentTimeMillis();

        //Set renderer and gui variables
        setGuiVariables();

        //Initialize renderers
        Gui.setupRenderers();

        //Initialize scrolling thing
        pollingLightPos = Gdx.graphics.getHeight();
        pollingLightColor = Gui.trim_color.cpy();

        //Create glow color object
        glowColor = Gui.trim_color.cpy();

        //Generate small, medium, and large fonts
        generateFonts();
        //Load overlay textures
        loadOverlayTextures();
        //Load audio files
        loadAudio();
        //Start background audio
        startBackgroundAudio();

        //TODO: REMOVE ------------
        TerminalScreen screen = new FileTerminalScreen(this);
        TerminalFile dir = new TerminalFile(null, true, "home");
        TerminalFile dir2 = new TerminalFile(dir, true, "Test folder");
        new TerminalFile(dir, false, "Test file 2").setContents("This is a test file");
        new TerminalFile(dir, false, "Test file 3").setContents("This is another test file");
        new TerminalFile(dir2, false, "Second text file").setContents("TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND\n TESTESTESTSETSTSETSTSTSETSTTS \nAND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND ");
        new TerminalFile(dir2, true, "Folder 3");
        new TerminalFile(dir2, false, "ayy lmao");
        new TerminalFile(dir, false, "test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file ");
        ((FileTerminalScreen) screen).setFile(dir);
        openScreen(screen);
        //TODO: REMOVE ------------

        Gdx.app.log("Create", "(" + getRunTime() + ") Finished creating");
    }

    /**
     * Find the time in since create was first called on this object.
     *
     * Calling this before create has been called will return unexpected results.
     *
     * @return The time this program has been running in a human readable format.
     */
    public static String getRunTime() {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        long millis = System.currentTimeMillis() - startTime;

        if (millis > 1000) {
            seconds = (int) (millis / 1000);
            millis %= 1000;
        }
        if (seconds > 60) {
            minutes = seconds / 60;
            seconds %= 60;
        }
        if (minutes > 60) {
            hours = minutes / 60;
            minutes %= 60;
        }

        String work = "";
        if (hours > 0) {
            work += hours + "h ";
        }
        if (minutes > 0) {
            work += minutes + "m ";
        }
        if (seconds > 0) {
            work += seconds;
        }
        work += "." + millis + "s";

        return work;
    }

    /**
     * Starts a random background audio file
     */
    private void startBackgroundAudio() {
        if (!playAudio) {
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

        Gdx.app.log("Setup", "(" + getRunTime() + ") Started background audio. (A:" + backgroundAudioA.isPlaying() + ", B:" + backgroundAudioB.isPlaying() + ", C:" + backgroundAudioC.isPlaying() + ")");
    }

    /**
     * Plays a randomized button click sound effect.
     *
     * If audio has not been loaded, a NullPointerException will be thrown.
     */
    public void playButtonClick() {
        if (!playAudio) {
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

    public void playMenuCancel() {
        if (!playAudio) {
            return;
        }

        menuCancelSound.play(soundFXVolume);
    }

    public void playMenuSelect() {
        if (!playAudio) {
            return;
        }

        menuSelectSound.play(soundFXVolume);
    }

    public void playCharScroll() {
        if (!playAudio) {
            return;
        }

        charScrollSound.play(soundFXVolume);
    }

    public void playCharSingle() {
        if (!playAudio) {
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

    public void playPassGood() {
        if (!playAudio) {
            return;
        }

        passGoodSound.play(soundFXVolume);
    }

    public void playPassBad() {
        if (!playAudio) {
            return;
        }

        passBadSound.play(soundFXVolume);
    }

    public void playCharMultiple() {
        if (!playAudio) {
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
    private void loadAudio() {
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

        Gdx.app.log("Setup", "(" + getRunTime() + ") Loaded all audio files");
    }

    /**
     * Loads all overlay textures used by tihs application.
     *
     * Automatically called by create() when the application is launched
     *
     * @see TerminalMain#create()
     */
    private void loadOverlayTextures() {
        vignetteTexture = new Texture(Gdx.files.internal("vignette.png"));
        noiseTexture = new Texture(Gdx.files.internal("noise.png"));

        Gdx.app.log("Setup", "(" + getRunTime() + ") Loaded all overlay textures");
    }

    /**
     * Initializes Gui variables.
     *
     * Automatically called by create() when this application is launched
     *
     * @see TerminalMain#create()
     */
    private void setGuiVariables() {
        Gui.background_color = new Color(8f / 255f, 30f / 255f, 16f / 255f, 1f);
        Gui.text_color = new Color(49f / 255f, 255f / 255f, 149f / 255f, 1f);
        Gui.alternate_color = new Color(52f / 255f, 88f / 255f, 52f / 255f, 1f);
        Gui.trim_color = Gui.text_color;
        Gui.frame_color = Gui.alternate_color;
        Gui.has_border = false;
        Gui.border = 5;

        Gdx.app.log("Setup", "(" + getRunTime() + ") Setup Gui variables");
    }

    /**
     * Generates terminal fonts for this application.
     *
     * Automatically called by create() when this application is launched
     *
     * @see TerminalMain#create()
     */
    private void generateFonts() {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("monofont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

        param.size = 18;
        param.kerning = false;
        smallFont = gen.generateFont(param);
        param.size = 24;
        mediumFont = gen.generateFont(param);
        param.size = 48;
        largeFont = gen.generateFont(param);

        smallFont.setColor(Gui.text_color);
        mediumFont.setColor(Gui.text_color);
        largeFont.setColor(Gui.text_color);

        gen.dispose();

        Gdx.app.log("Setup", "(" + getRunTime() + ") Generated fonts");
    }

    /**
     * Called when the application window has been resized.
     *
     * @param width Width of the resized canvas
     * @param height Height of the resized canvas
     */
    @Override
    public void resize(int width, int height) {
        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, width, height);
        Gui.batch.setProjectionMatrix(matrix);
        Gui.sr.setProjectionMatrix(matrix);

        Gdx.app.log("Resize", "(" + getRunTime() + ") Resized window to " + width + "x" + height);
    }

    /**
     * Opens a given TerminalScreen in this terminal and closes the previous one (if non-null)
     *
     * @param screen New screen to open
     */
    public void openScreen(TerminalScreen screen) {
        if (this.screen != null) {
            this.screen.closed();
        }
        this.screen = screen;
        if (screen != null) {
            screen.opened();
            Gdx.input.setInputProcessor(screen);
        }

        Gdx.app.log("ScreenChange", "(" + getRunTime() + ") Changed to TerminalScreen: " + screen);
    }

    /**
     * Updates non-rendering actions for this Terminal.
     *
     * Called from render() before any rendering has happened.
     *
     * @param deltaTime Time in seconds since last call to act
     */
    private void act(float deltaTime) {
        updateKeyboardInput();

        if (screen != null) {
            screen.act();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            drawDebug = !drawDebug;
        }
    }

    /**
     * @return The distance from the top of the canvas to the bottom of the title and title splitter.
     */
    int getHeightOfTitle() {
        return TITLE_DISTANCE_FROM_TOP + (int) (largeFont.getLineHeight() * 3);
    }

    /**
     * Checks on keyboard input
     */
    private void updateKeyboardInput() {
        //Is ALT currently down
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) {
            //Is enter just pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                toggleFullscreen();
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullscreen();
        }
    }

    /**
     * Toggles fullscreen status
     */
    private void toggleFullscreen() {
        if (!Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(LwjglApplicationConfiguration.getDesktopDisplayMode().width, LwjglApplicationConfiguration.getDesktopDisplayMode().height);
            lwjglFrame.setExtendedState(LwjglFrame.MAXIMIZED_BOTH);
        }

        Gdx.app.log("Fullscreen", "(" + getRunTime() + ") Toggled fullscreen to: " + Gdx.graphics.isFullscreen());
    }

    /**
     * Disposes of all application resources
     *
     * @see TerminalMain#disposeAudio()
     * @see TerminalMain#disposeFonts()
     * @see TerminalMain#disposeOverlayTextures()
     * @see Gui#dispose()
     */
    @Override
    public void dispose() {
        //Dispose renderers
        Gui.dispose();

        //Dispose of fonts
        disposeFonts();

        //Dispose of overlay textures
        disposeOverlayTextures();

        //Dispose of audio
        disposeAudio();

        Gdx.app.log("Dispose", "(" + getRunTime() + ") Disposed of all resources");
    }

    /**
     * Disposes of this application's generated fonts
     */
    private void disposeFonts() {
        smallFont.dispose();
        mediumFont.dispose();
        largeFont.dispose();

        Gdx.app.log("Dispose", "(" + getRunTime() + ") Disposed of fonts");
    }

    /**
     * Disposes of this application's overlay textures
     */
    private void disposeOverlayTextures() {
        vignetteTexture.dispose();
        noiseTexture.dispose();

        Gdx.app.log("Dispose", "(" + getRunTime() + ") Disposed of overlay textures");
    }

    /**
     * Disposes of this application's audio clips
     */
    private void disposeAudio() {
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

        Gdx.app.log("Dispose", "(" + getRunTime() + ") Disposed of audio");
    }

    /**
     * Central game loop. Called by application window to update this game and render it.
     *
     * act(float) is called before any rendering occurs.
     *
     * Renders everything.
     *
     * @see TerminalMain#act(float)
     */
    @Override
    public void render() {
        //Act
        act(Gdx.graphics.getDeltaTime());

        //Clear screen
        Gdx.gl.glClearColor(Gui.background_color.r, Gui.background_color.g, Gui.background_color.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackgroundLines();

        //Begin rendering
        Gui.begin(Gui.batch);

        screen.draw(Gui.batch);

        //Render title
        if (screen == null || screen.drawTitle) {
            drawTitleText();
        }

        //Render splitter
        if (screen == null || screen.drawTitleSplitter) {
            drawSplitter();
        }

        //Render scrolling light thing
        drawPollingLight();

        //Render vignetteTexture
        Gui.begin(Gui.batch);
        Gui.batch.draw(vignetteTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Draw glow
        drawGlow();

        //Render noiseTexture
        Gui.begin(Gui.batch);
        Gui.batch.draw(noiseTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Draw fps
        if (drawDebug) {
            drawDebug();
        }

        //End rendering
        Gui.end(Gui.batch);
        Gui.end(Gui.sr);
    }

    /**
     * Draws debug information to the top left corner of the screen
     */
    private void drawDebug() {
        Gui.begin(Gui.batch);

        //Draw framerate
        String fpsStr = "FPS: " + Gdx.graphics.getFramesPerSecond();
        Gui.drawTextInWidth(Gui.batch, smallFont, fpsStr, 5, Gdx.graphics.getHeight() - 8, 200);

        //Draw used memory
        float usedMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0f / 1024.0f;
        Gui.drawTextInWidth(Gui.batch, smallFont, "Used: " + BigDecimal.valueOf(usedMB).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "MB", 5, Gdx.graphics.getHeight() - 8 - Gui.font.getLineHeight(), 200);

        //Draw total memory
        float totalMB = Runtime.getRuntime().totalMemory() / 1024.0f / 1024.0f;
        Gui.drawTextInWidth(Gui.batch, smallFont, "Total: " + BigDecimal.valueOf(totalMB).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "MB", 5, Gdx.graphics.getHeight() - 8 - Gui.font.getLineHeight()*2, 200);
    }

    /**
     * Draws a splitter line below the title text
     */
    private void drawSplitter() {
        Gui.end(Gui.batch);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.trim_color);

        int splitterY = Gdx.graphics.getHeight() - getHeightOfTitle();
        Gui.sr.rect(TITLE_DISTANCE_FROM_TOP, splitterY, Gdx.graphics.getWidth() - TITLE_DISTANCE_FROM_TOP * 2, 5);

        Gui.end(Gui.sr);
    }

    /**
     * Draws screen glow overlay
     */
    private void drawGlow() {
        if (glowIncrease) {
            glow++;
        } else {
            glow--;
        }
        if (glow >= MAX_GLOW * 10) {
            glowIncrease = false;
        } else if (glow <= 0) {
            glowIncrease = true;
        }
        glowColor.a = glow / (255f * 10f);
        Gui.end(Gui.batch);
        Gui.end(Gui.sr);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, glowColor);
        Gui.sr.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gui.end(Gui.sr);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Draws title text
     */
    private void drawTitleText() {
        Gui.begin(Gui.batch);

        String topLine = "ROBCO INDUSTRIES UNIFIED OPERATING SYSTEM";
        String middleLine = "COPYRIGHT 2075-2077 ROBCO INDUSTRIES";
        String bottomLine = "-SERVER 1-";

        largeFont.setColor(Gui.text_color);

        Gui.GLYPHS.setText(largeFont, topLine);
        largeFont.draw(Gui.batch, topLine, Gdx.graphics.getWidth() / 2 - Gui.GLYPHS.width / 2, Gdx.graphics.getHeight() - TITLE_DISTANCE_FROM_TOP);
        Gui.GLYPHS.setText(largeFont, middleLine);
        largeFont.draw(Gui.batch, middleLine, Gdx.graphics.getWidth() / 2 - Gui.GLYPHS.width / 2, Gdx.graphics.getHeight() - TITLE_DISTANCE_FROM_TOP - largeFont.getLineHeight());
        Gui.GLYPHS.setText(largeFont, bottomLine);
        largeFont.draw(Gui.batch, bottomLine, Gdx.graphics.getWidth() / 2 - Gui.GLYPHS.width / 2, Gdx.graphics.getHeight() - TITLE_DISTANCE_FROM_TOP - largeFont.getLineHeight() * 2);
    }

    /**
     * Draws dim background lines
     */
    private void drawBackgroundLines() {
        Gui.end(Gui.batch);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.alternate_color);

        final int lineGap = 6;

        for (int i = 0; i < Gdx.graphics.getHeight(); i += lineGap) {
            Gui.sr.rect(0, i, Gdx.graphics.getWidth(), lineGap / 2);
        }

        Gui.end(Gui.sr);
    }

    /**
     * Draws polling light
     */
    private void drawPollingLight() {
        Gui.end(Gui.batch);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Line, pollingLightColor);
        for (int i = 0; i < 127; i++) {
            pollingLightColor.a = (128 - i) / 255f;
            Gui.sr.setColor(pollingLightColor);

            Gui.sr.line(0, pollingLightPos + i, Gdx.graphics.getWidth(), pollingLightPos + i);
        }
        pollingLightPos -= 3;
        if (pollingLightPos < -Gdx.graphics.getHeight()) {
            pollingLightPos = Gdx.graphics.getHeight();
        }
        Gui.end(Gui.sr);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
