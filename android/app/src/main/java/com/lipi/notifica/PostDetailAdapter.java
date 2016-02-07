package com.lipi.notifica;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lipi.notifica.database.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhx on 2/7/16.
 */
public class PostDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Comment> mComments;
    private Context mContext;

    private static final int VIEW_DETAIL = 0;
    private static final int VIEW_COMMENT = 1;

    public PostDetailAdapter(Context context){
        mContext = context;
        mComments = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
//        return mComments.size();
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0? VIEW_DETAIL: VIEW_COMMENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_DETAIL) {
            return new CommentViewHolder(LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.layout_post_detail, parent, false));
        }
        else{
            return new PostDetailViewHolder(LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.layout_comment, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        Comment comment = mComments.get(position);
//        DbHelper helper = new DbHelper(mContext);
//        User poster = User.get(User.class, helper, comment.posted_by);
//        Profile posterProfile= Profile.get(Profile.class, helper, poster.profile);
//        holder.userName.setText(poster.first_name + " " + poster.last_name);
//        holder.comment.setText(comment.body);
//
//        // TODO: bibek, tyo getAvatar() ley avatar chaina bhaney ic_default_avatar aauney banau hai
//        holder.avatar.setImageBitmap(posterProfile.getAvatar());

    }

    public class PostDetailViewHolder extends RecyclerView.ViewHolder{
        protected TextView title;
        PostDetailViewHolder(View v){
            super(v);
            title = (TextView)v.findViewById(R.id.title);
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
