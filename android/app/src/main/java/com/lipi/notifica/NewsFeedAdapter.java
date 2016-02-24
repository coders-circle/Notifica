package com.lipi.notifica;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Period;
import com.lipi.notifica.database.Post;
import com.lipi.notifica.database.Profile;
import com.lipi.notifica.database.Subject;
import com.lipi.notifica.database.User;

import java.util.Calendar;
import java.util.List;

public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Post> mPosts;
    private Context mContext;

    public NewsFeedAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    public void setPosts(List<Post> posts) {
        mPosts = posts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPosts.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;   // header
        else
            return 1;   // posts
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new HeaderViewHolder(LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.layout_feed_header, parent, false));
        else
            return new PostViewHolder(LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.layout_post, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        // Header
        if (position == 0)
            fillHeader((HeaderViewHolder) viewHolder);
        // Posts
        else
            fillPosts((PostViewHolder) viewHolder, position-1);
    }

    public void fillHeader(HeaderViewHolder holder) {
        DbHelper dbHelper = new DbHelper(mContext);
        Calendar cal = Calendar.getInstance();

        // Get current time and day of week
        int currentTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;

        // Get current and next periods
        Period current = Period.get(Period.class, dbHelper, "start_time<=? AND end_time>? AND day=?", new String[]{"" + currentTime, "" + currentTime, "" + day}, "start_time");
        Period next = Period.get(Period.class, dbHelper, "start_time>? AND day=?", new String[]{"" + currentTime, "" + day}, "start_time");

        // If next period isn't today, get tomorrow's period and so on
        int count = 0;
        while (next == null && count < 7) {
            day = (day + 1) % 7;
            next = Period.get(Period.class, dbHelper, "day=?", new String[]{"" + day}, "start_time");
            count++;
        }

        int remaining;

        if (next != null) {
            // Find remaining time to next period
            remaining = next.start_time - currentTime;
            if (count > 0)
                remaining = 24 * 60 - currentTime + (count - 1) * 24 + next.start_time;

            Subject subject = Subject.get(Subject.class, dbHelper, next.subject);

            // Show current period if exists
            if (current != null) {
                Subject sub = Subject.get(Subject.class, dbHelper, current.subject);
                String text = "Current: " + sub.name + "\n" + current.getStartTime() + " - " + current.getEndTime();

                holder.textView1.setVisibility(View.VISIBLE);
                holder.textView1.setText(text);
            } else
                holder.textView1.setVisibility(View.GONE);

            // Show next period
            String text = "Next: " + subject.name + " in " + Utilities.formatMinutes(remaining) + "\n(";

            if (count == 1)
                text += "Tomorrow ";
            else if (count > 1) {
                text += DbHelper.DAYS[day] + " ";
            }

            text += next.getStartTime() + " - " + next.getEndTime() + ")";
            holder.textView2.setText(text);
        }

        int nextMinute = (60-cal.get(Calendar.SECOND))*1000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, nextMinute);
    }

    public void fillPosts(PostViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.title.setText(post.title);
        holder.content.setText(post.body);

        DbHelper helper = new DbHelper(mContext);
        User poster = User.get(User.class, helper, post.posted_by);
        Bitmap avatar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_default_avatar);

        String info = "";
        if (poster != null) {
            Profile posterProfile = Profile.get(Profile.class, helper, poster.profile);
            info = "by " + poster.getName() + ", ";

            if (posterProfile != null && posterProfile.getAvatar() != null)
                avatar = posterProfile.getAvatar();
        }
        info += Utilities.getTimeAgo(post.modified_at);

        holder.avatar.setImageBitmap(avatar);
        ((GradientDrawable) holder.avatar.getBackground()).setColor(Utilities.returnColor(post.posted_by));

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
            long postId = mPosts.get(getAdapterPosition()-1)._id;
            Intent intent = new Intent(mContext, PostDetailActivity.class);
            intent.putExtra("post_id", postId);
            mContext.startActivity(intent);
        }
    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView1;
        protected TextView textView2;

        HeaderViewHolder(View v){
            super(v);

            textView1 = (TextView)v.findViewById(R.id.currentPeriod);
            textView2 = (TextView)v.findViewById(R.id.nextPeriod);
        }
    }
}
