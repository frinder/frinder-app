package com.frinder.frinder.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mallikaviswas on 10/20/17.
 */

//TODO convert to Parcel
public class DiscoverUser implements Serializable {
    private User user;
    private Double distanceFromAppUser;
    private ArrayList<String> commonInterests;

    public DiscoverUser(User user, Double distance, ArrayList<String> interests) {
        this.user = user;
        this.distanceFromAppUser = distance;
        this.commonInterests = interests;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getDistanceFromAppUser() {
        return distanceFromAppUser;
    }

    public void setDistanceFromAppUser(Double distanceFromAppUser) {
        this.distanceFromAppUser = distanceFromAppUser;
    }

    public ArrayList<String> getCommonInterests() {
        return commonInterests;
    }

    public void setCommonInterests(ArrayList<String> commonInterests) {
        this.commonInterests = commonInterests;
    }

    @Override
    public String toString() {
        return "DiscoverUser{" +
                "distanceFromAppUser=" + this.distanceFromAppUser +
                ", interests=" + this.commonInterests +
                ", " + this.user.toString() +
                '}';
    }
}