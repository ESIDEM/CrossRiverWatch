package com.crossriverwatch.crossriverwatch;

import android.content.ContentResolver;
import android.content.Context;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.graphics.Palette;

import android.widget.ImageView;
import android.widget.TextView;

import com.crossriverwatch.crossriverwatch.database.NewsContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends AppCompatActivity {

    private  long id;
    private   String title;
    private TextView title_textView;
    private  TextView detailsView;
    private Context context;
    private String url;
    private String photoUrl;
    private ImageView detailImage;
    private String detailStr;


    private CollapsingToolbarLayout collapsingToolbarLayout;


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_news_detail);


        supportPostponeEnterTransition();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = this.getIntent().getExtras();

        id = bundle.getLong("rowId");
        getNewsDetail();
        title_textView = (TextView) findViewById(R.id.title_view);

        detailsView = (TextView) findViewById(R.id.description_view);

        detailImage = (ImageView) findViewById(R.id.image_view);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        Picasso.with(this).load(photoUrl).into(detailImage, new Callback() {
            @Override public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) detailImage.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });
            }

            @Override public void onError() {

            }
        });

        title_textView.setText(title);
        detailsView.setText(detailStr);
    }

    @Override public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        updateBackground((FloatingActionButton) findViewById(R.id.fab), palette);
        supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorAccent));

        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    private void getNewsDetail(){

        String[] projection = {
                NewsContract.Entry._ID,
                NewsContract.Entry.COLUMN_NAME_TITLE,
                NewsContract.Entry.COLUMN_NAME_LINK,
                NewsContract.Entry.COLUMN_NAME_IMAGE_URL,
                NewsContract.Entry.COLUMN_NAME_DESCRIPTION};

        Uri uri = Uri.parse(NewsContract.Entry.CONTENT_URI + "/" + id);
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String mTitle = cursor.getString(cursor.getColumnIndexOrThrow(NewsContract.Entry.COLUMN_NAME_TITLE));
            String mUrl = cursor.getString(cursor.getColumnIndexOrThrow(NewsContract.Entry.COLUMN_NAME_LINK));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(NewsContract.Entry.COLUMN_NAME_IMAGE_URL));
            String detals = cursor.getString(cursor.getColumnIndexOrThrow(NewsContract.Entry.COLUMN_NAME_DESCRIPTION));

            title = mTitle;
            url = mUrl;
            photoUrl = imageUrl;
            detailStr = detals;


        }


    }

}
