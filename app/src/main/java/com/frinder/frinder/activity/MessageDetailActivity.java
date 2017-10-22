package com.frinder.frinder.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.frinder.frinder.R;
import com.frinder.frinder.adapters.MessagesAdapter;
import com.frinder.frinder.dataaccess.MessageFirebaseDas;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.Message;
import com.frinder.frinder.model.MessageThread;
import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.Constants;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageDetailActivity extends AppCompatActivity {

    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
    private MessageThread mThread;
    private UserFirebaseDas mUserFirebaseDas;
    private MessageFirebaseDas mMessageFirebaseDas;

    @BindView(R.id.rvMessages)
    RecyclerView rvMessages;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mThread = (MessageThread)Parcels.unwrap(getIntent().getParcelableExtra(Constants.INTENT_EXTRA_THREAD));
        mMessages = new ArrayList<>();
        mAdapter = new MessagesAdapter(this, mMessages);
        rvMessages.setAdapter(mAdapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Set user's name as title
        mUserFirebaseDas = new UserFirebaseDas(this);
        mUserFirebaseDas.getUser(mThread.userId, new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                if (user != null) {
                    getSupportActionBar().setTitle(user.getName());
                }
            }
        });


        mMessageFirebaseDas = new MessageFirebaseDas(this);
        mMessageFirebaseDas.getMessages(mThread, new MessageFirebaseDas.OnCompletionListener() {
            @Override
            public void onMessagesReceived(ArrayList<Message> messages) {
                mMessages.clear();
                mMessages.addAll(messages);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
