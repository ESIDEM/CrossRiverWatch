package com.crossriverwatch.crossriverwatch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crossriverwatch.crossriverwatch.R;
import com.crossriverwatch.crossriverwatch.parser.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ESIDEM jnr on 4/7/2017.
 */

public class CategoryPostAdapter extends RecyclerView.Adapter<CategoryPostAdapter.ViewHolder>{

    // A list of posts
    private List<Post> posts;
    private Context mContext;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    public CategoryPostAdapter(ArrayList<Post> posts, OnItemClickListener listener) {
        this.posts = posts;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.news_item, viewGroup, false);
        mContext = viewGroup.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        Glide.with(mContext)
                .load(posts.get(i).getThumbnailUrl())
                .centerCrop()
                .into(viewHolder.thumbnailImageView);

        viewHolder.title.setText(Html.fromHtml(posts.get(i).getTitle()));

        viewHolder.dateView.setText(posts.get(i).getDate());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(posts.get(i));

//                String url =
//
//                Intent feedDetail = new Intent(mContext.getApplicationContext(), CategoryNewsDetail.class);
//                Bundle bundle = new Bundle();
//                bundle.putLong("rowId", rowId);
//
//                feedDetail.putExtras(bundle);
//                mContext.startActivity(feedDetail);

                
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView title;
        TextView dateView;

        public ViewHolder(View itemView) {
            super(itemView);

            thumbnailImageView = (ImageView) itemView.findViewById(R.id.news_image);
            title = (TextView) itemView.findViewById(R.id.news_title);
            dateView = (TextView) itemView.findViewById(R.id.news_date);
        }

    }
}
