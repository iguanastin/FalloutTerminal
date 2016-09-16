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

import java.util.Random;

public class TerminalMain extends ApplicationAdapter {

    private static final int TITLE_DISTANCE_FROM_TOP = 75;

    private int scrollingThingPos = 0;
    private Color scrollingThingColor;

    private int glow = 0;
    private boolean glowIncrease = true;
    private static final int MAX_GLOW = 30;
    private Color glowColor;

    private TerminalScreen screen;

    static BitmapFont smallFont;
    static BitmapFont mediumFont;
    static BitmapFont largeFont;

    private Texture vignette;
    private Texture noise;

    private boolean drawTitle = true;
    private boolean drawSplitter = true;
    private boolean drawDebug = false;

    private LwjglFrame lwjglFrame;

    private boolean playAudio = true;
    private Music backgroundAudioA, backgroundAudioB, backgroundAudioC;
    private Sound buttonClickSound1, buttonClickSound2, buttonClickSound3;
    private float backgroundAudioVolume = 0.8f;
    private float soundFXVolume = 0.8f;

    public void setLwjglFrame(LwjglFrame lwjglFrame) {
        this.lwjglFrame = lwjglFrame;
    }

    @Override
    public void create() {
        //Set renderer and gui variables
        setGuiVariables();

        //Initialize renderers
        Gui.setupRenderers();

        //Initialize scrolling thing
        scrollingThingPos = Gdx.graphics.getHeight();
        scrollingThingColor = Gui.trim_color.cpy();

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
        new TerminalFile(dir, false, "Test file 2");
        new TerminalFile(dir, false, "Test file 3");
        new TerminalFile(dir2, false, "Second text file");
        new TerminalFile(dir2, true, "Folder 3");
        new TerminalFile(dir2, false, "ayy lmao");
        new TerminalFile(dir, false, "test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file test file ");
        ((FileTerminalScreen)screen).setDirectory(dir);
        openScreen(screen);
        //TODO: REMOVE ------------
    }

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
    }

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
    }

    private void loadOverlayTextures() {
        vignette = new Texture(Gdx.files.internal("vignette.png"));
        noise = new Texture(Gdx.files.internal("noise.png"));
    }

    private void setGuiVariables() {
        Gui.background_color = new Color(8f / 255f, 30f / 255f, 16f / 255f, 1f);
        Gui.text_color = new Color(49f / 255f, 255f / 255f, 149f / 255f, 1f);
        Gui.alternate_color = new Color(52f / 255f, 88f / 255f, 52f / 255f, 1f);
        Gui.trim_color = Gui.text_color;
        Gui.frame_color = Gui.alternate_color;
        Gui.has_border = false;
        Gui.border = 5;
    }

    private void generateFonts() {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("monofont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

        param.size = 12;
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
    }

    @Override
    public void resize(int width, int height) {
//        Gui.setupRenderers();

        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, width, height);
        Gui.batch.setProjectionMatrix(matrix);
        Gui.sr.setProjectionMatrix(matrix);


    }

    public void openScreen(TerminalScreen screen) {
        if (this.screen != null) {
            this.screen.closed();
        }
        this.screen = screen;
        screen.opened();
        Gdx.input.setInputProcessor(screen);
    }

    private void act(float deltaTime) {
        updateGlow();

        screen.act();

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            drawDebug = !drawDebug;
        }
    }

    /**
     * @return The distance from the top of the canvas to approximately one line-height below the title
     */
    int getHeightOfTitle() {
        return TITLE_DISTANCE_FROM_TOP + (int) (largeFont.getLineHeight() * 3);
    }

    private void updateGlow() {
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

    private void toggleFullscreen() {
        if (!Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(LwjglApplicationConfiguration.getDesktopDisplayMode().width, LwjglApplicationConfiguration.getDesktopDisplayMode().height);
            lwjglFrame.setExtendedState(LwjglFrame.MAXIMIZED_BOTH);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        //Dispose renderers
        Gui.dispose();

        //Dispose of fonts
        disposeFonts();

        //Dispose of overlay textures
        disposeOverlayTextures();

        //Dispose of audio
        disposeAudio();
    }

    private void disposeFonts() {
        smallFont.dispose();
        mediumFont.dispose();
        largeFont.dispose();
    }

    private void disposeOverlayTextures() {
        vignette.dispose();
        noise.dispose();
    }

    private void disposeAudio() {
        //Dispose audio
        backgroundAudioA.dispose();
        backgroundAudioB.dispose();
        backgroundAudioC.dispose();

        buttonClickSound1.dispose();
        buttonClickSound2.dispose();
        buttonClickSound3.dispose();
    }

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
        drawScrollingLightThing();

        //Render vignette
        Gui.begin(Gui.batch);
        Gui.batch.draw(vignette, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Draw glow
        drawGlow();

        //Render noise
        Gui.begin(Gui.batch);
        Gui.batch.draw(noise, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Draw fps
        if (drawDebug) {
            drawDebug();
        }

        //End rendering
        Gui.end(Gui.batch);
        Gui.end(Gui.sr);
    }

    private void drawDebug() {
        Gui.begin(Gui.batch);
        String str = Gdx.graphics.getFramesPerSecond() + "";
        Gui.font.draw(Gui.batch, str, 5, Gdx.graphics.getHeight() - 5);
        long usedMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        Gui.font.draw(Gui.batch, usedMB + "MB", 5, Gdx.graphics.getHeight() - 5 - Gui.font.getLineHeight());
    }

    private void drawSplitter() {
        if (drawSplitter) {
            Gui.end(Gui.batch);
            Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.trim_color);

            int splitterY = Gdx.graphics.getHeight() - getHeightOfTitle();
            Gui.sr.rect(TITLE_DISTANCE_FROM_TOP, splitterY, Gdx.graphics.getWidth() - TITLE_DISTANCE_FROM_TOP * 2, 5);

            Gui.end(Gui.sr);
        }
    }

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

    private void drawBackgroundLines() {
        Gui.end(Gui.batch);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.alternate_color);

        final int lineGap = 6;

        for (int i = 0; i < Gdx.graphics.getHeight(); i += lineGap) {
            Gui.sr.rect(0, i, Gdx.graphics.getWidth(), lineGap / 2);
        }

        Gui.end(Gui.sr);
    }

    private void drawScrollingLightThing() {
        Gui.end(Gui.batch);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Line, scrollingThingColor);
        for (int i = 0; i < 127; i++) {
            scrollingThingColor.a = (128 - i) / 255f;
            Gui.sr.setColor(scrollingThingColor);

            Gui.sr.line(0, scrollingThingPos + i, Gdx.graphics.getWidth(), scrollingThingPos + i);
        }
        scrollingThingPos -= 3;
        if (scrollingThingPos < -Gdx.graphics.getHeight()) {
            scrollingThingPos = Gdx.graphics.getHeight();
        }
        Gui.end(Gui.sr);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
