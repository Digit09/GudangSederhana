package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuCashier extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView judulMenuC;
    private Button scannerBtn, manualAddBtn, resetBtn, incomeBtn;
    public static SwitchCompat switchCompat;
    private String getResult = "0";
    public static String wordMC;
    public static RecyclerView recyclerView;
    public static RelativeLayout rlOpsiKasir;
    private CashierAdapter cashierAdapter;
    public static TextView tvKet;
    public static TextView tvTotal;
    public static Long total;
    public static Integer wordMCL = 0;
    private ArrayList<Goods> list;
    private ArrayList<Trans> listTemp;
    private String auth;

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
        listTemp = new ArrayList<>();
        cashierAdapter = new CashierAdapter(this, list);
        recyclerView.setAdapter(cashierAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView = findViewById(R.id.rvBarang_mc);
        tvKet = findViewById(R.id.tvKet_mc);
        tvTotal = findViewById(R.id.tvTotal_mc);

        manualAddBtn = findViewById(R.id.btManualAdd_mc);
        scannerBtn = findViewById(R.id.btScan_mc);
        resetBtn = findViewById(R.id.btReset);
        incomeBtn = findViewById(R.id.btIncome);
        switchCompat = findViewById(R.id.scStok);

        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        scannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuScanner.class);
                intent.putExtra("sMenu", "Cashier");
                startActivity(intent);
                finish();
            }
        });
        manualAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualSearch();
            }
        });
        resetBtn.setOnClickListener(v ->  {
            AlertDialog.Builder confirm = new AlertDialog.Builder(this);
            confirm.setTitle("Mengatur Ulang");
            confirm.setMessage("Apakah anda yakin ingin mengatur ulang?");
            confirm.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reset();
                }
            });
            confirm.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            confirm.create().show();
        });
        incomeBtn.setOnClickListener(v -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-yyyy");
            LocalDateTime now = LocalDateTime.now();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Transactions").child(auth);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        listTemp.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Trans trans1 = ds.getValue(Trans.class);
                            listTemp.add(trans1);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            insertIncome(dtf.format(now));
        });
        loadSettings();
    }

    private void loadSettings(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Settings").child(auth);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    switchCompat.setChecked(Boolean.parseBoolean(snapshot.child("stockReduction").getValue().toString()));
                } else {
                    switchCompat.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuCashier.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reset(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Transactions").child(auth);
        ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    rlOpsiKasir.setVisibility(View.GONE);
                    Toast.makeText(MenuCashier.this, "Mengatur ulang...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MenuCashier.this, "Gagal mengatur ulang", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertIncome(String date){
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle("Tambah ke Pemasukan");
        confirm.setMessage("Apakah anda yakin ingin menambahkan total transaksi ke pemasukan bulan ini?");
        confirm.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sales").child(auth).child(date);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            total = total + Integer.parseInt(snapshot.child("income").getValue().toString());
                            //Toast.makeText(getBaseContext(), total.toString(), Toast.LENGTH_SHORT).show();
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put("date", date);
                        map.put("income", total.toString());
                        ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    stockReduction();
                                } else {
                                    Toast.makeText(getBaseContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

    private void stockReduction(){
        if (switchCompat.isChecked()) {
            reset();
            // Fix
            Boolean deteksiStokKosong = false;
            for (Trans object : listTemp) {
                if (!object.getStock().equals("-")) {
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(object.getId());
                    Integer stok = Integer.parseInt(object.getStock());
                    Integer x = stok - Integer.parseInt(object.getCount());
                    Map<String, Object> map = new HashMap<>();
                    map.put("stock", x.toString());
                    ref2.updateChildren(map);
                } else {
                    deteksiStokKosong = true;
                }
            }
            if (deteksiStokKosong) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(MenuCashier.this);
                confirm.setMessage("Berhasil menambah pemasukan.\nBeberapa barang tidak dilakukan proses pengurangan stok, karena stok terdeteksi kosong.");
                confirm.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                confirm.create().show();
            } else {
                Toast.makeText(getBaseContext(), "Berhasil menambah pemasukan", Toast.LENGTH_SHORT).show();
            }
        } else {
            reset();
        }
    }

    private void manualSearch(){
        final View customLayout2 = getLayoutInflater().inflate(R.layout.alert_custom_layout_filter_mc, null);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setView(customLayout2);
        builder2.setTitle("Cari Manual");

        EditText edSearch = customLayout2.findViewById(R.id.edSearch_mc);
        RecyclerView rvMS = customLayout2.findViewById(R.id.rvMenuCashier);

        Query database = FirebaseDatabase.getInstance().getReference("Goods").child(auth);
        ArrayList<Goods> alist = new ArrayList<>();
        ArrayList<Goods> blist = new ArrayList<>();
        ManualAddAdapter manualAddAdapter = new ManualAddAdapter(this, alist);
        rvMS.setAdapter(manualAddAdapter);
        rvMS.setHasFixedSize(true);
        rvMS.setLayoutManager(new LinearLayoutManager(this));

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    alist.clear(); blist.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Goods goods = ds.getValue(Goods.class);
                        alist.add(goods); blist.add(goods);
                    }
                    manualAddAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuCashier.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                wordMCL = s.length();
                if (!s.toString().isEmpty()){
                    wordMC = s.toString().toLowerCase();
                    alist.clear();
                    for (Goods object : blist) {
                        if (object.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                            alist.add(object);
                        }
                    }
                    manualAddAdapter.notifyDataSetChanged();
                } else {
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                alist.clear(); blist.clear();
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    Goods goods = ds.getValue(Goods.class);
                                    alist.add(goods); blist.add(goods);
                                }
                                manualAddAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MenuCashier.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        builder2.setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog1 = builder2.create();
        dialog1.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        dialog1.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cekIntentScanner();
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Settings").child(auth);
                Map<String, Object> map = new HashMap<>();
                map.put("stockReduction", isChecked);
                ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(MenuCashier.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        }
                        if (isChecked){
                            Toast.makeText(MenuCashier.this, "Pengurangan Stok Aktif", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MenuCashier.this, "Pengurangan Stok Tidak Aktif", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(result);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    String vStock = snapshot.child("stock").getValue().toString();
                    String vCount = "1";
                    Trans trans = new Trans(vId, vName,vPrice, vFund, vCategory, vProducer, vExpired, vCount, vStock);

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
        public String id, name, price, fund, category, producer, expired, count, stock;

        public Trans(){

        }

        public Trans(String id, String name, String price, String fund, String category, String producer, String expired, String count, String stock){
            this.id = id;
            this.name = name;
            this.price = price;
            this.fund = fund;
            this.category = category;
            this.producer = producer;
            this.expired = expired;
            this.count = count;
            this.stock = stock;
        }

        public String getId() {
            return id;
        }

        public String getCount() {
            return count;
        }

        public String getStock() {
            return stock;
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