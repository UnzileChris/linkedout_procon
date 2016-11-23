package com.example.christopher.linkedoutapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickLogin(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void onClickSignUp(View view){
        Intent intent = new Intent(this, Quiz.class);
        startActivity(intent);
    }

    public void onClickRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
