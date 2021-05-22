package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.annotation.SuppressLint;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.User;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ViewUserActivity extends AppCompatActivity {
    private static final String TAG = "ViewUserActivity";
    int type;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_view_user);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        Intent i = getIntent();
        User user = (User) i.getSerializableExtra("user");
        type = i.getIntExtra("type", 3);
        String uid = user.getUid();
        AtomicReference<String> email = new AtomicReference<>(user.getEmail());
        String name = user.getName();
        String password = user.getPassword();
        TextView uidView = findViewById(R.id.uid);
        TextView nameView = findViewById(R.id.name);
        TextView emailView = findViewById(R.id.email);
        TextView userTypeView = findViewById(R.id.user_type);
        uidView.setText(uid);
        nameView.setText(name);
        emailView.setText(email.get());
        if (type == 3) userTypeView.setText("USER");
        else if (type == 2) userTypeView.setText("CLEANER");
        else if (type == 1) userTypeView.setText("ADMIN");


        DatabaseReference.CompletionListener mRemoveListener =
                (error, ref) -> {
                    if (error == null) {
//                        Toast.makeText(ViewUserActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                        FirebaseOptions options = new FirebaseOptions.Builder()
                                .setApplicationId("1:16498016959:android:f5512cb564ebd13fff7c0a") // Required for Analytics.
                                .setApiKey("AIzaSyB9dqPlUm9hC6dUecxyV5KiGwlKIf1YIJQ") // Required for Auth.
                                .setDatabaseUrl("https://easyclean-se-default-rtdb.firebaseio.com/") // Required for RTDB.
                                .build();
                        FirebaseApp.initializeApp(ViewUserActivity.this /* Context */, options, "secondary");

// Retrieve my other app.
                        FirebaseApp app = FirebaseApp.getInstance("secondary");
// Get the database for the other app.
                        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(app);
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email.get(), password);

                        secondaryAuth.signInWithEmailAndPassword(email.get(), password)
                                .addOnCompleteListener(ViewUserActivity.this, task -> {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");

//                                        updateUI(user);
                                        FirebaseUser firebaseUser = secondaryAuth.getCurrentUser();
                                        // Prompt the user to re-provide their sign-in credentials
                                        assert firebaseUser != null;
                                        firebaseUser.reauthenticate(credential)
                                                .addOnCompleteListener(task1 -> firebaseUser.delete()
                                                        .addOnCompleteListener(task11 -> {
                                                            if (task11.isSuccessful()) {
                                                                Toast.makeText(ViewUserActivity.this, "Deleted.", Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.GONE);
                                                                secondaryAuth.signOut();
                                                                app.delete();
                                                                Intent i1 = null;
                                                                if (type == 3)
                                                                    i1 = new Intent(ViewUserActivity.this, UserListActivity.class);
                                                                else if (type == 2)
                                                                    i1 = new Intent(ViewUserActivity.this, CleanerListActivity.class);
                                                                else if (type == 1)
                                                                    i1 = new Intent(ViewUserActivity.this, AdminListActivity.class);
                                                                startActivity(i1);
                                                                finish();

                                                            }
                                                        }));

                                    } else {
                                        // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(ViewUserActivity.this, "Deletion failed.",
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        app.delete();
                                    }
                                });

                    } else {
                        Toast.makeText(ViewUserActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                };


        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {

            Intent i12 = null;
            if (type == 3) i12 = new Intent(ViewUserActivity.this, UserListActivity.class);
            else if (type == 2) i12 = new Intent(ViewUserActivity.this, CleanerListActivity.class);
            else if (type == 1) i12 = new Intent(ViewUserActivity.this, AdminListActivity.class);
            startActivity(i12);
            finish();
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            if (type == 3) databaseReference.child("users").child(uid).removeValue(mRemoveListener);
            else if (type == 2)
                databaseReference.child("cleaners").child(uid).removeValue(mRemoveListener);
            else if (type == 1)
                databaseReference.child("admins").child(uid).removeValue(mRemoveListener);

        });



