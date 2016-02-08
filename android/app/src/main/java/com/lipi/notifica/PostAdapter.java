package com.lipi.notifica;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Post;
import com.lipi.notifica.database.Profile;
import com.lipi.notifica.database.User;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> mPosts;
    private Context mContext;

    public PostAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    public void setPosts(List<Post> posts) {
        mPosts = posts;
        notifyDataSetChanged();
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
        holder.content.setText(post.body);

        DbHelper helper = new DbHelper(mContext);
        User poster = User.get(User.class, helper, post.posted_by);

        Bitmap avatar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_default_avatar);

        String info= "";
        if (poster != null) {
            Profile posterProfile = Profile.get(Profile.class, helper, poster.profile);
            info = "by " + poster.getName();
            info += ", ";

            if (posterProfile != null && posterProfile.getAvatar() != null)
                avatar = posterProfile.getAvatar();
        }
        info += Utilities.getTimeAgo(post.modified_at);

        holder.avatar.setImageBitmap(avatar);
        ((GradientDrawable)holder.avatar.getBackground()).setColor(Utilities.returnColor(post.posted_by));

        holder.info.setText(info);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView title;
        protected TextView content;
        protected TextView info;
        protected ImageView avatar;

        PostViewHolder(View v){
            super(v);
            v.setOnClickListener(this);
            title = (TextView)v.findViewById(R.id.title);
            content = (TextView)v.findViewById(R.id.content);
            info = (TextView)v.findViewById(R.id.info);
            avatar = (ImageView)v.findViewById(R.id.avatar);
        }
        @Override
        public void onClick(View view) {
            long postId = mPosts.get(getAdapterPosition())._id;
            Intent intent = new Intent(mContext, PostDetailActivity.class);
            intent.putExtra("post_id", postId);
            mContext.startActivity(intent);
        }
    }
}
