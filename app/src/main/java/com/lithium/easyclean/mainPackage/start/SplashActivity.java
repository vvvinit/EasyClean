package com.lithium.easyclean.mainPackage.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.AdminDashboardActivity;
import com.lithium.easyclean.mainPackage.dashboard.CleanerDashboardActivity;
import com.lithium.easyclean.mainPackage.dashboard.UserDashboardActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    int progressStatus = 0;
    Handler handler = new Handler();
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_splash);

        ProgressBar progressBar = findViewById(R.id.progressBar1);

        new Thread(() -> {

            while (progressStatus < 100) {
                progressStatus += 1;
                handler.post(() -> progressBar.setProgress(progressStatus));
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (intent == null) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            startActivity(intent);
            finish();
        }).start();


        Animation a = AnimationUtils.loadAnimation(this, R.anim.scale);
        a.reset();
        TextView tv = findViewById(R.id.logo_text);
        tv.clearAnimation();
        tv.startAnimation(a);

        Animation a2 = AnimationUtils.loadAnimation(this, R.anim.scale2);
        a2.reset();
        TextView tv2 = findViewById(R.id.group_info);
        ImageView iv1 = findViewById(R.id.dev_icon);
        tv2.clearAnimation();
        iv1.clearAnimation();
        tv2.startAnimation(a2);
        iv1.startAnimation(a2);

        Handler handler = new Handler();
        handler.postDelayed(() -> {


            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("admins").child(user.getUid());

                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Toast.makeText(SplashActivity.this, "Logged in as an Admin!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("cleaners").child(user.getUid());

                                        ValueEventListener eventListener2 = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                                if (dataSnapshot1.exists()) {
                                                    Toast.makeText(SplashActivity.this, "Logged in as a Cleaner!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SplashActivity.this, CleanerDashboardActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(SplashActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SplashActivity.this, UserDashboardActivity.class);
                                                    startActivity(intent);
                                                }
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                                            }
                                        };
                                        databaseReference1.addListenerForSingleValueEvent(eventListener2);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                                }
                            };
                            databaseReference.addListenerForSingleValueEvent(eventListener);
                        } else {
                            intent = new Intent(SplashActivity.this, EnterEmailActivity.class);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


        }, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}