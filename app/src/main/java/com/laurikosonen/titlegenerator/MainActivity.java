package com.laurikosonen.titlegenerator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.EditText;

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int maxDisplayedTitles = 10;
    private final int maxTitleWords = 5;

    private List<TextView> titleSlots;
    private List<TextView> titleNumberSlots;
    private TextView displayedCategoryText;
    private MenuItem currentDisplayedCatItem;
    private MenuItem titleCountMenuItem;
    private MenuItem titleWordCountMenuItem;
    private ToggleButton customTemplateToggle;
    private EditText customTemplateInput;
    private MenuItem titleDecorationsToggle;
    private MenuItem randomTitleLengthToggle;

    private int[] categoryItemIds = {
        R.id.action_displayCategory1,
        R.id.action_displayCategory2,
        R.id.action_displayCategory3,
        R.id.action_displayCategory4,
        R.id.action_displayCategory5,
        R.id.action_displayCategory6,
        R.id.action_displayCategory7,
        R.id.action_displayCategory8
    };

    private List<List<Word>> wordLists;
    private List<Word> allWords;
    private List<Category> categories;

    private int displayedCategory = -1;
    private int displayedTitleCount = 10;
    private int titleWordCount = 2;
    private boolean enableTitleDecorations = true;
    private boolean enableRandomTitleLength = false;

    // Custom template
    private String customTemplate;
    private char templateWordChar;
    private int mutatorBlockLength;
    private boolean skipSpace;
    private boolean lastCharWasTemplateWordChar = false;
    private boolean enableCustomTemplate = false;
    private Word lastWord;
    private String[] lastWordMutators;
    private int lastWordCategory;

    private enum TitleDecoration {
        X_Y,
        X_colon_Y,
        X_colon_theY,
        XofY,
        XoftheY,
        XandY,
        XandtheY,
        XsY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initWords();
        initDisplayedCategoryText();
        initTitleSlots();
        initCustomTemplate();

        generateTitles();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateTitles();
            }
        });

        customTemplateToggle = (ToggleButton) findViewById(R.id.toggle_customTemplate);
        customTemplateToggle.setChecked(enableCustomTemplate);
        customTemplateToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateCustomTemplate(customTemplateToggle.isChecked());
            }
        });
    }

    private void initWords() {
        wordLists = new ArrayList<>();
        allWords = new ArrayList<>();
        categories = new ArrayList<>();
        categories.add(new Category(getString(R.string.category_all), getString(R.string.function_catAny), -1));
        CustomXmlResourceParser.parseWords(getResources(), R.xml.title_words, wordLists, allWords, categories);
        templateWordChar = getString(R.string.function_word).charAt(0);
    }

    private void initDisplayedCategoryText() {
        displayedCategoryText = (TextView) findViewById(R.id.displayedCategory);
        updateDisplayedCategoryText();
    }

    private void updateDisplayedCategoryText() {
        Category category = categories.get(displayedCategory + 1);

        String text = "";
        if (category.type == Category.Type.all)
            text = getString(R.string.displayingAllCategories);
        else
            text = String.format(getString(R.string.displayingCategory),
                displayedCategory + 1, category.name, category.shortName);

        displayedCategoryText.setText(text);
    }

    private void initTitleSlots() {
        titleSlots = new ArrayList<>(10);
        titleSlots.add((TextView) findViewById(R.id.titleSlot01));
        titleSlots.add((TextView) findViewById(R.id.titleSlot02));
        titleSlots.add((TextView) findViewById(R.id.titleSlot03));
        titleSlots.add((TextView) findViewById(R.id.titleSlot04));
        titleSlots.add((TextView) findViewById(R.id.titleSlot05));
        titleSlots.add((TextView) findViewById(R.id.titleSlot06));
        titleSlots.add((TextView) findViewById(R.id.titleSlot07));
        titleSlots.add((TextView) findViewById(R.id.titleSlot08));
        titleSlots.add((TextView) findViewById(R.id.titleSlot09));
        titleSlots.add((TextView) findViewById(R.id.titleSlot10));

        titleNumberSlots = new ArrayList<>(10);
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber01));
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber02));
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber03));
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber04));
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber05));
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber06));
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber07));
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber08));
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber09));
        titleNumberSlots.add((TextView) findViewById(R.id.titleSlotNumber10));
    }

    private void initCustomTemplate() {
        setCustomTemplate(getString(R.string.customTemplateExample0), false);
        customTemplateInput = (EditText) findViewById(R.id.textInput_customTemplate);
        customTemplateInput.setVisibility(View.GONE);
    }

    private void activateCustomTemplate(boolean activate) {
        enableCustomTemplate = activate;
        customTemplateToggle.setChecked(enableCustomTemplate);
        customTemplateInput.setVisibility(enableCustomTemplate ? View.VISIBLE : View.GONE);
        if (enableCustomTemplate) {
            customTemplateInput.setText(customTemplate);
        }
    }

    private void generateTitles() {
        if (enableCustomTemplate) {
            customTemplate = customTemplateInput.getText().toString();
        }

        displayTitles();
    }

    private void displayTitles() {
        for (int i = 0; i < titleSlots.size(); i++) {
            skipSpace = false;

            boolean emptySlot = i >= displayedTitleCount;
            if (emptySlot) {
                titleSlots.get(i).setText("");
                titleNumberSlots.get(i).setText("");
                continue;
            }

            // Sets the number text before the title, e.g. "1) Title"
            String numberText = String.format(getString(R.string.titleNumber), i + 1);
            titleNumberSlots.get(i).setText(numberText);

            StringBuilder title = new StringBuilder();
            if (enableCustomTemplate) {
                createCustomTemplateTitle(title);
            }
            else {
                createTitle(title);
            }

            titleSlots.get(i).setText(title);
        }
    }

    private void createTitle(StringBuilder title) {
        int wordsPerTitle = titleWordCount;
        if (enableRandomTitleLength) {
            wordsPerTitle = (int) (Math.random() * titleWordCount) + 1;
        }

        if (enableTitleDecorations && Math.random() <= 0.3f) {
            title.append(getString(R.string.str_the)).append(' ');
        }

        // The index of the word which will get the only decoration of the title
        int formPlaceWordIndex = (int) (Math.random() * (wordsPerTitle - 1));

        for (int j = 0; j < wordsPerTitle; j++) {
            boolean isLastWord = j == wordsPerTitle - 1;
            boolean hasDecoration =
                enableTitleDecorations && wordsPerTitle > 1 && j == formPlaceWordIndex;

            appendStringToTitle(title, getRandomWord(displayedCategory).getRandomWordForm());

            if (hasDecoration) {
                applyTitleDecoration(getRandomTitleDecoration(), title);
            }
            else if (!isLastWord && !skipSpace) {
                applyTitleDecoration(TitleDecoration.X_Y, title);
            }
        }
    }

    private void replaceStringBuilderString(StringBuilder sb, String str) {
        sb.replace(0, sb.length(), str);
    }

    private void appendWordToTitle(StringBuilder title, Word word) {
        String wordStr = word.toString();
        if (word.getLastChar() == '-') {
            wordStr = wordStr.substring(0, wordStr.length() - 1);
        }

        title.append(wordStr);
    }

    private void appendStringToTitle(StringBuilder title, String str) {
        if (Word.getLastChar(str) == '-') {
            str = str.substring(0, str.length() - 1);
        }

        title.append(str);
    }

    private void appendCharToTitle(StringBuilder title, char c) {
        if (title.length() > 0 || c != ' ')
            title.append(c);
    }

    private void appendDecorationToTitle(StringBuilder title, String decoration) {
        title.append(decoration);
        skipSpace = false;
    }

    private void appendSpaceToTitleIfNotSkipped(StringBuilder title) {
        if (!skipSpace)
            title.append(" ");
        else
            skipSpace = false;
    }

    private Word getRandomWord(int wordCategoryId) {
        List<Word> wordList;
        if (wordCategoryId < 0)
            wordList = allWords;
        else
            wordList = wordLists.get(wordCategoryId);

        int wordIndex = (int) (Math.random() * wordList.size());
        Word word = wordList.get(wordIndex);
        lastWordCategory = word.category.id;

        if (word.getLastChar() == '-') {
            skipSpace = true;
        }

        if (enableCustomTemplate)
            lastWord = word;

        return word;
    }

    private TitleDecoration getRandomTitleDecoration() {
        double rand = Math.random();
        if (rand <= 0.12) {
            return TitleDecoration.X_colon_Y;
        }
        else if (rand <= 0.2) {
            return TitleDecoration.X_colon_theY;
        }
        else if (rand <= 0.3) {
            return TitleDecoration.XofY;
        }
        else if (rand <= 0.45) {
            return TitleDecoration.XoftheY;
        }
        else if (rand <= 0.55) {
            return TitleDecoration.XandY;
        }
        else if (rand <= 0.65) {
            return TitleDecoration.XandtheY;
        }
        else if (rand <= 0.7) {
            return TitleDecoration.XsY;
        }
        else {
            return TitleDecoration.X_Y;
        }
    }

    private void applyTitleDecoration(TitleDecoration decoration, StringBuilder title) {
        if (decoration == null) {
            appendSpaceToTitleIfNotSkipped(title);
            return;
        }

        switch (decoration) {
            case X_Y:
                appendSpaceToTitleIfNotSkipped(title);
                break;
            case X_colon_Y:
                appendDecorationToTitle(title, getString(R.string.form_X__Y));
                break;
            case X_colon_theY:
                appendDecorationToTitle(title, getString(R.string.form_X__The_Y));
                break;
            case XofY:
                appendDecorationToTitle(title, getString(R.string.form_X_of_Y));
                break;
            case XoftheY:
                appendDecorationToTitle(title, getString(R.string.form_X_of_the_Y));
                break;
            case XandY:
                appendDecorationToTitle(title, getString(R.string.form_X_and_Y));
                break;
            case XandtheY:
                appendDecorationToTitle(title, getString(R.string.form_X_and_the_Y));
                break;
            case XsY:
                appendDecorationToTitle(
                    title, Word.getPossessiveEnding(title.toString()) + " ");
                break;
            default:
                appendSpaceToTitleIfNotSkipped(title);
                break;
        }
    }

    private void createCustomTemplateTitle(StringBuilder title) {
        if (customTemplate.length() == 0) {
            title.append(getString(R.string.untitled));
            return;
        }

        lastWordCategory = -1;

        for (int i = 0; i < customTemplate.length(); i++) {
            if (mutatorBlockLength > 0) {
                mutatorBlockLength--;
                continue;
            }

            char currentChar = customTemplate.charAt(i);
            boolean isLastChar = i == customTemplate.length() - 1;

            char nextChar = '_';
            if (!isLastChar) {
                nextChar = customTemplate.charAt(i + 1);
            }

            boolean anyCharCanBeAppended =
                !skipSpace || currentChar != ' ' || nextChar != templateWordChar;

            // When a template word character is hit, a word isn't yet added to the title.
            // Instead, it is checked if the word has any mutators after it.
            // (Unless there are consecutive template word chars in which case a word is added
            // to the title.)
            if (currentChar == templateWordChar) {
                if (lastCharWasTemplateWordChar)
                    appendWordToTitle(title, getRandomWord(displayedCategory));

                skipSpace = false;
                lastCharWasTemplateWordChar = true;
            }
            // The character immediately following a word template char
            else if (lastCharWasTemplateWordChar) {
                appendWordWithMutatorsToTitle(title, i);

                // Evaluated again because the value of skipSpace may have changed
                anyCharCanBeAppended =
                    !skipSpace || currentChar != ' ' || nextChar != templateWordChar;

                if (mutatorBlockLength == 0 && anyCharCanBeAppended) {
                    appendCharToTitle(title, currentChar);
                    skipSpace = false;
                }

                lastCharWasTemplateWordChar = false;
            }
            else if (anyCharCanBeAppended) {
                appendCharToTitle(title, currentChar);
                skipSpace = false;
            }

            if (skipSpace && currentChar == ' ') {
                skipSpace = false;
            }

            if (isLastChar && lastCharWasTemplateWordChar) {
                Word word = getRandomWord(displayedCategory);
                appendWordToTitle(title, word);
                lastWordCategory = word.category.id;
            }
        }

        if (title.length() == 0)
            title.append(getString(R.string.untitled));

        lastWord = null;
        lastWordMutators = null;
        mutatorBlockLength = 0;
        lastCharWasTemplateWordChar = false;
        skipSpace = false;
    }

    private void appendWordWithMutatorsToTitle(StringBuilder title, int customTemplateIndex) {
        StringBuilder mutators = parseMutators(customTemplateIndex);
        String wordWithMutators;
        if (mutators == null)
            wordWithMutators = getRandomWord(displayedCategory).toString();
        else
            wordWithMutators = getWordWithAppliedMutators(mutators);

        if (wordWithMutators.length() == 0)
            skipSpace = true;
        else
            appendStringToTitle(title, wordWithMutators);
    }

    /* Parses the custom template for mutators when an opening paren is found after a templateWordChar.
     * Mutators: uppercase, lowercase, plural, possessive, noun, present participle, present tense, past tense,
     * initialism, comparative, superlative, 50 % chance for no word, same mutators as the last,
     * same non-category mutators, same category, same category options and mutator chaining.
     *
     * @param index  The index on the custom template.
     * @returns      A string array of mutators.
     */
    private StringBuilder parseMutators(int index) {
        StringBuilder mutators = new StringBuilder();
        boolean mutatorsActive = false;
        mutatorBlockLength = 0;

        for (int i = index; i < customTemplate.length(); i++) {

            // There is no mutator block
            if (i == index && customTemplate.charAt(i) != getString(R.string.function_mutatorBlockStart).charAt(0)) {
                return null;
            }
            // The start of the mutator block
            else if (i == index) {
                mutatorsActive = true;
            }
            // The end of the mutator block
            else if (customTemplate.charAt(i) == getString(R.string.function_mutatorBlockEnd).charAt(0)) {
                mutatorBlockLength = i - index;
                mutatorsActive = false;
                break;
            }
            // The mutators
            else {
                mutators.append(customTemplate.charAt(i));
            }
        }

        // The mutator block misses an end char and thus is considered null and void
        // or there are no mutators
        if (mutatorsActive || mutators.length() == 0)
            return null;

        return mutators;
    }

    private String getWordWithAppliedMutators(StringBuilder mutatorString) {
        String mutatorSeparator = getString(R.string.function_mutatorSeparator);
        String[] mutators = mutatorString.toString().
            split("[" + mutatorSeparator + "]");

        // General mutators
        boolean copyWord = false;
        boolean copyCategory = false;
        boolean copyNonCategoryMutators = false;
        boolean emptyResult = false;

        for (int i = 0; i < mutators.length; i++) {
            mutators[i] = mutators[i].trim();

            if (!emptyResult && lastWord != null) {
                if (mutators[i].equals(getString(R.string.function_copyWord))) {
                    copyWord = true;
                }
                else if (mutators[i].equals(getString(R.string.function_copyAllMutators))) {
                    copyCategory = true;
                    copyNonCategoryMutators = true;
                }
                else if (mutators[i].equals(getString(R.string.function_copyNonCatMutators))) {
                    copyNonCategoryMutators = true;
                }
                // Copy category mutator is checked in getCategoryFromMutators()
            }

            // 50 % chance for an empty result
            if (mutators[i].equals(getString(R.string.function_emptyChance1)) && Math.random() < 0.5) {
                emptyResult = true;
            }
        }

        if (emptyResult) {
            lastWordMutators = mutators;
            return "";
        }

        Word word;
        if (copyWord) {
            word = lastWord;
        }
        else {
            int category = copyCategory ? lastWordCategory : getCategoryFromMutators(mutators);
            word = getRandomWord(category);
        }

        if (copyNonCategoryMutators) {
            mutators = lastWordMutators;
        }

        StringBuilder result;
        if (word.isPlaceholder) {
            result = new StringBuilder(word.toString());
        }
        else {
            result = new StringBuilder(getWordFormFromMutators(word, mutators));
            applyVisualMutators(result, mutators);
        }

        lastWordMutators = mutators;
        return result.toString();
    }

    private int getCategoryFromMutators(String[] mutators) {
        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_copyCategory))) {
                return lastWordCategory;
            }

            for (Category category : categories) {
                if (mutator.equals("" + (category.id + 1)) || mutator.equals(category.shortName))
                    return category.id;
            }
        }

        return displayedCategory;
    }

    private String getWordFormFromMutators(Word word, String[] mutators) {
        StringBuilder result = new StringBuilder(word.toString());

        boolean usePossessive = false;
        boolean usePreposition = false;

        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_plural1)) || mutator.equals(getString(R.string.function_plural2))) {
                replaceStringBuilderString(result, word.getPlural());
            }
            else if (mutator.equals(getString(R.string.function_pluralChance))) {
                // 50 % chance for plural form
                if (Math.random() < 0.5) {
                    replaceStringBuilderString(result, word.getPlural());
                }
            }
            else if (mutator.equals(getString(R.string.function_noun))) {
                replaceStringBuilderString(result, word.getNoun());
            }
            else if (mutator.equals(getString(R.string.function_presentParticiple))) {
                replaceStringBuilderString(result, word.getPresentParticiple());
            }
            else if (mutator.equals(getString(R.string.function_presentTense))) {
                replaceStringBuilderString(result, word.getPresentTense());
            }
            else if (mutator.equals(getString(R.string.function_pastTense1)) || mutator.equals(getString(R.string.function_pastTense2))) {
                replaceStringBuilderString(result, word.getPastTense());
            }
            else if (mutator.equals(getString(R.string.function_pastPerfectTense))) {
                replaceStringBuilderString(result, word.getPastPerfectTense());
            }
            else if (mutator.equals(getString(R.string.function_actor))) {
                replaceStringBuilderString(result, word.getActor());
            }
            else if (mutator.equals(getString(R.string.function_comparative))) {
                replaceStringBuilderString(result, word.getComparative());
            }
            else if (mutator.equals(getString(R.string.function_superlative))) {
                replaceStringBuilderString(result, word.getSuperlative());
            }
            else if (mutator.equals(getString(R.string.function_manner))) {
                replaceStringBuilderString(result, word.getManner());
            }
            else if (mutator.equals(getString(R.string.function_possessive))) {
                usePossessive = true;
            }
            else if (mutator.equals(getString(R.string.function_preposition))) {
                usePreposition = true;
            }
        }

        if (usePossessive)
            replaceStringBuilderString(result, word.getPossessive(result.toString()));

        if (usePreposition) {
            String preposition = word.getRandomPreposition();
            if (preposition != null) {
                result.append(' ').append(preposition);
            }
        }

        return result.toString();
    }

    private void applyVisualMutators(StringBuilder sb, String[] mutators) {
        boolean useUppercase = false;
        boolean useLowercase = false;
        boolean useInitialism = false;
        boolean reverseWord = false;
        boolean jumbleWord = false;

        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_uppercase))) {
                useUppercase = true;
            }
            else if (mutator.equals(getString(R.string.function_lowercase))) {
                useLowercase = true;
            }
            else if (mutator.equals(getString(R.string.function_initialism))) {
                useInitialism = true;
            }
            else if (mutator.equals(getString(R.string.function_reverse))) {
                reverseWord = true;
            }
            else if (mutator.equals(getString(R.string.function_jumble1)) || mutator.equals(getString(R.string.function_jumble2))) {
                jumbleWord = true;
            }
        }

        if (reverseWord) {
            reverseString(sb);
            skipSpace = false;
        }
        if (jumbleWord) {
            jumbleString(sb);
            skipSpace = false;
        }
        if (useInitialism) {
            convertStringToInitialism(sb);
            useUppercase = true;
            skipSpace = false;
        }

        if (useLowercase)
            replaceStringBuilderString(sb, sb.toString().toLowerCase());
        else if (useUppercase)
            replaceStringBuilderString(sb, sb.toString().toUpperCase());
    }

    private void reverseString(StringBuilder sb) {
        String str = sb.toString().toLowerCase();
        sb.delete(0, sb.length());

        int startIndex = str.length() - (Word.getLastChar(str) == '-' ? 2 : 1);
        for (int i = startIndex; i >= 0; i--) {
            char c = str.charAt(i);
            if (i == startIndex)
                c = ("" + str.charAt(i)).toUpperCase().charAt(0);

            sb.append(c);
        }
    }

    private void jumbleString(StringBuilder sb) {
        if (sb.length() < 2)
            return;

        String str = sb.toString().toLowerCase();
        if (Word.getLastChar(str) == '-')
            str = str.substring(0, str.length() - 1);

        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            int i2 = (int) (Math.random() * chars.length);
            char temp = chars[i];
            chars[i] = chars[i2];
            chars[i2] = temp;
        }

        sb.delete(0, sb.length());
        sb.append(("" + chars[0]).toUpperCase().charAt(0));
        for (int i = 1; i < chars.length; i++) {
            sb.append(chars[i]);
        }
    }

    private void convertStringToInitialism(StringBuilder sb) {
        if (sb.length() > 1) {
            String str = sb.toString();
            sb.delete(0, sb.length());
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) != '-' && str.charAt(i) != ' ' && str.charAt(i) != '\'')
                    sb.append(str.charAt(i)).append('.');
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initMenu(menu);
        initCategoryMenu(menu);

        titleDecorationsToggle = menu.findItem(R.id.action_titleDecorations);
        titleDecorationsToggle.setChecked(enableTitleDecorations);

        randomTitleLengthToggle = menu.findItem(R.id.action_randomTitleLength);
        randomTitleLengthToggle.setChecked(enableRandomTitleLength);

        return true;
    }

    private void initMenu(Menu menu) {
        titleCountMenuItem = menu.findItem(R.id.submenu_titleCount);
        titleCountMenuItem.
            setTitle(String.format(getString(R.string.titleCount), displayedTitleCount));

        titleWordCountMenuItem = menu.findItem(R.id.submenu_titleWordCount);
        titleWordCountMenuItem.
            setTitle(String.format(getString(R.string.titleWordCount), titleWordCount));
    }

    private void initCategoryMenu(Menu menu) {
        currentDisplayedCatItem = menu.findItem(R.id.action_displayAll);
        currentDisplayedCatItem.setEnabled(false);

        for (int i = 0; i < categoryItemIds.length; i++) {
            menu.findItem(categoryItemIds[i]).setTitle(String.format(
                getString(R.string.action_displayCategory), categories.get(i + 1).name, categories.get(i + 1).shortName));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (handleSetTitleCount(id)) {
            return true;
        }
        else if (handleSetTitleWordCount(id)) {
            return true;
        }
        else if (handleDisplayedCategoryOptions(id, item)) {
            return true;
        }
        else if (handleTitleDecorationsActivation(id)) {
            return true;
        }
        else if (handleRandomTitleLengthActivation(id)) {
            return true;
        }
        else if (handleCustomTemplateExampleOptions(id)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean handleSetTitleCount(int id) {
        switch (id) {
            case R.id.action_setTitleCount_1: {
                return setDisplayedTitleCount(1, maxDisplayedTitles);
            }
            case R.id.action_setTitleCount_2: {
                return setDisplayedTitleCount(2, maxDisplayedTitles);
            }
            case R.id.action_setTitleCount_3: {
                return setDisplayedTitleCount(3, maxDisplayedTitles);
            }
            case R.id.action_setTitleCount_4: {
                return setDisplayedTitleCount(4, maxDisplayedTitles);
            }
            case R.id.action_setTitleCount_5: {
                return setDisplayedTitleCount(5, maxDisplayedTitles);
            }
            case R.id.action_setTitleCount_6: {
                return setDisplayedTitleCount(6, maxDisplayedTitles);
            }
            case R.id.action_setTitleCount_7: {
                return setDisplayedTitleCount(7, maxDisplayedTitles);
            }
            case R.id.action_setTitleCount_8: {
                return setDisplayedTitleCount(8, maxDisplayedTitles);
            }
            case R.id.action_setTitleCount_9: {
                return setDisplayedTitleCount(9, maxDisplayedTitles);
            }
            case R.id.action_setTitleCount_10: {
                return setDisplayedTitleCount(10, maxDisplayedTitles);
            }
        }

        return false;
    }

    private boolean handleSetTitleWordCount(int id) {
        switch (id) {
            case R.id.action_setTitleWordCount_1: {
                return setTitleWordCount(1, maxTitleWords);
            }
            case R.id.action_setTitleWordCount_2: {
                return setTitleWordCount(2, maxTitleWords);
            }
            case R.id.action_setTitleWordCount_3: {
                return setTitleWordCount(3, maxTitleWords);
            }
            case R.id.action_setTitleWordCount_4: {
                return setTitleWordCount(4, maxTitleWords);
            }
            case R.id.action_setTitleWordCount_5: {
                return setTitleWordCount(5, maxTitleWords);
            }
        }

        return false;
    }

    private boolean handleDisplayedCategoryOptions(int id, MenuItem item) {
        if (id == R.id.action_displayAll)
            return setDisplayedCategory(item, -1);

        for (int i = 0; i < categoryItemIds.length; i++) {
            if (id == categoryItemIds[i])
                return setDisplayedCategory(item, i);
        }

        return false;
    }

    private boolean handleCustomTemplateExampleOptions(int id) {
        switch (id) {
            case R.id.action_exampleTemplate1:
                return setCustomTemplate(getString(R.string.customTemplateExample1), true);
            case R.id.action_exampleTemplate2:
                return setCustomTemplate(getString(R.string.customTemplateExample2), true);
            case R.id.action_exampleTemplate3:
                return setCustomTemplate(getString(R.string.customTemplateExample3), true);
            case R.id.action_exampleTemplate4:
                return setCustomTemplate(getString(R.string.customTemplateExample4), true);
            case R.id.action_exampleTemplate5:
                return setCustomTemplate(getString(R.string.customTemplateExample5), true);
            case R.id.action_exampleTemplate6:
                return setCustomTemplate(getString(R.string.customTemplateExample6), true);
            case R.id.action_exampleTemplate7:
                return setCustomTemplate(getString(R.string.customTemplateExample7), true);
            case R.id.action_exampleTemplate8:
                return setCustomTemplate(getString(R.string.customTemplateExample8), true);
            case R.id.action_exampleTemplate9:
                return setCustomTemplate(getString(R.string.customTemplateExample9), true);
            case R.id.action_exampleTemplate10:
                return setCustomTemplate(getString(R.string.customTemplateExample10), true);
            case R.id.action_exampleTemplate11:
                return setCustomTemplate(getString(R.string.customTemplateExample11), true);
            case R.id.action_exampleTemplate12:
                return setCustomTemplate(getString(R.string.customTemplateExample12), true);
            case R.id.action_exampleTemplate13:
                return setCustomTemplate(getString(R.string.customTemplateExample13), true);
            case R.id.action_exampleTemplate14:
                return setCustomTemplate(getString(R.string.customTemplateExample14), true);
        }

        return false;
    }

    private boolean handleTitleDecorationsActivation(int id) {
        if (id == R.id.action_titleDecorations) {
            enableTitleDecorations = !enableTitleDecorations;
            titleDecorationsToggle.setChecked(enableTitleDecorations);

            if (!enableCustomTemplate)
                generateTitles();

            return true;
        }

        return false;
    }

    private boolean handleRandomTitleLengthActivation(int id) {
        if (id == R.id.action_randomTitleLength) {
            enableRandomTitleLength = !enableRandomTitleLength;
            randomTitleLengthToggle.setChecked(enableRandomTitleLength);

            if (!enableCustomTemplate)
                generateTitles();

            return true;
        }

        return false;
    }

    private boolean setDisplayedTitleCount(int value, int max) {
        if (value >= 1 && value <= max) {
            displayedTitleCount = value;
            titleCountMenuItem.
                setTitle(String.format(getString(R.string.titleCount), displayedTitleCount));

            generateTitles();

            return true;
        }

        return false;
    }

    private boolean setTitleWordCount(int value, int max) {
        if (value >= 1 && value <= max) {
            titleWordCount = value;
            titleWordCountMenuItem.
                setTitle(String.format(getString(R.string.titleWordCount), titleWordCount));

            if (!enableCustomTemplate)
                generateTitles();

            return true;
        }

        return false;
    }

    private boolean setDisplayedCategory(MenuItem item, int categoryId) {
        if (categoryId < wordLists.size()) {
            displayedCategory = categoryId;
            item.setEnabled(false);
            currentDisplayedCatItem.setEnabled(true);
            currentDisplayedCatItem = item;
            updateDisplayedCategoryText();

            generateTitles();

            return true;
        }

        return false;
    }

    private boolean setCustomTemplate(String template, boolean activateCustomTemplate) {
        if (template != null) {
            customTemplate = template;

            if (activateCustomTemplate) {
                if (!enableCustomTemplate) {
                    activateCustomTemplate(true);
                }
                else {
                    customTemplateInput.setText(customTemplate);
                }

                generateTitles();
            }

            return true;
        }

        return false;
    }
}
