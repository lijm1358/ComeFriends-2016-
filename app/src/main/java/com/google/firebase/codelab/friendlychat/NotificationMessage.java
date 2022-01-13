package com.google.firebase.codelab.friendlychat;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by USER on 2017-01-06.
 */

@IgnoreExtraProperties
public class NotificationMessage {
    public String dest,time,etc;
    public NotificationMessage(){

    }
    public NotificationMessage(String dest,String time,String etc){
        this.dest=dest;
        this.time=time;
        this.etc=etc;
    }
}
