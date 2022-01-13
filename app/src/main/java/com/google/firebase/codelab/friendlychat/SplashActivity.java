package com.google.firebase.codelab.friendlychat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Hardroll on 2017-01-02.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try
        {
            Thread.sleep(2000); // 2초간 대기하고
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //메인으로 넘어간다.
        startActivity(new Intent(this, ChooseState.class));
        finish();
    }
}
