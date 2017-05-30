package com.crossriverwatch.crossriverwatch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crossriverwatch.crossriverwatch.R;
import com.crossriverwatch.crossriverwatch.services.SyncUtils;
import com.crossriverwatch.crossriverwatch.utility.AppController;

public class IntroActivity extends AppCompatActivity {

    public boolean isFirstStart;
    Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ((AppController) this.getApplication()).setBASE_URL("http://crossriverwatch.com/?json=get_posts&page=");
        SyncUtils.CreateSyncAccount(this);
        SyncUtils.TriggerRefresh();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Intro App Initialize SharedPreferences
                SharedPreferences getSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                //  Check either activity or app is open very first time or not and do action
                if (isFirstStart) {

                    //  Launch application introduction screen
                    Intent i = new Intent(IntroActivity.this, AppIntoduction.class);
                    startActivity(i);
                    finish();
                    SharedPreferences.Editor e = getSharedPreferences.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }else {

                    Intent i = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

                }
            }
        });
        t.start();

    }

}
