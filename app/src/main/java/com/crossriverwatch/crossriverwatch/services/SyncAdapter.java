package com.crossriverwatch.crossriverwatch.services;

import android.accounts.Account;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;


import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;



import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.crossriverwatch.crossriverwatch.activities.MainActivity;
import com.crossriverwatch.crossriverwatch.activities.NewsDetailActivity;
import com.crossriverwatch.crossriverwatch.database.NewsProvider;
import com.crossriverwatch.crossriverwatch.utility.AppController;

import com.crossriverwatch.crossriverwatch.R;
import com.crossriverwatch.crossriverwatch.database.NewsContract;

import com.crossriverwatch.crossriverwatch.parser.JSONParser;
import com.crossriverwatch.crossriverwatch.parser.Post;

import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;


import java.util.Locale;
import java.util.Set;



/**
 * Created by ESIDEM jnr on 3/8/2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {




    private final static String LOG_TAG = "SyncAdapter";
    // Global variables
    // Define a variable to contain a content resolver instance

    private final static String PREFNAME = "SyncPref";

    private ContentResolver mContentResolver;

    String title;
    String date;




    List<Post>postItems = new ArrayList<Post>();



    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */

    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

//    private void downloadRss(String url)
//    {
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        ParsRss(response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("Feed","can't get Rss");
//            }
//        });
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);
//    }

//    private void ParsRss(String url)
//    {
//        try {
//            InputStream inputStream = new ByteArrayInputStream(url.getBytes(StandardCharsets.UTF_8));
//            Feed feed = EarlParser.parseOrThrow(inputStream, 0);
//            Log.i("Feed", "Processing feed: " + feed.getTitle());
//
//            ArrayList<ContentValues> cvArray = new ArrayList<>();
//
//            if (RSSFeed.class.isInstance(feed)) {
//                RSSFeed rssFeed = (RSSFeed) feed;
//                for (RSSItem item : rssFeed.items) {
//                    String photoLink;
//                    //Log.i("Feed", "Item title: " + (title == null ? "N/A" : title));
//
//                    //Date date = new Date(item.pubDate.getTime());
//                    //SimpleDateFormat sdf = new SimpleDateFormat("d LLL yyyy  HH:mm", Locale.getDefault());
//                    RSSEnclosure enclosure;
//                    if(!item.enclosures.isEmpty()) {
//                        enclosure = item.enclosures.get(0);
//                        photoLink = enclosure.getLink();
//                    }else{
//                        photoLink = "";
//                    }
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put(NewsContract.Items.TITLE, item.getTitle());
//                    contentValues.put(NewsContract.Items.LINK, item.getLink());
//                    contentValues.put(NewsContract.Items.BODY, item.getDescription());
//                    contentValues.put(NewsContract.Items.FAVOURITE, 0);
//                    contentValues.put(NewsContract.Items.PUBLISHED_DATE, Long.toString(item.pubDate.getTime()));
//                    contentValues.put(NewsContract.Items.PHOTO_URL, photoLink);
//
//                    String select = "("+ NewsContract.Items.TITLE+ " = ? )";
//                    Uri dirUri = NewsContract.Items.buildDirUri();
//                    Cursor check = mContentResolver.query(dirUri,new String[]{ NewsContract.Items.TITLE},
//                            select,new String[]{item.getTitle()},null,null);
//                    check.moveToFirst();
//                    if(check.getCount() > 0) {
//                        int columIndex = check.getColumnIndex(NewsContract.Items.TITLE);
//                        if (item.getTitle().compareTo(check.getString(columIndex)) == 1 ) {
//                            cvArray.add(contentValues);
//                        }
//                    }else{
//                        cvArray.add(contentValues);
//                    }
//                    check.close();
//                }
//            }
//            ContentValues[] cc = new ContentValues[cvArray.size()];
//            cvArray.toArray(cc);
//
//            Uri dirUri = NewsContract.Items.buildDirUri();
//            mContentResolver.bulkInsert(dirUri, cc);
//
////            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
////            if(cc.length>0 && prefs.getBoolean(getContext().getString(R.string.pref_notification_key),true) ) {
////                checkNotifications();
////               upDateWidget();
////            }
//            // deleteOldData();
//
//            //Cursor c =  mContentResolver.query(NewsProvider.Lists.LISTS,new String[]{ NewsContract._ID},null,null,null);
//            Log.d("Provider data", Integer.toString(cc.length));
//            //c.close();
//
//
//
//        }catch (MalformedURLException e) {
//            Log.d("Url","error");
//        }catch (IOException e){
//            Log.d("IO","ERROR");
//        }catch (XmlPullParserException e)
//        {
//            Log.d("XML","error");
//        }catch (DataFormatException e){
//            Log.d("Date","Error");
//        }catch (NullPointerException e){
//            e.printStackTrace();
//        }
//    }

    /*
         * Specify the code you want to run in the sync adapter. The entire
         * sync adapter runs in a background thread, so you don't have to set
         * up your own background processing.
         */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
    /*
     * Put the data transfer code here.
     */


        new FetchNewsTask().execute();


    }



    private void checkNotifications()
    {

        String[] projection = {
                NewsContract.Entry._ID,
                NewsContract.Entry.COLUMN_NAME_TITLE,

                NewsContract.Entry.COLUMN_NAME_PUBLISHED,
        };

        String order  =  NewsContract.Entry.COLUMN_NAME_PUBLISHED + " DESC" + " LIMIT 1";
        Cursor c =  mContentResolver.query(NewsContract.Entry.CONTENT_URI,projection,null,null,order);
        c.moveToFirst();
        //get news title from SharedPreferences
        SharedPreferences notifications = getContext().getSharedPreferences(PREFNAME,Context.MODE_PRIVATE);
        String newsTitle = notifications.getString(getContext().getResources().getString(R.string.notification_key),"");

       // if(newsTitle.length()>1){
            if (newsTitle.equals(c.getString(c.getColumnIndex(NewsContract.Entry.COLUMN_NAME_TITLE)) )) {

                return;


        }else{
                showNotifications();
                SharedPreferences.Editor editor = notifications.edit();
                editor.putString(getContext().getResources().getString(R.string.notification_key), c.getString(c.getColumnIndex(NewsContract.Entry.COLUMN_NAME_TITLE))).apply();

            }


        c.close();
    }



