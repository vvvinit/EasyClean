package com.lithium.easyclean.mainPackage.start;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.AdminDashboardActivity;
import com.lithium.easyclean.mainPackage.dashboard.CleanerDashboardActivity;
import com.lithium.easyclean.mainPackage.dashboard.UserDashboardActivity;

import java.util.Objects;

public class EnterPasswordActivity extends AppCompatActivity {
    private static final String TAG = "EnterPasswordActivity";
    ImageButton loginButton;
    TextView forgetPassword;
    TextInputEditText passwordEditText;
    private FirebaseAuth mAuth;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        ProgressBar progressBar = findViewById(R.id.progressBar1);


        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        mAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.login_button);
        passwordEditText = findViewById(R.id.editTextPassword);
        forgetPassword = findViewById(R.id.forget_password);
        passwordEditText.requestFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Reset Password");
        builder.setMessage("Password reset link will be sent to " + email);
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
//                        if(email.equals("iit2019232@iiita.ac.in"))
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    Toast.makeText(EnterPasswordActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();


        forgetPassword.setOnClickListener(v -> dialog.show());

        passwordEditText.setOnEditorActionListener(
                (v, actionId, event) -> {
                    // Identifier of the action. This will be either the identifier you supplied,
                    // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        progressBar.setVisibility(View.VISIBLE);
                        password = Objects.requireNonNull(passwordEditText.getText()).toString();
                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(getApplicationContext(), "Please enter password...", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(EnterPasswordActivity.this, task -> {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
//                                        updateUI(user);


                                            assert user != null;
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("admins").child(user.getUid());

                                            ValueEventListener eventListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        Toast.makeText(EnterPasswordActivity.this, "Logged in as an Admin!", Toast.LENGTH_SHORT).show();
                                                        Intent intent1 = new Intent(EnterPasswordActivity.this, AdminDashboardActivity.class);
                                                        progressBar.setVisibility(View.GONE);
                                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent1);
                                                        finish();
                                                    } else {
                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("cleaners").child(user.getUid());

                                                        ValueEventListener eventListener2 = new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                                                if (dataSnapshot1.exists()) {
                                                                    Toast.makeText(EnterPasswordActivity.this, "Logged in as a Cleaner!", Toast.LENGTH_SHORT).show();
                                                                    Intent intent1 = new Intent(EnterPasswordActivity.this, CleanerDashboardActivity.class);
                                                                    progressBar.setVisibility(View.GONE);
                                                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    startActivity(intent1);
                                                                } else {
                                                                    Toast.makeText(EnterPasswordActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                                                                    Intent intent1 = new Intent(EnterPasswordActivity.this, UserDashboardActivity.class);
                                                                    progressBar.setVisibility(View.GONE);
                                                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    startActivity(intent1);
                                                                }
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                                                            }
                                                        };
                                                        databaseReference1.addListenerForSingleValueEvent(eventListener2);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                                                }
                                            };
                                            databaseReference.addListenerForSingleValueEvent(eventListener);


                                        } else {
                                            // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            Toast.makeText(EnterPasswordActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            forgetPassword.setVisibility(View.VISIBLE);
                                        }
                                    });
                        }
                        return true;
                    }
                    // Return true if you have consumed the action, else false.
                    return false;
                });

        loginButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            password = Objects.requireNonNull(passwordEditText.getText()).toString();
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Please enter password...", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(EnterPasswordActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
//                                        updateUI(user);


                                assert user != null;
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("admins").child(user.getUid());

                                ValueEventListener eventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Toast.makeText(EnterPasswordActivity.this, "Logged in as an Admin!", Toast.LENGTH_SHORT).show();
                                            Intent intent1 = new Intent(EnterPasswordActivity.this, AdminDashboardActivity.class);
                                            progressBar.setVisibility(View.GONE);
                                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent1);
                                            finish();
                                        } else {
                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("cleaners").child(user.getUid());

                                            ValueEventListener eventListener2 = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot1) {
                                                    if (dataSnapshot1.exists()) {
                                                        Toast.makeText(EnterPasswordActivity.this, "Logged in as a Cleaner!", Toast.LENGTH_SHORT).show();
                                                        Intent intent1 = new Intent(EnterPasswordActivity.this, CleanerDashboardActivity.class);
                                                        progressBar.setVisibility(View.GONE);
                                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent1);
                                                    } else {
                                                        Toast.makeText(EnterPasswordActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                                                        Intent intent1 = new Intent(EnterPasswordActivity.this, UserDashboardActivity.class);
                                                        progressBar.setVisibility(View.GONE);
                                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent1);
                                                    }
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                                                }
                                            };
                                            databaseReference1.addListenerForSingleValueEvent(eventListener2);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                                    }
                                };
                                databaseReference.addListenerForSingleValueEvent(eventListener);


                            } else {
                                // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(EnterPasswordActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                forgetPassword.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });


    }
}