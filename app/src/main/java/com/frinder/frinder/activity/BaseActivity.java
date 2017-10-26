package com.frinder.frinder.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.frinder.frinder.R;
import com.frinder.frinder.utils.ConnectivityChangeReceiver;

public class BaseActivity extends AppCompatActivity implements ConnectivityChangeReceiver.OnConnectivityChangedListener{

    private ConnectivityChangeReceiver connectivityChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityChangeReceiver = new ConnectivityChangeReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityChangeReceiver, filter);
    }

    @Override
    public void onConnectivityChanged(boolean isConnected) {
        if(!isConnected) {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, R.string.snackbar_no_internet_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_no_internet_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishAffinity();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityChangeReceiver);
    }
}
