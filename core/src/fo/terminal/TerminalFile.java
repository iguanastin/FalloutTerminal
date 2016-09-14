package fo.terminal;

/**
 * @author austinbt
 */
public class TerminalFile {
    private boolean directory = false;

    private TerminalFile parent;

    private Object contents;

    public TerminalFile(TerminalFile parent, boolean directory) {
        this.parent = parent;
        this.directory = directory;
    }

    public TerminalFile getParent() {
        return parent;
    }

    public Object getContents() {
        return contents;
    }

    public void setContents(Object contents) {
        this.contents = contents;
    }

    public boolean isDirectory() {
        return directory;
    }

    public boolean isFile() {
        return !directory;
    }
}
