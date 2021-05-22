package com.lithium.easyclean.mainPackage.dashboard.user;

import android.app.ActionBar;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.Toilet;

import java.util.ArrayList;

public class ToiletListUserActivity extends AppCompatActivity {

    String userName;
    ToiletsUserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_toilet_list_user);

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
        adapter = new ToiletsUserAdapter(this, list);


        ListView listView = findViewById(R.id.toilet_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((arg0, view, position, id) -> {

        Toilet toilet = (Toilet) arg0.getItemAtPosition(position);
        final Dialog turbidityDialog = new Dialog(ToiletListUserActivity.this);
        turbidityDialog.setContentView(R.layout.toilet_dialog_layout);
        turbidityDialog.setCancelable(true);


        TextView turbidityResult = turbidityDialog.findViewById(R.id.turbidity_result);
        int threshold = Integer.parseInt(getApplicationContext().getString(R.string.TURBIDITY_THRESHOLD));
        if (toilet.getTurbidity() < threshold) {
            turbidityResult.setText("The toilet is clean!");
        } else {
            turbidityResult.setText("The toilet is not clean!");
        }


        Button cancelResult = turbidityDialog.findViewById(R.id.cancel);
        turbidityDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        turbidityDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);


        cancelResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turbidityResult.setText("");
                turbidityDialog.dismiss();
            }
        });

        turbidityDialog.show();

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
                    list.add(toilet);
                    adapter.notifyDataSetChanged();

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Toilet toilet = snapshot.getValue(Toilet.class);
                assert toilet != null;
                toilet.setId(snapshot.getKey());
                    String toiletId = snapshot.getKey();
                    list.removeIf(n -> (n.getId().equals(toiletId)));
                    list.add(toilet);
                    adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Toilet toilet = snapshot.getValue(Toilet.class);
                assert toilet != null;
                toilet.setId(snapshot.getKey());
                    list.remove(toilet);
                    adapter.notifyDataSetChanged();

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