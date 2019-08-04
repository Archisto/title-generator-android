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

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int maxDisplayedTitles = 10;
    private final int maxTitleWords = 5;

    private List<TextView> titleSlots;
    private List<TextView> titleNumberSlots;
    private MenuItem titleCountMenuItem;
    private MenuItem titleWordCountMenuItem;
    private ToggleButton titleDecorationsToggle;

    private List<List<Word>> wordLists;
    private List<Word> allWords;

    private int displayedCategory = -1;
    private int displayedTitleCount = 10;
    private int titleWordCount = 3;
    private boolean randomTitleLength = false;
    private boolean enableTitleDecorations = true;

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

        for (TextView text : titleSlots) {
            text.setText(getString(R.string.category_feature));
        }

        displayTitles();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayTitles();
            }
        });

        titleDecorationsToggle = (ToggleButton) findViewById(R.id.toggle_titleDecorations);
        titleDecorationsToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTitleDecorations = titleDecorationsToggle.isChecked();
            }
        });
    }

    private void initWords() {
        wordLists = new ArrayList<>();
        allWords = new ArrayList<>();
        CustomXmlResourceParser.parseWords(getResources(), R.xml.title_words, wordLists, allWords);
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

    private void displayTitles() {
        int wordsPerTitle = titleWordCount;

        for (int i = 0; i < titleSlots.size(); i++) {
            StringBuilder title = new StringBuilder();

            if (randomTitleLength) {
                wordsPerTitle = (int) (Math.random() * titleWordCount) + 1;
            }

            boolean emptySlot = i >= displayedTitleCount;

            // Sets the number text before the title, e.g. "1) Title"
            String numberText = "";
            if (!emptySlot)
                numberText = String.format(getString(R.string.titleNumber), i + 1);
            titleNumberSlots.get(i).setText(numberText);

            // Creates the title
            if (!emptySlot) {
                TitleDecoration decoration = getRandomTitleDecoration();

                if (enableTitleDecorations && Math.random() <= 0.3f) {
                    title.append("The ");
                }

                int formPlaceWordIndex = (int) (Math.random() * (wordsPerTitle - 1));

                for (int j = 0; j < wordsPerTitle; j++) {
                    boolean isLastWord = j == wordsPerTitle - 1;

                    title.append(getRandomWord(-1));

                    if (enableTitleDecorations && j == formPlaceWordIndex && wordsPerTitle > 1) {
                        applyTitleDecoration(decoration, title);
                    }
                    else if (!isLastWord) {
                        applyTitleDecoration(TitleDecoration.X_Y, title);
                    }
                }
            }

            titleSlots.get(i).setText(title);
        }
    }

    private Word getRandomWord(int wordCategoryId) {
        if (wordCategoryId < 0) {
            wordCategoryId = (int) (Math.random() * wordLists.size());
        }

        List<Word> wordList = wordLists.get(wordCategoryId);
        int randWordIndex = (int) (Math.random() * wordList.size());
        return wordList.get(randWordIndex);
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
                title.append(": ");
                break;
            case X_colon_theY:
                title.append(": The ");
                break;
            case XofY:
                title.append(" of ");
                break;
            case XoftheY:
                title.append(" of the ");
                break;
            case XandY:
                title.append(" and ");
                break;
            case XandtheY:
                title.append(" and the ");
                break;
            case XsY:
                if (title.charAt(title.length() - 1) == 's'
                    || title.charAt(title.length() - 1) == 'z')
                    title.append("' ");
                else
                    title.append("'s ");
                break;
            default:
                title.append(" ");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initMenu(menu);
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

    private boolean setDisplayedTitleCount(int value, int max) {
        if (value >= 1 && value <= max) {
            displayedTitleCount = value;
            titleCountMenuItem.
                setTitle(String.format(getString(R.string.titleCount), displayedTitleCount));
            return true;
        }
        else {
            return false;
        }
    }

    private boolean setTitleWordCount(int value, int max) {
        if (value >= 1 && value <= max) {
            titleWordCount = value;
            titleWordCountMenuItem.
                setTitle(String.format(getString(R.string.titleWordCount), titleWordCount));
            return true;
        }
        else {
            return false;
        }
    }
}
