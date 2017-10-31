package com.frinder.frinder.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frinder.frinder.R;
import com.frinder.frinder.model.DiscoverUser;
import com.frinder.frinder.model.User;

import org.apmem.tools.layouts.FlowLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static com.frinder.frinder.R.id.ivFullProfileUserImage;

/**
 * Created by mallikaviswas on 10/22/17.
 */

public class ShowFullProfileFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "ShowFullProfileFragment";

    public ShowFullProfileFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static ShowFullProfileFragment newInstance(DiscoverUser discoverUser) {
        ShowFullProfileFragment frag = new ShowFullProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("discover_user", discoverUser);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_full_profile, container);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DiscoverUser discoverUser = (DiscoverUser) getArguments().getSerializable("discover_user");
        User user = discoverUser.getUser();

        Context context = getActivity();

        LinearLayout llFullProfileBtnDistance = (LinearLayout) view.findViewById(R.id.llFullProfileBtnDistance);
        if (discoverUser.isMeetupRequestSent()) {
            llFullProfileBtnDistance.setBackground(ContextCompat.getDrawable(context, R.drawable.item_user_bluegreen_gradient));
        }
        else {
            llFullProfileBtnDistance.setBackground(ContextCompat.getDrawable(context, R.drawable.item_user_blueorange_gradient));
        }

        ImageView ivFullProfileBtnCloseIcon = (ImageView) view.findViewById(R.id.ivFullProfileBtnCloseIcon);
        ivFullProfileBtnCloseIcon.setOnClickListener(this);

        TextView tvFullProfileDistance = (TextView) view.findViewById(R.id.tvFullProfileDistance);
        double dInMtr = discoverUser.getDistanceFromAppUser();
        double dInMiles = dInMtr/1609;
        double dInFeet = dInMtr*3.2808;
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        double distance = Double.parseDouble(numberFormat.format(dInMiles));
        if (distance < 0.1) {
            distance = Double.parseDouble(numberFormat.format(dInFeet));
            if (distance < 10) {
                tvFullProfileDistance.setText("< 10ft");
            }
            else {
                tvFullProfileDistance.setText(distance + "ft");
                //Log.d(TAG, "FINAL: " + user.getName() + " @ " + distance + "ft");
            }
        }
        else {
            tvFullProfileDistance.setText(distance + "mi");
            //Log.d(TAG, "FINAL: " + user.getName() + " @ " + distance + "mi");
        }

        // Set item views based on your views and data model
        ImageView ivUserImage = (ImageView) view.findViewById(ivFullProfileUserImage);
        if (user.getProfilePicUrl() == null || user.getProfilePicUrl().isEmpty()) {
            if (user.getGender().contentEquals("female")) {
                ivUserImage.setImageResource(R.drawable.profile_img_female);
            } else if (user.getGender().contentEquals("male")) {
                ivUserImage.setImageResource(R.drawable.profile_img_male);
            } else {
                ivUserImage.setImageResource(R.drawable.profile_img_neutral);
            }
        }
        else {
            Glide.with(getActivity())
                    .load(user.getProfilePicUrl())
                    .centerCrop()
                    .into(ivUserImage);
        }

        TextView tvUserName = (TextView) view.findViewById(R.id.tvFullProfileUserName);
        tvUserName.setText(user.getName());

        TextView tvAge = (TextView) view.findViewById(R.id.tvFullProfileUserAge);
        int age = user.getAge();
        int lowerLimit = (age/10)*10;
        int upperLimit = lowerLimit+10;
        if (lowerLimit == 10) {
            lowerLimit = 18;
            upperLimit = 25;
        }
        tvAge.setText(lowerLimit + " - " + upperLimit + " yrs");

        TextView tvGender = (TextView) view.findViewById(R.id.tvFullProfileUserGender);
        String genderStr = user.getGender();
        genderStr = genderStr.substring(0,1).toUpperCase() + genderStr.substring(1);
        tvGender.setText(genderStr);

        TextView tvUserDesc = (TextView) view.findViewById(R.id.tvFullProfileUserDesc);
        tvUserDesc.setText(user.getDesc());

        FlowLayout flInterestsLayout = (FlowLayout) view.findViewById(R.id.flFullProfileInterestsLayout);

        ArrayList<String> filterInterestLabel = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.filter_interest_label)));
        ArrayList<String> filterInterestForDB = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.filter_interest_forDB)));
        TypedArray filterInterestIconArray = getResources().obtainTypedArray(R.array.filter_interest_icon);

        for (String interest : user.getInterests()) {
            int index = filterInterestForDB.indexOf(interest);
            String interestLabel = filterInterestLabel.get(index);

            TextView textView = new TextView(context);
            textView.setTypeface(null, Typeface.ITALIC);
            textView.setTextColor(ContextCompat.getColor(context, R.color.white));
            textView.setText(interestLabel);

            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.interest_tvroundedcorner_bg));
            //GradientDrawable textViewBackground = (GradientDrawable) textView.getBackground();
            //textViewBackground.setColor(ContextCompat.getColor(context, filterInterestColorArray.getResourceId(index, 0)));

            textView.setCompoundDrawablesWithIntrinsicBounds(filterInterestIconArray.getResourceId(index, 0), 0, 0, 0);
            textView.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.filter_pic_padding));
            Drawable textViewDrawable = textView.getCompoundDrawables()[0];
            textViewDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN));

            flInterestsLayout.addView(textView);

            View tView = flInterestsLayout.getChildAt(flInterestsLayout.getChildCount()-1);
            FlowLayout.LayoutParams tLayoutParams = (FlowLayout.LayoutParams) tView.getLayoutParams();
            tLayoutParams.setMargins(15, 15, 15, 15); // llp.setMargins(left, top, right, bottom);
            textView.setLayoutParams(tLayoutParams);
        }

        filterInterestIconArray.recycle();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivFullProfileBtnCloseIcon) {
            dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}