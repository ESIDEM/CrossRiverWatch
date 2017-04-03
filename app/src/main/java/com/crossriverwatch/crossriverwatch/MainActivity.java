package com.crossriverwatch.crossriverwatch;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.crossriverwatch.crossriverwatch.database.NewsContract;
import com.crossriverwatch.crossriverwatch.services.SyncUtils;
import com.crossriverwatch.crossriverwatch.utility.ConnectionTest;


import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.attr.description;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;



public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,NavigationView.OnNavigationItemSelectedListener,
        android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";
    public Context nContext;
    protected TextView mEmptyView;
    protected SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private ShimmerRecyclerView shimmerRecycler;
    boolean isConnected;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        shimmerRecycler = (ShimmerRecyclerView) findViewById(R.id.shimmer_recycler_view);
        shimmerRecycler.setLayoutManager(layoutManager);
        mEmptyView = (TextView) findViewById(R.id.show_net_text);
        mSwipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(this);
        mSwipeContainer.setColorSchemeResources(R.color.colorAccent);
        isConnected = ConnectionTest.isNetworkAvailable(this);

        SyncUtils.CreateSyncAccount(this);

        getSupportLoaderManager().initLoader(0, null, this);

        loadNews();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_latest) {
            // Handle the camera action
        } else if (id == R.id.nav_politics) {

        } else if (id == R.id.nav_education) {

        } else if (id == R.id.nav_report) {

        } else if (id == R.id.nav_health) {

        } else if (id == R.id.nav_interview) {

        }else if (id == R.id.nav_favourite){
            Intent intent = new Intent(this,Favourites.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }




    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return MyLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mSwipeContainer.setRefreshing(false);
        Adapter adapter = new Adapter(cursor);
        adapter.setHasStableIds(true);

       // mRecyclerView.addItemDecoration(new NewsDivider());

       // mRecyclerView.setAdapter(adapter);
        shimmerRecycler.setAdapter(adapter);
        LinearLayoutManager lm =
                new LinearLayoutManager(this);
        shimmerRecycler.setLayoutManager(lm);
        if(cursor.getCount()==0) {

            mEmptyView.setVisibility(VISIBLE);
        }else{

            mEmptyView.setVisibility(GONE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        shimmerRecycler.setAdapter(null);
    }

    @Override
    public void onRefresh() {

        if (isConnected) {

            SyncUtils.TriggerRefresh();
            getSupportLoaderManager().restartLoader(0, null, this);

        } else {
            Toast.makeText(this,getString(R.string.offline_text),Toast.LENGTH_LONG).show();
            mSwipeContainer.setRefreshing(false);
            return;
        }

    }

    private void loadNews() {

        if (isConnected) {

            SyncUtils.TriggerRefresh();
            mSwipeContainer.setRefreshing(false);
        } else {
            Toast.makeText(this,getString(R.string.offline_text),Toast.LENGTH_LONG).show();
            mSwipeContainer.setRefreshing(false);
            return;
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder>  {


   public int mfav;
        public int newsId;

        private Cursor mCursor ;


        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(MyLoader.Query.COLUMN_ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.news_item, parent, false);

            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {




                    if(isConnected) {



                        long rowId = getItemId(vh.getAdapterPosition());


                        Intent feedDetail = new Intent(getApplicationContext(), NewsDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("rowId", rowId);

                        feedDetail.putExtras(bundle);
                        startActivity(feedDetail);

                    }else {

                        ConnectionTest.showToastForDuration(getApplicationContext(), getString(R.string.offline_text), 5000,
                                Gravity.CENTER);
                    }
                }
            });




            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(MyLoader.Query.COLUMN_TITLE));

            holder.pubDate.setText(mCursor.getString(MyLoader.Query.COLUMN_PUB_DATE));
           // holder.description.setText(mCursor.getString(MyLoader.Query.COLUMN_DESC));
        final String favourite = mCursor.getString(MyLoader.Query.COLUMN_FAV);



            Glide.with(holder.thumbnailView.getContext()).load(mCursor.getString(
                    MyLoader.Query.COLUMN_PHOTO_URL))

                    //load images as bitmaps to get fixed dimensions
                    .asBitmap()

                    //set a placeholder image
                    .placeholder(R.drawable.cinema_new)

                    //disable cache to avoid garbage collection that may produce crashes
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.thumbnailView);
           // mfav = Integer.valueOf(favourite);
            newsId = mCursor.getInt(MyLoader.Query.COLUMN_ID);

        }

        private void saveOrRemoveFavourite()
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NewsContract.Entry.COLUMN_NAME_FAV, mfav);
            Uri uri = Uri.parse(NewsContract.Entry.CONTENT_URI + "/" + newsId);
            getContentResolver().update(uri, contentValues, null, null);

        }

        @Override
        public int getItemCount() {


            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
       // public String favourite;
       // public TextView description;
        public TextView pubDate;


        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.news_image);
            titleView = (TextView) view.findViewById(R.id.news_title);
           // description =(TextView) view.findViewById(R.id.card_subtitle);
            pubDate = (TextView) view.findViewById(R.id.news_date);


        }
    }



//    public void progressDelay(long time, final Dialog d){
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                d.dismiss();
//            }
//        }, time);
//    }


}
