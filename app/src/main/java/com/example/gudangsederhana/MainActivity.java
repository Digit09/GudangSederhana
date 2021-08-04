package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private long backPressedTime;
    private Toast backToast;
    private Toolbar toolBar;
    private CardView toolbarSearch;
    private RelativeLayout rlRead, rlDataShow, rlRecyclerviewSearch;
    private TextView tvResult, tvNama, tvHarga, tvModal, tvProdusen, tvKedaluwarsa, tvKet, tvKetRV;
    private EditText edSearch;
    private Button btManualAdd, updateBtn, deleteBtn, scannerBtn, showBtn, hideBtn;
    private String auth, getResult = "0";
    private Boolean datakosong = false;

    DatabaseReference dataRef;
    RecyclerView recyclerView;
    GoodsAdapter goodsAdapter;
    ArrayList<Goods> list;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        toolbarSearch = findViewById(R.id.toolbarSearch);
        scannerBtn = findViewById(R.id.btScan);
        showBtn = findViewById(R.id.btShow);
        hideBtn = findViewById(R.id.btHide);
        btManualAdd = findViewById(R.id.btManualAdd);

        tvResult = findViewById(R.id.tvResult);
        tvNama = findViewById(R.id.tvNama);
        tvHarga = findViewById(R.id.tvHarga);
        tvModal = findViewById(R.id.tvModal);
        tvProdusen = findViewById(R.id.tvProdusen);
        tvKedaluwarsa = findViewById(R.id.tvKedaluwarsa);
        tvKet = findViewById(R.id.tvKet);
        rlRead = findViewById(R.id.rlRead);
        rlDataShow = findViewById(R.id.rlDataShow);
        edSearch = findViewById(R.id.edSearch);

        fAuth = FirebaseAuth.getInstance();

        loadJudul();

        allSettingClick();

        cekIntentScanner();
        cekInternet();
    }

    private void allSettingClick() {
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

        scannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MenuScanner.class));
                finish();
            }
        });

        btManualAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuCreate.class);
            startActivity(intent);
            MenuCreate.getEdId.setEnabled(true);
            MenuCreate.getEdId.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(MenuCreate.getEdId, InputMethodManager.SHOW_IMPLICIT);
            finish();
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
                if (!s.toString().isEmpty()){
                    loadSearch(s.toString());
                } else {
                    Toast.makeText(MainActivity.this, "GAGAL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadSearch(String data){

        recyclerView = findViewById(R.id.rvMain);
        dataRef = FirebaseDatabase.getInstance().getReference("Goods").child(auth);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        goodsAdapter = new GoodsAdapter(this,list);
        recyclerView.setAdapter(goodsAdapter);

        Query query = dataRef.orderByChild("name").startAt(data).endAt(data+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Goods goods = dataSnapshot.getValue(Goods.class);
                    list.add(goods);
                }
                goodsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal mengambil data (Kode : MAEC1)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cekIntentScanner() {
        Intent intent = getIntent();
        if (intent.hasExtra("result")) {
            getResult = intent.getStringExtra("result");
            loadData(getResult);
        } else if (intent.hasExtra("result2")) {
            getResult = intent.getStringExtra("result2");
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
                    tvKet.setVisibility(View.GONE);
                    rlRead.setVisibility(View.VISIBLE);

                    String vId = snapshot.child("id").getValue().toString();
                    String vName = snapshot.child("name").getValue().toString();
                    String vPrice = snapshot.child("price").getValue().toString();
                    String vFund = snapshot.child("fund").getValue().toString();
                    String vProducer = snapshot.child("producer").getValue().toString();
                    String vExpired = snapshot.child("expired").getValue().toString();

                    tvResult.setText(vId);
                    tvNama.setText(vName);
                    tvHarga.setText(rupiahkan(vPrice));
                    tvModal.setText(rupiahkan(vFund));
                    tvProdusen.setText(vProducer);
                    tvKedaluwarsa.setText(vExpired);

                    updateBtn = findViewById(R.id.btUbah);
                    deleteBtn = findViewById(R.id.btHapus);

                    updateBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, MenuUpdate.class);
                            intent.putExtra("idBarangU", result);
                            startActivity(intent);
                            finish();
                        }
                    });
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteComfirm(result);
                        }
                    });
                } else {
                    tvKet.setVisibility(View.VISIBLE);
                    rlRead.setVisibility(View.GONE);

                    dataBaruTerdeteksi(result);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal mencari data", Toast.LENGTH_LONG).show();
                tvKet.setVisibility(View.VISIBLE);
                rlRead.setVisibility(View.GONE);
            }
        });
    }

    String rupiahkan(String nominal) {
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
                        Toast.makeText(MainActivity.this, "Berhasil menghapus barang", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        intent.removeExtra("result");
                        finish();
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Gagal menghapus barang", Toast.LENGTH_SHORT).show();
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

    private void dataBaruTerdeteksi(String idBarangBaru) {
        AlertDialog.Builder newData = new AlertDialog.Builder(this);
        newData.setTitle("Konfirmasi Barang Baru");
        newData.setMessage("Barang tidak ditemukan. Apakah anda ingin menambahkan barang ini?");
        newData.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, MenuCreate.class);
                intent.putExtra("idBarangC", idBarangBaru);
                startActivity(intent);
            }
        });
        newData.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        newData.create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = fAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        } else if (datakosong){
            startActivity(new Intent(MainActivity.this, MenuInitialSetup.class));
            finish();
        }
    }

    private void loadJudul(){
        String ref = fAuth.getCurrentUser().getUid();
        DatabaseReference data = FirebaseDatabase.getInstance().getReference("Sellers").child(ref);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    datakosong = true;
                } else {
                    TextView judulMenuC = findViewById(R.id.tvNamaTokoToolbar);
                    judulMenuC.setText(snapshot.child("shopName").getValue().toString());

                    judulMenuC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tvKet.setVisibility(View.VISIBLE);
                            rlRead.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Terjadi Kesalahan (Kode : MAEC1)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Tekan lagi tombol Kembali untuk keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btCari){
            if (toolbarSearch.getVisibility() == View.VISIBLE) {
                toolbarSearch.setVisibility(View.GONE);
                edSearch.setText("");
                if (!tvResult.getText().toString().trim().equals("tvResult")){
                    rlRead.setVisibility(View.VISIBLE);
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edSearch.getWindowToken(), 0);
            } else {
                toolbarSearch.setVisibility(View.VISIBLE);
                rlRead.setVisibility(View.GONE);
                tvKet.setVisibility(View.VISIBLE);
                edSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        }
        switch (item.getItemId()) {
            case R.id.logout:
                AlertDialog.Builder logout = new AlertDialog.Builder(this);
                logout.setTitle("Keluar dari Akun");
                logout.setMessage("Apakah Anda yakin ingin keluar dari akun ini?");
                logout.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut(); //logout
                        Intent intent = new Intent(MainActivity.this, Login.class);
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
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, MenuSettings.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void cekInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
        } else {
            Toast.makeText(getBaseContext(), "Belum terhubung ke jaringan internet!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        cekInternet();
    }
}