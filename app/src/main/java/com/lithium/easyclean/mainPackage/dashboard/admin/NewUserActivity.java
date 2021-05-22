package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.User;

import java.util.Objects;

public class NewUserActivity extends AppCompatActivity {

    private String email;
    private String password;
    private String name;

    public NewUserActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_new_user);
        Animation rotation = AnimationUtils.loadAnimation(NewUserActivity.this, R.anim.rotate);
        rotation.setFillAfter(true);
        ImageButton signUp = findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(v -> {
            signUp.startAnimation(rotation);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    signUp.clearAnimation();
                }
            }, 1000);

            TextInputEditText passwordInput = findViewById(R.id.editPasswordValue);
            TextInputEditText nameInput = findViewById(R.id.editTextName);
            TextInputEditText emailInput = findViewById(R.id.editEmailValue);


            if (!Objects.requireNonNull(nameInput.getText()).toString().isEmpty() && !Objects.requireNonNull(passwordInput.getText()).toString().isEmpty() && !Objects.requireNonNull(emailInput.getText()).toString().isEmpty()) {
                name = Objects.requireNonNull(nameInput.getText()).toString();
                password = Objects.requireNonNull(passwordInput.getText()).toString();
                email = Objects.requireNonNull(emailInput.getText()).toString();
//                Toast.makeText(NewUserActivity.this, email + password, Toast.LENGTH_SHORT).show();
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApplicationId("1:16498016959:android:f5512cb564ebd13fff7c0a") // Required for Analytics.
                        .setApiKey("AIzaSyB9dqPlUm9hC6dUecxyV5KiGwlKIf1YIJQ") // Required for Auth.
                        .setDatabaseUrl("https://easyclean-se-default-rtdb.firebaseio.com/") // Required for RTDB.
                        .build();
                FirebaseApp.initializeApp(NewUserActivity.this /* Context */, options, "secondary");

// Retrieve my other app.
                FirebaseApp app = FirebaseApp.getInstance("secondary");
// Get the database for the other app.
                FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(app);

                secondaryAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(NewUserActivity.this, task -> {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = secondaryAuth.getCurrentUser();
                                assert user != null;
                                String uid = user.getUid();

                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference();
                                databaseReference.child("users").child(uid).setValue(new User(name, password, email));
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .setPhotoUri(null)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(NewUserActivity.this, "Profile created!", Toast.LENGTH_SHORT).show();
                                                secondaryAuth.signOut();
                                                app.delete();
                                                Intent intent = new Intent(NewUserActivity.this, UserListActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        });


                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(NewUserActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(NewUserActivity.this, NewUserActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        });
            }

            else {
                Toast.makeText(this, "Please enter required details...", Toast.LENGTH_SHORT).show();
            }

        });


    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        Intent i = new Intent(NewUserActivity.this, UserListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }
}