package fot.actions;

/**
 * @author austinbt
 */
public class CountAction extends ScreenAction {

    private int target = 0;
    private int count = 0;

    public CountAction(int target) {
        this.target = target;
    }

    @Override
    protected void started() {
        count = 0;
    }

    public int getCount() {
        return count;
    }

    public int getTarget() {
        return target;
    }

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
