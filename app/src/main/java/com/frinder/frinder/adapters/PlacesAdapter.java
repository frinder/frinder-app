package com.frinder.frinder.adapters;


import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frinder.frinder.R;
import com.frinder.frinder.model.Place;
import com.frinder.frinder.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private List<Place> mPlaces;
    private Context mContext;
    private List<Double> mUserLocation;

    public PlacesAdapter(Context context, List<Place> places) {
        mPlaces = places;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_place
                , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Get the data model based on position
        final Place place = mPlaces.get(position);
        holder.tvTitle.setText(place.name);
        Glide.with(mContext)
                .load(place.pictureUrl)
                .centerCrop()
                .into(holder.ivPicture);
        String distance = getDistanceString(place.location, getUserLocation());
        if (distance != null) {
            holder.tvDistance.setText(distance);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSelectedListener listener = (OnSelectedListener)mContext;
                listener.onSelected(place);
            }
        });
    }

    private String getDistanceString(List<Double> l1, List<Double> l2) {
        if (l1 == null || l2 == null) {
            return null;
        }
        float[] results = new float[1];
        Location.distanceBetween(l1.get(0), l1.get(1), l2.get(0), l2.get(1), results);
        return results[0] + "m";
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public List<Double> getUserLocation() {
        ArrayList<Double> location = null;
        Location cachedLocation = LocationUtils.cachedLocation;
        if (cachedLocation != null) {
            location = new ArrayList<>();
            location.add(cachedLocation.getLatitude());
            location.add(cachedLocation.getLongitude());
        }
        return location;
    }

    public interface OnSelectedListener {
        abstract void onSelected(Place place);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivPicture)
        ImageView ivPicture;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvDistance)
        TextView tvDistance;

        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
