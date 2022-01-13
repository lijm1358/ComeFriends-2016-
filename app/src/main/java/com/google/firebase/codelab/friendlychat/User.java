package com.google.firebase.codelab.friendlychat;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by USER on 2017-01-04.
 */
@IgnoreExtraProperties
public class User {

    public String username;
    public double latitude;
    public double longitude;

    public User(){

    }

    public User(String username, double latitude, double longitude){
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUsername(){
        return username;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getlongitude(){
        return longitude;
    }
}
