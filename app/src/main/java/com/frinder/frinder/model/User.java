package com.frinder.frinder.model;

import java.io.Serializable;
import java.util.Date;

//TODO change to parcelable
public class User implements Serializable{
    String name;
    String email;
    String gender;
    Date birthday;
    String profilePicUrl;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
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

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                '}';
    }
}
