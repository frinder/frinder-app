package com.frinder.frinder.dataaccess;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.Profile;
import com.frinder.frinder.model.Message;
import com.frinder.frinder.model.MessageThread;
import com.frinder.frinder.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFirebaseDas {

    FirebaseFirestore db;
    private static final String TAG = "MessageFirebaseDas";
    private Context context;

    public MessageFirebaseDas(Context context) {
        this.context = context;
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();
    }

    public void getOrCreateThread(final String inUser1, final String inUser2, final OnCompletionListener listener) {
        getDocument(inUser1, inUser2).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists() && document.getData() != null) {
                        MessageThread thread = convertThreadFromFirebaseObject(document.getId(), document.getData());
                        listener.onThreadReceived(thread);
                        return;
                    } else {
                        Log.d(TAG, "GetThread: No such document");
                    }
                } else {
                    Log.d(TAG, "GetThread: get failed with ", task.getException());
                }
                createThread(inUser1, inUser2, listener);
            }
        });
    }

    private void createThread(String inUser1, String inUser2, final OnCompletionListener listener) {
        // TODO: This code can race between users (fix this)
        HashMap<String, String> map = new HashMap<>();
        final String user1, user2;
        if (inUser1.compareTo(inUser2) > 0) {
            user1 = inUser2;
            user2 = inUser1;
        } else {
            user1 = inUser1;
            user2 = inUser2;
        }
        map.put(Constants.THREAD_COLUMN_USER1, user1);
        map.put(Constants.THREAD_COLUMN_USER2, user2);
        final String threadId = getDocumentId(inUser1, inUser2);
        getDocument(threadId)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "LocationUpdate: DocumentSnapshot successfully updated!");
                        MessageThread thread = new MessageThread();
                        thread.uid = threadId;
                        thread.userId = getThreadUserId(user1, user2);
                        listener.onThreadReceived(thread);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "LocationUpdate: Error updating document", e);
                        listener.onThreadReceived(null);
                    }
                });
    }

    public void getThreads(final OnCompletionListener listener) {
        String loggedInUserId = Profile.getCurrentProfile().getId();
        OnCompletionListener inListener = new OnCompletionListener() {

            private boolean mFirstReceived = false;
            private ArrayList<MessageThread> mThreads;

            @Override
            public void onThreadsReceived(ArrayList<MessageThread> threads) {
                // TODO: Can this be called from different thread? If so, add synchronization logic
                if (!mFirstReceived) {
                    mThreads = new ArrayList<>(threads);
                    mFirstReceived = true;
                } else {
                    mThreads.addAll(threads);
                    Collections.sort(mThreads);
                    listener.onThreadsReceived(mThreads);
                }
            }
        };

        Query query1 = getCollection().whereEqualTo(Constants.THREAD_COLUMN_USER1, loggedInUserId);
        Query query2 = getCollection().whereEqualTo(Constants.THREAD_COLUMN_USER2, loggedInUserId);
        setupThreadCompletionListeners(query1, inListener);
        setupThreadCompletionListeners(query2, inListener);
    }

    public void getMessages(final MessageThread thread, final OnCompletionListener listener) {
        getDocument(thread.uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "GetUser: DocumentSnapshot data: " + task.getResult().getData());
                        if (task.getResult() != null && task.getResult().getData() != null) {
                            listener.onMessagesReceived(parseMessageList(document, thread));
                            return;
                        }
                    } else {
                        Log.d(TAG, "GetUser: No such document");
                    }
                } else {
                    Log.d(TAG, "GetUser: get failed with ", task.getException());
                }
                listener.onMessagesReceived(null);
            }
        });
    }

    public void addMessage(final MessageThread thread,
                           final Message message,
                           final OnMessageSendCompletionListener inListener) {
        getMessages(thread, new OnCompletionListener() {
            @Override
            public void onMessagesReceived(ArrayList<Message> inMessages) {
                ArrayList<Message> messages = new ArrayList<Message>();
                messages.addAll(inMessages);
                messages.add(message);
                List fMessages = convertMessageListToFirebaseObjectList(thread, messages);
                Map<String,Object> threadData = new HashMap<>();
                threadData.put(Constants.THREAD_COLUMN_MESSAGES, fMessages);
                threadData.put(Constants.THREAD_COLUMN_LAST_MESSAGE, message.text);
                threadData.put(Constants.THREAD_COLUMN_LAST_TIMESTAMP, message.timestamp);
                threadData.put(Constants.THREAD_COLUMN_LAST_SENDERID, message);
                threadData.put(Constants.THREAD_COLUMN_UNREAD, true);
                getDocument(thread.uid)
                        .update(threadData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                inListener.onSuccess();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                inListener.onFailure();
                            }
                        });
            }
        });
    }

    private void setupThreadCompletionListeners(Query query, final OnCompletionListener listener) {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<MessageThread> threadList = parseThreadList(task.getResult());
                    listener.onThreadsReceived(threadList);
                    return;
                }

                Log.d(TAG, "Error getting documents: ", task.getException());
                listener.onThreadsReceived(null);
            }
        });
    }

    public void updateUnread(String threadId, boolean unread) {
        DocumentReference document = getDocument(threadId);
        if (document == null) {
            Log.w(TAG, "Cannot find request to be updated");
            return;
        }
        Task task  = document.update(Constants.THREAD_COLUMN_UNREAD , unread);
        setListeners(task);
    }

    private CollectionReference getCollection() {
        return db.collection("messages");
    }

    private DocumentReference getDocument(String user1, String user2) {
        return getCollection().document(getDocumentId(user1, user2));
    }

    private DocumentReference getDocument(String threadId) {
        return getCollection().document(threadId);
    }

    private String getDocumentId(String user1, String user2) {
        if (user1 == null || user2 == null) {
            throw new RuntimeException("Null user strings");
        }

        int comparison = user1.compareTo(user2);
        if (comparison == 0) {
            throw new RuntimeException("Equal user strings");
        }

        return (comparison < 0) ? user1 + "_" + user2 : user2 + "_" + user1;
    }

    private void setListeners(Task task) {
        // TODO: Show error messages based on this
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }

    public static class OnCompletionListener {
        public void onThreadReceived(MessageThread thread) {
            // override if required
        }

        public void onThreadsReceived(ArrayList<MessageThread> threads) {
            // override if required
        }

        public void onMessagesReceived(ArrayList<Message> messages) {
            // override if required
        }
    }

    public abstract static class OnMessageSendCompletionListener {
        public abstract void onSuccess();
        public abstract void onFailure();
    }

    private static MessageThread convertThreadFromFirebaseObject(String id, Map<String, Object> threadMap) {
        MessageThread thread = new MessageThread();
        thread.uid = id;
        thread.unread = threadMap.containsKey(Constants.THREAD_COLUMN_UNREAD) ?
                (boolean)threadMap.get(Constants.THREAD_COLUMN_UNREAD) : false;
        thread.lastTimestamp = threadMap.containsKey(Constants.THREAD_COLUMN_LAST_TIMESTAMP) ?
                (Date)threadMap.get(Constants.THREAD_COLUMN_LAST_TIMESTAMP) : null;
        String user1 = (String)threadMap.get(Constants.THREAD_COLUMN_USER1);
        String user2 = (String)threadMap.get(Constants.THREAD_COLUMN_USER2);
        thread.userId = getThreadUserId(user1, user2);
        if (thread.userId == null) {
            Log.i(TAG, "Message with inconsistent user ids");
            return null;
        }
        thread.messageSnippet = threadMap.containsKey(Constants.THREAD_COLUMN_LAST_MESSAGE) ?
                (String)threadMap.get(Constants.THREAD_COLUMN_LAST_MESSAGE) : null;
        thread.lastSenderId = threadMap.containsKey(Constants.THREAD_COLUMN_LAST_SENDERID) ?
                (String)threadMap.get(Constants.THREAD_COLUMN_LAST_SENDERID) : null;
        return thread;
    }

    private static String getThreadUserId(String user1, String user2) {
        String loggedInUserId = Profile.getCurrentProfile().getId();
        if (!user1.equals(loggedInUserId) && user2.equals(loggedInUserId)) {
            return user1;
        } else if (!user2.equals(loggedInUserId) && user1.equals(loggedInUserId)) {
            return user2;
        } else {
            return null;
        }
    }

    private static Message convertMessageFromFirebaseObject(MessageThread thread, Map<String, Object> messageMap) {
        Message message = new Message();
        message.thread = thread;
        message.timestamp = (Date)messageMap.get(Constants.MESSAGE_COLUMN_TIMESTAMP);
        message.text = (String)messageMap.get(Constants.MESSAGE_COLUMN_TEXT);
        message.senderId = (String)messageMap.get(Constants.MESSAGE_COLUMN_SENDERID);
        message.type = thread.userId.equals(message.senderId) ? Message.Type.TYPE_RECEIVED : Message.Type.TYPE_SENT;

        return message;
    }

    @NonNull
    private Map<String, Object> convertMessageToFirebaseObject(MessageThread thread, Message message) {
        Map<String, Object> msg = new HashMap<>();
        msg.put(Constants.MESSAGE_COLUMN_TIMESTAMP, message.timestamp);
        msg.put(Constants.MESSAGE_COLUMN_TEXT, message.text);
        switch (message.type) {
            case TYPE_RECEIVED:
                msg.put(Constants.MESSAGE_COLUMN_SENDERID, thread.userId);
                break;
            default:
                msg.put(Constants.MESSAGE_COLUMN_SENDERID, Profile.getCurrentProfile().getId());
                break;
        }
        return msg;
    }

    @NonNull
    private List<Map> convertMessageListToFirebaseObjectList(MessageThread thread, List<Message> inMessages) {
        ArrayList<Map> messages = new ArrayList<>();
        for (Message message : inMessages) {
            messages.add(convertMessageToFirebaseObject(thread, message));
        }
        return messages;
    }

    @NonNull
    private static ArrayList<MessageThread> parseThreadList(QuerySnapshot snapshot) {
        ArrayList<MessageThread> threadList = new ArrayList<>();
        for (DocumentSnapshot document : snapshot) {
            if (document.exists()) {
                MessageThread thread = convertThreadFromFirebaseObject(document.getId(), document.getData());
                threadList.add(thread);
            }
            else {
                Log.d(TAG, "No such document");
            }
        }
        return threadList;
    }

        @NonNull
        private static ArrayList<Message> parseMessageList(DocumentSnapshot snapshot, MessageThread thread) {
            ArrayList<Message> messageList = new ArrayList<>();
            List<HashMap> messages = (List<HashMap>)snapshot.get(Constants.THREAD_COLUMN_MESSAGES);
            for (HashMap data : messages) {
                Message message = convertMessageFromFirebaseObject(thread, data);
                messageList.add(message);
            }
            return messageList;
        }
}
