package fot.terminal;

import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class HackData {
    public static final int lineLength = 12;
    public static final int linesPerCol = 18;
    public static final int columns = 2;

    public static final int numChars = lineLength * linesPerCol * columns;

    private int difficulty;

    public static final int DIFF_VERY_EASY = 0;
    public static final int DIFF_EASY = 1;
    public static final int DIFF_AVERAGE = 2;
    public static final int DIFF_HARD = 3;
    public static final int DIFF_VERY_HARD = 4;

    private static final char[] randomChars = {'{', ']', '(', ')', '[', ']', '<', '>', '?', '-', '_', '\'', ';', '^', '*', '\\', '/', '!', '+', '#', '$', '%', '.', '|', ':', '"', '=', ','};
    private static final String bracketStarts = "([{<";
    private static final String bracketEnds = ")]}>";

    private final static ArrayList<String> easyWords = new ArrayList<String>();
    private final static ArrayList<String> veryEasyWords = new ArrayList<String>();
    private final static ArrayList<String> averageWords = new ArrayList<String>();
    private final static ArrayList<String> hardWords = new ArrayList<String>();
    private final static ArrayList<String> veryHardWords = new ArrayList<String>();

    private String allText;

    public HackData(int difficulty) {
        this.difficulty = difficulty;

        if (easyWords.isEmpty()) {
            loadWordsFromFile("words.txt");
        }

        generateAllText();
    }

    public String getAllText() {
        return allText;
    }

    public String getLine(int row, int col) {
        int rowIndex = row;
        if (col == 1) rowIndex += linesPerCol;

        int index = rowIndex * lineLength;

        return allText.substring(index, index + lineLength);
    }

    public String getExpandedLine(int row, int col) {
        String line = getLine(row, col);
        String work = "";

        for (int i = 1; i < lineLength; i++) {
            work += line.charAt(i) + " ";
        }

        return work.substring(0, work.length() - 1);
    }

    private void generateAllText() {
        randomizeAllText();

        fillWordsInAllText();
    }

    private void fillWordsInAllText() {
        ArrayList<String> words = getWordSet(difficulty);
        int wordCount = getWordCount(difficulty);
        Random rand = new Random();

        while (wordCount > 0) {
            String word = words.get(rand.nextInt(words.size()));

            if (!allText.contains(word)) {
                putWordInAllText(word);

                wordCount--;
            }
        }
    }

    private void putWordInAllText(String word) {
        Random rand = new Random();

        while (true) {
            int pos = rand.nextInt(numChars - word.length());

            if (!overlapsOtherWord(word, pos)) {
                insertWord(word, pos);
                break;
            }
        }
    }

    private void insertWord(String word, int pos) {
        allText = allText.substring(0, pos) + word + allText.substring(pos + word.length());
    }

    private boolean overlapsOtherWord(String word, int pos) {
        String inQuestion = allText.substring(pos, pos + word.length());

        for (int i = 0; i < inQuestion.length(); i++) {
            if (Character.isLetter(inQuestion.charAt(i))) {
                return true;
            }
        }
        if (pos > 0 && Character.isLetter(allText.charAt(pos-1))) {
            return true;
        }
        if (pos + word.length() + 1 < allText.length() && Character.isLetter(allText.charAt(pos + word.length() + 1))) {
            return true;
        }

        return false;
    }

    private ArrayList<String> getWordSet(int difficulty) {
        switch (difficulty) {
            case DIFF_VERY_EASY:
                return veryEasyWords;
            case DIFF_EASY:
                return easyWords;
            case DIFF_AVERAGE:
                return averageWords;
            case DIFF_HARD:
                return hardWords;
            case DIFF_VERY_HARD:
                return veryHardWords;
            default:
                return null;
        }
    }

    private int getWordCount(int difficulty) {
        switch (difficulty) {
            case DIFF_VERY_EASY:
                return 8;
            case DIFF_EASY:
                return 10;
            case DIFF_AVERAGE:
                return 12;
            case DIFF_HARD:
                return 10;
            case DIFF_VERY_HARD:
                return 6;
            default:
                return -1;
        }
    }

    private void randomizeAllText() {
        allText = "";
        for (int i = 0; i < numChars; i++) {
            allText += randomChars[(int)(Math.random()*randomChars.length)];
        }
    }

    private void loadWordsFromFile(String path) {
        try {
            Scanner scan = new Scanner(new File(path));

            while (scan.hasNextLine()) {
                String[] words = scan.nextLine().split(" ");
                for (int i = 0; i < words.length; i++) {
                    if (words[i].length() == 4) {
                        veryEasyWords.add(words[i]);
                    } else if (words[i].length() == 6) {
                        easyWords.add(words[i]);
                    } else if (words[i].length() == 8) {
                        averageWords.add(words[i]);
                    } else if (words[i].length() == 10) {
                        hardWords.add(words[i]);
                    } else if (words[i].length() == 12) {
                        veryHardWords.add(words[i]);
                    }
                }
            }

            scan.close();
        } catch (IOException ex) {
            Gdx.app.error("File Loading", "(" + TerminalMain.getRunTime() + ") Failed to load words from \"" + path + "\"");
        }
    }

}
