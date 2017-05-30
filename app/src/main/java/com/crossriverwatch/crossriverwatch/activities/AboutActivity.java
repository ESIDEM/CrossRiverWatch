package com.crossriverwatch.crossriverwatch.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.crossriverwatch.crossriverwatch.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }



    public void openStartHub(View view){

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.starthub.com.ng"));
        startActivity(browserIntent);

    }

    public void openCrossRiverWatch(View view){

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.crossriverwatch.com"));
        startActivity(browserIntent);

    }

    public void closeActivity(View view){

        finish();

    }
}
