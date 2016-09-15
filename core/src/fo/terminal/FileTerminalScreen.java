package fo.terminal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;

/**
 * @author austinbt
 */
public class FileTerminalScreen extends TerminalScreen {

    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_GAP = 15;
    public static final int SIDE_BORDER = 100;

    private TerminalMain terminal;

    private TerminalFile folder;

    private ArrayList<TerminalButton> buttons;
    private int page = 0, index = 0;
    private int maxPage = 0, maxIndex = 0;

    public FileTerminalScreen() {
        drawTitle = true;
        drawTitleSplitter = true;

        buttons = new ArrayList<TerminalButton>();
    }

    public FileTerminalScreen setDirectory(TerminalFile file) {
        if (!file.isDirectory()) {
            throw new TerminalFileException("Cannot open non-directory");
        }

        this.folder = file;

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

        if (folder.getChildren().size() > totalButtons) {
            availableButtons -= 2;
        }

        maxIndex = availableButtons;
        maxPage = (folder.getChildren().size()+1)/maxIndex;
        index = 0;
        page = 0;

        for (TerminalFile file : folder.getChildren()) {
            buttons.add(new TerminalButton(this, file, file.getName(), -1000, -1000, 10, 10));
        }

        select(buttons.get(0));
    }

    public void select(TerminalButton button) {
        if (buttons.contains(button)) {
            for (TerminalButton unselectButton : buttons) {
                unselectButton.setSelected(false);
            }

            index = buttons.indexOf(button);
            page = 0;

            while (index > maxIndex) {
                page++;
                index -= maxIndex;
            }

            buttons.get(index).setSelected(true);
        }
    }

    @Override
    public void act() {
        for (TerminalButton button : buttons) {
            button.act(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void opened(TerminalMain terminal) {
        this.terminal = terminal;
    }

    @Override
    public void draw(Batch batch) {
        int i = 0;
        for (TerminalButton button : buttons) {
            if (i > maxIndex) {
                break;
            }

            button.setPosition(100, Gdx.graphics.getHeight() - terminal.getHeightOfTitle() - SIDE_BORDER - i*(BUTTON_HEIGHT+BUTTON_GAP));
            button.setSize(Gdx.graphics.getWidth() - SIDE_BORDER *2, BUTTON_HEIGHT);
            button.draw(batch, 1f);

            i++;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            //Move down
            if (index < maxIndex) {
                select(buttons.get(index+maxIndex*page));
                index++;
                select(buttons.get(index+maxIndex*page));
            }

            //TODO: Fix bug out of bounds error when there are less than maxIndex buttons on the page

            return true;
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            //Move up
            if (index > 0) {
                select(buttons.get(index+maxIndex*page));
                index--;
                select(buttons.get(index+maxIndex*page));
            }

            return true;
        } else if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
            //Select
            //TODO: Implement selecting

            return true;
        } else if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            //Left
            if (page > 0) {
                select(buttons.get(index+maxIndex*page));
                page--;
                index = 0;
                select(buttons.get(index+maxIndex*page));
            }

            //TODO: Implement page changing

            return true;
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            //Right
            if (page < maxPage) {
                select(buttons.get(index+maxIndex*page));
                page++;
                index = 0;
                select(buttons.get(index+maxIndex*page));
            }

            //TODO: Implement page changing

            return true;
        }

        return false;
    }
}
