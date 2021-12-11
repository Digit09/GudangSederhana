package com.example.gudangsederhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MenuIncome extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<Sales> myList;
    private IncomeAdapter incomeAdapter;
    private RecyclerView recyclerView;
    private String auth;
    private TextView tvKet_mc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_income);

        TextView judulMenuC = findViewById(R.id.judulMenu);
        judulMenuC.setText("Pendapatan");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = findViewById(R.id.rvIncomeA);
        tvKet_mc = findViewById(R.id.tvKet_mc);
        myList = new ArrayList<>();
        incomeAdapter = new IncomeAdapter(this, myList);
        recyclerView.setAdapter(incomeAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
        periksaTahun();
    }

    private void loadData(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sales").child(auth);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    myList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Sales sales = ds.getValue(Sales.class);
                        myList.add(sales);
                    }
                    GraphView graph = (GraphView) findViewById(R.id.gvIncome_mi);
                    //graph.setTitle("Grafik Pendapatan Bulanan");
                    /*LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                            new DataPoint(0, 1),
                            new DataPoint(1, 5),
                            new DataPoint(2, 3),
                            new DataPoint(3, 2),
                            new DataPoint(4, 6)
                    });*/
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                    series.appendData(new DataPoint(1, 0), true, 60);
                    for (Sales object : myList){
                        graph.getViewport().setXAxisBoundsManual(true);
                        Integer x = Integer.parseInt(object.getDate().substring(0, 2));
                        Integer y = Integer.parseInt(object.getIncome());
                        series.appendData(new DataPoint(x, y), true, 60);
                    }
                    graph.addSeries(series);
                    graph.getViewport().setMinX(1);
                    graph.getViewport().setMaxX(12);
                    graph.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
                    graph.getViewport().setScrollable(true);  // activate horizontal scrolling
                    graph.getViewport().setScalableY(true);  // activate horizontal and vertical zooming and scrolling
                    graph.getViewport().setScrollableY(true);  // activate vertical scrolling
                    series.setDrawBackground(true);
                    series.setBackgroundColor(Color.rgb(171, 204, 255));
                    incomeAdapter.notifyDataSetChanged();
                } else {
                    // Belom ada
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuIncome.this, "Gagal menampilkan data", Toast.LENGTH_LONG).show();
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void periksaTahun(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy");
        LocalDateTime now = LocalDateTime.now();
        String dateNowV = dtf.format(now);
        Toast.makeText(MenuIncome.this, "Memeriksa data lama...", Toast.LENGTH_LONG).show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sales").child(auth);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvKet_mc.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Boolean dapat = false;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String dateDBV = ds.child("date").getValue().toString();
                        if (!dateDBV.substring(3).equals(dateNowV)){
                            dapat = true;
                            break;
                        }
                    }
                    if (dapat){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sales").child(auth);
                        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MenuIncome.this, "Membersihkan data lama...", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MenuIncome.this, "Terjadi kesalahan saat membersihkan data lama...", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(MenuIncome.this, "Data lama tidak ada", Toast.LENGTH_LONG).show();
                    }
                } else {
                    tvKet_mc.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(MenuIncome.this, "Pendapatan bulanan masih kosong.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuIncome.this, "Gagal menampilkan data", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
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