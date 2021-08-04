package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MenuUpdate extends AppCompatActivity {

    private EditText getEdId, getEdNamaB, getEdHarga, getEdModal, getEdProdusen, getEdKedaluwarsa;
    private String vNamaB, vHarga, vModal, vProdusen, vKedaluwarsa;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_update);

        TextView judulMenuU = findViewById(R.id.judulMenu);
        judulMenuU.setText("Ubah Barang");

        Toolbar toolbar = findViewById(R.id.toolbar_submenu);
        Button btUbah = findViewById(R.id.btUbah);

        setSupportActionBar(toolbar);

        getEdId = findViewById(R.id.edIdU);

        getEdNamaB = findViewById(R.id.edNamaBU);
        getEdHarga = findViewById(R.id.edHargaU);
        getEdModal = findViewById(R.id.edModalU);
        getEdProdusen = findViewById(R.id.edProdusenU);
        getEdKedaluwarsa = findViewById(R.id.edKedaluwarsaU);

        progressBar = findViewById(R.id.progressbarUpdate);

        dateSettings();

        Intent intent = getIntent();
        if (intent.hasExtra("idBarangU")) {
            String getId = intent.getStringExtra("idBarangU");
            loadDataBarang(getId);
        }

        btUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekData(getEdId.getText().toString());
            }
        });
    }

    private void cekIntentUbah(){

    }

    @Override
    protected void onStart() {
        super.onStart();
        cekIntentUbah();
    }

    private void cekData(String id){
        if (getEdNamaB.getText().toString().trim().equals(vNamaB)) {
            if (getEdHarga.getText().toString().trim().equals(vHarga)) {
                if (getEdModal.getText().toString().trim().equals(vModal)) {
                    if (getEdProdusen.getText().toString().trim().equals(vProdusen)) {
                        if (getEdKedaluwarsa.getText().toString().trim().equals(vKedaluwarsa)) {
                            String teks = "Data barang belum ada yang diubah! Tekan tombol kembali jika ingin batal";
                            Toast.makeText(MenuUpdate.this, teks, Toast.LENGTH_LONG).show();
                        } else {
                            updateConfirm(id);
                        }
                    } else {
                        updateConfirm(id);
                    }
                } else {
                    updateConfirm(id);
                }
            } else {
                updateConfirm(id);
            }
        } else {
            updateConfirm(id);
        }
    }

    private void updateConfirm(String id){
        AlertDialog.Builder newData = new AlertDialog.Builder(this);
        newData.setTitle("Konfirmasi Ubah Data");
        newData.setMessage("Apakah anda yakin data yang dimasukkan sudah benar?");
        newData.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateData(id);
            }
        });
        newData.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        newData.create().show();
    }

    private void updateData(String id){
        String nama = getEdNamaB.getText().toString().trim();
        String harga = getEdHarga.getText().toString().trim();
        String modal = getEdModal.getText().toString().trim();
        String produsen = getEdProdusen.getText().toString().trim();
        String kedaluwarsa = getEdKedaluwarsa.getText().toString().trim();

        if (nama.isEmpty()) {
            getEdNamaB.setError("Nama Barang harus diisi..");
            getEdNamaB.requestFocus();
        }
        else if (harga.isEmpty()) {
            getEdHarga.setError("Harga Barang harus diisi..");
            getEdHarga.requestFocus();
        }
        else {
            if (modal.isEmpty()) {
                getEdModal.setText("-");
            }
            if (produsen.isEmpty()) {
                getEdProdusen.setText("-");
            }
            if (kedaluwarsa.isEmpty()) {
                getEdKedaluwarsa.setText("-");
            }

            Map<String, Object> map = new HashMap<>();
            map.put("name", getEdNamaB.getText().toString());
            map.put("price", getEdHarga.getText().toString());
            map.put("fund", getEdModal.getText().toString());
            map.put("producer", getEdProdusen.getText().toString());
            map.put("expired", getEdKedaluwarsa.getText().toString());

            progressBar.setVisibility(View.VISIBLE);
            String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(id);
            ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Data barang berhasil diubah", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Data barang gagal diubah", Toast.LENGTH_SHORT).show();
                    }
                    onBackPressed();
                }
            });
        }
    }

    private void loadDataBarang(String id){
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vNamaB = snapshot.child("name").getValue().toString();
                vHarga = snapshot.child("price").getValue().toString();
                vModal = snapshot.child("fund").getValue().toString();
                vProdusen = snapshot.child("producer").getValue().toString();
                vKedaluwarsa = snapshot.child("expired").getValue().toString();

                getEdId.setText(id);
                getEdNamaB.setText(vNamaB);
                getEdHarga.setText(vHarga);
                if (!vModal.equals("-")){
                    getEdModal.setText(vModal);
                }
                if (!vProdusen.equals("-")) {
                    getEdProdusen.setText(vProdusen);
                }
                if (!vKedaluwarsa.equals("-")){
                    getEdKedaluwarsa.setText(vKedaluwarsa);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dateSettings(){
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
            private void updateLabel() {
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                getEdKedaluwarsa.setText(sdf.format(myCalendar.getTime()));
            }
        };

        getEdKedaluwarsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MenuUpdate.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnBack:
                Intent intent = new Intent(MenuUpdate.this, MainActivity.class);
                intent.putExtra("result2", getEdId.getText().toString());
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MenuUpdate.this, MainActivity.class);
        intent.putExtra("result2", getEdId.getText().toString());
        startActivity(intent);
        finish();
    }
}