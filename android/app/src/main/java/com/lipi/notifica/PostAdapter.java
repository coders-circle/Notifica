package com.lipi.notifica;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fhx on 1/8/16.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> mPosts;
    private Context mContext;

    public PostAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.layout_post, parent, false));
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.title.setText(post.title);
        holder.content.setText(post.content);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView title;
        protected TextView content;
        PostViewHolder(View v){
            super(v);
            v.setOnClickListener(this);
            title = (TextView)v.findViewById(R.id.title);
            content = (TextView)v.findViewById(R.id.content);
        }
        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
        }
    }
}
