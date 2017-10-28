package com.frinder.frinder.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.Profile;
import com.frinder.frinder.R;
import com.frinder.frinder.adapters.PlacesAdapter;
import com.frinder.frinder.dataaccess.MessageFirebaseDas;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.Message;
import com.frinder.frinder.model.MessageThread;
import com.frinder.frinder.model.Place;
import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.Constants;
import com.frinder.frinder.utils.FacebookPlacesSearch;
import com.frinder.frinder.views.IncomingMessageViewHolder;
import com.google.firebase.firestore.ListenerRegistration;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageDetailActivity extends BaseActivity {

    private static final String TAG = "MessageDetailActivity";

    private MessageThread mThread;
    private UserFirebaseDas mUserFirebaseDas;
    private MessageFirebaseDas mMessageFirebaseDas;
    private MessagesListAdapter<Message> mAdapter;
    private List<ListenerRegistration> mRegistrations;
    private ArrayList<Message> mMessages;
    private ArrayList<Place> mPlaces;
    private PlacesAdapter mPlaceAdapter;

    @BindView(R.id.mlMessages)
    MessagesList mlMessages;
    @BindView(R.id.miInput)
    MessageInput miInput;
    @BindView(R.id.rvPlaces)
    RecyclerView rvPlaces;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        mThread = (MessageThread)Parcels.unwrap(getIntent().getParcelableExtra(Constants.INTENT_EXTRA_THREAD));

        MessageHolders holders = new MessageHolders();
        holders.setIncomingTextHolder(IncomingMessageViewHolder.class);
        mAdapter = new MessagesListAdapter<>(Profile.getCurrentProfile().getId(), holders, null);
        mlMessages.setAdapter(mAdapter);
        mMessages = new ArrayList<>();

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
        mMessages = new ArrayList<>();
        miInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                addMessage(input.toString());
                return true;
            }
        });

        mPlaces = new ArrayList<>();
        mPlaceAdapter = new PlacesAdapter(this, mPlaces);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPlaces.setLayoutManager(horizontalLayoutManager);
        rvPlaces.setAdapter(mPlaceAdapter);
        setupLocationListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRegistrations = mMessageFirebaseDas.getMessages(mThread, new MessageFirebaseDas.OnCompletionListener() {
            @Override
            public void onMessagesReceived(List<Message> messages) {
                updateOrAddMessages(messages);
            }
        }, new MessageFirebaseDas.OnMessagesUpdateListener() {
            @Override
            public void onUpdateMessages(List<Message> messages) {
                updateOrAddMessages(messages);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMessageFirebaseDas.removeRegistrations(mRegistrations);
    }

    private void addMessage(String text) {
        Message message = createMessage(text);
        mAdapter.addToStart(message, true);
        postMessage(message);
    }

    private Message createMessage(String text) {
        Message message = new Message();
        message.text = text;
        message.timestamp = new Date();
        message.thread = mThread;
        message.type = Message.Type.TYPE_SENT;
        message.senderId = Profile.getCurrentProfile().getId();
        return  message;
    }

    private void postMessage(Message message) {
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

    private void updateOrAddMessages(List<Message> messages) {
        // since we do not support deleting or updating messages, we can skip this step if the count is the same
        if (messages.size() == mMessages.size()) {
            return;
        }
        List<Message> addedMessages = getAddedMessages(messages);
        for (Message message : addedMessages) {
            mAdapter.addToStart(message, true);
            mMessages.add(message);
        }
    }

    private List<Message> getAddedMessages(List<Message> updatedMessages) {
        ArrayList<Message> addedMessages = new ArrayList<>();
        // TODO: Optimize
        for (Message updatedMessage : updatedMessages) {
            // We currently do not support updating messages
            if (!containsMessage(updatedMessage)) {
                addedMessages.add(updatedMessage);
            }
        }
        return addedMessages;
    }

    private boolean containsMessage(Message inMessage) {
        for (Message message : mMessages) {
            if (inMessage.uid != null && inMessage.uid.equals(message.uid)) {
                return true;
            }
        }
        return false;
    }

    private void setupLocationListener() {
        miInput.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                if (rvPlaces.getVisibility() == View.VISIBLE) {
                    rvPlaces.setVisibility(View.GONE);
                } else if (rvPlaces.getVisibility() == View.GONE) {
                    rvPlaces.setVisibility(View.VISIBLE);
                    FacebookPlacesSearch.searchPlaces(MessageDetailActivity.this,
                            new FacebookPlacesSearch.OnCompletionListener() {
                                @Override
                                public void onSuccess(List<Place> places) {
                                    mPlaces.addAll(places);
                                    mPlaceAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure() {
                                    Log.d(TAG, "Querying places failed");
                                }
                            });
                }
            }
        });
    }
}
