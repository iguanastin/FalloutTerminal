package fot.terminal.hack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import fot.actions.CountAction;
import fot.actions.ScreenActionListener;
import fot.terminal.TerminalAudio;
import fot.terminal.TerminalMain;
import fot.terminal.TerminalScreen;
import gui.Gui;

/**
 * @author austinbt
 */
public class HackScreen extends TerminalScreen {

    private static final int EDGE_GAP = 100;
    private static final int HEX_GAP = 500;
    private static final int OUTPUT_WIDTH = 24;

    private CountAction firstLineCounter, secondLineCounter, hexLineCounter;

    private final String firstLine = "ROBCO INDUSTRIES (TM) TERMLINK PROTOCOL";
    private final String secondLine = "ENTER PASSWORD NOW";
    private final String attemptsLine = " ATTEMPT(S) LEFT: ";

    private String[] hexes = new String[HackData.linesPerCol * 2];

    private boolean drawFirstLine = true, drawSecondLine, drawAttemptsLine, drawContents, drawOutput, drawHexLines;
    private boolean allowInput;

    private HackData data;
    private HackScreenListener listener;
    private String[] output;
    private String selected = "";
    private int index = 0;


    public HackScreen(TerminalMain terminal, int difficulty) {
        super(terminal);

        drawTitleSplitter = false;
        drawTitle = false;

        generateHexValues();

        data = new HackData(difficulty);
        output = new String[HackData.linesPerCol - 1];

        setIndex(0);
    }

    public void setIndex(int i) {
        if (i < 0 || i > data.length()) {
            throw new ArrayIndexOutOfBoundsException("Index " + i + " out of bounds");
        }

        index = i;
        selected = getSelection(i);

        if (selected.length() == 1) {
            TerminalAudio.playCharSingle();
        } else {
            TerminalAudio.playCharMultiple();
        }
    }

    public String getSelection(int i) {
        if (data.isBracketGroup(i)) {
            return data.getBracketGroup(i);
        } else if (data.isWord(i)) {
            return data.getWord(i);
        } else {
            return data.getChar(i) + "";
        }
    }

    public void addOutput(String msg) {
        if (msg.length() < OUTPUT_WIDTH - 1) {
            shiftConsoleUp();
            output[0] = '>' + msg;
        } else {
            String work = msg;
            String excess = "";

            while (work.length() > OUTPUT_WIDTH - 1) {
                if (work.contains(" ")) {
                    excess = work.substring(work.lastIndexOf(' ') + 1) + ' ' + excess;
                    work = work.substring(0, work.lastIndexOf(' '));
                } else {
                    excess = work.charAt(work.length() - 1) + excess;
                    work = work.substring(0, work.length() - 1);
                }
            }

            addOutput(work);
            addOutput(excess);
        }
    }

    public String[] getOutput() {
        return output;
    }

    private void shiftConsoleUp() {
        for (int i = output.length - 1; i > 0; i--) {
            output[i] = output[i - 1];
        }
    }

    public void setListener(HackScreenListener listener) {
        this.listener = listener;
    }

    private void generateHexValues() {
        int hexVal = 63088;
        for (int i = 0; i < hexes.length; i++) {
            hexVal += 4 + (int) (Math.random() * 8);

            hexes[i] = "0x" + Integer.toHexString(hexVal).toUpperCase();
        }
    }

    @Override
    public void opened() {
        TerminalAudio.playCharScroll();

        /*
        Set up and play opening animation ------------------------------------------------------------------------------
         */
        firstLineCounter = new CountAction(firstLine.length());
        firstLineCounter.setListener(new ScreenActionListener() {
            @Override
            public void actionCompleted() {
                drawSecondLine = true;
                secondLineCounter.start();
                TerminalAudio.playCharScroll();
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
                TerminalAudio.playCharScroll();
            }
        });
        addAction(secondLineCounter);

        hexLineCounter = new CountAction(HackData.linesPerCol * 2);
        hexLineCounter.setListener(new ScreenActionListener() {
            @Override
            public void actionCompleted() {
                TerminalAudio.playCharScroll();
                drawContents = true;
                drawOutput = true;
                allowInput = true;
            }

            @Override
            public void actionUpdated() {
                if (hexLineCounter.getCount() == HackData.linesPerCol) TerminalAudio.playCharScroll();
            }
        });
        addAction(hexLineCounter);
        /*
        ----------------------------------------------------------------------------------------------------------------
         */
    }

