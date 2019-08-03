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

    private List<TextView> titleSlots;
    private TextView wordListSizeText;

    private List<List<Word>> wordLists;
    private List<Word> allWords;

    private int displayedCategory = -1;
    private int displayedTitleCount = 10;
    private int titleWordCount = 3;
    private boolean randomTitleLength = true;

    private enum TitleForm {
        X_Y,
        X_colon_Y,
        XofY,
        XoftheY,
        XandY,
        XandtheY
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
        wordListSizeText.setText(String.format(getString(R.string.word_count), allWords.size()));

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
                title.append(String.format(getString(R.string.title_number), i + 1));
                title.append(" ");

                for (int j = 0; j < wordsPerTitle; j++) {
                    title.append(getRandomWord(-1));
                    title.append(" ");
                }
            }

            titleSlots.get(i).setText(title);
        }
    }

    private Word getRandomWord(int wordCategoryId) {
        double rand;

        if (wordCategoryId < 0) {
            rand = Math.random();
            wordCategoryId = (int) (rand * wordLists.size());
        }

        List<Word> wordList = wordLists.get(wordCategoryId);
        rand = Math.random();
        int randWordIndex = (int) (rand * wordList.size());
        return wordList.get(randWordIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
