package com.worldtechpoints.lambenewsupdate.AdminPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.worldtechpoints.lambenewsupdate.R;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){

            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String uId;
    String pushId;
    ProgressDialog dialog;
    private RecyclerView recyclerView;
    private List<Withdraw> withdrawList;
    WithdrawAdapter adapter;
    Withdraw withdraw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);


        Toolbar toolbar = findViewById(R.id.adminToolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Admin Panel");

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        withdraw = new Withdraw();
        withdrawList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        dialog = new ProgressDialog(this);

        if (user != null){
            uId = user.getUid();
            pushId = myRef.push().getKey();

            userWithdraw();

        }


    }

    private void userWithdraw() {

        myRef.child("WithdrawList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    withdrawList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        withdraw = snapshot.getValue(Withdraw.class);

                    }
                    withdrawList.add(withdraw);
                    adapter = new WithdrawAdapter(AdminPanelActivity.this,withdrawList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}