    @Override
    public void draw(Batch batch) {
        Gui.begin(batch);
        BitmapFont font = TerminalMain.mediumFont;
        font.setColor(Gui.text_color);

        //Draw first line
        if (drawFirstLine) {
            Gui.begin(batch);

            if (!firstLineCounter.isCompleted()) {
                font.draw(batch, firstLine.substring(0, firstLineCounter.getCount()), EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - Gui.stutter);
            } else {
                font.draw(batch, firstLine, EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - Gui.stutter);
            }
        }

        //Draw second line
        if (drawSecondLine) {
            Gui.begin(batch);

            if (!secondLineCounter.isCompleted()) {
                font.draw(batch, secondLine.substring(0, secondLineCounter.getCount()), EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() - Gui.stutter);
            } else {
                font.draw(batch, secondLine, EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() - Gui.stutter);
            }
        }

        //Draw attempts line
        if (drawAttemptsLine) {
            font.draw(batch, data.getAttempts() + attemptsLine, EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * 3 - Gui.stutter);

            Gui.end(batch);
            Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.trim_color);

            //Draw attempt squares
            for (int i = 0; i < data.getAttempts(); i++) {
                Gui.sr.rect(EDGE_GAP + Gui.getStringPixelWidth(font, data.getAttempts() + attemptsLine) + (font.getLineHeight() + 10) * (i), Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * 4 + 5 - Gui.stutter, font.getLineHeight(), font.getLineHeight());
            }

            Gui.end(Gui.sr);
            Gui.begin(batch);
        }

        //Draw hex values
        if (drawHexLines) {
            Gui.begin(batch);

            for (int i = 0; i < hexLineCounter.getCount(); i++) {
                if (i < HackData.linesPerCol) {
                    font.draw(batch, hexes[i], EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * (5 + i) - Gui.stutter);
                } else {
                    font.draw(batch, hexes[i], EDGE_GAP + HEX_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * (5 + i - HackData.linesPerCol) - Gui.stutter);
                }
            }
        }

        //Draw contents
        if (drawContents) {
            drawContents(batch, font);
        }

