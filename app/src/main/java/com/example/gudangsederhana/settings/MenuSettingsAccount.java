package com.example.gudangsederhana.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gudangsederhana.MainActivity;
import com.example.gudangsederhana.MenuCashier;
import com.example.gudangsederhana.MenuSettings;
import com.example.gudangsederhana.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MenuSettingsAccount extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvEmail_msa, tvKataSandi_msa;
    SwitchCompat scPrivasi;
    String auth;
    LinearLayout llEmail_msa, llKataSandi_msa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settings_account);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tvEmail_msa = findViewById(R.id.tvEmail_msa);
        tvKataSandi_msa = findViewById(R.id.tvKataSandi_msa);
        scPrivasi = findViewById(R.id.scPrivasiAkun);
        llEmail_msa = findViewById(R.id.llEmail_msa);
        llKataSandi_msa = findViewById(R.id.llKataSandi_msa);

        TextView judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Akun");

        llEmail_msa.setOnClickListener(v -> {
            //changeEmail();
            Toast.makeText(MenuSettingsAccount.this, "Fitur ini belum tersedia!", Toast.LENGTH_SHORT).show();
        });
        llKataSandi_msa.setOnClickListener(v -> {
            String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseAuth.getInstance().sendPasswordResetEmail(MainActivity.emailSaved.getString(auth, "false")).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(MenuSettingsAccount.this, "Periksa email anda untuk melakukan Reset!!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MenuSettingsAccount.this, "Harap Coba lagi!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        loadAccount();
        loadSettings();
    }

    private void loadAccount() {
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tvEmail_msa.setText(MainActivity.emailSaved.getString(auth, "false"));
        tvKataSandi_msa.setText(convertPass(MainActivity.passwordSaved.getString(auth, "false")));
    }

    private String convertPass(String pass){
        String temp = pass.substring(0, 3);
        for (Integer x = 3; x < pass.length(); x++){
            temp = temp + "*";
        }
        return temp;
    }

    private void loadSettings(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Settings").child(auth);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    scPrivasi.setChecked(Boolean.parseBoolean(snapshot.child("accountPrivacy").getValue().toString()));
                } else {
                    scPrivasi.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuSettingsAccount.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeEmail(){
        final View customLayout2 = getLayoutInflater().inflate(R.layout.alert_custom_layout_email_msa, null);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setView(customLayout2);
        builder2.setTitle("Ubah Email");

        EditText edEmail = customLayout2.findViewById(R.id.edEmail_msa);
        builder2.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = edEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(MenuSettingsAccount.this, "Masukkan nama email baru terlebih dahulu!", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder newData = new AlertDialog.Builder(getApplicationContext());
                    newData.setTitle("Konfirmasi Ubah Email");
                    newData.setMessage("Apakah anda yakin data yang dimasukkan sudah benar?");
                    newData.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MenuSettingsAccount.this, "Email berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MenuSettingsAccount.this, "Email gagal diperbarui", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    newData.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    newData.create().show();
                }
            }
        });
        builder2.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder2.create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        scPrivasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Settings").child(auth);
                Map<String, Object> map = new HashMap<>();
                map.put("accountPrivacy", isChecked);
                ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(MenuSettingsAccount.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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