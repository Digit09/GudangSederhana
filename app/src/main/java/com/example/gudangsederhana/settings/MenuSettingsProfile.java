package com.example.gudangsederhana.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gudangsederhana.MainActivity;
import com.example.gudangsederhana.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuSettingsProfile extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvNamaToko, tvNamaPemilik, tvAlamat, tvNoTelp;
    LinearLayout llNamaToko, llNamaPemilik, llAlamat, llNoTelp;
    public static String getEdit = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settings_profil);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvNamaToko = findViewById(R.id.tvNamaToko_msp);
        tvNamaPemilik = findViewById(R.id.tvNamaPemilik_msp);
        tvAlamat = findViewById(R.id.tvAlamat_msp);
        tvNoTelp = findViewById(R.id.tvNoTelp_msp);
        llNamaToko = findViewById(R.id.llNamaToko_msp);
        llNamaPemilik = findViewById(R.id.llNamaPemilik_msp);
        llAlamat = findViewById(R.id.llAlamat_msp);
        llNoTelp = findViewById(R.id.llNoTelp_msp);

        TextView judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Profil");
        loadData();
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

    @Override
    protected void onResume() {
        super.onResume();
        listenerData();
    }

    private void listenerData(){
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sellers").child(auth);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String namaT = snapshot.child("shopName").getValue().toString();
                    String namaP = snapshot.child("owner").getValue().toString();
                    String alamat = snapshot.child("address").getValue().toString();
                    String noHp = snapshot.child("phoneNumber").getValue().toString();

                    MainActivity.shopNameSaved.edit().putString(auth, namaT).apply();
                    MainActivity.ownerSaved.edit().putString(auth, namaP).apply();
                    MainActivity.addressSaved.edit().putString(auth, alamat).apply();
                    MainActivity.phoneNumberSaved.edit().putString(auth, noHp).apply();
                    tvNamaToko.setText(namaT);
                    tvNamaPemilik.setText(namaP);
                    tvAlamat.setText(alamat);
                    tvNoTelp.setText(noHp);

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                    llNamaToko.setOnClickListener(v -> {
                        getEdit = "shopName";
                        bottomSheetDialog.show(getSupportFragmentManager(), "bottomSheetDialog");
                    });
                    llNamaPemilik.setOnClickListener(v -> {
                        getEdit = "owner";
                        bottomSheetDialog.show(getSupportFragmentManager(), "bottomSheetDialog");
                    });
                    llAlamat.setOnClickListener(v -> {
                        getEdit = "address";
                        bottomSheetDialog.show(getSupportFragmentManager(), "bottomSheetDialog");
                    });
                    llNoTelp.setOnClickListener(v -> {
                        getEdit = "phoneNumber";
                        bottomSheetDialog.show(getSupportFragmentManager(), "bottomSheetDialog");
                    });
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadData(){
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String loadName = MainActivity.shopNameSaved.getString(auth, "false");
        String loadName2 = MainActivity.ownerSaved.getString(auth, "false");
        String loadName3 = MainActivity.addressSaved.getString(auth, "false");
        String loadName4 = MainActivity.phoneNumberSaved.getString(auth, "false");
        tvNamaToko.setText(loadName);
        tvNamaPemilik.setText(loadName2);
        tvAlamat.setText(loadName3);
        tvNoTelp.setText(loadName4);
    }
}