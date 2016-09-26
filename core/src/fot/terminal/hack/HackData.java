package fot.terminal.hack;

import com.badlogic.gdx.Gdx;
import fot.terminal.TerminalMain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 *
 * @author austinbt
 */
public class HackData {


    /**
     * Number of characters that are in each line in this data
     */
    public static final int LINE_LENGTH = 12;
    /**
     * Number of total lines in each column
     */
    public static final int LINES_PER_COL = 18;
    /**
     * Number of columns in this data
     */
    public static final int COLUMNS = 2;

    /**
     * Total number of characters contained in this data
     */
    public static final int NUM_CHARS = LINE_LENGTH * LINES_PER_COL * COLUMNS;

    //Difficulty enum --------------------------------------------------------------------------------------------------
    /**
     * Easiest difficulty
     */
    public static final int DIFF_VERY_EASY = 0;
    /**
     * Easy difficulty
     */
    public static final int DIFF_EASY = 1;
    /**
     * Average difficulty
     */
    public static final int DIFF_AVERAGE = 2;
    /**
     * Hard difficulty
     */
    public static final int DIFF_HARD = 3;
    /**
     * Hardest difficulty
     */
    public static final int DIFF_VERY_HARD = 4;
    //------------------------------------------------------------------------------------------------------------------

    //Random filler characters
    private static final char[] randomChars = {'{', '}', '(', ')', '[', ']', '<', '>', '?', '-', '_', '\'', ';', '^', '*', '\\', '/', '!', '+', '#', '$', '%', '.', '|', ':', '"', '=', ','};
    //Characters that can be bracket group beginners
    private static final String bracketStarts = "([{<";
    //Corresponding bracket group closers. Each type of bracket end must match the same index as its type in bracketStarts
    private static final String bracketEnds = ")]}>";

    //Wordsets ---------------------------------------------------------------------------------------------------------
    private final static ArrayList<String> easyWords = new ArrayList<String>();
    private final static ArrayList<String> veryEasyWords = new ArrayList<String>();
    private final static ArrayList<String> averageWords = new ArrayList<String>();
    private final static ArrayList<String> hardWords = new ArrayList<String>();
    private final static ArrayList<String> veryHardWords = new ArrayList<String>();
    //------------------------------------------------------------------------------------------------------------------

    //Maximum number of possible attempts
    private static final int MAX_ATTEMPTS = 3;


    //The difficulty of this hack
    private int difficulty;
    //The solution of this hack
    private String solution;
    //Whether or not a bracket group has replenished attempts allowance yet.
    //Allowance can only be replenished once per hack
    private boolean usedReplenish = false;
    //Password attempts remaining
    private int attempts = MAX_ATTEMPTS;

    //Words that are currently in this data
    private ArrayList<String> usedWords = new ArrayList<String>();
    //Bracket group indexes that have been used
    private ArrayList<Integer> usedBracketGroups = new ArrayList<Integer>();

    //All contents of this data
    private String allText;

    /**
     * Constructs a HackData object and generates the contents from the given difficulty enum
     *
     * @param difficulty Difficulty to construct data with
     * @throws HackDataException When difficulty is not a valid enum
     */
    public HackData(int difficulty) {
        if (difficulty != DIFF_VERY_EASY && difficulty != DIFF_EASY && difficulty != DIFF_AVERAGE && difficulty != DIFF_HARD && difficulty != DIFF_VERY_HARD) {
            throw new HackDataException("Invalid difficulty enum: " + difficulty);
        }

        this.difficulty = difficulty;

        //If wordsets haven't been loaded, load them
        if (easyWords.isEmpty()) {
            loadWordsFromFile("words.txt");
        }

        generateAllText();
    }

