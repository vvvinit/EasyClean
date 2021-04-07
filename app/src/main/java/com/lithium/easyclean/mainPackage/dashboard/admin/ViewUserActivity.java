package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.User;

public class ViewUserActivity extends AppCompatActivity {

    private static final String TAG = "ViewUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        Intent i = getIntent();
        User user = (User) i.getSerializableExtra("user");
        String uid = user.getUid();
        String email = user.getEmail();
        String name = user.getName();
        String password = user.getPassword();
        TextView uidView = findViewById(R.id.uid);
        TextView nameView = findViewById(R.id.name);
        TextView emailView = findViewById(R.id.email);
        uidView.setText(uid);
        nameView.setText(name);
        emailView.setText(email);

        DatabaseReference.CompletionListener mRemoveListener =
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(ViewUserActivity.this, "Removed", Toast.LENGTH_SHORT).show();
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
                                    .addOnCompleteListener(ViewUserActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
                                                FirebaseUser secUser = secondaryAuth.getCurrentUser();

//                                        updateUI(user);
                                                FirebaseUser firebaseUser = secondaryAuth.getCurrentUser();
                                                // Prompt the user to re-provide their sign-in credentials
                                                firebaseUser.reauthenticate(credential)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                firebaseUser.delete()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(ViewUserActivity.this, "Deleted.",Toast.LENGTH_SHORT).show();
                                                                                    secondaryAuth.signOut();
                                                                                    app.delete();
                                                                                    Intent intent=new Intent(ViewUserActivity.this, UserListActivity.class);
                                                                                    startActivity(intent);
                                                                                    finish();

                                                                                }
                                                                            }
                                                                        });

                                                            }
                                                        });

                                            } else {
                                                // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                Toast.makeText(ViewUserActivity.this, "Deletion failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(ViewUserActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                        }
                    }
                };


        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUserActivity.this, UserListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                databaseReference.child("users").child(uid).removeValue(mRemoveListener);
            }
        });


        AlertDialog.Builder nameDialog = new AlertDialog.Builder(this);
        nameDialog.setCancelable(true);
        nameDialog.setTitle("Change Name");
        final EditText nameInput = new EditText (this);
        nameInput.setHint("Enter new name");
        nameDialog.setView(nameInput);
        nameDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email, password);

                        secondaryAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(ViewUserActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
                                            FirebaseUser secUser = secondaryAuth.getCurrentUser();
                                            // Prompt the user to re-provide their sign-in credentials
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(newName)
                                                    .build();
                                            if(secUser!=null)
                                                secUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            String secUid = secUser.getUid();

                                                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                            DatabaseReference databaseReference = firebaseDatabase.getReference();
                                                            databaseReference.child("users").child(secUid).child("name").setValue(newName);
                                                            nameView.setText(newName);
                                                            Toast.makeText(ViewUserActivity.this,"Name changed to "+newName,Toast.LENGTH_LONG).show();
                                                        } else
                                                        Toast.makeText(ViewUserActivity.this,"Name update failed, please try again",Toast.LENGTH_LONG).show();
                                                        secondaryAuth.signOut();
                                                        app.delete();
                                                        Intent intent = new Intent(ViewUserActivity.this, UserListActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                        } else {
                                            // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            Toast.makeText(ViewUserActivity.this, "Name change failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });

        nameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog nameAlert = nameDialog.create();



        ImageButton changeNameButton = (ImageButton) findViewById(R.id.edit_name);
        changeNameButton.setOnClickListener(v -> {
            changeNameButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed,null));
            nameAlert.show();
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
        final EditText emailInput = new EditText (this);
        emailInput.setHint("Enter new email");
        final EditText email2Input = new EditText (this);
        email2Input.setHint("Confirm email");
        layout.addView(emailInput);
        layout.addView(email2Input);

        AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);
        emailDialog.setCancelable(true);
        emailDialog.setTitle("Change Email");
        emailDialog.setView(layout);
        emailDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    String email;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(email, password);

                            secondaryAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(ViewUserActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
                                                FirebaseUser secUser = secondaryAuth.getCurrentUser();
                                                AuthCredential credential = EmailAuthProvider
                                                        .getCredential(email, password); // Current Login Credentials \\
                                                // Prompt the user to re-provide their sign-in credentials
                                                if (secUser != null)
                                                    secUser.reauthenticate(credential)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d(TAG, "User re-authenticated.");
                                                                        //Now change your email address \\
                                                                        //----------------Code for Changing Email Address----------\\
                                                                        FirebaseUser secUser = FirebaseAuth.getInstance().getCurrentUser();
                                                                        if (secUser != null)
                                                                            secUser.updateEmail(newEmail)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Log.d(TAG, "User email address updated.");

                                                                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                                                                DatabaseReference databaseReference = firebaseDatabase.getReference();
                                                                                                databaseReference.child("users").child(uid).child("email").setValue(newEmail);
                                                                                                emailView.setText(newEmail);
                                                                                                Toast.makeText(ViewUserActivity.this, "Email changed to " + newEmail, Toast.LENGTH_LONG).show();

                                                                                            } else {
                                                                                                Toast.makeText(ViewUserActivity.this, "Error! Please Try again.", Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                            secondaryAuth.signOut();
                                                                                            app.delete();
                                                                                            Intent intent = new Intent(ViewUserActivity.this, UserListActivity.class);
                                                                                            startActivity(intent);
                                                                                            finish();

                                                                                        }
                                                                                    });
                                                                    } else {
                                                                        Toast.makeText(ViewUserActivity.this, "Please Try again.", Toast.LENGTH_LONG).show();
                                                                        secondaryAuth.signOut();
                                                                        app.delete();
                                                                        Intent intent = new Intent(ViewUserActivity.this, UserListActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }

                                                                }
                                                            });


                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(ViewUserActivity.this, "Emails don't match! Please Try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });


                            emailDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog emailAlert = emailDialog.create();



        ImageButton changeEmailButton = (ImageButton) findViewById(R.id.edit_email);
        changeEmailButton.setOnClickListener(v -> {
            changeEmailButton.setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_edit_pressed,null));
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
}