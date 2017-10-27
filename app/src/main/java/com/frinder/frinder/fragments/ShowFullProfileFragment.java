package com.frinder.frinder.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
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

import java.util.ArrayList;
import java.util.Arrays;

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
        return dialog;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DiscoverUser discoverUser = (DiscoverUser) getArguments().getSerializable("discover_user");
        User user = discoverUser.getUser();

        Context context = getActivity();

        // Set item views based on your views and data model
        ImageView ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage);
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

        TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());

        TextView tvUserDesc = (TextView) view.findViewById(R.id.tvUserDesc);
        tvUserDesc.setText(user.getDesc());

        TextView tvAge = (TextView) view.findViewById(R.id.tvAge);
        int age = user.getAge();
        int lowerLimit = (age/10)*10;
        int upperLimit = lowerLimit+10;
        if (lowerLimit == 10) {
            lowerLimit = 18;
            upperLimit = 25;
        }
        tvAge.setText(lowerLimit + " - " + upperLimit + " yrs, ");

        TextView tvGender = (TextView) view.findViewById(R.id.tvGender);
        tvGender.setText(user.getGender());

        LinearLayout llInterestsLayout = (LinearLayout) view.findViewById(R.id.llInterestsLayout);
        ArrayList<String> filterInterestLabel = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.filter_interest_label)));
        ArrayList<String> filterInterestForDB = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.filter_interest_forDB)));
        TypedArray filterInterestIconArray = getResources().obtainTypedArray(R.array.filter_interest_icon);
        TypedArray filterInterestColorArray = getResources().obtainTypedArray(R.array.filter_interest_color);
        //filterInterestIconArray.getResourceId(i, 0)

        for (String interest : user.getInterests()) {
            int index = filterInterestForDB.indexOf(interest);
            String interestLabel = filterInterestLabel.get(index).replace("\n", "/");
            TextView textView = new TextView(context);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(8, 8, 8, 8); // llp.setMargins(left, top, right, bottom);
            textView.setLayoutParams(llp);
            textView.setTextColor(ContextCompat.getColor(context, R.color.white));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(interestLabel);
            textView.setGravity(Gravity.CENTER_VERTICAL);

            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.interest_tvroundedcorner_bg));
            GradientDrawable textViewBackground = (GradientDrawable) textView.getBackground();
            textViewBackground.setColor(ContextCompat.getColor(context, filterInterestColorArray.getResourceId(index, 0)));

            textView.setCompoundDrawablesWithIntrinsicBounds(filterInterestIconArray.getResourceId(index, 0), 0, 0, 0);
            textView.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.filter_icon_padding));
            Drawable textViewDrawable = textView.getCompoundDrawables()[0];
            textViewDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN));

            llInterestsLayout.addView(textView);
        }

        ImageView ivBtnProfileClose = (ImageView) view.findViewById(R.id.ivBtnProfileClose);
        ivBtnProfileClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBtnProfileClose) {
            dismiss();
        }
    }
}