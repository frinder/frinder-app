package com.frinder.frinder.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.frinder.frinder.R;
import com.frinder.frinder.adapters.ThreadsAdapter;
import com.frinder.frinder.dataaccess.MessageFirebaseDas;
import com.frinder.frinder.model.MessageThread;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesListActivity extends BaseActivity {

    private ArrayList<MessageThread> mThreads;
    private ThreadsAdapter mAdapter;
    private MessageFirebaseDas mMessageFirebaseDas;

    @BindView(R.id.rvThreads)
    RecyclerView rvThreads;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");

        mThreads = new ArrayList<>();
        mAdapter = new ThreadsAdapter(this, mThreads);
        rvThreads.setAdapter(mAdapter);
        rvThreads.setLayoutManager(new LinearLayoutManager(this));

        mMessageFirebaseDas = new MessageFirebaseDas(this);
        mMessageFirebaseDas.getThreads(new MessageFirebaseDas.OnCompletionListener() {
            public void onThreadsReceived(ArrayList<MessageThread> threads) {
                mThreads.clear();
                mThreads.addAll(threads);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
