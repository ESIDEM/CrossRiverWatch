package com.crossriverwatch.crossriverwatch;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.crossriverwatch.crossriverwatch.database.NewsContract;

import com.crossriverwatch.crossriverwatch.utility.ConnectionTest;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Favourites extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    String[] PROJECTION = {
            NewsContract.Entry._ID,
            NewsContract.Entry.COLUMN_NAME_TITLE,
            NewsContract.Entry.COLUMN_NAME_PUBLISHED,
            NewsContract.Entry.COLUMN_NAME_LINK,
            NewsContract.Entry.COLUMN_NAME_IMAGE_URL,
            NewsContract.Entry.COLUMN_NAME_FAV
    };

    protected TextView mEmptyView;
    private ShimmerRecyclerView shimmerRecycler;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shimmerRecycler = (ShimmerRecyclerView) findViewById(R.id.shimmer_recycler_view);
        mEmptyView = (TextView) findViewById(R.id.no_favourite_text);
        isConnected = ConnectionTest.isNetworkAvailable(this);
        getSupportLoaderManager().initLoader(0, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String order  = NewsContract.Entry.COLUMN_NAME_PUBLISHED+" DESC";
        String select = NewsContract.Entry.COLUMN_NAME_FAV + " = ?";
        return new CursorLoader(this,
                NewsContract.Entry.CONTENT_URI,
                PROJECTION,
                select,
                new String[]{"1"},
                order);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        FavAdapter adapter = new FavAdapter(data);
        adapter.setHasStableIds(true);
        shimmerRecycler.setAdapter(adapter);
        LinearLayoutManager lm =
                new LinearLayoutManager(this);
        shimmerRecycler.setLayoutManager(lm);
        if(data.getCount()==0) {

            mEmptyView.setVisibility(VISIBLE);
        }else{

            mEmptyView.setVisibility(GONE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        shimmerRecycler.setAdapter(null);

    }

    private class FavAdapter extends RecyclerView.Adapter<ViewHolder>{

        public int mfav;
        private Context context;
        public int newsId;

        private Cursor mCursor ;


        public FavAdapter(Cursor cursor) {
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
      //      final String favourite = mCursor.getString(MyLoader.Query.COLUMN_FAV);



//            Glide.with(holder.thumbnailView.getContext()).load(mCursor.getString(
//                    MyLoader.Query.COLUMN_PHOTO_URL))
//
//                    //load images as bitmaps to get fixed dimensions
//                    .asBitmap()
//
//                    //set a placeholder image
//                    .placeholder(R.drawable.cinema_new)
//
//                    //disable cache to avoid garbage collection that may produce crashes
//                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                    .into(holder.thumbnailView);
            // mfav = Integer.valueOf(favourite);

            Glide.with(getApplicationContext())
                    .load(mCursor.getString(mCursor.getColumnIndex(NewsContract.Entry.COLUMN_NAME_IMAGE_URL)))
                    .error(R.drawable.cinema_new)
                    .crossFade()
                    .centerCrop()
                    .into(holder.thumbnailView);
            newsId = mCursor.getInt(MyLoader.Query.COLUMN_ID);

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
}
