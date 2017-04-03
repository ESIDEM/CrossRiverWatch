package com.crossriverwatch.crossriverwatch;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.graphics.Palette;

import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crossriverwatch.crossriverwatch.database.NewsContract;
import com.crossriverwatch.crossriverwatch.database.NewsProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static android.R.attr.author;


public class NewsDetailActivity extends AppCompatActivity {

    private long id;
    private String title;
    private TextView title_textView;
    //private TextView detailsView;
    private Context context;
    private String dateNews;
    public String url;
    private String photoUrl;
    private ImageView detailImage;
    private String detailStr;
    private WebView webView;
    private ProgressDialog progressDialog;

    private FloatingActionButton mFab;
    private int mFav;

    // WebView params
    private final String base = "file:///android_asset/";
    private final String mime = "text/html";
    private final String encoding = "utf-8";
    private final String history = null;

    Document doc;



    private CollapsingToolbarLayout collapsingToolbarLayout;


    @SuppressLint("SetJavaScriptEnabled")
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_dialogue));


        // title_textView = (TextView) findViewById(R.id.title_view);

        //detailsView = (TextView) findViewById(R.id.description_view);

        mFab = (FloatingActionButton) findViewById(R.id.favButton);

        detailImage = (ImageView) findViewById(R.id.image_view);
        webView = (WebView) findViewById(R.id.webView);
        // webView = new WebViewHelper().webview(this);
       // WebSettings ws = webView.getSettings();
       // ws.setJavaScriptEnabled(true);

        getNewsDetail();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toatsText;
                if(mFav == 1) {
                    mFab.setImageResource(R.drawable.ic_action_action_favorite_outline);
                    toatsText = getString(R.string.removed_favorite);
                    mFav = 0;
                }else{
                    mFab.setImageResource(R.drawable.ic_action_action_favorite);
                    mFav = 1;
                    toatsText = getString(R.string.added_favorite);
                }
                saveOrRemoveReadlist();

                Toast.makeText(getApplicationContext(),toatsText,Toast.LENGTH_SHORT).show();

            }
        });





        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
       // new FetchDetails().execute();

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

        // title_textView.setText(title);
        // detailsView.setText(detailStr);

        // Load actual article content async, after the view is drawn
        // webView.loadDataWithBaseURL(base, getCleanContent(), mime, encoding, history);

//        webView.setWebChromeClient(new WebChromeClient());
//
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//
//
//
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                Toast.makeText(getApplicationContext(), "Cannot load page", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//            }
//        });
//
//        webView.loadUrl(url);



    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onStart()
    {
        getNewsDetail();
        super.onStart();

    }

    public void onPause()
    {
        finish();
        super.onPause();

    }

    public void onStop()
    {
        finish();
        super.onStop();

    }

    public void onRestart()
    {
        getNewsDetail();
        super.onRestart();

    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
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

    private void getNewsDetail() {

//        progressDialog.setMessage(getString(R.string.progress_dialogue));
//        progressDialog.show();

        String[] projection = {
                NewsContract.Entry._ID,
                NewsContract.Entry.COLUMN_NAME_TITLE,
                NewsContract.Entry.COLUMN_NAME_LINK,
                NewsContract.Entry.COLUMN_NAME_IMAGE_URL,
                NewsContract.Entry.COLUMN_NAME_PUBLISHED,
                NewsContract.Entry.COLUMN_NAME_FAV,
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
            String details = cursor.getString(cursor.getColumnIndexOrThrow(NewsContract.Entry.COLUMN_NAME_DESCRIPTION));
            int favourite = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(NewsContract.Entry.COLUMN_NAME_FAV)));
            String newsDate = cursor.getString(cursor.getColumnIndexOrThrow(NewsContract.Entry.COLUMN_NAME_PUBLISHED));

            title = mTitle;
            url = mUrl;
            photoUrl = imageUrl;
            detailStr = details;
            mFav = favourite;
            dateNews = newsDate;

            if(mFav == 1) {
                mFab.setImageResource(R.drawable.ic_action_action_favorite);
            }else{
                mFab.setImageResource(R.drawable.ic_action_action_favorite_outline);
            }

            webView.loadData("", "text/html; charset=UTF-8", null);

            String html = "<style>img{max-width:100%;height:auto;} " +
                    "iframe{width:100%;}</style> ";
            // Article Title
            html += "<h2>" + title + "</h2> ";
            // Date & author
            html += "<h4>" + dateNews + " " + author + "</h4>";
            // The actual content
            html += detailStr;

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());

            // Load and display HTML content
            // Use "charset=UTF-8" to support non-English language
            webView.loadData(html, "text/html; charset=UTF-8", null);


        }

}

    private void saveOrRemoveReadlist()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsContract.Entry.COLUMN_NAME_FAV, mFav);
        Uri uri = Uri.parse(NewsContract.Entry.CONTENT_URI + "/" + id);

        this.getContentResolver().update(uri,contentValues,null,null);
    }







