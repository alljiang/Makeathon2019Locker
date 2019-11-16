package com.example.makeathonlockerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Configuration extends AppCompatActivity {

    Button backBtn;
    Button syncBtn;
    EditText seedField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        backBtn = findViewById(R.id.backBtn);
        syncBtn = findViewById(R.id.syncBtn);
        seedField = findViewById(R.id.seedField);

        updateSeed();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonClicked();
            }
        });
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSync();
            }
        });
        seedField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() != 0) {
                    long seed = Long.parseLong(editable.toString());
                    if (seed > 60000) {
                        seed = 60000;
                        seedField.setText("60000");
                    } else if (seed < 0) {
                        seed = 0;
                        seedField.setText("0");
                    }
                    setSeed(seed);
                }
            }
        });
    }

    private void updateSeed() {
        SharedPreferences sharedPref =getSharedPreferences("Makeathon",MODE_PRIVATE);
        String defaultValue = "0";
        String seedStr = sharedPref.getString(getString(R.string.seed), defaultValue);
        seedField.setText(seedStr);
    }

    private void setSync() {
        SharedPreferences sharedPref =getSharedPreferences("Makeathon",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Long currentTimeSeconds = System.currentTimeMillis()/1000;
        editor.putString(getString(R.string.sync_pref), ""+currentTimeSeconds);
        editor.commit();
    }

    private void setSeed(long seed) {
        SharedPreferences sharedPref =getSharedPreferences("Makeathon",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.seed), ""+seed);
        editor.commit();
    }

    private void backButtonClicked() {
        super.onBackPressed();
    }
}
