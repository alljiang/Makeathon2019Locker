package com.example.makeathonlockerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    Button settingsBtn;
    Button refreshBtn;
    ProgressBar timerBar;
    TextView codeView;

    long sync = -1;
    int seed = -1;
    int code = -1;
    int lastCycle = -1;

    final double refreshTime_ms = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateSync();
        updateSeed();

        settingsBtn = findViewById(R.id.settingsBtn);
        refreshBtn = findViewById(R.id.refreshButton);
        timerBar = findViewById(R.id.timerBar);
        codeView = findViewById(R.id.codeView);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), Configuration.class);
                startActivity(myIntent);
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSync();
                updateSeed();
                lastCycle = -1;
            }
        });

        Runnable loop = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long timeDifference = (currentTime - sync*1000);
                int timeOffset = (int)(timeDifference % refreshTime_ms);
                if(timeOffset < 0) return;
                timerBar.setProgress((int)(timeOffset*1000/refreshTime_ms));

                int cycle = (int)(timeDifference / refreshTime_ms);
                if(lastCycle == -1) {
                    recalculateCode(cycle);
                }
                else if(cycle - lastCycle > 0){
                    getNextCode();
                }
                setCode(code);
                lastCycle = cycle;
            }
        };

        scheduler.scheduleAtFixedRate(loop, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void setCode(int i) {
        final int val = i;
        Thread t = new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String binVal = Integer.toBinaryString(val);
                        while(binVal.length() < 10) binVal = "0" + binVal;
                        codeView.setText("" + binVal);
                    }
                });
            }
        };
        t.start();
    }

    public void updateSync() {
        SharedPreferences sharedPref =getSharedPreferences("Makeathon",MODE_PRIVATE);
        String defaultValue = "1573875000";
        String syncTime = sharedPref.getString(getString(R.string.sync_pref), defaultValue);
        sync = Long.parseLong(syncTime);
        System.out.println(""+sync);
    }

    private void updateSeed() {
        SharedPreferences sharedPref =getSharedPreferences("Makeathon",MODE_PRIVATE);
        String defaultValue = "0";
        String seedStr = sharedPref.getString(getString(R.string.seed), defaultValue);
        seed = Integer.parseInt(seedStr);
    }

    private void recalculateCode(int cycle) {
        // Xn+1 = (aXn + c) mod m
        // Linear congruential generator
        // a = 3
        // X0 = seed
        // c = 2555
        // m = 1024, 10 bit

        int x = seed;
        int a = 3;
        int c = 2555;
        int m = 1024;
        for(int i = 0; i <= cycle; i++) {
            x = (a * x + c) % m;
        }
        code = x;
    }

    private void getNextCode() {
        int a = 3;
        int c = 2555;
        int m = 1024;

        code = (a * code + c) % m;
    }
}
