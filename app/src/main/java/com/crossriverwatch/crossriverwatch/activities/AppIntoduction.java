package com.crossriverwatch.crossriverwatch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.crossriverwatch.crossriverwatch.R;

import com.crossriverwatch.crossriverwatch.fragments.Welcome;

import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by ESIDEM jnr on 4/14/2017.
 */

public class AppIntoduction extends AppIntro {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //adding the three slides for introduction app you can ad as many you needed
        addSlide(Welcome.newInstance(R.layout.welcome1));
        addSlide(Welcome.newInstance(R.layout.welcome2));
        addSlide(Welcome.newInstance(R.layout.welcome3));



        // Show and Hide Skip and Done buttons
        showStatusBar(false);
        showSkipButton(false);




        //Add animation to the intro slider
        setFlowAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {

        super.onSkipPressed(currentFragment);
        // Do something here when users click or tap on Skip button.
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }



    @Override
    public void onDonePressed(Fragment currentFragment) {

        super.onDonePressed(currentFragment);
        // Do something here when users click or tap tap on Done button.
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {

        super.onSlideChanged(oldFragment,newFragment);
        // Do something here when slide is changed
    }
}
