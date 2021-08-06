package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MenuSearch extends AppCompatActivity {

    public static Integer lengthStrSearch = 0;
    private Toolbar toolbar;
    private EditText edSearch;
    private CardView toolbarSearch;
    private RecyclerView recyclerView;
    private TextView tvKetRV, judulMenuC;
    private ProgressBar progressBar;
    private Button btShow_ms, btShare, btSaveDocx;
    private SharedPreferences shopNameSaved;
    private String loadName;

    private String auth;
    private DatabaseReference database;
    private GoodsAdapter goodsAdapter;
    private ArrayList<Goods> list;
    private ArrayList<Goods>  myList;
    private File filePath = null;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_search);

        toolbar = findViewById(R.id.toolbar_submenu);
        setSupportActionBar(toolbar);

        judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Cari Barang");

        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = findViewById(R.id.rvMainA);
        database = FirebaseDatabase.getInstance().getReference("Goods").child(auth);
        list = new ArrayList<>();
        myList = new ArrayList<>();
        goodsAdapter = new GoodsAdapter(this, list);
        recyclerView.setAdapter(goodsAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbarSearch = findViewById(R.id.toolbarSearch);
        edSearch = findViewById(R.id.edSearch);
        tvKetRV = findViewById(R.id.tvKetRV);
        progressBar = findViewById(R.id.uploadProgress_ms);
        btShow_ms = findViewById(R.id.btShow_ms);

        shopNameSaved = getApplicationContext().getSharedPreferences("shopNameSaved", MODE_PRIVATE);
        loadName = shopNameSaved.getString(auth, "false");

        itemAction();
    }

    private void itemAction(){
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
                    lengthStrSearch = s.length();
                    loadSearch(s.toString());
                } else {
                    lengthStrSearch = 0;
                    loadAllData();
                }
            }
        });
        btShow_ms.setOnClickListener(v -> {
            final View customLayout = getLayoutInflater().inflate(R.layout.alert_custom_layout_options_ms, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(customLayout);
            builder.setTitle("Bagikan dalam bentuk :");

            btShare = customLayout.findViewById(R.id.btShare);
            btSaveDocx = customLayout.findViewById(R.id.btSaveDocx);

            btShare.setOnClickListener(v1 -> {
                shareToWA();
            });

            btSaveDocx.setOnClickListener(v1 -> {
                final View customLayout2 = getLayoutInflater().inflate(R.layout.alert_custom_layout_docx_ms, null);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setView(customLayout2);
                builder2.setTitle("Nama File Dokumen (*.docx)");

                EditText edNamaFile = customLayout2.findViewById(R.id.edFileNameDocx);
                edNamaFile.setText("Barang-"+loadName);
                builder2.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = edNamaFile.getText().toString();
                        if (str.isEmpty()){
                            edNamaFile.setError("Nama file harus diisi");
                        } else {
                            prepareDocx(str);
                            createDocx();
                        }
                    }
                });
                builder2.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog1 = builder2.create();
                dialog1.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                dialog1.show();
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
        });
    }

    private void prepareDocx(String str){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PackageManager.PERMISSION_GRANTED);
        // parameter_1 can switch with this : getExternalFilesDir(null) -> its gonna save to com.example.gudangsederhana
        filePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), str+".docx");

        try {
            if (!filePath.exists()){
                filePath.createNewFile();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createDocx(){
        try {
            XWPFDocument xwpfDocument = new XWPFDocument();
            XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();
            XWPFRun xwpfRun = xwpfParagraph.createRun();

            xwpfRun.setFontSize(12);
            xwpfRun.setFontFamily("Times New Roman");

            xwpfRun.setText("Aplikasi Gudang Sederhana Berbagi Data Barang");
            xwpfRun.addBreak(); xwpfRun.addBreak();

            xwpfRun.setText("Berikut data barang dari toko anda yang bernama " + loadName + " :");
            xwpfRun.addBreak(); xwpfRun.addBreak();
            Integer i = 1;
            for (Goods goods : list) {
                xwpfRun.addTab(); xwpfRun.setText("Barang ke-" + i.toString()); xwpfRun.addBreak();
                xwpfRun.addTab(); xwpfRun.setText("ID Barang : " + goods.getId()); xwpfRun.addBreak();
                xwpfRun.addTab(); xwpfRun.setText("Nama Barang : " + goods.getName()); xwpfRun.addBreak();
                xwpfRun.addTab(); xwpfRun.setText("Harga Barang : " + MainActivity.rupiahkan(goods.getPrice())); xwpfRun.addBreak(); xwpfRun.addBreak();
                i++;
            }
            xwpfRun.setText("Mohon data jangan disalahgunakan."); xwpfRun.addBreak();
            xwpfRun.setText("Developer Aplikasi : Digit Marsshal Assah | BanuaDev."); xwpfRun.addBreak();
            xwpfRun.setText("Kontak personal : +6285757044494.");

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            xwpfDocument.write(fileOutputStream);

            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
                String textT = "Barang berhasil tersimpan dalam bentuk dokumen (*.docx)";
                Toast.makeText(MenuSearch.this, textT, Toast.LENGTH_LONG).show();
            }
            xwpfDocument.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void shareToWA(){
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, convertToTextWa());
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MenuSearch.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAllData(){
        if (database != null) {
            progressBar.setVisibility(View.VISIBLE);
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        tvKetRV.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        // Long c = snapshot.getChildrenCount();
                        // Toast.makeText(MainActivity.this, c.toString(), Toast.LENGTH_SHORT).show();
                        list.clear(); myList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            Goods goods = ds.getValue(Goods.class);
                            list.add(goods); myList.add(goods);
                        }
                        goodsAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        tvKetRV.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MenuSearch.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void loadSearch(String data){
        progressBar.setVisibility(View.VISIBLE);
        if (!data.isEmpty()) {
            //Toast.makeText(MainActivity.this, d, Toast.LENGTH_SHORT).show();
        /*
        Query query = database.orderByChild("name").startAt(d).endAt(d+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    tvKetRV.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Goods goods = ds.getValue(Goods.class);
                        list.add(goods);
                    }
                    goodsAdapter.notifyDataSetChanged();
                } else {
                    tvKetRV.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */

            list.clear();
            for (Goods object : myList) {
                if (object.getName().toLowerCase().startsWith(data.toLowerCase())) {
                    list.add(object);
                }
            }

            if (!list.isEmpty()) {
                tvKetRV.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                tvKetRV.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            goodsAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        } else {
            loadAllData();
            progressBar.setVisibility(View.GONE);
        }
    }

    private String convertToTextWa(){
        String s = "*Aplikasi Gudang Sederhana Berbagi Data Barang*" + "\n\n" +
                "Berikut data barang dari toko anda yang bernama " + loadName + " :\n\n";
        for(Goods goods : list) {
            s +=
                    "ID Barang : " + goods.getId() + "\n" +
                            "Nama Barang : " + goods.getName() + "\n" +
                            "Harga Barang : " + MainActivity.rupiahkan(goods.getPrice()) + "\n\n";
        }
        s += "Mohon data jangan disalahgunakan.\nDeveloper Aplikasi : Digit Marsshal Assah | BanuaDev.\nKontak personal : +6285757044494.";
        return s;
    }

    @Override
    protected void onStart() {
        super.onStart();
        edSearch.requestFocus();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edSearch, InputMethodManager.SHOW_IMPLICIT);
        loadSearch(edSearch.getText().toString());
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
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edSearch.getWindowToken(), 0);
    }
}