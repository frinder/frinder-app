package com.frinder.frinder.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.frinder.frinder.R;
import com.frinder.frinder.fragments.NoRequestsFragment;
import com.frinder.frinder.fragments.RequestFragment;

public class RequestsFragmentPagerAdapter extends FragmentPagerAdapter {

    private String mTabTitles[];
    private Context mContext;

    public RequestsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        Resources resources = context.getResources();
        mTabTitles = new String[] {
                resources.getString(R.string.title_incoming),
                resources.getString(R.string.title_outgoing),
                resources.getString(R.string.title_accepted)
        };
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RequestFragment.newInstance();
            default:
                // TODO: Change this
                return NoRequestsFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return mTabTitles[position];
    }
}
