package com.example.myofficebase;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myofficebase.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    static String test = "Your Task App";
    DatabaseReference databaseReference;
    FirebaseAuth efirebaseAuth;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        getSupportActionBar().setIcon(R.drawable.ic_person);
        //Firebase Authentication
        efirebaseAuth = FirebaseAuth.getInstance();
        //Firebase User
        FirebaseUser firebaseUser = efirebaseAuth.getCurrentUser();
        String uId = null;
        if (firebaseUser != null) {
            uId = firebaseUser.getUid();
            //user is loged in
        }

        //Firebase Database
        if (uId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uId);
        }
        databaseReference.keepSynced(true);
        // Recycler View
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        FloatingActionButton floatingActionButton = findViewById(R.id.fab_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup viewGroup = findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.custom_input_field, viewGroup, false);


                //Now we need an AlertDialog.Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);


                //setting the view of the builder to our custom view that we already inflated
                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final EditText e_title = dialogView.findViewById(R.id.edit_Title);
                final EditText e_notes = dialogView.findViewById(R.id.edit_Notes);
                Button btn_save = dialogView.findViewById(R.id.btn_Save);
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s_title = e_title.getText().toString().trim();
                        String s_notes = e_notes.getText().toString().trim();
                        if (TextUtils.isEmpty(s_title)) {
                            e_title.setError("Required Field.. ");
                            return;
                        }
                        if (TextUtils.isEmpty(s_notes)) {
                            e_notes.setError("Required Field.. ");
                            return;
                        }

                        String s_id = databaseReference.push().getKey();
                        String s_date = DateFormat.getDateInstance().format(new Date());
                        Data data = new Data(s_title, s_notes, s_date, s_id);
                        if (s_id != null) {
                            databaseReference.child(s_id).setValue(data);
                        }
                        Toast.makeText(HomeActivity.this, "Data Inserted ", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });


            }
        });

    }
}