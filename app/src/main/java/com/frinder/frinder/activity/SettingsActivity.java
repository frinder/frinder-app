package com.frinder.frinder.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;

import static com.frinder.frinder.R.id.sbRadius;

public class SettingsActivity extends AppCompatActivity {
    private UserFirebaseDas userFirebaseDas;
    private SharedPreferences pref;
    private int radiusVal, radiusProgressVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userFirebaseDas = new UserFirebaseDas(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        final Switch discoverableSwitch = (Switch) findViewById(R.id.switchDiscoverable);
        Boolean isDiscoverablePref = pref.getBoolean("discoverable", true);
        discoverableSwitch.setChecked(isDiscoverablePref);

        final SeekBar sbRadius = (SeekBar)findViewById(R.id.sbRadius);
        final TextView tvRadiusValue = (TextView)findViewById(R.id.tvRadiusValue);
        int radiusValuePref = pref.getInt("radius",300);
        int radiusProgressValPref = pref.getInt("radiusProgressVal",2);
        sbRadius.setProgress(radiusProgressValPref);
        tvRadiusValue.setText(radiusValuePref+"m");


        discoverableSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("discoverable", discoverableSwitch.isChecked());
                edit.commit();
                userFirebaseDas.updateUserDiscoverability(Profile.getCurrentProfile().getId(),discoverableSwitch.isChecked());
            }
        });


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

        final Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("radius", radiusVal);
                edit.putInt("radiusProgressVal", radiusProgressVal);
                edit.commit();
                finish();
                Toast.makeText(getApplicationContext(), "Settings Saved!", Toast.LENGTH_SHORT);
            }
        });
    }
}
