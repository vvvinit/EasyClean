package com.lithium.easyclean.mainPackage.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.lithium.easyclean.R;

import java.util.Objects;

public class EnterEmailActivity extends AppCompatActivity {
    TextInputEditText emailTextInput;

    ImageButton nextButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_enter_email);

        emailTextInput = findViewById(R.id.editTextUsername);
        emailTextInput.requestFocus();
        nextButton = findViewById(R.id.login_button);

        Animation rotation = AnimationUtils.loadAnimation(EnterEmailActivity.this, R.anim.rotate);
        rotation.setFillAfter(true);


        mAuth = FirebaseAuth.getInstance();
        ProgressBar progressBar = findViewById(R.id.progressBar1);

        nextButton.setOnClickListener(v -> {
            nextButton.startAnimation(rotation);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextButton.clearAnimation();
                }
            }, 1000);

            progressBar.setVisibility(View.VISIBLE);
            String email = Objects.requireNonNull(emailTextInput.getText()).toString();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {

                mAuth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                boolean check = !Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).isEmpty();
                                Intent intent;
                                if (!check) {
                                    intent = new Intent(EnterEmailActivity.this, SignUpActivity.class);
                                } else {
                                    intent = new Intent(EnterEmailActivity.this, EnterPasswordActivity.class);
                                }
                                intent.putExtra("email", email);
                                progressBar.setVisibility(View.GONE);
                                startActivity(intent);
                            } else {

                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(EnterEmailActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }

        });

        emailTextInput.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        progressBar.setVisibility(View.VISIBLE);
                        String email = Objects.requireNonNull(emailTextInput.getText()).toString();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(getApplicationContext(), "Please enter Email", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            mAuth.fetchSignInMethodsForEmail(email)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            boolean check = !Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).isEmpty();
                                            Intent intent;
                                            if (!check) {
                                                intent = new Intent(EnterEmailActivity.this, SignUpActivity.class);
                                            } else {
                                                intent = new Intent(EnterEmailActivity.this, EnterPasswordActivity.class);
                                            }
                                            intent.putExtra("email", email);
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(intent);
                                        } else {
                                            try {
                                                throw Objects.requireNonNull(task.getException());
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                Toast.makeText(EnterEmailActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }
                        return true;
                    }
                    return false;
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}