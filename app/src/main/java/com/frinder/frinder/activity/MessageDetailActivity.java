package com.frinder.frinder.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageDetailActivity extends BaseActivity {

    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
    private MessageThread mThread;
    private UserFirebaseDas mUserFirebaseDas;
    private MessageFirebaseDas mMessageFirebaseDas;

    @BindView(R.id.rvMessages)
    RecyclerView rvMessages;
    @BindView(R.id.ibSend)
    ImageButton ibSend;
    @BindView(R.id.etSend)
    EditText etSend;
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

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etSend.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    Message message = new Message();
                    message.text = text;
                    message.timestamp = new Date();
                    message.thread = mThread;
                    message.type = Message.Type.TYPE_SENT;
                    mMessages.add(message);
                    mAdapter.notifyItemInserted(mMessages.size() - 1);
                    mMessageFirebaseDas.addMessage(mThread, message, new MessageFirebaseDas.OnMessageSendCompletionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getBaseContext(), "Sending message succeeded", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(getBaseContext(), "Sending message failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
