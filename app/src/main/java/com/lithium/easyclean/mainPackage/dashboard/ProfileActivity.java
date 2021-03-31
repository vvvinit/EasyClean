package com.lithium.easyclean.mainPackage.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.EnterEmailActivity;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String TAG = "ProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent i = getIntent();
        int dashboardType = i.getIntExtra("type",3);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String s = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        TextView textView = findViewById(R.id.name);
        TextView textView1 = findViewById(R.id.email);
        textView.setText(s);
        textView1.setText(userEmail);

        Button button = (Button)findViewById(R.id.sign_out_button);
        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(ProfileActivity.this, EnterEmailActivity.class);
            startActivity(intent);
            finish();
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
                        String name = nameInput.getText().toString();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        if(user!=null)
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    String uid = user.getUid();

                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference();
                                    if(dashboardType==0)
                                    databaseReference.child("admins").child(uid).child("name").setValue(name);
                                    if(dashboardType==1)
                                        databaseReference.child("cleaners").child(uid).child("name").setValue(name);
                                    if(dashboardType==2)
                                        databaseReference.child("users").child(uid).child("name").setValue(name);
                                    textView.setText(name);
                                    Toast.makeText(ProfileActivity.this,"Name changed to "+name,Toast.LENGTH_LONG).show();
                                }else
                                    Toast.makeText(ProfileActivity.this,"Name update failed, please try again",Toast.LENGTH_LONG).show();
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
        final EditText passwordInput = new EditText (this);
        passwordInput.setHint("Enter Password");
        layout.addView(emailInput);
        layout.addView(email2Input);
        layout.addView(passwordInput);

        AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);
        emailDialog.setCancelable(true);
        emailDialog.setTitle("Change Email");

        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        emailDialog.setView(layout);
        emailDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    String email;
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newEmail = emailInput.getText().toString();
                        if(newEmail.equals(email2Input.getText().toString())) {
                            String password = passwordInput.getText().toString();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user!=null)
                            email = user.getEmail();
                            assert email != null;
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(email, password); // Current Login Credentials \\
                            // Prompt the user to re-provide their sign-in credentials
                            if(user!=null)
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Log.d(TAG, "User re-authenticated.");
                                                //Now change your email address \\
                                                //----------------Code for Changing Email Address----------\\
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                if (user != null)
                                                    user.updateEmail(newEmail)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d(TAG, "User email address updated.");
                                                                        String uid = user.getUid();

                                                                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                                        DatabaseReference databaseReference = firebaseDatabase.getReference();
                                                                        if(dashboardType==0)
                                                                            databaseReference.child("admins").child(uid).child("email").setValue(newEmail);
                                                                        if(dashboardType==1)
                                                                            databaseReference.child("cleaners").child(uid).child("email").setValue(newEmail);
                                                                        if(dashboardType==2)
                                                                            databaseReference.child("users").child(uid).child("email").setValue(newEmail);
                                                                        textView1.setText(newEmail);
                                                                        Toast.makeText(ProfileActivity.this,"Email changed to "+newEmail,Toast.LENGTH_LONG).show();

                                                                    }
                                                                    else {
                                                                        Toast.makeText(ProfileActivity.this,"Error! Please Try again.",Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                            }
                                            else {
                                                Toast.makeText(ProfileActivity.this,"Please Try again.",Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                        }
                        else {
                            Toast.makeText(ProfileActivity.this,"Emails don't match! Please Try again.",Toast.LENGTH_LONG).show();
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

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if(dashboardType==0)
                intent = new Intent(ProfileActivity.this, AdminDashboardActivity.class);
                if(dashboardType==1)
                    intent = new Intent(ProfileActivity.this, CleanerDashboardActivity.class);
                if(dashboardType==2)
                    intent = new Intent(ProfileActivity.this, UserDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}