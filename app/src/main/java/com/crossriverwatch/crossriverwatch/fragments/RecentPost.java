package com.crossriverwatch.crossriverwatch.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.crossriverwatch.crossriverwatch.utility.MyLoader;
import com.crossriverwatch.crossriverwatch.R;
import com.crossriverwatch.crossriverwatch.adapters.RecentPostAdapter;
import com.crossriverwatch.crossriverwatch.services.SyncUtils;
import com.crossriverwatch.crossriverwatch.utility.ConnectionTest;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentPost extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
         {

    private static final String TAG = "RecentPost";
    public Context nContext;
    protected TextView mEmptyView;
    protected SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private ShimmerRecyclerView shimmerRecycler;
    boolean isConnected;
    RecyclerView.LayoutManager layoutManager;
    RecentPostAdapter recentPostAdapter;


    private ProgressDialog mProgress;
    private ContentResolver mContentResolver;


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
       // mSwipeContainer = (SwipeRefreshLayout)view.findViewById(R.id.swipeContainer);
      //  mSwipeContainer.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) getContext());
       // mSwipeContainer.setColorSchemeResources(R.color.colorAccent);
        isConnected = ConnectionTest.isNetworkAvailable(getContext());

        mContentResolver = getActivity().getContentResolver();

        mProgress = new ProgressDialog(getContext());

        getLoaderManager().initLoader(0, null, this);

        loadNews();

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
        recentPostAdapter = new RecentPostAdapter(cursor);
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
        }

        mProgress.dismiss();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        shimmerRecycler.setAdapter(null);
    }

//    @Override
//    public void onRefresh() {
//
//        if (isConnected) {
//
//            SyncUtils.TriggerRefresh();
//            getLoaderManager().restartLoader(0, null, this);
//
//        } else {
//            Toast.makeText(getContext(),getString(R.string.offline_text),Toast.LENGTH_LONG).show();
//           // mSwipeContainer.setRefreshing(false);
//            return;
//        }
//
//    }

    private void loadNews() {

        if (isConnected) {

            mProgress.setMessage("Please Wait..." );
            mProgress.show();
            // progressDelay(6000,mProgress);

            SyncUtils.TriggerRefresh();
           // mSwipeContainer.setRefreshing(false);
        } else {
            Toast.makeText(getContext(),getString(R.string.offline_text),Toast.LENGTH_LONG).show();
           // mSwipeContainer.setRefreshing(false);
            return;
        }
    }

      //       android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener


//    private class Adapter extends RecyclerView.Adapter<ViewHolder>  {
//
//
//        public int mfav;
//        public int newsId;
//
//        private Cursor mCursor ;
//
//
//        public Adapter(Cursor cursor) {
//            mCursor = cursor;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            mCursor.moveToPosition(position);
//            return mCursor.getLong(MyLoader.Query.COLUMN_ID);
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = getLayoutInflater().inflate(R.layout.news_item, parent, false);
//
//            final ViewHolder vh = new ViewHolder(view);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//
//
//
//
//
//
//                    long rowId = getItemId(vh.getAdapterPosition());
//
//
//                    Intent feedDetail = new Intent(getContext(), NewsDetailActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putLong("rowId", rowId);
//
//                    feedDetail.putExtras(bundle);
//                    startActivity(feedDetail);
//
//
//                }
//            });
//
//
//
//
//            return vh;
//        }
//
//
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            mCursor.moveToPosition(position);
//            holder.titleView.setText(mCursor.getString(MyLoader.Query.COLUMN_TITLE));
//
//            holder.pubDate.setText(mCursor.getString(MyLoader.Query.COLUMN_PUB_DATE));
//
//            // holder.category.setText(mCursor.getString(MyLoader.Query.COLUMN_CAT));
//            // holder.description.setText(mCursor.getString(MyLoader.Query.COLUMN_DESC));
//            final String favourite = mCursor.getString(MyLoader.Query.COLUMN_FAV);
//
//
//
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
//            // mfav = Integer.valueOf(favourite);
//            newsId = mCursor.getInt(MyLoader.Query.COLUMN_ID);
//
//        }
//
//
//
//        @Override
//        public int getItemCount() {
//
//
//            return mCursor.getCount();
//        }
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView thumbnailView;
//        public TextView titleView;
//        // public String favourite;
//        // public TextView description;
//        public TextView pubDate;
//        // public TextView category;
//
//
//        public ViewHolder(View view) {
//            super(view);
//            thumbnailView = (ImageView) view.findViewById(R.id.news_image);
//            titleView = (TextView) view.findViewById(R.id.news_title);
//            // description =(TextView) view.findViewById(R.id.card_subtitle);
//            pubDate = (TextView) view.findViewById(R.id.news_date);
//            // category = (TextView) view.findViewById(R.id.cate);
//
//
//        }
//    }



    public void progressDelay(long time, final Dialog d){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }


}
