package com.lithium.easyclean.mainPackage.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.admin.ToiletListActivity;
import com.lithium.easyclean.mainPackage.start.Toilet;

public class ViewToiletActivity extends AppCompatActivity {

    private static final String TAG = "ViewToiletActivity";
    int type;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_view_toilet);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        Intent i = getIntent();
        Toilet toilet = (Toilet) i.getSerializableExtra("toilet");
        String id = toilet.getId();
        int turbidity = toilet.getTurbidity();
        String cleaner = toilet.getCleaner();
        String name = toilet.getName();
        TextView idView = findViewById(R.id.iddd);
        TextView nameView = findViewById(R.id.name);
        TextView cleanerView = findViewById(R.id.cleaner);
        TextView turbidityView = findViewById(R.id.turbidity);
        idView.setText(id);
        nameView.setText(name);
        cleanerView.setText(cleaner);
        turbidityView.setText(String.valueOf(turbidity));

        DatabaseReference.CompletionListener mRemoveListener =
                (error, ref) -> {
                    if (error == null) {
                        Intent i12 =  new Intent(ViewToiletActivity.this, ToiletListActivity.class);
                        startActivity(i12);
                        finish();
                    } else {
                        Toast.makeText(ViewToiletActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                };


        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            Intent i12 =  new Intent(ViewToiletActivity.this, ToiletListActivity.class);
            startActivity(i12);
            finish();
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            databaseReference.child("toilets").child(id).removeValue(mRemoveListener);
        });


        AlertDialog.Builder nameDialog = new AlertDialog.Builder(this);
        nameDialog.setCancelable(true);
        nameDialog.setTitle("Change Name");
        EditText nameInput = new EditText(this);
        nameInput.setHint("Enter new name");
        nameDialog.setView(nameInput);
        nameDialog.setPositiveButton("Confirm",
                (dialog, which) -> {
                    String newName = nameInput.getText().toString();
                    if(newName!=null) {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference();
                        databaseReference.child("toilets").child(id).child("name").setValue(newName, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    Toast.makeText(ViewToiletActivity.this, "Name updated to "+newName, Toast.LENGTH_SHORT).show();
                                    nameView.setText(newName);
                                } else {
                                    Toast.makeText(ViewToiletActivity.this, "Error.."+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent i2 = new Intent(ViewToiletActivity.this, ToiletListActivity.class);
                                    startActivity(i2);
                                    finish();
                                }

                            }
                        });
                    }
                    else
                        Toast.makeText(this, "New name cannot be empty..", Toast.LENGTH_SHORT).show();
                });

        nameDialog.setNegativeButton("Cancel", (dialog, which) -> {
        });
        AlertDialog nameAlert = nameDialog.create();


        ImageButton changeNameButton = findViewById(R.id.edit_name);
        changeNameButton.setOnClickListener(v -> {
            changeNameButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed, null));
            nameAlert.show();
        });

