package fot.terminal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import fot.terminal.hack.HackData;
import fot.terminal.hack.HackScreen;
import gui.Gui;

import java.math.BigDecimal;
import java.util.Arrays;

public class TerminalMain extends ApplicationAdapter {

    private static final String topLine = "ROBCO INDUSTRIES UNIFIED OPERATING SYSTEM";
    private static final String middleLine = "COPYRIGHT 2075-2077 ROBCO INDUSTRIES";
    private static final String bottomLine = "-SERVER 1-";

    private static final int backgroundLineGap = 6;

    /**
     * Distance from the top of the canvas to the top of the title text
     */
    private static final int TITLE_DISTANCE_FROM_TOP = 75;


    /**
     * Current position of the polling light
     */
    private int pollingLightPos = 0;

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

    public static BitmapFont smallFont;
    public static BitmapFont mediumFont;
    public static BitmapFont largeFont;


    private Texture vignetteTexture;
    private Texture noiseTexture;
    private Texture pollingTexture;


    /**
     * Whether or not debug info is being drawn
     */
    private boolean drawDebug = false;


    /**
     * LwjglFrame containing this application
     */
    private LwjglFrame lwjglFrame;


    /**
     * Time in milliseconds that create was called.
     *
     * @see TerminalMain#create()
     */
    private static long startTime;

    private static final int STUTTER_TIME = 300;
    private int stutterTimer = 0;


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

        //Create glow color object
        glowColor = Gui.trim_color.cpy();

        //Generate small, medium, and large fonts
        generateFonts();
        //Load overlay textures
        loadOverlayTextures();
        //Load audio files
        TerminalAudio.loadAudio();
        //Start background audio
        TerminalAudio.startBackgroundAudio();

        //TODO: REMOVE ------------
//        TerminalScreen screen = new FileScreen(this);
//        TerminalFile dir = new TerminalFile(null, true, "home");
//        TerminalFile dir2 = new TerminalFile(dir, true, "Test folder");
//        new TerminalFile(dir, false, "Test file 2").setContents("This is a test file");
//        new TerminalFile(dir, false, "Test file 3").setContents("This is another test file");
//        new TerminalFile(dir2, false, "Second text file").setContents("TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND\n TESTESTESTSETSTSETSTSTSETSTTS \nAND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND TESTESTESTSETSTSETSTSTSETSTTS AND ");
//        new TerminalFile(dir2, true, "Folder 3");
//        new TerminalFile(dir2, false, "ayy lmao");
//        new TerminalFile(dir, false, "test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file ");
//        ((FileScreen) screen).setFile(dir);
//        openScreen(screen);

        HackScreen screen = new HackScreen(this, HackData.DIFF_EASY);
//        screen.addOutput("test output 1");
//        screen.addOutput("long line test here, should wrap at least once successfully");
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
     * Loads all overlay textures used by this application.
     *
     * Automatically called by create() when the application is launched
     *
     * @see TerminalMain#create()
     */
    private void loadOverlayTextures() {
        vignetteTexture = new Texture(Gdx.files.internal("vignette.png"));
        noiseTexture = new Texture(Gdx.files.internal("noise.png"));
        pollingTexture = new Texture(Gdx.files.internal("poll.png"));

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
        param.size = 32;
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
        Gui.resizeRenderers(width, height);

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
     * @param deltaTime Time in seconds since last call to acted
     */
    private void act(float deltaTime) {
        updateKeyboardInput();

        if (screen != null) {
            screen.superAct();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            drawDebug = !drawDebug;
        }

        //Update stutter
        updateStutter();
    }

    private void updateStutter() {
        stutterTimer--;
        if (stutterTimer <= 0) {
            stutterTimer = (int)(Math.random()*STUTTER_TIME);
            Gui.stutter = (int)(Math.random()*6);
        } else if (Gui.stutter != 0) {
            Gui.stutter = 0;
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
     * @see TerminalAudio#disposeAudio()
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
        TerminalAudio.disposeAudio();

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
        pollingTexture.dispose();

        Gdx.app.log("Dispose", "(" + getRunTime() + ") Disposed of overlay textures");
    }

    /**
     * Central game loop. Called by application window to update this game and render it.
     *
     * acted(float) is called before any rendering occurs.
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

        //Draw splitter line
        int splitterY = Gdx.graphics.getHeight() - getHeightOfTitle();
        Gui.sr.rect(TITLE_DISTANCE_FROM_TOP, splitterY - Gui.stutter, Gdx.graphics.getWidth() - TITLE_DISTANCE_FROM_TOP * 2, 5);

        Gui.end(Gui.sr);
    }

    /**
     * Draws screen glow overlay
     */
    private void drawGlow() {
        //Update glow counters
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

        //Update glowColor alpha
        glowColor.a = glow / (255f * 10f);

        Gui.end(Gui.batch);
        Gui.end(Gui.sr);

        //Enable blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, glowColor);

        //Draw glow
        Gui.sr.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Cleanup
        Gui.end(Gui.sr);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Draws title text
     */
    private void drawTitleText() {
        Gui.begin(Gui.batch);

        largeFont.setColor(Gui.text_color);

        Gui.GLYPHS.setText(largeFont, topLine);
        largeFont.draw(Gui.batch, topLine, Gdx.graphics.getWidth() / 2 - Gui.GLYPHS.width / 2, Gdx.graphics.getHeight() - TITLE_DISTANCE_FROM_TOP - Gui.stutter);
        Gui.GLYPHS.setText(largeFont, middleLine);
        largeFont.draw(Gui.batch, middleLine, Gdx.graphics.getWidth() / 2 - Gui.GLYPHS.width / 2, Gdx.graphics.getHeight() - TITLE_DISTANCE_FROM_TOP - largeFont.getLineHeight() - Gui.stutter);
        Gui.GLYPHS.setText(largeFont, bottomLine);
        largeFont.draw(Gui.batch, bottomLine, Gdx.graphics.getWidth() / 2 - Gui.GLYPHS.width / 2, Gdx.graphics.getHeight() - TITLE_DISTANCE_FROM_TOP - largeFont.getLineHeight() * 2 - Gui.stutter);
    }

    /**
     * Draws dim background lines
     */
    private void drawBackgroundLines() {
        Gui.end(Gui.batch);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.alternate_color);

        //Draw background lines
        for (int i = 0; i < Gdx.graphics.getHeight(); i += backgroundLineGap) {
            Gui.sr.rect(0, i, Gdx.graphics.getWidth(), backgroundLineGap / 2);
        }

        Gui.end(Gui.sr);
    }

    /**
     * Draws polling light
     */
    private void drawPollingLight() {
        Gui.end(Gui.batch);
        //Enable transparency blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gui.begin(Gui.batch);

        //Draw polling light
        Gui.batch.draw(pollingTexture, 0, pollingLightPos, Gdx.graphics.getWidth(), pollingTexture.getHeight());

        //Move polling light
        pollingLightPos -= 3;

        //Reset if far enough down
        if (pollingLightPos < -Gdx.graphics.getHeight()) {
            pollingLightPos = Gdx.graphics.getHeight();
        }

        //Cleanup
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
