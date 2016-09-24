package fot.terminal.hack;

import com.badlogic.gdx.Gdx;
import fot.terminal.TerminalMain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class HackData {
    public static final int lineLength = 12;
    public static final int linesPerCol = 18;
    public static final int columns = 2;

    public static final int numChars = lineLength * linesPerCol * columns;

    private int difficulty;
    private String solution;
    private boolean usedReplenish = false;
    private int attempts = 3;

    public static final int DIFF_VERY_EASY = 0;
    public static final int DIFF_EASY = 1;
    public static final int DIFF_AVERAGE = 2;
    public static final int DIFF_HARD = 3;
    public static final int DIFF_VERY_HARD = 4;

    private static final char[] randomChars = {'{', '}', '(', ')', '[', ']', '<', '>', '?', '-', '_', '\'', ';', '^', '*', '\\', '/', '!', '+', '#', '$', '%', '.', '|', ':', '"', '=', ','};
    private static final String bracketStarts = "([{<";
    private static final String bracketEnds = ")]}>";

    private final static ArrayList<String> easyWords = new ArrayList<String>();
    private final static ArrayList<String> veryEasyWords = new ArrayList<String>();
    private final static ArrayList<String> averageWords = new ArrayList<String>();
    private final static ArrayList<String> hardWords = new ArrayList<String>();
    private final static ArrayList<String> veryHardWords = new ArrayList<String>();

    private String allText;

    public HackData(int difficulty) {
        if (difficulty != DIFF_VERY_EASY && difficulty != DIFF_EASY && difficulty != DIFF_AVERAGE && difficulty != DIFF_HARD && difficulty != DIFF_VERY_HARD) {
            throw new HackDataException("Invalid difficulty enum: " + difficulty);
        }

        this.difficulty = difficulty;

        if (easyWords.isEmpty()) {
            loadWordsFromFile("words.txt");
        }

        generateAllText();
    }

    public String getAllText() {
        return allText;
    }

    public int getAttempts() {
        return attempts;
    }

    public void useAttempt() {
        attempts--;
    }

    public String getLine(int row, int col) {
        if (col < 0 || col >= columns) {
            throw new IndexOutOfBoundsException("Column " + col + " out of column bounds [0, " + (columns - 1) + "]");
        }
        if (row < 0 || row >= linesPerCol) {
            throw new IndexOutOfBoundsException("Row " + row + " out of row bounds [0, " + (linesPerCol - 1) + "]");
        }

        int rowIndex = row;
        if (col == 1) rowIndex += linesPerCol;

        int index = rowIndex * lineLength;

        return allText.substring(index, index + lineLength);
    }

    public boolean isBracketGroup(int charIndex) {
        if (!bracketStarts.contains(allText.charAt(charIndex) + "")) {
            return false;
        }

        int col = 0;
        if (charIndex >= lineLength * linesPerCol) col = 1;
        int row = (charIndex - col * linesPerCol * lineLength) / lineLength;
        String line = getLine(row, col).substring(charIndex % lineLength);

        return line.contains(bracketEnds.charAt(bracketStarts.indexOf(allText.charAt(charIndex))) + "");
    }

    public int getBracketGroupEnd(int groupIndex) {
        if (isBracketGroup(groupIndex)) {
            char toFind = bracketEnds.charAt(bracketStarts.indexOf(allText.charAt(groupIndex)));
            return allText.indexOf(toFind, groupIndex);
        } else {
            throw new HackDataException("Attempting to find bracket group end of non-bracket group [index=" + groupIndex + "]");
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    public boolean isWord(int charIndex) {
        return Character.isLetter(allText.charAt(charIndex));
    }

    public String getWord(int wordIndex) {
        if (!isWord(wordIndex)) {
            throw new HackDataException("Attempting to get non-word [index=" + wordIndex + "]");
        }

        int wordBegin = getWordStart(wordIndex);
        int wordEnd = getWordEnd(wordIndex);

        return allText.substring(wordBegin, wordEnd);
    }

    public int getWordStart(int wordIndex) {
        if (!isWord(wordIndex)) {
            throw new HackDataException("Index " + wordIndex + " is not a word");
        }

        while (wordIndex > 0 && Character.isLetter(allText.charAt(wordIndex-1))) {
            wordIndex--;
        }

        return wordIndex;
    }

    public int getWordEnd(int wordIndex) {
        if (!isWord(wordIndex)) {
            throw new HackDataException("Index " + wordIndex + " is not a word");
        }

        while (wordIndex < allText.length() && Character.isLetter(allText.charAt(wordIndex))) {
            wordIndex++;
        }

        return wordIndex;
    }

    public int getWordLength(int wordIndex) {
        return getWord(wordIndex).length();
    }

    public char getChar(int i) {
        if (i < 0 || i >= allText.length()) {
            throw new ArrayIndexOutOfBoundsException("Bracket group out of bounds: " + i);
        }

        return allText.charAt(i);
    }

    public String getBracketGroup(int i) {
        if (i < 0 || i >= allText.length()) {
            throw new ArrayIndexOutOfBoundsException("Bracket group out of bounds: " + i);
        } else if (!isBracketGroup(i)) {
            throw new HackDataException("Index " + i + " is not a bracket group");
        }

        return allText.substring(i, getBracketGroupEnd(i)+1);
    }

    public int length() {
        return allText.length();
    }

    public boolean isWordWrapped(int wordIndex) {
        if (!isWord(wordIndex)) {
            throw new HackDataException("Checking wrap of non-word [index=" + wordIndex + "]");
        }

        String word = getWord(wordIndex);
        int row = wordIndex / lineLength;
        int endRow = (wordIndex + word.length() - 1) / lineLength;

        return endRow > row;

    }

    public int getWordWrapSplitIndex(int wordIndex) {
        if (!isWordWrapped(wordIndex)) {
            throw new HackDataException("Attempting to get wrap split index of non-word [index=" + wordIndex + "]");
        }

        return wordIndex - wordIndex % lineLength + lineLength;
    }

    public String getExpandedLine(int row, int col) {
        return expandString(getLine(row, col));
    }

    public static String expandString(String str) {
        String work = "";

        for (int i = 0; i < str.length(); i++) {
            work += str.charAt(i) + " ";
        }

        return work;
    }

    public String getSolution() {
        return solution;
    }

    private void generateAllText() {
        randomizeJunkAllText();

        fillWordsInAllText();
    }

    private void fillWordsInAllText() {
        ArrayList<String> words = getWordSet(difficulty);
        int wordCount = getWordCount(difficulty);
        Random rand = new Random();
        String[] used = new String[wordCount];
        int usedIndex = 0;

        while (wordCount > 0) {
            String word = words.get(rand.nextInt(words.size()));

            if (!allText.contains(word)) {
                putWordInAllText(word);

                used[usedIndex] = word;
                usedIndex++;

                wordCount--;
            }
        }

        solution = used[rand.nextInt(used.length)];
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
        return pos + word.length() + 1 < allText.length() && Character.isLetter(allText.charAt(pos + word.length() + 1));

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

    private void randomizeJunkAllText() {
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
