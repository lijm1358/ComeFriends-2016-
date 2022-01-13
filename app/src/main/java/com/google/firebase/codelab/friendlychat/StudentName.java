package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class StudentName extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_name);

        editText = (EditText) findViewById(R.id.editText);
    }

    public void onButton1Clicked(View v){
        Intent intent = new Intent(this,MapLocation.class);
        //Intent name = new Intent(this,MapLocation.class);
        String sname = editText.getText().toString();
        //name.putExtra("name",sname);
        intent.putExtra("WhoIs","student");
        intent.putExtra("Name",sname);
        startActivity(intent);

    }
}
