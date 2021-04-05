package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.User;

public class ViewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        Intent i = getIntent();
        User user = (User) i.getSerializableExtra("user");
        String uid = i.getStringExtra("uid");

        TextView uidView = findViewById(R.id.uid);
        TextView nameView = findViewById(R.id.name);
        TextView emailView = findViewById(R.id.email);

        uidView.setText(uid);
        nameView.setText(user.getName());
        emailView.setText(user.getEmail());

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUserActivity.this, UserListActivity.class);
                finish();
            }
        });
    }
}