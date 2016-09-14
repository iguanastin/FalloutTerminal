package fo.terminal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
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

public class TerminalMain extends ApplicationAdapter {

    private static final int TITLE_DISTANCE_FROM_TOP = 75;

    private int scrollingThingPos = 0;
    private Color scrollingThingColor;

    private int glow = 0;
    private boolean glowIncrease = true;
    private static final int MAX_GLOW = 30;
    private Color glowColor;

    private BitmapFont smallFont;
    private BitmapFont mediumFont;
    private BitmapFont largeFont;

    private Texture vignette;
    private Texture noise;

    private boolean drawTitle = true;
    private boolean drawSplitter = true;
    private boolean drawFps = false;

    private LwjglFrame parentFrame;

    private Music backgroundAudioA, backgroundAudioB, backgroundAudioC;

    public void setParentFrame(LwjglFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    @Override
    public void create() {
        //Set renderer and gui variables
        setGuiVariables();

        //Initialize renderers
        Gui.resetRenderers();

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
    }

    private void loadAudio() {
        backgroundAudioA = Gdx.audio.newMusic(Gdx.files.internal("audio/obj_console_03_a_lp.wav"));
        backgroundAudioB = Gdx.audio.newMusic(Gdx.files.internal("audio/obj_console_03_b_lp.wav"));
        backgroundAudioC = Gdx.audio.newMusic(Gdx.files.internal("audio/obj_console_03_c_lp.wav"));
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
    }

    @Override
    public void resize(int width, int height) {
//        Gui.resetRenderers();

        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, width, height);
        Gui.batch.setProjectionMatrix(matrix);
        Gui.sr.setProjectionMatrix(matrix);
    }

    private void act(float deltaTime) {
        updateGlow();

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            drawFps = !drawFps;
        }
    }

    /**
     *
     * @return The distance from the top of the canvas to approximately one line-height below the title
     */
    public int getHeightOfTitle() {
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
            parentFrame.setExtendedState(LwjglFrame.MAXIMIZED_BOTH);
        }
    }

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
        backgroundAudioA.dispose();
        backgroundAudioB.dispose();
        backgroundAudioC.dispose();
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

        //TODO: STANDARD RENDERING OF ALL OTHER ACTORS

        //Render title
        if (drawTitle) {
            drawTitleText();
        }

        //Render splitter
        drawSplitter();

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
        if (drawFps) {
            Gui.begin(Gui.batch);
            String str = Gdx.graphics.getFramesPerSecond() + "";
            Gui.font.draw(Gui.batch, str, 5, Gdx.graphics.getHeight() - 5);
        }

        //End rendering
        Gui.end(Gui.batch);
        Gui.end(Gui.sr);
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
