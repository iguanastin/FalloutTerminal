package fot.actions;

/**
 * A simple listener class that is called by ScreenAction objects under certain circumstances.
 *
 * @author austinbt
 */
public abstract class ScreenActionListener {

    /**
     * Is called when the action controlling this listener is marked as completed
     */
    public void actionCompleted() {

    }

    /**
     * Is called when the action controlling this listener has been started
     */
    public void actionStarted() {

    }

    /**
     * Is called when the action controlling this listener has been updated
     */
    public void actionUpdated() {

    }
}
