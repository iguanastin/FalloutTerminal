package fo.terminal;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import gui.Button;
import gui.Gui;

/**
 * @author austinbt
 *
 */
public class TerminalButton extends Button {

    protected boolean selected = false;
    protected FileTerminalScreen terminalScreen;
    protected TerminalFile file;

    public TerminalButton(FileTerminalScreen terminalScreen, TerminalFile file, String text, int x, int y, int width, int height) {
        super(text, x, y, width, height);
        this.terminalScreen = terminalScreen;
        this.file = file;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public TerminalFile getFile() {
        return file;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isMouseOver() && !isSelected()) {
            terminalScreen.select(this);
        } else {

        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //Draw button background
        if (isSelected()) {
            Gui.end(batch);
            Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.trim_color);
            Gui.sr.rect(getX(), getY(), getWidth(), getHeight());
            Gui.end(Gui.sr);
        }

        Gui.begin(batch);

        //Draw text
        if (isSelected()) {
            TerminalMain.mediumFont.setColor(Gui.alternate_color);
        } else {
            TerminalMain.mediumFont.setColor(Gui.text_color);
        }
        Gui.GLYPHS.setText(TerminalMain.mediumFont, text);
        TerminalMain.mediumFont.draw(batch, text, getX() + 10, getY() + getHeight()/2 + Gui.GLYPHS.height/2, getWidth(), -1, false);
    }
}
