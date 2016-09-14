package fo.terminal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import gui.Button;

import java.util.ArrayList;

/**
 * @author austinbt
 */
public class FileTerminalScreen extends TerminalScreen {

    private static final int BUTTON_HEIGHT = 30;
    private static final int BUTTON_GAP = 10;

    private TerminalMain terminal;

    private TerminalFile file;

    private ArrayList<TerminalButton> buttons;
    private int page = 0, index = 0;
    private int maxPage = 0, maxIndex = 0;

    public FileTerminalScreen() {
        drawTitle = true;
        drawTitleSplitter = true;
    }

    public FileTerminalScreen setDirectory(TerminalFile file) {
        if (!file.isDirectory()) {
            throw new TerminalFileException("Cannot open non-directory");
        }

        this.file = file;

        index = 0;
        page = 0;

        generateButtons();

        return this;
    }

    private void generateButtons() {
        buttons.clear();

        int availableSpace = Gdx.graphics.getHeight() - terminal.getHeightOfTitle() - 25;
        int totalButtons = availableSpace/(BUTTON_HEIGHT + BUTTON_GAP);
        int availableButtons = totalButtons;

        if (file.getChildren().size() > totalButtons) {
            availableButtons -= 2;
        }

        //TODO: Generate buttons to be rendered in the current page
    }

    @Override
    public void act() {

    }

    @Override
    public void opened(TerminalMain terminal) {
        this.terminal = terminal;
    }

    @Override
    public void draw(Batch batch) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            //Move down
            if (index < maxIndex) {
                buttons.get(index+maxIndex*page).setSelected(false);
                index++;
                buttons.get(index+maxIndex*page).setSelected(true);
            }

            return true;
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            //Move up
            if (index > 0) {
                buttons.get(index+maxIndex*page).setSelected(false);
                index--;
                buttons.get(index+maxIndex*page).setSelected(true);
            }

            return true;
        } else if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
            //Select
            //TODO: Implement selecting

            return true;
        } else if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            //Left
            if (page > 0) {
                buttons.get(index+maxIndex*page).setSelected(false);
                page--;
                index = 0;
                buttons.get(index+maxIndex*page).setSelected(true);
            }

            return true;
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            //Right
            if (page < maxPage) {
                buttons.get(index+maxIndex*page).setSelected(false);
                page++;
                index = 0;
                buttons.get(index+maxIndex*page).setSelected(true);
            }

            return true;
        }

        return false;
    }
}
