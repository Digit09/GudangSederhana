package com.example.gudangsederhana;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MenuScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;
    private String getMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_scanner);

        // Camera Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        Intent intent = getIntent();
        if (intent.hasExtra("sMenu")) {
            getMenu = intent.getStringExtra("sMenu");
        }
    }

    @Override
    public void handleResult(Result result) {
        Intent intent;
        if (getMenu.equals("Cashier")){
            intent = new Intent(MenuScanner.this, MenuCashier.class);
        } else {
            intent = new Intent(MenuScanner.this, MainActivity.class);
        }
        intent.putExtra("result", result.getText());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (getMenu.equals("Cashier")){
            intent = new Intent(getApplicationContext(), MenuCashier.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}