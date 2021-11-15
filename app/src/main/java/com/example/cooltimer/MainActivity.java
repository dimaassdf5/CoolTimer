package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    private TextView time;
    private SeekBar seekBar;
    private int defaultTime = 60;
    private CountDownTimer timer;
    private Button st;
    private boolean isTimerOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        time = findViewById(R.id.time);
        st = findViewById(R.id.start_stop);

        seekBar.setMax(600); // Max - 10 minuts
        seekBar.setProgress(defaultTime);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                time.setText(transformTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    public void start(View view) {

        if(!isTimerOn) {
            st.setText("Stop");
            seekBar.setEnabled(false);
            isTimerOn = true;
            timer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
//                Log.d("Time tag: ", transformTime((int)(millisUntilFinished / 1000)));
                    time.setText(transformTime((int)(millisUntilFinished / 1000)));
                }

                @Override
                public void onFinish() {

                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); // Получаем доступ к файлу
                    if(sharedPreferences.getBoolean("enable_sound", true)) {

                        String melodyName = sharedPreferences.getString("timer_melody", "bell");

                        if(melodyName.equals("bell")) {
                            MediaPlayer  mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.bell_sound);
                            mediaPlayer.start();
                        } else if(melodyName.equals("alarm_siren")) {
                            MediaPlayer  mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.alarm_siren_sound);
                            mediaPlayer.start();
                        } else if(melodyName.equals("bip")) {
                            MediaPlayer  mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.bip_sound);
                            mediaPlayer.start();
                        }
                    }

                    resetTimer();
                }
            }.start();

        } else {
            resetTimer();
        }

    }

    private void resetTimer() {
        timer.cancel();
        time.setText(transformTime(defaultTime));
        st.setText("Start");
        seekBar.setEnabled(true);
        seekBar.setProgress(defaultTime);
        isTimerOn = false;
    }

    static private String transformTime(int progress) {
        int m = (progress / 60) % 60;
        int s = progress % 60;
        return String.format("%02d:%02d", m, s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Creating menu
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        } else if(id == R.id.action_about) {
            Intent openAbout = new Intent(this, AboutActivity.class);
            startActivity(openAbout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}