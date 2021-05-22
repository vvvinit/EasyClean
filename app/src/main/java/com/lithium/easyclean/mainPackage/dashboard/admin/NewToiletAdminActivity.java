package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.SelectCleanerAdapter;
import com.lithium.easyclean.mainPackage.start.Toilet;
import com.lithium.easyclean.mainPackage.start.User;

import java.util.ArrayList;

public class NewToiletAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            setContentView(R.layout.activity_new_toilet);
        Animation rotation = AnimationUtils.loadAnimation(NewToiletAdminActivity.this, R.anim.rotate);
        rotation.setFillAfter(true);
            Button selectCleaner = findViewById(R.id.select_cleaner);

            selectCleaner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog selectCleanerDialog = new Dialog(NewToiletAdminActivity.this);
                    selectCleanerDialog.setContentView(R.layout.layout_select_cleaner);
                    selectCleanerDialog.setCancelable(false);

                    ArrayList<User> list = new ArrayList<>();

                    SelectCleanerAdapter adapter = new SelectCleanerAdapter(NewToiletAdminActivity.this, list);
                    ListView listView = selectCleanerDialog.findViewById(R.id.cleaner_select_list_view);
                    listView.setAdapter(adapter);


                    listView.setOnItemClickListener((arg0, view, position, id) -> {
                        User user = (User) arg0.getItemAtPosition(position);
                        TextInputEditText cleanerID = findViewById(R.id.editTextCleaner);
                        cleanerID.setText(user.getUid());
                        selectCleanerDialog.dismiss();
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

                    selectCleanerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    selectCleanerDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                    selectCleanerDialog.show();

                }


            });
            
            
            TextInputEditText location = findViewById(R.id.editTextLocation);
            TextInputEditText longitude = findViewById(R.id.editTextLongitude);
            TextInputEditText latitude = findViewById(R.id.editTextLatitude);
            TextInputEditText cleanerID = findViewById(R.id.editTextCleaner);




        ImageButton insertToilet = findViewById(R.id.insert_toilet_button);
        insertToilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertToilet.startAnimation(rotation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        insertToilet.clearAnimation();
                    }
                }, 1000);
                if(location.getText().toString().equals("")){
                    Toast.makeText(NewToiletAdminActivity.this, "Please enter location name", Toast.LENGTH_SHORT).show();
                }
                else if(longitude.getText().toString().equals("")){
                    Toast.makeText(NewToiletAdminActivity.this, "Please enter longitude", Toast.LENGTH_SHORT).show();
                }
                else if(latitude.getText().toString().equals("")){
                    Toast.makeText(NewToiletAdminActivity.this, "Please enter latitude", Toast.LENGTH_SHORT).show();
                }
                else if(cleanerID.getText().toString().equals("")){
                    Toast.makeText(NewToiletAdminActivity.this, "Please select a cleaner", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference ref = rootRef.child("geofire");
                        GeoFire geoFire = new GeoFire(ref);
                        DatabaseReference databaseReference1 = rootRef.child("toilets").push();
                        geoFire.setLocation(databaseReference1.getKey(), new GeoLocation(Double.parseDouble(longitude.getText().toString()), Double.parseDouble(latitude.getText().toString())), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
//                                    Toast.makeText(NewToiletAdminActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    databaseReference1.setValue(new Toilet(databaseReference1.getKey(),location.getText().toString(), cleanerID.getText().toString(),100));
                                    Intent intent = new Intent(NewToiletAdminActivity.this, ToiletListAdminActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        });

                    }
                    catch (Exception e){
//                        Toast.makeText(NewToiletAdminActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        if(e instanceof IllegalArgumentException)
                            Toast.makeText(NewToiletAdminActivity.this, "Invalid longitude/latitude", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(NewToiletAdminActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        Intent i = new Intent(NewToiletAdminActivity.this, ToiletListAdminActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }

}