package com.lithium.easyclean.mainPackage.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.UserDashboardActivity;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    ImageButton loginButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private String name;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        mAuth = FirebaseAuth.getInstance();
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        loginButton = findViewById(R.id.login_button);
        TextInputEditText passwordInput = findViewById(R.id.editPasswordValue);
        TextInputEditText nameInput = findViewById(R.id.editTextName);
        ImageButton signUp = findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);


            if (nameInput != null && passwordInput != null) {
                name = Objects.requireNonNull(nameInput.getText()).toString();
                password = Objects.requireNonNull(passwordInput.getText()).toString();
//                    Toast.makeText(SignUpActivity.this, email+password, Toast.LENGTH_SHORT).show();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, task -> {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                uid = user.getUid();

                                firebaseDatabase = FirebaseDatabase.getInstance();
                                databaseReference = firebaseDatabase.getReference();
                                databaseReference.child("users").child(uid).setValue(new User(name, password, email, uid));
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .setPhotoUri(null)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Profile created!", Toast.LENGTH_SHORT).show();
                                                Intent intent1 = new Intent(SignUpActivity.this, UserDashboardActivity.class);
                                                progressBar.setVisibility(View.GONE);
                                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent1);
                                                finish();
                                            }
                                        });


                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }


        });

        passwordInput.setOnEditorActionListener(
                (v, actionId, event) -> {
                    // Identifier of the action. This will be either the identifier you supplied,
                    // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        progressBar.setVisibility(View.VISIBLE);


                        if (nameInput != null) {
                            name = Objects.requireNonNull(nameInput.getText()).toString();
                            password = Objects.requireNonNull(passwordInput.getText()).toString();
//                    Toast.makeText(SignUpActivity.this, email+password, Toast.LENGTH_SHORT).show();
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(SignUpActivity.this, task -> {
                                        if (task.isSuccessful()) {

                                            // Sign in success, update UI with the signed-in user's information

                                            FirebaseUser user = mAuth.getCurrentUser();
                                            assert user != null;
                                            uid = user.getUid();

                                            firebaseDatabase = FirebaseDatabase.getInstance();
                                            databaseReference = firebaseDatabase.getReference();
                                            databaseReference.child("users").child(uid).setValue(new User(name, password, email, uid));
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(name)
                                                    .setPhotoUri(null)
                                                    .build();

                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            Toast.makeText(SignUpActivity.this, "Profile created!", Toast.LENGTH_SHORT).show();
                                                            Intent intent1 = new Intent(SignUpActivity.this, UserDashboardActivity.class);
                                                            progressBar.setVisibility(View.GONE);
                                                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent1);
                                                            finish();
                                                        }
                                                    });


                                        } else {
                                            // If sign in fails, display a message to the user.

                                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                        }

                        return true;
                    }
                    // Return true if you have consumed the action, else false.
                    return false;
                });



    }


}