package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.UsersAdapter;
import com.lithium.easyclean.mainPackage.start.User;

import java.util.ArrayList;

public class AdminListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_admin_list);
        Button backButton = findViewById(R.id.back);
        TextInputEditText etSearch = findViewById(R.id.etSearch);


        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        });
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Button addAdminButton = findViewById(R.id.add_admin_button);
        addAdminButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminListActivity.this, NewAdminActivity.class);
            startActivity(intent);
            finish();
        });

        ArrayList<User> list = new ArrayList<>();

        UsersAdapter adapter = new UsersAdapter(this, list);
        ListView listView = findViewById(R.id.admin_list_view);
        listView.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        listView.setOnItemClickListener((arg0, view, position, id) -> {
            User user = (User) arg0.getItemAtPosition(position);
//                Toast.makeText(UserListActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminListActivity.this, ViewUserActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", 1);
            startActivity(intent);
            finish();

        });
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoreRef = rootRef.child("admins");
        ChildEventListener mChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                User user = snapshot.getValue(User.class);
                assert user != null;
                user.setUid(snapshot.getKey());
                if(!firebaseAuth.getCurrentUser().getUid().equals(snapshot.getKey()))
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}