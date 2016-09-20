package fot.terminal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import fot.actions.CountAction;
import fot.actions.ScreenActionListener;
import gui.Gui;

/**
 * @author austinbt
 */
public class HackScreen extends TerminalScreen {

    private static final int EDGE_GAP = 100;
    private static final int HEX_GAP = 500;

    private int attempts = 3;
    private int difficulty;

    private HackData data;

    private boolean drawFirstLine = true, drawSecondLine, drawAttemptsLine, drawContents, drawOutput, drawHexLines;
    private boolean allowInput;

    private String[] hexes = new String[HackData.linesPerCol * 2];

    private CountAction firstLineCounter, secondLineCounter, hexLineCounter;

    private String firstLine = "ROBCO INDUSTRIES (TM) TERMLINK PROTOCOL";
    private String secondLine = "ENTER PASSWORD NOW";
    private String attemptsLine = " ATTEMPT(S) LEFT: ";

//    private String[] veryEasyWords = {"PLOT", "POLE", "BLUE", "PLAN", "FLEE", "FILE", "ALLY", "PALE", "ELSE", "PLUS", "PILE", "WAIT", "WANT", "BACK", "BARE", "EAST", "SALT", "BELT", "PART", "BASE", "BEAT", "WENT", "LOTS", "BEEN", "LETS", "BIDS", "SETS", "NEWS", "LESS", "BEAR", "BITS", "DENS", "HELP", "WERE", "HERE", "TILE", "WISP", "HIRE", "HIDE", "WILD", "LONE", "FOES", "FORK", "DONE", "NINE", "FOUL", "FEND", "FANG", "NONE", "SONG", "GONE"};
//    private String[] easyWords = {"CITIES", "DRONES", "CHANGE", "TRADER", "SHOWED", "CHASED", "RUINED", "TUNNEL", "CHANCE", "SHINER", "THANKS", "SENSED", "DONNED", "SAYING", "PERISH", "MAKING", "FAMILY", "PAYING", "EASILY", "POLICE", "TAKING", "LAUNCH", "NATION", "LIVING", "HAVING", "LOSING", "TRYING", "RAVING", "RADIUS", "WAGONS", "STAIRS", "DIVINE", "SIRENS", "THEIRS", "MOVING", "TAVERN", "ROVING", "HEATED", "DESERT", "HEALED", "FEARED", "GEIGER", "BEATEN", "BELIES", "DEALER", "BEETLE", "DEVOUT", "FELLOW", "BULLET", "CRATER", "CROWDS", "CRAVEN", "BLASTS", "SPIKES", "RANGES", "STORES", "LEARNS", "GRATES", "STONES", "BRICKS", "STATED", "SERIES", "STAGES", "WASTED", "WOODEN", "PERSON", "CORNER", "MELTED", "GOLDEN", "DELVED", "FOLLOW", "PULLED", "POSTED", "WORKED", "BELIEF", "PRETTY", "FRAMES", "WISHES", "NIGHTS", "LIGHTS", "FIGHTS", "GROWTH", "ARGUED", "ORNATE"};
//    private String[] averageWords = {"UNDERWAY", "ATTEMPTS", "ARRESTED", "INTENDED", "INTRUDER", "RELEASED", "RESULTED", "RETURNED", "ANIMATED", "DEFEATED", "ACTUALLY", "REPELLED", "INJURIES", "REVERTED", "KNUCKLES", "INTERNAL", "SHIPMENT", "GRUELING", "SUPPLIED", "APPARENT", "THOUSAND", "PROPERTY", "GRASPING", "TRUSTING", "CREATING", "PLANNING", "PROBLEMS", "PREPARED", "PAINTING", "TRAINING", "BRAWLING", "CLAIMING", "CRAWLING", "REPAIRED", "DEFENDED", "DEVOURED", "FOLLOWED", "MAINLAND", "SHIELDED", "LAUNCHED", "PRESUMED", "INFESTED", "CHILDREN", "INFECTED", "UNVEILED", "RECORDED", "STRONGER", "INFRARED", "ESCORTED", "IMPOSING", "UNLOCKED", "UNCOMMON", "OCCUPIED", "SYNOPSES", "REPORTED", "UNCOVERS", "INVOLVED", "INVENTED", "FEVERISH", "CRACKING", "RESIDING", "CONFLICT", "CACKLING", "SECURITY", "DEFINITE", "REMOVING", "TIMELINE", "STEALING", "METALLIC", "PERILOUS", "LEARNING", "CEREMONY"};
//    private String[] hardWords = {"CONCOCTION", "PHILOSOPHY", "BLACKSTAFF", "CHARACTERS", "COMMISSION", "REASONABLY", "INCENDIARY", "ADVANTAGES", "UNFAITHFUL", "INEVITABLE", "BOBBLEHEAD", "COMPLETELY", "BELONGINGS", "VEHEMENTLY", "BEFRIENDED", "DECIMATING", "ENCOURAGED", "SKYSCRAPER", "DISTURBING", "STRATEGIES", "APPROACHES", "ENDORPHINS", "FLOURISHED", "INTENTIONS", "BUSINESSES"};
//    private String[] veryHardWords = {"ANTICIPATING", "RESURRECTION", "APPRECIATION", "INFILTRATION", "TRIUMPHANTLY", "TRANSCRIBING", "PURIFICATION", "ACCOMPANYING", "ORGANIZATION", "RECUPERATING", "CIVILIZATION", "BRAINWASHING", "AUTHENTICITY", "TECHNOLOGIES", "CONSPIRATORS", "IMMACULATELY", "RELATIONSHIP", "CONSTRUCTION", "BROADCASTING", "DISAPPEARING", "TRANSMITTERS", "INDEFINITELY", "HEADQUARTERS", "CONVERSATION", "PARTNERSHIPS", "SECLUSIONIST", "TRANSMISSION", "CIRCUMSTANCE", "REPRIMANDING", "APPREHENSIVE", "ENCOUNTERING", "INITIATEHOOD", "SPOKESPERSON", "LABORATORIES", "REPLENISHING", "SUBTERRANEAN"};

