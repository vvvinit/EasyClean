package com.lithium.easyclean.mainPackage.start;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.lithium.easyclean.R;

public class EnterEmailActivity extends AppCompatActivity {
    TextInputEditText emailTextInput;
    ImageButton nextButton;
    String email;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_email);

        emailTextInput = findViewById(R.id.editTextUsername);
        nextButton = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTextInput.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        boolean check = !task.getResult().getSignInMethods().isEmpty();
                                        if (!check) {
                                            Intent intent = new Intent(EnterEmailActivity.this,SignUpActivity.class);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(EnterEmailActivity.this,EnterPasswordActivity.class);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                    else {
                                        try {
                                            throw task.getException();
                                        } catch(FirebaseAuthInvalidCredentialsException e) {
                                            Toast.makeText(EnterEmailActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                }
            }


        });
    }
}