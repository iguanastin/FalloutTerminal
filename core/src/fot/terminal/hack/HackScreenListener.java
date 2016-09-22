package fot.terminal.hack;

/**
 * @author austinbt
 */
public interface HackScreenListener {
    void hackSuccess();

    void hackFailure();

    void hackCancel();
}
