package com.frinder.frinder.utils;

import android.content.Context;

import com.frinder.frinder.dataaccess.RequestFirebaseDas;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class UnreadRequestsUtils extends UnreadUtils{
    private static final String TAG = "UnreadRequestsUtils";
    private static UnreadRequestsUtils mInstance = null;
    private RequestFirebaseDas mRequestFirebaseDas;

    public interface UnreadRequestsListener extends UnreadListener {
        // Called only when the value changes
        void onUnreadRequestsUpdated(boolean value);

        // Called for every update
        void onUnreadRequestsUpdated();
    }

    // Only the first context is used
    public static UnreadRequestsUtils getInstance(Context applicationContext) {
        if(mInstance == null )
            return new UnreadRequestsUtils(applicationContext);
        return mInstance;
    }

    public UnreadRequestsUtils(Context context) {
        super(context);
        mRequestFirebaseDas = new RequestFirebaseDas(context);
        mRequestFirebaseDas.getUnreadStatus(new RequestFirebaseDas.OnUnreadStatusUpdateListener() {
            @Override
            public void onUnreadStatusUpdated(boolean status) {
                updateInitialStatus(status);
            }
        });
        register();
    }

    @Override
    protected ArrayList<ListenerRegistration> setupRegistrations() {
        return mRequestFirebaseDas.setupUnreadListener(new RequestFirebaseDas.OnUnreadStatusUpdateListener() {
            @Override
            public void onUnreadStatusUpdated(boolean status) {
                updateStatus(status);
            }
        });
    }

    @Override
    protected void removeRegistrations(ArrayList<ListenerRegistration> registrations) {
        mRequestFirebaseDas.removeRegistrations(registrations);
    }

    @Override
    protected void notifyUnreadStatusChange(UnreadListener inListener, boolean status) {
        UnreadRequestsListener listener = (UnreadRequestsListener)inListener;
        listener.onUnreadRequestsUpdated(status);
    }

    @Override
    protected void notifyUpdate(UnreadListener inListener) {
        UnreadRequestsListener listener = (UnreadRequestsListener)inListener;
        listener.onUnreadRequestsUpdated();
    }
}
