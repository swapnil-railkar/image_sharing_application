package com.example.variablesharing;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
    }


    public void onclicksend(View view) {
        Intent intent_send=new Intent(this,SharingScreen.class);
        startActivity(intent_send);
    }


}