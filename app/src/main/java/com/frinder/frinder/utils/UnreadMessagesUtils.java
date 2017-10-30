package com.frinder.frinder.utils;

import android.content.Context;

import com.frinder.frinder.dataaccess.MessageFirebaseDas;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class UnreadMessagesUtils extends UnreadUtils{

    private static final String TAG = "UnreadMessagesUtils";
    private static UnreadMessagesUtils mInstance = null;
    private MessageFirebaseDas mMessageFirebaseDas;

    public interface UnreadMessagesListener extends UnreadListener {
        void onUnreadMessagesUpdated(boolean value);
    }

    // Only the first context is used
    public static UnreadMessagesUtils getInstance(Context applicationContext) {
        if(mInstance == null )
            return new UnreadMessagesUtils(applicationContext);
        return mInstance;
    }

    public UnreadMessagesUtils(Context context) {
        super(context);
        mMessageFirebaseDas = new MessageFirebaseDas(context);
        mMessageFirebaseDas.getUnreadStatus(new MessageFirebaseDas.OnUnreadStatusUpdateListener() {
            @Override
            public void onUnreadStatusUpdated(boolean status) {
                updateInitialStatus(status);
            }
        });
        register();
    }

    @Override
    protected ArrayList<ListenerRegistration> setupRegistrations() {
        return mMessageFirebaseDas.setupUnreadListener(new MessageFirebaseDas.OnUnreadStatusUpdateListener() {
            @Override
            public void onUnreadStatusUpdated(boolean status) {
                updateStatus(status);
            }
        });
    }

    @Override
    protected void removeRegistrations(ArrayList<ListenerRegistration> registrations) {
        mMessageFirebaseDas.removeRegistrations(registrations);
    }

    @Override
    protected void notifyUnreadStatusChange(UnreadListener inListener, boolean status) {
        UnreadMessagesListener listener = (UnreadMessagesListener)inListener;
        listener.onUnreadMessagesUpdated(status);
    }

    @Override
    protected void notifyUpdate(UnreadListener inListener) {
        // Ignore
    }

}
