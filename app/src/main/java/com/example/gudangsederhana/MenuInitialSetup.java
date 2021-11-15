package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MenuInitialSetup extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btSimpan;
    private EditText edNamaToko, edNamaPemilik, edAlamat, edNoTelp;
    private ProgressBar progressBar;
    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_initial_setup);

        TextView judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Gudang Sederhana");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btSimpan = findViewById(R.id.btSimpan);
        edNamaToko = findViewById(R.id.edNamaToko_pa);
        edNamaPemilik = findViewById(R.id.edNamaPemilik_pa);
        edAlamat = findViewById(R.id.edAlamat_pa);
        edNoTelp = findViewById(R.id.edNoTelepon_pa);
        progressBar = findViewById(R.id.progressbar_is);

        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btSimpan.setOnClickListener(v -> {
            createData();
        });
    }

    public static class CreateSeller {
        public String uid, shopName, owner, address, phoneNumber;
        public Boolean ban;

        public CreateSeller(){

        }

        public CreateSeller(String uid, String shopName, String owner, String address, String phoneNumber, Boolean ban){
            this.uid = uid;
            this.shopName = shopName;
            this.owner = owner;
            this.address = address;
            this.phoneNumber = phoneNumber;
            this.ban = ban;
        }
    }

    private void createData() {
        String uid = auth;
        String namaToko = MainActivity.capitalizeEachWord(edNamaToko.getText().toString().trim());
        String namaPemilik = MainActivity.capitalizeEachWord(edNamaPemilik.getText().toString().trim());
        String alamat = MainActivity.capitalizeEachWord(edAlamat.getText().toString().trim());
        String noTelp = MainActivity.capitalizeEachWord(edNoTelp.getText().toString().trim());
        Boolean ban = false;

        if (namaToko.isEmpty()){
            edNamaToko.setError("Nama Toko harus diisi..");
            edNamaToko.requestFocus();
        } else if (namaPemilik.isEmpty()){
            edNamaPemilik.setError("Nama Pemilik harus diisi..");
            edNamaPemilik.requestFocus();
        } else if (alamat.isEmpty()){
            edAlamat.setError("Alamat harus diisi..");
            edAlamat.requestFocus();
        } else if (noTelp.isEmpty()){
            edNoTelp.setError("Nomor Telepon harus diisi..");
            edNoTelp.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            CreateSeller createSeller = new CreateSeller(uid, namaToko, namaPemilik, alamat, noTelp, ban);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sellers").child(auth);
            ref.setValue(createSeller).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()){
                        Toast.makeText(MenuInitialSetup.this, "Data berhasil tersimpan", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MenuInitialSetup.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MenuInitialSetup.this, "Data gagal tersimpan", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pengaturan_awal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                AlertDialog.Builder logout = new AlertDialog.Builder(this);
                logout.setTitle("Keluar dari Akun");
                logout.setMessage("Apakah Anda yakin ingin keluar dari akun ini?");
                logout.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut(); //logout
                        Intent intent = new Intent(MenuInitialSetup.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                logout.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                logout.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}