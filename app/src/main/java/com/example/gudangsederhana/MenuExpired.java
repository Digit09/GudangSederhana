package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuExpired extends AppCompatActivity {

    private TextView judulMenuC;
    private Toolbar toolbar;

    private String auth;
    public static Query database;
    public static GoodsAdapter goodsAdapter;
    public static ArrayList<Goods> list;
    public static ArrayList<Goods> myList;
    public static RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_expired);

        judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Barang Kedaluwarsa");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = findViewById(R.id.rvMainMe);
        list = new ArrayList<>();
        myList = new ArrayList<>();
        goodsAdapter = new GoodsAdapter(this, list);
        recyclerView.setAdapter(goodsAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadAllData();
    }

    private void loadAllData(){
        database = FirebaseDatabase.getInstance().getReference("Goods").child(auth);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    recyclerView.setVisibility(View.VISIBLE);
                    list.clear(); myList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Goods goods = ds.getValue(Goods.class);
                        list.add(goods); myList.add(goods);
                    }
                    goodsAdapter.notifyDataSetChanged();
                } else {
                    recyclerView.setVisibility(View.GONE);
                }

                list.clear();
                for (Goods object : myList) {
                    String expired = object.getExpired().toLowerCase();
                    if (!expired.equals("-")) {
                        if (MainActivity.cariKedaluwarsa(expired)) {
                            list.add(object);
                        }
                    }
                }
                goodsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuExpired.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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