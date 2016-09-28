package fot.terminal.hack;

/**
 * @author austinbt
 */
public interface HackScreenListener {
    /**
     * Called when the parent HackScreen has successfully been hacked and completed the closing animation
     */
    void hackSuccess();

    /**
     * Called when the parent HackScreen has been failed and completed the fail animation
     */
    void hackFailure();

    /**
     * Called when the user manually cancels the hack
     */
    void hackCancel();
}
