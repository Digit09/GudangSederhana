package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuSearchDetail extends AppCompatActivity {

    Toolbar toolbar;
    RelativeLayout rlDataShow;
    TextView tvResult, tvNama, tvHarga, tvModal, tvKategori, tvProdusen, tvKedaluwarsa, tvStok, judulMenuC;
    Button updateBtn, deleteBtn, showBtn, hideBtn;
    String getResult = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_search_detail);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Detail Barang");

        declaration();
        clickSettings();
    }

    private void declaration(){
        tvResult = findViewById(R.id.tvResult);
        tvNama = findViewById(R.id.tvNama);
        tvHarga = findViewById(R.id.tvHarga);
        tvModal = findViewById(R.id.tvModal);
        tvKategori = findViewById(R.id.tvKategori);
        tvProdusen = findViewById(R.id.tvProdusen);
        tvKedaluwarsa = findViewById(R.id.tvKedaluwarsa);
        tvStok = findViewById(R.id.tvStok);
        rlDataShow = findViewById(R.id.rlDataShow);
        showBtn = findViewById(R.id.btShow);
        hideBtn = findViewById(R.id.btHide);
        updateBtn = findViewById(R.id.btUbah);
        deleteBtn = findViewById(R.id.btHapus);
    }

    private void clickSettings(){
        showBtn.setOnClickListener(v -> {
            rlDataShow.setVisibility(View.VISIBLE);
            showBtn.setVisibility(View.GONE);
            hideBtn.setVisibility(View.VISIBLE);
        });

        hideBtn.setOnClickListener(v -> {
            rlDataShow.setVisibility(View.GONE);
            showBtn.setVisibility(View.VISIBLE);
            hideBtn.setVisibility(View.GONE);
        });

        tvKedaluwarsa.setOnClickListener(v -> {
            if (MainActivity.cariKedaluwarsa(tvKedaluwarsa.getText().toString())){
                Toast.makeText(MenuSearchDetail.this, "Kedaluwarsa", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MenuSearchDetail.this, "Tidak", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (intent.hasExtra("result")) {
            getResult = intent.getStringExtra("result");
            loadData(getResult);
        }
    }

    private void loadData(String result) {
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(result);

        // Toast.makeText(MainActivity.this, "Mencari Data..", Toast.LENGTH_LONG).show();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String vId = snapshot.child("id").getValue().toString();
                    String vName = snapshot.child("name").getValue().toString();
                    String vPrice = snapshot.child("price").getValue().toString();
                    String vFund = snapshot.child("fund").getValue().toString();
                    String vCategory = snapshot.child("category").getValue().toString();
                    String vProducer = snapshot.child("producer").getValue().toString();
                    String vExpired = snapshot.child("expired").getValue().toString();
                    String vStock = snapshot.child("stock").getValue().toString();

                    tvResult.setText(vId);
                    tvNama.setText(vName);
                    tvHarga.setText(rupiahkan(vPrice));
                    tvModal.setText(rupiahkan(vFund));
                    tvKategori.setText(vCategory);
                    tvProdusen.setText(vProducer);
                    tvKedaluwarsa.setText(vExpired);
                    tvStok.setText(vStock);

                    updateBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MenuSearchDetail.this, MenuUpdate.class);
                            intent.putExtra("idBarangU", result);
                            startActivity(intent);
                        }
                    });
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteComfirm(result);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuSearchDetail.this, "Gagal mencari data", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String rupiahkan(String nominal) {
        if (!nominal.equals("-")) {
            // mis. : nominal = "2000"
            Integer i = nominal.length(); // i = 4
            String temp = "";
            while (i > 0) { // 4 != 0
                if (i > 3) {
                    temp = "." + nominal.substring(i - 3, i) + temp; // temp = .000
                    i -= 3; // i = 4-3 = 1
                } else {
                    temp = nominal.substring(0, i) + temp;
                    i = 0;
                }
            }
            return "Rp. " + temp;
        } else {
            return nominal;
        }
    }

    private void deleteComfirm(String idbarang){
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle("Hapus Barang");
        confirm.setMessage("Apakah anda yakin ingin menghapus barang ini?");
        confirm.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(idbarang);
                ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MenuSearchDetail.this, "Berhasil menghapus barang", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuSearchDetail.this, "Gagal menghapus barang", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        confirm.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirm.create().show();
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