package com.laurikosonen.titlegenerator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int maxDisplayedTitles = 10;
    private final int maxTitleWords = 5;

    private List<TextView> titleSlots;
    private TextView wordListSizeText;
    private MenuItem titleCountMenuItem;
    private MenuItem titleWordCountMenuItem;

    private List<List<Word>> wordLists;
    private List<Word> allWords;

    private int displayedCategory = -1;
    private int displayedTitleCount = 10;
    private int titleWordCount = 3;
    private boolean randomTitleLength = false;
    private boolean enableTitleForms = true;

    private enum TitleForm {
        X_Y,
        X_colon_Y,
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

        wordListSizeText = (TextView) findViewById(R.id.tipText);
        wordListSizeText.setText(String.format(
            getString(R.string.totalWordCount), allWords.size()));

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
    }

    private void displayTitles() {
        int wordsPerTitle = titleWordCount;

        for (int i = 0; i < titleSlots.size(); i++) {
            StringBuilder title = new StringBuilder();

            if (randomTitleLength) {
                wordsPerTitle = (int) (Math.random() * titleWordCount) + 1;
            }

            boolean emptySlot = i >= displayedTitleCount;
            if (!emptySlot) {
                TitleForm form = getRandomTitleForm();

                title.append(String.format(getString(R.string.titleNumber), i + 1));
                title.append(" ");

                if (enableTitleForms && Math.random() <= 0.3f) {
                    title.append("The ");
                }

                int formPlaceWordIndex = (int) (Math.random() * (wordsPerTitle - 1));

                for (int j = 0; j < wordsPerTitle; j++) {
                    //Word word = getRandomWord(-1);

                    title.append(getRandomWord(-1));
                    //title.append(" (" + word.categoryName + ")");

                    if (enableTitleForms && j == formPlaceWordIndex && wordsPerTitle > 1) {
                        applyTitleForm(form, title);
                    }
                    else if (j < wordsPerTitle - 1) {
                        applyTitleForm(null, title);
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

    private TitleForm getRandomTitleForm() {
        double rand = Math.random();
        if (rand <= 0.2) {
            return TitleForm.X_colon_Y;
        }
        else if (rand <= 0.3) {
            return TitleForm.XofY;
        }
        else if (rand <= 0.45) {
            return TitleForm.XoftheY;
        }
        else if (rand <= 0.55) {
            return TitleForm.XandY;
        }
        else if (rand <= 0.65) {
            return TitleForm.XandtheY;
        }
        else if (rand <= 0.7) {
            return TitleForm.XsY;
        }
        else {
            return TitleForm.X_Y;
        }
    }

    private void applyTitleForm(TitleForm form, StringBuilder title) {
        if (form == null) {
            title.append(" ");
            return;
        }

        switch (form) {
            case X_colon_Y:
                title.append(": ");
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
