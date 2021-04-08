package com.lithium.easyclean.mainPackage.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
    int dashboardType;
    String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        Intent i = getIntent();
        dashboardType = i.getIntExtra("type", 3);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String s = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        TextView textView = findViewById(R.id.name);
        TextView textView1 = findViewById(R.id.email);
        textView.setText(s);
        textView1.setText(userEmail);

        Button button = findViewById(R.id.sign_out_button);
        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, EnterEmailActivity.class);
            startActivity(intent);
            finish();
        });

        AlertDialog.Builder nameDialog = new AlertDialog.Builder(this);
        nameDialog.setCancelable(true);
        nameDialog.setTitle("Change Name");
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Enter new name");
        nameDialog.setView(nameInput);
        nameDialog.setPositiveButton("Confirm",
                (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    String name = nameInput.getText().toString();
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
                });

        nameDialog.setNegativeButton("Cancel", (dialog, which) -> {
        });
        AlertDialog nameAlert = nameDialog.create();

        ImageButton changeNameButton = findViewById(R.id.edit_name);
        changeNameButton.setOnClickListener(v -> {
            changeNameButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed, null));
            nameAlert.show();
        });


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
        final EditText emailInput = new EditText(this);
        emailInput.setHint("Enter new email");
        final EditText email2Input = new EditText(this);
        email2Input.setHint("Confirm email");
        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("Enter Password");
        layout.addView(emailInput);
        layout.addView(email2Input);
        layout.addView(passwordInput);

        AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);
        emailDialog.setCancelable(true);
        emailDialog.setTitle("Change Email");

        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        emailDialog.setView(layout);
        emailDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    String email;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.VISIBLE);
                        String newEmail = emailInput.getText().toString();
                        if (newEmail.equals(email2Input.getText().toString())) {
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
                });

        emailDialog.setNegativeButton("Cancel", (dialog, which) -> {
        });
        AlertDialog emailAlert = emailDialog.create();

        ImageButton changeEmailButton = findViewById(R.id.edit_email);
        changeEmailButton.setOnClickListener(v -> {
            changeEmailButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed, null));
            emailAlert.show();
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