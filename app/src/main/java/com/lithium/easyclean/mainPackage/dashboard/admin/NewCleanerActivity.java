package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.User;

public class NewCleanerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private String name;
    private String uid;
    ImageButton loginButton;
    TextInputEditText nameEditText;
    TextInputEditText passwordEditText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cleaner);


        ImageButton signUp = findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TextInputEditText passwordInput = findViewById(R.id.editPasswordValue);
                TextInputEditText nameInput = findViewById(R.id.editTextName);
                TextInputEditText emailInput = findViewById(R.id.editEmailValue);
                if(nameInput!=null&&passwordInput!=null&&emailInput!=null) {
                    name = nameInput.getText().toString();
                    password = passwordInput.getText().toString();
                    email = emailInput.getText().toString();
                    Toast.makeText(NewCleanerActivity.this, email+password, Toast.LENGTH_SHORT).show();
                    FirebaseOptions options = new FirebaseOptions.Builder()
                            .setApplicationId("1:16498016959:android:f5512cb564ebd13fff7c0a") // Required for Analytics.
                            .setApiKey("AIzaSyB9dqPlUm9hC6dUecxyV5KiGwlKIf1YIJQ") // Required for Auth.
                            .setDatabaseUrl("https://easyclean-se-default-rtdb.firebaseio.com/") // Required for RTDB.
                            .build();
                    FirebaseApp.initializeApp(NewCleanerActivity.this /* Context */, options, "secondary");

// Retrieve my other app.
                    FirebaseApp app = FirebaseApp.getInstance("secondary");
// Get the database for the other app.
                    FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(app);

                    secondaryAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(NewCleanerActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        // Sign in success, update UI with the signed-in user's information

                                        FirebaseUser user = secondaryAuth.getCurrentUser();
                                        String uid = user.getUid();

                                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                        DatabaseReference databaseReference = firebaseDatabase.getReference();
                                        databaseReference.child("cleaners").child(uid).setValue(new User(name,password,email));
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .setPhotoUri(null)
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(NewCleanerActivity.this, "Profile created!", Toast.LENGTH_SHORT).show();
                                                            secondaryAuth.signOut();
                                                            app.delete();
                                                            Intent intent=new Intent(NewCleanerActivity.this, CleanerListActivity.class);
                                                            startActivity(intent);
                                                            finish();

                                                        }
                                                    }
                                                });



                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(NewCleanerActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                        Intent intent=new Intent(NewCleanerActivity.this, NewCleanerActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
                }


            }
        });




    }
}