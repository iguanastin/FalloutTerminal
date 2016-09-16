package fo.terminal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import gui.Gui;

import java.util.ArrayList;

/**
 * @author austinbt
 */
class FileTerminalScreen extends TerminalScreen {

    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_GAP = 15;
    private static final int SIDE_BORDER = 100;

    private TerminalFile file;

    private ArrayList<TerminalButton> buttons;
    private int index = 0;

    private int lastMX = 0;
    private int lastMY = 0;

    FileTerminalScreen(TerminalMain terminal) {
        super(terminal);
        drawTitle = true;
        drawTitleSplitter = true;

        buttons = new ArrayList<TerminalButton>();
    }

    FileTerminalScreen setFile(TerminalFile file) {
        this.file = file;

        index = 0;

        if (file.isDirectory()) {
            generateDirectoryButtons();
        }

        return this;
    }

    private void generateDirectoryButtons() {
        buttons.clear();

        int availableSpace = Gdx.graphics.getHeight() - terminal.getHeightOfTitle() - BUTTON_GAP - BUTTON_HEIGHT;
        int buttonsPerPage = availableSpace / (BUTTON_HEIGHT + BUTTON_GAP);

        //TODO: Implement paging

        index = 0;

        for (TerminalFile file : this.file.getChildren()) {
            TerminalButton button = new TerminalFileButton(file);
            button.setListener(new TerminalButtonListener() {
                @Override
                public void clicked(TerminalButton button) {
                    setFile(((TerminalFileButton) button).getFile());
                }
            });
            buttons.add(button);
        }
    }

    @Override
    public void opened() {
        generateDirectoryButtons();
    }

    @Override
    public void act() {
        handleMouseInput();
    }

    private void handleMouseInput() {
        if (file != null && file.isDirectory()) {
            int i = 0;
            for (TerminalButton button : buttons) {
                //Mouse over button
                if (TerminalButton.isMouseOver(SIDE_BORDER, getRenderYForIndex(i), Gdx.graphics.getWidth() - SIDE_BORDER * 2, BUTTON_HEIGHT)) {
                    if (Gdx.input.getX() != lastMX || Gdx.input.getY() != lastMY) {
                        lastMX = Gdx.input.getX();
                        lastMY = Gdx.input.getY();

                        if (index != i) {
                            terminal.playButtonClick();
                        }

                        index = i;
                    }
                }

                //Mouse down over button
                if (TerminalButton.isMouseJustDown(SIDE_BORDER, getRenderYForIndex(i), Gdx.graphics.getWidth() - SIDE_BORDER * 2, BUTTON_HEIGHT)) {
                    if (index == i) {
                        terminal.playButtonClick();
                        button.click();
                        break; //MUST BREAK OR LOOP WILL THROW EXCEPTION
                    }
                }

                i++;
            }
        }
    }

    private int getRenderYForIndex(int index) {
        return Gdx.graphics.getHeight() - terminal.getHeightOfTitle() - BUTTON_GAP - BUTTON_HEIGHT - (BUTTON_GAP + BUTTON_HEIGHT) * index;
    }

    @Override
    public void draw(Batch batch) {
        if (file != null) {
            if (file.isDirectory()) {
                drawDirectoryButtons(batch);
            } else if (file.isFile()) {
                Gui.begin(batch);
                int height = Gdx.graphics.getHeight() - terminal.getHeightOfTitle() - 25;
                String contents = (String)file.getContents();
                if (contents == null) {
                    contents = "[EMPTY]";
                }
                TerminalMain.mediumFont.setColor(Gui.text_color);
                Gui.drawTextInArea(batch, TerminalMain.mediumFont, contents, SIDE_BORDER, height, Gdx.graphics.getWidth() - SIDE_BORDER * 2, height - SIDE_BORDER);
            }
        }
    }

    private void drawDirectoryButtons(Batch batch) {
        int i = 0;
        for (TerminalButton button : buttons) {

            boolean selected = false;
            if (i == index) {
                selected = true;
            }
            button.draw(batch, SIDE_BORDER, getRenderYForIndex(i), Gdx.graphics.getWidth() - SIDE_BORDER * 2, BUTTON_HEIGHT, selected);

            i++;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            //Move down
            if (index < buttons.size() - 1) {
                index++;
                terminal.playButtonClick();
            }

            return true;
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            //Move up
            if (index > 0) {
                index--;
                terminal.playButtonClick();
            }

            return true;
        } else if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
            //Select
            if (index < buttons.size()) {
                buttons.get(index).click();
                terminal.playButtonClick();
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
