package fot.actions;

/**
 * A basic update based counting action.
 *
 * @author austinbt
 */
public class CountAction extends ScreenAction {

    private int target = 0;
    private int count = 0;

    /**
     * Constructs this class with given a target count. Counts always begin from 0 and count positive or negative once per update until the target is reached.
     *
     * @param target Target value to count to
     */
    public CountAction(int target) {
        this.target = target;
    }

    /**
     * Retrieves the current count value
     *
     * @return The current count of this counter
     */
    public int getCount() {
        return count;
    }

    /**
     * Retrieves the target count value
     *
     * @return The target count that this counter counts to
     */
    public int getTarget() {
        return target;
    }

    /**
     * Updates counting for this counter.
     *
     * If count has reached target, stop() will be called
     *
     * @see ScreenAction#stop()
     */
    @Override
    protected void updated() {
        if (count > target) {
            count--;
        } else if (count < target) {
            count++;
        } else {
            stop();
        }
    }
}
