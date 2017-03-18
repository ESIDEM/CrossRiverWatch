package com.crossriverwatch.crossriverwatch;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.crossriverwatch.crossriverwatch.database.NewsContract;
import com.crossriverwatch.crossriverwatch.database.NewsProvider;
import com.crossriverwatch.crossriverwatch.utility.ConnectionTest;

import static android.R.attr.data;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavouriteListActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

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


    public FavouriteListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_favourite_list, container, false);

        shimmerRecycler = (ShimmerRecyclerView) view.findViewById(R.id.shimmer_recycler_view);
        mEmptyView = (TextView) view.findViewById(R.id.empty_favourite);
        isConnected = ConnectionTest.isNetworkAvailable(getContext());
        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String order  = NewsContract.Entry.COLUMN_NAME_PUBLISHED+" DESC";
        String select = NewsContract.Entry.COLUMN_NAME_FAV + " = ?";
        return new CursorLoader(getActivity(),
                NewsContract.Entry.CONTENT_URI,
                PROJECTION,
                select,
                new String[]{"1"},
                order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        Adapter adapter = new Adapter(cursor);
        adapter.setHasStableIds(true);

        // mRecyclerView.addItemDecoration(new NewsDivider());

        // mRecyclerView.setAdapter(adapter);
        shimmerRecycler.setAdapter(adapter);
        LinearLayoutManager lm =
                new LinearLayoutManager(getContext());
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



    private class Adapter extends RecyclerView.Adapter<FavouriteListActivityFragment.ViewHolder>  {


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
        public FavouriteListActivityFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.layout_news_card, parent, false);

            final FavouriteListActivityFragment.ViewHolder vh = new FavouriteListActivityFragment.ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {




                    if(isConnected) {



                        long rowId = getItemId(vh.getAdapterPosition());


                        Intent feedDetail = new Intent(getContext(), NewsDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("rowId", rowId);
                        feedDetail.putExtras(bundle);
                        startActivity(feedDetail);
                    }else {

                        ConnectionTest.showToastForDuration(getContext(), getString(R.string.offline_text), 5000,
                                Gravity.CENTER);
                    }
                }
            });




            return vh;
        }

        private LayoutInflater getLayoutInflater() {
            return null;
        }

        @Override
        public void onBindViewHolder(final FavouriteListActivityFragment.ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(MyLoader.Query.COLUMN_TITLE));

            holder.pubDate.setText(mCursor.getString(MyLoader.Query.COLUMN_PUB_DATE));
          //  holder.description.setText(mCursor.getString(MyLoader.Query.COLUMN_DESC));
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

//        private void saveOrRemoveFavourite()
//        {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(NewsContract.Entry.COLUMN_NAME_FAV, mfav);
//            Uri uri = Uri.parse(NewsContract.Entry.CONTENT_URI + "/" + newsId);
//            getContentResolver().update(uri, contentValues, null, null);
//
//        }

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
            thumbnailView = (ImageView) view.findViewById(R.id.card_image);
            titleView = (TextView) view.findViewById(R.id.card_title);
        //    description =(TextView) view.findViewById(R.id.card_subtitle);
            pubDate = (TextView) view.findViewById(R.id.card_summary);


        }
    }


}
