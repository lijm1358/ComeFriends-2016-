package com.google.firebase.codelab.friendlychat;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by USER on 2017-01-05.
 */

@IgnoreExtraProperties
public class Circle {
    public double latitude,longitude,radius;
    public Circle(){

    }

    public Circle(double latitude,double longitude,double radius){
        this.latitude=latitude;
        this.longitude=longitude;
        this.radius=radius;
    }

}
