package com.example.gudangsederhana;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Goods> list;

    public GoodsAdapter(Context context, ArrayList<Goods> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_main_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Goods goods = list.get(position);
        holder.nama.setText(goods.getName());
        holder.produsen.setText(goods.getProducer());
        holder.harga.setText(rupiahkan(goods.getPrice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nama, produsen, harga;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tvNamaBarang_item);
            produsen = itemView.findViewById(R.id.tvProdusen_item);
            harga = itemView.findViewById(R.id.tvHarga_item);
        }
    }

    private String periksaData(String data){
        if (data.isEmpty()){
            data = "-";
        }
        return data;
    }

    String rupiahkan(String nominal) {
        if (!nominal.equals("-")) {
            // mis. : nominal = "2000"
            Integer i = nominal.length(); // i = 4
            String temp = "";
            while (i > 0) { // 4 != 0
                if (i > 3) {
                    temp = "." + nominal.substring(i - 3, i) + temp; // temp = .000
                    i -= 3; // i = 4-3 = 1
                } else {
                    temp = nominal.substring(0, i) + temp;
                    i = 0;
                }
            }
            return "Rp. " + temp;
        } else {
            return nominal;
        }
    }
}
