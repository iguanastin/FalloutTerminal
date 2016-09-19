package fot.actions;

/**
 * @author austinbt
 */
public class TimerAction extends ScreenAction {

    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;

    private long time;
    private long startTime;

    public TimerAction(long millis) {
        this.time = millis;
    }

    @Override
    protected void started() {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void updated() {
        long current = System.currentTimeMillis();

        if (current > startTime + time) {
            stop();
        }
    }
}
