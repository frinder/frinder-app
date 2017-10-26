package com.frinder.frinder.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by sanal on 10/24/17.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private OnConnectivityChangedListener listener;

    public ConnectivityChangeReceiver(OnConnectivityChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeNetInfoWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean isConnected = (activeNetInfoMobile != null && activeNetInfoMobile.isConnectedOrConnecting())
                || (activeNetInfoWifi != null && activeNetInfoWifi.isConnectedOrConnecting());
        listener.onConnectivityChanged(isConnected);
    }

    public interface OnConnectivityChangedListener {
        void onConnectivityChanged(boolean isConnected);
    }
}
