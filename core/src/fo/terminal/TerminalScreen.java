package fo.terminal;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * @author austinbt
 *
 * //TODO: Implement screens into TerminalMain
 */
abstract class TerminalScreen implements InputProcessor {
    boolean drawTitle = false;
    boolean drawTitleSplitter = false;
    protected TerminalMain terminal;

    public TerminalScreen(TerminalMain terminal) {
        this.terminal = terminal;
    }

    public abstract void draw(Batch batch);

    public void act() {

    }

    public void opened() {

    }

    public void closed() {}

    public void resized(int width, int height) {}

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
