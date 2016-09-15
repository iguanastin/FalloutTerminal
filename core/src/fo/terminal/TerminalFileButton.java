package fo.terminal;

/**
 * @author austinbt
 */
public class TerminalFileButton extends TerminalButton {
    private TerminalFile file;

    public TerminalFileButton(TerminalFile file) {
        super(file.getName());
        this.file = file;
    }

    public TerminalFile getFile() {
        return file;
    }
}
