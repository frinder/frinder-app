package com.frinder.frinder.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frinder.frinder.R;
import com.frinder.frinder.model.Interest;

import java.util.List;

/**
 * Created by mallikaviswas on 10/21/17.
 */

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.ViewHolder> {
    private final static String TAG = "InterestsAdapter";
    private Context mContext;
    private List<Interest> mInterests;
    private OnItemClickListener listener;

    public InterestsAdapter(Context context, List<Interest> interests) {
        mContext = context;
        mInterests = interests;
    }

    private Context getContext() {
        return mContext;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public InterestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View interestView = inflater.inflate(R.layout.item_interest, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(interestView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(InterestsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Interest interest = mInterests.get(position);

        // Set item views based on your views and data model
        viewHolder.tvInterestLabel.setText(interest.getLabel());
        viewHolder.ivInterestIcon.setImageResource(interest.getIcon());

        if (interest.isSelected()) {
            //color = ContextCompat.getColor(mContext, R.color.colorPrimary);
            //viewHolder.ivInterestIcon.setBackgroundColor(color);

            int selectedColor = interest.getColor();
            viewHolder.tvInterestLabel.setTextColor(selectedColor);
            //viewHolder.ivInterestIcon.setBackgroundColor(selectedBgTxtColor);
            viewHolder.ivInterestIcon.setColorFilter(selectedColor);

            //Drawable myIcon = viewHolder.ivInterestIcon.getDrawable();
            //myIcon.setColorFilter(selectedBgTxtColor, PorterDuff.Mode.SRC_ATOP);
            //viewHolder.ivInterestIcon.setImageDrawable(myIcon);
        }
        else {
            int unselectedColor = ContextCompat.getColor(mContext, R.color.black);
            viewHolder.tvInterestLabel.setTextColor(unselectedColor);
            //viewHolder.ivInterestIcon.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.ivInterestIcon.setColorFilter(unselectedColor);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mInterests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivInterestIcon;
        public TextView tvInterestLabel;
        public LinearLayout llInterestContainer;

        public ViewHolder(final View itemView) {
            super(itemView);

            llInterestContainer = (LinearLayout) itemView.findViewById(R.id.llInterestContainer);
            ivInterestIcon = (ImageView) itemView.findViewById(R.id.ivInterestIcon);
            tvInterestLabel = (TextView) itemView.findViewById(R.id.tvInterestLabel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }
    }
}
