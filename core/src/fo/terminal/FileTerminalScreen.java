package fo.terminal;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * @author austinbt
 */
public class FileTerminalScreen extends TerminalScreen {

    private TerminalMain terminal;

    private TerminalFile file;

    public FileTerminalScreen() {
        drawTitle = true;
        drawTitleSplitter = true;
    }

    public FileTerminalScreen setDirectory(TerminalFile file) {
        if (!file.isDirectory()) {
            throw new TerminalFileException("Cannot open non-directory");
        }

        this.file = file;
        
        return this;
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
}
