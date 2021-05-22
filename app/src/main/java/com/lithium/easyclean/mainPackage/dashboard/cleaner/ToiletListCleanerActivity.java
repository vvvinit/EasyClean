package com.lithium.easyclean.mainPackage.dashboard.cleaner;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.Toilet;

import java.util.ArrayList;

public class ToiletListCleanerActivity extends AppCompatActivity {
    String userName;
    ToiletsCleanerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_toilet_list_cleaner);
        userName = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        });

        TextInputEditText etSearch = findViewById(R.id.etSearch);


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


        setListView();

    }

    private void setListView(){
        ArrayList<Toilet> list = new ArrayList<>();
        adapter = new ToiletsCleanerAdapter(this, list);


        ListView listView = findViewById(R.id.toilet_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((arg0, view, position, id) -> {
            Toilet toilet = (Toilet) arg0.getItemAtPosition(position);
//                Toast.makeText(ToiletListAdminActivity.this, toilet.getId(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ToiletListCleanerActivity.this, ViewToiletCleanerActivity.class);
            intent.putExtra("toilet", toilet);
            startActivity(intent);
            finish();

        });

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoreRef = rootRef.child("toilets");
//        Toast.makeText(ToiletListCleanerActivity.this, userName+1, Toast.LENGTH_SHORT).show();


        ChildEventListener mChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Toilet toilet = snapshot.getValue(Toilet.class);
                assert toilet != null;
                toilet.setId(snapshot.getKey());
//                Toast.makeText(ToiletListCleanerActivity.this, toilet.getCleaner(), Toast.LENGTH_SHORT).show();
                if(toilet.getCleaner().equals(userName)) {
                    list.add(toilet);
                    adapter.notifyDataSetChanged();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Toilet toilet = snapshot.getValue(Toilet.class);
                assert toilet != null;
                toilet.setId(snapshot.getKey());




                if(toilet.getCleaner().equals(userName)) {
                    String toiletId = snapshot.getKey();
                    list.removeIf(n -> (n.getId().equals(toiletId)));
                    list.add(toilet);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Toilet toilet = snapshot.getValue(Toilet.class);
                assert toilet != null;
                toilet.setId(snapshot.getKey());
                if(!toilet.getCleaner().equals(userName)) {
                    list.remove(toilet);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        scoreRef.orderByChild("turbidity").addChildEventListener(mChildEventListener);
        adapter.notifyDataSetChanged();
//        scoreRef.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}