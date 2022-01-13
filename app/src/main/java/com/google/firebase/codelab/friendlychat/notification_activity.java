package com.google.firebase.codelab.friendlychat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class notification_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_activity);

        TextView dest = (TextView) findViewById(R.id.dest);
        TextView time = (TextView) findViewById(R.id.time);
        TextView etc = (TextView) findViewById(R.id.etc);

        dest.setText("목적지 : " + MapLocation.dest);
        time.setText("시간 : " + MapLocation.time);
        etc.setText("기타 사항 : " + MapLocation.etc);
    }
}
