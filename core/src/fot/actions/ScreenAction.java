package fot.actions;

/**
 * A generic type that provides a framework for actions running in a scene. Primarily used for animation, but not exclusively.
 *
 * @author austinbt
 */
public abstract class ScreenAction {

    private ScreenActionListener listener;

    private boolean running = false;
    private boolean completed = false;

    /**
     * Sets the listener to fire events at for this action.
     *
     * @param listener Listener to fire events at
     */
    public void setListener(ScreenActionListener listener) {
        this.listener = listener;
    }

    /**
     * Updates listener events if listener is non-null and calls updated()
     *
     * @see ScreenAction#updated()
     */
    public final void update() {
        if (listener != null) {
            listener.actionUpdated();
        }

        updated();
    }

    /**
     * Is called by the update(), which is in turn called by the TerminalScreen hosting this action.
     *
     * This method is intended to be overridden for its subclasses
     *
     * @see ScreenAction#update()
     */
    protected void updated() {}

    /**
     * Determines whether this action is currently running or not.
     *
     * @return True if this action is currently active, false otherwise.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Determines whether this action has finished and is ready to be removed.
     *
     * @return True if this action is finished and ready to be removed, false otherwise.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Starts this action, fires a listener event if non-null, and calls the started() method.
     *
     * @see ScreenAction#started()
     */
    public final void start() {
        running = true;
        if (listener != null) {
            listener.actionStarted();
        }
        started();
    }

    /**
     * Is called when start() is called, which is called when this action has been started.
     *
     * This method is intended to be overridden by its subclasses
     */
    protected void started() {

    }

    /**
     * Stops this action and marks it as completed. Calls stopped()
     *
     * @see ScreenAction#stopped()
     */
    public final void stop() {
        running = false;
        completed = true;
        if (listener != null) {
            listener.actionCompleted();
        }
        stopped();
    }

    /**
     * Is called when stop is called.
     *
     * This method is intended to be overridden by its subclasses
     *
     * @see ScreenAction#stop()
     */
    protected void stopped() {

    }
}
