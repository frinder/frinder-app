package com.frinder.frinder.model;

import com.frinder.frinder.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

public class Place {

    public ArrayList<Double> location;
    public String name;
    public String description;
    public String address;
    public int checkins;
    public String pictureUrl;

    public Place(JSONObject jsonObject) {
        JSONObject locationObject = jsonObject.optJSONObject(Constants.FACEBOOK_PLACES_LOCATION);
        if (locationObject != null) {
            if (locationObject.has(Constants.FACEBOOK_PLACES_LOCATION_LATITUDE)
                    && locationObject.has(Constants.FACEBOOK_PLACES_LOCATION_LONGITUDE)) {
                double latitude = locationObject.optDouble(Constants.FACEBOOK_PLACES_LOCATION_LATITUDE);
                double longitude = locationObject.optDouble(Constants.FACEBOOK_PLACES_LOCATION_LONGITUDE);
                location = new ArrayList<Double>();
                location.add(latitude);
                location.add(longitude);
            }
        }
        name = jsonObject.optString(Constants.FACEBOOK_PLACES_NAME);
        description = jsonObject.optString(Constants.FACEBOOK_PLACES_DESCRIPTION);
        address = jsonObject.optString(Constants.FACEBOOK_PLACES_ADDRESS);
        checkins = jsonObject.optInt(Constants.FACEBOOK_PLACES_CHECKINS, 0);
        JSONObject pictureObject = jsonObject.optJSONObject(Constants.FACEBOOK_PLACES_PICTURE);
        if (pictureObject != null) {
            JSONObject dataObject = pictureObject.optJSONObject(Constants.FACEBOOK_PLACES_PICTURE_DATA);
            if (dataObject != null) {
                pictureUrl = dataObject.optString(Constants.FACEBOOK_PLACES_PICTURE_DATA_URL);
            }
        }
    }
}
