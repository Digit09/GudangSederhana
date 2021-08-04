package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MenuCreate extends AppCompatActivity {

    private Toolbar toolbar;
    public static EditText getEdId;
    private EditText getEdNamaB, getEdHarga, getEdModal, getEdProdusen, getEdKedaluwarsa;
    private Button btSimpan;
    private ProgressBar progressBar;
    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_create);

        TextView judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Tambah Barang");

        toolbar = findViewById(R.id.toolbar_submenu);
        btSimpan = findViewById(R.id.btSimpan);

        setSupportActionBar(toolbar);

        getEdId = findViewById(R.id.edId);
        String id = getIntent().getStringExtra("idBarangC");
        getEdId.setText(id);

        getEdNamaB = findViewById(R.id.edNamaB);
        getEdHarga = findViewById(R.id.edHarga);
        getEdModal = findViewById(R.id.edModal);
        getEdProdusen = findViewById(R.id.edProdusen);
        getEdKedaluwarsa = findViewById(R.id.edKedaluwarsa);

        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressBar = findViewById(R.id.progressbarCreate);

        dateSettings();

        btSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createData(id);
            }
        });
    }

    public static class Create {
        public String id, name, price, fund, producer, expired;

        public Create(){

        }

        public Create(String id, String name, String price, String fund, String producer, String expired){
            this.id = id;
            this.name = name;
            this.price = price;
            this.fund = fund;
            this.producer = producer;
            this.expired = expired;
        }
    }

    private void createData(String id) {
        String nama = getEdNamaB.getText().toString().trim();
        String harga = getEdHarga.getText().toString().trim();
        String modal = getEdModal.getText().toString().trim();
        String produsen = getEdProdusen.getText().toString().trim();
        String kedaluwarsa = getEdKedaluwarsa.getText().toString().trim();

        if (id.isEmpty()){
            getEdNamaB.setError("Barcode Barang harus diisi (Manual)..");
            getEdNamaB.requestFocus();
        }
        else if (nama.isEmpty()) {
            getEdNamaB.setError("Nama Barang harus diisi..");
            getEdNamaB.requestFocus();
        }
        else if (harga.isEmpty()) {
            getEdHarga.setError("Harga Barang harus diisi..");
            getEdHarga.requestFocus();
        }
        else {
            if (modal.isEmpty()) {
                modal = "-";
            }
            if (produsen.isEmpty()) {
                produsen = "-";
            }
            if (kedaluwarsa.isEmpty()) {
                kedaluwarsa = "-";
            }
            progressBar.setVisibility(View.VISIBLE);
            Create create = new Create(id, nama, harga, modal, produsen, kedaluwarsa);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(id);
            ref.setValue(create).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()){
                        Toast.makeText(MenuCreate.this, "Barang berhasil tersimpan", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(MenuCreate.this, "Barang gagal tersimpan", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                new DatePickerDialog(MenuCreate.this, date, myCalendar
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
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}