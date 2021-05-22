package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.SelectCleanerAdapter;
import com.lithium.easyclean.mainPackage.start.Toilet;
import com.lithium.easyclean.mainPackage.start.User;

import java.util.ArrayList;

public class ViewToiletAdminActivity extends AppCompatActivity {

    private static final String TAG = "ViewToiletAdminActivity";
    int type;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_view_toilet_admin);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        Intent i = getIntent();
        Toilet toilet = (Toilet) i.getSerializableExtra("toilet");

        String toiletID = toilet.getId();
        String location = toilet.getLocation();

        String cleanerID = toilet.getCleaner();
        int turbidity = toilet.getTurbidity();


        TextView vToiletID = findViewById(R.id.toilet_id);
        TextView vLocation = findViewById(R.id.location);
        TextView vCoordinates = findViewById(R.id.geo);
        TextView vTurbidity = findViewById(R.id.turbidity);
        TextView vCleanerID = findViewById(R.id.cleaner_id);
        TextView vCleanerName = findViewById(R.id.cleaner_name);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = rootRef.child("geofire");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.getLocation(toiletID, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    vCoordinates.setText(location.longitude+" - "+location.latitude);
                } else {
                    Toast.makeText(ViewToiletAdminActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });

        rootRef.child("cleaners").child(toilet.getCleaner()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                vCleanerName.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        vToiletID.setText(toiletID);
        vLocation.setText(location);
        vCleanerID.setText(cleanerID);
        vTurbidity.setText(Integer.toString(turbidity));


        DatabaseReference.CompletionListener mRemoveListener =
                (error, rRef) -> {
                    if (error == null) {
                        geoFire.removeLocation(toiletID, new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    Toast.makeText(ViewToiletAdminActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent i12 =  new Intent(ViewToiletAdminActivity.this, ToiletListAdminActivity.class);
                                    startActivity(i12);
                                    finish();
                                }
                            }

                        });
                    } else {
                        Toast.makeText(ViewToiletAdminActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                };


        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            Intent i12 =  new Intent(ViewToiletAdminActivity.this, ToiletListAdminActivity.class);
            startActivity(i12);
            finish();
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference databaseReference1 = rootRef.child("toilets").child(toiletID);
            databaseReference1.removeValue(mRemoveListener);
        });

        Button selectCleaner = findViewById(R.id.edit_cleaner);

        selectCleaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog selectCleanerDialog = new Dialog(ViewToiletAdminActivity.this);
                selectCleanerDialog.setContentView(R.layout.layout_select_cleaner);
                selectCleanerDialog.setCancelable(false);

                ArrayList<User> list = new ArrayList<>();

                SelectCleanerAdapter adapter = new SelectCleanerAdapter(ViewToiletAdminActivity.this, list);
                ListView listView = selectCleanerDialog.findViewById(R.id.cleaner_select_list_view);
                listView.setAdapter(adapter);


                listView.setOnItemClickListener((arg0, view, position, id) -> {
                    User user = (User) arg0.getItemAtPosition(position);
                    vCleanerID.setText(user.getUid());
                    toilet.setCleaner(user.getUid());
                    rootRef.child("toilets").child(toiletID).setValue(toilet).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            vCleanerName.setText(user.getName());
                            selectCleanerDialog.dismiss();
                        }
                    });

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



    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        Intent i = new Intent(ViewToiletAdminActivity.this, ToiletListAdminActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }
}