package com.crossriverwatch.crossriverwatch.activities;

import android.content.ContentResolver;
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
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

import com.crossriverwatch.crossriverwatch.R;
import com.crossriverwatch.crossriverwatch.database.NewsContract;
import com.crossriverwatch.crossriverwatch.utility.ShareUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.R.attr.id;

public class CategoryNewsDetail extends AppCompatActivity {

    private String title;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton mFab;
    private WebView webView;
    private String dateNews;
    public String url;
    private String photoUrl;
    private ImageView detailImage;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_category_news_detail);

        supportPostponeEnterTransition();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = this.getIntent().getExtras();
        title = bundle.getString("title");
        url = bundle.getString("url");
        photoUrl = bundle.getString("imageUrl");
        content = bundle.getString("content");
        dateNews = bundle.getString("date");

        webView = (WebView) findViewById(R.id.webView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNews();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailImage = (ImageView) findViewById(R.id.image_view);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        if(photoUrl.isEmpty()){

            Picasso.with(this)
                    .load(R.drawable.cinema_new)
                    .placeholder(R.drawable.cinema_new)
                    .error(R.drawable.cinema_new)
                    .into(detailImage);



        }else {

            Picasso.with(this).load(photoUrl).into(detailImage, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) detailImage.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            applyPalette(palette);
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });

        }

        getNewsDetail();

    }


    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
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

    private void shareNews() {




        String message =  title +", " + url;

        ShareUtils.shareCustom(message, this);

    }

    private void getNewsDetail() {


            webView.loadData("", "text/html; charset=UTF-8", null);

            String html = "<style>img{max-width:100%;height:auto;} " +
                    "iframe{width:100%;}</style> ";
            // Article Title
            html += "<h2>" + title + "</h2> ";
            // Date & author
            html += "<h4>" + dateNews + " " + "</h4>";
            // The actual content
            html += content;

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());

            // Load and display HTML content
            // Use "charset=UTF-8" to support non-English language
            webView.loadData(html, "text/html; charset=UTF-8", null);


        }

    }


