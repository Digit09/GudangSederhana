package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    long backPressedTime;
    Toast backToast;
    Toolbar toolBar;
    RelativeLayout rlRead, rlDataShow, rlAllButton;
    TextView tvResult, tvNama, tvHarga, tvModal, tvKategori, tvProdusen, tvKedaluwarsa, tvStok, tvKet, judulMenuC;
    Button updateBtn, deleteBtn, showBtn, hideBtn, btManualAdd, scannerBtn;
    String auth, getResult = "0";
    public static SharedPreferences shopNameSaved, ownerSaved, addressSaved, phoneNumberSaved, emailSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shopNameSaved = getApplicationContext().getSharedPreferences("shopNameSaved", MODE_PRIVATE);
        ownerSaved = getApplicationContext().getSharedPreferences("ownerSaved", MODE_PRIVATE);
        addressSaved = getApplicationContext().getSharedPreferences("addressSaved", MODE_PRIVATE);
        phoneNumberSaved = getApplicationContext().getSharedPreferences("phoneNumberSaved", MODE_PRIVATE);
        emailSaved = getApplicationContext().getSharedPreferences("emailSaved", MODE_PRIVATE);

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        declaration();
        allSettingClick();
        //loadJudul();
        //cekIntentScanner();
        cekInternet();
    }

    private void declaration(){

        rlAllButton = findViewById(R.id.rlAllButton);
        scannerBtn = findViewById(R.id.btScan);
        btManualAdd = findViewById(R.id.btManualAdd);

        updateBtn = findViewById(R.id.btUbah);
        deleteBtn = findViewById(R.id.btHapus);

        tvResult = findViewById(R.id.tvResult);
        tvNama = findViewById(R.id.tvNama);
        tvHarga = findViewById(R.id.tvHarga);
        tvModal = findViewById(R.id.tvModal);
        tvKategori = findViewById(R.id.tvKategori);
        tvProdusen = findViewById(R.id.tvProdusen);
        tvKedaluwarsa = findViewById(R.id.tvKedaluwarsa);
        tvStok = findViewById(R.id.tvStok);
        tvKet = findViewById(R.id.tvKet);
        rlRead = findViewById(R.id.rlRead);
        rlDataShow = findViewById(R.id.rlDataShow);
        showBtn = findViewById(R.id.btShow);
        hideBtn = findViewById(R.id.btHide);
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
                Intent intent = new Intent(getApplicationContext(), MenuScanner.class);
                intent.putExtra("sMenu", "Main");
                startActivity(intent);
                finish();
            }
        });

        btManualAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuCreate.class);
            intent.putExtra("btAdd", "true");
            startActivity(intent);
        });
    }

    public static String capitalizeEachWord(String str){
        if (!str.isEmpty()) {
            String words[] = str.split("\\s");
            String c = "";
            for (String w : words) {
                String firstChar = w.substring(0, 1);
                String afterFisrtChar = w.substring(1);
                c += firstChar.toUpperCase() + afterFisrtChar + " ";
            }
            return c.trim();
        }
        return str;
    }

    private void cekIntentScanner() {
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
                    tvKet.setVisibility(View.GONE);
                    rlRead.setVisibility(View.VISIBLE);

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
                            Intent intent = new Intent(MainActivity.this, MenuUpdate.class);
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
                } else {
                    tvKet.setVisibility(View.VISIBLE);
                    rlRead.setVisibility(View.GONE);
                    if (getIntent().hasExtra("result")) {
                        dataBaruTerdeteksi(result);
                    }
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
                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Berhasil menghapus barang", Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            intent.removeExtra("result");
                            finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Gagal menghapus barang", Toast.LENGTH_SHORT).show();
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

    private void dataBaruTerdeteksi(String idBarangBaru) {
        getIntent().removeExtra("result");
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        } else {
            String ref = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference data = FirebaseDatabase.getInstance().getReference("Sellers").child(ref);
            data.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (Boolean.parseBoolean(snapshot.child("ban").getValue().toString())) {
                            FirebaseAuth.getInstance().signOut(); //logout
                            Toast.makeText(MainActivity.this, "Akun anda belum teraktivasi oleh admin.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Login.class));
                            finish();
                        } else {
                            auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            loadJudul();
                            cekIntentScanner();
                        }
                    } else {
                        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        loadJudul();
                        cekIntentScanner();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void loadJudul(){
        judulMenuC = findViewById(R.id.judulMenu);
        if (cekSharedPreferenced()){
            String ref = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference data = FirebaseDatabase.getInstance().getReference("Sellers").child(ref);
            data.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()){
                        startActivity(new Intent(MainActivity.this, MenuInitialSetup.class));
                        finish();
                    } else {
                        String getShopName = snapshot.child("shopName").getValue().toString();
                        String getOwner = snapshot.child("owner").getValue().toString();
                        String getAddress = snapshot.child("address").getValue().toString();
                        String getPhoneNumber = snapshot.child("phoneNumber").getValue().toString();
                        //String getEmail = snapshot.child("email").getValue().toString();
                        //String getPass = snapshot.child("pass").getValue().toString();
                        shopNameSaved.edit().putString(auth, getShopName).apply();
                        ownerSaved.edit().putString(auth, getOwner).apply();
                        addressSaved.edit().putString(auth, getAddress).apply();
                        phoneNumberSaved.edit().putString(auth, getPhoneNumber).apply();
                        judulMenuC.setText(shopNameSaved.getString(auth, "false"));
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
        } else {
            judulMenuC.setText(shopNameSaved.getString(auth, "false"));
        }
    }

    private Boolean cekSharedPreferenced(){
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String loadName = shopNameSaved.getString(auth, "false");
        String loadName2 = ownerSaved.getString(auth, "false");
        String loadName3 = addressSaved.getString(auth, "false");
        String loadName4 = phoneNumberSaved.getString(auth, "false");
        String loadName5 = emailSaved.getString(auth, "false");
        if (loadName.equals("false")){
            return true;
        } else if (loadName2.equals("false")){
            return true;
        } else if (loadName3.equals("false")){
            return true;
        } else if (loadName4.equals("false")){
            return true;
        } else if (loadName5.equals("false")){
            return true;
        } else {
            return false;
        }
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
            Intent intent = new Intent(this, MenuSearch.class);
            intent.putExtra("shopName", judulMenuC.getText().toString());
            startActivity(intent);
        } else if (item.getItemId() == R.id.cashier) {
            Intent intent = new Intent(MainActivity.this, MenuCashier.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.income) {
            Intent intent = new Intent(MainActivity.this, MenuIncome.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, MenuSettings.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.logout) {
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
        }
//        switch (item.getItemId()) {
//            case R.id.logout:
//
//            case R.id.settings:
//
//        }
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
}