package com.laurikosonen.titlegenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class HelpActivity extends AppCompatActivity {
    String customTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateCustomTemplate();
    }

    private void goToMainActivity() {
        Intent i = new Intent(HelpActivity.this, MainActivity.class);
        i.putExtra("customTemplate", customTemplate);
        startActivity(i);
    }

    private void updateCustomTemplate() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            customTemplate = extras.getString("customTemplate");
    }
}
