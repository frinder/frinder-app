package com.frinder.frinder.utils;

import android.content.Context;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashSet;

public abstract  class UnreadUtils {
    private static final String TAG = "UnreadUtils";
    private static UnreadRequestsUtils mInstance = null;

    private HashSet<UnreadListener> mListeners;
    private boolean mStatus = false;
    private Context mContext;
    private ArrayList<ListenerRegistration> mRegistrations;

    protected interface UnreadListener {
        void onUnreadRequestsUpdated(boolean value);
    }

    public UnreadUtils(Context context) {
        mContext = context;
        mListeners = new HashSet<>();
    }

    public void addListener(UnreadListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(UnreadListener listener) {
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
            mRegistrations = setupRegistrations();
        }
    }

    // TODO: Figure out when to call this
    public void unregister() {
        if (isRegistered()) {
            removeRegistrations(mRegistrations);
            mRegistrations = null;
        }
    }

    protected abstract ArrayList<ListenerRegistration> setupRegistrations();
    protected abstract void removeRegistrations(ArrayList<ListenerRegistration> registrations);
    protected abstract void notifyUnreadStatusChange(UnreadListener listener, boolean status);
    protected abstract void notifyUpdate(UnreadListener listener);

    protected Context getContext() {
        return mContext;
    }

    protected void updateInitialStatus(boolean status) {
        if (status != mStatus) {
            mStatus = status;
            notifyUnreadStatusChange(status);
        }
    }

    protected void updateStatus(boolean status) {
        updateInitialStatus(status);
        notifyUpdate();
    }

    private void notifyUnreadStatusChange(boolean status) {
        for (UnreadListener listener : mListeners) {
            notifyUnreadStatusChange(listener, status);
        }
    }

    private void notifyUpdate() {
        for (UnreadListener listener : mListeners) {
            notifyUpdate(listener);
        }
    }
}
