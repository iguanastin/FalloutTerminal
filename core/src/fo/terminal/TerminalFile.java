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
        this.parent = parent;
        this.directory = directory;
        this.name = name;

        children = new ArrayList<TerminalFile>();
        if (parent != null) {
            parent.addChild(this);
        }
    }

    ArrayList<TerminalFile> getChildren() {
        return children;
    }

    void addChild(TerminalFile child) {
        if (!children.contains(child)) {
            children.add(child);
        }
    }

    TerminalFile getParent() {
        return parent;
    }

    Object getContents() {
        return contents;
    }

    void setContents(Object contents) {
        this.contents = contents;
    }

    String getName() {
        return name;
    }

    boolean isDirectory() {
        return directory;
    }

    boolean isFile() {
        return !directory;
    }
}
