package fo.terminal;

import java.util.ArrayList;

/**
 * @author austinbt
 */
class TerminalFile {
    private boolean directory = false;

    private TerminalFile parent;
    private ArrayList<TerminalFile> children;

    private Object contents;

    private String name;

    TerminalFile(TerminalFile parent, boolean directory, String name) {
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
        if (!children.contains(child) && child.getParent() == null) {
            children.add(child);
            child.setParent(this);
        }
    }

    public TerminalFile getParent() {
        return parent;
    }

    public void setParent(TerminalFile parent) {
        this.parent = parent;
    }

    public Object getContents() {
        return contents;
    }

    public void setContents(Object contents) {
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return directory;
    }

    public boolean isFile() {
        return !directory;
    }

    @Override
    public String toString() {
        return getName();
    }
}
