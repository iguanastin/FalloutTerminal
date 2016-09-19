package fot.terminal;

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

    public boolean removeChild(TerminalFile file) {
        if (children.contains(file)) {
            file.nullifyParent();
            return children.remove(file);
        }

        return false;
    }

    private void nullifyParent() {
        parent = null;
    }

    public TerminalFile getParent() {
        return parent;
    }

    private void setParent(TerminalFile parent) {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
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
