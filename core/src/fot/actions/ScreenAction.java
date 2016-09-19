package fot.actions;

/**
 * @author austinbt
 */
public class ScreenAction {

    private ScreenActionListener listener;

    private boolean running = false;
    private boolean completed = false;

    public void setListener(ScreenActionListener listener) {
        this.listener = listener;
    }

    public final void update() {
        if (listener != null) {
            listener.actionUpdated();
        }

        updated();
    }

    protected void updated() {

    }

    public boolean isRunning() {
        return running;
    }

    public boolean isCompleted() {
        return completed;
    }

    public final void start() {
        running = true;
        if (listener != null) {
            listener.actionStarted();
        }
        started();
    }

    protected void started() {

    }

    public final void stop() {
        running = false;
        if (listener != null) {
            listener.actionCompleted();
        }
        stopped();
    }

    protected void stopped() {

    }
}
