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
    private MenuItem currentDisplayedCatItem;
    private MenuItem titleCountMenuItem;
    private MenuItem titleWordCountMenuItem;
    private ToggleButton customTemplateToggle;
    private EditText customTemplateInput;
    private MenuItem titleDecorationsToggle;
    private MenuItem randomTitleLengthToggle;

    private List<List<Word>> wordLists;
    private List<Word> allWords;

    private int displayedCategory = -1;
    private int displayedTitleCount = 10;
    private int titleWordCount = 2;
    private boolean enableTitleDecorations = true;
    private boolean enableRandomTitleLength = false;

    // Custom template
    private String customTemplate;
    private String defaultTemplate;
    private char templateWordChar;
    private int mutatorBlockLength;
    private boolean skipSpace;
    private boolean lastCharWasTemplateWordChar = false;
    private boolean enableCustomTemplate = false;
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
                enableCustomTemplate = customTemplateToggle.isChecked();
                customTemplateInput.setVisibility(enableCustomTemplate ? View.VISIBLE : View.GONE);
                if (enableCustomTemplate
                    && (customTemplate == null
                    || customTemplate.isEmpty()
                    || customTemplateInput.getText().toString().isEmpty())) {
                    customTemplate = defaultTemplate;
                    customTemplateInput.setText(customTemplate);
                }
            }
        });
    }

    private void initWords() {
        wordLists = new ArrayList<>();
        allWords = new ArrayList<>();
        CustomXmlResourceParser.parseWords(getResources(), R.xml.title_words, wordLists, allWords);
        templateWordChar = getString(R.string.function_word).charAt(0);
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
        defaultTemplate = getString(R.string.customTemplateExample);
        customTemplateInput = (EditText) findViewById(R.id.textInput_customTemplate);
        customTemplateInput.setVisibility(View.GONE);
    }

    private void generateTitles() {
        if (enableCustomTemplate) {
            customTemplate = customTemplateInput.getText().toString();
        }

        displayTitles();
    }

    private void displayTitles() {
        int wordsPerTitle = titleWordCount;

        for (int i = 0; i < titleSlots.size(); i++) {
            // TODO: Skip space in non-custom template titles
            skipSpace = false;

            boolean emptySlot = i >= displayedTitleCount;
            if (emptySlot) {
                titleSlots.get(i).setText("");
                titleNumberSlots.get(i).setText("");
                continue;
            }

            StringBuilder title = new StringBuilder();

            // Sets the number text before the title, e.g. "1) Title"
            String numberText = String.format(getString(R.string.titleNumber), i + 1);
            titleNumberSlots.get(i).setText(numberText);

            // Custom template titles:
            if (enableCustomTemplate) {
                applyCustomTemplate(title);
                titleSlots.get(i).setText(title);
                continue;
            }

            // Non-custom template titles:

            if (enableRandomTitleLength) {
                wordsPerTitle = (int) (Math.random() * titleWordCount) + 1;
            }

            // Creates the title:

            if (enableTitleDecorations && Math.random() <= 0.3f) {
                title.append(getString(R.string.str_the)).append(' ');
            }

            int formPlaceWordIndex = (int) (Math.random() * (wordsPerTitle - 1));

            for (int j = 0; j < wordsPerTitle; j++) {
                boolean isLastWord = j == wordsPerTitle - 1;

                title.append(getRandomWord(displayedCategory));

                if (enableTitleDecorations && j == formPlaceWordIndex && wordsPerTitle > 1) {
                    applyTitleDecoration(getRandomTitleDecoration(), title);
                }
                else if (!isLastWord) {
                    applyTitleDecoration(TitleDecoration.X_Y, title);
                }
            }

            titleSlots.get(i).setText(title);
        }
    }

    private void applyCustomTemplate(StringBuilder title) {
        lastWordCategory = -1;

        for (int i = 0; i < customTemplate.length(); i++) {
            if (mutatorBlockLength > 0) {
                mutatorBlockLength--;
                continue;
            }

            char currentChar = customTemplate.charAt(i);
            boolean isLastChar = i == customTemplate.length() - 1;

            // When a template word character is hit, a word isn't yet added to the title.
            // Instead, it is checked if the word has any mutators after it.
            // (Unless there are consecutive template word chars in which case a word is added
            // to the title.)
            if (currentChar == templateWordChar) {
                if (lastCharWasTemplateWordChar) {
                    title.append(getRandomWord(displayedCategory));
                }

                lastCharWasTemplateWordChar = true;
            }
            else if (lastCharWasTemplateWordChar) {
                appendWordWithMutatorsToTitle(title, i);
                lastCharWasTemplateWordChar = false;
            }
            else if (!skipSpace || currentChar != ' ') {
                title.append(currentChar);
            }

            if (skipSpace && currentChar == ' ') {
                skipSpace = false;
            }

            if (isLastChar && lastCharWasTemplateWordChar) {
                title.append(getRandomWord(displayedCategory));
                lastWordCategory = displayedCategory;
            }
        }

        lastCharWasTemplateWordChar = false;
        mutatorBlockLength = 0;
    }

    private Word getRandomWord(int wordCategoryId) {
        List<Word> wordList;
        if (wordCategoryId < 0)
            wordList = allWords;
        else
            wordList = wordLists.get(wordCategoryId);

        int wordIndex = (int) (Math.random() * wordList.size());
        Word word = wordList.get(wordIndex);
        lastWordCategory = word.categoryId;

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
            title.append(" ");
            return;
        }

        switch (decoration) {
            case X_Y:
                title.append(" ");
                break;
            case X_colon_Y:
                title.append(getString(R.string.form_X__Y));
                break;
            case X_colon_theY:
                title.append(getString(R.string.form_X__The_Y));
                break;
            case XofY:
                title.append(getString(R.string.form_X_of_Y));
                break;
            case XoftheY:
                title.append(getString(R.string.form_X_of_the_Y));
                break;
            case XandY:
                title.append(getString(R.string.form_X_and_Y));
                break;
            case XandtheY:
                title.append(getString(R.string.form_X_and_the_Y));
                break;
            case XsY:
                if (title.charAt(title.length() - 1) == 's'
                    || title.charAt(title.length() - 1) == 'z')
                    title.append(getString(R.string.form_Xs_Y2));
                else
                    title.append(getString(R.string.form_Xs_Y));
                break;
            default:
                title.append(" ");
                break;
        }
    }

    private void appendWordWithMutatorsToTitle(StringBuilder title, int customTemplateIndex) {
        // TODO: Better mutator parsing
//        StringBuilder mutators = parseMutators(customTemplateIndex);
//        title.append(getRandomStringWithAppliedMutators(mutators))
//            .append(mutatorBlockLength == 0 ? customTemplate.charAt(customTemplateIndex) : "");

        String wordWithMutators = getRandomWordAndParseMutators(customTemplateIndex);

        boolean appendWord = true;
        if (wordWithMutators.length() == 0) {
            appendWord = false;
            skipSpace = true;
        }
        else if (wordWithMutators.charAt(wordWithMutators.length() - 1) == '-') {
            // TODO: The last word shouldn't have a dash at the end
            wordWithMutators = wordWithMutators.substring(0, wordWithMutators.length() - 1);
            skipSpace = true;
        }

        if (appendWord) {
            title.append(wordWithMutators);
        }

        // The character immediately following the word template char is added to the title
        // if there's no mutator block and, if the char's a space, it's not skipped
        if (mutatorBlockLength == 0
              && (!skipSpace || customTemplate.charAt(customTemplateIndex) != ' ')) {
            title.append(customTemplate.charAt(customTemplateIndex));
        }
    }

