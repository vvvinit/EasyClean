package com.lithium.easyclean.mainPackage.dashboard;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.EnterEmailActivity;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    int dashboardType;
    String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_profile);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        Intent i = getIntent();
        dashboardType = i.getIntExtra("type", 3);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String s = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        String userUid = firebaseUser.getUid();

        TextView textView = findViewById(R.id.name);
        TextView textView1 = findViewById(R.id.email);
        TextView textView2 = findViewById(R.id.uid);
        textView.setText(s);
        textView1.setText(userEmail);
        textView2.setText(userUid);

        Button button = findViewById(R.id.sign_out_button);
        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, EnterEmailActivity.class);
            startActivity(intent);
            finish();
        });





        final Dialog nameDialog = new Dialog(ProfileActivity.this);
        nameDialog.setContentView(R.layout.name_dialog_layout);
        nameDialog.setCancelable(true);



        TextInputEditText nameInput = nameDialog.findViewById(R.id.change_name);
        Button confirmName = nameDialog.findViewById(R.id.confirm);
        Button cancelName = nameDialog.findViewById(R.id.cancel);
        nameDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        nameDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        confirmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(nameInput.getText().toString().equals("")){
                    Toast.makeText(ProfileActivity.this, "Name cannot be empty", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    String name = Objects.requireNonNull(nameInput.getText()).toString();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    if (user != null)
                        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = user.getUid();

                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference();
                                if (dashboardType == 0)
                                    databaseReference.child("admins").child(uid).child("name").setValue(name);
                                if (dashboardType == 1)
                                    databaseReference.child("cleaners").child(uid).child("name").setValue(name);
                                if (dashboardType == 2)
                                    databaseReference.child("users").child(uid).child("name").setValue(name);
                                textView.setText(name);
                                Toast.makeText(ProfileActivity.this, "Name changed to " + name, Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            } else{
                                Toast.makeText(ProfileActivity.this, "Name update failed, please try again", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);}
                        });
                }
                nameInput.setText("");
                nameDialog.dismiss();
            }
        });

            cancelName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nameInput.setText("");
                    nameDialog.dismiss();
                }
            });


        Button changeNameButton = findViewById(R.id.edit_name);
        changeNameButton.setOnClickListener(g -> {
            nameDialog.show();
        });









        final Dialog emailDialog = new Dialog(ProfileActivity.this);
        emailDialog.setContentView(R.layout.self_email_dialog_layout);
        emailDialog.setCancelable(true);



        TextInputEditText emailInput1 = emailDialog.findViewById(R.id.change_email1);
        TextInputEditText emailInput2 = emailDialog.findViewById(R.id.change_email2);
        TextInputEditText passwordInput = emailDialog.findViewById(R.id.password);
        Button confirmEmail = emailDialog.findViewById(R.id.confirm);
        Button cancelEmail = emailDialog.findViewById(R.id.cancel);
        emailDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        emailDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        confirmEmail.setOnClickListener(new View.OnClickListener() {
            String email;
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (emailInput1.getText().toString().equals("") || emailInput2.getText().toString().equals("") || passwordInput.getText().toString().equals("")) {
                    Toast.makeText(ProfileActivity.this, "Email/Password cannot be empty", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                } else {

                    String newEmail = emailInput1.getText().toString();
                    if (newEmail.equals(emailInput2.getText().toString())) {
                        String password = passwordInput.getText().toString();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null)
                            email = user.getEmail();
                        assert email != null;
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email, password); // Current Login Credentials \\
                        // Prompt the user to re-provide their sign-in credentials
                        if (user != null)
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User re-authenticated.");
                                            //Now change your email address \\
                                            //----------------Code for Changing Email Address----------\\
                                            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user1 != null)
                                                user1.updateEmail(newEmail)
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                Log.d(TAG, "User email address updated.");
                                                                String uid = user1.getUid();

                                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                                DatabaseReference databaseReference = firebaseDatabase.getReference();
                                                                if (dashboardType == 0)
                                                                    databaseReference.child("admins").child(uid).child("email").setValue(newEmail);
                                                                if (dashboardType == 1)
                                                                    databaseReference.child("cleaners").child(uid).child("email").setValue(newEmail);
                                                                if (dashboardType == 2)
                                                                    databaseReference.child("users").child(uid).child("email").setValue(newEmail);
                                                                textView1.setText(newEmail);
                                                                Toast.makeText(ProfileActivity.this, "Email changed to " + newEmail, Toast.LENGTH_LONG).show();
                                                                progressBar.setVisibility(View.GONE);

                                                            } else {
                                                                Toast.makeText(ProfileActivity.this, "Error! Please Try again.", Toast.LENGTH_LONG).show();
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        });
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Please Try again.", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }

                                    });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Emails don't match! Please Try again.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                emailInput1.setText("");
                emailInput2.setText("");
                passwordInput.setText("");
                emailDialog.dismiss();
            }
        });

        cancelEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailInput1.setText("");
                emailInput2.setText("");
                passwordInput.setText("");
                emailDialog.dismiss();
            }
        });


        Button changeEmailButton = findViewById(R.id.edit_email);
        changeEmailButton.setOnClickListener(v -> {
            emailDialog.show();
        });

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            Intent intent = null;
            if (dashboardType == 0)
                intent = new Intent(ProfileActivity.this, AdminDashboardActivity.class);
            if (dashboardType == 1)
                intent = new Intent(ProfileActivity.this, CleanerDashboardActivity.class);
            if (dashboardType == 2)
                intent = new Intent(ProfileActivity.this, UserDashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;
        if (dashboardType == 0)
            intent = new Intent(ProfileActivity.this, AdminDashboardActivity.class);
        if (dashboardType == 1)
            intent = new Intent(ProfileActivity.this, CleanerDashboardActivity.class);
        if (dashboardType == 2)
            intent = new Intent(ProfileActivity.this, UserDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}