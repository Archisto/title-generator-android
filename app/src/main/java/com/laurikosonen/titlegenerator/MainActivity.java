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
                activateCustomTemplate(customTemplateToggle.isChecked());
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
        setCustomTemplate(getString(R.string.customTemplateExample0), false);
        customTemplateInput = (EditText) findViewById(R.id.textInput_customTemplate);
        customTemplateInput.setVisibility(View.GONE);
    }

    private void activateCustomTemplate(boolean activate) {
        enableCustomTemplate = activate;
        customTemplateInput.setVisibility(enableCustomTemplate ? View.VISIBLE : View.GONE);
        if (enableCustomTemplate
            && (customTemplate == null
            || customTemplate.isEmpty()
            || customTemplateInput.getText().toString().isEmpty())) {
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

                title.append(getRandomWord(displayedCategory).getRandomWordForm());

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

            // TODO: Apply mutators to remove ending dash
            if (isLastChar && lastCharWasTemplateWordChar) {
                Word word = getRandomWord(displayedCategory);
                title.append(word);
                lastWordCategory = word.categoryId;
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
    * Mutators: uppercase, lowercase, plural, possessive, noun, infinitive, present tense, past tense,
    * initialism, comparative, superlative, 50 % chance for no word, same mutators as the last,
    * same non-category mutators, same category, same category options and mutator chaining.
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
        // No mutators in otherwise valid mutator block
        else if (mutators.length() == 0) {
            return getRandomWord(displayedCategory).toString();
        }

        return getWordWithAppliedMutators(mutators);
    }

    private String getWordWithAppliedMutators(StringBuilder mutatorString) {
        String mutatorSeparator = getString(R.string.function_mutatorSeparator);
        String[] mutators = mutatorString.toString().
            split("[" + mutatorSeparator + "]");

        // General mutators
        boolean copyCategory = false;
        boolean copyNonCategoryMutators = false;
        boolean optionalWord = false;

        for (int i = 0; i < mutators.length; i++) {
            mutators[i] = mutators[i].trim();

            if (lastWordMutators != null) {
                if (mutators[i].equals(getString(R.string.function_copyAllMutators))) {
                    mutators = lastWordMutators;
                    copyCategory = true;
                    break;
                }
                else if (mutators[i].equals(getString(R.string.function_copyNonCatMutators))) {
                    copyNonCategoryMutators = true;
                }
                // Copy category mutator is checked in getCategoryFromMutators()
            }

            if (mutators[i].equals(getString(R.string.function_emptyChance1))) {
                optionalWord = true;
            }
        }

        // 50 % chance for an empty result
        if (optionalWord && Math.random() < 0.5) {
            return "";
        }

        int category = copyCategory ? lastWordCategory : getCategoryFromMutators(mutators);

        if (copyNonCategoryMutators) {
            mutators = lastWordMutators;
        }

        Word word = getRandomWord(category);
        StringBuilder result = new StringBuilder(getWordFormFromMutators(word, mutators));
        applyVisualMutators(result, mutators);

        lastWordMutators = mutators;
        return result.toString();
    }

    private void replaceStringBuilderString(StringBuilder sb, String str) {
        sb.replace(0, sb.length(), str);
    }

    private int getCategoryFromMutators(String[] mutators) {
        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_copyCategory))) {
                return lastWordCategory;
            } else if (mutator.equals(getString(R.string.function_catAny1)) || mutator.equals(getString(R.string.function_catAny2))) {
                return -1;
            } else if (mutator.equals(getString(R.string.number_1)) || mutator.equals(getString(R.string.category_kind_short))) {
                return 0;
            } else if (mutator.equals(getString(R.string.number_2)) || mutator.equals(getString(R.string.category_concept_short))) {
                return 1;
            } else if (mutator.equals(getString(R.string.number_3)) || mutator.equals(getString(R.string.category_substance_short))) {
                return 2;
            } else if (mutator.equals(getString(R.string.number_4)) || mutator.equals(getString(R.string.category_thing_short))) {
                return 3;
            } else if (mutator.equals(getString(R.string.number_5)) || mutator.equals(getString(R.string.category_personAndCreature_short))) {
                return 4;
            } else if (mutator.equals(getString(R.string.number_6)) || mutator.equals(getString(R.string.category_action_short))) {
                return 5;
            } else if (mutator.equals(getString(R.string.number_7)) || mutator.equals(getString(R.string.category_placeAndTime_short))) {
                return 6;
            }
        }

        return displayedCategory;
    }

    private String getWordFormFromMutators(Word word, String[] mutators) {
        StringBuilder result = new StringBuilder(word.toString());

        boolean possessive = false;
        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_plural1)) || mutator.equals(getString(R.string.function_plural2))) {
                replaceStringBuilderString(result, word.getPlural());
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
                possessive = true;
            }
        }

        if (possessive) {
            replaceStringBuilderString(result, word.getPossessive(result.toString()));
        }

        return result.toString();
    }

    private void applyVisualMutators(StringBuilder sb, String[] mutators) {
        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_uppercase))) {
                replaceStringBuilderString(sb, sb.toString().toUpperCase());
            }
            else if (mutator.equals(getString(R.string.function_lowercase))) {
                replaceStringBuilderString(sb, sb.toString().toLowerCase());
            }
            else if (mutator.equals(getString(R.string.function_initialism))) {
                String str = sb.toString();
                sb.delete(0, sb.length());
                for (int i = 0; i < str.length(); i++) {
                    sb.append(str.charAt(i)).append('.');
                }
                sb.append(sb.toString().toUpperCase());
            }
        }
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
            getString(R.string.action_displayCategory), getString(R.string.category_kind), getString(R.string.category_kind_short)));
        menu.findItem(R.id.action_displayCategory1).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_concept), getString(R.string.category_concept_short)));
        menu.findItem(R.id.action_displayCategory2).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_substance), getString(R.string.category_substance_short)));
        menu.findItem(R.id.action_displayCategory3).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_thing), getString(R.string.category_thing_short)));
        menu.findItem(R.id.action_displayCategory4).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_personAndCreature), getString(R.string.category_personAndCreature_short)));
        menu.findItem(R.id.action_displayCategory5).setTitle(String.format(
            getString(R.string.action_displayCategory), getString(R.string.category_action), getString(R.string.category_action_short)));
        menu.findItem(R.id.action_displayCategory6).setTitle(String.format(
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
            case R.id.action_displayCategory6: {
                return setDisplayedCategory(item, 6);
            }
        }

        return false;
    }

    private boolean handleCustomTemplateExampleOptions(int id) {
        switch (id) {
            case R.id.action_exampleTemplate1: {
                return setCustomTemplate(getString(R.string.customTemplateExample1), true);
            }
            case R.id.action_exampleTemplate2: {
                return setCustomTemplate(getString(R.string.customTemplateExample2), true);
            }
            case R.id.action_exampleTemplate3: {
                return setCustomTemplate(getString(R.string.customTemplateExample3), true);
            }
            case R.id.action_exampleTemplate4: {
                return setCustomTemplate(getString(R.string.customTemplateExample4), true);
            }
            case R.id.action_exampleTemplate5: {
                return setCustomTemplate(getString(R.string.customTemplateExample5), true);
            }
            case R.id.action_exampleTemplate6: {
                return setCustomTemplate(getString(R.string.customTemplateExample6), true);
            }
            case R.id.action_exampleTemplate7: {
                return setCustomTemplate(getString(R.string.customTemplateExample7), true);
            }
            case R.id.action_exampleTemplate8: {
                return setCustomTemplate(getString(R.string.customTemplateExample8), true);
            }
            case R.id.action_exampleTemplate9: {
                return setCustomTemplate(getString(R.string.customTemplateExample9), true);
            }
            case R.id.action_exampleTemplate10: {
                return setCustomTemplate(getString(R.string.customTemplateExample10), true);
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
