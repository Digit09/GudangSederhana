package com.example.gudangsederhana;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.MyViewHolder> {

    Context context;
    ArrayList<Sales> list;

    public IncomeAdapter (Context context, ArrayList<Sales> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public IncomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_income_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeAdapter.MyViewHolder holder, int position) {
        Sales sales = list.get(position);
        holder.pendapatan.setText(MainActivity.rupiahkan(sales.getIncome()));
        holder.bulan.setText(sales.getDate());
        holder.bulanText.setText(cekBulan(sales.getDate().substring(0, 2)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pendapatan, bulan, bulanText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pendapatan = itemView.findViewById(R.id.tvPendatapan_item);
            bulan = itemView.findViewById(R.id.tvBulan_item);
            bulanText = itemView.findViewById(R.id.tvBulanText_item);
        }
    }

    public String cekBulan(String bulan){
        if (bulan.equals("01")){
            return "Januari";
        } else if (bulan.equals("02")){
            return "Februari";
        } else if (bulan.equals("03")){
            return "Maret";
        } else if (bulan.equals("04")){
            return "April";
        } else if (bulan.equals("05")){
            return "Mei";
        } else if (bulan.equals("06")){
            return "Juni";
        } else if (bulan.equals("07")){
            return "Juli";
        } else if (bulan.equals("08")){
            return "Agustus";
        } else if (bulan.equals("09")){
            return "September";
        } else if (bulan.equals("10")){
            return "Oktober";
        } else if (bulan.equals("11")){
            return "November";
        } else if (bulan.equals("12")){
            return "Desember";
        } else {
            return "Tidak Mengenali Bulan";
        }
    }
}
