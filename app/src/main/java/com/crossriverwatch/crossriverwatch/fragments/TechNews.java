package com.crossriverwatch.crossriverwatch.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.crossriverwatch.crossriverwatch.utility.AppController;
import com.crossriverwatch.crossriverwatch.activities.CategoryNewsDetail;
import com.crossriverwatch.crossriverwatch.R;
import com.crossriverwatch.crossriverwatch.adapters.CategoryPostAdapter;
import com.crossriverwatch.crossriverwatch.parser.Config;
import com.crossriverwatch.crossriverwatch.parser.JSONParser;
import com.crossriverwatch.crossriverwatch.parser.Post;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class TechNews extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "TechNews";
    private AdView mAdView;

    protected static final String QUERY = "query";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private CategoryPostAdapter mAdaptor;
    private LinearLayoutManager mLayoutManager;
    // Widget to show user a loading message
    private TextView mLoadingView;
    // List of all posts in the ListView
    private ArrayList<Post> postList = new ArrayList<>();
    // A flag to keep track if the app is currently loading new posts
    private boolean isLoading = false;

    private int mPage = 1; // Page number
    private int mCatId = 2498; // Category ID
    private int mPreviousPostNum = 0; // Number of posts in the list
    private int mPostNum; // Number of posts in the "new" list
    private String mQuery = ""; // Query string used for search result
    // Flag to determine if current fragment is used to show search result
    private boolean isSearch = false;
    // Keep track of the list items
    private int mPastVisibleItems;
    private int mVisibleItemCount;
    private TextView catTextView;
    ProgressDialog progressDialog;
    private FirebaseAnalytics mFirebaseAnalytics;

    public TechNews() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_business_news, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_cat);
        mLoadingView = (TextView) view.findViewById(R.id.text_view_loading);
        mLayoutManager = new LinearLayoutManager(getActivity());

        // Pull to refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(this);
        catTextView = (TextView) view.findViewById(R.id.news_category);
        catTextView.setVisibility(View.GONE);
        catTextView.setText("Technology News");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading_news));
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());


        // RecyclerView adaptor for Post object
        mAdaptor = new CategoryPostAdapter(postList, new CategoryPostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                //mListener.onPostSelected(post, isSearch);
                Intent feedDetail = new Intent(getActivity(), CategoryNewsDetail.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", post.getTitle());
                bundle.putString("url",post.getUrl());
                bundle.putString("imageUrl",post.getFeaturedImageUrl());
                bundle.putString("content",post.getContent());
                bundle.putString("date",post.getDate());

                feedDetail.putExtras(bundle);
                startActivity(feedDetail);
            }
        });

        mRecyclerView.setHasFixedSize(true); // Every row in the list has the same size
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdaptor);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            // Automatically load new posts if end of the list is reached
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = mLayoutManager.getChildCount();
                mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();

                if (mPostNum > mPreviousPostNum && !postList.isEmpty() && mVisibleItemCount != 0 &&
                        totalItemCount > mVisibleItemCount && !isLoading &&
                        (mVisibleItemCount + mPastVisibleItems) >= totalItemCount) {
                    loadNextPage();
                    // Update post number
                    mPreviousPostNum = mPostNum;
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, "open Tech News ");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        return view;
    }


    /**
     * Load the first page of a category
     */
    public void loadFirstPage(){
        mPage = 1; // Reset page number

        if (postList.isEmpty()) {
            showLoadingView();
            // Reset post number to 0
            mPreviousPostNum = 0;
            loadPosts(mPage, false);
        } else {
            hideLoadingView();
        }
    }

    /**
     * Load the next page of a category
     */
    public void loadNextPage(){
        mPage ++;
        loadPosts(mPage, true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadFirstPage();
    }


    /**
     * Load posts from a specific page number
     *
     * @param page Page number
     * @param showLoadingMsg Flag to determine whether to show Toast loading msg to inform the user
     */
    private void loadPosts(int page, final boolean showLoadingMsg) {
        Log.d(TAG, "----------------- Loading category id " + mCatId +
                ", page " + String.valueOf(page));

        isLoading = true;

        if (showLoadingMsg) {
            Toast.makeText(getActivity(), getString(R.string.loading_news),
                    Toast.LENGTH_LONG).show();
        }

        // Construct the proper API Url
        String url;
        if (!mQuery.isEmpty()) { // Not empty mQuery means this list is for search result.
            isSearch = true;
            url = Config.BASE_URL + "?json=get_search_results&search=" + mQuery +
                    "&page=" + String.valueOf(page);
        } else { // Empty mQuery means normal list of posts
            isSearch = false;

            if (mCatId == 0) { // The "All" tab
                url = Config.BASE_URL + "?json=get_posts&page=" + String.valueOf(page);
            } else { // Everything else
                isSearch = false;
                url = Config.BASE_URL + "?json=get_category_posts&category_id=" + String.valueOf(mCatId)
                        + "&page=" + String.valueOf(page);
            }

        }

        Log.d(TAG, url);
        // Request post JSON
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        mSwipeRefreshLayout.setRefreshing(false); // Stop when done

                        // Parse JSON data
                        postList.addAll(JSONParser.parsePosts(jsonObject));

                        // A temporary workaround to avoid downloading duplicate posts in some
                        // rare circumstances by converting ArrayList to a LinkedHashSet without
                        // losing its order
                        Set<Post> set = new LinkedHashSet<>(postList);
                        postList.clear();
                        postList.addAll(new ArrayList<>(set));

                        mPostNum = postList.size(); // The newest post number
                        Log.d(TAG, "Number of posts: " + mPostNum);
                        mAdaptor.notifyDataSetChanged(); // Display the list

                        // Set ListView position
                        if (TechNews.this.mPage != 1) {
                            // Move the article list up by one row
                            // We don't actually need to add 1 here since position starts at 0
                            mLayoutManager.scrollToPosition(mPastVisibleItems + mVisibleItemCount);
                        }

                        // Loading finished. Set flag to false
                        isLoading = false;

                        hideLoadingView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        isLoading = false;
                        hideLoadingView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        volleyError.printStackTrace();
                        Log.d(TAG, "----- Error: " + volleyError.getMessage());

                        Toast.makeText(getActivity(),getString(R.string.error_load_posts),Toast.LENGTH_LONG).show();
                    }
                });

        // Set timeout to 10 seconds instead of the default value 5 since my
        // crappy server is quite slow
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add request to request queue
        AppController.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    public void onRefresh() {
        // Clear the list
        postList.clear();
        mAdaptor.notifyDataSetChanged();
        loadFirstPage();
    }

    /**
     * Show the loading view and hide the list
     */
    private void showLoadingView() {
        mRecyclerView.setVisibility(View.GONE);
        progressDialog.show();
        mSwipeRefreshLayout.setRefreshing(false);
        catTextView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);

    }

    /**
     * Hide the loading view and show the list
     */
    private void hideLoadingView() {

        if(mAdaptor.getItemCount()==0){

            mRecyclerView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
            catTextView.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        }else {

            mRecyclerView.setVisibility(View.VISIBLE);
            catTextView.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
            mLoadingView.setVisibility(View.GONE);
        }

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
