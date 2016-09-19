package fot.actions;

/**
 * A basic targetTime-based action. Once started will wait until the given targetTime in milliseconds has passed.
 *
 * @author austinbt
 */
public class TimerAction extends ScreenAction {

    /**
     * 1 second in milliseconds
     */
    public static final long SECOND = 1000;
    /**
     * 1 minute in milliseconds
     */
    public static final long MINUTE = SECOND * 60;

    private long targetTime;
    private long startTime;

    /**
     * Constructs a timer action that, once started, will wait until the given target targetTime has been passed.
     *
     * @param millis Time in milliseconds to wait before completing this action.
     */
    public TimerAction(long millis) {
        this.targetTime = millis;
    }

    /**
     * Sets the start time instance variable
     */
    @Override
    protected void started() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Determines if the target time has passed. If the target time has passed, this action will be stopped with a call to stop()
     *
     * @see ScreenAction#stop()
     */
    @Override
    protected void updated() {
        long current = System.currentTimeMillis();

        if (current > startTime + targetTime) {
            stop();
        }
    }
}
