package com.worldtechpoints.lambenewsupdate.AdminPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.worldtechpoints.lambenewsupdate.HomeActivity;
import com.worldtechpoints.lambenewsupdate.R;

import java.util.ArrayList;
import java.util.List;

public class WithdrawActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }



    private TextView pointTV;
    private EditText paymentNumberET,paymentAmountET;
    private Spinner spinner;
    private String spinnerValue;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String uId;
    String pushId;
    String phoneNumber;
    String amount;
    int mainPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);


        Toolbar toolbar = findViewById(R.id.withdrawToolbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Wallet");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        if (user != null) {
            uId = user.getUid();
            pushId = myRef.push().getKey();
            balanceControl();

        }

        pointTV = findViewById(R.id.withdrawPoints_id);
        paymentNumberET = findViewById(R.id.paymentNumber_id);
        paymentAmountET = findViewById(R.id.paymentAmount_id);
        spinner = findViewById(R.id.spinner_id);

        List<String> paymentSystem = new ArrayList<String>();
        paymentSystem.add("Paypal");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentSystem);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerValue = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void balanceControl() {

        myRef.child("MainPoint").child(user.getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String value = dataSnapshot.getValue(String.class);
                    mainPoints = Integer.parseInt(value);

                    pointTV.setText("Your Points : " + value);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void SubmitPayment(View view) {

        sentPayment();

    }


    private void sentPayment() {

        phoneNumber = paymentNumberET.getText().toString().trim();
        amount = paymentAmountET.getText().toString().trim();

        if (phoneNumber.isEmpty()) {

            paymentNumberET.setError("Please enter valid phone number");

        } if (amount.isEmpty()) {

            paymentAmountET.setError("Please enter valid phone number");

        } else {

            if (mainPoints >= 5000 && Integer.parseInt(amount) <= mainPoints) {

                    if (mainPoints >= 5000){
                        if (Integer.parseInt(amount) >= 5000){
                            confirmAlert2(spinnerValue, phoneNumber,amount);
                        }else {

                            problemAlert("Minimum 5000Tk needed");
                        }

                    }else {
                        problemAlert("You have not enough Points");

                    }
                }else {
                problemAlert("You have not enough Points");

            }

            }


        }


    private void confirmAlert2(String name, String number, final String amount) {

        AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawActivity.this);

        builder.setTitle("Confirm Alert!")
                .setMessage("Please check your \nPayment Method: " + name + "\nNumber is: " + number+"\nAnd Amount: "+amount+"Tk")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int countPoint = mainPoints - Integer.parseInt(amount);
                        String lastPoint = String.valueOf(countPoint);
                        myRef.child("MainPoint").child(user.getDisplayName()).setValue(lastPoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Withdraw withdraw = new Withdraw(spinnerValue, phoneNumber, amount,uId);
                                    myRef.child("WithdrawList").child(uId).setValue(withdraw)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        completeAlert();

                                                    } else {

                                                        Toast.makeText(WithdrawActivity.this, "Net Connection problem", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                } else {
                                    Toast.makeText(WithdrawActivity.this, "Net connection problem.", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void completeAlert() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawActivity.this);

        builder.setMessage("Congratulation! \nYour withdraw is successfully")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(WithdrawActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }).setNegativeButton("Go to home", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(WithdrawActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void problemAlert(String value) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawActivity.this);

        builder.setTitle("Sorry ..!")
                .setMessage(value)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(WithdrawActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


}