//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
//        EditText emailInput = new EditText(this);
//        emailInput.setHint("Enter new email");
//        EditText email2Input = new EditText(this);
//        email2Input.setHint("Confirm email");
//        layout.addView(emailInput);
//        layout.addView(email2Input);
//
//        AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);
//        emailDialog.setCancelable(true);
//        emailDialog.setTitle("Change Email");
//        emailDialog.setView(layout);
//        emailDialog.setPositiveButton("Confirm",
//                new DialogInterface.OnClickListener() {
//                    String email;
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        progressBar.setVisibility(View.VISIBLE);
//                        String newEmail = emailInput.getText().toString();
//                        if (newEmail.equals(email2Input.getText().toString())) {
//
//                            FirebaseOptions options = new FirebaseOptions.Builder()
//                                    .setApplicationId("1:16498016959:android:f5512cb564ebd13fff7c0a") // Required for Analytics.
//                                    .setApiKey("AIzaSyB9dqPlUm9hC6dUecxyV5KiGwlKIf1YIJQ") // Required for Auth.
//                                    .setDatabaseUrl("https://easyclean-se-default-rtdb.firebaseio.com/") // Required for RTDB.
//                                    .build();
//                            FirebaseApp.initializeApp(ViewUserActivity.this /* Context */, options, "secondary");
//
//// Retrieve my other app.
//                            FirebaseApp app = FirebaseApp.getInstance("secondary");
//// Get the database for the other app.
//                            FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(app);
//
//                            secondaryAuth.signInWithEmailAndPassword(email, password)
//                                    .addOnCompleteListener(ViewUserActivity.this, task -> {
//                                        if (task.isSuccessful()) {
//                                            // Sign in success, update UI with the signed-in user's information
////                                        Log.d(TAG, "signInWithEmail:success");
//                                            FirebaseUser secUser = secondaryAuth.getCurrentUser();
//                                            AuthCredential credential1 = EmailAuthProvider
//                                                    .getCredential(email, password); // Current Login Credentials \\
//                                            // Prompt the user to re-provide their sign-in credentials
//                                            if (secUser != null)
//                                                secUser.reauthenticate(credential1)
//                                                        .addOnCompleteListener(task13 -> {
//                                                            if (task13.isSuccessful()) {
//                                                                Log.d(TAG, "User re-authenticated.");
//                                                                //Now change your email address \\
//                                                                //----------------Code for Changing Email Address----------\\
//                                                                FirebaseUser secUser1 = FirebaseAuth.getInstance().getCurrentUser();
//                                                                if (secUser1 != null)
//                                                                    secUser1.updateEmail(newEmail)
//                                                                            .addOnCompleteListener(task131 -> {
//                                                                                if (task131.isSuccessful()) {
//                                                                                    Log.d(TAG, "User email address updated.");
//
//                                                                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                                                                                    DatabaseReference databaseReference = firebaseDatabase.getReference();
//                                                                                    if (type == 3)
//                                                                                        databaseReference.child("users").child(uid).child("email").setValue(newEmail);
//                                                                                    else if (type == 2)
//                                                                                        databaseReference.child("cleaners").child(uid).child("email").setValue(newEmail);
//                                                                                    else if (type == 1)
//                                                                                        databaseReference.child("admins").child(uid).child("email").setValue(newEmail);
//                                                                                    emailView.setText(newEmail);
//                                                                                    Toast.makeText(ViewUserActivity.this, "Email changed to " + newEmail, Toast.LENGTH_LONG).show();
//                                                                                    progressBar.setVisibility(View.GONE);
//
//                                                                                } else {
//                                                                                    Toast.makeText(ViewUserActivity.this, "Error! Please Try again.", Toast.LENGTH_LONG).show();
//                                                                                    progressBar.setVisibility(View.GONE);
//                                                                                }
//                                                                                secondaryAuth.signOut();
//                                                                                app.delete();
//                                                                                Intent i14 = null;
//                                                                                if (type == 3)
//                                                                                    i14 = new Intent(ViewUserActivity.this, UserListActivity.class);
//                                                                                else if (type == 2)
//                                                                                    i14 = new Intent(ViewUserActivity.this, CleanerListActivity.class);
//                                                                                else if (type == 1)
//                                                                                    i14 = new Intent(ViewUserActivity.this, AdminListActivity.class);
//                                                                                startActivity(i14);
//                                                                                finish();
//
//                                                                            });
//                                                            } else {
//                                                                Toast.makeText(ViewUserActivity.this, "Please Try again.", Toast.LENGTH_LONG).show();
//                                                                progressBar.setVisibility(View.GONE);
//                                                                secondaryAuth.signOut();
//                                                                app.delete();
//                                                                Intent i14 = null;
//                                                                if (type == 3)
//                                                                    i14 = new Intent(ViewUserActivity.this, UserListActivity.class);
//                                                                else if (type == 2)
//                                                                    i14 = new Intent(ViewUserActivity.this, CleanerListActivity.class);
//                                                                else if (type == 1)
//                                                                    i14 = new Intent(ViewUserActivity.this, AdminListActivity.class);
//                                                                startActivity(i14);
//                                                                finish();
//                                                            }
//
//                                                        });
//
//
//                                        }
//                                    });
//                        } else {
//                            Toast.makeText(ViewUserActivity.this, "Emails don't match! Please Try again.", Toast.LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                });
//
//
//        emailDialog.setNegativeButton("Cancel", (dialog, which) -> {
//        });
//        AlertDialog emailAlert = emailDialog.create();
//
//
//        ImageButton changeEmailButton = findViewById(R.id.edit_email);
//        changeEmailButton.setOnClickListener(v -> {
//            changeEmailButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed, null));
//            emailAlert.show();
//        });
//
//
////            secondaryAuth.createUserWithEmailAndPassword(email, password)
////                    .addOnCompleteListener(ViewUserActivity.this, new OnCompleteListener<AuthResult>() {
////                        @Override
////                        public void onComplete(@NonNull Task<AuthResult> task) {
////                            if (task.isSuccessful()) {
////
////                                // Sign in success, update UI with the signed-in user's information
////
////                                FirebaseUser user = secondaryAuth.getCurrentUser();
////                                String uid = user.getUid();
////
////                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
////                                DatabaseReference databaseReference = firebaseDatabase.getReference();
////                                databaseReference.child("users").child(uid).removeValue();
////
////                                user.updateProfile(profileUpdates)
////                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                            @Override
////                                            public void onComplete(@NonNull Task<Void> task) {
////                                                if (task.isSuccessful()) {
////                                                    Toast.makeText(NewUserActivity.this, "Profile created!", Toast.LENGTH_SHORT).show();
////                                                    secondaryAuth.signOut();
////                                                    app.delete();
////                                                    Intent intent=new Intent(NewUserActivity.this, UserListActivity.class);
////                                                    startActivity(intent);
////                                                    finish();
////
////                                                }
////                                            }
////                                        });
////
////
////
////                            } else {
////                                // If sign in fails, display a message to the user.
////
////                                Toast.makeText(NewUserActivity.this, "Authentication failed.",
////                                        Toast.LENGTH_SHORT).show();
////
////                                Intent intent=new Intent(NewUserActivity.this, NewUserActivity.class);
////                                startActivity(intent);
////                                finish();
////
////                            }
////                        }
////                    });

    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        Intent i = new Intent(ViewToiletActivity.this, ToiletListActivity.class);
        startActivity(i);
        finish();
    }
}