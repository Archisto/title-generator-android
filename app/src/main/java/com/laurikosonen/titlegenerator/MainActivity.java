package com.laurikosonen.titlegenerator;

import android.content.Intent;
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

    private List<List<Word>> wordLists;
    private List<Word> allWords;
    private List<Category> categories;

    private int displayedCategory = -1;
    private int displayedTitleCount = 10;
    private int titleWordCount = 3;
    private boolean enableTitleDecorations = true;
    private boolean enableRandomTitleLength = true;

    // Custom template
    private boolean enableCustomTemplate = false;
    private String customTemplate;
    private char templateWordChar;
    private int mutatorBlockLength;
    private boolean skipSpace;
    private boolean lastCharWasTemplateWordChar;
    private Word lastWord;
    private String[] lastWordMutators;
    private int lastWordCategory;

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

    private int[] titleCountOptionIds = {
        R.id.action_setTitleCount_1,
        R.id.action_setTitleCount_2,
        R.id.action_setTitleCount_3,
        R.id.action_setTitleCount_4,
        R.id.action_setTitleCount_5,
        R.id.action_setTitleCount_6,
        R.id.action_setTitleCount_7,
        R.id.action_setTitleCount_8,
        R.id.action_setTitleCount_9,
        R.id.action_setTitleCount_10
    };

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
        initCustomTemplateToggle();

        generateTitles();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateTitles();
            }
        });

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
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            customTemplate = extras.getString("customTemplate");

        customTemplateInput = (EditText) findViewById(R.id.textInput_customTemplate);
    }

    private void initCustomTemplateToggle() {
        customTemplateToggle = (ToggleButton) findViewById(R.id.toggle_customTemplate);
    }

    private void activateCustomTemplate(boolean activate) {
        enableCustomTemplate = activate;
        customTemplateToggle.setChecked(enableCustomTemplate);
        customTemplateInput.setVisibility(enableCustomTemplate ? View.VISIBLE : View.GONE);

        if (enableCustomTemplate) {
            customTemplateInput.setText(customTemplate);
            customTemplateInput.requestFocus();
        }
        else {
            customTemplate = customTemplateInput.getText().toString();
        }
    }

    private void goToHelp() {
        Intent i = new Intent(MainActivity.this, HelpActivity.class);
        i.putExtra("customTemplate", customTemplateInput.getText().toString());
        startActivity(i);
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

        boolean startsWithThe = false;
        if (enableTitleDecorations && Math.random() <= 0.3f) {
            title.append(getString(R.string.str_the)).append(' ');
            startsWithThe = true;
        }

        // The index of the word which will get the only decoration of the title
        int formPlaceWordIndex = (int) (Math.random() * (wordsPerTitle - 1));

        for (int j = 0; j < wordsPerTitle; j++) {
            boolean isLastWord = j == wordsPerTitle - 1;
            boolean hasDecoration =
                enableTitleDecorations && wordsPerTitle > 1 && j == formPlaceWordIndex;

            Word word = getRandomWord(displayedCategory, false, false);
            appendStringToTitle(title, word.getRandomWordForm());

            if (!isLastWord && !hasDecoration && word.usesPreposition())
                title.append(' ').append(word.getRandomPreposition(false));

            if (hasDecoration)
                applyTitleDecoration(getRandomTitleDecoration(), title, startsWithThe);
            else if (!isLastWord && !skipSpace)
                applyTitleDecoration(TitleDecoration.X_Y, title, startsWithThe);

            skipSpace = false;
        }
    }

    private void replaceStringBuilderString(StringBuilder sb, String str) {
        if (sb.length() == 0)
            sb.append(str);
        else
            sb.replace(0, sb.length(), str);
    }

    private void appendWordToTitle(StringBuilder title, Word word) {
        String wordStr = word.toString();
        if (word.getLastChar() == '-')
            wordStr = wordStr.substring(0, wordStr.length() - 1);

        title.append(wordStr);
    }

    private void appendStringToTitle(StringBuilder title, String str) {
        if (Word.getLastChar(str) == '-')
            str = str.substring(0, str.length() - 1);

        title.append(str);
    }

    private void appendCharToTitle(StringBuilder title, char c) {
        if (c != ' ' || (title.length() > 0 && title.charAt(title.length() - 1) != ' '))
            title.append(c);
    }

    private void appendSpaceToTitleIfNotSkipped(StringBuilder title) {
        if (!skipSpace)
            title.append(" ");
    }

    private Word getRandomWord(int wordCategoryId,
                               boolean startsWithVowel,
                               boolean startsWithConsonant) {
        List<Word> wordList;
        if (wordCategoryId < 0)
            wordList = allWords;
        else
            wordList = wordLists.get(wordCategoryId);

        int wordIndex = (int) (Math.random() * wordList.size());
        Word word;

        // Tries a set amount of times to get a word starting with either a vowel or a consonant
        if (startsWithVowel || startsWithConsonant) {
            int tries = 25;
            for (int i = 0; i < tries; i++) {
                if (startsWithVowel && wordList.get(wordIndex).startsWithVowel())
                    break;
                else if (startsWithConsonant && wordList.get(wordIndex).startsWithConsonant())
                    break;
                else
                    wordIndex = (int) (Math.random() * wordList.size());
            }
        }

        word = wordList.get(wordIndex);
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
        else if (rand <= 0.72) {
            return TitleDecoration.XsY;
        }
        else {
            return TitleDecoration.X_Y;
        }
    }

    private void applyTitleDecoration(TitleDecoration decoration,
                                      StringBuilder title,
                                      boolean startsWithThe) {
        if (decoration == null) {
            appendSpaceToTitleIfNotSkipped(title);
            return;
        }

        switch (decoration) {
            case X_Y:
                appendSpaceToTitleIfNotSkipped(title);
                break;
            case X_colon_Y:
                title.append(getString(R.string.form_X_colon_Y));
                break;
            case X_colon_theY:
                if (!startsWithThe)
                    title.append(getString(R.string.form_X_colon_The_Y));
                else
                    title.append(getString(R.string.form_X_colon_Y));
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
                title.append(Word.getPossessiveEnding(title.toString())).append(' ');
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
                    appendWordToTitle(title, getRandomWord(displayedCategory, false, false));

                lastCharWasTemplateWordChar = true;
            }
            // The character immediately following a word template char
            else if (lastCharWasTemplateWordChar) {
                appendWordWithMutatorsToTitle(title, i);

                // Evaluated again because the value of skipSpace may have changed
                anyCharCanBeAppended =
                    !skipSpace || currentChar != ' ' || nextChar != templateWordChar;

                if (mutatorBlockLength == 0 && anyCharCanBeAppended)
                    appendCharToTitle(title, currentChar);

                lastCharWasTemplateWordChar = false;
            }
            else if (anyCharCanBeAppended) {
                appendCharToTitle(title, currentChar);
            }

            if (isLastChar && lastCharWasTemplateWordChar) {
                Word word = getRandomWord(displayedCategory, false, false);
                appendWordToTitle(title, word);
                lastWordCategory = word.category.id;
            }

            if (mutatorBlockLength == 0)
                skipSpace = false;
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
        String[] mutators = parseMutators(customTemplateIndex);
        String wordWithMutators;
        if (mutators == null)
            wordWithMutators = getRandomWord(displayedCategory, false, false).toString();
        else
            wordWithMutators = getWordWithAppliedMutators(mutators);

        if (wordWithMutators.length() > 0)
            appendStringToTitle(title, wordWithMutators);
    }

    /* Parses the custom template for mutators when
     * an opening paren is found after a templateWordChar.
     *
     * @param index  The index on the custom template
     * @returns      A string array of mutators
     */
    private String[] parseMutators(int index) {
        StringBuilder mutatorSB = new StringBuilder();
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
                mutatorSB.append(customTemplate.charAt(i));
            }
        }

        // Splits the mutator string into an array and returns it
        // if the mutator block is completed and there are mutators
        if (!mutatorsActive && mutatorSB.length() > 0) {
            String mutatorSeparator = getString(R.string.function_mutatorSeparator);
            String[] mutators = mutatorSB.toString().split("[" + mutatorSeparator + "]");

            for (int i = 0; i < mutators.length; i++)
                mutators[i] = mutators[i].trim();

            return mutators;
        }
        else {
            return null;
        }
    }

    private String getWordWithAppliedMutators(String[] mutators) {
        boolean copyWord = false;
        boolean copyCategory = false;
        boolean copyNonCategoryMutators = false;
        boolean startsWithVowel = false;
        boolean startsWithConsonant = false;
        boolean emptyResult = false;

        for (String mutator : mutators) {
            if (!emptyResult) {
                if (mutator.equals(getString(R.string.function_copyWord))) {
                    copyWord = true;
                }
                else if (mutator.equals(getString(R.string.function_copyAllMutators))) {
                    copyCategory = true;
                    copyNonCategoryMutators = true;
                }
                else if (mutator.equals(getString(R.string.function_copyNonCatMutators))) {
                    copyNonCategoryMutators = true;
                }
                else if (mutator.equals(getString(R.string.function_startsWithVowel))) {
                    startsWithVowel = true;
                }
                else if (mutator.equals(getString(R.string.function_startsWithConsonant))) {
                    startsWithConsonant = true;
                }
                // Copy category mutator is checked in getCategoryIdFromMutators()
            }

            // 50 % chance for an empty result
            if (mutator.equals(getString(R.string.function_emptyChance)) && Math.random() < 0.5) {
                emptyResult = true;
            }
        }

        if (emptyResult) {
            lastWordMutators = mutators;
            return "";
        }

        Word word;
        if (copyWord && lastWord != null) {
            word = lastWord;
        }
        else {
            int category = copyCategory ? lastWordCategory : getCategoryIdFromMutators(mutators);
            word = getRandomWord(category, startsWithVowel, startsWithConsonant);
        }

        if (copyNonCategoryMutators && lastWordMutators != null) {
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

    private int getCategoryIdFromMutators(String[] mutators) {
        // TODO: Remove used mutators from the mutator list

        List<Category> categoryPossibilities = new ArrayList<>();

        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_copyCategory))) {
                return lastWordCategory;
            }

            for (Category category : categories) {
                if (mutator.equals("" + (category.id + 1)) || mutator.equals(category.shortName)) {
                    categoryPossibilities.add(category);
                    break;
                }
            }
        }

        int categoryId = displayedCategory;
        if (categoryPossibilities.size() == 1) {
            categoryId = categoryPossibilities.get(0).id;
        }
        else if (categoryPossibilities.size() > 1) {
            int index = (int) (Math.random() * categoryPossibilities.size());
            categoryId = categoryPossibilities.get(index).id;
        }

        return categoryId;
    }

    private String getWordFormFromMutators(Word word, String[] mutators) {
        StringBuilder result = new StringBuilder();

        boolean usePlural = false;
        boolean useNoun = false;
        boolean usePresentParticiple = false;
        boolean useActor = false;
        boolean usePossessive = false;
        boolean usePreposition = false;
        boolean useRandomForm = false;

        for (String mutator : mutators) {
            if (mutator.equals(getString(R.string.function_plural1)) || mutator.equals(getString(R.string.function_plural2))) {
                usePlural = true;
            }
            else if (mutator.equals(getString(R.string.function_pluralChance))) {
                // 50 % chance for plural form
                if (Math.random() < 0.5) {
                    usePlural = true;
                }
            }
            else if (mutator.equals(getString(R.string.function_noun))) {
                useNoun = true;
            }
            else if (mutator.equals(getString(R.string.function_presentParticiple))) {
                usePresentParticiple = true;
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
                useActor = true;
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
            else if (mutator.equals(getString(R.string.function_possessive1)) || mutator.equals(getString(R.string.function_possessive2))) {
                usePossessive = true;
            }
            else if (mutator.equals(getString(R.string.function_preposition))) {
                usePreposition = true;
            }
            else if (mutator.equals(getString(R.string.function_randomForm))) {
                useRandomForm = true;
            }
        }

        if (useRandomForm) {
            replaceStringBuilderString(result, word.getRandomWordForm());
        }
        else if (result.length() == 0) {
            if (usePlural && useNoun)
                result.append(word.getPluralNoun());
            else if (usePlural && usePresentParticiple)
                result.append(word.getPluralPresentParticiple());
            else if (usePlural && useActor)
                result.append(word.getPluralActor());
            else if (usePlural)
                result.append(word.getPlural());
            else if (useNoun)
                result.append(word.getNoun());
            else if (usePresentParticiple)
                result.append(word.getPresentParticiple());
            else if (useActor)
                result.append(word.getActor());
            else
                result.append(word.toString());
        }

        if (usePossessive)
            replaceStringBuilderString(result, word.getPossessive(result.toString()));

        if (usePreposition) {
            String preposition = word.getRandomPreposition(false);
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
        boolean removeLastVowels = false;
        boolean removeLastConsonants = false;
        int charsRemovedFromBeginning = 0;
        int charsRemovedFromEnd = 0;

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
            else if (mutator.equals(getString(R.string.function_jumble))) {
                jumbleWord = true;
            }
            else if (mutator.equals(getString(R.string.function_removeLastVowels))) {
                removeLastVowels = true;
            }
            else if (mutator.equals(getString(R.string.function_removeLastConsonants))) {
                removeLastConsonants = true;
            }
            else if (safeSubstring(mutator, 0, 2).equals(getString(R.string.function_removeCharsFromBeginning))) {
                charsRemovedFromBeginning = CustomXmlResourceParser.
                    parseInt(safeSubstring(mutator, 2, 3));
            }
            else if (safeSubstring(mutator, 0, 2).equals(getString(R.string.function_removeCharsFromEnd))) {
                charsRemovedFromEnd = CustomXmlResourceParser.
                    parseInt(safeSubstring(mutator, 2, 3));
            }
        }

        boolean removedVowels = false;
        if (removeLastVowels) {
            removedVowels = Word.trimEndVowels(sb);
            skipSpace = false;
        }
        if (removeLastConsonants) {
            boolean removedConsonants = Word.trimEndConsonants(sb);
            skipSpace = false;

            // Tries to remove last vowels again after consonants have been taken out of the way
            if (removeLastVowels && !removedVowels && removedConsonants) {
                Word.trimEndVowels(sb);
            }
        }

        if ((charsRemovedFromBeginning > 0 || charsRemovedFromEnd > 0) && sb.length() > 1) {
            if (sb.charAt(sb.length() - 1) == '-')
                sb.deleteCharAt(sb.length() - 1);

            if (charsRemovedFromBeginning > 0) {
                removeCharsFromBeginning(sb, charsRemovedFromBeginning);
            }
            if (charsRemovedFromEnd > 0) {
                removeCharsFromEnd(sb, charsRemovedFromEnd);
            }

            skipSpace = false;
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

    private void removeCharsFromBeginning(StringBuilder sb, int charsRemoved) {
        if (charsRemoved > sb.length() - 1)
            charsRemoved = sb.length() - 1;
        sb.delete(0, charsRemoved);
        sb.setCharAt(0, ("" + sb.charAt(0)).toUpperCase().charAt(0));
    }

    private void removeCharsFromEnd(StringBuilder sb, int charsRemoved) {
        if (charsRemoved > sb.length() - 1)
            charsRemoved = sb.length() - 1;
        sb.delete(sb.length() - charsRemoved, sb.length());
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

        int firstLetterIndex = chars[0] != ' ' && chars[0] != '\'' ? 0 : 1;
        sb.append(("" + chars[firstLetterIndex]).toUpperCase().charAt(0));

        for (int i = firstLetterIndex + 1; i < chars.length; i++) {
            if (chars[i] != ' ' && chars[i] != '\'')
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

    private String safeSubstring(String str, int beginIndex, int endIndex) {
        if (endIndex > str.length())
            endIndex = str.length();

        return str.substring(beginIndex, endIndex);
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

        if (handleHelpAction(id))
            return true;
        else if (handleTitleCountOptions(id))
            return true;
        else if (handleTitleWordCountOptions(id))
            return true;
        else if (handleDisplayedCategoryOptions(id, item))
            return true;
        else if (handleTitleDecorationsActivation(id))
            return true;
        else if (handleRandomTitleLengthActivation(id))
            return true;
        else if (handleCustomTemplateExampleOptions(id))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private boolean handleHelpAction(int id) {
        if (id == R.id.action_help) {
            goToHelp();
            return true;
        }

        return  false;
    }

    private boolean handleTitleCountOptions(int id) {
        for (int i = 0; i < titleCountOptionIds.length; i++) {
            if (id == titleCountOptionIds[i])
                return setDisplayedTitleCount(i + 1);
        }

        return false;
    }

    private boolean handleTitleWordCountOptions(int id) {
        switch (id) {
            case R.id.action_setTitleWordCount_1:
                return setTitleWordCount(1);
            case R.id.action_setTitleWordCount_2:
                return setTitleWordCount(2);
            case R.id.action_setTitleWordCount_3:
                return setTitleWordCount(3);
            case R.id.action_setTitleWordCount_4:
                return setTitleWordCount(4);
            case R.id.action_setTitleWordCount_5:
                return setTitleWordCount(5);
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
            case R.id.action_exampleTemplate15:
                return setCustomTemplate(getString(R.string.customTemplateExample15), true);
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

    private boolean setDisplayedTitleCount(int value) {
        if (value >= 1) {
            displayedTitleCount = value;
            titleCountMenuItem.
                setTitle(String.format(getString(R.string.titleCount), displayedTitleCount));

            generateTitles();

            return true;
        }

        return false;
    }

    private boolean setTitleWordCount(int value) {
        if (value >= 1) {
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
