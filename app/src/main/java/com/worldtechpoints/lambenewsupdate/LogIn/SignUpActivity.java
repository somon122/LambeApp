package com.worldtechpoints.lambenewsupdate.LogIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.worldtechpoints.lambenewsupdate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.worldtechpoints.lambenewsupdate.R.color.colorAccent;

public class SignUpActivity extends AppCompatActivity {

    private TextView gotologin;

    private EditText passwordET, confirmPasswordET, referET, emailET, numberET;
    private Button registerButton;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private ReferCodeClass referCodeClass;
    private List<ReferCodeClass> referList;

    private String referupdatePoint;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        gotologin = findViewById(R.id.gotoLogin_id);
        passwordET = findViewById(R.id.signUpPassword);
        confirmPasswordET = findViewById(R.id.signUpConfirmPassword_id);
        referET = findViewById(R.id.signUpReferCode_id);
        emailET = findViewById(R.id.signUpEmail_id);
        numberET = findViewById(R.id.signUpNumber_id);
        registerButton = findViewById(R.id.signUpButton_id);
        progressBar = findViewById(R.id.signUpProgressBar);
        progressBar.setVisibility(View.GONE);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        auth = FirebaseAuth.getInstance();
        //user = auth.getCurrentUser();

        referCodeClass = new ReferCodeClass();
        referList = new ArrayList<>();

        uniqueUserReferCode();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                registerButton.setEnabled(false);
                registerMethod();

            }
        });


        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!referET.getText().toString().isEmpty()){
                    referUserPoint(s.toString());
                }


            }
        };

        referET.addTextChangedListener(textWatcher);



    }

    private void registerMethod() {

        String password = passwordET.getText().toString();
        String confirmPass = confirmPasswordET.getText().toString();
        final String refer = referET.getText().toString();
        String email = emailET.getText().toString();
        final String number = numberET.getText().toString();

        if (password.isEmpty()) {
            passwordET.setError("Please enter password");
        } else if (confirmPass.isEmpty()) {
            confirmPasswordET.setError("Please enter confirm password");
        } else if (number.isEmpty()) {
            numberET.setError("Please enter confirm password");
        }else if (email.isEmpty()) {
            emailET.setError("Please enter Email");
        } else {

            if (referCodeClass.getmReferCode() != null) {

                progressBar.setVisibility(View.VISIBLE);

                if (referCodeClass.getmReferCode().contains(refer)) {

                    register(email,password,confirmPass,number,refer);


                } else {
                    register(email,password,confirmPass,number,refer);

                }

            } else {

               register(email,password,confirmPass,number,refer);

            }

        }


    }




        private void uniqueUserReferCode () {

            myRef.child("ReferList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        referList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            referCodeClass = snapshot.getValue(ReferCodeClass.class);
                        }
                        referList.add(referCodeClass);

                    } else {
                        Toast.makeText(SignUpActivity.this, "data empty", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


        private void referUserPoint (String referCode){

        try {

            myRef.child("MainPoint").child(referCode).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        String userPoint = dataSnapshot.getValue(String.class);
                        int newPoint = Integer.parseInt(userPoint)+100;
                        referupdatePoint = String.valueOf(newPoint);
                        referET.setTextColor(SignUpActivity.this.getResources().getColor(R.color.colorPrimary));

                    } else {
                        referET.setTextColor(SignUpActivity.this.getResources().getColor(colorAccent));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e ){

            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();

        }



        }


       private void register(String email, String password, String confirmPass,final String number, final String refer){

           if (confirmPass.equals(password)) {


               auth.createUserWithEmailAndPassword(email, confirmPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       if (task.isSuccessful()) {

                           user = auth.getCurrentUser();

                           UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                   .setDisplayName(number)
                                   .build();
                           user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   if (task.isSuccessful()) {

                                       referCodeClass = new ReferCodeClass(number);

                                       myRef.child("ReferList").child(user.getUid()).setValue(referCodeClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {

                                                   myRef.child("MainPoint").child(number).setValue("100")
                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {

                                                                   if (task.isSuccessful()){

                                                                       if (refer.equals("")){

                                                                           Toast.makeText(SignUpActivity.this, "SignUp Successfully", Toast.LENGTH_SHORT).show();
                                                                           auth.signOut();
                                                                           startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                                                           finish();

                                                                       }else {


                                                                           myRef.child("MainPoint").child(refer).setValue(referupdatePoint)
                                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                       @Override
                                                                                       public void onComplete(@NonNull Task<Void> task) {


                                                                                           if (task.isSuccessful()){

                                                                                               Toast.makeText(SignUpActivity.this, "refer Successfully", Toast.LENGTH_SHORT).show();
                                                                                               auth.signOut();
                                                                                               startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                                                                               finish();


                                                                                           }else {
                                                                                               Toast.makeText(SignUpActivity.this, "without refer Successfully", Toast.LENGTH_SHORT).show();
                                                                                               auth.signOut();
                                                                                               startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                                                                               finish();

                                                                                           }


                                                                                       }
                                                                                   }).addOnFailureListener(new OnFailureListener() {
                                                                               @Override
                                                                               public void onFailure(@NonNull Exception e) {

                                                                                   Toast.makeText(SignUpActivity.this, "without refer error Successfully", Toast.LENGTH_SHORT).show();
                                                                                   auth.signOut();
                                                                                   startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                                                                   finish();

                                                                               }
                                                                           });

                                                                       }


                                                                   }else {

                                                                       Toast.makeText(SignUpActivity.this, "without new user point Successfully", Toast.LENGTH_SHORT).show();
                                                                       auth.signOut();
                                                                       startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                                                       finish();

                                                                   }


                                                               }
                                                           }).addOnFailureListener(new OnFailureListener() {
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {

                                                           Toast.makeText(SignUpActivity.this, "without new user point error Successfully", Toast.LENGTH_SHORT).show();
                                                           auth.signOut();
                                                           startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                                           finish();

                                                       }
                                                   });

                                               }else {

                                                   Toast.makeText(SignUpActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                                                   progressBar.setVisibility(View.GONE);
                                                   registerButton.setEnabled(true);

                                               }



                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {

                                               Toast.makeText(SignUpActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                                               progressBar.setVisibility(View.GONE);
                                               registerButton.setEnabled(true);

                                           }
                                       });




                                   } else {

                                       Toast.makeText(SignUpActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                                       progressBar.setVisibility(View.GONE);
                                       registerButton.setEnabled(true);
                                   }
                               }

                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {

                                   Toast.makeText(SignUpActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                                   progressBar.setVisibility(View.GONE);
                                   registerButton.setEnabled(true);

                               }
                           });
                       } else {
                           Toast.makeText(SignUpActivity.this, "Register field", Toast.LENGTH_SHORT).show();
                           progressBar.setVisibility(View.GONE);
                           registerButton.setEnabled(true);

                       }


                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(SignUpActivity.this, "Register field", Toast.LENGTH_SHORT).show();
                       progressBar.setVisibility(View.GONE);
                       registerButton.setEnabled(true);

                   }
               });


           } else {
               confirmPasswordET.setError("confirm password could not match");
               registerButton.setEnabled(false);
           }



       }

    }