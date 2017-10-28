package com.frinder.frinder.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.frinder.frinder.R;
import com.frinder.frinder.fragments.SettingsFragment;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_action_discover:
                startActivity(new Intent(this, DiscoverActivity.class));
                return true;
            case R.id.menu_action_notifications:
                startActivity(new Intent(this, NotificationsActivity.class));
                return true;
            case R.id.menu_action_messages:
                startActivity(new Intent(this, MessagesListActivity.class));
                return true;
            case R.id.menu_action_edit_profile:
                Intent intent = new Intent(this,EditProfileActivity.class);
                intent.putExtra("userId", Profile.getCurrentProfile().getId());
                startActivity(intent);
                return true;
            case R.id.menu_action_settings:
                FragmentManager fm = getSupportFragmentManager();
                SettingsFragment settingsDialogFrament = SettingsFragment.newInstance();
                settingsDialogFrament.show(fm, "fragment_settings");
                return true;
            case R.id.menu_action_logout:
                Toast.makeText(this, "User logged out ", Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
