package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.AttributedCharacterIterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MenuUpdate extends AppCompatActivity {

    private EditText getEdId, getEdNamaB, getEdHarga, getEdModal, getEdProdusen, getEdKedaluwarsa;
    private Spinner getEdKategori;
    private String vNamaB, vHarga, vModal, vKategori, vProdusen, vKedaluwarsa;
    private ProgressBar progressBar;
    private Button btUbah, btClearExpiredU, btClearKategoriU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_update);

        TextView judulMenuU = findViewById(R.id.judulMenu);
        judulMenuU.setText("Ubah Barang");

        Toolbar toolbar = findViewById(R.id.toolbar);
        btUbah = findViewById(R.id.btUbah);
        btClearExpiredU = findViewById(R.id.btClearExpiredU);
        btClearKategoriU = findViewById(R.id.btClearKategoriU);

        setSupportActionBar(toolbar);

        getEdId = findViewById(R.id.edIdU);

        getEdNamaB = findViewById(R.id.edNamaBU);
        getEdHarga = findViewById(R.id.edHargaU);
        getEdModal = findViewById(R.id.edModalU);
        getEdKategori = findViewById(R.id.edKategoriU);
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
        btClearExpiredU.setOnClickListener(v -> {
            getEdKedaluwarsa.getText().clear();
        });
        btClearKategoriU.setOnClickListener(v -> {
            getEdKategori.setSelection(0);
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
                    if (getEdKategori.getSelectedItem().toString().trim().equals(vKategori)) {
                        if (getEdProdusen.getText().toString().trim().equals(vProdusen)) {
                            if (getEdKedaluwarsa.getText().toString().trim().equals(vKedaluwarsa)) {
                                String teks = "Data belum ada perubahan! Tekan tombol kembali jika ingin batal";
                                Toast.makeText(MenuUpdate.this, teks, Toast.LENGTH_LONG).show();
                            } else {
                                //Toast.makeText(MenuUpdate.this, "expired", Toast.LENGTH_LONG).show();
                                updateConfirm(id);
                            }
                        } else {
                            //Toast.makeText(MenuUpdate.this, "producer", Toast.LENGTH_LONG).show();
                            updateConfirm(id);
                        }
                    } else {
                        //Toast.makeText(MenuUpdate.this, "fund", Toast.LENGTH_LONG).show();
                        updateConfirm(id);
                    }
                } else {
                    //Toast.makeText(MenuUpdate.this, "fund", Toast.LENGTH_LONG).show();
                    updateConfirm(id);
                }
            } else {
                //Toast.makeText(MenuUpdate.this, "price", Toast.LENGTH_LONG).show();
                updateConfirm(id);
            }
        } else {
            //Toast.makeText(MenuUpdate.this, "name", Toast.LENGTH_LONG).show();
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
        String nama = MainActivity.capitalizeEachWord(getEdNamaB.getText().toString().trim());
        String harga = MainActivity.capitalizeEachWord(getEdHarga.getText().toString().trim());
        String modal = MainActivity.capitalizeEachWord(getEdModal.getText().toString().trim());
        String kategori = MainActivity.capitalizeEachWord(getEdKategori.getSelectedItem().toString().trim());
        String produsen = MainActivity.capitalizeEachWord(getEdProdusen.getText().toString().trim());
        String kedaluwarsa = MainActivity.capitalizeEachWord(getEdKedaluwarsa.getText().toString().trim());

        if (nama.isEmpty()) {
            getEdNamaB.setError("Nama Barang harus diisi..");
            getEdNamaB.requestFocus();
        }
        else if (harga.isEmpty()) {
            getEdHarga.setError("Harga Barang harus diisi..");
            getEdHarga.requestFocus();
        }
        else if (kategori.equals("-- Pilih Kategori --")) {
            Toast.makeText(MenuUpdate.this, "Kategori Barang harus dipilih..", Toast.LENGTH_LONG).show();
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

            Map<String, Object> map = new HashMap<>();
            map.put("name", nama);
            map.put("price", harga);
            map.put("fund", modal);
            map.put("category", kategori);
            map.put("producer", produsen);
            map.put("expired", kedaluwarsa);

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
                vKategori = snapshot.child("category").getValue().toString();
                vProdusen = snapshot.child("producer").getValue().toString();
                vKedaluwarsa = snapshot.child("expired").getValue().toString();

                getEdId.setText(id);
                getEdNamaB.setText(vNamaB);
                getEdHarga.setText(vHarga);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuUpdate.this,
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
                getEdKategori.setSelection(selectCategory(vKategori));
                if (!vModal.equals("-")){
                    getEdModal.setText(vModal);
                } else {
                    vModal = "";
                }
                if (!vProdusen.equals("-")) {
                    getEdProdusen.setText(vProdusen);
                } else {
                    vProdusen = "";
                }
                if (!vKedaluwarsa.equals("-")){
                    getEdKedaluwarsa.setText(vKedaluwarsa);
                } else {
                    vKedaluwarsa = "";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Integer selectCategory(String str){
        if (str.equalsIgnoreCase("barang")){
            return 1;
        } else if (str.equalsIgnoreCase("makanan")){
            return 2;
        } else if (str.equalsIgnoreCase("minuman")){
            return 3;
        } else {
            return 0;
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
                /*
                Intent intent = new Intent(MenuUpdate.this, MainActivity.class);
                intent.putExtra("result", getEdId.getText().toString());
                startActivity(intent);
                finish();

                 */
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}