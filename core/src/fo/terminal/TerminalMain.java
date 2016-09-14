package fo.terminal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import gui.Gui;

public class TerminalMain extends ApplicationAdapter {

    private int scrollingThingPos = 0;
    private Color scrollingThingColor = Color.GREEN;

    private BitmapFont smallFont;
    private BitmapFont mediumFont;
    private BitmapFont largeFont;

    private boolean drawTitle = true;

    @Override
    public void create() {
        setGuiVariables();

        Gui.resetRenderers();

        scrollingThingPos = Gdx.graphics.getHeight();
        scrollingThingColor = Gui.trim_color.cpy();

        generateFonts();
    }

    private void setGuiVariables() {
        Gui.background_color = Color.BLACK;
        Gui.text_color = new Color(103f/255f, 209f/255f, 119f/255f, 1f);
        Gui.alternate_color = new Color(52f/255f, 88f/255f, 52f/255f, 1f);
        Gui.trim_color = Gui.text_color;
        Gui.frame_color = Gui.alternate_color;
        Gui.has_border = false;
        Gui.border = 5;
    }

    private void generateFonts() {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("monofont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 12;
        smallFont = gen.generateFont(param);
        param.size = 24;
        mediumFont = gen.generateFont(param);
        param.size = 36;
        largeFont = gen.generateFont(param);
        smallFont.setColor(Gui.text_color);
        mediumFont.setColor(Gui.text_color);
        largeFont.setColor(Gui.text_color);
    }

    @Override
    public void resize(int width, int height) {
        Gui.resetRenderers();
    }

    private void act(float deltaTime) {

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

        //Render scrolling light thing
        drawScrollingLightThing();

        //Render title
        if (drawTitle) {
            drawTitleText();
        }

        //End rendering
        Gui.end(Gui.batch);
        Gui.end(Gui.sr);
    }

    private void drawTitleText() {
        Gui.begin(Gui.batch);

        String topLine = "ROBCO INDUSTRIES UNIFIED OPERATING SYSTEM";
        String middleLine = "COPYRIGHT 2075-2077 ROBCO INDUSTRIES";
        String bottomLine = "-SERVER 1-";

        Gui.GLYPHS.setText(largeFont, topLine);
        largeFont.draw(Gui.batch, topLine, Gdx.graphics.getWidth()/2 - Gui.GLYPHS.width/2, Gdx.graphics.getHeight() - 50);
        Gui.GLYPHS.setText(largeFont, middleLine);
        largeFont.draw(Gui.batch, middleLine, Gdx.graphics.getWidth()/2 - Gui.GLYPHS.width/2, Gdx.graphics.getHeight() - 50 - largeFont.getLineHeight());
        Gui.GLYPHS.setText(largeFont, bottomLine);
        largeFont.draw(Gui.batch, bottomLine, Gdx.graphics.getWidth()/2 - Gui.GLYPHS.width/2, Gdx.graphics.getHeight() - 50 - largeFont.getLineHeight()*2);
    }

    private void drawBackgroundLines() {
        Gui.end(Gui.batch);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.alternate_color);

        final int lineGap = 6;

        for (int i = 0; i < Gdx.graphics.getHeight(); i += lineGap) {
            Gui.sr.rect(0, i, Gdx.graphics.getWidth(), lineGap/2);
        }

        Gui.end(Gui.sr);
    }

    private void drawScrollingLightThing() {
        Gui.end(Gui.batch);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Line, scrollingThingColor);
        for (int i = 127; i < 255; i++) {
            scrollingThingColor.a = i;
            Gui.sr.setColor(scrollingThingColor);

            Gui.sr.line(0, scrollingThingPos + i, Gdx.graphics.getWidth(), scrollingThingPos + i);
        }
        scrollingThingPos -= 4;
        if (scrollingThingPos < -Gdx.graphics.getHeight()) {
            scrollingThingPos = Gdx.graphics.getHeight();
        }
        Gui.end(Gui.sr);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
