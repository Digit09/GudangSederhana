package com.example.gudangsederhana;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManualAddAdapter extends RecyclerView.Adapter<ManualAddAdapter.MyViewHolder> {

    Context context;
    ArrayList<Goods> list;

    public ManualAddAdapter (Context context, ArrayList<Goods> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ManualAddAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_main_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManualAddAdapter.MyViewHolder holder, int position) {
        Goods goods = list.get(position);
        holder.id = goods.getId();
        holder.nama.setText(goods.getName());
        holder.produsen.setText(goods.getProducer());
        holder.harga.setText(MainActivity.rupiahkan(goods.getPrice()));
        holder.rlItem.setOnClickListener(v -> {
            AlertDialog.Builder newData = new AlertDialog.Builder(context);
            newData.setMessage("Tambahkan barang?");
            newData.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tempTransaction(holder.id);
                }
            });
            newData.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            newData.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlItem;
        String id;
        TextView nama, produsen, harga;
        EditText edSearch;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = null;
            rlItem = itemView.findViewById(R.id.rlItem_mi);
            nama = itemView.findViewById(R.id.tvNamaBarang_item);
            produsen = itemView.findViewById(R.id.tvProdusen_item);
            harga = itemView.findViewById(R.id.tvHarga_item);
            edSearch = itemView.findViewById(R.id.edSearch_mc);
        }
    }

    private void tempTransaction(String result) {
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(result);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    MenuCashier.tvKet.setVisibility(View.GONE);
                    MenuCashier.recyclerView.setVisibility(View.VISIBLE);
                    MenuCashier.rlOpsiKasir.setVisibility(View.VISIBLE);
                    String vId = snapshot.child("id").getValue().toString();
                    String vName = snapshot.child("name").getValue().toString();
                    String vPrice = snapshot.child("price").getValue().toString();
                    String vFund = snapshot.child("fund").getValue().toString();
                    String vCategory = snapshot.child("category").getValue().toString();
                    String vProducer = snapshot.child("producer").getValue().toString();
                    String vExpired = snapshot.child("expired").getValue().toString();
                    String vCount = "1";
                    MenuCashier.Trans trans = new MenuCashier.Trans(vId, vName,vPrice, vFund, vCategory, vProducer, vExpired, vCount);

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Transactions").child(auth).child(vId);
                    ref2.setValue(trans).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    MenuCashier.tvKet.setVisibility(View.VISIBLE);
                    MenuCashier.recyclerView.setVisibility(View.GONE);
                    MenuCashier.rlOpsiKasir.setVisibility(View.GONE);
                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Gagal mencari data", Toast.LENGTH_LONG).show();
                MenuCashier.tvKet.setVisibility(View.VISIBLE);
                MenuCashier.recyclerView.setVisibility(View.GONE);
                MenuCashier.rlOpsiKasir.setVisibility(View.GONE);
            }
        });
    }
}
