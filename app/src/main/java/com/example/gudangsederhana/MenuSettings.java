package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gudangsederhana.settings.MenuSettingsProfil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuSettings extends AppCompatActivity {

    Toolbar toolBar;
    RelativeLayout rlProfil;
    TextView tvToko, tvPemilik;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settings);
        toolBar = findViewById(R.id.toolbar_submenu);

        setSupportActionBar(toolBar);
        TextView judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Setelan");

        rlProfil = findViewById(R.id.rlProfil_ms);
        tvToko = findViewById(R.id.tvToko);
        tvPemilik = findViewById(R.id.tvPemilik);

        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("Sellers").child(auth);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String id = snapshot.child("uid").getValue().toString();
                    tvToko.setText(snapshot.child("shopName").getValue().toString());
                    tvPemilik.setText(snapshot.child("owner").getValue().toString());

                    rlProfil.setOnClickListener(v -> {
                        Intent intent = new Intent(MenuSettings.this, MenuSettingsProfil.class);
                        intent.putExtra("uidPemilik", id);
                        startActivity(intent);
                    });
                } else {
                    Toast.makeText(MenuSettings.this, "Gagal mengambil data (Kode : MPEC1)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuSettings.this, "Gagal mengambil data (Kode : MPEC2)", Toast.LENGTH_SHORT).show();
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