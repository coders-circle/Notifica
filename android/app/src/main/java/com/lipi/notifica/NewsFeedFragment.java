package com.lipi.notifica;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private PostAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Post> mPosts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPosts = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_posts);
        recyclerView.addItemDecoration(new PostDivider(rootView.getContext()));
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new PostAdapter(getActivity(), mPosts);
        recyclerView.setAdapter(mAdapter);

        getPosts();

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_posts);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });
        return rootView;
    }

    // fetch posts from the server
    private void getPosts() {
        // get from cache and show them
        final DbHelper helper = new DbHelper(getContext());
        changeData(Post.getAll(Post.class, helper, "modified_at DESC"));
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // get recent ones as well
                Client client = new Client(getContext());
                client.getPosts(-1, 30, -1, new Client.ClientListener() {
                    @Override
                    public void refresh() {
                        changeData(Post.getAll(Post.class, helper, "modified_at DESC"));
                        refreshView();
                    }
                });

                /*long time = 0;
                if (mPosts.size() > 0)
                    time = mPosts.get(0).modified_at;
                client.getPosts(-1, -1, time...*/
            }
        }).run();
    }

    public void changeData(List<Post> newPosts) {
        mPosts = newPosts;

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
            mDivider = context.getResources().getDrawable(R.drawable.divider_post);
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
