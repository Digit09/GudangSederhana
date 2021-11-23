package com.example.gudangsederhana.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gudangsederhana.MenuSettings;
import com.example.gudangsederhana.R;

public class MenuSettingsHelp extends AppCompatActivity {

    Toolbar toolBar;
    LinearLayout llInfoAplikasi_sh, separatorPtjk, separatorBrsrn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settings_help);

        toolBar = findViewById(R.id.toolbar);

        setSupportActionBar(toolBar);
        TextView judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Bantuan");

        llInfoAplikasi_sh = findViewById(R.id.llInfoAplikasi_sh);
        separatorPtjk = findViewById(R.id.separatorPtjk);
        separatorBrsrn = findViewById(R.id.separatorBrsrn);

        llInfoAplikasi_sh.setOnClickListener(v -> {
            //Intent intent = new Intent(MenuSettingsHelp.this, MenuSettingsProfile.class);
            //startActivity(intent);
        });
        separatorPtjk.setOnClickListener(v -> {
            Toast.makeText(MenuSettingsHelp.this, "Fitur ini belum tersedia!", Toast.LENGTH_SHORT).show();
        });
        separatorBrsrn.setOnClickListener(v -> {
            Toast.makeText(MenuSettingsHelp.this, "Fitur ini belum tersedia!", Toast.LENGTH_SHORT).show();
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