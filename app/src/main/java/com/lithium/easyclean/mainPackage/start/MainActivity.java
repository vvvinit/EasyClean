package com.lithium.easyclean.mainPackage.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.lithium.easyclean.R;


public class MainActivity extends AppCompatActivity {
    static final int USER_LOGIN = 1;
    static final int ADMIN_LOGIN = 2;
    static final int CLEANER_LOGIN = 3;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button userLogin = (Button) findViewById(R.id.user_login);
        Button adminLogin = (Button) findViewById(R.id.admin_login);
        Button cleanerLogin = (Button) findViewById(R.id.cleaner_login);
        Button userSignUp = (Button) findViewById(R.id.user_sign_up);


        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EnterEmailActivity.class);
                intent.putExtra("loginType", USER_LOGIN);
                startActivity(intent);
            }
        });

        userSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EnterEmailActivity.class);
                intent.putExtra("loginType", ADMIN_LOGIN);
                startActivity(intent);

            }
        });

        cleanerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EnterEmailActivity.class);
                intent.putExtra("loginType", CLEANER_LOGIN);
                startActivity(intent);
            }
        });

    }
}