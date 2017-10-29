package com.frinder.frinder.utils;


import android.content.Context;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.places.PlaceManager;
import com.facebook.places.model.PlaceFields;
import com.facebook.places.model.PlaceSearchRequestParams;
import com.frinder.frinder.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FacebookPlacesSearch {

    private static final String TAG = "FacebookPlacesSearch";

    public static void searchPlaces(Context context, final OnCompletionListener listener) {
        PlaceSearchRequestParams.Builder builder = new PlaceSearchRequestParams.Builder();
        builder.setDistance(250);
        builder.setLimit(20);
        builder.addField(PlaceFields.NAME);
        builder.addField(PlaceFields.LOCATION);
        builder.addField(PlaceFields.CHECKINS);
        builder.addField(PlaceFields.DESCRIPTION);
        builder.addField(PlaceFields.PICTURE);
        builder.addField(PlaceFields.SINGLE_LINE_ADDRESS);

        // TODO: Use midpoint of user location instead
        PlaceManager.newPlaceSearchRequest(builder.build(), new OnPlaceSearchRequestReadyCallback(listener));
    }

    public abstract static class OnCompletionListener {
        public abstract void onSuccess(List<Place> places);
        public abstract void onFailure();
    }

    private static class OnPlaceSearchRequestReadyCallback
            implements PlaceManager.OnRequestReadyCallback, GraphRequest.Callback {

        private final OnCompletionListener mListener;

        OnPlaceSearchRequestReadyCallback(OnCompletionListener listener) {
            mListener = listener;
        }

        @Override
        public void onLocationError(PlaceManager.LocationError error) {
            Log.e(TAG, "Failed to get location:" +  error);
            mListener.onFailure();
        }

        @Override
        public void onRequestReady(GraphRequest graphRequest) {
            graphRequest.setCallback(this);
            graphRequest.executeAsync();
        }

        @Override
        public void onCompleted(GraphResponse response) {
            FacebookRequestError error = response.getError();
            if (error == null) {
                // Parses the place search response.
                try {
                    List<Place> places = null;
                    JSONObject jsonObject = response.getJSONObject();
                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            places = new ArrayList<>(length);
                            for (int i = 0; i < length; i++) {
                                places.add(new Place(jsonArray.getJSONObject(i)));
                            }
                        }
                    }
                    mListener.onSuccess(places);
                    return;
                } catch (JSONException e) {
                    Log.e(TAG, "failed to parse place the place search response", e);
                }
            } else {
                // The error object contains more information on the error.
                Log.d(TAG, "response error: " + error);
            }
            mListener.onFailure();
        }
    }
 }