//    private StringBuilder parseMutators(int customTemplateIndex) {
//
//    }
//
//    private String getRandomStringWithAppliedMutators(StringBuilder mutators) {
//
//    }

   /* Parses the custom template for mutators when an opening paren is found after a templateWordChar.
    * Mutators: singular, plural, nominative, possessive, infinitive, past tense, negative, actor, uppercase, lowercase,
    * initialism, 50 % chance for no word, same mutators as the last, same non-category mutators, same category,
    * same category options and mutator chaining.
    *
    * @param index  The index on the custom template.
    * @returns      A word string with applied mutators.
    */
    private String getRandomWordAndParseMutators(int index) {
        StringBuilder mutators = new StringBuilder();
        boolean mutatorsActive = false;
        for (int i = index; i < customTemplate.length(); i++) {

            // There is no mutator block; returns a random word
            if (i == index && customTemplate.charAt(i) != getString(R.string.function_mutatorBlockStart).charAt(0)) {
                mutatorBlockLength = 0;
                return getRandomWord(displayedCategory).toString();
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

        // The mutator block misses an end char and thus is considered null and void; returns a random word
        if (mutatorsActive) {
            mutatorBlockLength = 0;
            return getRandomWord(displayedCategory).toString();
        }

        return getWordWithAppliedMutators(mutators);
    }

    private String getWordWithAppliedMutators(StringBuilder mutatorString) {
        // TODO: Mutators

        String result = "";
        String[] mutators = mutatorString.toString().split("[|]"); // R.string.function_chainMutators

        // General mutators
        boolean copyNonCatMutators = false;
        if (lastWordMutators != null) {
            for (String mutator : mutators) {
                if (mutator.equals(getString(R.string.function_copyAllMutators))) {
                    mutators = lastWordMutators;
                    break;
                } else if (mutator.equals(getString(R.string.function_copyNonCatMutators))) {
                    copyNonCatMutators = true;
                }
            }
        }

        // Category mutators
        int category = displayedCategory;
        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_copyCategory))) {
                category = lastWordCategory;
                break;
            } else if (mutator.equals(getString(R.string.function_catAny1)) || mutator.equals(getString(R.string.function_catAny2))) {
                category = -1;
                break;
            } else if (mutator.equals(getString(R.string.function_catFeature1)) || mutator.equals(getString(R.string.function_catFeature2))) {
                category = 0;
                break;
            } else if (mutator.equals(getString(R.string.function_catConcept1)) || mutator.equals(getString(R.string.function_catConcept2))) {
                category = 1;
                break;
            } else if (mutator.equals(getString(R.string.function_catThing1)) || mutator.equals(getString(R.string.function_catThing2))) {
                category = 2;
                break;
            } else if (mutator.equals(getString(R.string.function_catPeopleAndCreatures1)) || mutator.equals(getString(R.string.function_catPeopleAndCreatures2))) {
                category = 3;
                break;
            } else if (mutator.equals(getString(R.string.function_catAction1)) || mutator.equals(getString(R.string.function_catAction2))) {
                category = 4;
                break;
            } else if (mutator.equals(getString(R.string.function_catPlaceAndTime1)) || mutator.equals(getString(R.string.function_catPlaceAndTime2))) {
                category = 5;
                break;
            }
        }

        Word word = getRandomWord(category);
        result = word.toString();

        if (copyNonCatMutators) {
            mutators = lastWordMutators;
        }

        // Postfix mutators
        StringBuilder newResult = new StringBuilder(result);
        char lastLetter = newResult.charAt(result.length() - 1);
        char secondToLastLetter = '_';
        if (result.length() >= 2) {
            secondToLastLetter = newResult.charAt(result.length() - 2);
        }

        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_singular))) {
                // TODO: Singular
            }
            else if (!word.isPlural && (mutator.equals(getString(R.string.function_plural1)) || mutator.equals(getString(R.string.function_plural2)))) {
                // TODO: Use the plural attribute
                if (lastLetter == 's' || lastLetter == 'x' || lastLetter == 'z'
                    || (secondToLastLetter == 'c' && lastLetter == 'h')
                    || (secondToLastLetter == 's' && lastLetter == 'h'))
                    newResult.append("es");
                else if (lastLetter == 'y')
                    newResult.replace(newResult.length() - 1, newResult.length(), "ies");
                else
                    newResult.append("s");
            }
            else if (mutator.equals(getString(R.string.function_possessive))) {
                if (lastLetter == 's' || lastLetter == 'z')
                    newResult.append("'");
                else
                    newResult.append("'s");
            }
        }

        result = newResult.toString();

        // Visual mutators
        boolean optionalWord = false;
        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_uppercase))) {
                result = result.toUpperCase();
            }
            else if (mutator.equals(getString(R.string.function_lowercase))) {
                result = result.toLowerCase();
            }
            else if (mutator.equals(getString(R.string.function_initialism))) {
                newResult = new StringBuilder();
                for (int i = 0; i < result.length(); i++) {
                    newResult.append(result.charAt(i)).append('.');
                }
                result = newResult.toString().toUpperCase();
            }
            else if (mutator.equals(getString(R.string.function_emptyChance1)) || mutator.length() == 0) {
                optionalWord = true;
            }
        }

        // 50 % chance for an empty result
        if (optionalWord && Math.random() < 0.5) {
            result = "";
        }

        lastWordMutators = mutators;
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initMenu(menu);
        initCategories(menu);

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

    private void initCategories(Menu menu) {
        currentDisplayedCatItem = menu.findItem(R.id.action_displayAll);
        currentDisplayedCatItem.setEnabled(false);

        menu.findItem(R.id.action_displayCategory0).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_feature),getString(R.string.category_feature_short)));
        menu.findItem(R.id.action_displayCategory1).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_concept), getString(R.string.category_concept_short)));
        menu.findItem(R.id.action_displayCategory2).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_thing), getString(R.string.category_thing_short)));
        menu.findItem(R.id.action_displayCategory3).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_peopleAndCreatures), getString(R.string.category_peopleAndCreatures_short)));
        menu.findItem(R.id.action_displayCategory4).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_action), getString(R.string.category_action_short)));
        menu.findItem(R.id.action_displayCategory5).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_placeAndTime), getString(R.string.category_placeAndTime_short)));
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
        switch (id) {
            case R.id.action_displayAll: {
                return setDisplayedCategory(item, -1);
            }
            case R.id.action_displayCategory0: {
                return setDisplayedCategory(item, 0);
            }
            case R.id.action_displayCategory1: {
                return setDisplayedCategory(item, 1);
            }
            case R.id.action_displayCategory2: {
                return setDisplayedCategory(item, 2);
            }
            case R.id.action_displayCategory3: {
                return setDisplayedCategory(item, 3);
            }
            case R.id.action_displayCategory4: {
                return setDisplayedCategory(item, 4);
            }
            case R.id.action_displayCategory5: {
                return setDisplayedCategory(item, 5);
            }
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

            generateTitles();

            return true;
        }

        return false;
    }
}
