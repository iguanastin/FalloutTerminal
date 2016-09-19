package fot.terminal;

import com.badlogic.gdx.graphics.g2d.Batch;
import fot.actions.ScreenActionListener;
import fot.actions.TimerAction;

/**
 * @author austinbt
 */
public class HackScreen extends TerminalScreen {

    public HackScreen(TerminalMain terminal) {
        super(terminal);

        drawTitleSplitter = false;
        drawTitle = false;
    }

    @Override
    public void opened() {
        final TimerAction testTimer = new TimerAction(TimerAction.SECOND * 5);
        testTimer.setListener(new ScreenActionListener() {
            @Override
            public void actionCompleted() {

            }

            @Override
            public void actionStarted() {

            }

            @Override
            public void actionUpdated() {

            }
        });
        testTimer.start();
        addAction(testTimer);
    }

    @Override
    public void draw(Batch batch) {

    }
}