final Dialog nameDialog = new Dialog(ViewUserActivity.this);
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
                String newName = nameInput.getText().toString();
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApplicationId("1:16498016959:android:f5512cb564ebd13fff7c0a") // Required for Analytics.
                        .setApiKey("AIzaSyB9dqPlUm9hC6dUecxyV5KiGwlKIf1YIJQ") // Required for Auth.
                        .setDatabaseUrl("https://easyclean-se-default-rtdb.firebaseio.com/") // Required for RTDB.
                        .build();
                FirebaseApp.initializeApp(ViewUserActivity.this /* Context */, options, "secondary");

// Retrieve my other app.
                FirebaseApp app = FirebaseApp.getInstance("secondary");
// Get the database for the other app.
                FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(app);

                secondaryAuth.signInWithEmailAndPassword(email.get(), password)
                        .addOnCompleteListener(ViewUserActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser secUser = secondaryAuth.getCurrentUser();
                                // Prompt the user to re-provide their sign-in credentials
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newName)
                                        .build();
                                if (secUser != null)
                                    secUser.updateProfile(profileUpdates).addOnCompleteListener(task12 -> {
                                        if (task12.isSuccessful()) {

                                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                            DatabaseReference databaseReference = firebaseDatabase.getReference();
                                            if (type == 3)
                                                databaseReference.child("users").child(uid).child("name").setValue(newName);
                                            else if (type == 2)
                                                databaseReference.child("cleaners").child(uid).child("name").setValue(newName);
                                            else if (type == 1)
                                                databaseReference.child("admins").child(uid).child("name").setValue(newName);
                                            nameView.setText(newName);
                                            Toast.makeText(ViewUserActivity.this, "Name changed to " + newName, Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                            app.delete();
                                        } else {
                                            Toast.makeText(ViewUserActivity.this, "Name update failed, please try again", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                            secondaryAuth.signOut();
                                            app.delete();
                                            Intent i13 = null;
                                            if (type == 3)
                                                i13 = new Intent(ViewUserActivity.this, UserListActivity.class);
                                            else if (type == 2)
                                                i13 = new Intent(ViewUserActivity.this, CleanerListActivity.class);
                                            else if (type == 1)
                                                i13 = new Intent(ViewUserActivity.this, AdminListActivity.class);
                                            startActivity(i13);
                                            finish();
                                        }
                                    });

                            } else {
                                // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(ViewUserActivity.this, "Name change failed.",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });

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



        final Dialog emailDialog = new Dialog(ViewUserActivity.this);
        emailDialog.setContentView(R.layout.user_email_dialog_layout);
        emailDialog.setCancelable(true);

        TextInputEditText emailInput1 = emailDialog.findViewById(R.id.change_email1);
        TextInputEditText emailInput2 = emailDialog.findViewById(R.id.change_email2);
        Button confirmEmail = emailDialog.findViewById(R.id.confirm);
        Button cancelEmail = emailDialog.findViewById(R.id.cancel);
        emailDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        emailDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);


        confirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (emailInput1.getText().toString().equals("") || emailInput2.getText().toString().equals("")) {
                    Toast.makeText(ViewUserActivity.this, "Email cannot be empty", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                } else {

                    String newEmail = emailInput1.getText().toString();
                    if (newEmail.equals(emailInput2.getText().toString())) {

                        FirebaseOptions options = new FirebaseOptions.Builder()
                                .setApplicationId("1:16498016959:android:f5512cb564ebd13fff7c0a") // Required for Analytics.
                                .setApiKey("AIzaSyB9dqPlUm9hC6dUecxyV5KiGwlKIf1YIJQ") // Required for Auth.
                                .setDatabaseUrl("https://easyclean-se-default-rtdb.firebaseio.com/") // Required for RTDB.
                                .build();
                        FirebaseApp.initializeApp(ViewUserActivity.this /* Context */, options, "secondary2");

// Retrieve my other app.
                        FirebaseApp app = FirebaseApp.getInstance("secondary2");
// Get the database for the other app.
                        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(app);
                        secondaryAuth.fetchSignInMethodsForEmail(newEmail)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {

                        secondaryAuth.signInWithEmailAndPassword(email.get(), password)
                                .addOnCompleteListener(ViewUserActivity.this, task -> {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser secUser = secondaryAuth.getCurrentUser();
                                        AuthCredential credential1 = EmailAuthProvider
                                                .getCredential(email.get(), password); // Current Login Credentials \\
                                        // Prompt the user to re-provide their sign-in credentials
                                        if (secUser != null)
                                            secUser.reauthenticate(credential1)
                                                    .addOnCompleteListener(task13 -> {
                                                        if (task13.isSuccessful()) {
                                                            Log.d(TAG, "User re-authenticated.");
                                                            //Now change your email address \\
                                                            //----------------Code for Changing Email Address----------\\
                                                            FirebaseUser secUser1 = secondaryAuth.getCurrentUser();
                                                            if (secUser1 != null)
                                                                secUser1.updateEmail(newEmail)
                                                                        .addOnCompleteListener(task131 -> {
                                                                            if (task131.isSuccessful()) {
                                                                                Log.d(TAG, "User email address updated.");

                                                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                                                DatabaseReference databaseReference = firebaseDatabase.getReference();
                                                                                if (type == 3)
                                                                                    databaseReference.child("users").child(uid).child("email").setValue(newEmail);
                                                                                else if (type == 2)
                                                                                    databaseReference.child("cleaners").child(uid).child("email").setValue(newEmail);
                                                                                else if (type == 1)
                                                                                    databaseReference.child("admins").child(uid).child("email").setValue(newEmail);
                                                                                emailView.setText(newEmail);

                                                                                Toast.makeText(ViewUserActivity.this, "Email changed to " + newEmail, Toast.LENGTH_LONG).show();
                                                                                progressBar.setVisibility(View.GONE);
                                                                                app.delete();
                                                                                email.set(newEmail);
                                                                            } else {
                                                                                Toast.makeText(ViewUserActivity.this, "Error! Please Try again.", Toast.LENGTH_LONG).show();
                                                                                progressBar.setVisibility(View.GONE);
                                                                                app.delete();
                                                                                Intent i14 = null;
                                                                                if (type == 3)
                                                                                    i14 = new Intent(ViewUserActivity.this, UserListActivity.class);
                                                                                else if (type == 2)
                                                                                    i14 = new Intent(ViewUserActivity.this, CleanerListActivity.class);
                                                                                else if (type == 1)
                                                                                    i14 = new Intent(ViewUserActivity.this, AdminListActivity.class);
                                                                                startActivity(i14);
                                                                                finish();
                                                                            }
                                                                            secondaryAuth.signOut();
                                                                            app.delete();
                                                                        });
                                                        } else {
                                                            Toast.makeText(ViewUserActivity.this, "Please Try again.", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                            secondaryAuth.signOut();
                                                            app.delete();
                                                            Intent i14 = null;
                                                            if (type == 3)
                                                                i14 = new Intent(ViewUserActivity.this, UserListActivity.class);
                                                            else if (type == 2)
                                                                i14 = new Intent(ViewUserActivity.this, CleanerListActivity.class);
                                                            else if (type == 1)
                                                                i14 = new Intent(ViewUserActivity.this, AdminListActivity.class);
                                                            startActivity(i14);
                                                            finish();
                                                        }
                                                    });


                                    }

                                    else {
                                        Toast.makeText(ViewUserActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        app.delete();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                                    } else {
                                        app.delete();
                                        progressBar.setVisibility(View.GONE);
                                        try {
                                            throw Objects.requireNonNull(task1.getException());
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            Toast.makeText(ViewUserActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Toast.makeText(ViewUserActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                    } else {
                        Toast.makeText(ViewUserActivity.this, "Emails don't match! Please Try again.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                emailInput1.setText("");
                emailInput2.setText("");
                emailDialog.dismiss();
            }
        });

     cancelEmail.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             emailInput1.setText("");
             emailInput2.setText("");
             emailDialog.dismiss();
         }
     });


     Button editEmail = findViewById(R.id.edit_email);
     editEmail.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             emailDialog.show();
         }
     });
    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        Intent i = null;
        if (type == 3) i = new Intent(ViewUserActivity.this, UserListActivity.class);
        else if (type == 2) i = new Intent(ViewUserActivity.this, CleanerListActivity.class);
        else if (type == 1) i = new Intent(ViewUserActivity.this, AdminListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }
}