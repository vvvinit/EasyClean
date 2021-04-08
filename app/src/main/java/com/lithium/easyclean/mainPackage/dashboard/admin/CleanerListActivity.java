package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.UsersAdapter;
import com.lithium.easyclean.mainPackage.start.User;

import java.util.ArrayList;

public class CleanerListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_list);
        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> finish());

        Button addCleanerButton = findViewById(R.id.add_cleaner_button);
        addCleanerButton.setOnClickListener(v -> {
            Intent intent = new Intent(CleanerListActivity.this, NewCleanerActivity.class);
            startActivity(intent);
            finish();
        });

        ArrayList<User> list = new ArrayList<>();

        UsersAdapter adapter = new UsersAdapter(this, list);
        ListView listView = findViewById(R.id.cleaner_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((arg0, view, position, id) -> {
            User user = (User) arg0.getItemAtPosition(position);
//                Toast.makeText(UserListActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CleanerListActivity.this, ViewUserActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", 2);
            startActivity(intent);
            finish();

        });
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoreRef = rootRef.child("cleaners");
        ChildEventListener mChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                User user = snapshot.getValue(User.class);
                assert user != null;
                user.setUid(snapshot.getKey());
                list.add(user);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                user.setUid(snapshot.getKey());
                list.remove(user);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        scoreRef.addChildEventListener(mChildEventListener);
//        scoreRef.addListenerForSingleValueEvent(eventListener);
    }
}