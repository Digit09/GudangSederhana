package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuSearch extends AppCompatActivity {

    public static Integer lengthStrSearch = 0;
    Toolbar toolbar;
    EditText edSearch;
    CardView toolbarSearch;
    RecyclerView recyclerView;
    TextView tvKetRV, judulMenuC;
    ProgressBar progressBar;

    String auth;
    DatabaseReference database;
    GoodsAdapter goodsAdapter;
    ArrayList<Goods> list;
    ArrayList<Goods> myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_search);

        toolbar = findViewById(R.id.toolbar_submenu);
        setSupportActionBar(toolbar);

        judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Cari Barang");

        recyclerView = findViewById(R.id.rvMainA);
        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        edSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edSearch, InputMethodManager.SHOW_IMPLICIT);
        loadSearch(edSearch.getText().toString());
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