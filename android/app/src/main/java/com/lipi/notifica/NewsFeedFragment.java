package com.lipi.notifica;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lipi.notifica.database.Client;
import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Post;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFragment extends Fragment {
    private NewsFeedAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Post> mPosts = new ArrayList<>();
    private DbHelper mDbHelper;
    private long mProfileId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null)
            mProfileId = getArguments().getLong("profile_id", -1);

        mDbHelper = new DbHelper(getContext());

        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_posts);
        recyclerView.addItemDecoration(new PostDivider(rootView.getContext()));
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new NewsFeedAdapter(getActivity(), mPosts);
        recyclerView.setAdapter(mAdapter);

        getPosts();

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_posts);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });

        recyclerView.addOnScrollListener(new VerticalScrollListener() {
            @Override
            public void onScrolledUp() {
            }

            @Override
            public void onScrolledDown() {
            }

            @Override
            public void onScrolledToTop() {
            }

            @Override
            public void onScrolledToBottom() {
                loadMorePosts();
            }
        });
        return rootView;
    }

    // Fetch posts from the server
    private void getPosts() {

        // First get from cache and show them
        changeData();
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Then get 5 recent ones from the server as well
                Client client = new Client(getContext());
                client.getPosts(-1, 5, -1, mProfileId, new Client.ClientListener() {
                    @Override
                    public void refresh() {
                        changeData();
                        refreshView();
                    }
                });
            }
        }).run();
    }

    private void loadMorePosts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get 5 more posts from the server
                Client client = new Client(getContext());
                client.getPosts(mPosts.size(), 5, -1, mProfileId, new Client.ClientListener() {
                    @Override
                    public void refresh() {
                        changeData();
                        refreshView();
                    }
                });
            }
        }).run();
    }

    public void changeData() {
        if (mProfileId >= 0) {
            mPosts = Post.query(Post.class, mDbHelper, "profile=?", new String[]{mProfileId+""}, null, null, "modified_at DESC");
        } else
            mPosts = Post.getAll(Post.class, mDbHelper, "modified_at DESC");

        if(mAdapter != null) {
            mAdapter.setPosts(mPosts);
        }
    }

    public void refreshView(){
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public class PostDivider extends RecyclerView.ItemDecoration{
        private Drawable mDivider;

        public PostDivider(Context context){
            mDivider = ContextCompat.getDrawable(context, R.drawable.divider_post);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if( parent.getChildCount() < 2 ){
                return;
            }
            int left = parent.getPaddingLeft()+20;
            int right = parent.getWidth() - parent.getPaddingRight()-20;
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount-1; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
