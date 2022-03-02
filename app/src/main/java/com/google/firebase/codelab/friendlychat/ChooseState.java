package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class ChooseState extends AppCompatActivity
{
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosestate);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) //ACCESS_FINE_LOCATION 권한이 허용되었을때
        {} //아무것도 안함
        else //아니면
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0); //권한 요청
        }
    }

    public void onButton1Clicked(View v)//button1이 눌렸을 때
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            intent = new Intent(this,MapLocation.class); //MapLocation으로 이동
            //intent:내가 행동하고 싶은 것에 대한 정의, Intent intent = new Intent(activity,activity):두 액티비티 간의 통신이 가능
            intent.putExtra("WhoIs","teacher"); //teacher라는 값을 WhoIs에 저장
            startActivity(intent);
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    public void onButton2Clicked(View v)
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            intent = new Intent(this,MapLocation.class); //MapLocation으로 이동
            //intent:내가 행동하고 싶은 것에 대한 정의, Intent intent = new Intent(activity,activity):두 액티비티 간의 통신이 가능
            //intent.putExtra("WhoIs","student"); //student라는 값을 WhoIs에 저장
            Intent moveToName = new Intent(this, StudentName.class);
            startActivity(moveToName);
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

}
