package com.frinder.frinder.utils;

import android.content.Context;

import com.frinder.frinder.dataaccess.RequestFirebaseDas;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashSet;

public class UnreadRequestsUtils {
    private static final String TAG = "UnreadRequestsUtils";
    private static UnreadRequestsUtils mInstance = null;

    private HashSet<UnreadRequestsListener> mListeners;
    private boolean mStatus = false;
    private Context mContext;
    private ArrayList<ListenerRegistration> mRegistrations;
    private RequestFirebaseDas mRequestFirebaseDas;

    public interface UnreadRequestsListener {
        void onUnreadRequestsUpdated(boolean value);
    }

    // Only the first context is used
    public static UnreadRequestsUtils getInstance(Context applicationContext) {
        if(mInstance == null )
            return new UnreadRequestsUtils(applicationContext);
        return mInstance;
    }

    public UnreadRequestsUtils(Context context) {
        mContext = context;
        mListeners = new HashSet<>();
        mRequestFirebaseDas = new RequestFirebaseDas(mContext);
        mRequestFirebaseDas.getUnreadStatus(new RequestFirebaseDas.OnUnreadStatusUpdateListener() {
            @Override
            public void onUnreadStatusUpdated(boolean status) {
                updateStatus(status);
            }
        });
        register();
    }

    public void addListener(UnreadRequestsListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(UnreadRequestsListener listener) {
        mListeners.remove(listener);
    }

    public boolean getUnreadStatus() {
        return mStatus;
    }

    public boolean isRegistered() {
        return mRegistrations != null;
    }

    public void register() {
        if (!isRegistered()) {
            mRegistrations = mRequestFirebaseDas.setupUnreadListener(new RequestFirebaseDas.OnUnreadStatusUpdateListener() {
                @Override
                public void onUnreadStatusUpdated(boolean status) {
                    updateStatus(status);
                }
            });
        }
    }

    // TODO: Figure out when to call this
    public void unregister() {
        if (isRegistered()) {
            mRequestFirebaseDas.removeRegistrations(mRegistrations);
            mRegistrations = null;
        }
    }

    private void updateStatus(boolean status) {
        if (status != mStatus) {
            mStatus = status;
            notifyStatusChange(status);
        }
    }

    private void notifyStatusChange(boolean status) {
        for (UnreadRequestsListener listener : mListeners) {
            listener.onUnreadRequestsUpdated(status);
        }
    }
}
