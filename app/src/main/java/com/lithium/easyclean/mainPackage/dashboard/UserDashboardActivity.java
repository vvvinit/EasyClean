package com.lithium.easyclean.mainPackage.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.EnterEmailActivity;

public class UserDashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String s = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        TextView textView = findViewById(R.id.user_name);
        TextView textView1 = findViewById(R.id.user_email);
        textView.setText(s);
        textView1.setText(userEmail);

        ImageButton profileButton = (ImageButton) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(v -> {
            profileButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_profile_pressed,null));
            Intent intent=new Intent(
                    UserDashboardActivity.this, ProfileActivity.class);
            intent.putExtra("type",2);
            startActivity(intent);
            finish();
        });


        Button button = (Button)findViewById(R.id.sign_out_button);
        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(UserDashboardActivity.this, EnterEmailActivity.class);
            startActivity(intent);
            finish();
        });




    }
}