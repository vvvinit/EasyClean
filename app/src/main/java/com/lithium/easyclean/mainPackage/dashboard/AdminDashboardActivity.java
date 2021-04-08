package com.lithium.easyclean.mainPackage.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.admin.AdminListActivity;
import com.lithium.easyclean.mainPackage.dashboard.admin.CleanerListActivity;
import com.lithium.easyclean.mainPackage.dashboard.admin.UserListActivity;
import com.lithium.easyclean.mainPackage.start.EnterEmailActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;

        ImageButton profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(v -> {
            profileButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_profile_pressed, null));
            Intent intent = new Intent(
                    AdminDashboardActivity.this, ProfileActivity.class);
            intent.putExtra("type", 0);
            startActivity(intent);
            finish();
        });
        Button button = findViewById(R.id.sign_out_button);
        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminDashboardActivity.this, EnterEmailActivity.class);
            startActivity(intent);
            finish();
        });
        Button button1 = findViewById(R.id.user_list_button);
        button1.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, UserListActivity.class);
            startActivity(intent);

        });

        Button button2 = findViewById(R.id.cleaner_list_button);
        button2.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, CleanerListActivity.class);
            startActivity(intent);

        });

        Button button3 = findViewById(R.id.admin_list_button);
        button3.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminListActivity.class);
            startActivity(intent);

        });


    }
}