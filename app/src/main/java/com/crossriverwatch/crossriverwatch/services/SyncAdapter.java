package com.crossriverwatch.crossriverwatch.services;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;




import com.crossriverwatch.crossriverwatch.database.NewsContract;
import com.crossriverwatch.crossriverwatch.parser.RSSItem;
import com.crossriverwatch.crossriverwatch.parser.ReadRss;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Locale;


/**
 * Created by ESIDEM jnr on 3/8/2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final static String LOG_TAG = "SyncAdapter";
    // Global variables
    // Define a variable to contain a content resolver instance

    private final static String PREFNAME = "SyncPref";

    private ContentResolver mContentResolver;

    ReadRss rssParser = new ReadRss();

    List<RSSItem> rssItems = new ArrayList<RSSItem>();


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



//    private void checkNotifications()
//    {
//        String order  = NewsContract.DATE+" DESC";
//        Cursor c =  mContentResolver.query(NewsProvider.Lists.LISTS,new String[]{ NewsContract._ID,NewsContract.TITLE,NewsContract.DATE},null,null,order);
//        c.moveToFirst();
//        //get news title from SharedPreferences
//        SharedPreferences notifications = getContext().getSharedPreferences(PREFNAME,Context.MODE_PRIVATE);
//        String newsTitle = notifications.getString(getContext().getResources().getString(R.string.natition_key),"");
//        Long date = notifications.getLong(getContext().getResources().getString(R.string.date_nattion_key),0);
//
//        if(newsTitle.length()>1){
//            if (newsTitle.compareTo(c.getString(c.getColumnIndex(NewsContract.TITLE))) == 0 ) {
//                Long cDate = Long.valueOf(c.getString(c.getColumnIndex(NewsContract.DATE)));
//                if(date < cDate) {
//                    showNotifications();
//                    SharedPreferences.Editor editor = notifications.edit();
//                    editor.putString(getContext().getResources().getString(R.string.natition_key), c.getString(c.getColumnIndex(NewsContract.TITLE))).commit();
//                    editor.putLong(getContext().getResources().getString(R.string.date_nattion_key),cDate).commit();
//                }
//            }
//        }else{
//            Long cDate = Long.valueOf(c.getString(c.getColumnIndex(NewsContract.DATE)));
//            showNotifications();
//            SharedPreferences.Editor editor = notifications.edit();
//            editor.putString(getContext().getResources().getString(R.string.natition_key), c.getString(c.getColumnIndex(NewsContract.TITLE))).commit();
//            editor.putLong(getContext().getResources().getString(R.string.date_nattion_key),cDate).commit();
//        }
//
//
//        c.close();
//    }

//    private void showNotifications()
//    {
//        String order  = NewsContract.DATE+" DESC";
//        Cursor c =  mContentResolver.query(NewsProvider.Lists.LISTS,new String[]{ NewsContract._ID,NewsContract.TITLE,NewsContract.DATE},null,null,order);
//        c.moveToFirst();
//        Intent resultIntent = new Intent(getContext(), DetailActivity.class);
//        resultIntent.putExtra("news_id",c.getString(c.getColumnIndex(NewsContract._ID)));
//        resultIntent.putExtra("news_fav","0");
//
//        PendingIntent resultPendingIntent =
//                PendingIntent.getActivity(
//                        getContext(),
//                        0,
//                        resultIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//        Date date = new Date(Long.valueOf(c.getString(c.getColumnIndex(NewsContract.DATE))));
//        SimpleDateFormat sdf = new SimpleDateFormat("d LLL yyyy  HH:mm", Locale.getDefault());
//        String dateText = sdf.format(date);
//
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(getContext())
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setAutoCancel(true)
//                        .setContentTitle(c.getString(c.getColumnIndex(NewsContract.TITLE)))
//                        .setContentText(dateText);
//
//        builder.setContentIntent(resultPendingIntent);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
//        notificationManager.notify(1, builder.build());
//        c.close();
//    }

//    private void deleteOldData()
//    {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//        String order  = NewsContract.Items.PUBLISHED_DATE+" ASC";
//        String select = NewsContract.Items.FAVOURITE + " = ?";
//        Uri dirUri = NewsContract.Items.buildDirUri();
//        Cursor c =  mContentResolver.query(dirUri,
//                new String[]{ NewsContract.Items._ID, NewsContract.Items.FAVOURITE},
//                select,
//                new String[]{"0"},
//                order);
//        int maxItem = Integer.valueOf(prefs.getString(getContext().getString(R.string.pref_maxitem_key),getContext().getString(R.string.pref_maxitem_default)));
//        int deletNum = 0;
//        if(c.getCount() >0 && c.getCount() > maxItem) {
//            deletNum = c.getCount() - maxItem;
//        }
//        int[] deleID;
//        if(deletNum != 0)
//        {
//            c.moveToFirst();
//            deleID = new int[deletNum];
//            for(int i = 0;i < deletNum;i++)
//            {
//                deleID[i] = Integer.valueOf(c.getString(c.getColumnIndex(NewsContract.Items._ID)));
//                c.moveToNext();
//            }
//            for(int i = 0;i < deletNum;i++) {
//                mContentResolver.delete(NewsContract.Lists.withId((long)deleID[i]),null,null);
//            }
//        }
//        c.close();
//    }

//    private void upDateWidget()
//    {
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
//        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
//                new ComponentName(getContext(), ListNewsWidgetProvider.class));
//        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
//    }


    private void insertEntry(RSSItem entry) {




        ContentValues values = new ContentValues();
        values.clear();

        values.put(NewsContract.Entry.COLUMN_NAME_TITLE, entry.getTitle());
        values.put(NewsContract.Entry.COLUMN_NAME_LINK, entry.getLink());
        values.put(NewsContract.Entry.COLUMN_NAME_DESCRIPTION, entry.getDescription());
        values.put(NewsContract.Entry.COLUMN_NAME_IMAGE_URL, entry.getImageUrl());
        values.put(NewsContract.Entry.COLUMN_NAME_FAV, 0);
        values.put(NewsContract.Entry.COLUMN_NAME_PUBLISHED, entry.getPubDate());


        mContentResolver.insert(NewsContract.Entry.CONTENT_URI, values);
    }

    private class FetchNewsTask extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            rssItems = rssParser.parse();
            for(RSSItem item : rssItems){




                ContentValues contentValues = new ContentValues();
                contentValues.put(NewsContract.Entry.COLUMN_NAME_TITLE, item.getTitle());
                contentValues.put(NewsContract.Entry.COLUMN_NAME_LINK, item.getLink());
                contentValues.put(NewsContract.Entry.COLUMN_NAME_DESCRIPTION, item.getDescription());
                contentValues.put(NewsContract.Entry.COLUMN_NAME_FAV, 0);
                contentValues.put(NewsContract.Entry.COLUMN_NAME_PUBLISHED, item.getPubDate());
                contentValues.put(NewsContract.Entry.COLUMN_NAME_IMAGE_URL, item.getImageUrl());

                String select = "("+ NewsContract.Entry.COLUMN_NAME_TITLE+ " = ? )";
                Uri dirUri = NewsContract.Entry.buildDirUri();
                Cursor check = mContentResolver.query(dirUri,new String[]{ NewsContract.Entry.COLUMN_NAME_TITLE},
                        select,new String[]{item.getTitle()},null,null);
                check.moveToFirst();
                if(check.getCount() > 0) {
                    int columIndex = check.getColumnIndex(NewsContract.Entry.COLUMN_NAME_TITLE);
                    if (item.getTitle().compareTo(check.getString(columIndex)) == 1 ) {
                        insertEntry(item);
                    }
                }else{
                    insertEntry(item);
                }
                check.close();
            }


            return null;
        }
    }
}


