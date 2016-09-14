package fo.terminal;

import java.util.ArrayList;

/**
 * @author austinbt
 */
public class TerminalFile {
    private boolean directory = false;

    private TerminalFile parent;
    private ArrayList<TerminalFile> children;

    private Object contents;

    private String name;

    public TerminalFile(TerminalFile parent, boolean directory, String name) {
        this.parent = parent;
        this.directory = directory;
        this.name = name;

        children = new ArrayList<TerminalFile>();
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public ArrayList<TerminalFile> getChildren() {
        return children;
    }

    public void addChild(TerminalFile child) {
        children.add(child);
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
