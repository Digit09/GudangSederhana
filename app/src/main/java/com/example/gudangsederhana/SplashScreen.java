package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class SplashScreen extends AppCompatActivity {

    private String version;
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tvVersion = findViewById(R.id.tvVersion_ss);
        try {
            Context context = SplashScreen.this;
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName.trim();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version = "error...";
        }

        tvVersion.setText(version);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String versionDb = snapshot.child("version").getValue().toString();
                            String newestUrlDb = snapshot.child("newestUrl").getValue().toString();
                            String lockDb = snapshot.child("lock").getValue().toString();
                            String notifyShowDb = snapshot.child("Notify").child("show").getValue().toString();
                            String notifyTextDb = snapshot.child("Notify").child("text").getValue().toString();
                            if (Boolean.parseBoolean(lockDb)){
                                dialogLock();
                            } else if (!versionDb.equals(version)){
                                dialogUpdate(versionDb, newestUrlDb);
                            } else if (Boolean.parseBoolean(notifyShowDb)){
                                dialogNotify(notifyTextDb);
                            } else {
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getBaseContext(), "Terjadi kesalahan saat mengambil data", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, 2000);
    }

    private void dialogLock(){
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle("Pemberitahuan");
        confirm.setMessage("Aplikasi masih dalam tahap perbaikan. Mohon maaf atas ketidaknyamanannya.");
        confirm.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                SplashScreen.this.finish();
                System.exit(0);
            }
        });
        confirm.create().show();
    }

    private void dialogUpdate(String versionRtdb, String newestUrl){
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle("Pemberitahuan");
        confirm.setMessage("Versi terbaru aplikasi Gudang Sederhana ("+versionRtdb+") telah tersedia. Unduh sekarang?");
        confirm.setPositiveButton("Unduh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cara update tanpa play store atau download APK
                // Toast.makeText(SplashScreen.this, version, Toast.LENGTH_LONG).show();
                download(versionRtdb, newestUrl);
            }
        }).setNegativeButton("Jangan sekarang", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        confirm.create().show();
    }

    private void dialogNotify(String text){
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle("Pemberitahuan");
        confirm.setMessage(text);
        confirm.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        confirm.create().show();
    }

    private void download(String versionRtdb, String urlFile){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReferenceFromUrl(urlFile);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFiles(SplashScreen.this, "Gudang Sederhana v" + versionRtdb, "apk", DIRECTORY_DOWNLOADS, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "Gagal mengunduh file", Toast.LENGTH_LONG).show();
            }
        });
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("")
    }

    private void downloadFiles(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + "." + fileExtension);

        downloadManager.enqueue(request);
    }
}