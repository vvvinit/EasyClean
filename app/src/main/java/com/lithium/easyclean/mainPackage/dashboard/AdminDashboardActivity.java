package com.lithium.easyclean.mainPackage.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lithium.easyclean.R;
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
        String s = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        TextView textView = findViewById(R.id.admin_name);
        TextView textView1 = findViewById(R.id.admin_email);
        textView.setText(s);
        textView1.setText(userEmail);




        Button button = (Button)findViewById(R.id.sign_out_button);
        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(AdminDashboardActivity.this, EnterEmailActivity.class);
            startActivity(intent);
            finish();
        });
        Button button1 = (Button)findViewById(R.id.user_list_button);
        button1.setOnClickListener(v -> {
            Intent intent=new Intent(AdminDashboardActivity.this, UserListActivity.class);
            startActivity(intent);
            finish();
        });

        Button button2 = (Button)findViewById(R.id.cleaner_list_button);
        button2.setOnClickListener(v -> {
            Intent intent=new Intent(AdminDashboardActivity.this, CleanerListActivity.class);
            startActivity(intent);
            finish();
        });





    }
}