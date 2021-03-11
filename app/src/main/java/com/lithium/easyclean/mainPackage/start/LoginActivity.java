package com.lithium.easyclean.mainPackage.start;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lithium.easyclean.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        int loginType = intent.getIntExtra("loginType", -1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}