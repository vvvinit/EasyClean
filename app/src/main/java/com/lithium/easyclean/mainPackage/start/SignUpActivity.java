package com.lithium.easyclean.mainPackage.start;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.DashboardActivity;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private String name;
    ImageButton loginButton;
    TextInputEditText nameEditText;
    TextInputEditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        mAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.login_button);
        nameEditText = findViewById(R.id.editTextName);
        passwordEditText = findViewById(R.id.editTextPassword);

        ImageButton signUp = findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TextInputEditText passwordInput = findViewById(R.id.editPasswordValue);
                TextInputEditText nameInput = findViewById(R.id.editTextName);
                if(nameInput!=null&&passwordInput!=null) {
                    name = nameInput.getText().toString();
                    password = passwordInput.getText().toString();
                    Toast.makeText(SignUpActivity.this, email+password, Toast.LENGTH_SHORT).show();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information

                                        FirebaseUser user = mAuth.getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .setPhotoUri(null)
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SignUpActivity.this, "Profile created!", Toast.LENGTH_SHORT).show();
                                                            Intent intent=new Intent(SignUpActivity.this, DashboardActivity.class);

                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });

                                       

                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }


            }
        });

    }


}