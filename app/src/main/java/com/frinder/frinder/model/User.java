package com.frinder.frinder.model;
import com.frinder.frinder.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


//TODO change to parcelable
public class User implements Serializable {
    private String uid;
    private String name;
    private String email;
    private String gender;
    private String profilePicUrl;
    private String linkUrl;
    private int age;
    private String desc;
    private ArrayList<String> interests;
    private Double location[];
    private Date timestamp;

    public User() {
        interests = new ArrayList<>();
        location = new Double[2];
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getDesc() {
        return desc;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public Double[] getLocation() {
        return location;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public void setLocation(Double[] location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", age=" + age +
                ", desc='" + desc + '\'' +
                ", interests=" + interests +
                ", location=" + Arrays.toString(location) +
                ", timestamp=" + timestamp +
                '}';
    }

    public static User fromJSON(JSONObject object) {
        String id, name, email, gender, link, ageRange;
        User user = new User();
        try {
            id = object.getString(Constants.FACEBOOK_PROFILE_ID);

            if (object.has(Constants.FACEBOOK_PROFILE_NAME)) {
                name = object.getString(Constants.FACEBOOK_PROFILE_NAME);
            }
            else {
                name = "";
            }

            if (object.has(Constants.FACEBOOK_PROFILE_EMAIL)) {
                email = object.getString(Constants.FACEBOOK_PROFILE_EMAIL);
            }
            else {
                email = "";
            }

            if (object.has(Constants.FACEBOOK_PROFILE_GENDER)) {
                gender = object.getString(Constants.FACEBOOK_PROFILE_GENDER);
            }
            else {
                gender = "neutral";
            }

            if (object.has(Constants.FACEBOOK_PROFILE_LINK)) {
                link = object.getString(Constants.FACEBOOK_PROFILE_LINK);
            }
            else {
                link = "";
            }

            if (object.has(Constants.FACEBOOK_PROFILE_AGE_RANGE)) {
                ageRange = object.getString(Constants.FACEBOOK_PROFILE_AGE_RANGE);
            }
            else {
                ageRange = "";
            }

            user.setUid(id);
            user.setName(name);
            user.setEmail(email);
            user.setGender(gender);
            user.setLinkUrl(link);
            user.setProfilePicUrl("http://graph.facebook.com/" + id + "/picture?type=large");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
