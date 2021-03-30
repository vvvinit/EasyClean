package com.lithium.easyclean.mainPackage.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.EnterEmailActivity;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent i = getIntent();
        int DashboardType = i.getIntExtra("type",3);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String s = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        TextView textView = findViewById(R.id.name);
        TextView textView1 = findViewById(R.id.email);
        textView.setText(s);
        textView1.setText(userEmail);

        Button button = (Button)findViewById(R.id.sign_out_button);
        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(ProfileActivity.this, EnterEmailActivity.class);
            startActivity(intent);
            finish();
        });

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if(DashboardType==0)
                intent = new Intent(ProfileActivity.this, AdminDashboardActivity.class);
                if(DashboardType==1)
                    intent = new Intent(ProfileActivity.this, CleanerDashboardActivity.class);
                if(DashboardType==2)
                    intent = new Intent(ProfileActivity.this, UserDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}