//    private class FetchDetails extends AsyncTask<Document, Void, Document> {
//
//        String mYUrl;
//
//        @Override
//        protected void onPreExecute() {
//
//            String[] projection = {
//                    NewsContract.Entry._ID,
//
//                    NewsContract.Entry.COLUMN_NAME_LINK,
//                    };
//            Uri uri = Uri.parse(NewsContract.Entry.CONTENT_URI + "/" + id);
//            ContentResolver resolver = getApplicationContext().getContentResolver();
//            Cursor cursor = resolver.query(uri, projection, null, null,
//                    null);
//            if (cursor != null) {
//                cursor.moveToFirst();
//
//                String mUrl = cursor.getString(cursor.getColumnIndexOrThrow(NewsContract.Entry.COLUMN_NAME_LINK));
//
//                mYUrl = mUrl;
//
//            }
//
//
//            progressDialog.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Document doInBackground(Document... documents) {
//
//            Document document = null;
//            try {
//                document = Jsoup.connect(mYUrl.toString()).get();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if(document!=null){
//            document.getElementsByClass("breaking-news").remove();
//            document.getElementsByClass("category-title").remove();
//            document.getElementsByClass("short-link").remove();
//            document.getElementsByClass("sidebar main-sidebar").remove();
//            document.getElementsByClass("mom-social-share ss-horizontal border-box").remove();
//            document.getElementsByClass("addtoany_share_save_container addtoany_content_top").remove();
//            document.getElementsByClass("addtoany_share_save_container addtoany_content_bottom").remove();
//            document.getElementsByClass("copyrights-area").remove();
//            document.getElementsByClass("np-posts").remove();
//            document.getElementsByClass("post-tags").remove();
//            document.getElementsByClass("mom-e3lanat-wrap").remove();
//            document.getElementsByClass("short-link").remove();
//            document.getElementsByClass("single-title").remove();
//            document.getElementsByClass("base-box single-box about-the-author").remove();
//            document.getElementsByClass("base-box single-box").remove();
//            //document.getElementsByClass("short-link").remove();
//            //document.getElementsByClass("short-link").remove();
//            //document.getElementsByClass("short-link").remove();
//            //document.getElementsByClass("short-link").remove();
//            document.getElementById("header-wrapper").remove();
//            document.getElementById("navigation").remove();
//            document.getElementById("footer").remove();
//            return document;
//        }
//        return null;
//        }
//
//
//        @Override
//        protected void onPostExecute(Document document) {
//
//            if(document!=null) {
//
//                webView.setWebChromeClient(new WebChromeClient());
//
//                webView.setWebViewClient(new WebViewClient() {
//                    @Override
//                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                        super.onPageStarted(view, url, favicon);
//
//
//
//                    }
//
//                    @Override
//                    public void onPageFinished(WebView view, String url) {
//                        super.onPageFinished(view, url);
//                        progressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                        super.onReceivedError(view, request, error);
//                        Toast.makeText(getApplicationContext(), "Cannot load page", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                });
//
//                webView.loadDataWithBaseURL(base, document.toString(), "text/html", "utf-8", "");
//
//               // progressDialog.dismiss();
//            }else {
//
//                Toast.makeText(getApplicationContext(),"Unable to load News details ", Toast.LENGTH_LONG).show();
//
//                progressDialog.dismiss();
//            }
//            super.onPostExecute(document);
//        }
//    }


}