        if (drawOutput) {
            Gui.begin(batch);

            font.setColor(Gui.text_color);
            //Draw output
            for (int i = 0; i < output.length; i++) {
                if (output[i] != null) {
                    font.draw(batch, output[i], EDGE_GAP + HEX_GAP * 2 + 20, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * (output.length - i + 4) - Gui.stutter);
                }
            }

            //Draw selection line
            font.setColor(Gui.text_color);
            if (selected == null || selected.isEmpty()) {
                selected = ">";
            }
            font.draw(batch, ">" + selected, EDGE_GAP + HEX_GAP * 2 + 20, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * (output.length + 5) - Gui.stutter);
        }
    }

    private void drawContents(Batch batch, BitmapFont font) {
        Gui.begin(batch);
        for (int i = 0; i < HackData.linesPerCol; i++) {
            font.draw(batch, data.getExpandedLine(i, 0), EDGE_GAP + Gui.getStringPixelWidth(font, hexes[0]) + 20, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * (5 + i) - Gui.stutter);
            font.draw(batch, data.getExpandedLine(i, 1), EDGE_GAP + Gui.getStringPixelWidth(font, hexes[0]) + 20 + HEX_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * (5 + i) - Gui.stutter);
        }

        drawContentSelection(batch, font);
    }

    private void drawContentSelection(Batch batch, BitmapFont font) {
        Gui.end(batch);

        int x = index % HackData.lineLength, y = index / HackData.lineLength;
        int col = 0;
        if (y >= HackData.linesPerCol) {
            col = 1;
            y -= HackData.linesPerCol;
        }
        if (data.isWord(index)) {
            x = data.getWordStart(index) % HackData.lineLength;
        }

        //Calculate selection render position
        int renderX = EDGE_GAP + (int) Gui.getStringPixelWidth(font, hexes[0]) + 20 + (int) Gui.getStringPixelWidth(font, data.getLine(y, col).substring(0, x))*2;
        int renderY = Gdx.graphics.getHeight() - EDGE_GAP - (int) font.getLineHeight() * (5 + y) - Gui.stutter;
        if (col == 1) renderX += HEX_GAP;

        Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.trim_color);
        //Draw overlay based on selection type
        if (data.isBracketGroup(index)) {
            Gui.sr.rect(renderX - 5, renderY + 5, Gui.getStringPixelWidth(font, data.getBracketGroup(index))*2, -font.getLineHeight());
        } else if (data.isWord(index)) {
            //TODO: Implement word wrapping rendering

            Gui.sr.rect(renderX - 5, renderY + 5, Gui.getStringPixelWidth(font, data.getWord(index))*2, -font.getLineHeight());
        } else {
            Gui.sr.rect(renderX - 5, renderY + 5, Gui.getStringPixelWidth(font, "X "), -font.getLineHeight());
        }

        Gui.end(Gui.sr);
        Gui.begin(batch);
        font.setColor(Gui.alternate_color);

        if (data.isWord(index)) {
            font.draw(batch, HackData.expandString(selected), renderX, renderY);

            //TODO: Implement proper rendering of wrapped words
        } else {
            font.draw(batch, HackData.expandString(selected), renderX, renderY);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            left();
            return true;
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            right();
            return true;
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            up();
            return true;
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            down();
            return true;
        } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE || keycode == Input.Keys.E) {
            if (data.isBracketGroup(index)) {
                addOutput("Removed dud: " + data.getBracketGroup(index));

                data.removeDud(index);

                setIndex(index);

                //TODO: Implement allowance replenishment
            } else if (data.isWord(index)) {
                //TODO: Implement solution checking
            } else { //Is normal char
                TerminalAudio.playPassBad();

                addOutput("Invalid charset: " + selected);
            }

            TerminalAudio.playButtonClick();
        } else if (keycode == Input.Keys.ESCAPE) {
            if (listener != null) {
                listener.hackCancel();
            }

            TerminalAudio.playMenuCancel();
        }

        return false;
    }

    private void down() {
        if (index < data.length() - HackData.lineLength) {
            setIndex(index + HackData.lineLength);
        }
    }

    private void up() {
        if (index >= HackData.lineLength) {
            setIndex(index - HackData.lineLength);
        }
    }

    private void right() {
        if (data.isWord(index)) {
            if (data.isWordWrapped(index) && index < HackData.lineLength*HackData.linesPerCol) { //Word is wrapped and index is in first column
                setIndex(HackData.lineLength*HackData.linesPerCol + index - index%HackData.lineLength); //Set to same row in second column at x=0
            } else if (data.getWordEnd(index) < data.length()) {
                setIndex(data.getWordEnd(index));
            }
        } else {
            if (index + 1 < data.length()) {
                if (index%HackData.lineLength == HackData.lineLength-1) {
                    if (index < HackData.lineLength*HackData.linesPerCol) {
                        setIndex(HackData.lineLength*HackData.linesPerCol + index - index%HackData.lineLength); //Set to same row in second column at x=0
                    }
                } else {
                    setIndex(index + 1);
                }
            }
        }
    }

    private void left() {
        if (data.isWord(index)) {
            if (data.isWordWrapped(index) && index >= HackData.lineLength*HackData.linesPerCol) { //Word is wrapped and index is in second column
                setIndex(index - HackData.lineLength*HackData.linesPerCol + HackData.lineLength - index%HackData.lineLength - 1); //Set to same row in first column at x=HackData.lineLength
            } else if (data.getWordStart(index) > 0) {
                setIndex(data.getWordStart(index) - 1);
            }
        } else {
            if (index > 0) {
                if (index%HackData.lineLength == 0) { //Index on line is 0
                    if (index >= HackData.lineLength*HackData.linesPerCol) {
                        setIndex(index - HackData.lineLength * HackData.linesPerCol + HackData.lineLength - index % HackData.lineLength - 1); //Set to same row in first column at x=HackData.lineLength
                    }
                } else {
                    setIndex(index - 1);
                }
            }
        }
    }
}
