package com.example.gudangsederhana.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gudangsederhana.Goods;
import com.example.gudangsederhana.ManualAddAdapter;
import com.example.gudangsederhana.MenuCashier;
import com.example.gudangsederhana.MenuCreate;
import com.example.gudangsederhana.MenuSettings;
import com.example.gudangsederhana.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuSettingsHelp extends AppCompatActivity {

    Toolbar toolBar;
    LinearLayout llInfoAplikasi_sh, separatorPtjk, separatorBrsrn;
    private String auth;

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
        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        separatorPtjk.setOnClickListener(v -> {
            goToURL("https://drive.google.com/file/d/1xq6Xk0LK8nuPkkmDO4033myreGSwzgSC/view?usp=sharing");
            //Toast.makeText(MenuSettingsHelp.this, "Fitur ini belum tersedia!", Toast.LENGTH_SHORT).show();
        });
        separatorBrsrn.setOnClickListener(v -> {
            dialogSaran();
            //Toast.makeText(MenuSettingsHelp.this, "Fitur ini belum tersedia!", Toast.LENGTH_SHORT).show();
        });
        llInfoAplikasi_sh.setOnClickListener(v -> {
            Intent intent = new Intent(MenuSettingsHelp.this, MenuSettingsHelpInfo.class);
            startActivity(intent);
        });
    }

    private void dialogSaran() {
        final View customLayout2 = getLayoutInflater().inflate(R.layout.alert_custom_layout_suggestion, null);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setView(customLayout2);
        builder2.setTitle("Hubungi Admin");
        EditText edSaranS = customLayout2.findViewById(R.id.edSaranS);

        builder2.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = edSaranS.getText().toString().trim();
                if (s.isEmpty()){
                    Toast.makeText(MenuSettingsHelp.this, "Penjelasan masih kosong", Toast.LENGTH_SHORT).show();
                } else {
                    Idea idea = new Idea(s);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Idea").child(auth).push();
                    ref.setValue(idea).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MenuSettingsHelp.this, "Saran berhasil terkirim", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(MenuSettingsHelp.this, "Saran gagal terkirim", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        builder2.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog1 = builder2.create();
        dialog1.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        dialog1.show();
    }

    private void goToURL(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

    public static class Idea {
        public String idea;

        public Idea(){

        }

        public Idea(String idea){
            this.idea = idea;
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