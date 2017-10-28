package com.frinder.frinder.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SettingsFragment extends DialogFragment {
    private UserFirebaseDas userFirebaseDas;
    private SharedPreferences pref;
    private int radiusVal, radiusProgressVal;
    private int ageRangeProgressVal;
    private String ageRangeVal;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment frag = new SettingsFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        userFirebaseDas = new UserFirebaseDas(this.getContext());
        pref = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        final Switch discoverableSwitch = (Switch) view.findViewById(R.id.switchDiscoverable);
        Boolean isDiscoverablePref = pref.getBoolean("discoverable", true);
        discoverableSwitch.setChecked(isDiscoverablePref);

        final SeekBar sbRadius = (SeekBar)view.findViewById(R.id.sbRadius);
        final SeekBar sbAgeRange = (SeekBar) view.findViewById(R.id.sbAge);
        final TextView tvRadiusValue = (TextView)view.findViewById(R.id.tvRadiusValue);
        final TextView tvAgeRangeValue = (TextView)view.findViewById(R.id.tvAgeValue);

        int radiusValuePref = pref.getInt("radius",300);
        int radiusProgressValPref = pref.getInt("radiusProgressVal",2);
        radiusVal = radiusValuePref;
        radiusProgressVal = radiusProgressValPref;
        sbRadius.setProgress(radiusProgressValPref);
        tvRadiusValue.setText(radiusValuePref+"m");

        String ageRangeValuePref = pref.getString("ageRange","30-40");
        int ageRangeProgressValPref = pref.getInt("ageRangeProgressVal",2);
        ageRangeVal = ageRangeValuePref;
        ageRangeProgressVal = ageRangeProgressValPref;
        sbAgeRange.setProgress(ageRangeProgressValPref);
        tvAgeRangeValue.setText(ageRangeValuePref);

        sbRadius.setKeyProgressIncrement(10);
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusProgressVal = progress;
                radiusVal = 100 + (100*progress);
                tvRadiusValue.setText(String.valueOf(radiusVal)+"m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbAgeRange.setKeyProgressIncrement(10);
        sbAgeRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ageRangeProgressVal = progress;
                ageRangeVal = (20+10*progress) + "-" + (30+10*progress);
                tvAgeRangeValue.setText(ageRangeVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final Button btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("radius", radiusVal);
                edit.putInt("radiusProgressVal", radiusProgressVal);
                edit.putString("ageRange", ageRangeVal);
                edit.putInt("ageRangeProgressVal", ageRangeProgressVal);
                edit.commit();
                Toast.makeText(getApplicationContext(), "Settings Saved!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        ImageView ivBtnProfileClose = (ImageView) view.findViewById(R.id.ivBtnProfileClose);
        ivBtnProfileClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        discoverableSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("discoverable", discoverableSwitch.isChecked());
                edit.commit();
                userFirebaseDas.updateUserDiscoverability(Profile.getCurrentProfile().getId(),discoverableSwitch.isChecked());
            }
        });
    }
}
