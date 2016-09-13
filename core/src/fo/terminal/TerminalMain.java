package fo.terminal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import gui.Gui;

public class TerminalMain extends ApplicationAdapter {

    private int scrollingThingPos = 0;
    private Color scrollingThingColor = Color.GREEN;

    @Override
    public void create() {
        Gui.background_color = Color.BLACK;
        Gui.text_color = new Color(103f/255f, 209f/255f, 119f/255f, 1f);
        Gui.alternate_color = new Color(52f/255f, 88f/255f, 52f/255f, 1f);
        Gui.trim_color = Gui.text_color;
        Gui.frame_color = Gui.alternate_color;
        Gui.has_border = false;
        Gui.border = 5;

        Gui.resetRenderers();

        scrollingThingPos = Gdx.graphics.getHeight();
        scrollingThingColor = Gui.trim_color.cpy();
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


        //Render scrolling light thing
        drawScrollingLightThing();

        //End rendering
        Gui.end(Gui.batch);
        Gui.end(Gui.sr);
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
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
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
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }
}