    /**
     * Finds the number of characters that are shared (position dependent) between both strings.
     *
     * @param str1 String 1 to compare
     * @param str2 String 2 to compare
     * @return Number of same characters shared between strings. 0 - str length
     */
    public static int getSimilarity(String str1, String str2) {
        if (str1.length() != str2.length()) {
            throw new HackDataException("Cannot get similarity of non-equal length strings: " + str1.length() + ", " + str2.length());
        }

        int similarity = 0;

        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) == str2.charAt(i)) {
                similarity++;
            }
        }

        return similarity;
    }

    /**
     * Replaces a random word that isn't the solution in this data with "." characters and marks the bracket group index as used.
     *
     * @param bracketGroupIndex Index of an existing, unused bracket group within this data
     */
    public void removeDud(int bracketGroupIndex) {
        if (usedWords.size() <= 1) {
            return;
        }

        //Find random word to remove
        String toRemove = null;
        while (toRemove == null || solution.equals(toRemove)) toRemove = usedWords.get((int)(Math.random()*usedWords.size()));

        //Create replacement string of toRemove.length() dots
        String replacement = "";
        for (int i = 0; i < toRemove.length(); i++) {
            replacement += '.';
        }

        //Clean up used word list and used bracket group list
        usedWords.remove(toRemove);
        usedBracketGroups.add(bracketGroupIndex);

        //Replace word with replacement dots
        allText = allText.replace(toRemove, replacement);
    }

    /**
     * Allowance replenishment can only be used once per data
     *
     * @return True if the single allowance replenish available to the data has been used.
     */
    public boolean allowanceReplenishUsed() {
        return usedReplenish;
    }

    /**
     * Replenishes the attempt allowance for this data and marks the allowance replenishment as used.
     *
     * @return True if allowance was replenished, false if it has already been replenished before.
     */
    public boolean replenishAllowance() {
        if (!allowanceReplenishUsed()) {
            usedReplenish = true;

            attempts = MAX_ATTEMPTS;

            return true;
        }

        return false;
    }

    /**
     * Gets the String representing all the contents of this data
     *
     * @return The String of all contents of this data of length NUM_CHARS.
     */
    public String getAllText() {
        return allText;
    }

    /**
     *
     * @return The number of password attempts remaining
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Consumes one attempt
     *
     * @throws HackDataException When there are no remaining attempts
     */
    public void useAttempt() {
        if (attempts > 0) {
            attempts--;
        } else {
            throw new HackDataException("Trying to use an attempt when there are none remaining");
        }
    }

    /**
     * Retrieves a given line of length LINE_LENGTH
     *
     * @param row The vertical row index of the line
     * @param col The column the line is in
     * @return The line at row, col of length LINE_LENGTH
     * @throws IndexOutOfBoundsException When (col <0 || col >=COLUMNS). Or, when (row < 0 || row >=LINES_PER_COL)
     */
    public String getLine(int row, int col) {
        if (col < 0 || col >= COLUMNS) {
            throw new IndexOutOfBoundsException("Column " + col + " out of column bounds [0, " + (COLUMNS - 1) + "]");
        }
        if (row < 0 || row >= LINES_PER_COL) {
            throw new IndexOutOfBoundsException("Row " + row + " out of row bounds [0, " + (LINES_PER_COL - 1) + "]");
        }

        int rowIndex = row;
        if (col == 1) rowIndex += LINES_PER_COL;

        int index = rowIndex * LINE_LENGTH;

        return allText.substring(index, index + LINE_LENGTH);
    }

    /**
     * Checks a given index and determines whether or not it is a valid, unused bracket group.
     *
     * Valid bracket groups begin with a valid bracket start char, and has a bracket end char of the same type in the same line.
     *
     * @param charIndex Index of the contents to be checked
     * @return True if it is a valid, unused bracket group, false otherwise.
     * @throws IndexOutOfBoundsException When charIndex < 0 || charIndex >= NUM_CHARS
     */
    public boolean isBracketGroup(int charIndex) {
        if (charIndex < 0 || charIndex >= length()) {
            throw new IndexOutOfBoundsException("Char index out of range: " + charIndex);
        }

        if (!bracketStarts.contains(allText.charAt(charIndex) + "")) {
            return false;
        }

        if (usedBracketGroups.contains(charIndex)) {
            return false;
        }

        int col = 0;
        if (charIndex >= LINE_LENGTH * LINES_PER_COL) col = 1;
        int row = (charIndex - col * LINES_PER_COL * LINE_LENGTH) / LINE_LENGTH;
        String line = getLine(row, col).substring(charIndex % LINE_LENGTH);

        return line.contains(bracketEnds.charAt(bracketStarts.indexOf(allText.charAt(charIndex))) + "");
    }

    /**
     * Finds the index of the end of the given bracket group.
     *
     * @param groupIndex Index of the group to find the end index of
     * @return The index of the end of the bracket group
     * @throws HackDataException When given index is not a bracket group
     */
    public int getBracketGroupEnd(int groupIndex) {
        if (isBracketGroup(groupIndex)) {
            char toFind = bracketEnds.charAt(bracketStarts.indexOf(allText.charAt(groupIndex)));
            return allText.indexOf(toFind, groupIndex);
        } else {
            throw new HackDataException("Attempting to find bracket group end of non-bracket group [index=" + groupIndex + "]");
        }
    }

    /**
     *
     * @return The difficulty enum of this hack
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Determines whether the given index references a word in the data.
     *
     * If the index is in the middle of the word this method considers it to be referencing the word.
     *
     * @param charIndex Index to test
     * @return True if the given index is part of a word
     * @throws HackDataException When charIndex < 0 || charIndex >= NUM_CHARS
     */
    public boolean isWord(int charIndex) {
        if (charIndex >= 0 && charIndex < length()) {
            return Character.isLetter(allText.charAt(charIndex));
        } else {
            throw new IndexOutOfBoundsException("Char index out of bounds: " + charIndex);
        }
    }

    /**
     * Retrieves a full word from the given index.
     *
     * If the index given is in the middle of the word, the whole word will still be retrieved.
     *
     * @param wordIndex
     * @return
     */
    public String getWord(int wordIndex) {
        if (!isWord(wordIndex)) {
            throw new HackDataException("Attempting to get non-word [index=" + wordIndex + "]");
        }

        int wordBegin = getWordStart(wordIndex);
        int wordEnd = getWordEnd(wordIndex);

        return allText.substring(wordBegin, wordEnd);
    }

    /**
     * Finds the index that represents the start of the word.
     *
     * @param wordIndex Any index that references a character in the word.
     * @return The starting index of the given word
     * @throws HackDataException When index is not a word
     * @throws IndexOutOfBoundsException When index < 0 || index >= NUM_CHARS
     */
    public int getWordStart(int wordIndex) {
        if (wordIndex < 0 || wordIndex >= length()) {
            throw new IndexOutOfBoundsException("Word index out of bounds: " + wordIndex);
        }
        if (!isWord(wordIndex)) {
            throw new HackDataException("Index " + wordIndex + " is not a word");
        }

        while (wordIndex > 0 && Character.isLetter(allText.charAt(wordIndex-1))) {
            wordIndex--;
        }

        return wordIndex;
    }

    /**
     * Finds the ending index of a word.
     *
     * @param wordIndex An index representing any character in a word
     * @return The end index of the word such that, if substring was used with getWordStart and getWordEnd, would get the entire word.
     * @throws HackDataException When index does not represent a word.
     * @throws IndexOutOfBoundsException When wordIndex < 0 || wordIndex >= NUM_CHARS
     */
    public int getWordEnd(int wordIndex) {
        if (wordIndex < 0 || wordIndex >= length()) {
            throw new IndexOutOfBoundsException("Word index out of bounds: " + wordIndex);
        }
        if (!isWord(wordIndex)) {
            throw new HackDataException("Index " + wordIndex + " is not a word");
        }

        while (wordIndex < allText.length() && Character.isLetter(allText.charAt(wordIndex))) {
            wordIndex++;
        }

        return wordIndex;
    }

    /**
     * Finds the length of the word at the given index
     *
     * @param wordIndex The index of the word to test length of. Can be any index within the word.
     * @return Length of the given word.
     * @throws HackDataException When index does not reference a word.
     * @throws IndexOutOfBoundsException When index < 0 || index >= NUM_CHARS
     */
    public int getWordLength(int wordIndex) {
        if (wordIndex < 0 || wordIndex >= length()) {
            throw new IndexOutOfBoundsException("Word index out of bounds: " + wordIndex);
        }
        if (!isWord(wordIndex)) {
            throw new HackDataException("Index " + wordIndex + " does not reference a word");
        }
        return getWord(wordIndex).length();
    }

    /**
     * Retrieves a character at a given index.
     *
     * @param i Index of character to retrieve.
     * @return The character at index i in the data.
     * @throws IndexOutOfBoundsException When index < 0 || index >= NUM_CHARS
     */
    public char getChar(int i) {
        if (i < 0 || i >= allText.length()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + i);
        }

        return allText.charAt(i);
    }

    /**
     * Retrieves the bracket group at the given index.
     *
     * @param i Index of bracket group
     * @return The entire bracket group
     * @throws IndexOutOfBoundsException When i < 0 || i >= NUM_CHARS
     */
    public String getBracketGroup(int i) {
        if (i < 0 || i >= length()) {
            throw new IndexOutOfBoundsException("Bracket group out of bounds: " + i);
        } else if (!isBracketGroup(i)) {
            throw new HackDataException("Index " + i + " is not a bracket group");
        }

        return allText.substring(i, getBracketGroupEnd(i)+1);
    }

    /**
     *
     * @return The number of chars in this data's contents
     */
    public int length() {
        return allText.length();
    }

    /**
     * Tests whether a word is placed at an index that would require it to be rendered as wrapped.
     *
     * @param wordIndex Index of the word
     * @return True if (word length + line index > LINE_LENGTH), false otherwise
     * @throws HackDataException When given index does not reference a word
     * @throws IndexOutOfBoundsException When index < 0 || index >= NUM_CHARS
     */
    public boolean isWordWrapped(int wordIndex) {
        if (!isWord(wordIndex)) {
            throw new HackDataException("Checking wrap of non-word [index=" + wordIndex + "]");
        }
        if (wordIndex < 0 || wordIndex >= length()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + wordIndex);
        }

        int wordStart = getWordStart(wordIndex);
        int wordLength = getWordEnd(wordIndex) - wordStart;

        return wordLength + wordStart%LINE_LENGTH > LINE_LENGTH;
    }

    /**
     * Finds the index that is the wrap index for the given word.
     *
     * @param wordIndex Word to find wrap index of
     * @return The index of the word wrap where the word is split and wraps to the next line.
     * @throws HackDataException When word does not wrap or does not exist.
     * @throws IndexOutOfBoundsException When index < 0 || index >= NUM_CHARS
     */
    public int getWordWrapSplitIndex(int wordIndex) {
        if (!isWordWrapped(wordIndex)) {
            throw new HackDataException("Attempting to get wrap split index of non-word [index=" + wordIndex + "]");
        }
        if (wordIndex < 0 || wordIndex >= length()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + wordIndex);
        }

        int wordStart = getWordStart(wordIndex);

        return wordStart + (LINE_LENGTH - wordStart%LINE_LENGTH);
    }

    /**
     * Retrieves a line using getLine(int, int) and space-expands it for rendering.
     *
     * Space expanding leaves a trailing space on the end of the string.
     *
     * @param row Vertical row of the line to be retrieved
     * @param col Column that the line is in
     * @return The space-expanded line with a trailing space.
     */
    public String getExpandedLine(int row, int col) {
        return expandString(getLine(row, col));
    }

    /**
     * Space-expands a string so that every character in the given string has a space between the next character.
     *
     * A trailing space is left on the result.
     *
     * @param str String to be space-expanded
     * @return A space expanded copy of the parameter with a trailing space.
     */
    public static String expandString(String str) {
        String work = "";

        for (int i = 0; i < str.length(); i++) {
            work += str.charAt(i) + " ";
        }

        return work;
    }

    /**
     *
     * @return The password solution to this hack
     */
    public String getSolution() {
        return solution;
    }

    /*
    THIS METHOD IS CALLED IN THE CONSTRUCTOR AND SHOULD NEVER BE CALLED TWICE

    Generates all of the contents for this hack based on the instance variable 'difficulty'
     */
    private void generateAllText() {
        randomizeJunkAllText();

        fillWordsInAllText();
    }

    /*
    Inserts words into allText based on the instance variable 'difficulty'

    Sets the solution
     */
    private void fillWordsInAllText() {
        //Get wordset for difficulty
        ArrayList<String> words = getWordSet(difficulty);
        //Get number of words for the hack for difficulty
        int wordCount = getWordCount(difficulty);

        Random rand = new Random();

        //Loop until enough words have been inserted
        while (wordCount > 0) {
            //Get random word
            String word = words.get(rand.nextInt(words.size()));

            //Does allText already contain it?
            if (!allText.contains(word)) {
                //Put the word into allText
                putWordInAllText(word);

                //Add it to the used words list
                usedWords.add(word);

                //Decrement counter
                wordCount--;
            }
        }

        //Set random solution from used words list
        solution = usedWords.get(rand.nextInt(usedWords.size()));
    }

    /*
    Put a word into allText at a random index that doesn't overlap or connect with any other words
     */
    private void putWordInAllText(String word) {
        Random rand = new Random();

        while (true) {
            //New random pose to try
            int pos = rand.nextInt(NUM_CHARS - word.length());

            //If it fits, put it in and break
            if (!overlapsOtherWord(word, pos)) {
                insertWord(word, pos);
                break;
            }
        }
    }

    /*
    Directly insert a word into the given location. No checks are made to ensure it's safe.
     */
    private void insertWord(String word, int pos) {
        allText = allText.substring(0, pos) + word + allText.substring(pos + word.length());
    }

    /*
    Tests whether a word will overlap other words or be directly adjacent to them for the given index. No index checks are made.
     */
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

    /*
    Retrieves a wordset for a given difficulty
     */
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
                throw new HackDataException("No such wordSet with difficulty enum " + difficulty);
        }
    }

    /*
    Determines the number of words to be present in a hack with a given difficulty.

    -1 is returned if an invalid difficulty is given.
     */
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

    /*
    Constructs allText to be of proper length using random, non-letter characters.

    This includes bracket characters
     */
    private void randomizeJunkAllText() {
        allText = "";
        for (int i = 0; i < NUM_CHARS; i++) {
            allText += randomChars[(int)(Math.random()*randomChars.length)];
        }
    }

    /*
    Load wordsets from file
     */
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
            ex.printStackTrace();
        }
    }

}
