package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
    private EditText getEdNamaB, getEdHarga, getEdModal, getEdProdusen, getEdKedaluwarsa, getEdStok;
    private Spinner getEdKategori;
    private Button btSimpan, btClearExpired_mc, btClearKategori;
    private ProgressBar progressBar;
    private String auth;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_create);

        TextView judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Tambah Barang");

        btSimpan = findViewById(R.id.btSimpan);
        btClearExpired_mc = findViewById(R.id.btClearExpired_mc);
        btClearKategori = findViewById(R.id.btClearKategori);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getEdId = findViewById(R.id.edId);
        Intent intent = getIntent();
        if (intent.hasExtra("idBarangC")) {
            String id = intent.getStringExtra("idBarangC");
            getEdId.setText(id);
        } else {
            getEdId.setText("");
        }

        getEdNamaB = findViewById(R.id.edNamaB);
        getEdHarga = findViewById(R.id.edHarga);
        getEdModal = findViewById(R.id.edModal);
        getEdKategori = findViewById(R.id.edKategori);
        getEdProdusen = findViewById(R.id.edProdusen);
        getEdKedaluwarsa = findViewById(R.id.edKedaluwarsa);
        getEdStok = findViewById(R.id.edStok);

        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressBar = findViewById(R.id.progressbarCreate);

        dateSettings();
        kategoriAdapter();

        if (intent.hasExtra("btAdd")) {
            getEdId.setEnabled(true);
            getEdId.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(MenuCreate.getEdId, InputMethodManager.SHOW_IMPLICIT);
            intent.removeExtra("btAdd");
        }

        btSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createData();
            }
        });

        btClearExpired_mc.setOnClickListener(v -> {
            getEdKedaluwarsa.getText().clear();
        });
        btClearKategori.setOnClickListener(v -> {
            getEdKategori.setSelection(0);
        });
    }

    public static class Create {
        public String id, name, price, fund, category, producer, expired, stock;

        public Create(){

        }

        public Create(String id, String name, String price, String fund, String category, String producer, String expired, String stock){
            this.id = id;
            this.name = name;
            this.price = price;
            this.fund = fund;
            this.category = category;
            this.producer = producer;
            this.expired = expired;
            this.stock = stock;
        }
    }

    private void kategoriAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuCreate.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.category)){
            @Override
            public boolean isEnabled(int position) {
                if (position==0){
                    return false;
                }
                return true;
            }
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) v;
                if (position==0){
                    tv.setTextColor(Color.GRAY);
                }
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getEdKategori.setAdapter(adapter);
    }

    private void createData() {
        String id = getEdId.getText().toString().trim();
        String nama = MainActivity.capitalizeEachWord(getEdNamaB.getText().toString().trim());
        String harga = MainActivity.capitalizeEachWord(getEdHarga.getText().toString().trim());
        String modal = MainActivity.capitalizeEachWord(getEdModal.getText().toString().trim());
        String kategori = MainActivity.capitalizeEachWord(getEdKategori.getSelectedItem().toString().trim());
        String produsen = MainActivity.capitalizeEachWord(getEdProdusen.getText().toString().trim());
        String kedaluwarsa = MainActivity.capitalizeEachWord(getEdKedaluwarsa.getText().toString().trim());
        String stok = MainActivity.capitalizeEachWord(getEdStok.getText().toString().trim());

        if (id.isEmpty()){
            getEdId.setError("Barcode Barang harus diisi (Manual)..");
            getEdId.requestFocus();
        }
        else if (nama.isEmpty()) {
            getEdNamaB.setError("Nama Barang harus diisi..");
            getEdNamaB.requestFocus();
        }
        else if (harga.isEmpty()) {
            getEdHarga.setError("Harga Barang harus diisi..");
            getEdHarga.requestFocus();
        }
        else if (kategori.equals("-- Pilih Kategori --")) {
            Toast.makeText(MenuCreate.this, "Kategori Barang harus dipilih..", Toast.LENGTH_LONG).show();
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
            if (stok.isEmpty()) {
                stok = "-";
            }
            progressBar.setVisibility(View.VISIBLE);
            Create create = new Create(id, nama, harga, modal, kategori, produsen, kedaluwarsa, stok);

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