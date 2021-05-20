package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.User;

public class ViewUserActivity extends AppCompatActivity {
    private static final String TAG = "ViewUserActivity";
    int type;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        Intent i = getIntent();
        User user = (User) i.getSerializableExtra("user");
        type = i.getIntExtra("type", 3);
        String uid = user.getUid();
        String email = user.getEmail();
        String name = user.getName();
        String password = user.getPassword();
        TextView uidView = findViewById(R.id.uid);
        TextView nameView = findViewById(R.id.name);
        TextView emailView = findViewById(R.id.email);
        TextView userTypeView = findViewById(R.id.user_type);
        uidView.setText(uid);
        nameView.setText(name);
        emailView.setText(email);
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
                                .getCredential(email, password);

                        secondaryAuth.signInWithEmailAndPassword(email, password)
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





        ImageButton changeNameButton = findViewById(R.id.edit_name);
        changeNameButton.setOnClickListener(v -> {

            AlertDialog.Builder nameDialog = new AlertDialog.Builder(this);
            nameDialog.setCancelable(true);
            nameDialog.setTitle("Change Name");
            EditText nameInput = new EditText(this);
            nameInput.setHint("Enter new name");
            nameDialog.setView(nameInput);
            nameDialog.setPositiveButton("Confirm",
                    (dialog, which) -> {
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

                        secondaryAuth.signInWithEmailAndPassword(email, password)
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

                    });

            nameDialog.setNegativeButton("Cancel", (dialog, which) -> {
            });

            AlertDialog nameAlert = nameDialog.create();
            changeNameButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed, null));
            nameAlert.show();
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
        EditText emailInput = new EditText(this);
        emailInput.setHint("Enter new email");
        EditText email2Input = new EditText(this);
        email2Input.setHint("Confirm email");
        layout.addView(emailInput);
        layout.addView(email2Input);





        ImageButton changeEmailButton = findViewById(R.id.edit_email);
        changeEmailButton.setOnClickListener(v -> {

            AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);
            emailDialog.setCancelable(true);
            emailDialog.setTitle("Change Email");
            emailDialog.setView(layout);
            emailDialog.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        String email;

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressBar.setVisibility(View.VISIBLE);
                            String newEmail = emailInput.getText().toString();
                            if (newEmail.equals(email2Input.getText().toString())) {

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

                                secondaryAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(ViewUserActivity.this, task -> {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
                                                FirebaseUser secUser = secondaryAuth.getCurrentUser();
                                                AuthCredential credential1 = EmailAuthProvider
                                                        .getCredential(email, password); // Current Login Credentials \\
                                                // Prompt the user to re-provide their sign-in credentials
                                                if (secUser != null)
                                                    secUser.reauthenticate(credential1)
                                                            .addOnCompleteListener(task13 -> {
                                                                if (task13.isSuccessful()) {
                                                                    Log.d(TAG, "User re-authenticated.");
                                                                    //Now change your email address \\
                                                                    //----------------Code for Changing Email Address----------\\
                                                                    FirebaseUser secUser1 = FirebaseAuth.getInstance().getCurrentUser();
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

                                                                                    } else {
                                                                                        Toast.makeText(ViewUserActivity.this, "Error! Please Try again.", Toast.LENGTH_LONG).show();
                                                                                        progressBar.setVisibility(View.GONE);
                                                                                    }
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
                                        });
                            } else {
                                Toast.makeText(ViewUserActivity.this, "Emails don't match! Please Try again.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });


            emailDialog.setNegativeButton("Cancel", (dialog, which) -> {
            });
            AlertDialog emailAlert = emailDialog.create();
            changeEmailButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed, null));
            emailAlert.show();
        });


//            secondaryAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(ViewUserActivity.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//
//                                // Sign in success, update UI with the signed-in user's information
//
//                                FirebaseUser user = secondaryAuth.getCurrentUser();
//                                String uid = user.getUid();
//
//                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                                DatabaseReference databaseReference = firebaseDatabase.getReference();
//                                databaseReference.child("users").child(uid).removeValue();
//
//                                user.updateProfile(profileUpdates)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    Toast.makeText(NewUserActivity.this, "Profile created!", Toast.LENGTH_SHORT).show();
//                                                    secondaryAuth.signOut();
//                                                    app.delete();
//                                                    Intent intent=new Intent(NewUserActivity.this, UserListActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//
//                                                }
//                                            }
//                                        });
//
//
//
//                            } else {
//                                // If sign in fails, display a message to the user.
//
//                                Toast.makeText(NewUserActivity.this, "Authentication failed.",
//                                        Toast.LENGTH_SHORT).show();
//
//                                Intent intent=new Intent(NewUserActivity.this, NewUserActivity.class);
//                                startActivity(intent);
//                                finish();
//
//                            }
//                        }
//                    });

    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        Intent i = null;
        if (type == 3) i = new Intent(ViewUserActivity.this, UserListActivity.class);
        else if (type == 2) i = new Intent(ViewUserActivity.this, CleanerListActivity.class);
        else if (type == 1) i = new Intent(ViewUserActivity.this, AdminListActivity.class);
        startActivity(i);
        finish();
    }
}