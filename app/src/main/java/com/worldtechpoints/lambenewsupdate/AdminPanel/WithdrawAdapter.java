package com.worldtechpoints.lambenewsupdate.AdminPanel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.worldtechpoints.lambenewsupdate.R;

import java.util.List;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.ViewHolder> {


    private Context context;
    private List<Withdraw> withdrawList;
    private Withdraw withdraw;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;

    public WithdrawAdapter(Context context, List<Withdraw> withdrawList) {

        this.context = context;
        this.withdrawList = withdrawList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.block_user_list,parent,false);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


        withdraw = withdrawList.get(position);
        holder.userName.setText(withdraw.getPhoneNumber());
        holder.userEmail.setText(withdraw.getPaymentMethodName());


        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdraw = withdrawList.get(position);
                String userId = withdraw.uId;
                withdrawDelete(userId);

            }
        });


    }

    private void withdrawDelete(final String uId) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Are you Sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        myRef.child("WithdrawList").child(uId).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                            Toast.makeText(context, "Block user delete successfully", Toast.LENGTH_SHORT).show();
                                            context.startActivity(new Intent(context,AdminPanelActivity.class));

                                        }else {
                                            Toast.makeText(context, "Check your net Connection", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(context, "Check your net Connection", Toast.LENGTH_SHORT).show();

                            }
                        });




                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                Toast.makeText(context, "User Block Still..Ok", Toast.LENGTH_SHORT).show();



            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();






    }

    @Override
    public int getItemCount() {
        return withdrawList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView userName, userEmail;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.blockUserName);
            userEmail = itemView.findViewById(R.id.blockUserEmail);
            deleteButton = itemView.findViewById(R.id.delete_id);


        }
    }
}