    public HackScreen(TerminalMain terminal, int difficulty) {
        super(terminal);

        drawTitleSplitter = false;
        drawTitle = false;

        this.difficulty = difficulty;

        generateHexValues();

        data = new HackData(difficulty);
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
        terminal.playCharScroll();

        /*
        Set up and play opening animation ------------------------------------------------------------------------------
         */
        firstLineCounter = new CountAction(firstLine.length());
        firstLineCounter.setListener(new ScreenActionListener() {
            @Override
            public void actionCompleted() {
                drawSecondLine = true;
                secondLineCounter.start();
                terminal.playCharScroll();
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
                terminal.playCharScroll();
            }
        });
        addAction(secondLineCounter);

        hexLineCounter = new CountAction(HackData.linesPerCol * 2);
        hexLineCounter.setListener(new ScreenActionListener() {
            @Override
            public void actionCompleted() {
                terminal.playCharScroll();
                drawContents = true;
                drawOutput = true;
                allowInput = true;
            }

            @Override
            public void actionUpdated() {
                if (hexLineCounter.getCount() == HackData.linesPerCol) terminal.playCharScroll();
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
            if (!firstLineCounter.isCompleted()) {
                font.draw(batch, firstLine.substring(0, firstLineCounter.getCount()), EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - Gui.stutter);
            } else {
                font.draw(batch, firstLine, EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - Gui.stutter);
            }
        }

        //Draw second line
        if (drawSecondLine) {
            if (!secondLineCounter.isCompleted()) {
                font.draw(batch, secondLine.substring(0, secondLineCounter.getCount()), EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() - Gui.stutter);
            } else {
                font.draw(batch, secondLine, EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() - Gui.stutter);
            }
        }

        //Draw attempts line
        if (drawAttemptsLine) {
            font.draw(batch, attempts + attemptsLine, EDGE_GAP, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * 3 - Gui.stutter);

            Gui.end(batch);
            Gui.begin(Gui.sr, ShapeRenderer.ShapeType.Filled, Gui.trim_color);

            //Draw attempt squares
            for (int i = 0; i < attempts; i++) {
                Gui.sr.rect(EDGE_GAP + Gui.getStringPixelWidth(font, attempts + attemptsLine) + (font.getLineHeight() + 10)*(i), Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight()*4 + 5 - Gui.stutter, font.getLineHeight(), font.getLineHeight());
            }

            Gui.end(Gui.sr);
            Gui.begin(batch);
        }

        //Draw hex values
        if (drawHexLines) {
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
            //Draw left column
            for (int i = 0; i < HackData.linesPerCol; i++) {
                font.draw(batch, data.getExpandedLine(i, 0), EDGE_GAP + Gui.getStringPixelWidth(font, hexes[0]) + 20, Gdx.graphics.getHeight() - EDGE_GAP - font.getLineHeight() * (5 + i) - Gui.stutter);
            }
        }

        //Draw output
        if (drawOutput) {
            //TODO: DRAW OUTPUT
        }
    }
}
