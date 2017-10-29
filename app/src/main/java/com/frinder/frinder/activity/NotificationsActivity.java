package com.frinder.frinder.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.frinder.frinder.R;
import com.frinder.frinder.adapters.RequestsFragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsActivity extends BaseActivity {

    @BindView(R.id.tlTabs)
    TabLayout tlTabs;
    @BindView(R.id.vpPager)
    ViewPager vpPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Requests");

        vpPager.setAdapter(new RequestsFragmentPagerAdapter(getSupportFragmentManager(), this));
        tlTabs.setupWithViewPager(vpPager);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_action_notifications).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }
}
