package com.lithium.easyclean.mainPackage.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.EnterEmailActivity;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent i = getIntent();
        int dashboardType = i.getIntExtra("type",3);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String s = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        TextView textView = findViewById(R.id.name);
        TextView textView1 = findViewById(R.id.email);
        textView.setText(s);
        textView1.setText(userEmail);

        Button button = (Button)findViewById(R.id.sign_out_button);
        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(ProfileActivity.this, EnterEmailActivity.class);
            startActivity(intent);
            finish();
        });

        AlertDialog.Builder nameDialog = new AlertDialog.Builder(this);
        nameDialog.setCancelable(true);
        nameDialog.setTitle("Change Name");
        final EditText nameInput = new EditText (this);
        nameInput.setHint("Enter new name");
        nameDialog.setView(nameInput);
        nameDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameInput.getText().toString();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        if(user!=null)
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    String uid = user.getUid();

                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference();
                                    if(dashboardType==0)
                                    databaseReference.child("admins").child(uid).child("name").setValue(name);
                                    if(dashboardType==1)
                                        databaseReference.child("cleaners").child(uid).child("name").setValue(name);
                                    if(dashboardType==2)
                                        databaseReference.child("users").child(uid).child("name").setValue(name);
                                    textView.setText(name);
                                    Toast.makeText(ProfileActivity.this,"Name changed to "+name,Toast.LENGTH_LONG).show();
                                }else
                                    Toast.makeText(ProfileActivity.this,"Name update failed, please try again",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

        nameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog nameAlert = nameDialog.create();

        ImageButton changeNameButton = (ImageButton) findViewById(R.id.edit_name);
        changeNameButton.setOnClickListener(v -> {
            changeNameButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed,null));
            nameAlert.show();
        });

//        ImageButton changeEmailButton = (ImageButton) findViewById(R.id.edit_email);
//        changeNameButton.setOnClickListener(v -> {
//            changeNameButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed,null));
//            Intent intent=new Intent(
//                    ProfileActivity.this, ProfileActivity.class);
//            intent.putExtra("type",0);
//            startActivity(intent);
//            finish();
//        });

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if(dashboardType==0)
                intent = new Intent(ProfileActivity.this, AdminDashboardActivity.class);
                if(dashboardType==1)
                    intent = new Intent(ProfileActivity.this, CleanerDashboardActivity.class);
                if(dashboardType==2)
                    intent = new Intent(ProfileActivity.this, UserDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}