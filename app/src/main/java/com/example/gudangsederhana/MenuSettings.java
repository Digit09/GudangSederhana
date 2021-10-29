package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gudangsederhana.settings.MenuSettingsProfile;
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
    SharedPreferences shopNameSaved, ownerSaved;
    String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settings);
        toolBar = findViewById(R.id.toolbar);

        setSupportActionBar(toolBar);
        TextView judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Setelan");

        rlProfil = findViewById(R.id.rlProfil_ms);
        tvToko = findViewById(R.id.tvToko);
        tvPemilik = findViewById(R.id.tvPemilik);
        shopNameSaved = getApplicationContext().getSharedPreferences("shopNameSaved", MODE_PRIVATE);
        ownerSaved = getApplicationContext().getSharedPreferences("ownerSaved", MODE_PRIVATE);
        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadJudul();
        rlProfil.setOnClickListener(v -> {
            Intent intent = new Intent(MenuSettings.this, MenuSettingsProfile.class);
            startActivity(intent);
        });
    }

    private void loadJudul(){
        String loadName = MainActivity.shopNameSaved.getString(auth, "false");
        String loadName2 = MainActivity.ownerSaved.getString(auth, "false");
        tvToko.setText(loadName);
        tvPemilik.setText(loadName2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJudul();
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