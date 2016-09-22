package fot.terminal;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import fot.actions.ScreenAction;

import java.util.ArrayList;

/**
 * @author austinbt
 *
 * //TODO: Implement screens into TerminalMain
 */
public abstract class TerminalScreen implements InputProcessor {
    protected boolean drawTitle = false;
    protected boolean drawTitleSplitter = false;
    protected TerminalMain terminal;

    private ArrayList<ScreenAction> actions = new ArrayList<ScreenAction>();

    public TerminalScreen(TerminalMain terminal) {
        this.terminal = terminal;
    }

    public abstract void draw(Batch batch);

    public final void superAct() {
        updateActions();
        act();
    }

    protected void act() {

    }

    public final void updateActions() {
        ArrayList<ScreenAction> toRemove = new ArrayList<ScreenAction>();

        for (ScreenAction action : actions) {
            if (action.isCompleted()) {
                toRemove.add(action);
            } else if (action.isRunning()) {
                action.update();
            }
        }

        actions.removeAll(toRemove);
    }

    public final void clearActions() {
        actions.clear();
    }

    public final void addAction(ScreenAction action) {
        if (!actions.contains(action)) {
            actions.add(action);
        }
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
