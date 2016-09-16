package fo.terminal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;

/**
 * @author austinbt
 */
class FileTerminalScreen extends TerminalScreen {

    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_GAP = 15;
    private static final int SIDE_BORDER = 100;

    private TerminalFile folder;

    private ArrayList<TerminalButton> buttons;
    private int index = 0;

    FileTerminalScreen(TerminalMain terminal) {
        super(terminal);
        drawTitle = true;
        drawTitleSplitter = true;

        buttons = new ArrayList<TerminalButton>();
    }

    FileTerminalScreen setDirectory(TerminalFile file) {
        if (!file.isDirectory()) {
            throw new TerminalFileException("Cannot open non-directory");
        }

        this.folder = file;

        index = 0;

        generateButtons();

        return this;
    }

    private void generateButtons() {
        buttons.clear();

        int availableSpace = Gdx.graphics.getHeight() - terminal.getHeightOfTitle() - BUTTON_GAP - BUTTON_HEIGHT;
        int buttonsPerPage = availableSpace/(BUTTON_HEIGHT + BUTTON_GAP);

        //TODO: Implement paging

        index = 0;

        for (TerminalFile file : folder.getChildren()) {
            TerminalButton button = new TerminalFileButton(file);
            if (file.isDirectory()) {
                button.setListener(new TerminalButtonListener() {
                    @Override
                    public void clicked(TerminalButton button) {
                        terminal.playButtonClick();
                        setDirectory(((TerminalFileButton)button).getFile());
                    }
                });
            } else {
                button.setListener(new TerminalButtonListener() {
                    @Override
                    public void clicked(TerminalButton button) {
                        terminal.playButtonClick();

                        //TODO: Implement file viewing
                    }
                });
            }
            buttons.add(button);
        }
    }

    @Override
    public void opened() {
        generateButtons();
    }

    public void select(TerminalButton button) {
        if (buttons.contains(button)) {
            index = buttons.indexOf(button);
        }
    }

    @Override
    public void act() {
        int i = 0;
        for (TerminalButton button : buttons) {
            if (TerminalButton.isMouseOver(SIDE_BORDER, getRenderYForIndex(i), Gdx.graphics.getWidth() - SIDE_BORDER*2, BUTTON_HEIGHT)) {
                index = i;
            }
            if (TerminalButton.isMouseJustDown(SIDE_BORDER, getRenderYForIndex(i), Gdx.graphics.getWidth() - SIDE_BORDER*2, BUTTON_HEIGHT)) {
                button.click();
                break; //MUST BREAK OR LOOP WILL THROW EXCEPTION
            }

            i++;
        }
    }

    private int getRenderYForIndex(int index) {
        return Gdx.graphics.getHeight() - terminal.getHeightOfTitle() - BUTTON_GAP - BUTTON_HEIGHT - (BUTTON_GAP+BUTTON_HEIGHT)*index;
    }

    @Override
    public void draw(Batch batch) {
        int i = 0;
        for (TerminalButton button : buttons) {

            boolean selected = false;
            if (i == index) {
                selected = true;
            }
            button.draw(batch, SIDE_BORDER, getRenderYForIndex(i), Gdx.graphics.getWidth() - SIDE_BORDER*2, BUTTON_HEIGHT, selected);

            i++;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            //Move down
            if (index < buttons.size()-1) {
                index++;
            }

            return true;
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            //Move up
            if (index > 0) {
                index--;
            }

            return true;
        } else if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
            //Select
            if (index < buttons.size()) {
                buttons.get(index).click();
            }

            return true;
        } else if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            //Left

            //TODO: Implement page changing

            return true;
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            //Right

            //TODO: Implement page changing

            return true;
        }

        return false;
    }
}
