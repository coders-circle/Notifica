package com.toggle.notifica;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toggle.notifica.database.Client;
import com.toggle.notifica.database.DbHelper;
import com.toggle.notifica.database.PClass;
import com.toggle.notifica.database.Post;
import com.toggle.notifica.database.User;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFragment extends Fragment {
    private NewsFeedAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Post> mPosts = new ArrayList<>();
    private DbHelper mDbHelper;
    private long mProfileId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
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

        final PClass pClass = ((MainActivity)getActivity()).getPClass();

        FloatingActionButton addPostButton =
                (FloatingActionButton) rootView.findViewById(R.id.addPostButton);
        if (!pClass.checkIfAdmin(mDbHelper, User.getLoggedInUser(getContext())._id))
            addPostButton.setVisibility(View.GONE);
        else
            addPostButton.setVisibility(View.VISIBLE);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(R.layout.layout_add_post);
                builder.setPositiveButton("Post", null);
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                // Create the AlertDialog object and show it
                final AlertDialog dialog = builder.create();
                dialog.show();


                // We need to override onClick listener for OK some other way so that
                // we do not always need to close the dialog

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Post new content
                                if (pClass != null) {
                                    TextView titleTV = ((TextView) dialog.findViewById(R.id.title));
                                    TextView contentTV = ((TextView) dialog
                                            .findViewById(R.id.description));

                                    String title = titleTV.getText().toString();
                                    String content = contentTV.getText().toString();

                                    if (title.equals("")) {
                                        titleTV.setError("You must enter a title");
                                        return;
                                    }

                                    if (content.equals("")) {
                                        contentTV.setError("You must enter content for the post");
                                        return;
                                    }

                                    Client client = new Client(getContext());
                                    client.postPost(title, content, pClass.profile,
                                            new Client.ClientListener() {
                                                @Override
                                                public void refresh() {
                                                    // Dismiss only when posted
                                                    dialog.dismiss();
                                                    getPosts();
                                                }
                                            });
                                }
                            }
                        }
                );
            }
        });

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
