package com.example.gudangsederhana;

import android.content.Context;
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

public class CashierAdapter extends RecyclerView.Adapter<CashierAdapter.MyViewHolder> {

    Context context;
    ArrayList<Goods> list;

    public CashierAdapter (Context context, ArrayList<Goods> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CashierAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_cashier_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CashierAdapter.MyViewHolder holder, int position) {
        Goods goods = list.get(position);
        holder.id_barcode = goods.getId();
        holder.nama.setText(goods.getName());
        holder.id.setText(holder.id_barcode);
        holder.kedaluwarsa.setText(goods.getExpired());
        holder.harga.setText(MainActivity.rupiahkan(goods.getPrice()));
        holder.count.setText(goods.getCount());
        holder.inc.setOnClickListener(v -> {
            String x = holder.count.getText().toString();
            Integer y = Integer.parseInt(x);
            y = y + 1;
            String z = y.toString();
            holder.count.setText(z);
        });
        holder.dec.setOnClickListener(v -> {
            String x = holder.count.getText().toString();
            int y = Integer.parseInt(x);
            if (y > 1) {
                y = y - 1;
            }
            String z = Integer.toString(y);
            holder.count.setText(z);
        });
        holder.del.setOnClickListener(v -> {
            //MenuCashier.total = Integer.parseInt(holder.harga.getText().toString()) * Integer.parseInt(holder.count.getText().toString());
            String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Transactions").child(auth).child(holder.id_barcode);
            ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        holder.count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()){
                    if (s.toString().startsWith("0")){
                        holder.count.setText("1");
                    } else {
                        Integer x = Integer.parseInt(s.toString());
                        if (x > 0) {
                            String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods").child(auth).child(holder.id_barcode);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String stok = snapshot.child("stock").getValue().toString();
                                        if (!s.toString().isEmpty()) {
                                            if (Integer.parseInt(stok) >= Integer.parseInt(s.toString())) {
                                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Transactions").child(auth).child(holder.id_barcode);
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("count", s.toString());
                                                ref2.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            hitung(context, auth);
                                                        } else {
                                                            Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                holder.count.setText(stok);
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Terjadi kesalahan dalam memnemukan data", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            holder.count.setText("1");
                        }
                    }
                } else {
                    holder.count.setText("1");
                }
                holder.count.setSelection(holder.count.getText().length());
            }
        });
    }

    public static void hitung(Context context, String auth){
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Transactions").child(auth);
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long x3 = Long.valueOf(0);
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Long x1 = Long.parseLong(ds.child("price").getValue().toString());
                        Long x2 = Long.parseLong(ds.child("count").getValue().toString());
                        x3 = x3 + (x1 * x2);
                    }
                    MenuCashier.total = x3;
                    MenuCashier.tvTotal.setText(MainActivity.rupiahkan(x3.toString()));
                } else {
                    MenuCashier.tvTotal.setText("Rp. -");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        String id_barcode;
        RelativeLayout rlItem;
        TextView nama, id, kedaluwarsa, harga;
        EditText count;
        Button inc, dec, del;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id_barcode = null;
            rlItem = itemView.findViewById(R.id.rlItem_ci);
            nama = itemView.findViewById(R.id.tvNamaBarang_ci);
            id = itemView.findViewById(R.id.tvIdBarang_item);
            kedaluwarsa = itemView.findViewById(R.id.tvKedaluwarsa_item);
            harga = itemView.findViewById(R.id.tvHarga_item);
            count = itemView.findViewById(R.id.edCount_item);
            inc = itemView.findViewById(R.id.bIncr);
            dec = itemView.findViewById(R.id.bDecr);
            del = itemView.findViewById(R.id.bDelete_ci);
        }
    }
}
