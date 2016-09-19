package fot.terminal;

/**
 * @author austinbt
 */
public class ScreenFileButton extends ScreenButton {
    private TerminalFile file;

    public ScreenFileButton(TerminalFile file) {
        super(file.getName());
        this.file = file;
    }

    public TerminalFile getFile() {
        return file;
    }
}
