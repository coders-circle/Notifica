package com.lipi.notifica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lipi.notifica.database.Comment;
import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Post;
import com.lipi.notifica.database.Profile;
import com.lipi.notifica.database.User;

import java.util.ArrayList;
import java.util.List;

public class PostDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface PostDetailListener {
        void onAdd(String comment);
    }

    private List<Comment> mComments;
    private Post mPost;
    private Context mContext;
    private PostDetailListener mListener;

    private static final int VIEW_DETAIL = 0;
    private static final int VIEW_COMMENT = 1;

    public PostDetailAdapter(Context context, Post post, PostDetailListener listener){
        mContext = context;
        mComments = new ArrayList<>();
        mPost = post;
        mListener = listener;
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mComments.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0? VIEW_DETAIL: VIEW_COMMENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_DETAIL) {
            return new PostDetailViewHolder(LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.layout_post_detail, parent, false));
        }
        else{
            return new CommentViewHolder(LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.layout_comment, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DbHelper helper = new DbHelper(mContext);
        Bitmap avatar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_default_avatar);

        if (position == 0) {
            PostDetailViewHolder pHolder = (PostDetailViewHolder)holder;
            pHolder.title.setText(mPost.title);
            pHolder.content.setText(mPost.body);

            // Get poster info
            User poster = User.get(User.class, helper, mPost.posted_by);

            String info= "";
            if (poster != null) {
                Profile posterProfile = Profile.get(Profile.class, helper, poster.profile);
                info = "by " + poster.getName();
                info += ", ";

                if (posterProfile != null && posterProfile.getAvatar() != null)
                    avatar = posterProfile.getAvatar();
            }

            info += Utilities.getDateTimeString(mPost.modified_at);

            // holder.avatar.setImageBitmap(avatar);
            // ((GradientDrawable)holder.avatar.getBackground()).setColor(PeriodAdapter.returnColor(post.posted_by));

            pHolder.poster_info.setText(info);

        } else {
            CommentViewHolder cHolder = (CommentViewHolder)holder;
            Comment comment = mComments.get(position-1);
            cHolder.comment.setText(comment.body);

            // Get poster info
            User poster = User.get(User.class, helper, comment.posted_by);

            if (poster != null) {
                cHolder.userName.setText(poster.getName());
                Profile posterProfile = Profile.get(Profile.class, helper, poster.profile);

                if (posterProfile != null && posterProfile.getAvatar() != null)
                    avatar = posterProfile.getAvatar();
            }

            cHolder.avatar.setImageBitmap(avatar);
            ((GradientDrawable) cHolder.avatar.getBackground()).setColor(Utilities.returnColor(comment.posted_by));

            cHolder.date.setText(Utilities.getTimeAgo(comment.modified_at));
        }

    }

    public class PostDetailViewHolder extends RecyclerView.ViewHolder{
        protected TextView title;
        protected TextView content;
        protected TextView poster_info;
        protected EditText comment;

        PostDetailViewHolder(View v){
            super(v);
            title = (TextView)v.findViewById(R.id.title);
            content = (TextView)v.findViewById(R.id.content);
            poster_info = (TextView)v.findViewById(R.id.poster_info);
            comment = (EditText)v.findViewById(R.id.input_comment);
            comment.setText("");

            v.findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAdd(comment.getText().toString());
                }
            });
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        protected TextView userName;
        protected TextView date;
        protected TextView comment;
        protected ImageView avatar;

        CommentViewHolder(View v){
            super(v);
            userName = (TextView)v.findViewById(R.id.text_user);
            date = (TextView)v.findViewById(R.id.text_extra);
            comment = (TextView)v.findViewById(R.id.text_comment);
            avatar = (ImageView)v.findViewById(R.id.img_avatar);
        }
    }
}
