package com.lithium.easyclean.mainPackage.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lithium.easyclean.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

            Animation a = AnimationUtils.loadAnimation(this, R.anim.scale);
            a.reset();
            TextView tv = (TextView) findViewById(R.id.logo_text);
            tv.clearAnimation();
            tv.startAnimation(a);

        Animation a2 = AnimationUtils.loadAnimation(this, R.anim.scale2);
        a2.reset();
        TextView tv2 = (TextView) findViewById(R.id.group_info);
        tv2.clearAnimation();
        tv2.startAnimation(a2);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent=new Intent(SplashActivity.this, MainActivity.class);

            startActivity(intent);
            finish();
        },500);
    }
}