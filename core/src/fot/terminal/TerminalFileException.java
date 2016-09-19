package fot.terminal;

/**
 * @author austinbt
 */
class TerminalFileException extends RuntimeException {
    TerminalFileException(String message) {
        super(message);
    }

    public TerminalFileException() {
        super();
    }
}
