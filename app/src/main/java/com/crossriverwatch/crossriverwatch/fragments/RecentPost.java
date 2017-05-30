package com.crossriverwatch.crossriverwatch.fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.crossriverwatch.crossriverwatch.utility.AppController;
import com.crossriverwatch.crossriverwatch.database.NewsContract;
import com.crossriverwatch.crossriverwatch.parser.JSONParser;
import com.crossriverwatch.crossriverwatch.parser.Post;
import com.crossriverwatch.crossriverwatch.utility.MyLoader;
import com.crossriverwatch.crossriverwatch.R;
import com.crossriverwatch.crossriverwatch.adapters.RecentPostAdapter;
import com.crossriverwatch.crossriverwatch.utility.ConnectionTest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentPost extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "RecentPost";
    private AdView mAdView;
    public Context nContext;
    protected TextView mEmptyView;
    protected SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private ShimmerRecyclerView shimmerRecycler;
    boolean isConnected;
    RecyclerView.LayoutManager layoutManager;
    RecentPostAdapter recentPostAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;


    private ProgressDialog mProgress;
    private ContentResolver mContentResolver;
             private TextView catTextView;





    List<Post> postItems = new ArrayList<Post>();




    public RecentPost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent_post, container, false);

        shimmerRecycler = (ShimmerRecyclerView) view.findViewById(R.id.shimmer_recycler_view);
        shimmerRecycler.setLayoutManager(layoutManager);
        mEmptyView = (TextView) view.findViewById(R.id.show_net_text);

//       mSwipeContainer.setColorSchemeResources(R.color.colorAccent);
        isConnected = ConnectionTest.isNetworkAvailable(getContext());
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(this);
        mContentResolver = getActivity().getContentResolver();
        catTextView = (TextView) view.findViewById(R.id.news_category);
        catTextView.setVisibility(View.GONE);
        catTextView.setText("Recent News");

        mProgress = new ProgressDialog(getContext());
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());


        getLoaderManager().initLoader(0, null, this);

       // loadNews();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, "open Recent News ");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return MyLoader.newAllArticlesInstance(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
       // mSwipeContainer.setRefreshing(false);
        //Adapter adapter = new Adapter(cursor);
        recentPostAdapter = new RecentPostAdapter(getActivity(),cursor);
        recentPostAdapter.setHasStableIds(true);

        // mRecyclerView.addItemDecoration(new NewsDivider());

        // mRecyclerView.setAdapter(adapter);
        shimmerRecycler.setAdapter(recentPostAdapter);
        LinearLayoutManager lm =
                new LinearLayoutManager(getContext());
        shimmerRecycler.setLayoutManager(lm);
        if(cursor.getCount()==0) {

            mEmptyView.setVisibility(VISIBLE);
        }else{

            mEmptyView.setVisibility(GONE);
            catTextView.setVisibility(VISIBLE);
        }





    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        shimmerRecycler.setAdapter(null);
    }

    @Override
    public void onRefresh() {

        if (isConnected) {

            for (int page = 1 ; page <=5 ; page++){
                loadNews(page);

            }
            getLoaderManager().restartLoader(0, null, this);

        } else {
            Toast.makeText(getContext(),getString(R.string.offline_text),Toast.LENGTH_LONG).show();
            mSwipeContainer.setRefreshing(false);
            return;
        }

    }

//    private void loadNews() {
//
//        if (isConnected) {
//
//            mProgress.setMessage("Please Wait..." );
//            mProgress.show();
//            // progressDelay(6000,mProgress);
//
//            SyncUtils.TriggerRefresh();
//           // mSwipeContainer.setRefreshing(false);
//        } else {
//            Toast.makeText(getContext(),getString(R.string.offline_text),Toast.LENGTH_LONG).show();
//           // mSwipeContainer.setRefreshing(false);
//            return;
//        }
//    }




    private void loadNews(int page){

        // String url = Config.NEW_URL;
        String url = ((AppController) getContext().getApplicationContext()).getBASE_URL()  + String.valueOf(page);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        mSwipeContainer.setRefreshing(false); // Stop when done


                        // Parse JSON data
                        postItems.addAll(JSONParser.parsePosts(jsonObject));

                        // A temporary workaround to avoid downloading duplicate posts in some
                        // rare circumstances by converting ArrayList to a LinkedHashSet without
                        // losing its order
                        Set<Post> set = new LinkedHashSet<>(postItems);
                        postItems.clear();
                        postItems.addAll(new ArrayList<>(set));

                        for (Post item : postItems) {



                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_TITLE, item.getTitle());
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_LINK, item.getUrl());
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_DESCRIPTION, item.getContent());
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_FAV, 0);
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_PUBLISHED, item.getDate());
                            contentValues.put(NewsContract.Entry.COLUMN_NAME_IMAGE_URL, item.getFeaturedImageUrl());
                            // contentValues.put(NewsContract.Entry.COLUMN_NAME_CATEGORIES, changeToString(item.getCategories()));


                            String select = "(" + NewsContract.Entry.COLUMN_NAME_TITLE + " = ? )";
                            Uri dirUri = NewsContract.Entry.buildDirUri();
                            Cursor check = mContentResolver.query(dirUri, new String[]{NewsContract.Entry.COLUMN_NAME_TITLE},
                                    select, new String[]{item.getTitle()}, null, null);
                            check.moveToFirst();
                            if (check.getCount() > 0) {
                                int columIndex = check.getColumnIndex(NewsContract.Entry.COLUMN_NAME_TITLE);
                                if (item.getTitle().compareTo(check.getString(columIndex)) == 1) {
                                    insertEntry(item);
                                }
                            } else {
                                insertEntry(item);
                            }
                            check.close();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        mSwipeContainer.setRefreshing(false);


                        volleyError.printStackTrace();
                        Log.d(TAG, "----- Error: " + volleyError.getMessage());


                    }
                });


        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(request, TAG);
        AppController.getInstance().getRequestQueue().getCache().clear();
        // rssItems = rssParser.parse();


    }


    private void insertEntry(Post entry) {




        ContentValues values = new ContentValues();
        values.clear();

        values.put(NewsContract.Entry.COLUMN_NAME_TITLE, entry.getTitle());
        values.put(NewsContract.Entry.COLUMN_NAME_LINK, entry.getUrl());
        values.put(NewsContract.Entry.COLUMN_NAME_DESCRIPTION, entry.getContent());
        values.put(NewsContract.Entry.COLUMN_NAME_IMAGE_URL, entry.getFeaturedImageUrl());
        values.put(NewsContract.Entry.COLUMN_NAME_FAV, 0);
        values.put(NewsContract.Entry.COLUMN_NAME_PUBLISHED, entry.getDate());
        // values.put(NewsContract.Entry.COLUMN_NAME_CATEGORIES, changeToString(entry.getCategories()));



        mContentResolver.insert(NewsContract.Entry.CONTENT_URI, values);

        deleteOldData();

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
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdView != null) {
            mAdView.resume();
        }

    }




    @Override
    public void onDestroy() {

        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }



}
