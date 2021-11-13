package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    private String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AppLock");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if (Boolean.parseBoolean(snapshot.child("lock").getValue().toString())){
                                dialog();
                            } else {
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }, 2000);
    }

    private void dialog(){
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle("Pemberitahuan");
        confirm.setMessage("Aplikasi masih dalam tahap perbaikan. Mohon maaf atas ketidaknyamanannya. Aplikasi akan ditutup dalam 5 detik.");
        confirm.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SplashScreen.this.finish();
                System.exit(0);
            }
        });
        confirm.create().show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreen.this.finish();
                System.exit(0);
            }
        }, 5000);
    }
}