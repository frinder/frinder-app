package com.frinder.frinder.model;

import android.content.res.TypedArray;

import java.util.ArrayList;

/**
 * Created by mallikaviswas on 10/22/17.
 */

public class Interest {
    public final static String TAG = "Interest";

    int mIcon;
    int mColor;
    String mLabel;
    String mDBValue;
    Boolean mselected;
    int origArrayPosition;

    public Interest(int icon, int color, String label, String dbValue, int origArrayPosition) {
        this.mIcon = icon;
        this.mColor = color;
        this.mLabel = label;
        this.mDBValue = dbValue;
        this.mselected = false;
        this.origArrayPosition = origArrayPosition;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        this.mIcon = icon;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public String getDBValue() {
        return mDBValue;
    }

    public void setDBValue(String dbValue) {
        this.mDBValue = dbValue;
    }

    public Boolean isSelected() {
        return this.mselected;
    }

    public void setSelected(Boolean selected) {
        this.mselected = selected;
    }

    public int getOrigArrayPosition() {
        return origArrayPosition;
    }

    public void setOrigArrayPosition(int origArrayPosition) {
        this.origArrayPosition = origArrayPosition;
    }

    public static ArrayList<Interest> createFilterInterestList(String[] filterInterestLabelArray, TypedArray filterInterestIconArray, int[] filterInterestColorArray, String[] filterInterestDBValueArray) {
        ArrayList<Interest> filterInterestList = new ArrayList<Interest>();

        for (int i = 0; i < filterInterestLabelArray.length; i++) {
            filterInterestList.add(new Interest(filterInterestIconArray.getResourceId(i, 0), filterInterestColorArray[i], filterInterestLabelArray[i], filterInterestDBValueArray[i], i));
        }

        return filterInterestList;
    }
}
