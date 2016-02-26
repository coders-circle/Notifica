package com.toggle.notifica;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.toggle.notifica.database.Client;
import com.toggle.notifica.database.Comment;
import com.toggle.notifica.database.DbHelper;
import com.toggle.notifica.database.Post;

import java.util.List;

public class PostDetailActivity extends AppCompatActivity{
    private DbHelper mDbHelper;
    private PostDetailAdapter mAdapter;
    private long mPostId;
    private Client mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_post_detail);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_post_details);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);

        mDbHelper = new DbHelper(this);
        mClient = new Client(this);

        Bundle extras = getIntent().getExtras();
        mPostId = extras.getLong("post_id");
        Post post = Post.get(Post.class, mDbHelper, mPostId);

        mAdapter = new PostDetailAdapter(this, post, new PostDetailAdapter.PostDetailListener() {
            @Override
            public void onAdd(String comment) {
                mClient.postComment(comment, mPostId, new Client.ClientListener() {
                    @Override
                    public void refresh() {
                        refreshComments();

                        // TODO: Check for error

                        EditText commentInput =  ((EditText)recyclerView.findViewById(R.id.input_comment));
                        commentInput.setText("");
                        commentInput.clearFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(commentInput.getWindowToken(), 0);
                    }
                });
            }
        });
        recyclerView.setAdapter(mAdapter);

        mClient.getComments(mPostId, new Client.ClientListener() {
            @Override
            public void refresh() {
                refreshComments();
            }
        });
    }

    private void refreshComments() {
        List<Comment> comments = Comment.query(Comment.class,
                mDbHelper, "post=?", new String[]{"" + mPostId}, null, null, "modified_at DESC");
        mAdapter.setComments(comments);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
