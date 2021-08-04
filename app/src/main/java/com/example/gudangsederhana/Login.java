package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    EditText eEmail, ePassword;
    CheckBox cShowPassword;
    Button bMasuk;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eEmail = (EditText) findViewById(R.id.edEmail);
        ePassword = (EditText) findViewById(R.id.edPassword);
        cShowPassword = (CheckBox) findViewById(R.id.cbShowPassword);
        bMasuk = (Button) findViewById(R.id.btMasuk);
        progressBar = (ProgressBar) findViewById(R.id.uploadProgress);
        fAuth = FirebaseAuth.getInstance();

        cShowPassword.setOnClickListener(v -> {
            if (cShowPassword.isChecked()){
                ePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        bMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eEmail.getText().toString().trim();
                String pass = ePassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    eEmail.setError("Email belum dimasukan.");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    ePassword.setError("Kata Sandi belum dimasukan.");
                    return;
                }
                else if(pass.length() < 6){
                    eEmail.setError("Kata Sandi kurang dari 6 karakter.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            Toast.makeText(Login.this, "Email / Kata Sandi salah!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}