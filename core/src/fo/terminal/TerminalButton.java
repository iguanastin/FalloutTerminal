package fo.terminal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import gui.Gui;

/**
 * @author austinbt
 */
public class TerminalButton{

    private TerminalButtonListener listener;
    private String text;

    public TerminalButton(String text) {
        this.text = text;
    }

    public void setListener(TerminalButtonListener listener) {
        this.listener = listener;
    }

    public void click() {
        if (listener != null) {
            listener.clicked(this);
        }
    }

    public static boolean isMouseOver(int x, int y, int width, int height) {
        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();

        return mx >= x && mx <= x+width && my >= y && my <= y+height;
    }

    public static boolean isMouseJustDown() {
        return Gdx.input.justTouched();
    }

    public static boolean isMouseJustDown(int x, int y, int width, int height) {
        return isMouseJustDown() && isMouseOver(x, y, width, height);
    }

    public void draw(Batch batch, int x, int y, int width, int height, boolean selected) {
        if (selected) {
            Gui.end(batch);
            Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.trim_color);

            Gui.sr.rect(x, y, width, height);

            Gui.end(Gui.sr);
        }

        Gui.begin(batch);
        if (selected) {
            TerminalMain.mediumFont.setColor(Gui.alternate_color);
        } else {
            TerminalMain.mediumFont.setColor(Gui.text_color);
        }
        Gui.drawTextInWidth(batch, TerminalMain.mediumFont, text, x + 10, y + height/2 + TerminalMain.mediumFont.getCapHeight()/2, width);
    }
}
