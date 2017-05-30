package com.crossriverwatch.crossriverwatch.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.crossriverwatch.crossriverwatch.utility.MyLoader;
import com.crossriverwatch.crossriverwatch.activities.NewsDetailActivity;
import com.crossriverwatch.crossriverwatch.R;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;


/**
 * Created by ESIDEM jnr on 4/7/2017.
 */

public class RecentPostAdapter extends RecyclerView.Adapter<RecentPostAdapter.ViewHolder> {

    public int mfav;
    public int newsId;

    private Cursor mCursor;

    private Context mContext;


    public RecentPostAdapter(Context context, Cursor cursor) {
        mCursor = cursor;
        this.mContext = context;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(MyLoader.Query.COLUMN_ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        // getLayoutInflater().inflate(R.layout.news_item, parent, false);

        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                long rowId = getItemId(vh.getAdapterPosition());


                Intent feedDetail = new Intent(mContext.getApplicationContext(), NewsDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("rowId", rowId);

                feedDetail.putExtras(bundle);
                mContext.startActivity(feedDetail);


            }
        });


        return vh;
    }




    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.titleView.setText(Html.fromHtml(mCursor.getString(MyLoader.Query.COLUMN_TITLE)));

        holder.pubDate.setText(mCursor.getString(MyLoader.Query.COLUMN_PUB_DATE));

        // holder.category.setText(mCursor.getString(MyLoader.Query.COLUMN_CAT));
        // holder.description.setText(mCursor.getString(MyLoader.Query.COLUMN_DESC));
        final String favourite = mCursor.getString(MyLoader.Query.COLUMN_FAV);


        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(String.valueOf(Html.fromHtml(mCursor.getString(MyLoader.Query.COLUMN_DESC))))
                .setUrl(String.valueOf(Html.fromHtml(mCursor.getString(MyLoader.Query.COLUMN_LINK))))

                .build();

        FirebaseAppIndex.getInstance().update(messageToIndex);



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


    @Override
    public int getItemCount() {


        return mCursor.getCount();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
        // public String favourite;
        // public TextView description;
        public TextView pubDate;
        // public TextView category;


        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.news_image);
            titleView = (TextView) view.findViewById(R.id.news_title);
            // description =(TextView) view.findViewById(R.id.card_subtitle);
            pubDate = (TextView) view.findViewById(R.id.news_date);
            // category = (TextView) view.findViewById(R.id.cate);


        }
    }


}
