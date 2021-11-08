package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;

public class MenuCashier extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView judulMenuC;
    private Button scannerBtn, resetBtn;
    private String getResult = "0";
    private RecyclerView recyclerView;
    private RelativeLayout rlOpsiKasir;
    private CashierAdapter cashierAdapter;
    private TextView tvKet;
    public static TextView tvTotal;
    private ArrayList<Goods> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cashier);

        judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Kasir");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.rvBarang_mc);
        rlOpsiKasir = findViewById(R.id.rlOpsiKasir);
        list = new ArrayList<>();
        cashierAdapter = new CashierAdapter(this, list);
        recyclerView.setAdapter(cashierAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView = findViewById(R.id.rvBarang_mc);
        tvKet = findViewById(R.id.tvKet_mc);
        tvTotal = findViewById(R.id.tvTotal_mc);

        scannerBtn = findViewById(R.id.btScan_mc);
        resetBtn = findViewById(R.id.btReset);
        scannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuScanner.class);
                intent.putExtra("sMenu", "Cashier");
                startActivity(intent);
                finish();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    private void reset(){
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle("Mengatur Ulang");
        confirm.setMessage("Apakah anda yakin ingin mengatur ulang?");
        confirm.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Transactions").child(auth);
                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            rlOpsiKasir.setVisibility(View.GONE);
                            Toast.makeText(MenuCashier.this, "Berhasil mengatur ulang", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MenuCashier.this, "Gagal mengatur ulang", Toast.LENGTH_SHORT).show();
                        }
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
    protected void onStart() {
        super.onStart();
        cekIntentScanner();
    }

    private void cekIntentScanner() {
        Intent intent = getIntent();
        if (intent.hasExtra("result")) {
            getResult = intent.getStringExtra("result");
            tempTransaction(getResult);
        } else {
            loadData();
        }
    }

    private void tempTransaction(String result) {
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(result);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvKet.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    rlOpsiKasir.setVisibility(View.VISIBLE);
                    String vId = snapshot.child("id").getValue().toString();
                    String vName = snapshot.child("name").getValue().toString();
                    String vPrice = snapshot.child("price").getValue().toString();
                    String vFund = snapshot.child("fund").getValue().toString();
                    String vCategory = snapshot.child("category").getValue().toString();
                    String vProducer = snapshot.child("producer").getValue().toString();
                    String vExpired = snapshot.child("expired").getValue().toString();
                    String vCount = "1";
                    Trans trans = new Trans(vId, vName,vPrice, vFund, vCategory, vProducer, vExpired, vCount);

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Transactions").child(auth).child(vId);
                    ref2.setValue(trans).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                loadData();
                            } else {
                                Toast.makeText(MenuCashier.this, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    loadData();
                    tvKet.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    rlOpsiKasir.setVisibility(View.GONE);
                    Toast.makeText(MenuCashier.this, "Barcode tidak terdaftar.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuCashier.this, "Gagal mencari data", Toast.LENGTH_LONG).show();
                tvKet.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                rlOpsiKasir.setVisibility(View.GONE);
            }
        });
    }

    public void loadData(){
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Transactions").child(auth);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvKet.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    rlOpsiKasir.setVisibility(View.VISIBLE);
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Goods goods = ds.getValue(Goods.class);
                        list.add(goods);
                    }
                    cashierAdapter.notifyDataSetChanged();
                    CashierAdapter.hitung(MenuCashier.this, auth);
                } else {
                    tvKet.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    rlOpsiKasir.setVisibility(View.GONE);
                    MenuCashier.tvTotal.setText("Rp. -");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuCashier.this, "Gagal mencari data", Toast.LENGTH_LONG).show();
                tvKet.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                rlOpsiKasir.setVisibility(View.GONE);
            }
        });
    }

    public static class Trans {
        public String id, name, price, fund, category, producer, expired, count;

        public Trans(){

        }

        public Trans(String id, String name, String price, String fund, String category, String producer, String expired, String count){
            this.id = id;
            this.name = name;
            this.price = price;
            this.fund = fund;
            this.category = category;
            this.producer = producer;
            this.expired = expired;
            this.count = count;
        }
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