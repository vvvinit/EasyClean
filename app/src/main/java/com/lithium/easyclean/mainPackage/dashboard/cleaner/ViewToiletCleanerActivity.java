package com.lithium.easyclean.mainPackage.dashboard.cleaner;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.Toilet;

public class ViewToiletCleanerActivity extends AppCompatActivity {

    int type;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_view_toilet_cleaner);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        Intent i = getIntent();
        Toilet toilet = (Toilet) i.getSerializableExtra("toilet");

        String toiletID = toilet.getId();
        String location = toilet.getLocation();
        final int[] turbidity = {toilet.getTurbidity()};


        TextView vToiletID = findViewById(R.id.toilet_id);
        TextView vLocation = findViewById(R.id.location);
        TextView vCoordinates = findViewById(R.id.geo);
        TextView vTurbidity = findViewById(R.id.turbidity);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = rootRef.child("geofire");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.getLocation(toiletID, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    vCoordinates.setText(location.longitude+" - "+location.latitude);
                } else {
                    Toast.makeText(ViewToiletCleanerActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });




        vToiletID.setText(toiletID);
        vLocation.setText(location);
        vTurbidity.setText(Integer.toString(turbidity[0]));


        DatabaseReference.CompletionListener mRemoveListener =
                (error, rRef) -> {
                    if (error == null) {
                        geoFire.removeLocation(toiletID, new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    Toast.makeText(ViewToiletCleanerActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent i12 =  new Intent(ViewToiletCleanerActivity.this, ToiletListCleanerActivity.class);
                                    startActivity(i12);
                                    finish();
                                }
                            }

                        });
                    } else {
                        Toast.makeText(ViewToiletCleanerActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                };


        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            Intent i12 =  new Intent(ViewToiletCleanerActivity.this, ToiletListCleanerActivity.class);
            startActivity(i12);
            finish();
        });



        final Dialog turbidityDialog = new Dialog(ViewToiletCleanerActivity.this);
        turbidityDialog.setContentView(R.layout.turbidity_dialog_layout);
        turbidityDialog.setCancelable(true);

        TextView turbidityValue = turbidityDialog.findViewById(R.id.turbidity_value);

        SeekBar seekBar = turbidityDialog.findViewById(R.id.seekBar);
        seekBar.setProgress(toilet.getTurbidity());
        turbidityValue.setText(Integer.toString(toilet.getTurbidity()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                turbidity[0] = progress;
                turbidityValue.setText(Integer.toString(turbidity[0]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button confirmTurbidity = turbidityDialog.findViewById(R.id.confirm);
        Button cancelTurbidity = turbidityDialog.findViewById(R.id.cancel);
        turbidityDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        turbidityDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        confirmTurbidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                toilet.setTurbidity(turbidity[0]);
                rootRef.child("toilets").child(toiletID).setValue(toilet).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        vTurbidity.setText(Integer.toString(turbidity[0]));
                        progressBar.setVisibility(View.GONE);

                        turbidityDialog.dismiss();
                    }
                });



            }
        });

        cancelTurbidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turbidityDialog.dismiss();
            }
        });


        Button changeTurbidityButton = findViewById(R.id.set_turbidity);
        changeTurbidityButton.setOnClickListener(g -> {
            turbidityDialog.show();
        });


    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        Intent i = new Intent(ViewToiletCleanerActivity.this, ToiletListCleanerActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }
}