//    private void upDateWidget()
//    {
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
//        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
//                new ComponentName(getContext(), ListNewsWidgetProvider.class));
//        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
//    }


//    private void insertEntry(Post entry) {
//
//
//
//
//        ContentValues values = new ContentValues();
//        values.clear();
//
//        values.put(NewsContract.Entry.COLUMN_NAME_TITLE, entry.getTitle());
//        values.put(NewsContract.Entry.COLUMN_NAME_LINK, entry.getUrl());
//        values.put(NewsContract.Entry.COLUMN_NAME_DESCRIPTION, entry.getContent());
//        values.put(NewsContract.Entry.COLUMN_NAME_IMAGE_URL, entry.getFeaturedImageUrl());
//        values.put(NewsContract.Entry.COLUMN_NAME_FAV, 0);
//        values.put(NewsContract.Entry.COLUMN_NAME_PUBLISHED, entry.getDate());
//
//
//
//
//        mContentResolver.insert(NewsContract.Entry.CONTENT_URI, values);
//
//
//        deleteOldData();
//
//    }

    private class FetchNewsTask extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            for (int page = 1 ; page <=5 ; page++){
                loadNews(page);

           }
                return null;
            }





    }

    private void loadNews(int page){

        // String url = Config.NEW_URL;
        String url = ((AppController) getContext().getApplicationContext()).getBASE_URL()  + String.valueOf(page);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {


                        // Parse JSON data
                        postItems.addAll(JSONParser.parsePosts(jsonObject));

                        // A temporary workaround to avoid downloading duplicate posts in some
                        // rare circumstances by converting ArrayList to a LinkedHashSet without
                        // losing its order

                        ArrayList<ContentValues> cvArray = new ArrayList<>();
                        Set<Post> set = new LinkedHashSet<>(postItems);
                        postItems.clear();
                        postItems.addAll(new ArrayList<>(set));

                        for (Post item : postItems) {



                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_TITLE, item.getTitle());
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_LINK, item.getUrl());
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_DESCRIPTION, item.getContent());
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_FAV, 0);
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_PUBLISHED, (item.getDate()));
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_IMAGE_URL, item.getFeaturedImageUrl());


                            String select = "(" + NewsContract.Entry.COLUMN_NAME_TITLE + " = ? )";
                            Uri dirUri = NewsContract.Entry.buildDirUri();
                            Cursor check = mContentResolver.query(dirUri, new String[]{NewsContract.Entry.COLUMN_NAME_TITLE},
                                    select, new String[]{item.getTitle()}, null, null);
                            check.moveToFirst();
                            if (check.getCount() > 0) {
                                int columIndex = check.getColumnIndex(NewsContract.Entry.COLUMN_NAME_TITLE);
                                if (item.getTitle().compareTo(check.getString(columIndex)) == 1) {
                                    cvArray.add(contentValues);
                                }
                            }else {

                                cvArray.add(contentValues);
                            }

                            check.close();
                        }

                        ContentValues[] cc = new ContentValues[cvArray.size()];
                        cvArray.toArray(cc);

                        if(cc.length>0) {

                            mContentResolver.bulkInsert(NewsContract.Entry.CONTENT_URI, cc);


                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                            if ( prefs.getBoolean(getContext().getString(R.string.pref_notification_key), true)) {

                                checkNotifications();

                            }
                            deleteOldData();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {


                        volleyError.printStackTrace();
                        Log.d(LOG_TAG, "----- Error: " + volleyError.getMessage());


                    }
                });


        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(request, LOG_TAG);
        AppController.getInstance().getRequestQueue().getCache().clear();
        // rssItems = rssParser.parse();


    }

    private void deleteOldData()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String order  = NewsContract.Entry.COLUMN_NAME_PUBLISHED+" ASC";
        String select = NewsContract.Entry.COLUMN_NAME_FAV + " = ?";
        Cursor c =  mContentResolver.query(NewsContract.Entry.CONTENT_URI,
                new String[]{ NewsContract.Entry._ID,NewsContract.Entry.COLUMN_NAME_FAV},
                select,
                new String[]{"0"},
                order);
        int maxItem = Integer.valueOf(prefs.getString(getContext().getString(R.string.pref_max_item_key),getContext().getString(R.string.pref_max_item_default)));
        int deletNum = 0;
        if(c.getCount() >0 && c.getCount() > maxItem) {
            deletNum = c.getCount() - maxItem;
        }
        int[] deleID;
        if(deletNum != 0)
        {
            c.moveToFirst();
            deleID = new int[deletNum];
            for(int i = 0;i < deletNum;i++)
            {
                deleID[i] = Integer.valueOf(c.getString(c.getColumnIndex(NewsContract.Entry._ID)));
                c.moveToNext();
            }
            for(int i = 0;i < deletNum;i++) {
                mContentResolver.delete(NewsContract.Entry.buildItemUri((long)deleID[i]),null,null);
            }
        }
        c.close();
    }

    private void showNotifications() {

        String[] projection = {
                NewsContract.Entry._ID,
                NewsContract.Entry.COLUMN_NAME_TITLE,

                NewsContract.Entry.COLUMN_NAME_PUBLISHED,
        };



        String order = NewsContract.Entry.COLUMN_NAME_PUBLISHED + " DESC" + " LIMIT 1";

        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(NewsContract.Entry.CONTENT_URI, projection, null, null,
                order );
        if (cursor != null) {
            cursor.moveToFirst();

            int notificationId = 0x10;

            Intent resultIntent = new Intent(getContext(), MainActivity.class);
            //resultIntent.putExtra("news_id",c.getString(c.getColumnIndex(NewsContract.Entry._ID)));
            // resultIntent.putExtra("news_fav","0");

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
           // Date date = new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(NewsContract.Entry.COLUMN_NAME_PUBLISHED))));
           // SimpleDateFormat sdf = new SimpleDateFormat("d LLL yyyy  HH:mm", Locale.getDefault());
           // String dateText = sdf.format(date);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setContentTitle(cursor.getString(cursor.getColumnIndex(NewsContract.Entry.COLUMN_NAME_TITLE)))
                            .setContentText(cursor.getString(cursor.getColumnIndex(NewsContract.Entry.COLUMN_NAME_PUBLISHED)));

            builder.setContentIntent(resultPendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
            notificationManager.notify(++notificationId, builder.build());
            cursor.close();
        }
    }


}


