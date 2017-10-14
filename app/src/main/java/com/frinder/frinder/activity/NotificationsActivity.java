package com.frinder.frinder.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.frinder.frinder.R;
import com.frinder.frinder.adapters.RequestsFragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsActivity extends AppCompatActivity {

    @BindView(R.id.tlTabs)
    TabLayout tlTabs;
    @BindView(R.id.vpPager)
    ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);

        vpPager.setAdapter(new RequestsFragmentPagerAdapter(getSupportFragmentManager(), this));
        tlTabs.setupWithViewPager(vpPager);
    }
}
