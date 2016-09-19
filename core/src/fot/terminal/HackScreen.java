package fot.terminal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import fot.actions.CountAction;
import fot.actions.ScreenActionListener;
import gui.Gui;

/**
 * @author austinbt
 */
public class HackScreen extends TerminalScreen {

    private static final int HEX_ROWS = 18;
    private static final int EDGE_GAP = 100;
    private static final int HEX_GAP = 800;

    private int attempts = 3;

    private boolean drawFirstLine = true, drawSecondLine, drawAttemptsLine, drawContents, drawOutput, drawHexLines;
    private boolean allowInput;

    private String[] hexes = new String[HEX_ROWS * 2];

    private CountAction firstLineCounter, secondLineCounter, hexLineCounter;

    private String firstLine = "ROBCO INDUSTRIES (TM) TERMLINK PROTOCOL";
    private String secondLine = "ENTER PASSWORD NOW";
    private String attemptsLine = " ATTEMPT(S) LEFT: ";

    public HackScreen(TerminalMain terminal) {
        super(terminal);

        drawTitleSplitter = false;
        drawTitle = false;

        int hexVal = 63088;
        for (int i = 0; i < hexes.length; i++) {
            hexVal += 4 + (int) (Math.random() * 8);

            hexes[i] = Integer.toHexString(hexVal).toUpperCase();
        }
    }

    @Override
    public void opened() {
        firstLineCounter = new CountAction(firstLine.length());
        firstLineCounter.setListener(new ScreenActionListener() {
            @Override
            public void actionCompleted() {
                drawSecondLine = true;
                secondLineCounter.start();
            }
        });
        firstLineCounter.start();
        addAction(firstLineCounter);

        secondLineCounter = new CountAction(secondLine.length());
        secondLineCounter.setListener(new ScreenActionListener() {
            @Override
            public void actionCompleted() {
                drawAttemptsLine = true;
                drawHexLines = true;
                hexLineCounter.start();
            }
        });
        addAction(secondLineCounter);

        hexLineCounter = new CountAction(HEX_ROWS * 2);
        hexLineCounter.setListener(new ScreenActionListener() {
            @Override
            public void actionCompleted() {

            }
        });
        addAction(hexLineCounter);
    }

    @Override
    public void draw(Batch batch) {
        Gui.begin(batch);
        BitmapFont font = TerminalMain.mediumFont;
        font.setColor(Gui.text_color);

        //Draw first line
        if (drawFirstLine) {
            if (!firstLineCounter.isCompleted()) {
                font.draw(batch, firstLine.substring(0, firstLineCounter.getCount()), EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP);
            } else {
                font.draw(batch, firstLine, EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP);
            }
        }

        //Draw second line
        if (drawSecondLine) {
            if (!secondLineCounter.isCompleted()) {
                font.draw(batch, secondLine.substring(0, secondLineCounter.getCount()), EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight());
            } else {
                font.draw(batch, secondLine, EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight());
            }
        }

        //Draw attempts line
        if (drawAttemptsLine) {
            font.draw(batch, attempts + attemptsLine, EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * 3);
        }

        //Draw hex values
        if (drawHexLines) {
            for (int i = 0; i < hexLineCounter.getCount(); i++) {
                if (i < HEX_ROWS) {
                    font.draw(batch, hexes[i], EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * (5 + i));
                } else {
                    font.draw(batch, hexes[i], EDGE_GAP + HEX_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * (5 + i - HEX_ROWS));
                }
            }
        }
    }
}
